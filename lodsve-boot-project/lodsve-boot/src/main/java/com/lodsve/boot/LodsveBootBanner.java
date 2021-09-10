/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        "| |___| (_) | (_| \\__ \\\\ V /  __/ | |_/ / (_) | (_) | |_",
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
