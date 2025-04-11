package skagedal.javlar.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import skagedal.javlar.domain.JavaDirectoryService;
import skagedal.javlar.domain.model.ListPackagesResponse;
import skagedal.javlar.domain.model.UnversionedCoordinates;
import skagedal.javlar.mavencentral.MavenCentralApiException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class JavaDirectoryController implements HttpHandler {
    private final JavaDirectoryService javaDirectoryService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JavaDirectoryController(JavaDirectoryService javaDirectoryService) {
        this.javaDirectoryService = javaDirectoryService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                final var favorites = javaDirectoryService.listFavorites();
                sendJsonResponse(exchange, 200, new ListPackagesResponse(favorites));
            } else if ("POST".equals(exchange.getRequestMethod())) {
                final var request = objectMapper.readValue(exchange.getRequestBody(), UnversionedCoordinates.class);
                javaDirectoryService.createFavorite(request);
                final var pokemonList = javaDirectoryService.listFavorites();
                sendJsonResponse(exchange, 200, pokemonList);
            } else {
                String response = "Method not allowed";
                sendTextResponse(exchange, 405, response);
            }
        } catch (MavenCentralApiException e) {
            sendTextResponse(exchange, 500, "Error fetching maven central data: " + e.getMessage());
        }
    }

    private void sendTextResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        sendResponse(exchange, statusCode, response.getBytes(StandardCharsets.UTF_8));
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, Object response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        final var responseBytes = objectMapper.writeValueAsBytes(response);
        sendResponse(exchange, statusCode, responseBytes);
    }

    private void sendResponse(HttpExchange exchange, int statusCode, byte[] responseBytes) throws IOException {
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

}
