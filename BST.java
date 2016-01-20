import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.function.Function;
import java.util.LinkedList;
import java.util.Queue;

public class BST {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        // ArrayLists for 3 input lines
        ArrayList<Integer> data_add = new ArrayList<Integer>();
        ArrayList<Integer> data_del = new ArrayList<Integer>();
        ArrayList<Integer> data_out = new ArrayList<Integer>();

        //Block of reading input data
        StringBuffer sb = null;
        Scanner in = new Scanner(new File("bst.in"));
        sb = new StringBuffer();

        //1st line
        sb.append(in.nextLine()).append("\n");
        String[] numbers = sb.toString().split("\\s+");
        for (int i = 0; i < numbers.length; i++)
            data_add.add(i, Integer.valueOf(numbers[i]));
        sb = sb.delete(0, sb.length());

        //2nd line
        sb.append(in.nextLine()).append("\n");
        numbers = sb.toString().split("\\s+");
        for (int i = 0; i < numbers.length; i++)
            data_del.add(i, Integer.valueOf(numbers[i]));
        sb = sb.delete(0, sb.length());

        //3rd line
        sb.append(in.nextLine()).append("\n");
        numbers = sb.toString().split("\\s+");
        for (int i = 0; i < numbers.length; i++)
            data_out.add(i, Integer.valueOf(numbers[i]));


        //create BST and add elements
        BinarySearchTree<Integer> bst = new BinarySearchTree<Integer>();
        BinarySearchTreeNode<Integer> node;
        node = bst.addRoot(data_add.get(0));
        for (int i = 1; i < data_add.size(); i++)
            bst.add(node, data_add.get(i));

        //delete elements
        for (int i = 0; i < data_del.size(); i++)
            bst.delete(node, data_del.get(i));

        //create output string
        BinarySearchTreeNode<Integer> temp;
        String str = "";
        for (int i = 0; i < data_out.size(); i++) {
            temp = bst.find(node, data_out.get(i));
            if (temp != null)
                if (temp.right != null)
                    str = str + temp.right.data + " ";
                else
                    str = str + "null ";
            else
                str = str + "null ";
        }

