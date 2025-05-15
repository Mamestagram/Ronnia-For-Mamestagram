package net.mamesosu.osu;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class Beatmap {

    private static JsonNode getJson(String url) {
        JsonNode node = null;
        try {
            String line;
            URL obj = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) obj.openConnection();

            urlConnection.setRequestMethod("GET");

            ObjectMapper mapper = new ObjectMapper();
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder responce = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                responce.append(line);
            }
            reader.close();

            node = mapper.readTree(responce.toString());

        } catch (IOException e) {
            e.fillInStackTrace();
        }

        return node;
    }


    public static String getBeatmapStatus(String beatmapID) {

        String osuAPI = new Osu().getApiKey();
        String url = "https://osu.ppy.sh/api/get_beatmaps?k=" + osuAPI + "&b=" + beatmapID;
        JsonNode node = getJson(url);

        if(node.isEmpty()) {
            return null;
        }

        String beatmapStatus = node.get(0).get("approved").asText();

        switch (beatmapStatus) {
            case "-2" -> {
                return "Graveyard";
            }
            case "-1" -> {
                return "WIP";
            }
            case "0" -> {
                return "Pending";
            }
            case "1" -> {
                return "Ranked";
            }
            case "2" -> {
                return "Approved";
            }
            case "3" -> {
                return "Qualified";
            }
            case "4" -> {
                return "Loved";
            }
        }

        return "Graveyard";
    }

    public static String getBPM(String beatmapID) {

        String osuAPI = new Osu().getApiKey();
        String url = "https://osu.ppy.sh/api/get_beatmaps?k=" + osuAPI + "&b=" + beatmapID;
        JsonNode node = getJson(url);

        if(node.isEmpty()) {
            return null;
        }

        return node.get(0).get("bpm").asText();
    }

    public static String getBeatmapLength(String beatmapID) {

        String osuAPI = new Osu().getApiKey();
        String url = "https://osu.ppy.sh/api/get_beatmaps?k=" + osuAPI + "&b=" + beatmapID;
        JsonNode node = getJson(url);

        if(node.isEmpty()) {
            return null;
        }

        int beatmapLength = node.get(0).get("total_length").asInt();
        int minutes = beatmapLength / 60;
        int seconds = beatmapLength % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    public static String getBeatmapDifficulty(String beatmapID) {

        String osuAPI = new Osu().getApiKey();
        String url = "https://osu.ppy.sh/api/get_beatmaps?k=" + osuAPI + "&b=" + beatmapID;
        JsonNode node = getJson(url);

        if(node.isEmpty()) {
            return null;
        }

        return String.format("%.2f", node.get(0).get("difficultyrating").asDouble());
    }

    public static String getBeatmapTitle(String beatmapID) {

         String osuAPI = new Osu().getApiKey();
         String url = "https://osu.ppy.sh/api/get_beatmaps?k=" + osuAPI + "&b=" + beatmapID;
         JsonNode node = getJson(url);

         if(node.isEmpty()) {
             return null;
         }

         String beatmapTitle = node.get(0).get("title").asText();
         String beatmapArtist = node.get(0).get("artist").asText();
         String beatmapVersion = node.get(0).get("version").asText();

         return beatmapTitle + " - " + beatmapArtist + " [" + beatmapVersion + "]";
    }
}
