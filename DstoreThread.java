import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class DstoreThread implements Runnable {
    PrintWriter out;
    Socket socket;
    ArrayList<File> fileList;
    public DstoreThread(Socket clientSocket, CommQ commQ, ArrayList<File> fileList){
        this.socket = clientSocket;
        this.commQ = commQ;
        this.fileList = fileList;
    }
    BufferedReader input;
    CommQ commQ;
    BufferedInputStream binput;

    public void run() {

        OutputStream outputStream;

        try {
            System.out.println("dstore thread starting");
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            binput = new BufferedInputStream(socket.getInputStream());
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
            System.out.println("connection closed");
            System.exit(0);
            } catch (Exception e) {
            System.err.println(e);
        }

    }

    public void store(String fileName, int fileSize){
        out.println(Protocol.ACK_TOKEN);
        try {
            FileOutputStream f = new FileOutputStream(fileName);
            f.write(binput.readNBytes(fileSize));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        commQ.add(Protocol.STORE_ACK_TOKEN + " " + fileName);
    }

    public File load(String fileName) {
        return null;
    }

    public void remove(String fileName) {

    }

    public void list() {
        StringBuilder list = new StringBuilder(Protocol.LIST_TOKEN);
        for (File file:fileList) {
            list.append(" ").append(file.getName());
        }
        out.println(list);
    };

    public void rebalance(String filesToSend, String filesToRemove) {};

    public void rebalanceStore(String fileName, int fileSize) {

    }



}