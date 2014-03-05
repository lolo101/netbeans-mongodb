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

import com.mongodb.DB;
import com.mongodb.MongoClient;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import org.netbeans.modules.mongodb.CollectionInfo;
import org.netbeans.modules.mongodb.ConnectionProblems;
import org.netbeans.modules.mongodb.DbInfo;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 * @author Tim Boudreau
 * @author Yann D'Isanto
 */
final class OneDBChildren extends RefreshableChildFactory<CollectionInfo> {

    private final Lookup lookup;

    public OneDBChildren(Lookup lookup) {
        this.lookup = lookup;
    }

    @Override
    protected boolean createKeys(final List<CollectionInfo> list) {
        final ConnectionProblems problems = lookup.lookup(ConnectionProblems.class);
        problems.invoke(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                MongoClient client = lookup.lookup(MongoClient.class);
                DbInfo info = lookup.lookup(DbInfo.class);
                final DB db = client.getDB(info.getDbName());
                List<String> names = new LinkedList<>(db.getCollectionNames());
                for (String name : names) {
                    list.add(new CollectionInfo(name, lookup));
                }
                return null;
            }
        });
        return true;
    }

    @Override
    protected Node createNodeForKey(CollectionInfo key) {
        return new CollectionNode(key);
    }
}
