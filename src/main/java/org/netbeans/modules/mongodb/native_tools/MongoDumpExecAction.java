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
package org.netbeans.modules.mongodb.native_tools;

import com.mongodb.MongoClientURI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.modules.mongodb.ConnectionInfo;
import org.netbeans.modules.mongodb.ui.actions.ExecutionAction;
import org.netbeans.modules.mongodb.ui.native_tools.MongoDumpOptionsPanel;
import org.netbeans.modules.mongodb.util.ProcessCreator;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "ACTION_MongoDump=Dump",
    "mongoDumpOutputTitle=mongodump"
})
public final class MongoDumpExecAction extends ExecutionAction {

    public MongoDumpExecAction(Lookup lookup) {
        super(Bundle.ACTION_MongoDump(), lookup);
    }

    @Override
    protected String getDisplayName() {
        return Bundle.mongoDumpOutputTitle();
    }

    @Override
    protected ExecutionDescriptor getExecutionDescriptor() {
        return super.getExecutionDescriptor().frontWindow(true);
    }

    @Override
    protected Callable<Process> getProcessCreator() {
        final String mongoDumpExec = MongoNativeTools.MONGO_DUMP.getExecFullPath().toString();
        final Map<String, String> options = MongoDumpOptionsPanel.showDialog(getOptionsFromContext());
        if (options == null) {
            return null;
        }
        return new ProcessCreator.Builder(mongoDumpExec)
            .options(options)
            .build();
    }

    private Map<String, String> getOptionsFromContext() {
        final Map<String, String> options = new HashMap<>();
        final ConnectionInfo ci = getLookup().lookup(ConnectionInfo.class);
        if (ci != null) {
            final MongoClientURI uri = ci.getMongoURI();
            parseOptionsFromURI(uri, options);
        }
        return options;
    }

    private void parseOptionsFromURI(MongoClientURI uri, Map<String, String> options) {
        if (uri.getUsername() != null && uri.getUsername().isEmpty() == false) {
            options.put(MongoDumpOptions.USERNAME, uri.getUsername());
        }
        if (uri.getPassword() != null && uri.getPassword().length > 0) {
            options.put(MongoDumpOptions.PASSWORD, new String(uri.getPassword()));
        }
        if (uri.getHosts() != null && uri.getHosts().isEmpty() == false) {
            final String hostWithPort = uri.getHosts().get(0);
            final Pattern p = Pattern.compile("(.*)(:(\\d+))?");
            final Matcher m = p.matcher(hostWithPort);
            if (m.matches()) {
                final String host = m.group(1);
                final String port = m.group(3);
                if (host.isEmpty() == false) {
                    options.put(MongoDumpOptions.HOST, host);
                    if (port != null) {
                        options.put(MongoDumpOptions.PORT, port);
                    }
                }
            }
        }
    }
}
