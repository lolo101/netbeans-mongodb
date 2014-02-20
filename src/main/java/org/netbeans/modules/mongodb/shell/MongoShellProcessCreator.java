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
package org.netbeans.modules.mongodb.shell;

import org.netbeans.modules.mongodb.options.MongoShellOptions;
import com.mongodb.MongoClientURI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Yann D'Isanto
 */
public final class MongoShellProcessCreator implements Callable<Process> {

    private final MongoClientURI uri;

    public MongoShellProcessCreator(MongoClientURI uri) {
        this.uri = uri;
    }

    @Override
    public Process call() throws Exception {
        return new ProcessBuilder(buildCommands(uri)).start();
    }

    private List<String> buildCommands(MongoClientURI uri) {
        final List<String> commands = new ArrayList<>();
        commands.add(MongoShellOptions.INSTANCE.getMongoExecPath());
        if (uri.getUsername() != null && uri.getUsername().isEmpty() == false) {
            commands.add("--username");
            commands.add(uri.getUsername());
        }
        if (uri.getPassword() != null && uri.getPassword().length > 0) {
            commands.add("--password");
            commands.add(new String(uri.getPassword()));
        }
        if (uri.getHosts() != null && uri.getHosts().isEmpty() == false) {
            final String hostWithPort = uri.getHosts().get(0);
            final Pattern p = Pattern.compile("(.*)(:(\\d+))?");
            final Matcher m = p.matcher(hostWithPort);
            if (m.matches()) {
                final String host = m.group(1);
                final String port = m.group(3);
                if (host.isEmpty() == false) {
                    commands.add("--host");
                    commands.add(host);
                    if (port != null) {
                        commands.add("--port");
                        commands.add(port);
                    }
                }
            }
        }
        return commands;
    }
}
