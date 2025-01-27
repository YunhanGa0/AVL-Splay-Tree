package dsa.impl;

import dsa.iface.IEntry;
import dsa.iface.ISortedMap;

public class AVLTreeMap<K extends Comparable<K>,V> extends BinarySearchTreeMap<K,V> implements ISortedMap<K,V> {

   public AVLTreeMap() {
      super();
   }

   @Override
   public V get(K k) {
      return super.get(k);
   }

   @Override
   public V put(K k, V v) {
      AVLPosition p =  (AVLPosition) this.find(this.root(),k); // Find the node to insert
      V value = super.put(k, v); // Invoke the insert method of the base class
      afterPut(p);
      return value;
   }

   @Override
   public V remove(K k) {
      AVLPosition p =  (AVLPosition) this.find(this.root(),k); // Find the node to remove
      V value = super.remove(k); // Invoke the remove method of the base class
      afterRemove(p);
      return value;
   }



   // YOU SHOULD NEED TO ENTER ANYTHING BELOW THIS

   private void afterPut(AVLPosition node){
      while (node != null) {
         updateHeight(node);
         int bf = Math.abs(heightOf((AVLPosition) node.left) - heightOf((AVLPosition) node.right));
         if (bf == 2){
            while (node != null) {
               updateHeight(node); // Update the height along the path
               node = restructure(node); // Rebalance the tree while necessary
               node = (AVLPosition) node.parent; // All the way up to the root node
            }
            break;
         }
         node = (AVLPosition) node.parent; // Keep going up to the root node
      }
   }

   private void afterRemove(AVLPosition node){
      // Find the nearest unbalanced node of its grandfather node
      while ((node = (AVLPosition) node.parent) != null) {
         // Determine whether the current node is balanced
         if (node.isBalanced(node)) {
            // If balanced, update the height of the node
            updateHeight(node);
         } else {
            // If unbalanced, this node is the unbalanced node closest to the added node
            restructure(node);
         }
      }
   }

   private AVLPosition restructure(AVLPosition p) {
      if (heightOf((AVLPosition) p.left) - heightOf((AVLPosition) p.right) == 2){  // Left child has bigger height
         if (heightOf((AVLPosition) p.left.left) > heightOf((AVLPosition) p.left.right)){ // left.left is higher than left.right, LL rotate
            rightRotate(p);
         } else if (heightOf((AVLPosition) p.left.left) == heightOf((AVLPosition) p.left.right)) { // left.left is equal as left.right, choose the outside one, LL rotate
            rightRotate(p);
         } else { // LR
            leftAndRightRotate(p);
         }
      } else if (heightOf((AVLPosition) p.left) - heightOf((AVLPosition) p.right) == -2) { // Right child has bigger height
         if (heightOf((AVLPosition) p.right.right) > heightOf((AVLPosition) p.right.left)){ // right.right is higher than right.left, RR rotate
            leftRotate(p);
         } else if (heightOf((AVLPosition) p.left.left) == heightOf((AVLPosition) p.left.right)) { // right.right is equal as right.left, choose the outside one, RR rotate
            leftRotate(p);
         }
         else { // RL
            rightAndLeftRotate(p);
         }
      }
      return p;
   }

   private void leftRotate(AVLPosition grand) {
      // Get the parent node
      AVLPosition parent = (AVLPosition)grand.right;
      // Extract the left child of the parent node
      AVLPosition leftChild = (AVLPosition)parent.left;
      // Rotate left
      grand.right = leftChild;
      parent.left = grand;
      // After rotation makes the parent node be the root node and updates the heights of grand, parent, and child nodes
      afterRotate(grand,parent,leftChild);
   }

   private void rightRotate(AVLPosition grand) {
      // Get the parent node, as well as the left child of the grandparent node
      AVLPosition parent = (AVLPosition)grand.left;
      // Get the right child of the parent node, so that the height can be updated easier later
      AVLPosition rightChild = (AVLPosition)parent.right;
      // Rotate right
      grand.left = rightChild;
      parent.right = grand;
      // After rotation makes the parent node be the root node and updates the heights of grand, parent, and child nodes
      afterRotate(grand,parent,rightChild);
   }

   private void leftAndRightRotate(AVLPosition t) {
      leftRotate((AVLPosition) t.left);
      rightRotate(t);
   }

   private void rightAndLeftRotate(AVLPosition t) {
      rightRotate((AVLPosition) t.right);
      leftRotate(t);
   }

   private void afterRotate(AVLPosition grand,AVLPosition parent,AVLPosition child){
      // Make the parent node the root of the current subtree
      parent.parent = grand.parent;
      if(grand.isLeftChild()){
         grand.parent.left = parent;
      }else if(grand.isRightChild()){
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
      // Update height, compare the lower node first and update the higher node
      updateHeight(grand);
      updateHeight(parent);
   }

   private void updateHeight(AVLPosition p) {
      p.height = 1 + Math.max(heightOf((AVLPosition) p.left), heightOf((AVLPosition)p.right));
   }

   private int heightOf(AVLPosition p) {
      return (p == null) ? -1 : p.height;
   }

   /**
    * Define a subclass of BTPosition so that we can also store the height
    *    of each position in its object.
    *
    * This will be more efficient than calculating the height every time
    *    we need it, but we will need to update heights whenever we change
    *    the structure of the tree.
    */
   class AVLPosition extends AbstractBinaryTree<IEntry<K,V>>.BTPosition {
      // Store the height of this position, so that we can test for balance
      public int height = 0;

      /**
       * Constructor - create a new AVL node
       * @param element The element to store in the node.
       * @param parent The parent node of this node (or {@code null} if this is the root)
       */
      AVLPosition( IEntry<K,V> element, AbstractBinaryTree.BTPosition parent ) {
         super( element, parent );
      }

      public boolean isLeftChild(){
         return parent != null && this == parent.left;
      }
      public boolean isRightChild(){
         return parent != null && this == parent.right;
      }

      // Get the balance factor of the current node
      public int balanceFactor(){
         int leftHeight = left == null ? 0 : ((AVLPosition)left).height;
         int rightHeight = right == null ? 0 : ((AVLPosition)right).height;
         return leftHeight - rightHeight;
      }

      private boolean isBalanced(AVLPosition node){
         // The incoming node is converted to AVLNode, and then the absolute value of the node's balance factor is less than or equal to 1, indicating balance
         return Math.abs((node).balanceFactor()) <= 1;
      }
   }

   @Override
   protected AbstractBinaryTree<IEntry<K,V>>.BTPosition newPosition(IEntry<K,V> element, AbstractBinaryTree<IEntry<K,V>>.BTPosition parent) {
      return new AVLPosition(element, parent);
   }
}
