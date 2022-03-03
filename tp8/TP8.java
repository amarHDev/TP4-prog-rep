import java.util.concurrent.locks.*;
public class TP8 {

    public interface Set {
        boolean add(Integer x);
        boolean remove(Integer x);
        boolean contains(Integer x);
        void print();
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

    public static class MonSet implements Set{
        private Node head;
        private Lock lock = new ReentrantLock();

        public MonSet() {
            head = new Node(Integer.MIN_VALUE);
            head.next = new Node(Integer.MAX_VALUE);
        }

        public void print(){
            Node node = head;
            String str = "_";
            for(int i = 0; i < ThreadID.get(); i++){
                str += str + str;
            }
            while(node != null){
                System.out.println(str+ThreadID.get() + " item:"+ node.item + " key:"+ node.key);
                node = node.next;
            }
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

    public static class Super extends Thread{
        public Set set;

        public Super(Set set){
            this.set = set;
        }

        public void run(){
            int i = ThreadID.get();
            int[] values = {i, i * 2, i + 4, i + 3};
            for(int value : values){
                set.add(value);
            }
            System.out.println(i + " has? " + (i+5) +" " + set.contains(i+5));
            System.out.println(i + " remove? " + (i+4) +" " + set.remove(i+4));
            set.print();
        }
    }

    public static void main(String[] args) {
    
        MonSet set = new MonSet();

        Thread threads[] = new Thread[3];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Super(set);
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            try{
                threads[i].join();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

       set.print();
    }
}