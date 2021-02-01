/*
 * Copyright © 2020 Sun.Hao(https://www.crazy-coder.cn/)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.lodsve.boot;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.core.SpringVersion;
import org.springframework.core.env.Environment;

import java.io.PrintStream;

/**
 * LODSVE BOOT BANNER.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class LodsveBootBanner implements Banner {
    private static final String[] DEFAULT_BANNER = new String[]{
        " _               _                ______             _",
        "| |             | |               | ___ \\           | |",
        "| |     ___   __| |_____   _____  | |_/ / ___   ___ | |_",
        "| |    / _ \\ / _` / __\\ \\ / / _ \\ | ___ \\/ _ \\ / _ \\| __|",
        "| |___| (_) | (_| \\__ \\ V /  __/ | |_/ / (_) | (_) | |_",
        "\\_____/\\___/ \\__,_|___/ \\_/ \\___| \\____/ \\___/ \\___/ \\__|"
    };
    private static final int LINE_WIDTH = DEFAULT_BANNER[3].length();
    private static final String LODSVE_DESCRIPTION = "Let our development of Spring very easy!";
    private static final String LODSVE_VERSION = " :: Lodsve Boot :: ";
    private static final String SPRING_BOOT_VERSION = " :: Spring Boot :: ";
    private static final String SPRING_FRAMEWORK_VERSION = " :: Spring Framework :: ";
    private static final String UNKNOWN_VERSION = "(unknown version)";

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        String lodsveVersion = formatVersion(LodsveBootVersion.getVersion());
        StringBuilder blank2 = new StringBuilder();
        fillBlank(LODSVE_VERSION.length() + lodsveVersion.length(), blank2);

        String springBootVersion = formatVersion(SpringBootVersion.getVersion());
        StringBuilder blank3 = new StringBuilder();
        fillBlank(springBootVersion.length() + SPRING_BOOT_VERSION.length(), blank3);

        String springFrameworkVersion = formatVersion(SpringVersion.getVersion());
        StringBuilder blank4 = new StringBuilder();
        fillBlank(springFrameworkVersion.length() + SPRING_FRAMEWORK_VERSION.length(), blank4);

        out.println("\n");
        for (String line : DEFAULT_BANNER) {
            out.println(AnsiOutput.toString(AnsiColor.BRIGHT_GREEN, line, AnsiColor.DEFAULT));
        }

        out.println("\n" + AnsiOutput.toString(AnsiColor.BLUE, LODSVE_DESCRIPTION, AnsiColor.DEFAULT));
        out.println(AnsiOutput.toString(AnsiColor.BLUE, "Author: " + environment.getProperty("lodsve-boot.author"), AnsiColor.DEFAULT));
        out.println(AnsiOutput.toString(AnsiColor.GREEN, LODSVE_VERSION, AnsiColor.RED, blank2.toString(), lodsveVersion, AnsiColor.DEFAULT));
        out.println(AnsiOutput.toString(AnsiColor.GREEN, SPRING_BOOT_VERSION, AnsiColor.RED, blank3.toString(), springBootVersion, AnsiColor.DEFAULT));
        out.println(AnsiOutput.toString(AnsiColor.GREEN, SPRING_FRAMEWORK_VERSION, AnsiColor.RED, blank4.toString(), springFrameworkVersion, AnsiColor.DEFAULT));
        out.println("\n");
    }

    private String formatVersion(String version) {
        if (StringUtils.isBlank(version)) {
            return "(" + UNKNOWN_VERSION + ")";
        }

        return "(v" + version + ")";
    }

    private void fillBlank(int nowLength, StringBuilder blank) {
        if (nowLength < LINE_WIDTH) {
            for (int i = 0; i < LINE_WIDTH - nowLength; i++) {
                blank.append(" ");
            }
        }
    }
}
