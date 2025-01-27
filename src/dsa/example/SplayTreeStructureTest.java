package dsa.example;

import dsa.iface.IBinaryTree;
import dsa.iface.IEntry;
import dsa.iface.IPosition;
import dsa.iface.ISortedMap;
import dsa.impl.SplayTreeMap;
import dsa.impl.BinarySearchTreeMap;
import dsa.impl.TreePrinter;

/**
 * Simple class to compare the structure of a Splay tree with the
 *    expected outcome.
 *
 * Inserts 6 values into a Splay Tree, which should cause 7 splaying:
 * - 1 Zig when 3 is inserted.
 * - 1 Zig-Zig when 10 is inserted.
 * - 1 Zig-Zag and 1 Zig when 5 is inserted.
 * - 1 Zig-Zag when 2 is inserted.
 * - 1 Zig-Zag and 1 Zig when 4 is inserted.
 *
 *  Remove 2 values from the Splay Tree
 * - 1 Zig when 4 is removed.
 * - 1 Zig is after switching at root when 5 is removed.
 *
 * A Binary Search Tree is constructed that has the expected final shape for the Splay tree.
 *
 * Result: Splay tree is the correct shape.
 *
 * This class is referenced from AVLTreeStructureTest.
 * @author David Lillis
 * @author Yunhan Gao
 */
public class SplayTreeStructureTest {
    public static void main( String[] args ) {

        // I want to insert the following values into a Splay tree, in order.
        // For this test, the values (which don't affect the performance in any way)
        //   will be the same as the keys).
        int[] SplayOrder = new int[] { 8, 3, 10, 5, 2, 4 };
        // Inserting them in this order into a Binary Search Tree (BST) should give me
        //   the same result as the final Splay tree (remember a BST does not restructure)
        int[] BSTOrder  = new int[] { 4, 2, 3, 5, 10, 8 };

        // create my two trees
        ISortedMap<Integer,Integer> m1 = new SplayTreeMap<>();
        ISortedMap<Integer,Integer> m2 = new BinarySearchTreeMap<>();

        // insert the values into the two trees
        for ( int v : SplayOrder )
            m1.put( v, v );

        for ( int v : BSTOrder )
            m2.put( v, v );

        // treat them as trees
        IBinaryTree<IEntry<Integer,Integer>> t1 = (IBinaryTree<IEntry<Integer,Integer>>) m1;
        IBinaryTree<IEntry<Integer,Integer>> t2 = (IBinaryTree<IEntry<Integer,Integer>>) m2;

        System.out.println("Put test:");
        TreePrinter.printTree( t1 );
        TreePrinter.printTree( t2 );

        System.out.println( "Is the Splay Tree in the expected shape(put)? " + ( areEqual( t1, t1.root(), t2, t2.root() ) ? "YES! :-D" : "No! :-(" ) );

        // Remove check
        System.out.println();
        System.out.println("Remove test:");
        m1.remove(4);
        m1.remove(5);
        t1 = (IBinaryTree<IEntry<Integer,Integer>>) m1;

        BSTOrder  = new int[] { 10, 8, 2, 3 };
        ISortedMap<Integer,Integer> m3 = new BinarySearchTreeMap<>();
        for ( int v : BSTOrder )
            m3.put( v, v );
        IBinaryTree<IEntry<Integer,Integer>> t3 = (IBinaryTree<IEntry<Integer,Integer>>) m3;
        TreePrinter.printTree( t1 );
        TreePrinter.printTree( t3 );

        System.out.println( "Is the Splay Tree in the expected shape(remove)? " + ( areEqual( t1, t1.root(), t3, t3.root() ) ? "YES! :-D" : "No! :-(" ) );
    }

    // check if two subtrees are equal (have the same shape and the same keys).
    // to check a whole tree, pass in the tree roots as the IPosition objects.
    private static <K extends Comparable<K>,V> boolean areEqual(IBinaryTree<IEntry<K,V>> t1, IPosition<IEntry<K,V>> p1, IBinaryTree<IEntry<K,V>> t2, IPosition<IEntry<K,V>> p2 ) {
        // they're both external nodes, so they are equal.
        if ( t1.isExternal( p1 ) && t2.isExternal( p2 ) )
            return true;
            // they are both internal, have the same element, and their left and right subtrees are also equal.
        else if ( t1.isInternal( p1 ) && t2.isInternal( p2 ) ) {
            return p1.element().key().equals( p2.element().key() ) && areEqual( t1, t1.left( p1 ), t2, t2.left( p2 ) ) && areEqual( t1, t1.right( p1 ), t2, t2.right( p2 ) );
        }
        // one is internal and the other is external: not the same tree.
        else {
            return false;
        }
    }
}
