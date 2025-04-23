package net.mamesosu.twitch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class User {

    static String url = "https://api.twitch.tv/helix/users?";

    private static JsonNode getUserData(String param, String username) {
        JsonNode node = null;
        String twitchURL = url + param + "=" + username;
        Twitch twitch = new Twitch();

        try {
            String line;
            URL obj = new URL(twitchURL);
            HttpURLConnection urlConnection = (HttpURLConnection) obj.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Client-Id", twitch.clientId);
            urlConnection.setRequestProperty("Authorization", "Bearer " + twitch.oauthToken);

            ObjectMapper mapper = new ObjectMapper();
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder responce = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    responce.append(line);
                }
                reader.close();

                node = mapper.readTree(responce.toString());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return node;
    }

    public static int getUserID(String username) {

        JsonNode node = getUserData("login" ,username);

        return node.get("data").isEmpty() ? 0 : node.get("data").get(0).get("id").asInt();
    }

    public static String getUserName(String userID) {

        JsonNode node = getUserData("id", userID);

        return node.get("data").isEmpty() ? null : node.get("data").get(0).get("login").asText();
    }
}
