package skagedal.javlar.web;

import org.intellij.lang.annotations.Language;
import org.intellij.lang.annotations.PrintFormat;
import org.jetbrains.annotations.NotNull;

public class Html {
    private Html() {}

    public static @NotNull @Language("html") String
    html(@Language("html") @NotNull @PrintFormat String template, Object... args) {
        return template.formatted(args);
    }
}
