import java.util.*;


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

    public synchronized void setStatus(String fileName, String status) {
        this.index.get(fileName).setStatus(status);
    }
    public synchronized String getStatus(String fileName) {
        return this.index.get(fileName).getStatus();
    }

    public synchronized HashSet<Integer> getLocations(String fileName) {
        return this.index.get(fileName).getLocations();
    }

    public synchronized void addLocation(String fileName, Integer port) {
        this.index.get(fileName).addLocation(port);
    }

    public synchronized void removeLocation(String fileName, Integer port) {
        this.index.get(fileName).removeLocation(port);
        //if number of locations  now = 0 then maybe remove file idk
    }

    public synchronized boolean doesContain(String fileName) {
        return index.containsKey(fileName);
    }

    public synchronized int getFileSize(String fileName) {
        return this.index.get(fileName).getFileSize();
    }

    public synchronized int getDstoreSize(Integer port) {
        int i = 0;
        for (String s: index.keySet()) {
            if (index.get(s).getLocations().contains(port)){
                i++;
            }
        }
        return i;
    }

    public synchronized String listFiles() {
        StringBuilder files = new StringBuilder();
        for (String s:index.keySet()) {
            if(index.get(s).getStatus().equals("store complete")) {
                files.append(" ").append(s);
            }
        }
        if(files.isEmpty()) {
            return "";
        }
        //remove the leading space and return
        return files.toString().substring(1);
    }

    public synchronized int getNumFiles() {
        return index.size();
    }

}
