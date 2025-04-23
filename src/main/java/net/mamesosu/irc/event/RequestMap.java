package net.mamesosu.irc.event;

import org.pircbotx.hooks.ListenerAdapter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestMap extends ListenerAdapter {

    final String URL_REGEX = "https://osu\\.ppy\\.sh/beatmapsets/(\\d+)#(osu|taiko|fruits|mania)/(\\d+)";

    @Override
    public void onMessage(org.pircbotx.hooks.events.MessageEvent event) {
        if(!event.getMessage().contains("!req")) {
            return;
        }

        String[] message = event.getMessage().split(" ");

        if(message.length != 2) {
            return;
        }

        Pattern pattern = Pattern.compile(URL_REGEX);
        Matcher matcher = pattern.matcher(message[1]);

        if(matcher.find()) {

        }
    }
}
