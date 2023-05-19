import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Dstore {

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        int cport = Integer.parseInt(args[1]);
        int timeout = Integer.parseInt(args[2]);
        File file_folder = new File(args[3]);
        ArrayList<File> fileList = new ArrayList<>();

        //for (File file: file_folder.listFiles()) {
        //    fileList.add(file);
        //}

        //Joins Controller and starts thread to communicate
        try {
            InetAddress address = InetAddress.getLocalHost();
            Socket socket = new Socket(address, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Protocol.JOIN_TOKEN + " " + cport);
            new Thread(new DstoreThread(socket)).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Listens for a client then starts thread to communicate
        /*
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(cport);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while(true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                new DstoreThread(socket);

            } catch (IOException e) {
                System.err.println("error: " + e);
            }
        }
        */

    }


}
