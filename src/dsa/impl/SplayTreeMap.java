package dsa.impl;

import dsa.iface.IEntry;
import dsa.iface.IPosition;
import dsa.iface.ISortedMap;

public class SplayTreeMap<K extends Comparable<K>, V> extends BinarySearchTreeMap<K, V> implements ISortedMap<K, V> {

    public SplayTreeMap() {super(); }

    @Override
    public V get(K k) {
        BTPosition p = (BTPosition) this.find(this.root(), k);
        V value = super.get(k);
        if (value == null){
            if (p.parent != null){splay(p.parent);}
            return value;
        }
        else {
            splay(p);
        }
        return value;
    }

    @Override
    public V put(K k, V v) {
        BTPosition p = (BTPosition)this.find(this.root(), k); // Find the node to insert
        if (isInternal(p)) { // If the value is already exist in the tree
            splay(p);
            return p.element().value();
        } else {
            V value = super.put(k, v); // Invoke the insert method of the base class
            splay(p);
            return value;
        }
    }

    @Override
    public V remove(K k) {
        BTPosition p = (BTPosition)this.find(this.root(), k); // Find the node to remove
        if (p != root){
            if (p == null){
                splay(p.parent);
                return null;
            }else {
                splay(p);
            }
        }
        if (isInternal(p.left)&&isInternal(p.right)){
            //BTPosition highestRight = p.right;
            BTPosition highestRight = newRemove(k);
            V value = super.remove(k);
            splay(highestRight.parent);
            return value;
        }else {
            return super.remove(k);
        }
    }

    private BTPosition newRemove(K var1) {
        IPosition var2 = this.find(this.root(), var1);
        if (!this.isInternal(var2)) {
            return null;
        } else {
            if (this.isInternal(this.left(var2)) && this.isInternal(this.right(var2))) {
                IPosition var4;
                for(var4 = this.right(var2); this.isInternal(this.left(var4)); var4 = this.left(var4)) {
                }
                return (BTPosition)var4;
            }
            return null;
        }
    }

    private void splay(BTPosition p) {
        while (!isRoot(p)) {
            if (p.parent == root()) { // Zig: If the parent of the current node is the root
                if (p == left(parent(p))) { // If it's a left child, perform a right rotation on the parent
                    rightRotate((BTPosition) parent(p));
                } else { // If it's a right child, perform a left rotation on the parent
                    leftRotate((BTPosition) parent(p));
                }
            } else {
                BTPosition parent = (BTPosition) parent(p);
                BTPosition grand = (BTPosition) parent(parent);
                if (p == left(parent) && parent == left(grand)) { // Zig-Zig
                    rightRotate(grand);
                    rightRotate(parent);
                } else if (p == right(parent) && parent == right(grand)) { // Zig-Zig
                    leftRotate(grand);
                    leftRotate(parent);
                } else if (p == left(parent) && parent == right(grand)) { // Zig-Zag
                    rightRotate(parent);
                    leftRotate(grand);
                } else { // Zig-Zag
                    leftRotate(parent);
                    rightRotate(grand);
                }
            }
        }
    }

    private void leftRotate(BTPosition grand) {
        // Get the parent node
        BTPosition parent = grand.right;
        // Extract the left child of the parent node
        BTPosition leftChild = parent.left;
        // Extract the left child of the parent node
        grand.right = leftChild;
        parent.left = grand;
        // After rotation makes the parent node be the root node and updates the heights of grand, parent, and child nodes
        afterRotate(grand,parent,leftChild);
    }

    private void rightRotate(BTPosition grand) {
        // Get the parent node, as well as the left child of the grandparent node
        BTPosition parent = grand.left;
        // Get the right child of the parent node, so that the height can be updated easier later
        BTPosition rightChild = parent.right;
        // Rotate right
        grand.left = rightChild;
        parent.right = grand;
        // After rotation makes the parent node be the root node and updates the heights of grand, parent, and child nodes
        afterRotate(grand,parent,rightChild);
    }

    private void afterRotate(BTPosition grand, BTPosition parent, BTPosition child){
        // Make the parent node the root of the current subtree
        parent.parent = grand.parent;
        if((grand.parent) != null && (grand == (grand.parent).left)){
            grand.parent.left = parent;
        }else if((grand.parent) != null && (grand == (grand.parent).right)){
            grand.parent.right = parent;
        }else {
            // The current node has no parent, that is, the grand node is the root node
            root = parent;
        }

        // The grand node was updated above, so it's also need to update the parent node and the parent node of the leftChild node
        if(child != null){
            child.parent = grand;
        }
        // Update the parent node of the grand node
        grand.parent = parent;
    }
}
