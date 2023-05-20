import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

public class Data {
    private int status;
    private HashSet<Integer> locations;

    private int fileSize;

    public Data(int status, int location, int fileSize) {
        this.status = status;
        this.fileSize = fileSize;
        locations = new HashSet<>();
        locations.add(location);
    }

    public int getStatus() {
        return status;
    }

    //public String getFilename() {
    //    return filename;
    //}

    public void setStatus(int status) {
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
