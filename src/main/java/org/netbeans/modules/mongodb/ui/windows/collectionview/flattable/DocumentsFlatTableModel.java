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
package org.netbeans.modules.mongodb.ui.windows.collectionview.flattable;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.netbeans.modules.mongodb.ui.windows.collectionview.CollectionQueryResult;
import org.netbeans.modules.mongodb.ui.windows.collectionview.CollectionQueryResultProvider;
import org.netbeans.modules.mongodb.ui.windows.collectionview.CollectionQueryResultModelUpdateListener;

/**
 *
 * @author Yann D'Isanto
 */
public final class DocumentsFlatTableModel extends AbstractTableModel implements CollectionQueryResultProvider, CollectionQueryResultModelUpdateListener {
    
    private final List<String> columns = new ArrayList<>();
    
    private final CollectionQueryResult collectionQueryResult;

    public DocumentsFlatTableModel(CollectionQueryResult collectionQueryResult) {
        this.collectionQueryResult = collectionQueryResult;
        collectionQueryResult.addCollectionQueryResultModelUpdateListener(this);
    }
    
    @Override
    public CollectionQueryResult getCollectionQueryResult() {
        return collectionQueryResult;
    }

    @Override
    public void updateStarting() {
            columns.clear();
            columns.add("_id");
    }

    @Override
    public void documentAdded(DBObject document) {
                updateColumns(document);
    }

    @Override
    public void updateFinished() {
            fireTableStructureChanged();
//            fireTableDataChanged();
    }

    private void updateColumns(DBObject document) {
        for (String field : document.keySet()) {
            if(columns.contains(field) == false) {
                columns.add(field);
            }
        }
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
        return collectionQueryResult.getDocuments().size();
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
        return collectionQueryResult.getDocuments().get(rowIndex);
    }    
}
