package skagedal.javlar.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;
import java.util.function.Supplier;

@NullMarked
public class FileSystemCache<T> {
    public static final Duration EXPIRY_TIME = Duration.of(2, ChronoUnit.HOURS);
    private final Class<T> klass;
    private final Function<String, T> supplier;
    private final Path path;
    private final ObjectMapper mapper = new ObjectMapper();

    public FileSystemCache(final Class<T> klass, final Function<String, T> supplier, final Path path) {
        this.klass = klass;
        this.supplier = supplier;
        this.path = path;
    }

    public void initialize() throws IOException {
        Files.createDirectories(path);
    }

    public T get(final String key) throws IOException {
        final var filePath = path.resolve(key + ".json");
        if (existsAndIsFresh(filePath)) {
            return mapper.readValue(Files.newBufferedReader(filePath), klass);
        } else {
            final var value = supplier.apply(key);
            mapper.writeValue(Files.newBufferedWriter(filePath), value);
            return value;
        }
    }

    private boolean existsAndIsFresh(final Path filePath) {
        try {
            final var fileTime = Files.getLastModifiedTime(filePath);
            return Duration.between(fileTime.toInstant(), Instant.now())
                .compareTo(EXPIRY_TIME) < 0;
        } catch (IOException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        final var cache = new FileSystemCache<>(String.class, String::toUpperCase, Path.of("/tmp/upcasecache"));
        try {
            cache.initialize();
            System.out.println(cache.get("test"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
