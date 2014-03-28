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
package org.netbeans.modules.mongodb.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Yann D'Isanto
 */
public final class Version implements Comparable<Version> {

    private static final Pattern PATTERN = Pattern.compile("(\\d+)\\.(\\d+)(\\.(\\d+))?");

    private final int major;

    private final int minor;

    private final int patch;

    private final String stringValue;

    
    public Version(int major, int minor) {
        this(major, minor, 0);
    }
    
    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        stringValue = new StringBuilder()
            .append(major)
            .append('.')
            .append(minor)
            .append('.')
            .append(patch)
            .toString();
    }

    public Version(String string) {
        final Matcher matcher = PATTERN.matcher(string);
        if (matcher.find() == false) {
            throw new IllegalArgumentException("invalid version string");
        }
        this.major = Integer.parseInt(matcher.group(1));
        this.minor = Integer.parseInt(matcher.group(2));
        final String patchGroup = matcher.group(4);
        this.patch = patchGroup != null
            ? Integer.parseInt(patchGroup)
            : 0;
        this.stringValue = string;
    }

    @Override
    public int compareTo(Version other) {
        int comp = major - other.major;
        if (comp == 0) {
            comp = minor - other.minor;
            if (comp == 0) {
                comp = patch - other.patch;
            }
        }
        return comp;
    }

    @Override
    public String toString() {
        return stringValue;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + this.major;
        hash = 19 * hash + this.minor;
        hash = 19 * hash + this.patch;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Version other = (Version) obj;
        if (this.major != other.major) {
            return false;
        }
        if (this.minor != other.minor) {
            return false;
        }
        return this.patch == other.patch;
    }
}
