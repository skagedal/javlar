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
import java.net.http.HttpResponse;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public class MavenCentralApi {
    private static final Logger logger = LoggerFactory.getLogger(MavenCentralApi.class);

    private final URI baseUri = URI.create("https://search.maven.org/solrsearch/select");

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final BodyMapper bodyMapper = new BodyMapper(
        new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
    );

    public MavenCentralResponse search(String query, SearchMode mode) {
        final var request = buildRequest(query, mode);
        return sendRequest(request, bodyMapper.receiving(MavenCentralResponse.class));
    }

    public String searchReturningRaw(String query, SearchMode mode) {
        final var request = buildRequest(query, mode);
        return sendRequest(request, HttpResponse.BodyHandlers.ofString());
    }

    private <T> T sendRequest(final HttpRequest request, final HttpResponse.BodyHandler<T> bodyHandler) {
        try {
            logger.atInfo().addKeyValue("uri", request.uri()).log("Sending HTTP request");
            final var response = httpClient.send(request, bodyHandler);
            return response.body();
        } catch (IOException e) {
            throw new MavenCentralApiException("IO Exception when calling MavenCentralApi", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MavenCentralApiException("InterruptedException when calling MavenCentralApi", e);
        }
    }

    private HttpRequest buildRequest(final String query, final SearchMode mode) {
        final var uri = baseUri()
            .queryParam("wt", "json")
            .queryParam("q", query)
            .queryParam("core", "gav")
            .build();
        return HttpRequest.newBuilder()
            .uri(uri)
            .build();
    }

    private UriBuilder baseUri() {
        return UriBuilder.fromUri(baseUri);
    }

    // Main runners for testing

    @SuppressWarnings("java:S106") // System.out
    public static void main(String[] args) {
        if (args.length == 0) {
            searchWithVersions();
        } else {
            simpleSearch();
        }
    }

    private static void simpleSearch() {
        final var api = new MavenCentralApi();
        final var rawResponse = api.searchReturningRaw("g:com.fasterxml.jackson.core AND a:jackson-databind", new SearchMode.LatestVersion());
        printFormatted(rawResponse);
    }

    private static void searchWithVersions() {
        final var api = new MavenCentralApi();
        final var rawResponse = api.searchReturningRaw("g:io.micrometer AND a:micrometer-core", new SearchMode.AllVersions());
        printFormatted(rawResponse);
    }

    private static void printFormatted(final String rawResponse) {
        try {
            final var objectMapper = new ObjectMapper();
            final var jsonNode = objectMapper.readTree(rawResponse);
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
