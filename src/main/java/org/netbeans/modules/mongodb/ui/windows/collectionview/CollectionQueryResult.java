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
package org.netbeans.modules.mongodb.ui.windows.collectionview;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Yann D'Isanto
 */
public final class CollectionQueryResult {

    public static final int DEFAULT_PAGE_SIZE = 20;

    @Getter
    @Setter
    private DBCollection dbCollection;

    @Getter
    private final List<DBObject> documents = new ArrayList<>();

    @Getter
    @Setter
    private int pageSize = DEFAULT_PAGE_SIZE;

    @Getter
    @Setter
    private int page = 1;

    @Getter
    private int totalDocumentsCount = 0;

    @Getter
    @Setter
    private DBObject criteria;

    @Getter
    @Setter
    private DBObject projection;

    @Getter
    @Setter
    private DBObject sort;

    @Setter
    private CollectionQueryResultUpdateListener view;

    private boolean viewRefreshNecessary;

    public CollectionQueryResult(DBCollection dbCollection) {
        this.dbCollection = dbCollection;
    }

    public void update() {
        documents.clear();
        fireUpdateStarting();
        if (dbCollection == null) {
            // TODO: error message?
            return;
        }
        try (DBCursor cursor = dbCollection.find(criteria, projection)) {
            if (sort != null) {
                cursor.sort(sort);
            }
            totalDocumentsCount = cursor.count();
            try (DBCursor pageCursor = getPageCursor(cursor)) {
                for (DBObject document : pageCursor) {
                    documents.add(document);
                    fireDocumentAdded(document);
                }
            }
        }
        fireUpdateFinished();
        viewRefreshNecessary = true;
    }

    public void refreshViewIfNecessary() {
        if(viewRefreshNecessary == false) {
            return;
        }
        fireUpdateStarting();
        for (DBObject dBObject : documents) {
            fireDocumentAdded(dBObject);
        }
        fireUpdateFinished();
        viewRefreshNecessary = false;
    }

    private DBCursor getPageCursor(DBCursor queryCursor) {
        if (pageSize > 0) {
            final int toSkip = (page - 1) * pageSize;
            return queryCursor.skip(toSkip).limit(pageSize);
        }
        return queryCursor;
    }

    private void fireUpdateStarting() {
        if (view != null) {
            view.updateStarting(this);
        }
    }

    private void fireDocumentAdded(DBObject document) {
        if (view != null) {
            view.documentAdded(this, document);
        }
    }

    private void fireUpdateFinished() {
        if (view != null) {
            view.updateFinished(this);
        }
    }

    public int getPageCount() {
        if (pageSize > 0) {
            final double pageCount = (double) totalDocumentsCount / (double) pageSize;
            return (int) Math.ceil(pageCount);
        }
        return 1;
    }
}
