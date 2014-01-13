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

/**
 *
 * @author Yann D'Isanto
 */
public final class ImportProperties {
    
    private String collection;
    
    private boolean drop;

    public ImportProperties() {
        this(null);
    }

    public ImportProperties(String collection) {
        this(collection, false);
    }

    public ImportProperties(String collection, boolean drop) {
        this.collection = collection;
        this.drop = drop;
    }
        
    
    public String getCollection() {
        return collection;
    }

    public ImportProperties collection(String collection) {
        this.collection = collection;
        return this;
    }

    public boolean isDrop() {
        return drop;
    }

    public ImportProperties drop(boolean drop) {
        this.drop = drop;
        return this;
    }
    
    
}
