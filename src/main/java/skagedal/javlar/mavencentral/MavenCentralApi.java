package skagedal.javlar.mavencentral;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skagedal.javlar.util.BodyMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import static com.fasterxml.jackson.databind.DeserializationFeature.*;

public class MavenCentralApi {
    private static final Logger logger = LoggerFactory.getLogger(MavenCentralApi.class);

    private final URI baseUri = URI.create("https://search.maven.org/solrsearch/select");

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final BodyMapper bodyMapper = new BodyMapper(
        new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
    );

    public MavenCentralResponse search(String query) {
        final var request = HttpRequest
            .newBuilder()
            .uri(baseUri()
                .queryParam("wt", "json")
                .queryParam("q", query).build())
            .build();

        try {
            logger.atInfo().addKeyValue("uri", request.uri()).log("Sending HTTP request");
            final var response = httpClient.send(request, bodyMapper.receiving(MavenCentralResponse.class));
            return response.body();
        } catch (IOException e) {
            throw new MavenCentralApiException("IO Exception when calling MavenCentralApi", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MavenCentralApiException("InterruptedException when calling MavenCentralApi", e);
        }
    }

    private UriBuilder baseUri() {
        return UriBuilder.fromUri(baseUri);
    }

    @SuppressWarnings("java:S106") // System.out
    public static void main(String[] args) {
        final var api = new MavenCentralApi();
        final var response = api.search("g:com.fasterxml.jackson.core AND a:jackson-databind");
        System.out.println(response);
    }
}
