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

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import org.netbeans.modules.mongodb.util.TopComponentUtils;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.progress.ProgressUtils;
import org.netbeans.modules.mongodb.beans.MongoClientURIPropertyEditor;
import org.netbeans.modules.mongodb.shell.MongoShellAction;
import org.netbeans.modules.mongodb.options.MongoShellOptions;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListeners;
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
    "ACTION_Delete=Delete",
    "ACTION_Connect=Connect",
    "ACTION_Disconnect=Disconnect",
    "waitWhileConnecting=Please wait while connecting to mongo database"
})
final class OneConnectionNode extends AbstractNode implements PropertyChangeListener {

    private static final Logger LOG = Logger.getLogger(OneConnectionNode.class.getName());

    private MongoClient mongo;

    private final Object lock = new Object();

    private final Disconnecter disconnecter = new Disconnecter();

    private final InstanceContent content;

    private final Problems problems = new Problems();

    private final ConnectionConverter converter = new ConnectionConverter();

    private volatile boolean problem;

    private OneConnectionChildren childFactory;

    OneConnectionNode(ConnectionInfo connection) {
        this(connection, new InstanceContent());
    }

    OneConnectionNode(ConnectionInfo connection, InstanceContent content) {
        this(connection, content, new ProxyLookup(new AbstractLookup(content), Lookups.fixed(connection)));
    }

    OneConnectionNode(ConnectionInfo connection, InstanceContent content, ProxyLookup lkp) {
        this(connection, content, lkp, new OneConnectionChildren(lkp));
    }

    OneConnectionNode(ConnectionInfo connection, InstanceContent content, ProxyLookup lkp, OneConnectionChildren childFactory) {
        super(Children.create(childFactory, true), lkp);
        this.childFactory = childFactory;
        this.content = content;
        content.add(problems);
        content.add(connection, converter);
        setDisplayName(connection.getDisplayName());
        setName(connection.id());
        childFactory.setParentNode(this);
        connection.addPropertyChangeListener(WeakListeners.propertyChange(this, connection));
    }

    private void setProblem(boolean problem) {
        this.problem = problem;
    }

    private boolean isConnected() {
        return mongo != null && mongo.getConnector().isOpen() && isProblem() == false;
    }

    protected boolean isProblem() {
        return problem;
    }

    @Override
    public Image getIcon(int ignored) {
        return isConnected()
            ? Images.CONNECTION_ICON
            : Images.CONNECTION_DISCONNECTED_ICON;
    }

    @Override
    public Image getOpenedIcon(int ignored) {
        return getIcon(ignored);
    }

    @Override
    public Action[] getActions(boolean ignored) {
        final Action[] orig = super.getActions(ignored);
        final Action[] nue = new Action[orig.length + 8];
        System.arraycopy(orig, 0, nue, 8, orig.length);
        final Action refreshAction = new RefreshChildrenAction(childFactory);
        final Action connectAction = new ConnectAction();
        final Action disconnectAction = new DisconnectAction();
        final Action mongoShellAction = new MongoShellAction(getLookup());
        refreshAction.setEnabled(isConnected());
        connectAction.setEnabled(isConnected() == false);
        disconnectAction.setEnabled(isConnected());
        mongoShellAction.setEnabled(MongoShellOptions.INSTANCE.getMongoExecPath() != null);
        nue[0] = connectAction;
        nue[1] = disconnectAction;
        nue[2] = null;
        nue[3] = new DeleteAction();
        nue[4] = refreshAction;
        nue[5] = null;
        nue[6] = mongoShellAction;
        nue[7] = null;
        return nue;
    }

    @Override
    public Action getPreferredAction() {
        return isConnected() ? null : new ConnectAction();
    }

    private MongoClient connect(final boolean create) {
        ProgressUtils.showProgressDialogAndRun(new Runnable() {

            @Override
            public void run() {
                synchronized (lock) {
                    if (create && isConnected() == false) {
                        final ConnectionInfo connection = getLookup().lookup(ConnectionInfo.class);
                        try {
                            mongo = new MongoClient(connection.getMongoURI());
                            mongo.getDatabaseNames();  // ensure connection works
                            content.add(disconnecter);
                            setProblem(false);
                            fireIconChange();
                            childFactory.refresh();
                            updateSheet();
                        } catch (MongoException ex) {
                            setProblem(true);
                            DialogDisplayer.getDefault().notify(
                                new NotifyDescriptor.Message(
                                    "error connectiong to mongo database: " + ex.getLocalizedMessage(),
                                    NotifyDescriptor.ERROR_MESSAGE));
                        } catch (UnknownHostException ex) {
                            setProblem(true);
                            DialogDisplayer.getDefault().notify(
                                new NotifyDescriptor.Message(
                                    "unknown server: " + ex.getLocalizedMessage(),
                                    NotifyDescriptor.ERROR_MESSAGE));
                        }

                    }
                }
            }
        }, Bundle.waitWhileConnecting());
        return mongo;
    }

