package skagedal.javlar.domain.model;

public record UnversionedCoordinates(
    String groupId,
    String artifactId
) {
    public static UnversionedCoordinates parseFromGradleSyntax(final String string) {
        final var parts = string.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid format: " + string);
        }
        return new UnversionedCoordinates(parts[0], parts[1]);
    }

    public FullCoordinates withVersion(String version) {
        return new FullCoordinates(groupId, artifactId, version);
    }
}
