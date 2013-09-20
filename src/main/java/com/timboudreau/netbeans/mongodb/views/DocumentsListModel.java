/*
 * The MIT License
 *
 * Copyright 2013 PVVQ7166.
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
package com.timboudreau.netbeans.mongodb.views;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author PVVQ7166
 */
public class DocumentsListModel extends AbstractListModel<DBObject> {

    private DBCollection dbCollection;

    private final List<DBObject> data = new ArrayList<>();

    public DocumentsListModel(DBCollection dbCollection) {
        this.dbCollection = dbCollection;
    }

    public void query() {
        clear();
        if(dbCollection == null) {
            // TODO: error message
            return;
        }
        try (DBCursor cursor = dbCollection.find()) {
            for (DBObject document : cursor) {
                final int index = data.size();
                if (data.add(document)) {
                    fireIntervalAdded(this, index, index);
                }
            }
        }
    }

    private void clear() {
        final int size = data.size();
        if (size > 0) {
            data.clear();
            fireIntervalRemoved(this, 0, size - 1);
        }
    }

    @Override
    public int getSize() {
        return data != null ? data.size() : 0;
    }

    @Override
    public DBObject getElementAt(int index) {
        return data != null ? data.get(index) : null;
    }
}
