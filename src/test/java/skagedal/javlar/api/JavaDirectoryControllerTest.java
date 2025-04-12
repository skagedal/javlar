package skagedal.javlar.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import skagedal.javlar.domain.JavaDirectoryService;
import skagedal.javlar.maven.MavenRepository;
import skagedal.javlar.mavencentral.MavenCentralApi;
import skagedal.javlar.mavencentral.MavenCentralResponse;
import skagedal.javlar.util.BodyMapper;
import skagedal.javlar.util.FileSystemCache;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JavaDirectoryControllerTest {

    @Test
    void listLibraries() throws IOException, InterruptedException {
        final var objectMapper = new ObjectMapper().enable(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION);
        final var bodyMapper = new BodyMapper(objectMapper);
        final var mavenCentralApi = new MavenCentralApi();
        final var mavenRepository = new MavenRepository(URI.create("https://repo.maven.apache.org/maven2/"));
        final var directoryService = new JavaDirectoryService(mavenRepository, new FileSystemCache<>(
            MavenCentralResponse.class,
            mavenCentralApi::search,
            Path.of(System.getProperty("user.home"), ".javlar", "maven-central-cache")
        ));
        final var directoryController = new JavaDirectoryController(directoryService);
        final var server = new RootController(0, List.of(new RootController.ControllerDescription("/directory", directoryController)));
        server.start();

        try (final var client = HttpClient.newHttpClient()) {
            final var request = HttpRequest.newBuilder()
                .uri(server.listeningOnUri().resolve("/directory"))
                .build();
            final var response = client.send(request, bodyMapper.receiving(ListResponse.class));

            assertEquals(200, response.statusCode());
            assertThat(response.body()).isEqualTo(new ListResponse(List.of()));
        } finally {
            server.stop();
        }

        server.stop();
    }

    private record ListResponse(List<ListedLibrary> libraries) {
        record ListedLibrary(String groupId, String artifactId, String version) {
        }
    }
}