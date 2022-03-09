import java.util.ArrayList;

public class Map {
    protected ArrayList<Location> QRCodeLocations;
    protected Player player;

    public Map(Player currentPlayer) {
        this.player = currentPlayer;
        this.QRCodeLocations = new ArrayList<Location>();
    }

    // test constructor (remove once other classes are built more)
    public Map() {
        this.QRCodeLocations = new ArrayList<Location>();
    }

    /**
     * Displays the map for the player to see
     */
    public void displayMap() {
        // WIP
    }

    /**
     * Places the locations of nearby QR codes on the map
     */
    public void placeQRLocations() {
        // WIP
    }

    /**
     * Places the location of the current player
     */
    public void placePlayerLocation() {
        // WIP
    }

}
