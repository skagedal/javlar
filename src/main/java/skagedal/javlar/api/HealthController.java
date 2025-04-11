package skagedal.javlar.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HealthController implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        sendTextResponse(exchange, 200, "OK");
    }

    private void sendTextResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        sendResponse(exchange, statusCode, response.getBytes(StandardCharsets.UTF_8));
    }

    private void sendResponse(HttpExchange exchange, int statusCode, byte[] responseBytes) throws IOException {
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
