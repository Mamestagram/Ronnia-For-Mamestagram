package net.mamesosu.irc;


import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import net.mamesosu.data.DataBase;
import net.mamesosu.irc.event.RequestMap;
import net.mamesosu.twitch.UserAccount;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IRCService {

    String name;
    String oAuthPassword;
    @Getter
    PircBotX bot;

    public IRCService () {
        Dotenv dotenv = Dotenv.configure().load();

        this.name = dotenv.get("IRC_NICK");
        this.oAuthPassword = dotenv.get("IRC_OAUTH_PASSWORD");
    }

    public void connect() {

        DataBase dataBase = new DataBase();
        PreparedStatement ps;
        ResultSet result;
        List<String> userList = new ArrayList<>();

        try {
            Connection connection = dataBase.getConnection();
            ps = connection.prepareStatement("select * from users where twitch_id != 0");
            result = ps.executeQuery();
            while (result.next()) {
                long userID = result.getLong("twitch_id");
                String twitchName = UserAccount.getUserName(String.valueOf(userID));
                if (twitchName == null) {
                    continue;
                }

                userList.add("#" + twitchName);
                System.out.println("Adding " + twitchName + " to auto join list");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        Configuration configuration = new Configuration.Builder()
                .setName(name)
                .setServer("irc.chat.twitch.tv", 6667)
                .setServerPassword(oAuthPassword)
                .addAutoJoinChannels(userList)
                .addListener(new RequestMap())
                .buildConfiguration();

        bot = new PircBotX(configuration);

        System.out.println("Connecting to Twitch IRC server... ");

        try {
            bot.startBot();
        } catch (Exception e) {
            System.out.println("IRC connection error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
