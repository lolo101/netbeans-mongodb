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
package org.netbeans.modules.mongodb.ui.windows.collectionview;

import com.mongodb.DBObject;

/**
 * A listener to be notified on collection query result update events.
 *
 * @author Yann D'Isanto
 */
public interface CollectionQueryResultUpdateListener {

    /**
     * Invoked on collection query result update start.
     *
     * @param source the collection query result event source
     */
    void updateStarting(CollectionQueryResult source);

    /**
     * Invoked when a document is added to the collection query result during
     * update.
     *
     * @param source the collection query result event source
     * @param document the added document
     */
    void documentAdded(CollectionQueryResult source, DBObject document);

    /**
     * Invoked on collection query result update end.
     *
     * @param source the collection query result event source
     */
    void updateFinished(CollectionQueryResult source);
}
