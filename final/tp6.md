# TP6-prog-rep


## Partie 3 , Exercice 1 : 

Not yet





## Partie 3 , Exercice 2 : 

**1. Propriétés du snapshot**
------------------------- 

Parmi les propriétés suivantes, les quelles sont vraies ? (démonstration ou contre-exemple)  

**(a) Pour tout i: scan i [i] = i**  

**Réponse :** Propriété vrai. Vu que les opérations sont atomiques et que la "thread i" écrit la valeur au niveau de l'indice "i" il y'aura pas de conflit,  et aussi on a l'écriture puis la lecture sur le même indice.   

Donc si on a un scan_i[5] on aura le résultat retourné 5   

**Exemple :**  

Le tableau de mémoire partagée initial:  

|indice_0|indice_1|indice_2|indice_3|indice_4|indice_5|...|...|indice_n-1|
|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|
|-1 |-1 |-1 |-1 |-1 |... |... |-1 |-1  

La tableau après scan et update de tt les threads :

|indice_0|indice_1|indice_2|indice_3|indice_4|indice_5|...|...|indice_n-1|
|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|
|0 |1 |2 |3 |4 |5 |... |... |n-1  

**(b) Pour j ≠ i, scan j [i] = i ou scan j [i] = −1**

**Réponse :** Propriété vrai. Vu qu'ont regarde le scan et l'update de i via une thread j, avant il se peut que la thread i n'a rien écrit et donc on aura comme retour de scan_j[i] = -1 et si la thread i avait écrit un valeur avant et bien on lira cette valeur déja écrite et scan_j[i]=i  

**Exemple cas 1:**

Dans le cas ou la thread 2 n'a pas fait un update(x) et que la thread 4 a lit la valeur a la position 2, cette dernière lira -1 car la thread 2 n'a pas fait d'update    

|indice_0|indice_1|indice_2|indice_3|indice_4|...|...|indice_n-2|indice_n-1|
|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|
|0 |1 |-1 |3 |4 |... |... |n-2 |-1  

La valeur de la thread 2 est bien restée a -1   

**Exemple cas 2:**

Dans le cas ou la thread 2 a fait une update(x) et que la thread 4 a lit la valeur a la position 2, cette dernière lira x, car la thread 2 a fait l'update(x) à l'indice 2  

Tableau arpés update(x) par la thread 2  

|indice_0|indice_1|indice_2|indice_3|indice_4|...|...|indice_n-2|indice_n-1| 
|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|
|0 |1 |x |3 |4 |... |... |n-2 |-1

Quand la thread 4 lira, elle lira x (scan_j[i]=x)    

**(c) Pour j ≠ i, si scan j[i] = i alors scan i[j] = j**  

**Réponse :** Propriété fausse. Car on peut tomber dans un cas ou j < i et qu'ont a pas encore fait la mise à jour de la case j du tableau  

**Exemple :**  

Si on prend i = 2 et j = 4, et que on fait un scan_j[2] on tombe sur la valeur 2 mais quand on fera un scan_i[4] pourrai tomber sur -1 si la thread j n'a pas encore fait d'update

|indice_0|indice_1|indice_2|indice_3|indice_4|...|...|indice_n-2|indice_n-1| 
|:----:  |:----:  |:----:  |:----:  |:----:  |:----:|:----:|:----:|:----:|
|0 |1 |2 |3 |-1 |... |... |n-2 |n-1  

i pourra donc lire une autre valeur (-1) car j n'a pas encore fait d'update pour ça valeur


**(d) Pour j ≠ i, si scan j [i] = i alors scan i [j] = −1**

**Réponse :** Propriété fausse. Car on peut lire la valeur de j après son update

**Exemple :** 
Si on prend i = 2 et j = 4, et que on fait un scan_j[2] on tombe sur la valeur 2 et quand on fera un scan_i[4] pourrai tomber sur 4 car la thread j a fait une update(4)  

|indice_0|indice_1|indice_2|indice_3|indice_4|...|...|indice_n-2|indice_n-1| 
|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:| 
|0     |1     |2     |3     |4     |...   |...   |n-2   |n-1  


