import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

public class Data {
    private int status;
    private HashSet<Socket> locations;

    public Data(int status, Socket location) {
        this.status = status;
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

    public HashSet<Socket> getLocations() {
        return locations;
    }

    public void addLocation(Socket socket) {
        locations.add(socket);
    }

    public void removeLocation(Socket socket) {
        locations.remove(socket);
    }
}
