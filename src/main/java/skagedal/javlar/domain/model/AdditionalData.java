package skagedal.javlar.domain.model;

import org.intellij.lang.annotations.Language;

import java.net.URI;

public record AdditionalData(URI javadoc, URI homepage, URI scm, String description) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Builder () {
        }

        private URI javadocUrl;
        private String description;

        public Builder javadoc(URI javadoc) {
            this.javadocUrl = javadoc;
            return this;
        }

        public Builder description(@Language("markdown") String description) {
            this.description = description;
            return this;
        }

        public AdditionalData build() {
            return new AdditionalData(javadocUrl, null, null, description);
        }
    }
}
