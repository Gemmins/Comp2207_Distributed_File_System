import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

public class ControllerThread implements Runnable {

    Socket socket;
    Index index;
    HashSet<Integer> dstores;
    PrintWriter out;
    BufferedReader in;
    public ControllerThread(Socket clientSocket, Index index, HashSet<Integer> dstores){
        this.socket = clientSocket;
        this.index = index;
        this.dstores = dstores;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            System.err.println("error: " + e);
        }
    }

    public void run() {

        try {
            String line;
            while((line = in.readLine()) != null) {
                System.out.println(line + " received");
                String[] sentence = line.split(" ");
                switch (sentence[0]) {
                    case Protocol.JOIN_TOKEN -> rebalance(Integer.parseInt(sentence[1]));
                    case Protocol.STORE_TOKEN -> store(sentence[1], Integer.parseInt(sentence[2]));
                    case Protocol.LOAD_TOKEN -> load(sentence[1]);
                    case Protocol.REMOVE_TOKEN -> remove(sentence[1]);
                    default -> {
                        continue;
                    }
                }
            }
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void rebalance(int port) {
        String[] list;

        dstores.add(port);
        System.out.println("Added port: " + port);
        System.out.println(dstores);

        out.println(Protocol.LIST_TOKEN);

        try {
            list = in.readLine().split(" ");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (String file:list) {
            if(index.doesContain(file)) {
                index.addLocation(file, socket);
            } else {
                index.addFile(file, new Data(-1, socket));
            }
            System.out.println(file);
        }

    }

    public void store(String fileName, int fileSize) {

    }

    public void load(String fileName) {

    }

    public void remove(String fileName) {

    }


}
