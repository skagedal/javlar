package skagedal.javlar.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.function.Function;

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

    public void initialize(final String description) throws IOException {
        Files.createDirectories(path);
        Files.writeString(path.resolve("_README"), description);
    }

    public T getOrLoad(final String key) throws IOException {
        final var filePath = path.resolve(sha256(key));
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

    private static MessageDigest createDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private String sha256(String input) {
        final var digest = createDigest();
        final var hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
    }
}
