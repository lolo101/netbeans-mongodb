/*
 * The MIT License
 *
 * Copyright 2013 Yann D'Isanto.
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
 * @author Yann D'Isanto
 */
public class DocumentsListModel extends AbstractListModel<DBObject> {

    private DBCollection dbCollection;

    private final List<DBObject> data = new ArrayList<>();

    private int itemsPerPage = 10;

    private int page = 1;

    private int cursorItemCount = 0;

    private DBObject criteria;

    public DocumentsListModel(DBCollection dbCollection) {
        this.dbCollection = dbCollection;
    }

    public void update() {
        clear();
        if (dbCollection == null) {
            // TODO: error message
            return;
        }
        try (DBCursor cursor = dbCollection.find(criteria)) {
            cursorItemCount = cursor.count();
            DBCursor pageCursor = cursor;
            if (itemsPerPage > 0) {
                final int toSkip = (page - 1) * itemsPerPage;
                pageCursor = cursor.skip(toSkip).limit(itemsPerPage);
            }
            for (DBObject document : pageCursor) {
                data.add(document);
            }
            if (data.size() > 0) {
                System.out.println(data.size() - 1 + " item(s) added");
                fireIntervalAdded(this, 0, data.size() - 1);
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

    public List<DBObject> getDocuments() {
        return new ArrayList<>(data);
    }
    
    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageCount() {
        if (itemsPerPage > 0) {
            return (cursorItemCount / itemsPerPage) + 1;
        }
        return 1;
    }

    @Override
    public int getSize() {
        return data != null ? data.size() : 0;
    }

    @Override
    public DBObject getElementAt(int index) {
        return data != null ? data.get(index) : null;
    }

    public DBObject getCriteria() {
        return criteria;
    }

    public void setCriteria(DBObject criteria) {
        this.criteria = criteria;
    }
}
