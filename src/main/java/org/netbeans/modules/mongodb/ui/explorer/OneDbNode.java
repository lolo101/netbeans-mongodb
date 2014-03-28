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

import org.netbeans.modules.mongodb.properties.DatabaseNameProperty;
import org.netbeans.modules.mongodb.properties.ConnectionNameProperty;
import org.netbeans.modules.mongodb.properties.MongoClientURIProperty;
import org.netbeans.modules.mongodb.resources.Images;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.modules.mongodb.DbInfo;
import org.netbeans.modules.mongodb.native_tools.MongoNativeToolsAction;
import org.netbeans.modules.mongodb.ui.util.CollectionNameValidator;
import org.netbeans.modules.mongodb.ui.util.TopComponentUtils;
import org.netbeans.modules.mongodb.ui.util.ValidatingInputLine;
import org.netbeans.modules.mongodb.ui.windows.CollectionView;
import org.netbeans.modules.mongodb.ui.wizards.ExportWizardAction;
import org.netbeans.modules.mongodb.ui.wizards.ImportWizardAction;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle.Messages;
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
    "ACTION_AddCollection=Add Collection",
    "ACTION_DropDatabase=Drop Database",
    "ACTION_Export=Export",
    "ACTION_Import=Import",
    "addCollectionText=Collection name:",
    "# {0} - collection name",
    "collectionAlreadyExists=Collection ''{0}'' already exists",
    "# {0} - database name",
    "dropDatabaseConfirmText=Permanently drop ''{0}'' database?"})
final class OneDbNode extends AbstractNode {

    private final OneDBChildren childFactory;

    OneDbNode(DbInfo info) {
        this(info, new InstanceContent());
    }

    OneDbNode(DbInfo info, InstanceContent content) {
        this(info, content, new AbstractLookup(content));
    }

    OneDbNode(DbInfo info, InstanceContent content, AbstractLookup lkp) {
        this(info, content, new ProxyLookup(info.getLookup(), lkp, Lookups.fixed(info)));
    }

    OneDbNode(DbInfo info, InstanceContent content, ProxyLookup lkp) {
        this(info, content, lkp, new OneDBChildren(lkp));
    }

    OneDbNode(DbInfo info, InstanceContent content, ProxyLookup lookup, OneDBChildren childFactory) {
        super(Children.create(childFactory, true), lookup);
        this.childFactory = childFactory;
        content.add(info, new DBConverter());
        setName(info.getDbName());
        setDisplayName(info.getDbName());
        setIconBaseWithExtension(Images.DB_ICON_PATH);
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.put(new DatabaseNameProperty(getLookup()));
        set.put(new ConnectionNameProperty(getLookup()));
        set.put(new MongoClientURIProperty(getLookup()));
        sheet.put(set);
        return sheet;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
            new AddCollectionAction(),
            new RefreshChildrenAction(childFactory),
            new DropDatabaseAction(),
            null,
            new MongoNativeToolsAction(getLookup()),
            null,
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
            return client.getDB(info.getDbName());
        }

        @Override
        public Class<? extends DB> type(DbInfo t) {
            return DB.class;
        }

        @Override
        public String id(DbInfo t) {
            return t.getDbName();
        }

        @Override
        public String displayName(DbInfo t) {
            return id(t);
        }
    }

    public final class AddCollectionAction extends AbstractAction {

        public AddCollectionAction() {
            super(Bundle.ACTION_AddCollection());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final NotifyDescriptor.InputLine input = new ValidatingInputLine(
                Bundle.addCollectionText(),
                Bundle.ACTION_AddCollection(),
                new CollectionNameValidator(getLookup()));
            final Object dlgResult = DialogDisplayer.getDefault().notify(input);
            if (dlgResult.equals(NotifyDescriptor.OK_OPTION)) {
                final String collectionName = input.getInputText().trim();
                final DB db = getLookup().lookup(DB.class);
                final DBObject collectionOptions = new BasicDBObject("capped", false);
                try {
                    db.createCollection(collectionName, collectionOptions);
                    childFactory.refresh();
                } catch (MongoException ex) {
                    DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Message(ex.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE));
                }

            }
        }
    }

    public final class DropDatabaseAction extends AbstractAction {

        public DropDatabaseAction() {
            super(Bundle.ACTION_DropDatabase());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final DB db = getLookup().lookup(DB.class);

            final Object dlgResult = DialogDisplayer.getDefault().notify(new NotifyDescriptor.Confirmation(
                Bundle.dropDatabaseConfirmText(db.getName()),
                NotifyDescriptor.YES_NO_OPTION));
            if (dlgResult.equals(NotifyDescriptor.OK_OPTION)) {
                try {
                    db.dropDatabase();
                    ((OneConnectionNode) getParentNode()).refreshChildren();
                    final DbInfo dbInfo = getLookup().lookup(DbInfo.class);
                    for (TopComponent topComponent : TopComponentUtils.findAll(CollectionView.class, dbInfo)) {
                        topComponent.close();
                    }
                } catch (MongoException ex) {
                    DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Message(ex.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE));
                }
            }
        }
    }

}
