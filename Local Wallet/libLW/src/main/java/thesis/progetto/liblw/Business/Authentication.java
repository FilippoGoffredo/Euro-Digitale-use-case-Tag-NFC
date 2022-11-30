package thesis.progetto.liblw.Business;

import thesis.progetto.liblw.DAO.ISandboxDAO;
import thesis.progetto.liblw.DAO.SandboxDAO;
import thesis.progetto.liblw.Model.Sandbox;

public class Authentication {
    
    // VARIABLES
    //
    private static Authentication instance;

    // STATEMENTS
    //
    ISandboxDAO sDao = SandboxDAO.getInstance();

    // lines up all methods that want to use this method
    public static synchronized Authentication getInstance() {
        if (instance == null) {
            instance = new Authentication();
        }
        return instance;
    }

    // CONSTRUCTORS
    //
    private Authentication() {
    }

    // METHODS
    //
    // method verify tokenMap
    private int verifyToken(Sandbox sandbox) {
        try {
            // take the bank key
            String bankKey = "AAAAA";

            // check if the token have an valid verification id else delete the sandbox
            if(!(sandbox.getToken().substring(0,5) == bankKey)) {
                sDao.removeById(sandbox.getNId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // method to send verification result
    public int sendVer(Sandbox sandbox) {
        return verifyToken(sandbox);
    }
}
