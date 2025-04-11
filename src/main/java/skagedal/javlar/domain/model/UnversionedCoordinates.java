package skagedal.javlar.domain.model;

public record UnversionedCoordinates(
    String groupId,
    String artifactId
) {
    public FullCoordinates withVersion(String version) {
        return new FullCoordinates(groupId, artifactId, version);
    }
}
