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

package org.netbeans.modules.mongodb.ui.windows.collectionview.actions;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import org.netbeans.modules.mongodb.Images;
import org.netbeans.modules.mongodb.ui.windows.CollectionView;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "confirmDocumentDeletionText=Delete document?",
    "ACTION_deleteSelectedDocument=Delete document",
    "ACTION_deleteSelectedDocument_tooltip=Delete Selected Document"
})
public final class DeleteSelectedDocumentAction extends CollectionViewAction {
    
    public DeleteSelectedDocumentAction(CollectionView view) {
        super(view, 
            Bundle.ACTION_deleteSelectedDocument(), 
            new ImageIcon(Images.DELETE_DOCUMENT_ICON), 
            Bundle.ACTION_deleteSelectedDocument_tooltip());
    }

        @Override
        public void actionPerformed(ActionEvent e) {
            final DBObject document = getView().getResultTableSelectedDocument();
            final Object dlgResult = DialogDisplayer.getDefault().notify(
                    new NotifyDescriptor.Confirmation(Bundle.confirmDocumentDeletionText(), NotifyDescriptor.YES_NO_OPTION));
            if (dlgResult.equals(NotifyDescriptor.OK_OPTION)) {
                try {
                    final DBCollection dbCollection = getView().getLookup().lookup(DBCollection.class);
                    dbCollection.remove(document);
                    getView().refreshResults();
                } catch (MongoException ex) {
                    DialogDisplayer.getDefault().notify(
                            new NotifyDescriptor.Message(ex.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE));
                }
            }
        }
}
