import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.net.Socket;
import java.util.HashSet;
import java.util.Timer;

public class Controller {
    public static void main(String[] args) {

        int cport = Integer.parseInt(args[0]);
        int r = Integer.parseInt(args[1]);
        int timeout = Integer.parseInt(args[2]);
        int rp = Integer.parseInt(args[3]);
        Index index = new Index();
        HashSet<Integer> dstores = new HashSet<>();
        ServerSocket serverSocket = null;
        Timer timer = new Timer();

        try {
            serverSocket = new ServerSocket(cport);
        } catch (Exception e) {
            System.err.println("error :" + e);
        }

        //PrintWriter out = new PrintWriter(serverSocket.getOutputStream);


        while(true) {
            Socket socket;
            try {
                socket = serverSocket.accept();
                new Thread(new ControllerThread(socket, index, dstores)).start();
            } catch (IOException e) {
                System.err.println("error: " + e);
            }
        }

    }

}
