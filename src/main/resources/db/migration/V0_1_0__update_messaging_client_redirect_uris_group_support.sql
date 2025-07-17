-- Update messaging-client redirect URIs to support both Group 1 (port 8001) and Group 2 (port 8004)
-- This allows both Client Resource Service and BFF Service to use the same messaging-client configuration

UPDATE oauth2_registered_client 
SET redirect_uris = 'https://local.codzs.com:8001/login/oauth2/code/messaging-client-oidc,https://local.codzs.com:8001/authorized,https://local.codzs.com:8004/login/oauth2/code/messaging-client-oidc,https://local.codzs.com:8004/authorized',
    post_logout_redirect_uris = 'https://local.codzs.com:8001/logged-out,https://local.codzs.com:8004/logged-out'
WHERE client_id = 'messaging-client';