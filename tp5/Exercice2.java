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