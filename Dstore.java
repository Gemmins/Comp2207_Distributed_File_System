import java.io.*;
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
        System.out.println(file_folder.getAbsolutePath());
        deleteFolder(file_folder);
        file_folder.mkdir();
        ArrayList<File> fileList = new ArrayList<>();
        CommQ commQ = new CommQ();

        //for (File file: file_folder.listFiles()) {
        //    fileList.add(file);
        //}

        //Joins Controller and starts thread to communicate
        Socket cSocket;

        try {

            InetAddress address = InetAddress.getLocalHost();
            cSocket = new Socket(address, port);
            new Thread(new CommWriterThread(cSocket, commQ, fileList)).start();
            PrintWriter out = new PrintWriter(cSocket.getOutputStream(), true);
            out.println(Protocol.JOIN_TOKEN + " " + cport);
            new Thread(new DstoreThread(cSocket, commQ, fileList, file_folder)).start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Listens for a client then starts thread to communicate
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
                new Thread(new DstoreThread(socket, commQ, fileList, file_folder)).start();
            } catch (IOException e) {
                System.err.println("error: " + e);
            }
        }

    }

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }


}
