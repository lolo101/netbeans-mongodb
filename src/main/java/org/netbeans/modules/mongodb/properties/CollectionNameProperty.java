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

import java.lang.reflect.InvocationTargetException;
import org.netbeans.modules.mongodb.CollectionInfo;
import org.openide.nodes.PropertySupport;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Tim Boudreau
 * @author Yann D'Isanto
 */
@Messages({
    "LABEL_collectionName=Collection Name",
    "EMPTY_collectionName=[no name]"
})
public final class CollectionNameProperty extends PropertySupport.ReadOnly<String> {

    public static final String KEY = "collectionName";
    
    private final Lookup lkp;

    public CollectionNameProperty(Lookup lkp) {
        super(KEY, String.class, Bundle.LABEL_collectionName(), null);
        this.lkp = lkp;
    }

    @Override
    public String getValue() throws IllegalAccessException, InvocationTargetException {
        CollectionInfo info = lkp.lookup(CollectionInfo.class);
        return info == null ? Bundle.EMPTY_collectionName() : info.getName();
    }
}
