import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Data {
    private String status;
    private HashSet<Integer> locations;

    private int fileSize;

    public Data(String status, Integer[] location, int fileSize) {
        this.status = status;
        this.fileSize = fileSize;
        locations = new HashSet<>();
        locations.addAll(Arrays.asList(location));
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public HashSet<Integer> getLocations() {
        return locations;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public void addLocation(Integer port) {
        locations.add(port);
    }

    public void removeLocation(Integer port) {
        locations.remove(port);
    }
}
