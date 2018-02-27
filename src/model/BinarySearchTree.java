package model;

public class BinarySearchTree<T extends Comparable<T>> {
    private BinarySearchTree<T> left,right;
    private T content;

    public BinarySearchTree(T content){
        this.content = content;
    }

    public boolean add(T content){
        if(content == this.content) {
            return true;
        } else {
            if(content.compareTo(this.content) > 0){
                if(left == null){
                    left = new BinarySearchTree<>(content);
                    return false;
                } else {
                    return left.add(content);
                }
            } else {
                if(right == null){
                    right = new BinarySearchTree<>(content);
                    return false;
                } else {
                    return right.add(content);
                }
            }
        }
    }

    public boolean contains(T content){
        if(content == this.content){
            return true;
        } else {
            if(content.compareTo(this.content) > 0){
                if(left == null){
                    return false;
                } else {
                    return left.contains(content);
                }
            } else {
                if(right == null){
                    return false;
                } else {
                    return right.contains(content);
                }
            }
        }
    }
}
