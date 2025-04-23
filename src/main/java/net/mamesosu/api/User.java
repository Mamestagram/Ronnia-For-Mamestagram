package net.mamesosu.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.mamesosu.Main;
import net.mamesosu.data.DataBase;
import net.mamesosu.irc.IRCService;
import net.mamesosu.osu.Osu;
import net.mamesosu.twitch.UserAccount;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// プライベートAPIサーバー
// Twitchの名前の編集と追加

public class User implements HttpHandler {

    /*
    * {
    *  "id": "userid",
    *  "name": "twitch_name",
    *  "key: "secret_key",
    * }
    * */

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {

            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder body = new StringBuilder();
            JsonNode node;
            String line;

            while ((line = reader.readLine()) != null) {
                body.append(line);
            }

            node = mapper.readTree(body.toString());

            int userID = node.get("id").asInt();
            String twitchName = node.get("name").asText();
            String key = node.get("key").asText();
            int twitchID = UserAccount.getUserID(twitchName);
            IRCService irc = Main.irc;

            if(twitchID == 0) {
                System.out.println("Player not found");
                exchange.sendResponseHeaders(404, -1); // ユーザーが見つからない
                return;
            }

            if (!key.equals(new Osu().getSecretKey())) {
                System.out.println("Invalid key");
                exchange.sendResponseHeaders(401, -1); // 認証エラー
                return;
            }

            DataBase dataBase = new DataBase();
            PreparedStatement ps;

            try {
                Connection connection = dataBase.getConnection();
                ps = connection.prepareStatement("update users set twitch_id = ? where id = ?");
                ps.setInt(1, twitchID);
                ps.setInt(2, userID);
                ps.executeUpdate();
                irc.getBot().send().joinChannel("#" + twitchName);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                exchange.sendResponseHeaders(500, -1); // サーバーエラー
                return;
            }

            exchange.sendResponseHeaders(200, -1);
        } else {
            exchange.sendResponseHeaders(405, -1); // メソッド不許可
        }
    }
}
