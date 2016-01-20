import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.function.Function;

/**
 * Created by Vlad1slav on 29.10.2015.
 */
public class RBT {
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        ArrayList<Integer> data_add = new ArrayList<Integer>();
        ArrayList<Integer> data_out = new ArrayList<Integer>();

        //Block of reading input data
        StringBuffer sb = null;
        Scanner in = new Scanner(new File("rbt.in"));
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
            data_out.add(i, Integer.valueOf(numbers[i]));

        //create rbt and add elements
        redBlackTree<Integer> rbt = new redBlackTree<Integer>();
        BinarySearchTreeNode<Integer> rbt_node = null;
        for (int i = 0; i < data_add.size(); i++)
            rbt_node = rbt.insert(rbt_node, data_add.get(i));


        //create output string
        BinarySearchTreeNode<Integer> temp;
        String str = "";
        for (int i = 0; i < data_out.size(); i++) {
            temp = rbt.find(rbt_node, data_out.get(i));
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
        PrintWriter writer = new PrintWriter("rbt.out", "UTF-8");
        writer.print(str);
        writer.close();

    }

    public static class redBlackTree<E> {

        private int BLACK = 0;
        private int RED = 1;

        //function to compare values of objects
        Function<Object, Comparable> map = new Function<Object, Comparable>() {
            @Override
            public Comparable apply(Object t) {
                return (Comparable) t;
            }
        };

        public BinarySearchTreeNode<E> insert(BinarySearchTreeNode<E> node, E e) {
            node = insert(node, null, e);
            node.color = BLACK;
            return recolor(node);
        }


        private BinarySearchTreeNode<E> insert(BinarySearchTreeNode<E> node, BinarySearchTreeNode<E> parent, E e) {
            if (node == null)
                return new BinarySearchTreeNode(e, parent);

            else if (map.apply(node.data).compareTo(e) > 0)
                node.left = insert(node.left, node, e);
            else
                node.right = insert(node.right, node, e);

            return node;
        }

        private BinarySearchTreeNode<E> recolor(BinarySearchTreeNode<E> node) {
            if (node.right != null) recolor(node.right);
            if (node.left != null) recolor(node.left);

            if (node.color == RED) {
                if (isParentRed(node) && isUncleRed(node)) {
                    // Case 1: Parent & Uncle -> Red
                    node.parent.color = BLACK;
                    getUncle(node).color = BLACK;

                    BinarySearchTreeNode<E> grandpa = node.parent.parent;
                    grandpa.color = RED;
                    if (grandpa.parent == null)
                        grandpa.color = BLACK;
                    else if (grandpa.parent.color == RED)
                        recolor(grandpa);
                } else {
                    if (isParentRed(node) && !isUncleRed(node)) {
                        if (map.apply(node.data).compareTo(node.parent.data) > 0) {
                            // Case 2a: Parent -> Red; Uncle -> Black (inserted -> right, parent -> left)
                            node = rotateLeft(node.parent.parent);
                            node.color = BLACK;
                            node.left.color = RED;
                            node.right.color = RED;
                        } else {
                            // Case 2a: Parent -> Red; Uncle -> Black (inserted -> left, parent -> right)
                            node = rotateRight(node.parent.parent);
                            node.color = BLACK;
                            node.left.color = RED;
                            node.right.color = RED;
                        }
                    }
                    if (isParentRed(node) && !isUncleRed(node)) {
                        if (node.parent.parent != null) {
                            if ((map.apply(node.data).compareTo(node.parent.data) > 0) && (map.apply(node.parent.data).compareTo(node.parent.parent.data) > 0)) {
                                // Case 3a: Parent -> Red; Uncle -> Black (inserted -> left, parent -> left)
                                node.parent.parent.color = RED;
                                node.parent.color = BLACK;
                                node = rotateLeft(node.parent);
                            } else if ((map.apply(node.data).compareTo(node.parent.data) < 0) && (map.apply(node.parent.data).compareTo(node.parent.parent.data) < 0)) {
                                // Case 3b: Parent -> Red; Uncle -> Black (inserted -> right, parent -> right)
                                node.parent.parent.color = RED;
                                node.parent.color = BLACK;
                                node = rotateRight(node.parent);
                            }

                        }

                    }
                }
            }

            while (node.parent != null)
                node = node.parent;
            return node;
        }

