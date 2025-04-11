package skagedal.javlar.api;

import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.concurrent.Executors;

public class RootController {
    private static final Logger log = LoggerFactory.getLogger(RootController.class);

    private final com.sun.net.httpserver.HttpServer server;

    public RootController(int port, List<ControllerDescription> controllerDescriptions) throws IOException {
        this.server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(port), 0);

        for (ControllerDescription controllerDescription : controllerDescriptions) {
            server.createContext(controllerDescription.path(), controllerDescription.handler());
        }
        server.setExecutor(Executors.newFixedThreadPool(10));
    }

    public void start() {
        server.start();
        log.info("Java directory server started on port {}", server.getAddress().getPort());
    }

    public void stop() {
        server.stop(0);
        log.info("Java directory server stopped");
    }

    public URI listeningOnUri() {
        return URI.create("http:/" + server.getAddress());
    }

    public record ControllerDescription(String path, HttpHandler handler) {

    }
}