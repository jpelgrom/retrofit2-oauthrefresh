package nl.jpelgrm.retrofit2oauthrefresh.api.objects;

public class AccessToken {

    private String access_token;
    private String token_type;
    private Integer expires_in;
    private String refresh_token;
    private String scope;
    private String client_id;
    private String client_secret;

    public String getAccessToken() {
        return access_token;
    }

    public void setAccessToken(String access_token) {
        this.access_token = access_token;
    }

    public String getTokenType() {
        // OAuth requires uppercase Authorization HTTP header value for token type
        if(!Character.isUpperCase(token_type.charAt(0))) {
            token_type = Character.toString(token_type.charAt(0)).toUpperCase() + token_type.substring(1);
        }

        return token_type;
    }

    public void setTokenType(String token_type) {
        this.token_type = token_type;
    }

    public int getExpiry() {
        return expires_in;
    }

    public void setExpiry(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefreshToken() {
        return refresh_token;
    }

    public void setRefreshToken(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getClientID() {
        return client_id;
    }

    public void setClientID(String client_id) {
        this.client_id = client_id;
    }

    public String getClientSecret() {
        return client_secret;
    }

    public void setClientSecret(String client_secret) {
        this.client_secret = client_secret;
    }

}