**(e) Pour j ≠ i, scan j [i] = i ou scan i [j] = j**

**Réponse :** Propriété vrai. Car i fait un update dans la case i et scan la case j et j fait un update de la case j et scan case i  

**Pour j ≠ i, scan j ⊆ scan i ou scan i ⊆ scan j ( la relation ⊆ est A ⊆ B si et seulement si " si A[i] ≠ −1 alors A[i] = B[i] ")**

**Réponse :** Propriété vrai. Si on a un scan j est inclu dans un scan i cela veux dire que scan i contient toutes les valeurs qui étaient dans scan j  

**2. Implémentation non-blocking**
-------------------------------- 

**1-(a) Toutes les exécutions de ce programme donnent-elles les mêmes affichages ?**

**Réponse :** Non, toutes les exécution ne donnent pas les mêmes affichage, car parfois  il y'a des threads n’ont pas le temps de finir  

Ci-dessous 3 exécution consécutive du programme  

```
scan de 0: 0 3 3 2 2 1 0 0 0 0 0 0 0 0 0   
scan de 0: 0 3 3 2 2 1 1 0 0 0 0 0 0 0 0   
scan de 0: 0 3 3 2 1 0 0 0 0 0 0 0 0 0 0  

```
Les 3 exécutions du même programme sont différentes.   

**1-(b) L’implémentation réalise-t-elle l’atomicité des opérations update et scan?**

**Réponse :** Non, l'atomicité des opérations update et scan n'est pas réalisée, car on ne peut pas avoir d’update entre la lecture de la dernière valeur du premier collect() et la lecture de la premiere valeurs du 2éme collect(). Pour que les opérations soient atomiques on aurait fait 2 collect() jusqu'à trouver 2 collect() qui
retourne 2 fois la même chose  

**1-(c) Que se passe t-il si une thread écrit 2 fois la même valeur ?

**Réponse :** Si une thread écrit 2 fois la même valeur, le résultat de scan sera la dernière valeur écrite par cette thread  

Pour l'exemple données si une thread écrit 1 puis 2 puis 1 comme suit :

```
partage.update(new Integer(1));
partage.update(new Integer(2));
partage.update(new Integer(1));
```

Et bien à la fin, on aura le résultat du scan qui est 1 (cas scan 8) :  

```
scan de 8: 1 1 1 1 1 1 1 1 0 0 0 0 0 0 0

```

**2-(a) Dans quelle cas une exécution ne termine pas?**

**Réponse :** L'exécution ne se termine pas si :  

une thread_0 lit i et obtient 3  
une thread_1 change i en 5   
une thread_2 demande un objet libre et obtient i puis elle change i en 3  
une thread _0 fait une lecture de i et obtient a  

Dans se cas la thread_0 croit qu'il n'y a pas eu de changement sur i depuis ça lecture  

(plus généralement c'est dans le cas ou on a un scan avec des updates en concurrences)  

**-** Cette implémentation assure le non-blocking  

**2-(b) Justifier le fait que cette implémentation est atomique**  

**Réponse :** Cette implémentation est atomique car on a notre double collect() et notre estampe qui vérifie si une écriture n’a pas été faite entre-temps. D'une autre manière cela veux dire au lieu de modifier la valeur d’une référence, on modifie deux valeurs -> (une référence et un numéro de version "stamp")). Même si la valeur
passe de 3 à 5, puis de nouveau à 3, les numéros de version seront différents. Ce qui assure l'atomicité.  

**-Est ce que ce serait encore le cas si la classe AtomicStampedReference<T> était remplacer par une classe**

```
class Stamped<T>{
    T reference
    int stamp;
}
```

**Réponse :** Non. Si la classe est remplacé par Stamped<T>,on aura pas même résultat car dans ce cas, on va différencier l'écriture de la référence et l'écriture de l’estampe, on peut incrément l'estampe ou changer la référence (on pourra faire d'autre oppération entre les deux).  

**2-(c) Réaliser cette implémentation du snapshot**

```java
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
```
