/*
 * The MIT License
 *
 * Copyright 2014 Yann D'Isanto.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.netbeans.modules.mongodb.options;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.prefs.Preferences;
import org.netbeans.modules.mongodb.util.Version;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;

/**
 *
 * @author Yann D'Isanto
 */
public enum MongoNativeToolsOptions {

    INSTANCE;

    private static final String TOOLS_FOLDER = "mongodb-tools-folder";

    private static final String TOOLS_VERSION = "mongodb-tools-version";

    private String toolsFolder;

    private Version toolsVersion;

    private MongoNativeToolsOptions() {
        load();
    }

    private Preferences getPreferences() {
        return NbPreferences.forModule(MongoNativeToolsOptions.class);
    }

    public void load() {
        toolsFolder = getPreferences().get(TOOLS_FOLDER, null);
        final String versionAsString = getPreferences().get(TOOLS_VERSION, null);
        if (versionAsString != null) {
            toolsVersion = new Version(versionAsString);
        }
    }

    public void store() {
        if (toolsFolder == null) {
            getPreferences().remove(TOOLS_FOLDER);
            getPreferences().remove(TOOLS_VERSION);
        } else {
            getPreferences().put(TOOLS_FOLDER, toolsFolder);
            getPreferences().put(TOOLS_VERSION, toolsVersion.toString());
        }
    }

    public boolean isToolsFolderConfigured() {
        return toolsFolder != null;
    }

    public String getToolsFolder() {
        return toolsFolder;
    }

    public Version getToolsVersion() {
        return toolsVersion;
    }

    public void setToolsFolder(String toolsFolder) {
        this.toolsFolder = toolsFolder;
        if (toolsFolder != null) {
            try {
                toolsVersion = readVersion(toolsFolder);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            toolsVersion = null;
        }
    }

    private static Version readVersion(String toolsFolder) throws IOException {
        final Process process = new ProcessBuilder(
            new File(toolsFolder, "mongo").getAbsolutePath(),
            "--version"
        ).start();
        process.getOutputStream().close();
        process.getErrorStream().close();
        try (InputStream input = process.getInputStream()) {
            final String version = readVersion(input);
            return new Version(version);
        }
    }

    private static String readVersion(InputStream input) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(input);
            BufferedReader reader = new BufferedReader(isr)) {
            final String line = reader.readLine();
            return line.substring(line.lastIndexOf(" ")).trim();
        }
    }
}
