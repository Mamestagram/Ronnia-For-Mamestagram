package net.mamesosu;

import net.mamesosu.api.RequestServer;
import net.mamesosu.irc.IRCService;

public class Main {

    public static IRCService irc;

    public static void main(String[] args) {

        RequestServer requestServer = new RequestServer();
        irc = new IRCService();

        requestServer.startServer();
        irc.connect();
    }
}