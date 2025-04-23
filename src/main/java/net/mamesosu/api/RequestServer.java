package net.mamesosu.api;

import com.sun.net.httpserver.HttpServer;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;

import java.net.InetSocketAddress;

@Getter
public class RequestServer {

    int port;

    public RequestServer() {
        Dotenv dotenv = Dotenv.configure().load();
        port = Integer.parseInt(dotenv.get("HTTP_PORT"));
    }

    public void startServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/user", new User());
            server.setExecutor(null); // creates a default executor

            System.out.println("Web server started on port " + port);

            server.start();

        } catch (Exception e) {
            System.out.println("Error starting web server: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
