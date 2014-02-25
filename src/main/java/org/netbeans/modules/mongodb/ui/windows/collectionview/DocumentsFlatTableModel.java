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
package org.netbeans.modules.mongodb.ui.windows.collectionview;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Yann D'Isanto
 */
public final class DocumentsFlatTableModel extends AbstractTableModel implements CollectionQueryResultTableModel {
    
    private final DBCollection dbCollection;

    private final List<DBObject> data = new ArrayList<>();

    private int pageSize = DEFAULT_PAGE_SIZE;

    private int page = 1;

    private int totalDocumentsCount = 0;

    private DBObject criteria;

    private DBObject projection;

    private DBObject sort;
    
    private final List<String> columns = new ArrayList<>();
    
    public DocumentsFlatTableModel(DBCollection dbCollection) {
        this.dbCollection = dbCollection;
    }

    @Override
    public void update() {
        clear();
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
            columns.clear();
            columns.add("_id");
            for (DBObject document : pageCursor) {
                data.add(document);
                updateColumns(document);
            }
            fireTableStructureChanged();
            if (data.size() > 0) {
                fireTableRowsInserted(0, data.size() - 1);
            }
        }
    }
    
    private void updateColumns(DBObject document) {
        for (String field : document.keySet()) {
            if(columns.contains(field) == false) {
                columns.add(field);
            }
        }
    }

    private void clear() {
        final int size = data.size();
        if (size > 0) {
            data.clear();
            fireTableRowsDeleted(0, size - 1);
        }
    }

    @Override
    public List<DBObject> getDocuments() {
        return new ArrayList<>(data);
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public int getPageCount() {
        if (pageSize > 0) {
            final double pageCount = (double) totalDocumentsCount / (double) pageSize;
            return (int) Math.ceil(pageCount);
        }
        return 1;
    }

    @Override
    public int getTotalDocumentsCount() {
        return totalDocumentsCount;
    }

    @Override
    public int getColumnCount() {
        return columns != null ? columns.size() : 0;
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return DBObject.class;
    }

    @Override
    public int getRowCount() {
        return data != null ? data.size() : 0;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        final DBObject document = getRowValue(rowIndex);
        if(document == null) {
            return null;
        }
        final String field = columns.get(columnIndex);
        return document.get(field);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public DBObject getRowValue(int rowIndex) {
        if (rowIndex == -1) {
            return null;
        }
        return data != null ? data.get(rowIndex) : null;
    }

    @Override
    public DBObject getCriteria() {
        return criteria;
    }

    @Override
    public void setCriteria(DBObject criteria) {
        this.criteria = criteria;
    }

    @Override
    public DBObject getProjection() {
        return projection;
    }

    @Override
    public void setProjection(DBObject projection) {
        this.projection = projection;
    }

    @Override
    public DBObject getSort() {
        return sort;
    }

    @Override
    public void setSort(DBObject sort) {
        this.sort = sort;
    }
    
}