        //write to file
        str = str.substring(0, str.length() - 1);
        PrintWriter writer = new PrintWriter("bst.out", "UTF-8");
        writer.print(str);
        writer.close();

    }

    public static class BinarySearchTreeNode<E> {
        protected E data;
        protected BinarySearchTreeNode<E> parent;
        protected BinarySearchTreeNode<E> left;
        protected BinarySearchTreeNode<E> right;
        protected int height;

        public BinarySearchTreeNode(E data) {
            this.data = data;
            this.parent = null;
            this.left = null;
            this.right = null;
        }

        public BinarySearchTreeNode(E data, BinarySearchTreeNode<E> parent, BinarySearchTreeNode<E> left, BinarySearchTreeNode<E> right) {
            this.data = data;
            this.parent = null;
            this.left = null;
            this.right = null;
            this.height = 1;
        }


        public E getData() {
            return data;
        }

        public void setData(E data) {
            this.data = data;
        }

        public BinarySearchTreeNode<E> getParent() {
            return parent;
        }

        public void setParent(BinarySearchTreeNode<E> parent) {
            this.parent = parent;
        }

        public BinarySearchTreeNode<E> getLeft() {
            return left;
        }

        public void setLeft(BinarySearchTreeNode<E> childNode) {
            for (BinarySearchTreeNode<E> n = this; n != null; n = n.parent) {
                if (n == childNode) {
                    throw new IllegalArgumentException();
                }
            }

            if (this.left != null) {
                left.parent = null;
            }
            if (childNode != null) {
                childNode.parent = this;
            }
            this.left = childNode;
        }

        public BinarySearchTreeNode<E> getRight() {
            return right;
        }

        public void setRight(BinarySearchTreeNode<E> childNode) {
            for (BinarySearchTreeNode<E> n = this; n != null; n = n.parent) {
                if (n == childNode) {
                    throw new IllegalArgumentException();
                }
            }

            if (right != null) {
                right.parent = null;
            }
            if (childNode != null) {
                childNode.parent = this;
            }
            this.right = childNode;
        }

        public void removeFromParent() {
            if (parent != null) {
                if (parent != null) {
                    if (parent.left == this) {
                        parent.left = null;
                    } else if (parent.right == this) {
                        parent.right = null;
                    }
                    this.parent = null;
                }
            }
        }

        public int numChildren() {
            int count = 0;
            if (this.getLeft() != null)
                count++;
            if (this.getRight() != null)
                count++;
            return count;
        }

    }

    public  interface Visitor {
        <E> void visit(BinarySearchTreeNode<E> Node);
    }

    public static class BinarySearchTree<E> implements Visitor {

        protected BinarySearchTreeNode<E> root = null;

        private int size = 0;

        public BinarySearchTree() {
        }

        public int size() {
            return size;
        }

        public BinarySearchTreeNode<E> root() {
            return root;
        }

        public boolean isEmpty() {
            return size() == 0;
        }

        protected BinarySearchTreeNode<E> createNode(E e,
                                                     BinarySearchTreeNode<E> parent, BinarySearchTreeNode<E> left,
                                                     BinarySearchTreeNode<E> right) {
            return new BinarySearchTreeNode<E>(e, parent, left, right);
        }

        public BinarySearchTreeNode<E> addRoot(E e) throws IllegalStateException {
            if (!isEmpty())
                throw new IllegalStateException("Tree is not empty");
            root = createNode(e, null, null, null);
            size = 1;
            return root;
        }

        public BinarySearchTreeNode<E> addLeft(BinarySearchTreeNode<E> n, E e) {
            BinarySearchTreeNode<E> child = createNode(e, null, null, null);
            n.setLeft(child);
            size++;
            return child;
        }

        public BinarySearchTreeNode<E> addRight(BinarySearchTreeNode<E> n, E e) {
            BinarySearchTreeNode<E> child = createNode(e, null, null, null);
            n.setRight(child);
            size++;
            return child;
        }

        public boolean search(BinarySearchTreeNode<E> n, E e) {
            BinarySearchTreeNode<E> current = recursiveSearch(n, e, map);
            if (current != null)
                return true;
            else
                return false;
        }

        public BinarySearchTreeNode<E> find(BinarySearchTreeNode<E> n, E e) {
            BinarySearchTreeNode<E> current = recursiveSearch(n, e, map);
            if (current != null)
                return current;
            else
                return null;
        }

        Function<Object, Comparable> map = new Function<Object, Comparable>() {
            @Override
            public Comparable apply(Object t) {
                return (Comparable) t;
            }
        };

        public BinarySearchTreeNode<E> recursiveSearch(BinarySearchTreeNode<E> n,
                                                       E e, Function<Object, Comparable> map) {
            if (n == null) {
                return null;
            }
            if (map.apply(n.data).compareTo(e) == 0) {
                return n;
            }
            BinarySearchTreeNode<E> temp;

            if (map.apply(n.data).compareTo(e) > 0) {
                temp = recursiveSearch(n.left, e, map);
            } else
                temp = recursiveSearch(n.right, e, map);

            return temp;
        }

        public BinarySearchTreeNode<E> add(BinarySearchTreeNode<E> n, E e) {
            BinarySearchTreeNode<E> current = addRecursive(n, e, map);
            return current;
        }

        public BinarySearchTreeNode<E> addRecursive(BinarySearchTreeNode<E> n, E e,
                                                    Function<Object, Comparable> map) {

            if (map.apply(n.data).compareTo(e) == 0) {
                throw new IllegalArgumentException("Node already in the tree");
            }
            BinarySearchTreeNode<E> temp = null;

            if (map.apply(n.data).compareTo(e) > 0) {
                if (n.left == null) {
                    BinarySearchTreeNode<E> child = createNode(e, n, null, null);
                    n.setLeft(child);
                    size++;
                    return child;
                } else
                    temp = addRecursive(n.left, e, map);
            } else {
                if (n.right == null) {
                    BinarySearchTreeNode<E> child = createNode(e, n, null, null);
                    n.setRight(child);
                    size++;
                    return child;
                } else
                    temp = addRecursive(n.right, e, map);
            }
            return temp;
        }

        public void set(BinarySearchTreeNode<E> n, E e) {
            n.setData(e);
        }

        public void remove(BinarySearchTreeNode<E> n)
                throws IllegalArgumentException {
            if (n.numChildren() == 2)
                throw new IllegalArgumentException("node has two children");

            BinarySearchTreeNode<E> child = (n.getLeft() != null ? n.getLeft() : n
                    .getRight());
            if (child != null)
                child.setParent(n.getParent());
            if (n == root)
                root = child;
            else {
                BinarySearchTreeNode<E> parent = n.getParent();
                if (n == parent.getLeft())
                    parent.setLeft(child);
                else
                    parent.setRight(child);
            }
        }

        public void traversePreoder() throws IllegalStateException {
            if (isEmpty())
                throw new IllegalStateException("Tree is empty");
            traversePreoder(root);
        }

        private void traversePreoder(BinarySearchTreeNode<E> node) {
            visit(node);
            if (node.getLeft() != null)
                traversePreoder(node.getLeft());
            if (node.getRight() != null)
                traversePreoder(node.getRight());
        }

        public void traversePostoder() throws IllegalStateException {
            if (isEmpty())
                throw new IllegalStateException("Tree is empty");
            traversePostoder(root);
        }

        private void traversePostoder(BinarySearchTreeNode<E> node) {
            if (node.getLeft() != null)
                traversePreoder(node.getLeft());
            if (node.getRight() != null)
                traversePreoder(node.getRight());
            visit(node);
        }

        public void traverseInoder() throws IllegalStateException {
            if (isEmpty())
                throw new IllegalStateException("Tree is empty");
            traverseInoder(root);
        }

        private void traverseInoder(BinarySearchTreeNode<E> node) {
            if (node.getLeft() != null)
                traversePreoder(node.getLeft());
            visit(node);
            if (node.getRight() != null)
                traversePreoder(node.getRight());
        }

        @Override
        public <E> void visit(BinarySearchTreeNode<E> Node) {
            System.out.print("" + Node.getData() + " ");
        }

        public int height(BinarySearchTreeNode<E> Node) {
            int height = -1;
            if (Node == null) {
                return height;
            } else {
                return 1 + Math.max(height(Node.left), height(Node.right));
            }
        }

        public BinarySearchTreeNode<E> delete(BinarySearchTreeNode<E> Node, E e) {
            if (isEmpty())
                throw new IllegalStateException("Tree is empty");
            else {
                BinarySearchTreeNode<E> current = find(Node, e);
                if (current == null)
                    return null;
                else {
                    if (current.right == null & current.left == null) {
                        remove(current);
                    } else {
                        BinarySearchTreeNode<E> replaceSubTree = current;
                        if (current.left == null & current.right != null) {
                            replaceSubTree = replaceSubTree.right;
                        } else {
                            if (current.right == null & current.left != null) {
                                replaceSubTree = replaceSubTree.left;
                            } else {
                                replaceSubTree = replaceSubTree.left;
                                while (replaceSubTree.right != null) {
                                    replaceSubTree = replaceSubTree.right;
                                }
                            }
                        }
                        current.data = replaceSubTree.data;
                        remove(replaceSubTree);
                    }
                }
            }
            return Node;
        }

        //visualisation of the BST
        public void BFS(BinarySearchTreeNode<E> node) {
            Queue<BinarySearchTreeNode<E>> q = new LinkedList<BinarySearchTreeNode<E>>();
            if (node == null)
                return;
            q.add(node);
            int level = 1;
            int h = -2;
            String null_s = "@"; // definition of null node;
            for (int i = 0; i < Math.pow(2, height(node) + 1) - 1; i++) {
                BinarySearchTreeNode<E> n = (BinarySearchTreeNode<E>) q.remove();
                if (Math.log(level) / Math.log(2) % 1 == 0) {
                    System.out.println();
                    h++;
                    for (int j = (int) (Math.pow(2, height(node) - Math.log(level)
                            / Math.log(2))) - 1; j > 0; j--) {
                        System.out.print(" ");
                    }
                } else {
                    for (int j = 0; j < (Math.pow(2, height(node) - h)) - 1; j++)
                        System.out.print(" ");
                }
                if (n != null) {
                    System.out.print(n.data);
                    level++;
                }
                if (n.left != null)
                    q.add(n.left);
                else
                    q.add(createNode((E) null_s, null, null, null));
                if (n.right != null)
                    q.add(n.right);
                else
                    q.add(createNode((E) null_s, null, null, null));
            }
            System.out.println();
        }
    }

}