package br.com.simplepass.cadevanmotorista.retrofit.responses;


/**
 * Created by leandro on 3/7/16.
 */
public class OAuthTokenResponse extends BaseResponse {
    private String access_token;
    private String token_type;
    private String scope;

    public String getAccessToken() {
        return access_token;
    }

    public void setAccessToken(String access_token) {
        this.access_token = access_token;
    }

    public String getTokenType() {
        return token_type;
    }

    public void setTokenType(String token_type) {
        this.token_type = token_type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
