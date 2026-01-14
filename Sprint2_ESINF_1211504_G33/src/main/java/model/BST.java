package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BST<E extends Comparable<E>> implements BSTInterface<E> {

    /** Nested static class for a binary search tree node. */

    protected class Node<E> {
        private E element;          // an element stored at this node
        private Node<E> left;       // a reference to the left child (if any)
        private Node<E> right;      // a reference to the right child (if any)
        private final List<E> duplicates; // lista de elementos que partilham a mesma chave

        /**
         * Constructs a node with the given element and neighbors.
         *
         * @param e  the element to be stored
         * @param leftChild   reference to a left child node
         * @param rightChild  reference to a right child node
         */
        public Node(E e, Node<E> leftChild, Node<E> rightChild) {
            element = e;
            left = leftChild;
            right = rightChild;
            duplicates = new ArrayList<>();
            duplicates.add(e);
        }

        // accessor methods
        public E getElement() { return element; }
        public Node<E> getLeft() { return left; }
        public Node<E> getRight() { return right; }
        public List<E> getDuplicates() { return duplicates; }

        // update methods
        public void setElement(E e) { element = e; }
        public void setLeft(Node<E> leftChild) { left = leftChild; }
        public void setRight(Node<E> rightChild) { right = rightChild; }
    }

    //----------- end of nested Node class -----------

    protected Node<E> root = null;     // root of the tree


    /* Constructs an empty binary search tree. */
    public BST() {
        root = null;
    }

    /*
     * @return root Node of the tree (or null if tree is empty)
     */
    protected Node<E> root() {
        return root;
    }

    /*
     * Verifies if the tree is empty
     * @return true if the tree is empty, false otherwise
     */
    public boolean isEmpty(){
        return root==null;
    }

    /**
     * Returns the Node containing a specific Element, or null otherwise.
     *
     * @param element    the element to find
     * @return the Node that contains the Element, or null otherwise
     *
     * This method despite not being essential is very useful.
     * It is written here in order to be used by this class and its
     * subclasses avoiding recoding.
     * So its access level is protected
     */
    protected Node<E> find(Node<E> node, E element){

        if (node == null) {
            return null;
        }
        int cmp = element.compareTo(node.element);

        if (cmp == 0) {
            return node;
        } else if (cmp < 0) {
            return find(node.left, element); }
        else {
            return find(node.right, element);
        }
    }

    /*
     * Inserts an element in the tree.
     */
    public void insert(E element){
        root = insert(element, root);
    }

    private Node<E> insert(E element, Node<E> node){

        if (node == null) {
            return new Node<>(element, null, null);
        }

        int cmp = element.compareTo(node.getElement());

        if (cmp < 0) {
            node.setLeft(insert(element, node.getLeft()));
        } else if (cmp > 0) {
            node.setRight(insert(element,node.getRight()));
        } else {
            //Mesma chave: adicionar à lista de duplicates
            node.getDuplicates().add(element);
        }

        return node;
    }

    /**
     * Removes an element from the tree maintaining its consistency as a Binary Search Tree.
     */
    public void remove(E element){
        root = remove(element, root());
    }

    private Node<E> remove(E element, Node<E> node) {

        if (node == null) {
            return null;    //throw new IllegalArgumentException("Element does not exist");
        }
        int cmp = element.compareTo(node.getElement());

        if (cmp == 0) {
            //Se há múltiplos duplicates, removemos só da lista
            List<E> dups = node.getDuplicates();

            if (dups.size() > 1) {
                //Remover
                boolean removed = dups.remove(element);

                if (!removed) {

                    for (int i = 0; i < dups.size(); i++) {

                        if (dups.get(i).toString().equals(element.toString())) {
                            dups.remove(i);
                            removed = true;
                            break;
                        }
                    }
                }
                return node;
            }

            //Remoção normal do nó
            if (node.getLeft() == null && node.getRight() == null) {
                return null;
            }

            if (node.getLeft() == null) {
                return node.getRight();
            }

            if (node.getRight() == null) {
                return node.getLeft();
            }

            E min = smallestElement(node.getRight());
            node.setElement(min);

            //Copiar duplicates do node min: remover min do nó direito depois
            Node<E> minNode = find(node.getRight(), min);
            node.getDuplicates().clear();

            if (minNode != null){
                node.getDuplicates().addAll(minNode.getDuplicates());
            }

            node.setRight(remove(min, node.getRight()));

        } else if (cmp < 0) {
            node.setLeft( remove(element, node.getLeft()) );
        } else {
            node.setRight( remove(element, node.getRight()) );
        }

        return node;
    }

    /*
     * Returns the number of nodes in the tree.
     * @return number of nodes in the tree
     */
    public int size(){
        return size(root);
    }

    private int size(Node<E> node){
        if(node == null) return 0;

        int left = size(node.getLeft());
        int right = size(node.getRight());
        int dupCount = node.getDuplicates().size(); //Contar duplicates como elementos distintos

        return dupCount + left + right;
    }

    /*
     * Returns the height of the tree
     * @return height
     */
    public int height(){
        return height(root);
    }

    /*
     * Returns the height of the subtree rooted at Node node.
     * @param node A valid Node within the tree
     * @return height
     */
    protected int height(Node<E> node){

        if (node == null) {
            return -1;  // altura da árvore vazia
        }

        int leftHeight = height(node.getLeft());
        int rightHeight = height(node.getRight());

        if (leftHeight <= rightHeight) {
            return rightHeight+1;
        } else {
            return leftHeight+1;
        }

        //return Math.max(leftHeight, rightHeight); - Podia usar assim, alternativa.
    }

    /**
     * Returns the smallest element within the tree.
     * @return the smallest element within the tree
     */
    public E smallestElement(){
        return smallestElement(root);
    }

    protected E smallestElement(Node<E> node){

        if(node == null){
            return null;
        }

        if (node.getLeft() == null) {
            return node.getElement();
        } else {
            return smallestElement(node.getLeft());
        }
    }

    /**
     * Returns an iterable collection of elements of the tree, reported in
     * in-order.
     *
     * @return iterable collection of the tree's elements reported in in-order
     */
    public Iterable<E> inOrder() {
        List<E> snapshot = new ArrayList<>();
        if (root != null) {
            inOrderSubtree(root, snapshot);   // fill the snapshot recursively
        }
        return snapshot;
    }

    /**
     * Adds elements of the subtree rooted at Node node to the given snapshot
     * using an in-order traversal
     *
     * @param node Node serving as the root of a subtree
     * @param snapshot a list to which results are appended
     */
    private void inOrderSubtree(Node<E> node, List<E> snapshot) {
        if (node == null) {
            return;
        }

        inOrderSubtree(node.getLeft(), snapshot);
        snapshot.addAll(node.getDuplicates());
        inOrderSubtree(node.getRight(), snapshot);
    }

    /**
     * Returns an iterable collection of elements of the tree, reported in
     * pre-order.
     *
     * @return iterable collection of the tree's elements reported in pre-order
     */
    public Iterable<E> preOrder() {

        List<E> snapshot = new ArrayList<>();

        if (root != null) {
            preOrderSubtree(root, snapshot);   // fill the snapshot recursively
        }

        return snapshot;
    }

    /**
     * Adds elements of the subtree rooted at Node node to the given snapshot
     * using an pre-order traversal
     *
     * @param node Node serving as the root of a subtree
     * @param snapshot a list to which results are appended
     */
    private void preOrderSubtree(Node<E> node, List<E> snapshot) {

        if (node == null) {
            return;
        }

        snapshot.addAll(node.getDuplicates());
        preOrderSubtree(node.getLeft(), snapshot);
        preOrderSubtree(node.getRight(), snapshot);
    }

    /**
     * Returns an iterable collection of elements of the tree, reported in
     * post-order.
     *
     * @return iterable collection of the tree's elements reported in post-order
     */
    public Iterable<E> posOrder() {

        List<E> snapshot = new ArrayList<>();

        if (root != null) {
            posOrderSubtree(root, snapshot);   // fill the snapshot recursively
        }

        return snapshot;
    }

    /**
     * Adds positions of the subtree rooted at Node node to the given snapshot
     * using an post-order traversal
     *
     * @param node Node serving as the root of a subtree
     * @param snapshot a list to which results are appended
     */
    private void posOrderSubtree(Node<E> node, List<E> snapshot) {

        if (node == null) {
            return;
        }

        posOrderSubtree(node.getLeft(), snapshot);
        posOrderSubtree(node.getRight(), snapshot);
        snapshot.addAll(node.getDuplicates());
    }


    /*
     * Returns a map with a list of nodes by each tree level.
     * @return a map with a list of nodes by each tree level
     */
    public Map<Integer,List<E>> nodesByLevel(){
        Map<Integer, List<E>> map = new HashMap<>();
        processBstByLevel(root, map,0);

        return map;
    }

    private void processBstByLevel(Node<E> node, Map<Integer,List<E>> result, int level){
        if (node == null) {
            return;
        }

        //ver se existe a lista por nivel
        if (!result.containsKey(level)) {
            result.put(level, new ArrayList<>());
        }

        result.get(level).addAll(node.getDuplicates());
        processBstByLevel(node.getLeft(), result, level+1);
        processBstByLevel(node.getRight(), result, level+1);

    }

//#########################################################################

    /**
     * Returns a string representation of the tree.
     * Draw the tree horizontally
     */
    public String toString(){
        StringBuilder sb = new StringBuilder();
        toStringRec(root, 0, sb);

        return sb.toString();
    }

    private void toStringRec(Node<E> root, int level, StringBuilder sb){

        if(root==null){
            return;
        }


        toStringRec(root.getRight(), level+1, sb);

        if (level!=0){
            for(int i=0;i<level-1;i++){
                sb.append("|\t");
            }

            sb.append("|-------"+root.getElement()+"\n");
        } else{
            sb.append(root.getElement()+"\n");
        }

        toStringRec(root.getLeft(), level+1, sb);
    }

    @SuppressWarnings("unchecked")
    protected java.util.List<E> getNodeDuplicates(E element) {
        Node<E> n = find(root, element);
        return (n == null) ? new ArrayList<>() : new ArrayList<>(n.getDuplicates());
    }
}
