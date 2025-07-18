-- Fix mTLS certificate subject DN to match actual certificate
-- This resolves 401 errors for tls_client_auth and self_signed_tls_client_auth flows

UPDATE oauth2_registered_client 
SET client_settings = JSON_SET(
    client_settings, 
    '$.\"settings.client.x509-certificate-subject-dn\"', 
    'EMAILADDRESS=khaitan.nitin@gmail.com, CN=127.0.0.1, O=Codzs, L=Melbourne, ST=Victoria, C=AU'
) 
WHERE client_id = 'mtls-demo-client';