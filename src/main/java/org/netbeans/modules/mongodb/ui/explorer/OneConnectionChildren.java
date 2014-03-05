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

import com.mongodb.MongoClient;
import java.util.List;
import java.util.concurrent.Callable;
import org.netbeans.modules.mongodb.ConnectionInfo;
import org.netbeans.modules.mongodb.ConnectionProblems;
import org.netbeans.modules.mongodb.DbInfo;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author Tim Boudreau
 * @author Yann D'Isanto
 */
final class OneConnectionChildren extends RefreshableChildFactory<DbInfo> {

    private OneConnectionNode parentNode;

    private final Lookup lookup;

    public OneConnectionChildren(Lookup lookup) {
        this.lookup = lookup;
    }

    @Override
    protected boolean createKeys(final List<DbInfo> list) {
        if(parentNode == null) {
            return true;
        }
        final ConnectionProblems problems = lookup.lookup(ConnectionProblems.class);
        final ConnectionInfo connectionInfo = lookup.lookup(ConnectionInfo.class);
        final MongoClient client = lookup.lookup(MongoClient.class);
       
        if (client != null && client.getConnector().isOpen() && parentNode.isProblem() == false) {
            problems.invoke(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    final String connectionDBName = connectionInfo.getMongoURI().getDatabase();
                    if (connectionDBName != null) {
                        list.add(new DbInfo(lookup, connectionDBName));
                    } else {
                        for (String dbName : client.getDatabaseNames()) {
                            list.add(new DbInfo(lookup, dbName));
                        }
                    }
                    return null;
                }
            });
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(DbInfo key) {
        return new OneDbNode(key);
    }

    public OneConnectionNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(OneConnectionNode parentNode) {
        this.parentNode = parentNode;
    }

}
