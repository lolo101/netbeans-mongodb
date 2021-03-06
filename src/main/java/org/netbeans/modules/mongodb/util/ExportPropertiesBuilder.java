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

import com.mongodb.DBObject;
import java.io.File;
import java.nio.charset.Charset;

/**
 *
 * @author Yann D'Isanto
 */
public final class ExportPropertiesBuilder {
    
    private String collection;
    
    private DBObject criteria;

    private DBObject projection;

    private DBObject sort;
    
    private boolean jsonArray;

    private File file;
    
    private Charset encoding;

    public ExportPropertiesBuilder() {
        this(null);
    }

    public ExportPropertiesBuilder(String collection) {
        this.collection = collection;
    }
    
    public ExportPropertiesBuilder collection(String collection) {
        this.collection = collection;
        return this;
    }

    public ExportPropertiesBuilder criteria(DBObject criteria) {
        this.criteria = criteria;
        return this;
    }

    public ExportPropertiesBuilder projection(DBObject projection) {
        this.projection = projection;
        return this;
    }

    public ExportPropertiesBuilder sort(DBObject sort) {
        this.sort = sort;
        return this;
    }

    public ExportPropertiesBuilder jsonArray(boolean jsonArray) {
        this.jsonArray = jsonArray;
        return this;
    }
    
    public ExportPropertiesBuilder file(File file) {
        this.file = file;
        return this;
    }
    
    public ExportPropertiesBuilder encoding(Charset encoding) {
        this.encoding = encoding;
        return this;
    }
    
    public ExportProperties build() {
        return new ExportProperties(collection, criteria, projection, sort, jsonArray, file, encoding);
    }
}
