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

package org.netbeans.modules.mongodb.ui.windows.collectionview.treetable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.TreeNode;
import org.jdesktop.swingx.treetable.TreeTableNode;

/**
 *
 * @author Yann D'Isanto
 */
public class CollectionViewTreeTableNode<T> implements TreeTableNode {

    private final TreeTableNode parent;
    
    private final T userObject;

    private final List<TreeTableNode> children;

    public CollectionViewTreeTableNode(TreeTableNode parent, T userObject) {
        this(parent, userObject, null);
    }

    public CollectionViewTreeTableNode(TreeTableNode parent, T userObject, ChildrenFactory<T> childrenFactory) {
        this.parent = parent;
        this.userObject = userObject;
        this.children = childrenFactory != null 
            ? childrenFactory.createChildren(this, userObject) 
            : new ArrayList<TreeTableNode>();
    }

    
    @Override
    public Enumeration<? extends TreeTableNode> children() {
        return Collections.enumeration(children);
    }

    @Override
    public T getValueAt(int column) {
        return getUserObject();
    }

    @Override
    public TreeTableNode getChildAt(int childIndex) {
        return children.get(childIndex);
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public TreeTableNode getParent() {
        return parent;
    }

    @Override
    public boolean isEditable(int column) {
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int column) {
        throw new UnsupportedOperationException("this node is immutable");
    }

    @Override
    public T getUserObject() {
        return userObject;
    }

    @Override
    public void setUserObject(Object userObject) {
        throw new UnsupportedOperationException("this node is immutable");
    }

    @Override
    public int getChildCount() {
        return children.size();
    }

    @Override
    public int getIndex(TreeNode node) {
        return children.indexOf(node);
    }

    @Override
    public boolean getAllowsChildren() {
        return children.isEmpty() == false;
    }

    @Override
    public boolean isLeaf() {
        return children.isEmpty();
    }
    
    public static interface ChildrenFactory<T> {
        List<TreeTableNode> createChildren(TreeTableNode parent, T userObject);
    }
    
    public static final class SimpleChildrenFactory<T> implements ChildrenFactory<T> {

        private final Collection<TreeTableNode> children;

        public SimpleChildrenFactory(TreeTableNode... children) {
            this(Arrays.asList(children));
        }
        
        public SimpleChildrenFactory(Collection<TreeTableNode> children) {
            this.children = children;
        }
        
        @Override
        public List<TreeTableNode> createChildren(TreeTableNode parent, T userObject) {
            return new ArrayList<>(children);
        }
    }
    
}
