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

/**
 *
 * @author Yann D'Isanto
 */
public final class CollectionQueryResult {
    
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    private final DBCollection dbCollection;

    private final List<DBObject> documents = new ArrayList<>();

    private int pageSize = DEFAULT_PAGE_SIZE;

    private int page = 1;

    private int totalDocumentsCount = 0;

    private DBObject criteria;

    private DBObject projection;

    private DBObject sort;
    
    private final List<CollectionQueryResultModelUpdateListener> updateListeners = new ArrayList<>();

    public CollectionQueryResult(DBCollection dbCollection) {
        this.dbCollection = dbCollection;
    }
    
    public void update() {
        documents.clear();
        fireUpdateStarting();
        if (dbCollection == null) {
            // TODO: error message
            return;
        }
        try (DBCursor cursor = dbCollection.find(criteria, projection)) {
            if(sort != null) {
                cursor.sort(sort);
            }
            totalDocumentsCount = cursor.count();
            DBCursor pageCursor = cursor;
            if (pageSize > 0) {
                final int toSkip = (page - 1) * pageSize;
                pageCursor = cursor.skip(toSkip).limit(pageSize);
            }
            for (DBObject document : pageCursor) {
                documents.add(document);
                fireDocumentAdded(document);
            }
        }
        fireUpdateFinished();
    }
    
    private void fireUpdateStarting() {
        for (CollectionQueryResultModelUpdateListener listener : updateListeners) {
            listener.updateStarting();
        }
    }
    
    private void fireDocumentAdded(DBObject document) {
        for (CollectionQueryResultModelUpdateListener listener : updateListeners) {
            listener.documentAdded(document);
        }
    }
    
    private void fireUpdateFinished() {
        for (CollectionQueryResultModelUpdateListener listener : updateListeners) {
            listener.updateFinished();
        }
    }
    
    public void addCollectionQueryResultModelUpdateListener(CollectionQueryResultModelUpdateListener listener) {
        updateListeners.add(listener);
    }
    
    public void removeCollectionQueryResultModelUpdateListener(CollectionQueryResultModelUpdateListener listener) {
        updateListeners.remove(listener);
    }
    
    public List<DBObject> getDocuments() {
        return documents;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageCount() {
        if (pageSize > 0) {
            final double pageCount = (double) totalDocumentsCount / (double) pageSize;
            return (int) Math.ceil(pageCount);
        }
        return 1;
    }

    public int getTotalDocumentsCount() {
        return totalDocumentsCount;
    }

    public DBObject getCriteria() {
        return criteria;
    }

    public void setCriteria(DBObject criteria) {
        this.criteria = criteria;
    }

    public DBObject getProjection() {
        return projection;
    }

    public void setProjection(DBObject projection) {
        this.projection = projection;
    }

    public DBObject getSort() {
        return sort;
    }

    public void setSort(DBObject sort) {
        this.sort = sort;
    }
}
