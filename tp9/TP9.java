import java.util.concurrent.atomic.AtomicMarkableReference;

public class TP9 {
    public interface Set{
        public boolean add(Integer x);
        public boolean remove(Integer x);
        public boolean contains(Integer x);
    }

    public static class Node {
        public Integer item;
        public int key;
        public AtomicMarkableReference<Node> next;
    }

    public static record Window(Node pred, Node curr) {
        public static Window find(Node head, Integer key){
            Node pred = head;
            while(pred != null){
                if(pred.key == key){
                    return new Window(pred, pred.next.getReference());
                }
                pred = pred.next.getReference();
            }
            return new Window(null, null);
        }
    }

    public static class MySet implements Set{

        private Node head;

        public boolean add(Integer key){
            return true;
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
            return true;
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
    }
}
