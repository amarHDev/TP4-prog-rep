public class PileSync {
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