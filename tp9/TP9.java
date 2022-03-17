import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;

public class TP9 {
    public interface Set{
        public boolean add(Integer x);
        public boolean remove(Integer x);
        public boolean contains(Integer x);
        public void print();
    }

    public static class Node {
        public Integer item;
        public int key;
        public AtomicMarkableReference<Node> next;

        public Node(Integer i) {
            item = i;
            key = i;
        }
    }

    public static record Window(Node pred, Node curr) {
        public static Window find(Node head, Integer key){
            Node pred = null;
            Node curr = null;
            Node succ = null;

            boolean[] marked = {false};
            boolean snip;

            retry: while(true){
                pred = head;
                curr = pred.next.getReference();
                while(true){

                    if(curr.next == null){
                        return new Window(pred, curr);
                    }
                    
                    succ = curr.next.get(marked);

                    while(marked[0]){
                        snip = pred.next.compareAndSet(curr, succ, false, false);
                        if(!snip){
                            continue retry;
                        }
                        curr = succ;
                        succ = curr.next.get(marked);
                    }
                    if(curr != null && curr.key >= key){
                        return new Window(pred, curr); 
                    }
                    pred = curr;
                    curr = succ;
                }
            }
        }
    }

    public static class MySet implements Set{

        public Node head;

        public MySet(){
            head = new Node(Integer.MIN_VALUE);
            head.next = new AtomicMarkableReference<TP9.Node>(new Node(Integer.MAX_VALUE), false);
        }

        public boolean add(Integer key){
            while(true){
                Window window = Window.find(head, key);
                Node pred = window.pred;
                Node curr = window.curr;
                if(curr != null && curr.key == key){
                    return false;
                }else{
                    Node node = new Node(key);
                    node.next = new AtomicMarkableReference<TP9.Node>(curr, false);
                    if(pred != null && pred.next != null && pred.next.compareAndSet(curr, node, false, false)){
                        return true;
                    }
                }
            }
        }
        public boolean remove(Integer key){

            while(true){
                Window window = Window.find(head, key);
                Node pred = window.pred;
                Node curr = window.curr;
                if(curr != null && curr.key != key){
                    return false;
                }else{
                    Node succ = curr.next.getReference();

                    if(!curr.next.attemptMark(succ, true)){
                        continue;
                    }

                    pred.next.compareAndSet(curr, succ, false, false);
                    return true;
                }
            }
        }
        
        public boolean contains(Integer key){
            boolean[] marked = {false};
            Node curr = this.head;

            while(curr.key < key){
                curr = curr.next.getReference();
            }

            if(curr.next != null){
                curr.next.get(marked);
            }

            return (curr.key == key && !marked[0]);
        }

        public void print(){
            Node node = head;
            String str = "_";
            for(int i = 0; i < ThreadID.get(); i++){
                //str += str + str;
            }
            while(true){
                System.out.println(str+ThreadID.get() + " item:"+ node.item + " key:"+ node.key);
                if(node.next != null){
                    node = node.next.getReference();
                }else{
                    break;
                }
            }
        }
    }

    public static class Super extends Thread{
        public Set set;

        public Super(Set set){
            this.set = set;
        }

        public void run(){
            int i = ThreadID.get() * 100;
            int[] values = {i, i * 2, i + 4, i + 3};
            for(int value : values){
                System.out.println("add" + value + " :" + set.add(value));
            }
            System.out.println(i + " has? " + (i+5) +" " + set.contains(i+5));
            System.out.println(i + " remove? " + (i+4) +" " + set.remove(i+4));
        }
    }


    public static void main(String[] args) {
        Set set = new MySet();

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
