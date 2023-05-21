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
    CommQ commQ;
    int replication;
    int timeout;
    public ControllerThread(Socket clientSocket, Index index, HashSet<Integer> dstores, CommQ commQ, int timeout, int replication) {
        this.socket = clientSocket;
        this.index = index;
        this.dstores = dstores;
        this.commQ = commQ;
        this.timeout = timeout;
        this.replication = replication;
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
    //TODO rebalance/join not yet finished
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

        if (index.doesContain(fileName)) {
            out.println(Protocol.ERROR_FILE_ALREADY_EXISTS_TOKEN);
            return;
        }
        //TODO chooses to store to the first dstores that are below the maximum number of files
        ArrayList<Integer> dstoreList = new ArrayList<>();
        StringBuilder list = new StringBuilder(Protocol.STORE_TO_TOKEN);
        for (int i : dstores) {
            if (dstoreList.size() < 2) {
                dstoreList.add(i);
                list.append(" ").append(i);
            }
        }
        Integer[] arr = dstoreList.toArray(new Integer[dstoreList.size()]);
        //update index with dstores being stored to
        index.addFile(fileName, new Data("store in progress", arr, fileSize));
        //sends message to client
        System.out.println(list);
        out.println(list);

        int i = dstoreList.size();
        int j = 0;
        //loops around removing incoming store acks from the queue until timeout or until all required are removed
        long startTime = System.currentTimeMillis();
        while(System.currentTimeMillis() < (startTime + timeout) && (j < i)) {
            if (commQ.remove(Protocol.STORE_ACK_TOKEN + " " + fileName)) {
                System.out.println("removed");
                j++;
            }
        }
        System.out.println(System.currentTimeMillis());
        if(i != j) {
            index.removeFile(fileName);
            System.out.println("removing " + fileName);
            return;
        }
        System.out.println(Protocol.STORE_COMPLETE_TOKEN);
        out.println(Protocol.STORE_COMPLETE_TOKEN);
    }

    public void load(String fileName) {
        HashSet<Integer> locations = null;
        //check if file exists and status is ok
        try {
            if (index.getStatus(fileName).equals("hi")) {
                locations = index.getLocations(fileName);
            }
        } catch (Exception e) {
            //TODO deal with this
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

    public void storeAck(String fileName) {
        commQ.add(Protocol.STORE_ACK_TOKEN + " " + fileName);
        System.out.println(fileName + " added to q");
    }


    public void list() {
        out.println(Protocol.LIST_TOKEN + " " + index.listFiles());
    }

    public void execute(String line) {
        System.out.println(line + " received");
        String[] sentence = line.split(" ");
        switch (sentence[0]) {
            case Protocol.JOIN_TOKEN -> rebalance(Integer.parseInt(sentence[1]));
            case Protocol.STORE_TOKEN -> store(sentence[1], Integer.parseInt(sentence[2]));
            case Protocol.LOAD_TOKEN -> load(sentence[1]);
            case Protocol.REMOVE_TOKEN -> remove(sentence[1]);
            case Protocol.LIST_TOKEN -> list();
            case Protocol.STORE_ACK_TOKEN -> storeAck(sentence[1]);
            default -> {
                return;
            }
        }
    }


}
