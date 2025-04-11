package skagedal.javlar.api;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HealthControllerTest {

    @Test
    void health() throws IOException, InterruptedException {
        final var controller = new HealthController();
        final var server = new RootController(0, List.of(new RootController.ControllerDescription("/health", controller)));
        server.start();

        try (final var client = HttpClient.newHttpClient()) {
            final var request = java.net.http.HttpRequest.newBuilder()
                .uri(server.listeningOnUri().resolve("/health"))
                .build();
            final var response = client.send(request, BodyHandlers.ofString());

            assertEquals(200, response.statusCode());
            assertEquals("OK", response.body());
        } finally {
            server.stop();
        }

    }
}