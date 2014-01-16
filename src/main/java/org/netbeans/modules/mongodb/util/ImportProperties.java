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

import java.io.File;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 *
 * @author Yann D'Isanto
 */
public final class ImportProperties {
    
    private final String collection;
    
    private final boolean drop;

    private final File file;
    
    private final Charset encoding;

    public ImportProperties(String collection, boolean drop, File file, Charset encoding) {
        this.collection = Objects.requireNonNull(collection);
        this.drop = drop;
        this.file = Objects.requireNonNull(file);
        this.encoding = Objects.requireNonNull(encoding);
    }

    public String getCollection() {
        return collection;
    }

    public boolean isDrop() {
        return drop;
    }

    public File getFile() {
        return file;
    }

    public Charset getEncoding() {
        return encoding;
    }

}
