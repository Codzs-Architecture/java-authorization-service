package com.codzs.oauth2.authentication.device;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;

import com.codzs.oauth2.authentication.device.error.DeviceAuthenticationErrorHandler;

/**
 * An {@link AuthenticationProvider} implementation for device client authentication.
 * This provider has been enhanced to use DeviceAuthenticationErrorHandler for better
 * separation of concerns while maintaining backward compatibility.
 * 
 * @author Enhanced to use DeviceAuthenticationErrorHandler
 * @since 1.1
 */
public final class DeviceClientAuthenticationProvider implements AuthenticationProvider {
	private static final String ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-3.2.1";
	private final Log logger = LogFactory.getLog(getClass());
	private final RegisteredClientRepository registeredClientRepository;

	public DeviceClientAuthenticationProvider(RegisteredClientRepository registeredClientRepository) {
		Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");
		this.registeredClientRepository = registeredClientRepository;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		DeviceClientAuthenticationToken deviceClientAuthentication =
				(DeviceClientAuthenticationToken) authentication;

		if (!ClientAuthenticationMethod.NONE.equals(deviceClientAuthentication.getClientAuthenticationMethod())) {
			return null;
		}

		String clientId = deviceClientAuthentication.getPrincipal().toString();
		RegisteredClient registeredClient = this.registeredClientRepository.findByClientId(clientId);
		if (registeredClient == null) {
			// Use the new error handler for better separation of concerns
			DeviceAuthenticationErrorHandler.throwInvalidClient(OAuth2ParameterNames.CLIENT_ID);
		}

		if (this.logger.isTraceEnabled()) {
			this.logger.trace("Retrieved registered client");
		}

		if (!registeredClient.getClientAuthenticationMethods().contains(
				deviceClientAuthentication.getClientAuthenticationMethod())) {
			// Use the new error handler for better separation of concerns
			DeviceAuthenticationErrorHandler.throwInvalidClient("authentication_method");
		}

		if (this.logger.isTraceEnabled()) {
			this.logger.trace("Validated device client authentication parameters");
		}

		if (this.logger.isTraceEnabled()) {
			this.logger.trace("Authenticated device client");
		}

		return new DeviceClientAuthenticationToken(registeredClient,
				deviceClientAuthentication.getClientAuthenticationMethod(), null);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return DeviceClientAuthenticationToken.class.isAssignableFrom(authentication);
	}

	/**
	 * @deprecated Use DeviceAuthenticationErrorHandler.throwInvalidClient() instead.
	 * This method is kept for backward compatibility.
	 */
	@Deprecated
	private static void throwInvalidClient(String parameterName) {
		OAuth2Error error = new OAuth2Error(
				OAuth2ErrorCodes.INVALID_CLIENT,
				"Device client authentication failed: " + parameterName,
				ERROR_URI
		);
		throw new OAuth2AuthenticationException(error);
	}

}
