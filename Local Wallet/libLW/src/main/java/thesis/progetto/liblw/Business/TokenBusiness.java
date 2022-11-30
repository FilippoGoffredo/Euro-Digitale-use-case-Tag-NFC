package thesis.progetto.liblw.Business;

import thesis.progetto.liblw.DAO.ISandboxDAO;
import thesis.progetto.liblw.DAO.ITokenDAO;
import thesis.progetto.liblw.DAO.SandboxDAO;
import thesis.progetto.liblw.DAO.TokenDAO;
import thesis.progetto.liblw.Model.Sandbox;
import thesis.progetto.liblw.Model.Token;

public class TokenBusiness {
    
    // VARIABLES
    //
    private static TokenBusiness instance;

    // STATEMENTS
    //
    ITokenDAO tDao = TokenDAO.getInstance();
    ISandboxDAO sDao = SandboxDAO.getInstance();

    // lines up all methods that want to use this method
    public static synchronized TokenBusiness getInstance() {
        if (instance == null) {
            instance = new TokenBusiness();
        }
        return instance;
    }

    // CONSTRUCTORS
    //
    private TokenBusiness() {
    }

    // MANIPULATION DATA
    //
    //method to encapsulate token into a sandbox
    public Sandbox encapsulationToken(Token token) {
        Sandbox newSandbox = sDao.generateSandbox();
        return tDao.insertToken(token, newSandbox);
    }
}
