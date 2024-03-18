package uk.co.asepstrath.bank;

public class authToken {
    public String access_token;
    public String token_type;
    public int expires_in;

    public authToken(String access_token, String token_type, int expires_in){
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;

    }


}
