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
package org.netbeans.modules.mongodb.properties;

import com.mongodb.MongoClientURI;
import java.lang.reflect.InvocationTargetException;
import org.netbeans.modules.mongodb.ConnectionInfo;
import org.openide.nodes.PropertySupport;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Yann D'Isanto
 */
@Messages("LABEL_mongoURI=Mongo URI")
public final class MongoClientURIProperty extends PropertySupport.ReadOnly<MongoClientURI> {

    public static final String KEY = "mongoURI";
    
    private final Lookup lkp;

    public MongoClientURIProperty(Lookup lkp) {
        super(KEY, MongoClientURI.class, displayName(), null);
        this.lkp = lkp;
    }

    @Override
    public MongoClientURI getValue() throws IllegalAccessException, InvocationTargetException {
        return lkp.lookup(ConnectionInfo.class).getMongoURI();
    }
    
    public static String displayName() {
        return Bundle.LABEL_mongoURI();
    }
}
