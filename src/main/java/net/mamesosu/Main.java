package net.mamesosu;

import net.mamesosu.api.RequestServer;
import net.mamesosu.irc.IRCService;

public class Main {
    public static void main(String[] args) {

        RequestServer requestServer = new RequestServer();
        IRCService ircService = new IRCService();

        requestServer.startServer();
        ircService.connect();
    }
}