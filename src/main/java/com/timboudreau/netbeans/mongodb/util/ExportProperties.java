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

package com.timboudreau.netbeans.mongodb.util;

import com.mongodb.DBObject;

/**
 *
 * @author Yann D'Isanto
 */
public final class ExportProperties {
    
    private String collection;
    
    private DBObject criteria;

    private DBObject projection;

    private DBObject sort;
    
    private boolean jsonArray;

    public ExportProperties() {
        this(null);
    }

    public ExportProperties(String collection) {
        this(collection, null, null, null, false);
    }
        
    public ExportProperties(String collection, DBObject criteria, DBObject projection, DBObject sort, boolean jsonArray) {
        this.collection = collection;
        this.criteria = criteria;
        this.projection = projection;
        this.sort = sort;
        this.jsonArray = jsonArray;
    }

    
    public String getCollection() {
        return collection;
    }

    public ExportProperties collection(String collection) {
        this.collection = collection;
        return this;
    }

    public DBObject getCriteria() {
        return criteria;
    }

    public ExportProperties criteria(DBObject criteria) {
        this.criteria = criteria;
        return this;
    }

    public DBObject getProjection() {
        return projection;
    }

    public ExportProperties projection(DBObject projection) {
        this.projection = projection;
        return this;
    }

    public DBObject getSort() {
        return sort;
    }

    public ExportProperties sort(DBObject sort) {
        this.sort = sort;
        return this;
    }

    public boolean isJsonArray() {
        return jsonArray;
    }

    public ExportProperties jsonArray(boolean jsonArray) {
        this.jsonArray = jsonArray;
        return this;
    }
    
    
}
