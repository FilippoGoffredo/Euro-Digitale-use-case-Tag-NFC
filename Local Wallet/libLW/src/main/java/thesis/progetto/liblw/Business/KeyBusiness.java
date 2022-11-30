package thesis.progetto.liblw.Business;

import thesis.progetto.liblw.DAO.IKeyDAO;
import thesis.progetto.liblw.DAO.ISandboxDAO;
import thesis.progetto.liblw.DAO.KeyDAO;
import thesis.progetto.liblw.DAO.SandboxDAO;
import thesis.progetto.liblw.Model.Key;
import thesis.progetto.liblw.Model.Sandbox;

public class KeyBusiness {
    
    // VARIABLES
    //
    private static KeyBusiness instance;

    // STATEMENTS
    //
    IKeyDAO kDao = KeyDAO.getInstance();
    ISandboxDAO sDao = SandboxDAO.getInstance();

    // lines up all methods that want to use this method
    public static synchronized KeyBusiness getInstance() {
        if (instance == null) {
            instance = new KeyBusiness();
        }
        return instance;
    }

    // CONSTRUCTORS
    //
    private KeyBusiness() {
    }
    

    // MANIPULATION DATA
    //
    //method to encapsulate key into a sandbox
    public Sandbox encapsulationKey(Key key) {
        Sandbox availableSandbox = sDao.getAvailableKeyValue();
        return kDao.insertKey(key, availableSandbox);
    }
}
