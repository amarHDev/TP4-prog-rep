import java.util.Arrays;

public class TP6 {
    public interface Snapshot<T> {
        public void update(T v);
        public T[] scan();
    }

    public static class Stamped<T>{
        private T reference;
        private int stamp;

        public Stamped(T reference, int stamp){
            set(reference, stamp);
        }

        public void set(T reference, int stamp){
            this.reference = reference;
            this.stamp = stamp;
        }

        public int getStamp(){
            return this.stamp;
        }

        public T getReference(){
            return this.reference;
        }
    }

    public static class SimpleSnap<T> implements Snapshot<T> {
        private Stamped<T>[] a_table;

        public SimpleSnap(int capacity, T init) {
            a_table =  new Stamped[capacity];
            for (int i = 0; i < capacity; i++) a_table[i] = new Stamped<>(init, 0);
        }

        public void update(T v) {
            int me = ThreadID.get();
            int st = a_table[me].getStamp();
            a_table[me].set(v, st + 1);
        }

        private T[] collect() {
            T[] copy = (T[]) new Object[a_table.length];
            for (int j = 0; j < a_table.length; j++) {
                copy[j] = (T) a_table[j].getReference();
                MyThread.yield();
            }
            return copy;
        }

        public T[] scan() {
            T[] result;
            result = collect();
            System.out.println(Arrays.equals(result, collect()));
            return result;
        }
    }

    public static class MyThread extends Thread {
        public SimpleSnap<Integer> partage;
        public int nb;

        public MyThread(SimpleSnap<Integer> partage, int nb) {
            this.partage = partage;
            this.nb = nb;
        }

        public void run() {
            partage.update(ThreadID.get());
            Object[] O = new Object[nb];
            O = partage.scan();
            System.out.print("scan de " + ThreadID.get() + ": ");
            for (int i = 0; i < nb; i++) {
                System.out.print((Integer) O[i] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        int nb = 20;
        SimpleSnap<Integer> partage = new SimpleSnap<Integer>(nb, Integer.valueOf(0));
        MyThread R[] = new MyThread[nb];
        for (int i = 0; i < nb; i++) {
            R[i] = new MyThread(partage, nb);
            R[i].start();
        }
        try {
            for (int i = 0; i < nb; i++) {
                R[i].join();
            }
        } catch (InterruptedException e) {

        }

        Object[] O = new Object[nb];
        O = partage.scan();
        System.out.print("scan de " + ThreadID.get() + ": ");
        for (int i = 0; i < nb; i++) {
            System.out.print((Integer) O[i] + " ");
        }
        System.out.println();
    }
}