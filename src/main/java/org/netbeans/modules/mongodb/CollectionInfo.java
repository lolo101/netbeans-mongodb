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

import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import org.openide.util.Lookup;

/**
 *
 * @author Tim Boudreau
 * @author Yann D'Isanto
 */
public final class CollectionInfo implements Comparable<CollectionInfo> {

    @Getter
    private final String name;

    @Getter
    private final Lookup lookup;

    public CollectionInfo(@NonNull String name, @NonNull Lookup lookup) {
        this.name = name;
        this.lookup = lookup;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (object instanceof CollectionInfo) {
            final CollectionInfo other = (CollectionInfo) object;
            return name.equals(other.name)
                && Objects.equals(
                    lookup.lookup(DbInfo.class),
                    other.lookup.lookup(DbInfo.class));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.name);
        hash = 71 * hash + Objects.hashCode(this.lookup.lookup(DbInfo.class));
        return hash;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(CollectionInfo o) {
        return name.compareToIgnoreCase(o.name);
    }
}
