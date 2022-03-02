# TP7

> 1\. Est-ce que le scan et le update terminent toujours?   
----------

**Réponse :** Oui. Le scan et le update terminent toujours (on fait 2 * n lectures)  

> Cette implémentation n’est pas atomique, donnez un contre exemple !  

**Contre exemple :**  Les Threads vont faire chaqu'une un update, elle vont commencer par faire un scan qui n'aura que les valeurs initiales et l'écrivent dans le tableau.  
Par exemple la thread 3 commence est fait un scan et elle fait un premier collect, la thread 1 compte à elle fait un update puis la thread 3 fait son second collect et retourne la valeur du scan de la thread 1, cette dernière ne sera pas correct car de nombreux updates on était fait et il faut qu'il soit là au niveau du scan.  

> 2\. Même question si on prend celle dans le deuxième collect ! 
------------

**Réponse :** Même idée sauf que l'éciture de l'update 2 qui correspond au collect 2, va se trouvé au milieu du collect 1 et collect 2, mais le scan correspondant à cet update peut se trouvé très loin en arrière et donc si un scan se trouve avant l'update d'une autre thread alors on écrira d'ancienne valeurs sans prendre en compte les autres updates.  

> 3\.Combien fait-on au plus de collect pour réaliser un scan ?  
---------

**Réponse :** Pour réaliser un scan il faut 3 collect !  

Est-ce que le scan et le update terminent toujours ?  

**Réponse :** Oui le scan et l’update terminent toujours !  

Est-ce que l’implémentation obtenue est atomique (faites une preuve ou donnez un contre-exemple)?  

**Réponse :** Oui. L'implémentation est atomique  

**Preuve :** L'écriture de l’update 3 correspondant au collect 3 se trouve entre le collect2 et collect3, par contre son scan se trouve entre l'écriture de l’update 2 et l'écriture de l'autre update et donc on peut adopter cette valeur de scan.    

> 4\.Réalisez cette implémentation atomique wait-free. 
```java
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicStampedReference;

public class TP7 {
    public static boolean debug = true;

    public static void dd(String s) {
        if (debug) System.out.println(ThreadID.get() + " " + s);
    }

    public static interface Snapshot<T> {
        public void update(T v);

        public T[] scan();
    }

    public static class Elem<T> {
        public T value;
        public T[] snap;

        public Elem(T value) {
            this.value = value;
            snap = null;
        }

        public Elem(T value, T[] snap) {
            this.value = value;
            this.snap = snap;
        }
    }

    public static class WaitFreeSnap<T> implements Snapshot<T> {
        private AtomicStampedReference<Elem<T>>[] a_table;

        public WaitFreeSnap(int capacity, T init) {
            a_table = new AtomicStampedReference[capacity];
            T[] initsnap = (T[]) new Object[capacity];
            for (int i = 0; i < capacity; i++) {
                initsnap[i] = init;
            }
            for (int i = 0; i < capacity; i++) {
                Elem e = new Elem(init, initsnap);
                a_table[i] = new AtomicStampedReference<Elem<T>>(e, 0);
            }
        }

        public void update(T w) {
            int me = ThreadID.get();
            int st = a_table[me].getStamp();
            T[] lscan = scan();
            Elem<T> v = new Elem<T>(w, lscan);
            a_table[me].set(v, st + 1);
        }

        public T[] scan() {
            return scan_atom();
        }

        public AtomicStampedReference<Elem<T>>[] collect() {
            final AtomicStampedReference<Elem<T>>[] asr = new AtomicStampedReference[a_table.length];
            for (int i = 0; i < a_table.length; i++) {
                asr[i] = a_table[i];
            }
            return asr;
        }

        public static <T> T[] fromASRToArray(AtomicStampedReference<Elem<T>>[] array) {
            T[] collect = (T[]) new Object[array.length];
            for (int i = 0; i < array.length; i++) {
                collect[i] = array[i].getReference().value;
            }
            return collect;
        }

        public T[] scan_non_atom() {
            AtomicStampedReference<Elem<T>>[] collect1 = collect();
            AtomicStampedReference<Elem<T>>[] collect2 = collect();

            dd("arr1" + Arrays.toString(fromASRToArray(collect1)));
            dd("arr2" + Arrays.toString(fromASRToArray(collect2)));

            for (int i = 0; i < a_table.length; i++) {
                if (collect1[i].getStamp() != collect2[i].getStamp()) {
                    //if(collect1[i].getReference().value != collect2[i].getReference().value){
                    dd("scan_non_atom_diff " + i);
                    return collect1[i].getReference().snap;
                }
            }

            dd("scan_non_atom_end");
            return fromASRToArray(collect1);
        }

        public T[] scan_atom() {
            int a = 0;
            while (true) {
                a++;
                dd("passage " + a);

                int same = 0;
                final boolean[] moved = new boolean[a_table.length];
                final AtomicStampedReference<Elem<T>>[] collect1 = collect();
                try{
                    Thread.sleep(100);
                }catch (Exception e){}
                final AtomicStampedReference<Elem<T>>[] collect2 = collect();

                for (int i = 0; i < collect1.length; i++) {
                    moved[i] = collect1[i].getStamp() == collect2[i].getStamp();
                    dd("stamp " + i + " " + collect1[i].getStamp() + " " + collect2[i].getStamp());
                    if(moved[i]){
                        same++;
                    }
                }

                if (same == collect1.length){
                    dd("same");
                    dd("bool" + Arrays.toString(moved));
                    dd("arr1" + Arrays.toString(fromASRToArray(collect1)));
                    dd("arr2" + Arrays.toString(fromASRToArray(collect2)));
                    return fromASRToArray(collect1);
                }else{
                    dd("not-same");
                    for (int i = 0; i < moved.length - 3; i++) {
                        dd("moved" + Arrays.toString(moved));
                        if(!moved[i] && !moved[i+1] && !moved[i+2]){
                            dd("index found 3same " + (i+2));
                            dd("arr1" + Arrays.toString(fromASRToArray(collect1)));
                            dd("arr2" + Arrays.toString(fromASRToArray(collect2)));
                            return collect1[i+2].getReference().snap;
                        }
                    }
                }
            }
        }
    }

    public static void main(String a[]) {
        int capacity = 100;
        WaitFreeSnap<Integer> wfs = new WaitFreeSnap<Integer>(capacity, Integer.valueOf(-1));

        Thread[] threads = new Thread[capacity];
        for (int i = 0; i < capacity; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    wfs.update(ThreadID.get());
                    wfs.update(ThreadID.get() + (capacity * 10));
                    wfs.update(ThreadID.get() + (capacity * 100));
                }
                //dd(ThreadID.get()+ " " + Arrays.toString(wfs.scan()));
                //wfs.update(ThreadID.get() + 100);
            });
            threads[i].start();
        }

        for (int i = 0; i < capacity; i++) {
            try {
                threads[i].join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
```