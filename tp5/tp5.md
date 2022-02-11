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

