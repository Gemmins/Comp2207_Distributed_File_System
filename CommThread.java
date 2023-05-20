import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class CommThread implements Runnable{
    CommQ commQ;
    Socket socket;
    ArrayList<File> file;

    public CommThread (Socket socket, CommQ commQ, ArrayList<File> files) {
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
        while(!socket.isClosed()) {
            if(commQ.isEmpty()) {
                continue;
            } else {
                out.println(commQ.poll());
                out.flush();
            }
        }
    }
}
