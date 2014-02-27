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
package org.netbeans.modules.mongodb.ui.windows.collectionview.treetable;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.netbeans.modules.mongodb.ui.windows.collectionview.CollectionQueryResult;
import org.netbeans.modules.mongodb.ui.windows.collectionview.CollectionQueryResultProvider;
import org.netbeans.modules.mongodb.ui.windows.collectionview.CollectionQueryResultModelUpdateListener;

/**
 *
 * @author Yann D'Isanto
 */
public final class DocumentsTreeTableModel extends DefaultTreeTableModel implements CollectionQueryResultProvider, CollectionQueryResultModelUpdateListener {

    private final CollectionQueryResult collectionQueryResult;

    public DocumentsTreeTableModel(CollectionQueryResult collectionQueryResult) {
        this.collectionQueryResult = collectionQueryResult;
        collectionQueryResult.addCollectionQueryResultModelUpdateListener(this);
    }
    
    @Override
    public CollectionQueryResult getCollectionQueryResult() {
        return collectionQueryResult;
    }

    @Override
    public void updateStarting() {
    }

    @Override
    public void documentAdded(DBObject document) {
    }

    @Override
    public void updateFinished() {
        final TreeTableNode rootNode = new CollectionViewTreeTableNode<>(null, collectionQueryResult.getDocuments(),
            new CollectionViewTreeTableNode.ChildrenFactory<List<DBObject>>() {

                @Override
                public List<TreeTableNode> createChildren(TreeTableNode parent, List<DBObject> documents) {
                    final List<TreeTableNode> children = new ArrayList<>(documents.size());
                    for (DBObject document : documents) {
                        children.add(new DocumentNode(parent, document));
                    }
                    return children;
                }
            }
        );
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                setRoot(rootNode);
            }
        });
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public String getColumnName(int column) {
        return "Documents";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return DBObject.class;
    }
}