    private final class Problems extends ConnectionProblems {

        @Override
        public void handleException(Exception ex, String logMessage) {
            ConnectionInfo connection = getLookup().lookup(ConnectionInfo.class);
            if (logMessage == null) {
                logMessage = ex.getMessage();
            }
            if (logMessage == null) {
                logMessage = "Problem connecting to " + connection;
            }
            LOG.log(Level.FINE, logMessage, ex); //NOI18N
            String msg = ex.getLocalizedMessage();
            if (msg != null) {
                setShortDescription(msg);
            }
            setProblem(true);
            StatusDisplayer.getDefault().setStatusText(logMessage);
            fireIconChange();
        }

        @Override
        protected <T> T retry(Callable<T> callable, Exception ex) throws Exception {
            try {
                MongoClient client;
                synchronized (lock) {
                    client = mongo;
                    mongo = null;
                }
                if (client != null && client.getConnector().isOpen()) {
                    client.close();
                }
            } catch (Exception e) {
                disconnecter.close();
                LOG.log(Level.INFO, "Reconnecting", e);
            } finally {
                ConnectionInfo info = getLookup().lookup(ConnectionInfo.class);
                content.remove(info, converter);
                content.add(info, converter);
            }
            connect(true);
            return callable.call();
        }
    }

    @Override
    protected Sheet createSheet() {
        final Sheet sheet = Sheet.createDefault();
        sheet.put(buildSheetSet());
        return sheet;
    }

    private Sheet.Set buildSheetSet() {
        final Sheet.Set set = Sheet.createPropertiesSet();
        set.put(new ConnectionNameProperty(getLookup()));
        if (isConnected()) {
            set.put(new ConnectionURIReadOnlyProperty(getLookup()));
        } else {
            final ConnectionInfo connection = getLookup().lookup(ConnectionInfo.class);
            try {
                final PropertySupport.Reflection<MongoClientURI> uriProperty = 
                    new PropertySupport.Reflection<>(connection, MongoClientURI.class, "mongoURI");
                uriProperty.setPropertyEditorClass(MongoClientURIPropertyEditor.class);
                uriProperty.setDisplayName(Bundle.ConnectionURI());
                set.put(uriProperty);
            } catch (NoSuchMethodException ex) {
                Exceptions.printStackTrace(ex);
                throw new AssertionError();
            }
        }
        return set;
    }

    private void updateSheet() {
        getSheet().put(buildSheetSet());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ConnectionInfo.PREFS_KEY_DISPLAY_NAME:
                setDisplayName((String) evt.getNewValue());
                break;
            case ConnectionInfo.PREFS_KEY_URI:
                MongoDisconnect disconnect = getLookup().lookup(MongoDisconnect.class);
                if (disconnect != null) {
                    disconnect.close();
                }
                break;
        }
    }

    private final class Disconnecter extends MongoDisconnect implements Runnable {

        @Override
        public void close() {
            RequestProcessor.getDefault().post(this);
            setProblem(false);
            setShortDescription("");
        }

        @Override
        public void run() {
            MongoClient client;
            try {
                synchronized (lock) {
                    client = mongo;
                    mongo = null;
                    updateSheet();
                }
                if (client != null) {
                    client.close();
                }
            } finally {
                ConnectionInfo info = getLookup().lookup(ConnectionInfo.class);
                content.remove(info, converter);
                content.add(info, converter);
            }
            fireIconChange();
            childFactory.refresh();
        }
    }

    private final class ConnectionConverter implements InstanceContent.Convertor<ConnectionInfo, MongoClient> {

        @Override
        public MongoClient convert(ConnectionInfo t) {
            return connect(false);
        }

        @Override
        public Class<? extends MongoClient> type(ConnectionInfo t) {
            return MongoClient.class;
        }

        @Override
        public String id(ConnectionInfo t) {
            return "mongo"; //NOI18N
        }

        @Override
        public String displayName(ConnectionInfo t) {
            return id(t);
        }
    }

    private final class DeleteAction extends AbstractAction {

        public DeleteAction() {
            super(Bundle.ACTION_Delete());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final ConnectionInfo info = getLookup().lookup(ConnectionInfo.class);
            try {
                disconnecter.close();
                info.getPreferences().removeNode();
                for (TopComponent topComponent : TopComponentUtils.findAll(info)) {
                    topComponent.close();
                }
                ((MongoServicesNode) getParentNode()).getChildrenFactory().refresh();
            } catch (BackingStoreException ex) {
                Exceptions.printStackTrace(ex);
            }

        }
    }

    private final class ConnectAction extends AbstractAction {

        public ConnectAction() {
            super(Bundle.ACTION_Connect());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            connect(true);
        }

    }

    private final class DisconnectAction extends AbstractAction {

        public DisconnectAction() {
            super(Bundle.ACTION_Disconnect());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            disconnecter.close();
        }

    }
}
