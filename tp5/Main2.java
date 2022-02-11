public class Main2 {
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