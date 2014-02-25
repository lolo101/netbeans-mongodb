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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.netbeans.modules.mongodb.util.JsonProperty;

/**
 *
 * @author Yann D'Isanto
 */
public final class JsonValueNode extends CollectionViewTreeTableNode<Object> {

    public JsonValueNode(TreeTableNode parent, Object value) {
        super(parent, value, new ChildrenFactory<Object>() {

            @Override
            @SuppressWarnings("unchecked")
            public List<TreeTableNode> createChildren(TreeTableNode parent, Object value) {
                if (value instanceof Map) {
                    final Map<String, Object> map = (Map<String, Object>) value;
                    final List<TreeTableNode> children = new ArrayList<>(map.size());
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        children.add(new JsonPropertyNode(
                            parent,
                            new JsonProperty(entry.getKey(), entry.getValue())));
                    }
                    return children;
                } else if (value instanceof List) {
                    final List<Object> objects = (List<Object>) value;
                    final List<TreeTableNode> children = new ArrayList<>(objects.size());
                    for (Object object : objects) {
                        children.add(new JsonValueNode(parent, object));
                    }
                    return children;
                }
                return Collections.emptyList();
            }
        });
    }
}
