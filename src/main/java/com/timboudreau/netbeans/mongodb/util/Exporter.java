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

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import java.io.PrintWriter;
import java.io.Writer;

/**
 *
 * @author Yann D'Isanto
 */
public final class Exporter implements Runnable {

    private final DB db;

    private final ExportProperties properties;

    private final Writer writer;

    public Exporter(DB db, ExportProperties properties, Writer writer) {
        this.db = db;
        this.properties = properties;
        this.writer = writer;
    }

    @Override
    public void run() {
        PrintWriter output = new PrintWriter(writer);
        final DBCollection collection = db.getCollection(properties.getCollection());
        final DBCursor cursor = collection.find(properties.getCriteria(), properties.getProjection());
        if (properties.getSort() != null) {
            cursor.sort(properties.getSort());
        }
        if (properties.isJsonArray()) {
            output.print("[");
        }
        boolean first = true;
        for (DBObject document : cursor) {
            if(first) {
                first = false;
            } else if (properties.isJsonArray()) {
                output.print(",");
            }
            final String json = JSON.serialize(document);
            output.print(json);
            if (properties.isJsonArray()) {
//                if (cursor.hasNext()) {
//                    output.print(",");
//                }
            } else {
                output.println();
            }
            output.flush();
        }
        if (properties.isJsonArray()) {
            output.println("]");
        }
        output.flush();
    }
}
