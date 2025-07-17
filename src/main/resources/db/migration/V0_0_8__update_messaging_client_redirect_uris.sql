-- Update messaging-client to use port 8001 redirect URIs (client-resource-service) instead of port 8004 (BFF service)
-- This fixes the OAuth2 authorization flow when BFF service is not running

UPDATE oauth2_registered_client 
SET redirect_uris = 'https://local.codzs.com:8001/login/oauth2/code/messaging-client-oidc,https://local.codzs.com:8001/authorized'
WHERE client_id = 'messaging-client';

-- Update post logout redirect URI to port 8001
UPDATE oauth2_registered_client 
SET post_logout_redirect_uris = 'https://local.codzs.com:8001/logged-out'
WHERE client_id = 'messaging-client';

-- Debug: Show current client configuration
SELECT client_id, redirect_uris, post_logout_redirect_uris 
FROM oauth2_registered_client 
WHERE client_id = 'messaging-client';