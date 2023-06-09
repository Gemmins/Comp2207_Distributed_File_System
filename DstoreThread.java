import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.*;

public class DstoreThread implements Runnable {
    PrintWriter out;
    Socket socket;
    ArrayList<File> fileList;
    File folder;
    static final Object folderGuard = new Object();
    boolean isController;
    int timeout;
    public DstoreThread(Socket clientSocket, CommQ commQ, ArrayList<File> fileList, File folder, boolean isController, int timeout){
        this.socket = clientSocket;
        this.commQ = commQ;
        this.fileList = fileList;
        this.folder = folder;
        this.isController = isController;
        this.timeout = timeout;
    }
    BufferedReader input;
    CommQ commQ;
    BufferedInputStream binput;

    OutputStream outputStream;
    public void run() {



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
            if(isController) {
                System.out.println("Connection with controller closed - now terminating");
                System.exit(0);
            }
            System.out.println("Connection with client at " + socket.getPort() + " closed");
            } catch (Exception e) {
            System.err.println(e);
        }

    }
    //need some way to time out this
    //cant figure it out without timing the whole write operation which causes issues
    public void store(String fileName, int fileSize){
        out.println(Protocol.ACK_TOKEN);

        try {
            FileOutputStream f = new FileOutputStream(new File(folder, fileName));
            f.write(binput.readNBytes(fileSize));
        } catch (Exception e) {
            System.err.println("error: " + e);
        }
        System.out.println(Arrays.toString(folder.listFiles()));
        commQ.add(Protocol.STORE_ACK_TOKEN + " " + fileName);

    }

    public void load(String fileName) {
        byte[] stream = new byte[0];
        for (File f: Objects.requireNonNull(folder.listFiles())) {
            if(f.getName().equals(fileName)) {
                try {
                    stream = Files.readAllBytes(f.toPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
        }
        try {
            outputStream.write(stream);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(String fileName) {
        for (File f:Objects.requireNonNull(folder.listFiles())) {
            if(f.getName().equals(fileName)) {
                f.delete();
                commQ.add(Protocol.REMOVE_ACK_TOKEN + " " + fileName);
                break;
            }
        }
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


    class TimeoutTask extends TimerTask {
        private Thread thread;
        private Timer timer;


        public TimeoutTask(Thread thread, Timer timer) {
            this.thread = thread;
            this.timer = timer;
        }

        @Override
        public void run() {
            if(thread != null && thread.isAlive()) {
                try {
                    binput.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("read timeout");
                thread.interrupt();
                timer.cancel();
            }
        }
    }



}