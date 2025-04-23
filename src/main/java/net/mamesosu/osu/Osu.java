package net.mamesosu.osu;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;

@Getter
public class Osu {

    String apiKey;
    String secretKey;
    String baseDomain;

    public Osu() {
        Dotenv dotenv = Dotenv.configure().load();
        this.apiKey = dotenv.get("OSU_API_KEY");
        this.secretKey = dotenv.get("OSU_SECRET_KEY");
        this.baseDomain = dotenv.get("BASE_DOMAIN");
    }
}
