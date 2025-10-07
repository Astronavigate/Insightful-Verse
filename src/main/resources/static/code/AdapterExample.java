// Two-Phase Socket Connector
interface TwoElectricOutlet {
    void liveWire();
    void neutralWire();
}

// Three-Phase Socket Connector
interface ThreeElectricOutlet {
    void liveWire();
    void neutralWire();
    void groundWire();
}

// TV class, realizing two-phase socket interface
class TV implements TwoElectricOutlet {
    public void liveWire() {
        System.out.println("Live wire connected");
    }
    public void neutralWire() {
        System.out.println("Neutral wire connected");
    }
}

// Washing machine class, realizing three-phase socket interface
class WashingMachine implements ThreeElectricOutlet {
    public void liveWire() {
        System.out.println("Live wire connected");
    }
    public void neutralWire() {
        System.out.println("Neutral wire connected");
    }
    public void groundWire() {
        System.out.println("Ground wire connected");
    }
}

// Adapter class to convert three-phase sockets to two-phase sockets
class TwoToThreeAdapter implements ThreeElectricOutlet {
    TwoElectricOutlet twoElectricOutlet;

    public TwoToThreeAdapter(TwoElectricOutlet twoElectricOutlet) {
        this.twoElectricOutlet = twoElectricOutlet;
    }

    public void liveWire() {
        twoElectricOutlet.liveWire();
    }

    public void neutralWire() {
        twoElectricOutlet.neutralWire();
    }

    public void groundWire() {
        // Leave empty as ground wire is not needed for two-electric-outlet devices.
    }
}

public class Main {
    // Connecting two-phase sockets to appliances
    public static void ConnectElectricOutlet(TwoElectricOutlet outlet) {
        System.out.println("Connecting adapter electric outlet...");
        outlet.liveWire();
        outlet.neutralWire();
        System.out.println("Electric outlet connected.");
    }

    // Connecting three-phase sockets to appliances
    public static void ConnectElectricOutlet(ThreeElectricOutlet outlet) {
        System.out.println("Connecting three electric outlet...");
        outlet.liveWire();
        outlet.neutralWire();
        outlet.groundWire();
        System.out.println("Electric outlet connected.");
    }
    
    public static void main(String[] args) {
        // Connecting the TV to a three-phase socket using an adapter
        System.out.println("TV will connect");
        ConnectElectricOutlet(new TwoToThreeAdapter(new TV()));

        // Direct use of three-phase sockets for washing machines
        System.out.println("Washing Machine will connect");
        ConnectElectricOutlet(new WashingMachine());
    }
}
