package skagedal.javlar.domain.data;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.jetbrains.annotations.NotNull;
import skagedal.javlar.domain.model.AdditionalData;
import skagedal.javlar.domain.model.UnversionedCoordinates;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StaticData {
    private final YAMLMapper yamlMapper = new YAMLMapper();
    private StaticData() {

    }

    private static final List<Entry> all = new StaticData().readAll();

    public static final Map<UnversionedCoordinates, AdditionalData> ADDITIONAL_DATA = all.stream().collect(Collectors.toMap(Entry::coordinates, Entry::additionalData));

    public static List<UnversionedCoordinates> libraries() {
        return all.stream().map(Entry::coordinates).toList();
    }

    public List<Entry> readAll() {
        return paths().map(this::parseFile).toList();
    }

    public record Entry(UnversionedCoordinates coordinates, AdditionalData additionalData) {

    }

    public Entry parseFile(Path path) {
        final var coordinates = new UnversionedCoordinates(
            path.getParent().getFileName().toString(),
            removeExtension(path)
        );
        try {
            final var additionalData = yamlMapper.readValue(path.toFile(), AdditionalData.class);
            return new Entry(coordinates, additionalData);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static @NotNull String removeExtension(Path path) {
        return path.getFileName().toString().replaceFirst("\\.yaml$", "");
    }

    public static Stream<Path> paths() {
        final var libraries = librariesPath();
        try {
            return Files.walk(libraries).filter(path -> path.getFileName().toString().endsWith(".yaml"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static @NotNull Path librariesPath() {
        try {
            if (StaticData.class.getClassLoader().getResource("libraries") instanceof URL resource) {
                return Paths.get(resource.toURI());
            }
            throw new IllegalStateException("Could not find URL of libraries directory in resources");
        } catch (URISyntaxException exception) {
            throw new IllegalStateException("Could not find libraries directory", exception);
        }
    }

}
