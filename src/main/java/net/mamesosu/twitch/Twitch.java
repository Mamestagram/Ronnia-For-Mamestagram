package net.mamesosu.twitch;

import io.github.cdimascio.dotenv.Dotenv;

public class Twitch {

    String clientId;
    String oauthToken;

    public Twitch() {
        Dotenv dotenv = Dotenv.configure().load();
        this.clientId = dotenv.get("TWITCH_CLIENT_ID");
        this.oauthToken = dotenv.get("TWITCH_OAUTH_TOKEN");
    }
}