        private BinarySearchTreeNode<E> getUncle(BinarySearchTreeNode<E> node) {
            if (map.apply(node.parent.data).compareTo(node.parent.parent.data) < 0)
                return node.parent.parent.right;
            else
                return node.parent.parent.left;
        }

        private boolean isUncleRed(BinarySearchTreeNode<E> node) {
            if (node.parent == null || node.parent.parent == null)
                return false;
            else if (map.apply(node.parent.data).compareTo(node.parent.parent.data) < 0) {
                if (node.parent.parent.right != null)
                    if (node.parent.parent.right.color == RED)
                        return true;
            } else if (map.apply(node.parent.data).compareTo(node.parent.parent.data) > 0)
                if (node.parent.parent.left != null)
                    if (node.parent.parent.left.color == RED)
                        return true;
            return false;
        }

        private boolean isParentRed(BinarySearchTreeNode<E> node) {
            if (node.parent == null)
                return false;
            else if (node.parent.color == RED)
                return true;
            return false;
        }


        private BinarySearchTreeNode<E> rotateLeft(BinarySearchTreeNode<E> node) {
            BinarySearchTreeNode<E> temp = node.right;
            node.right = temp.left;
            temp.left = node;
            temp.parent = temp.left.parent;
            temp.left.parent = temp;
            if (temp.parent != null)
                if (map.apply(temp.parent.data).compareTo(temp.data) > 0)
                    temp.parent.left = temp;
                else temp.parent.right = temp;
            return temp;
        }

        private BinarySearchTreeNode<E> rotateRight1(BinarySearchTreeNode<E> node) {
            BinarySearchTreeNode<E> temp = node.left;
            node.left = temp.right;
            temp.right = node;
            temp.parent = temp.right.parent;
            temp.right.parent = temp;
            if (temp.parent != null)
                if (map.apply(temp.parent.data).compareTo(temp.data) > 0)
                    temp.parent.left = temp;
                else temp.parent.right = temp;
            return temp;
        }

        private BinarySearchTreeNode<E> rotateRight(BinarySearchTreeNode<E> node) {
            BinarySearchTreeNode<E> temp = node.left;
            node.left = temp.right;
            temp.right = node;
            temp.parent = temp.right.parent;
            temp.right.parent = temp;
            if (temp.parent != null)
                if (map.apply(temp.parent.data).compareTo(temp.data) > 0)
                    temp.parent.left = temp;
                else temp.parent.right = temp;
            return temp;
        }

        public BinarySearchTreeNode<E> find(BinarySearchTreeNode<E> n, E e) {
            BinarySearchTreeNode<E> current = recursiveSearch(n, e, map);
            if (current != null)
                return current;
            else
                return null;
        }

        public BinarySearchTreeNode<E> recursiveSearch(BinarySearchTreeNode<E> n, E e, Function<Object, Comparable> map) {
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

        //just 4 visualisation
        public int height(BinarySearchTreeNode<E> Node) {
            int height = -1;
            if (Node == null) {
                return height;
            } else {
                return 1 + Math.max(height(Node.left), height(Node.right));
            }
        }

        //visualisation of the avl tree
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
                    if (n.data == null_s)
                        System.out.print("@@");
                    else
                        System.out.print(n.data + ":" + n.color);
                    level++;
                }
                if (n.left != null)
                    q.add(n.left);
                else
                    q.add(new BinarySearchTreeNode(null_s));
                if (n.right != null)
                    q.add(n.right);
                else
                    q.add(new BinarySearchTreeNode(null_s));
            }
            System.out.println();
        }

    }

    public static class BinarySearchTreeNode<E> {

        protected E data;
        protected BinarySearchTreeNode<E> parent;
        protected BinarySearchTreeNode<E> left;
        protected BinarySearchTreeNode<E> right;
        protected int color;


        //constructor
        public BinarySearchTreeNode(E data) {
            this.data = data;
            this.parent = null;
            this.left = null;
            this.right = null;
            this.color = 1;
        }

        public BinarySearchTreeNode(E data, BinarySearchTreeNode<E> parent) {
            this.data = data;
            this.parent = parent;
            this.left = null;
            this.right = null;
            this.color = 1;
        }

        public E getData() {
            return data;
        }

        public void setData(E data) {
            this.data = data;
        }

        public int getColor() {
            return color;
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

        public void replaceWith(BinarySearchTreeNode<E> replacement) {
            if (parent == null) return;
            if (this == parent.left) parent.setLeft(replacement);
            else parent.setRight(replacement);
        }

    }
}
