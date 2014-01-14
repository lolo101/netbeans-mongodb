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
package org.netbeans.modules.nbmongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.modules.nbmongo.ui.wizards.ExportWizardAction;
import org.netbeans.modules.nbmongo.ui.wizards.ImportWizardAction;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Tim Boudreau
 * @author Yann D'Isanto
 */
@Messages({
    "DB_NAME_DESC=The name of the database",
    "DB_NAME=Database Name",
    "ACTION_AddCollection=Add Collection",
    "ACTION_Export=Export",
    "ACTION_Import=Import",
    "addCollectionTitle=Add Collection",
    "addCollectionText=Collection name:",
    "# {0} - collection name",
    "collectionAlreadyExists=Collection ''{0}'' already exists"})
public class OneDbNode extends AbstractNode {

    @StaticResource
    private static final String DB_ICON_PATH
            = "org/netbeans/modules/nbmongo/images/database.gif"; //NOI18N
    
    private final OneDBChildren childFactory;

    private final Lookup lookup;

    OneDbNode(DbInfo info) {
        this(info, new InstanceContent());
    }

    OneDbNode(DbInfo info, InstanceContent content) {
        this(info, content, new AbstractLookup(content));
    }

    OneDbNode(DbInfo info, InstanceContent content, AbstractLookup lkp) {
        this(info, content, new ProxyLookup(info.lookup, lkp, Lookups.fixed(info)));
    }

    OneDbNode(DbInfo info, InstanceContent content, ProxyLookup lkp) {
        this(info, content, lkp, new OneDBChildren(lkp));
    }

    OneDbNode(DbInfo info, InstanceContent content, ProxyLookup lookup, OneDBChildren childFactory) {
        super(Children.create(childFactory, true), lookup);
        this.childFactory = childFactory;
        this.lookup = lookup;
        content.add(info, new DBConverter());
        setName(info.dbName);
        setDisplayName(info.dbName);
        setIconBaseWithExtension(DB_ICON_PATH);
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.put(new DatabaseNameProperty(getLookup()));
        set.put(new ConnectionNameProperty(getLookup()));
        set.put(new ConnectionURIProperty(getLookup()));
        sheet.put(set);
        return sheet;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
            new AddCollectionAction(),
            new RefreshChildrenAction(childFactory),
            new ExportWizardAction(getLookup()),
            new ImportWizardAction(getLookup(), new Runnable() {

                @Override
                public void run() {
                    refreshChildren();
                }
            })
        };
    }

    public void refreshChildren() {
        childFactory.refresh();
    }

    private class DBConverter implements InstanceContent.Convertor<DbInfo, DB> {

        @Override
        public DB convert(DbInfo t) {
            DbInfo info = getLookup().lookup(DbInfo.class);
            MongoClient client = getLookup().lookup(MongoClient.class);
            return client.getDB(info.dbName);
        }

        @Override
        public Class<? extends DB> type(DbInfo t) {
            return DB.class;
        }

        @Override
        public String id(DbInfo t) {
            return t.dbName;
        }

        @Override
        public String displayName(DbInfo t) {
            return id(t);
        }
    }

    public class AddCollectionAction extends AbstractAction {

        public AddCollectionAction() {
            super(Bundle.ACTION_AddCollection());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final NotifyDescriptor.InputLine input = new NotifyDescriptor.InputLine(
                Bundle.addCollectionText(),
                Bundle.addCollectionTitle());
            boolean doLoop = true;
            while (doLoop) {
                doLoop = false;
                final Object dlgResult = DialogDisplayer.getDefault().notify(input);
                if (dlgResult.equals(NotifyDescriptor.OK_OPTION)) {

                    final String collectionName = input.getInputText();
                    final DB db = lookup.lookup(DB.class);
                    final DBObject collectionOptions = new BasicDBObject("capped", false);
                    try {
                        if (db.getCollectionNames().contains(collectionName)) {
                            DialogDisplayer.getDefault().notify(
                                new NotifyDescriptor.Message(
                                    Bundle.collectionAlreadyExists(collectionName),
                                    NotifyDescriptor.ERROR_MESSAGE));
                            doLoop = true;
                        } else {
                            db.createCollection(collectionName, collectionOptions);
                            childFactory.refresh();
                        }
                    } catch (MongoException ex) {
                        DialogDisplayer.getDefault().notify(
                            new NotifyDescriptor.Message(ex.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE));
                    }

                }
            }
        }
    }

}
