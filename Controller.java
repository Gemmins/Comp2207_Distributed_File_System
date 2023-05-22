import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;

public class Controller {
    public static void main(String[] args) {

        int cport = Integer.parseInt(args[0]);
        int replication = Integer.parseInt(args[1]);
        int timeout = Integer.parseInt(args[2]);
        int rebalance = Integer.parseInt(args[3]);
        Index index = new Index();
        //TODO need to synchronise access to this set
        HashSet<Integer> dstores = new HashSet<>();
        HashMap<Integer, Socket> dstoress= new HashMap<>();
        ServerSocket serverSocket = null;
        CommQ commQ = new CommQ();

        try {
            serverSocket = new ServerSocket(cport);
        } catch (Exception e) {
            System.err.println("error :" + e);
        }


        while(true) {
            Socket socket;
            try {
                socket = serverSocket.accept();
                new Thread(new ControllerThread(socket, index, dstores, commQ, timeout, replication, dstoress)).start();
            } catch (IOException e) {
                System.err.println("error: " + e);
            }
        }

    }

}
