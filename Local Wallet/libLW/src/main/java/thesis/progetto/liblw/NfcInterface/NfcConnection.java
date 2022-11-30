package thesis.progetto.liblw.NfcInterface;

public class NfcConnection implements INfcConnection {

    // VARIABLES
    //

    private static NfcConnection instance = new NfcConnection();

    // CONSTRUCTORS
    //
    private NfcConnection() {
    }

    // INSTANCE
    //
    // method to get an instance of BleConnection
    public static NfcConnection getInstance() {
        return instance;
    }

    // CHECK
    //
    // method to check if BLE is supported
    @Override
    public int checkBleSupported() {
        
        
        return 1;
    }
}