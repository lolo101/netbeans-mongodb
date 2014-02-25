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

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import org.netbeans.modules.mongodb.CollectionInfo;
import org.netbeans.modules.mongodb.Images;
import org.netbeans.modules.mongodb.ui.components.QueryEditor;
import org.netbeans.modules.mongodb.ui.windows.CollectionView;
import org.netbeans.modules.mongodb.ui.windows.CollectionViewAction;
import org.netbeans.modules.mongodb.ui.wizards.ExportWizardAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "ACTION_exportQueryResult=Export Query Result",
    "ACTION_exportQueryResult_tooltip=Export Query Result"
})
public final class ExportQueryResultAction extends CollectionViewAction {

    public ExportQueryResultAction(CollectionView view) {
        super(view,
            Bundle.ACTION_exportQueryResult(),
            new ImageIcon(Images.EXPORT_COLLECTION_ICON),
            Bundle.ACTION_exportQueryResult_tooltip());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final Lookup lookup = getView().getLookup();
        final CollectionInfo collectionInfo = lookup.lookup(CollectionInfo.class);
        final QueryEditor queryEditor = getView().getQueryEditor();
        final Map<String, Object> properties = new HashMap<>();
        properties.put(ExportWizardAction.PROP_COLLECTION, collectionInfo.getName());
        properties.put("criteria", queryEditor.getCriteria());
        properties.put("projection", queryEditor.getProjection());
        properties.put("sort", queryEditor.getSort());
        new ExportWizardAction(lookup, properties).actionPerformed(e);
    }
}
