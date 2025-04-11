package skagedal.javlar.domain;

import org.apache.maven.model.Model;
import org.apache.maven.model.Scm;
import skagedal.javlar.domain.data.StaticData;
import skagedal.javlar.domain.model.UnversionedCoordinates;
import skagedal.javlar.domain.model.LibraryInfo;
import skagedal.javlar.maven.MavenRepository;
import skagedal.javlar.mavencentral.MavenCentralApi;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class JavaDirectoryService {
    private final MavenCentralApi mavenCentralApi;
    private final MavenRepository mavenRepository;
    private final List<LibraryInfo> repository = new ArrayList<>();

    public static JavaDirectoryService create() {
        return new JavaDirectoryService(
            new MavenCentralApi(),
            MavenRepository.create()
        );
    }

    public JavaDirectoryService(MavenCentralApi mavenCentralApi, MavenRepository mavenRepository) {
        this.mavenCentralApi = mavenCentralApi;
        this.mavenRepository = mavenRepository;
    }

    public void createFavorite(UnversionedCoordinates createRequest) {
        final var info = fetchInfo(createRequest);
        repository.add(info);
    }

    public List<LibraryInfo> listFavorites() {
        return repository.stream().toList();
    }

    public LibraryInfo fetchInfo(UnversionedCoordinates coordinates) {
        final var response = mavenCentralApi.search("g:" + coordinates.groupId() + " AND a:" + coordinates.artifactId());
        final var foundPackages = response.response().numFound();
        if (foundPackages == 0) {
            throw new NoSuchLibraryException("No library found with coordinates " + coordinates);
        }
        if (foundPackages < 0 || foundPackages > 1) {
            throw new IllegalStateException("Expected to find exactly one package, but found " + foundPackages);
        }
        if (response.response().docs().size() != foundPackages) {
            throw new IllegalStateException("Expected to find " + foundPackages + " packages in \"docs\", but found " + response.response().docs().size());
        }
        final var doc = response.response().docs().getFirst();
        if (!doc.group().equals(coordinates.groupId())) {
            throw new IllegalStateException("Expected found package to have group " + coordinates.groupId() + ", but found " + doc.group());
        }
        if (!doc.artifact().equals(coordinates.artifactId())) {
            throw new IllegalStateException("Expected found package to have artifact " + coordinates.artifactId() + ", but found " + doc.artifact());
        }

        final var additionalData = StaticData.ADDITIONAL_DATA.get(coordinates);
        final var versionedCoordinates = coordinates.withVersion(doc.latestVersion());
        final var pom = mavenRepository.fetchPom(versionedCoordinates);
        final var homepageUri = pom.map(Model::getUrl).map(URI::create).orElse(null);
        final var scmUri = pom.map(Model::getScm).map(Scm::getUrl).map(URI::create).orElse(null);

        return new LibraryInfo(versionedCoordinates, doc.suffixes(), additionalData, homepageUri, scmUri);
    }

    public List<URI> artifacts(LibraryInfo libraryInfo) {
        return libraryInfo.suffixes().stream()
            .map(suffix -> mavenRepository.artifactUrl(libraryInfo.coordinates(), suffix))
            .toList();
    }

}
