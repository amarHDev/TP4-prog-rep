import java.util.concurrent.locks.*;
public class TP8 {

    public interface Set {
        boolean add(Integer x);
        boolean remove(Integer x);
        boolean contains(Integer x);
    }

    public static class Node {
        public Integer item;
        public int key;
        public Node next;

        public Node(Integer i) {
            item = i;
            key = i;
        }
    }

    public static class NodeLock extends Node {
        public ReentrantLock lock;
        public NodeLock(Integer i) {
            super(i);
        }
    }

    public class MonSet {
        private Node head;
        private Lock lock = new ReentrantLock();

        public MonSet() {
            head = new Node(Integer.MIN_VALUE);
            head.next = new Node(Integer.MAX_VALUE);
        }

        public boolean add(Integer item) {
            Node pred, curr;
            int key = item;
            lock.lock();
            try {
                pred = head;
                curr = pred.next;
                while (curr.key < key) {
                    pred = curr;
                    curr = curr.next;
                }
                if (key == curr.key) {
                    return false;
                } else {
                    Node node = new Node(item);
                    node.next = curr;
                    pred.next = node;
                    return true;
                }
            } finally {
                lock.unlock();
            }
        }

        public boolean contains(Integer item) {
            Node pred, curr;
            int key = item;
            lock.lock();
            try {
                pred = head;
                curr = pred.next;
                while (curr.key < key) {
                    pred = curr;
                    curr = curr.next;
                }
                if (key == curr.key) {
                    return true;
                } else {
                    return false;
                }
            } finally {
                lock.unlock();
            }
        }

        public boolean remove(Integer item) {
            Node pred, curr;
            int key = item;
            lock.lock();
            try {
                pred = head;
                curr = pred.next;
                while (curr.key < key) {
                    pred = curr;
                    curr = curr.next;
                }
                if (key == curr.key) {
                    pred.next = curr.next;
                    return true;
                } else {
                    return false;
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {

    }
}