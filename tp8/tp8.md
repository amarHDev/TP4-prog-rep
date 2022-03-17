# TP8

## Exercice 1

> 1\. Cette implementation est-elle linéarisable (si oui donnez les points de linéarisation et justifiez sinon donnez un exemple)

**REPONSE:** Oui cette implémentation est Linéarisable. et le point de linéarisation c'est **la sortie de la boucle while**, car quand on sort du while on a donc trouvé qu'ont a dépassé la clé et donc l'element n'était pas dedans (c'est à dire quand curr.key > key ), dans se cas là on va retourner faux car on a bien tout parcouru et on est au bout du parcours.

> 2\. Est -elle wait-free? Non

**REPONSE:** __no__

> 3\. Ecrire une thread qui utilise cette implementation (le constructeur de la thread aura en paramètre un Set) Si cette thread a pour identité i (donne par ThreadID) elle ajoute i, 2 ∗i, i + 4 , i + 3 à l’ensemble, teste si i + 5 y appartient et enlève i + 4.

```java
public class Super extends Thread{
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
        System.out.println(i + " has? " + (i+5) + " " + set.contains(i+5));
        System.out.println(i + " remove? " + (i+4) + " " + set.remove(i+4));
        set.print();
    }
}
```

> 4\. Ecrire un programme qui lance 3 threads qui partagent un Set.

```java
public static void main(String[] args) {

    Set set = new MonSuperSet();

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
```

## Exercice 2

> 1\. Ecrire une implementation de Set en utilisant une synchronisation à grains fins.

```java
import java.util.concurrent.locks.*;
public class TP8EX2 {

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
        public ReentrantLock lock = new ReentrantLock();

        public Node(Integer i) {
            item = i;
            key = i;
        }

        public ReentrantLock getLock() {
            return lock;
        }
    }

    public static class MonSet implements Set{
        private Node head;

        public MonSet() {
            head = new Node(Integer.MIN_VALUE);
            head.next = new Node(Integer.MAX_VALUE);
        }

        public void print(){
            Node node = head;
            String str = "_";
            for(int i = 0; i < ThreadID.get(); i++){
                //str += str + str;
            }
            while(node != null){
                System.out.println(str+ThreadID.get() + " item:"+ node.item + " key:"+ node.key);
                node = node.next;
            }
        }

        public boolean add(Integer item) {
            Node pred, curr;
            int key = item;

            pred = head;
            pred.getLock().lock();
            curr = pred.next;
            try {
                while (curr.key < key) {
                    pred.getLock().unlock(); 
                    pred = curr; 
                    pred.getLock().lock();
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
                pred.getLock().unlock();
            }
        }

        public boolean contains(Integer item) {
            Node pred, curr;
            int key = item;

            pred = this.head; 
            pred.getLock().lock();
            curr = pred.next;
            try{
                while (curr.key<key){
                    pred.getLock().unlock(); 
                    pred = curr; 
                    curr = curr.next;
                    pred.getLock().lock();
                }
                if (key==curr.key) {
                    return true;
                }else {
                    return false;
                }
            } finally {
                pred.getLock().unlock();
            }
        }

        public boolean remove(Integer item) {
            Node pred, curr;
            int key = item;

            pred = this.head; 
            pred.getLock().lock();
            curr = pred.next;
            curr.getLock().lock();

            try{
                while(curr.item < key){
                    if (item == curr.item) {
                        pred.next = curr.next;
                        return true; 
                    }
                    pred.getLock().unlock(); 
                    pred = curr; 
                    curr = curr.next; 
                    curr.getLock().lock();
                }
                return false;
            }finally{
                curr.getLock().unlock();
                pred.getLock().unlock();     
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
            
            //set.print();
        }
    }

    public static void main(String[] args) {
    
        Set set = new MonSet();

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
```

> 2\. Cette implementation est-elle linéarisable (si oui donnez les points de linéarisation et justifiez sinon donnez un exemple)

**REPONSE:** Oui cette implémentation est linéarisable et les points de linéarisations sont les return qui suivent le bloc while

> 3\. Est -elle wait-free?

**REPONSE:** Oui

> 4\. Réutilisé le programme de l’exercice precedent qui lancent 3 threads et les
threads qui ajoutent et enlèvent des élèments avec cette implémentation.

**REPONSE:** Dans le point 1.