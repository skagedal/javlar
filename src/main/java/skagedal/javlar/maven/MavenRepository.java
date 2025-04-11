package skagedal.javlar.maven;

import jakarta.ws.rs.core.UriBuilder;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skagedal.javlar.domain.model.FullCoordinates;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class MavenRepository {
    private static final URI DEFAULT_BASE_URI = URI.create("https://repo1.maven.org/maven2");

    private static final Logger logger = LoggerFactory.getLogger(MavenRepository.class);
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final URI mavenRepositoryBaseUri;

    public static MavenRepository create() {
        return new MavenRepository(DEFAULT_BASE_URI);
    }

    public MavenRepository(URI mavenRepositoryBaseUri) {
        this.mavenRepositoryBaseUri = mavenRepositoryBaseUri;
    }

    public Optional<Model> fetchPom(FullCoordinates coordinates) {
        try {
            final var pomUrl = buildPomUrl(coordinates);
            logger.info("Fetching POM from {}", pomUrl);

            final var request = HttpRequest.newBuilder()
                .uri(pomUrl)
                .build();

            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() == 200) {
                MavenXpp3Reader reader = new MavenXpp3Reader();
                return Optional.of(reader.read(response.body()));
            } else {
                logger.warn("Failed to fetch POM, status code: {}", response.statusCode());
                return Optional.empty();
            }
        } catch (IOException | XmlPullParserException e) {
            logger.error("Error fetching or parsing POM file", e);
            return Optional.empty();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted while fetching POM", e);
            return Optional.empty();
        }
    }

    public URI artifactUrl(FullCoordinates favorite, String suffix) {
        return UriBuilder.fromUri(mavenRepositoryBaseUri)
            .segment(favorite.groupId().split("\\."))
            .segment(
                favorite.artifactId(),
                favorite.version(),
                favorite.artifactId() + "-" + favorite.version() + suffix
            ).build();
    }

    private URI buildPomUrl(FullCoordinates coordinates) {
        return artifactUrl(coordinates, ".pom");
    }
}