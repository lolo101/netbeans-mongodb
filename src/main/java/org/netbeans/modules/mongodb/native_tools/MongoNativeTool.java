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
package org.netbeans.modules.mongodb.native_tools;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.lang.SystemUtils;
import org.netbeans.modules.mongodb.options.MongoNativeToolsOptions;

/**
 *
 * @author Yann D'Isanto
 */
public enum MongoNativeTool {

    MONGO_SHELL("mongo"),
    MONGO_DUMP("mongodump"),
    MONGO_RESTORE("mongorestore"),
    MONGO_IMPORT("mongoimport"),
    MONGO_EXPORT("mongoexport"),
    MONGO_STAT("mongostat"),
    MONGO_TOP("mongotop"),
    MONGO_PERF("mongoperf"),
    MONGO_FILES("mongofiles"),
    MONGO_OPLOG("mongooplog");

    private final String execBaseName;

    private MongoNativeTool(String execBaseName) {
        this.execBaseName = execBaseName;
    }

    public String getExecBaseName() {
        return execBaseName;
    }

    public String getExecFileName() {
        final StringBuilder sb = new StringBuilder();
        sb.append(execBaseName);
        if (SystemUtils.IS_OS_WINDOWS) {
            sb.append(".exe");
        }
        return sb.toString();
    }

    public Path getExecFullPath() {
        final MongoNativeToolsOptions options = MongoNativeToolsOptions.INSTANCE;
        final String execFolderPath = options.getToolsFolder();
        if (execFolderPath == null) {
            throw new IllegalStateException("mongo tools executables folder not configured");
        }
        return Paths.get(execFolderPath, getExecFileName());
    }

}
