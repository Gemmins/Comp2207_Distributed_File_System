import java.io.*;
import java.net.Socket;

public class DstoreThread implements Runnable {
    PrintWriter out;
    Socket socket;
    public DstoreThread(Socket clientSocket){
        this.socket = clientSocket;
    }

    public void run() {

        OutputStream outputStream;
        BufferedReader input;


        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputStream = socket.getOutputStream();
            out = new PrintWriter(outputStream, true);
            String line;
            while((line = input.readLine()) != null) {
                System.out.println(line + " received");
                String[] sentence = line.split(" ");
                switch (sentence[0]) {
                    case Protocol.STORE_TOKEN -> store(sentence[1],Integer.parseInt(sentence[2]));
                    case Protocol.LOAD_DATA_TOKEN -> load(sentence[1]);
                    case Protocol.REMOVE_TOKEN -> remove(sentence[1]);
                    case Protocol.LIST_TOKEN -> list();
                    case Protocol.REBALANCE_TOKEN -> rebalance(sentence[1], sentence[2]);
                    case Protocol.REBALANCE_STORE_TOKEN -> rebalanceStore(sentence[1], Integer.parseInt(sentence[2]));
                    default -> {
                        continue;
                    }
                }
            }
            } catch (Exception e) {
            System.err.println(e);
        }

    }

    public void store(String fileName, int fileSize){

    }

    public File load(String fileName) {
        return null;
    }

    public void remove(String fileName) {

    }

    public void list() {
        out.println("a b c d");
    };

    public void rebalance(String filesToSend, String filesToRemove) {};

    public void rebalanceStore(String fileName, int fileSize) {

    }



}