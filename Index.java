import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Index {

    private HashMap<String, Data> index;
    private boolean flag = true;

    public Index() {
        this.index = new HashMap<>();
    }

    //not sure about adding flags to these methods maybe it needs to happen idk

    //more fields to add in this method
    public synchronized void addFile(String fileName, Data data) {
        //fix this
        this.index.put(fileName, data);
    }
    public synchronized void removeFile(String fileName) {
        this.index.remove(fileName);
    }

    public synchronized void setStatus(String fileName, int status) {
        this.index.get(fileName).setStatus(status);
    }
    public synchronized int getStatus(String fileName) {
        return this.index.get(fileName).getStatus();
    }

    public synchronized HashSet<Socket> getLocations(String fileName) {
        return this.index.get(fileName).getLocations();
    }

    public synchronized void addLocation(String fileName, Socket socket) {
        this.index.get(fileName).addLocation(socket);
    }

    public synchronized void removeLocation(String fileName, Socket socket) {
        this.index.get(fileName).removeLocation(socket);
        //if number of locations  now = 0 then maybe remove file idk
    }

    public synchronized boolean doesContain(String fileName) {
        return index.containsKey(fileName);
    }

}
