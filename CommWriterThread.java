import java.io.File;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class CommWriterThread implements Runnable{
    CommQ commQ;
    Socket socket;
    ArrayList<File> file;

    public CommWriterThread(Socket socket, CommQ commQ, ArrayList<File> files) {
        this.commQ = commQ;
        this.socket = socket;
        this.file = files;
    }

    @Override
    public void run() {
        PrintWriter out = null;

        try {
            out = new PrintWriter(socket.getOutputStream());
        } catch (Exception e) {
            System.err.println("error: " + e);
        }
        //loops through queue of messages to tell controller and sends them
        while(true) {
            if(commQ.isEmpty()) {
                continue;
            } else {
                System.out.println(commQ.get());
                out.println(commQ.poll());
                out.flush();
            }
        }
    }
}
