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

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Yann D'Isanto
 */
public final class MongoUtil {

    private static final Set<String> SUPPORTED_OPTIONS = new HashSet<>();
    static {
        final Set<String> generalOptionsKeys = new HashSet<>();
        final Set<String> authKeys = new HashSet<>();
        final Set<String> readPreferenceKeys = new HashSet<>();
        final Set<String> writeConcernKeys = new HashSet<>();

        generalOptionsKeys.add("maxpoolsize");
        generalOptionsKeys.add("waitqueuemultiple");
        generalOptionsKeys.add("waitqueuetimeoutms");
        generalOptionsKeys.add("connecttimeoutms");
        generalOptionsKeys.add("sockettimeoutms");
        generalOptionsKeys.add("autoconnectretry");
        generalOptionsKeys.add("ssl");

        readPreferenceKeys.add("slaveok");
        readPreferenceKeys.add("readpreference");
        readPreferenceKeys.add("readpreferencetags");

        writeConcernKeys.add("safe");
        writeConcernKeys.add("w");
        writeConcernKeys.add("wtimeout");
        writeConcernKeys.add("fsync");
        writeConcernKeys.add("j");

        authKeys.add("authmechanism");
        authKeys.add("authsource");

        SUPPORTED_OPTIONS.addAll(generalOptionsKeys);
        SUPPORTED_OPTIONS.addAll(authKeys);
        SUPPORTED_OPTIONS.addAll(readPreferenceKeys);
        SUPPORTED_OPTIONS.addAll(writeConcernKeys);
    }

    public static boolean isSupportedOption(String option) {
        return SUPPORTED_OPTIONS.contains(option.toLowerCase());
    }

    private MongoUtil() {
    }

}
