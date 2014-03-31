/* 
 * The MIT License
 *
 * Copyright 2013 Tim Boudreau.
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
package org.netbeans.modules.mongodb.ui.explorer;

import org.netbeans.modules.mongodb.properties.CollectionNameProperty;
import org.netbeans.modules.mongodb.properties.DatabaseNameProperty;
import org.netbeans.modules.mongodb.properties.ConnectionNameProperty;
import org.netbeans.modules.mongodb.properties.MongoClientURIProperty;
import org.netbeans.modules.mongodb.resources.Images;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import java.awt.Image;
import org.netbeans.modules.mongodb.ui.util.TopComponentUtils;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.modules.mongodb.CollectionInfo;
import org.netbeans.modules.mongodb.native_tools.MongoNativeToolsAction;
import org.netbeans.modules.mongodb.ui.util.CollectionNameValidator;
import org.netbeans.modules.mongodb.ui.util.ValidatingInputLine;
import org.netbeans.modules.mongodb.ui.windows.CollectionView;
import org.netbeans.modules.mongodb.ui.wizards.ExportWizardAction;
import org.netbeans.modules.mongodb.ui.wizards.ImportWizardAction;
import org.netbeans.modules.mongodb.util.SystemCollectionPredicate;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.actions.OpenAction;
import org.openide.cookies.OpenCookie;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;

/**
 *
 * @author Tim Boudreau
 * @author Yann D'Isanto
 */
@Messages({
    "ACTION_DropCollection=Drop Collection",
    "ACTION_RenameCollection=Rename Collection",
    "# {0} - collection name",
    "dropCollectionConfirmText=Permanently drop ''{0}'' collection?",
    "# {0} - collection name",
    "renameCollectionText=rename ''{0}'' to:"})
final class CollectionNode extends AbstractNode {

    private final CollectionInfo collection;

    CollectionNode(CollectionInfo connection) {
        this(connection, new InstanceContent());
    }

    CollectionNode(CollectionInfo connection, InstanceContent content) {
        this(connection, content, new ProxyLookup(new AbstractLookup(content), Lookups.fixed(connection), connection.getLookup()));
    }

    CollectionNode(final CollectionInfo collection, final InstanceContent content, final Lookup lookup) {
        super(Children.LEAF, lookup);
        this.collection = collection;
        content.add(collection);
        content.add(collection, new CollectionConverter());
        content.add(new OpenCookie() {
            @Override
            public void open() {
                TopComponent tc = TopComponentUtils.find(CollectionView.class, collection);
                if (tc == null) {
                    tc = new CollectionView(collection, lookup);
                    tc.open();
                }
                tc.requestActive();
            }
        });
    }

    @Override
    public String getName() {
        return collection.getName();
    }

    @Override
    public Image getIcon(int ignored) {
        if (SystemCollectionPredicate.get().eval(collection.getName())) {
            return Images.SYSTEM_COLLECTION_ICON;
        } else {
            return Images.COLLECTION_ICON;
        }
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.put(new CollectionNameProperty(getLookup()));
        set.put(new DatabaseNameProperty(getLookup()));
        set.put(new ConnectionNameProperty(getLookup()));
        set.put(new MongoClientURIProperty(getLookup()));
        sheet.put(set);
        return sheet;
    }

    @Override
    public Action[] getActions(boolean context) {
        final Map<String, Object> properties = new HashMap<>();
        properties.put(ExportWizardAction.PROP_COLLECTION, collection.getName());
        properties.put(ImportWizardAction.PROP_COLLECTION, collection.getName());
        final Action importAction = new ImportWizardAction(getLookup(), properties);
        final Action renameAction = new RenameCollectionAction();
        final Action dropAction = new DropCollectionAction();
        if (SystemCollectionPredicate.get().eval(collection.getName())) {
            importAction.setEnabled(false);
            renameAction.setEnabled(false);
            dropAction.setEnabled(false);
        }
        return new Action[]{
            SystemAction.get(OpenAction.class),
            null,
            dropAction,
            renameAction,
            null,
            new MongoNativeToolsAction(getLookup()),
            null,
            new ExportWizardAction(getLookup(), properties),
            importAction
        };
    }

    @Override
    public Action getPreferredAction() {
        return SystemAction.get(OpenAction.class);
    }

    private class CollectionConverter implements InstanceContent.Convertor<CollectionInfo, DBCollection> {

        @Override
        public DBCollection convert(CollectionInfo t) {
            DB db = getLookup().lookup(DB.class);
            return db.getCollection(t.getName());
        }

        @Override
        public Class<? extends DBCollection> type(CollectionInfo t) {
            return DBCollection.class;
        }

        @Override
        public String id(CollectionInfo t) {
            return t.getName();
        }

        @Override
        public String displayName(CollectionInfo t) {
            return id(t);
        }
    }

    public class DropCollectionAction extends AbstractAction {

        public DropCollectionAction() {
            super(Bundle.ACTION_DropCollection());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final CollectionInfo ci = getLookup().lookup(CollectionInfo.class);
            final Object dlgResult = DialogDisplayer.getDefault().notify(new NotifyDescriptor.Confirmation(
                    Bundle.dropCollectionConfirmText(ci.getName()),
                    NotifyDescriptor.YES_NO_OPTION));
            if (dlgResult.equals(NotifyDescriptor.OK_OPTION)) {
                try {
                    getLookup().lookup(DBCollection.class).drop();
                    ((OneDbNode) getParentNode()).refreshChildren();
                    final TopComponent tc = TopComponentUtils.find(CollectionView.class, ci);
                    if (tc != null) {
                        tc.close();
                    }
                } catch (MongoException ex) {
                    DialogDisplayer.getDefault().notify(
                            new NotifyDescriptor.Message(ex.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE));
                }
            }
        }
    }

    public class RenameCollectionAction extends AbstractAction {

        public RenameCollectionAction() {
            super(Bundle.ACTION_RenameCollection());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final NotifyDescriptor.InputLine input = new ValidatingInputLine(
                    Bundle.renameCollectionText(collection.getName()),
                    Bundle.ACTION_RenameCollection(),
                    new CollectionNameValidator(getLookup()));
            input.setInputText(collection.getName());
            final Object dlgResult = DialogDisplayer.getDefault().notify(input);
            if (dlgResult.equals(NotifyDescriptor.OK_OPTION)) {
                try {
                    final String name = input.getInputText().trim();
                    getLookup().lookup(DBCollection.class).rename(name);
                    final OneDbNode parentNode = (OneDbNode) getParentNode();
                    parentNode.refreshChildren();

                    final CollectionView view = TopComponentUtils.find(CollectionView.class, collection);
                    if (view != null) {
                        final CollectionNode node = (CollectionNode) parentNode.getChildren().findChild(name);
                        if (node != null) {
                            view.setLookup(node.getLookup());
                            view.updateTitle();
                        }
                    }
                } catch (MongoException ex) {
                    DialogDisplayer.getDefault().notify(
                            new NotifyDescriptor.Message(ex.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE));
                }
            }
        }
    }
}
