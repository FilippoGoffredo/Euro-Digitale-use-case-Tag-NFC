package thesis.progetto.liblw.Business;

import thesis.progetto.liblw.DAO.ISandboxDAO;
import thesis.progetto.liblw.DAO.SandboxDAO;

public class SandboxBusiness {
    
    // VARIABLES
    //
    private static SandboxBusiness instance;

    // STATEMENTS
    //
    ISandboxDAO sDao = SandboxDAO.getInstance();

    // lines up all methods that want to use this method
    public static synchronized SandboxBusiness getInstance() {
        if (instance == null) {
            instance = new SandboxBusiness();
        }
        return instance;
    }

    // CONSTRUCTORS
    //
    private SandboxBusiness() {
    }

}
