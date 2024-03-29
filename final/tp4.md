# TP 4

## Exercice 1

-  Oui, cette histoire peut être linéarisable 

si le processus 1 écrit x puis y:   
```txt
p1 p.enq(x)
p1 p.enq(y)
```
    

puis le processus 3 lit à partir de la file, il lira x
```txt
p3 p.deq()
p3 p: x
```


puis le processus 2 lit à partir de la file, il  lira y
```txt
p2 p.deq()
p2 p: y
```
    

Cela respect bien la specification séquentielle, on a mit x le premier puis y sur la file, et on dépiler x puis y, , La relation d'arrive avant est également préservée :

p.enq(x) est bien p.deq(x)  
p.enq(y) est bien p.deq(y) 

- Oui, p2 et p3 peuvent obtenir une autre réponse car comme les processus sont concurents, on peut les mettres dans n'importe quel orde, on peut donc exécuter

```txt
p1 p.enq(x)
p1 p.enq(y)
```


et ensuite executer P2 au lieu de P3, ce qui pousse P2 à dépiler avant P3, et  lorsque P2 va dépiler, il enlèvera x et lorsque P3 va dépiler il enlèvera y

Avec cette execution P3 et P3 obtiennent d'autres valeurs possible.

## Exercice 2

> 1\. En vous inspirant de la définition classique d’une pile donné la définition et la spécification séquentielle de cet objet.

```txt
{p = P}    empiler(element x) {p = P.x}  
{p = P.x}  depiler() { p = P ; return x;}     
{p = vide] depiler() {return Exception}  
```

**RESPONSE**

> 2\. Cette implémentation réalise-t-elle une implémentation linéarisable d’une pile ? Si oui indiquer les points de linéarisation.

La pile avec ```synchnonized``` est une implementation est linéarisable.

Les point de linéarisations font référence au moment ou les opérations font réellement effets, donc notre cas il y'a trois points de linéarisations, à savoir :  
- Lors de l'ajout d'elements à la pile : cela prend effet avec ```t[sommet]=j```  
- Lorsqu'ont enlève un element à la pile : cela prend effet :    
    - soit lorsqu'ont fait un ```return -1``` si la pile est vide  
    - soit lorsqu'on décrémente le sommet ```sommet=sommet-1```

```txt
Thread 0 retire 210
Thread 1 retire 110
Thread 2 retire 10
Thread 2 retire 9
Thread 0 retire 209
Thread 1 retire 109
Thread 2 retire 208
Thread 1 retire 108
Thread 0 retire 8
Thread 2 retire 7
Thread 1 retire 107
Thread 0 retire 207
Thread 2 retire 206
Thread 0 retire 106
Thread 1 retire 6
Thread 2 retire 5
Thread 0 retire 105
Thread 1 retire 205
Thread 0 retire 204
Thread 2 retire 4
Thread 1 retire 104
Thread 0 retire 203
Thread 2 retire 3
Thread 1 retire 103
Thread 0 retire 102
Thread 1 retire 202
Thread 2 retire 2
Thread 1 retire 201
Thread 0 retire 101
Thread 2 retire 1
Rest:-1
Rest:-1
Rest:-1
```


> 3\. Cette implémentation réalise-t-elle une implémentation linéarisable d’une pile ? Si oui indiquer les points de linéarisation.

La pile n'est plus linéarizable sans ```synchronized```.

```txt
Thread 1 retire 10
Thread 0 retire 210
Thread 2 retire 110
Thread 2 retire 9
Thread 0 retire 209
Thread 1 retire 109
Thread 1 retire 108
Thread 0 retire 208
Thread 2 retire 8
Thread 0 retire 7
Thread 1 retire 107
Thread 2 retire 207
Thread 2 retire 206
Thread 1 retire 106
Thread 0 retire 6
Thread 0 retire 5
Thread 2 retire 105
Thread 1 retire 105
Thread 0 retire 205
Thread 2 retire 205
Thread 1 retire 4
Thread 0 retire 204
Thread 2 retire 104
Thread 1 retire 103
Thread 2 retire 203
Thread 0 retire 3
Thread 1 retire 203
Thread 0 retire 202
Thread 1 retire 102
Thread 2 retire 2
Rest:2
Rest:2
Rest:2
```

Le code java:
```java
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

public class TP4 {
    public static class PileSync {
        int t[];
        int sommet = -1;
    
        PileSync(int n) {
            t = new int[n];
        }
    
        synchronized void empiler(int j) {
            sommet = sommet + 1;
            t[sommet] = j;
        }
    
        synchronized int depiler() {
            if (sommet == -1) return -1;
            sommet = sommet - 1;
            return t[sommet + 1];
        }
    }

    public static class AjoutThread2 extends Thread {
        public PileSync p;
        int id;
    
        public AjoutThread2(PileSync p, int id) {
            this.p = p;
            this.id = id;
        }
    
        public void run() {
            for (int i = 1; i < 11; i++) {
                int c = i + 100 * id;
                System.out.println("Thread " + id + " empile " + c);
                p.empiler(c);
                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    System.out.println("probleme");
                }
                this.yield();
            }
    
            for (int i = 1; i < 11; i++) {
                System.out.println("Thread " + id + " retire " + p.depiler());
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    System.out.println("probleme");
                }
                this.yield();
            }
    
            System.out.println("Rest:" + p.sommet);
        }
    }
    
    public static void main(String[] v) {
        Thread[] Th = new Thread[3];
        PileSync f = new PileSync(100);
        for (int i = 0; i < 3; i++) {
            Th[i] = new AjoutThread2(f, i);
            Th[i].start();
        }
        for (int i = 0; i < 3; i++) {
            try {
                Th[i].join();
            } catch (Exception e) {
                System.out.println("probleme");
            }
        }
    }
}
```

> a) Il n’y a pas de concurrence entre les appels des threads

**Réponse:** s'il n'y pas de de concurrence, tout va bien. Mais il n'est plus question de linéarization, une seule thread est en train d'acceder à la pile. 

> b) Quand il y a concurrence entre deux threads, c’est 2 threads qui appelle emplier

**Réponse:** ce cas de figure n'est pas linéarizable car ```sommet = sommet + 1;``` devrait être atomique. ```Le Thread.sleep(10)``` rend la réponse difficile, puis qu'on n'a pas de garantie réelle que la durée sera respecté.

> c) Quand il y a concurrence entre deux threads, c’est 2 threads qui appelle dépiler

**Réponse:** ce cas de figure n'est pas linéarizable car ```sommet = sommet - 1;``` devrait être atomique.

> d) Quand il y a concurrence entre deux threads, c’est une thread qui appelle emplier et l’autre dépiler

**Réponse:** ce cas de figure n'est pas linéarizable car ```sommet = sommet - 1;``` et ```sommet = sommet + 1;``` devrait être atomique.