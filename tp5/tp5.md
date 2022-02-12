# TP 5

## Exercice 1

1. zzz

2. Pile avec synchnonized: l'implementation est linéarisable.

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

3. Pile sans  synchnonized: l'implementation n'est pas linéarisable.

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


## Exercice 2

```java
import java.util.concurrent.atomic.*;

public class Exercice2 {

    public static class TS {

        int c = 1;

        synchronized int ts(){
            if(c == 1){
                c = 0;
                return 1;
            } else if(c == 0){
                //c = 1;
                return 0;
            }
            return -1;
        }
    }

    public static class TSB {
        AtomicBoolean c = new AtomicBoolean(true);

        boolean ts(){
            if(c.compareAndSet(true, false)){
                return true;
            } else if(c.compareAndSet(false, false)){
                return false
            }
            return c.get();
        }
    }
    public static class TSI {
        AtomicInteger c = new AtomicInteger(1);

        Integer ts(){
            if(c.compareAndSet(1, 0)){
                return 1;
            } else if(c.compareAndSet(0, 0)){
                return 0;
            }
            return c.get();
        }
    }

    public static void main(String[] a){

        TS ts = new TS();
        TSB tsb = new TSB();
        TSI tsi = new TSI();

        Runnable r = () -> {
            try{
                System.out.println(Thread.currentThread().getName() + " " +  tsi.ts());
                Thread.sleep(10);
                System.out.println(Thread.currentThread().getName() + " " +  tsi.ts());
            }catch(Exception e){
                e.printStackTrace();
            }
        };
        
        Thread[] th = new Thread[400];

        for(int i = 0; i < th.length; i++) th[i] = new Thread(r);
    
        for(int i = 0; i < th.length; i++) th[i].start();

        for(int i = 0; i < th.length; i++){
            try{
                th[i].join();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
```

Avec toutes ces objects on assure la linéarisation.

### Exercice 3

1.  Donner un exemple d’exécution de 4 threads partageant un objet compteur et faisant chacune 2 appels add() de cet objet.  

**Réponse :**  

```
Thread_0 :        add()1               add()4  
Thread_1 :               add()2              add()5  
Thread_2 : add()0                                          add()7  
Thread_3 :                      add()3              add()6    
```

2. L'implémentation est-elle linéarisable ?   

**Réponse :**  Non, cette implèmentation n'est pas linèarisable, car l'instruction c=c+1 n'est pas atomique

3. L'implémentation est-elle linéarisable (si on suppose qu’il y a moins de 100 appels à un objet compteur) ? 

    **Réponse :**  
    Not yet


### Exercice 4

1. La spécification séquentielle de l'objet  
   **Réponse :**  
   Not yet

```java
    public class MyThreadMettre extends Thread{
        public ThreadID tID;
        private boolean etat;
        private LinkedBlockingDeque<Integer> file;
        
        public boolean nonVide() {
            return etat;
        }
        
        public MyThreadMettre(LinkedBlockingDeque<Integer> file) {
            tID = new ThreadID();
            etat = true;
            this.file = file;
        }
            
        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                try {
                    System.out.println("La thread " +tID.get() + " met    " + i);
                    file.put(i);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("error"); }}
            etat = false; 
            }
    }

    public class MyThreadEnleve extends Thread {
        public ThreadID tID;
        private MyThreadMettre ajoutF;
        private LinkedBlockingDeque<Integer> file;
        
        
        public MyThreadEnleve(MyThreadMettre ajoutF,LinkedBlockingDeque<Integer> file) {
            tID = new ThreadID();
            this.ajoutF = ajoutF;
            this.file = file;
        }
        
        @SuppressWarnings("static-access")
        @Override
        public void run() {
            while (ajoutF.nonVide()) {
                try {
                    System.out.println("La thread " +tID.get() + " enlève "+ file.take());
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("Erreur");
                }
            }
            System.out.println(file);
        }
    }

    public static void main(String[] args) {
		@SuppressWarnings("rawtypes")
		//File de 10 entiers
		LinkedBlockingDeque file_treads = new LinkedBlockingDeque(10);
		
		// 2 Threads qui mettent en file
		MyThreadMettre threadMettre1 = new MyThreadMettre(file_treads);
		MyThreadMettre threadMettre2 = new MyThreadMettre(file_treads);
		threadMettre1.start();
		threadMettre2.start();
		
		// 2 Threads qui enléve de la file
		MyThreadEnleve threadanlevee1 = new MyThreadEnleve(threadMettre1,file_treads);
		MyThreadEnleve threadanlevee2 = new MyThreadEnleve(threadMettre2,file_treads);
		threadanlevee1.start();
		threadanlevee2.start();
	}
```

3. a) Montrer que quand la file est utilisée par 2 threads qui mettent et enlèvent des éléments dans lafile, cette implémentation n’est pas linéarisable.

**Réponse :**  
On peut avoir 2 Threads qui exécute la méthode mettre en concurence ce qui implique de cette implèmentation n'est pas linéarisable  

Exemple  
    
Si on a deux thread tel que :  

```
Thread_0 :     mettre(4)  
Thread_1 :       mettre(5)    enleve(4)  
```

Dans ce cas si mettre(5) s'execute avant mettre(4), enleve(4) ne pourra pas avoir lieu, car la valeur 5 a été mise la première, c'est elle qui doit être enlevée la première, or dans notre cas c'est la valeur 4 qui a été enlevée. ce qui implique que cette situation n'est pas linèarisable    


3. b) Montrer que quand la file est utilisée par 2 threads l’une qui met des elements et l’autre qui enlève des elements cette implémentation est linéarisable.  

**Réponse :**  
    Car dans cette implémentation la thread qui enlève des elèments à partir de la file est bloqué si la file est vide car on utilise pour ça LinkedBlockingDeque, ce qui fait que la Thread sera bloquée jusqu'à ce que l'autre Thread insére au moins une donnée dans la file en question pour qu'elle puisse l'enlevée. Ce qui rend cette implèmentation linéarisable


