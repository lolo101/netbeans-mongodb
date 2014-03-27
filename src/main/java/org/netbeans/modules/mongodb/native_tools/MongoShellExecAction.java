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
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.modules.mongodb.ConnectionInfo;
import org.netbeans.modules.mongodb.ui.actions.ExecutionAction;
import org.netbeans.modules.mongodb.util.ProcessCreator;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages({
    "ACTION_MongoShell=Shell",
    "# {0} - connection uri",
    "mongoShellOutputTitle=mongo - {0}"
})
public final class MongoShellExecAction extends ExecutionAction {

    public MongoShellExecAction(Lookup lookup) {
        super(Bundle.ACTION_MongoShell(), lookup);
    }

    @Override
    protected String getDisplayName() {
        final MongoClientURI uri = getMongoURI();
        return Bundle.mongoShellOutputTitle(uri != null ? uri.toString() : "localhost");
    }

    @Override
    protected ExecutionDescriptor getExecutionDescriptor() {
        return super.getExecutionDescriptor().inputVisible(true).frontWindow(true);
    }

    @Override
    protected Callable<Process> getProcessCreator() {
        final String shellExec = MongoNativeTools.MONGO_SHELL.getExecFullPath().toString();
        final ProcessCreator.Builder builder = new ProcessCreator.Builder(shellExec);
        final MongoClientURI uri = getMongoURI();
        if(uri != null) {
            parseOptionsFromURI(builder, uri);
        }
        return builder.build();
    }

    private void parseOptionsFromURI(ProcessCreator.Builder builder, MongoClientURI uri) {
        if (uri.getUsername() != null && uri.getUsername().isEmpty() == false) {
            builder.option("--username", uri.getUsername());
        }
        if (uri.getPassword() != null && uri.getPassword().length > 0) {
            builder.option("--password", new String(uri.getPassword()));
        }
        if (uri.getHosts() != null && uri.getHosts().isEmpty() == false) {
            final String hostWithPort = uri.getHosts().get(0);
            final Pattern p = Pattern.compile("(.*)(:(\\d+))?");
            final Matcher m = p.matcher(hostWithPort);
            if (m.matches()) {
                final String host = m.group(1);
                final String port = m.group(3);
                if (host.isEmpty() == false) {
                    builder.option("--host", host);
                    if (port != null) {
                        builder.option("--port", port);
                    }
                }
            }
        }
    }

    private MongoClientURI getMongoURI() {
        final ConnectionInfo ci = getLookup().lookup(ConnectionInfo.class);
        return ci != null ? ci.getMongoURI() : null;
    }
}
