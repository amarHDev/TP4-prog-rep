public class AjoutThread2 extends Thread {
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