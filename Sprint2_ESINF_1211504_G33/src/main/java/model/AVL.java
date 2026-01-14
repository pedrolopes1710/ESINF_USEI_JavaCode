package model;


import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AVL <E extends Comparable<E>> extends BST<E> {

    private int balanceFactor(Node<E> node){

        return height(node.getRight()) - height(node.getLeft());
    }

    private Node<E> rightRotation(Node<E> node){

        Node<E> leftson = node.getLeft();

        node.setLeft(leftson.getRight());
        leftson.setRight(node);

        node = leftson;

        return node;
    }

    private Node<E> leftRotation(Node<E> node){

        Node<E> rightson = node.getRight();

        node.setRight(rightson.getLeft());
        rightson.setLeft(node);

        node = rightson;

        return node;
    }

    private Node<E> twoRotations(Node<E> node){

        if(balanceFactor(node) < 0){
            node.setLeft(leftRotation(node.getLeft()));
            node = rightRotation(node);
        }else{
            node.setRight(rightRotation(node.getRight()));
            node = leftRotation(node);
        }

        return node;
    }

    private Node<E> balanceNode(Node<E> node)
    {
        if(balanceFactor(node) < -1){
            if(balanceFactor(node.getLeft()) <=0){
                node = rightRotation(node);
            }else{
                node = twoRotations(node);
            }
        }

        if(balanceFactor(node) > 1){
            if(balanceFactor(node.getRight()) >=0){
                node = leftRotation(node);
            }else{
                node = twoRotations(node);
            }
        }

        return node;
    }

    @Override
    public void insert(E element){
        root = insert(element, root);
    }
    private Node<E> insert(E element, Node<E> node){

        if(node == null){
            return new Node<>(element, null, null);
        }

        int cmp = element.compareTo(node.getElement());

        if (cmp < 0) {
            node.setLeft(insert(element, node.getLeft()));
            node = balanceNode(node);
        } else if (cmp > 0) {
            node.setRight(insert(element, node.getRight()));
            node = balanceNode(node);
        } else {
            //Chave igual: adiciona à lista de duplicates e Tenta ordenar duplicates por Station name
            node.getDuplicates().add(element);
            sortDuplicatesByStationName(node.getDuplicates());

            return node;
        }

        return node;
    }

    @Override
    public void remove(E element){
        root = remove(element, root());
    }

    private Node<E> remove(E element, Node<E> node) {
        if (node == null) {
            return null;
        }

        int cmp = element.compareTo(node.getElement());

        if (cmp < 0) {
            node.setLeft(remove(element, node.getLeft()));
        } else if (cmp > 0) {
            node.setRight(remove(element, node.getRight()));
        } else {

            List<E> dups = node.getDuplicates();

            if (dups.size() > 1) {
                //Remover apenas da lista de duplicates
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

            //Remover nó normal
            if (node.getLeft() == null && node.getRight() == null) {
                return null;
            }

            if (node.getLeft() == null) {
                return node.getRight();
            }

            if (node.getRight() == null) {
                return node.getLeft();
            }

            E smallElem = smallestElement(node.getRight());
            node.setElement(smallElem);
            Node<E> minNode = find(node.getRight(), smallElem);
            node.getDuplicates().clear();

            if (minNode != null){
                node.getDuplicates().addAll(minNode.getDuplicates());
            }

            node.setRight(remove(smallElem, node.getRight()));
        }

        return balanceNode(node);
    }


    public boolean equals(Object otherObj) {

        if (this == otherObj)
            return true;

        if (otherObj == null || this.getClass() != otherObj.getClass())
            return false;

        AVL<E> second = (AVL<E>) otherObj;
        return equals(root, second.root);
    }

    public boolean equals(Node<E> root1, Node<E> root2) {
        if (root1 == null && root2 == null)
            return true;
        else if (root1 != null && root2 != null) {
            if (root1.getElement().compareTo(root2.getElement()) == 0) {
                return equals(root1.getLeft(), root2.getLeft())
                        && equals(root1.getRight(), root2.getRight());
            } else
                return false;
        }
        else return false;
    }

    /**
     * Devolve a lista de elementos exactamente iguais à chave element.
     */
    @SuppressWarnings("unchecked")
    public java.util.List<E> get(E element) {

        java.util.List<E> out = new java.util.ArrayList<>();
        Node<E> n = find(root, element);

        if (n != null) out.addAll(n.getDuplicates());

        return out;
    }

    /**
     * Range query: devolve todos os elementos econtrados no intervalo [low, high]
     * Resultado ordenado em in-order (ascendente).
     */
    @SuppressWarnings("unchecked")
    public java.util.List<E> rangeQuery(E low, E high) {

        java.util.List<E> out = new java.util.ArrayList<>();
        rangeRec(root, low, high, out);

        return out;
    }

    private void rangeRec(Node<E> node, E low, E high, java.util.List<E> acc) {

        if (node == null) return;

        //se low < node.element então explorar esquerda
        if (low.compareTo(node.getElement()) < 0) rangeRec(node.getLeft(), low, high, acc);

        //se node.element in [low, high] adiciona
        if (low.compareTo(node.getElement()) <= 0 && node.getElement().compareTo(high) <= 0) {
            acc.addAll(node.getDuplicates());
        }

        //se node.element < high então explorar direita
        if (node.getElement().compareTo(high) < 0) rangeRec(node.getRight(), low, high, acc);
    }

    // Tenta ordenar a lista duplicates por name usando reflection.
    private void sortDuplicatesByStationName(List<E> duplicates) {

        if (duplicates == null || duplicates.size() <= 1) return;

        try {
            Method m = duplicates.get(0).getClass().getMethod("getStation");
            Comparator<E> cmp = new Comparator<E>() {
                @Override
                public int compare(E o1, E o2) {
                    try {
                        Object s1 = m.invoke(o1);
                        Object s2 = m.invoke(o2);
                        Method getName = s1.getClass().getMethod("getName");
                        String n1 = (String)getName.invoke(s1);
                        String n2 = (String)getName.invoke(s2);

                        return n1.compareTo(n2);
                    } catch (Exception ex) {
                        return o1.toString().compareTo(o2.toString());
                    }
                }
            };

            Collections.sort(duplicates, cmp);
        } catch (Exception ex) {
            //Não é possível ordenar
        }
    }
}
