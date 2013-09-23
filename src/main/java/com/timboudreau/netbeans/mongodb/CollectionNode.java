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
package com.timboudreau.netbeans.mongodb;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import com.timboudreau.netbeans.mongodb.views.CollectionViewTopComponent;
import java.awt.event.ActionEvent;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
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
import org.openide.windows.WindowManager;

/**
 *
 * @author Tim Boudreau
 */
@Messages({
    "ACTION_DropCollection=Drop Collection",
    "# {0} - collection name",
    "dropCollectionConfirmText=Permanently drop ''{0}'' collection?"})
final class CollectionNode extends AbstractNode {

    private final Lookup lookup;

    CollectionNode(CollectionInfo connection) {
        this(connection, new InstanceContent());
    }

    CollectionNode(CollectionInfo connection, InstanceContent content) {
        this(connection, content, new ProxyLookup(new AbstractLookup(content), Lookups.fixed(connection), connection.lookup));
    }

    CollectionNode(final CollectionInfo collection, final InstanceContent content, final ProxyLookup lookup) {
        super(Children.LEAF, lookup);
        this.lookup = lookup;
        content.add(collection);
        content.add(collection, new CollectionConverter());
        content.add(new OpenCookie() {

            @Override
            public void open() {
                TopComponent tc = findTopComponent(collection);
                if (tc == null) {
                    tc = new CollectionViewTopComponent(collection, lookup);
                    tc.open();
                }
                tc.requestActive();
            }
        });
        setDisplayName(collection.name);
        setName(collection.name);
        setIconBaseWithExtension(MongoServicesNode.MONGO_COLLECTION);
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.put(new CollectionNameProperty(getLookup()));
        set.put(new DatabaseNameProperty(getLookup()));
        set.put(new ConnectionNameProperty(getLookup()));
        set.put(new ConnectionURIProperty(getLookup()));
        sheet.put(set);
        return sheet;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
            SystemAction.get(OpenAction.class),
            new DropCollectionAction()
        };
    }

    @Override
    public Action getPreferredAction() {
        return SystemAction.get(OpenAction.class);
    }

    private TopComponent findTopComponent(CollectionInfo collection) {
        final Set<TopComponent> openTopComponents = WindowManager.getDefault().getRegistry().getOpened();
        for (TopComponent tc : openTopComponents) {
            if (tc instanceof CollectionViewTopComponent) {
                if (tc.getLookup().lookup(CollectionInfo.class) == collection) {
                    return tc;
                }
            }
        }
        return null;
    }

    private class CollectionConverter implements InstanceContent.Convertor<CollectionInfo, DBCollection> {

        @Override
        public DBCollection convert(CollectionInfo t) {
            DB db = getLookup().lookup(DB.class);
            return db.getCollection(t.name);
        }

        @Override
        public Class<? extends DBCollection> type(CollectionInfo t) {
            return DBCollection.class;
        }

        @Override
        public String id(CollectionInfo t) {
            return t.name;
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
            final CollectionInfo ci = lookup.lookup(CollectionInfo.class);
            final Object dlgResult = DialogDisplayer.getDefault().notify(new NotifyDescriptor.Confirmation(
                    Bundle.dropCollectionConfirmText(ci.getName()),
                    NotifyDescriptor.YES_NO_OPTION));
            if (dlgResult.equals(NotifyDescriptor.OK_OPTION)) {
                try {
                    lookup.lookup(DBCollection.class).drop();
                    ((OneDbNode) getParentNode()).refreshChildren();
                    final TopComponent tc = findTopComponent(ci);
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
}
