package skagedal.javlar.cli;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CliTest {
    @Test
    void list() {
        final var listCommand = new Cli().parse(new String[]{"list"});
        assertThat(listCommand).isInstanceOf(Command.List.class);
    }

    @Test
    void describe() {
        final var describeCommand = new Cli().parse(new String[]{"describe", "foo"});
        assertThat(describeCommand)
            .isInstanceOfSatisfying(Command.Describe.class, describe -> {
                assertThat(describe.artifactId()).isEqualTo("foo");
            });
        ;
    }
}
