import java.util.ArrayDeque;

public class CommQ {
    ArrayDeque<String> commQ;

    public CommQ() {
        this.commQ = new ArrayDeque<>();
    }

    public synchronized String poll() {
        return commQ.pollFirst();
    }

    public synchronized void add(String string) {
        commQ.add(string);
    }

    public synchronized boolean isEmpty() {
        return commQ.isEmpty();
    }

}
