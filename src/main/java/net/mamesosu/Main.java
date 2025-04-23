package net.mamesosu;

import net.mamesosu.api.RequestServer;
import net.mamesosu.data.DataBase;
import net.mamesosu.irc.IRCService;
import net.mamesosu.twitch.UserAccount;

public class Main {

    public static IRCService irc;

    public static void main(String[] args) {

        RequestServer requestServer = new RequestServer();
        irc = new IRCService();

        requestServer.startServer();

        System.out.println(UserAccount.getUserID("mames1osu"));
        irc.connect();
    }
}