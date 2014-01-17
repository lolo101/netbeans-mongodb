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
package org.netbeans.modules.mongodb.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.TreeNode;

/**
 *
 * @author Yann D'Isanto
 */
public class ImmutableTreeNode<T> implements TreeNode {

    private final TreeNode parent;

    private final T userObject;

    private final List<TreeNode> children;

    public ImmutableTreeNode(TreeNode parent, T userObject) {
        this(parent, userObject, null);
    }

    public ImmutableTreeNode(TreeNode parent, T userObject, ChildrenFactory<T> childrenFactory) {
        this.parent = parent;
        this.userObject = userObject;
        this.children = childrenFactory != null 
            ? childrenFactory.createChildren(this, userObject) 
            : new ArrayList<TreeNode>();
    }

    public final T getUserObject() {
        return userObject;
    }

    @Override
    public final TreeNode getChildAt(int childIndex) {
        return children.get(childIndex);
    }

    @Override
    public final int getChildCount() {
        return children.size();
    }

    @Override
    public final TreeNode getParent() {
        return parent;
    }

    @Override
    public final int getIndex(TreeNode node) {
        return children.indexOf(node);
    }

    @Override
    public final boolean getAllowsChildren() {
        return children.isEmpty() == false;
    }

    @Override
    public final boolean isLeaf() {
        return children.isEmpty();
    }

    @Override
    public final Enumeration children() {
        return Collections.enumeration(children);
    }

    public static interface ChildrenFactory<T> {

        List<TreeNode> createChildren(TreeNode parent, T userObject);
    }
}
