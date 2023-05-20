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
    public ControllerThread(Socket clientSocket, Index index, HashSet<Integer> dstores) {
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
                execute(line);
            }
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    //not yet finished
    public void rebalance(int port) {
        String[] list;

        dstores.add(port);
        System.out.println("Added port: " + port);
        System.out.println(dstores);

        out.println(Protocol.LIST_TOKEN);
        System.out.println("Sending " + Protocol.LIST_TOKEN);

        try {
            String s = in.readLine();//.split(" ");
            System.out.println("Receiving " + s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void store(String fileName, int fileSize) {

    }

    public void load(String fileName) {
        HashSet<Integer> locations = null;
        //check if file exists and status is ok
        try {
            if (index.getStatus(fileName) == 0) {
                locations = index.getLocations(fileName);
            }
        } catch (Exception e) {
            //deal with this
            System.err.println("error: " + e);
            return;
        }

        //will send first location, if reload is received then loop to next location if not then execute next line then return to loop
        for (Integer s:locations) {
            String line;
            out.println(Protocol.LOAD_FROM_TOKEN + " " + s + " " + index.getFileSize(fileName));
            try {
                line = in.readLine();
                if (line.equals(Protocol.RELOAD_TOKEN)) {
                    System.out.println(line + " received");
                    continue;
                } else if (line == null) {
                    return;
                } else {
                    execute(line);
                    return;
                }
            } catch (Exception e) {
                System.err.println("error: " + e);
                return;
            }

        }
        //if no dstores work
        out.println(Protocol.ERROR_LOAD_TOKEN);

    }

    public void remove(String fileName) {

    }

    public void execute(String line) {
        System.out.println(line + " received");
        String[] sentence = line.split(" ");
        switch (sentence[0]) {
            case Protocol.JOIN_TOKEN -> rebalance(Integer.parseInt(sentence[1]));
            case Protocol.STORE_TOKEN -> store(sentence[1], Integer.parseInt(sentence[2]));
            case Protocol.LOAD_TOKEN -> load(sentence[1]);
            case Protocol.REMOVE_TOKEN -> remove(sentence[1]);
            default -> {
                return;
            }
        }
    }


}
