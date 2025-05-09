package net.mamesosu.irc.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.mamesosu.Main;
import net.mamesosu.data.DataBase;
import net.mamesosu.irc.IRCService;
import net.mamesosu.osu.Beatmap;
import net.mamesosu.osu.Osu;
import net.mamesosu.twitch.UserAccount;
import org.json.JSONObject;
import org.pircbotx.hooks.ListenerAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestMap extends ListenerAdapter {

    final String URL_REGEX = "https://osu\\.ppy\\.sh/beatmapsets/(\\d+)#(osu|taiko|fruits|mania)/(\\d+)";

    @Override
    public void onMessage(org.pircbotx.hooks.events.MessageEvent event) {

        if (event.getMessage().contains("!req") || event.getMessage().contains("!request")) {

            try {

                Pattern pattern = Pattern.compile(URL_REGEX);
                Matcher matcher = pattern.matcher(event.getMessage());

                IRCService irc = Main.irc;

                int twitchUserID = UserAccount.getUserID(event.getChannel().getName().replace("#", ""));

                DataBase dataBase = new DataBase();
                Connection connection = dataBase.getConnection();
                PreparedStatement ps;
                ResultSet result;

                if (matcher.find()) {
                    Osu osu = new Osu();

                    ps = connection.prepareStatement("select * from users where twitch_id = ?");
                    ps.setLong(1, twitchUserID);
                    result = ps.executeQuery();

                    if (result.next()) {

                        int osuUserID = result.getInt("id");

                        ObjectMapper mapper = new ObjectMapper();
                        Map<String, Object> jsonMap = new HashMap<>();
                        String bpm = Beatmap.getBPM(matcher.group(3));
                        String length = Beatmap.getBeatmapLength(matcher.group(3));
                        String difficulty = Beatmap.getBeatmapDifficulty(matcher.group(3));
                        String status = Beatmap.getBeatmapStatus(matcher.group(3));

                        jsonMap.put("key", osu.getSecretKey());
                        jsonMap.put("id", osuUserID);
                        jsonMap.put("requester", event.getUser().getNick());
                        jsonMap.put("set_id", Integer.parseInt(matcher.group(1)));
                        jsonMap.put("map_id", Integer.parseInt(matcher.group(3)));
                        jsonMap.put("map_name", Beatmap.getBeatmapTitle(matcher.group(3)));
                        jsonMap.put("difficulty", difficulty);
                        jsonMap.put("bpm", bpm);
                        jsonMap.put("length", length);
                        jsonMap.put("status", status);

                        String jsonBody = mapper.writeValueAsString(jsonMap);

                        String url = String.format(
                                "https://api.%s/v1/send_request_message",
                                osu.getBaseDomain());

                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(url))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                                .build();

                        HttpClient client = HttpClient.newHttpClient();

                        client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

                        irc.getBot().send().message(event.getChannel().getName(),
                                Beatmap.getBeatmapTitle(matcher.group(3)) +
                                        " - Request sent!");
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
