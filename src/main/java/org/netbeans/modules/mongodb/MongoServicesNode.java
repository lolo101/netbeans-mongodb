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
package org.netbeans.modules.mongodb;

import org.netbeans.modules.mongodb.ui.components.NewConnectionPanel;
import com.mongodb.DBTCPConnector;
import com.mongodb.MongoClientURI;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.core.ide.ServicesTabNodeRegistration;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;

/**
 * The services tab node for MongoDB connections.
 *
 * @author Tim Boudreau
 * @author Yann D'Isanto
 */
@ServicesTabNodeRegistration(displayName = "Mongo DB", iconResource = Images.MONGO_ICON_PATH, position = 3, name = "mongodb")
@Messages("MongoNodeName=Mongo DB")
public final class MongoServicesNode extends AbstractNode {

    private final ConnectionChildFactory factory;

    public MongoServicesNode() {
        this(new ConnectionChildFactory());
    }

    MongoServicesNode(ConnectionChildFactory factory) {
        super(Children.create(factory, false));
        this.factory = factory;
        setDisplayName(Bundle.MongoNodeName());
        setIconBaseWithExtension(Images.MONGO_ICON_PATH);
    }

    static {
        // By default every connection exception will result in a
        // popup dialog.  Try to crank down the volume.
        Logger mongoLogger = Logger.getLogger("com.mongodb");
        mongoLogger.setUseParentHandlers(false);
        Class<DBTCPConnector> c = DBTCPConnector.class;
        try {
            Field field = c.getDeclaredField("_logger");
            field.setAccessible(true);
            Logger lggr = (Logger) field.get(null);
            System.out.println("IT IS THE SAME? " + (lggr == mongoLogger));
            mongoLogger = lggr;
            if (mongoLogger != null) {
                mongoLogger.setUseParentHandlers(false);
            }
        } catch (NoSuchFieldException 
            | SecurityException 
            | IllegalArgumentException 
            | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    ConnectionChildFactory getChildrenFactory() {
        return factory;
    }

    static Preferences prefs() {
        return NbPreferences.forModule(MongoServicesNode.class).node("connections"); //NOI18N
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new NewConnectionAction()};
    }

    @Messages("LBL_newConnection=New Connection")
    private class NewConnectionAction extends AbstractAction {

        public NewConnectionAction() {
            super(Bundle.LBL_newConnection());
        }

        @Override
        @Messages("TTL_newConnection=New MongoDB Connection")
        public void actionPerformed(ActionEvent e) {
            final NewConnectionPanel panel = new NewConnectionPanel();
            final DialogDescriptor desc = new DialogDescriptor(panel, Bundle.TTL_newConnection());
            panel.setNotificationLineSupport(desc.createNotificationLineSupport());
            panel.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    desc.setValid(panel.isOk());
                }
            });
            if (NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(desc))) {
                final MongoClientURI uri = panel.getMongoURI();
                final String name = panel.getConnectionName();
                final Preferences prefs = prefs();
                try (ConnectionInfo info = new ConnectionInfo(prefs)) {
                    if (!name.isEmpty()) {
                        info.setDisplayName(name);
                    }
                    info.setMongoURI(uri);
                } finally {
                    factory.refresh();
                }
            }
        }
    }
}
