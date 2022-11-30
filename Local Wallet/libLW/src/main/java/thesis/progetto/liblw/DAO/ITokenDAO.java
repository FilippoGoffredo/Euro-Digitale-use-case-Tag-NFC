package thesis.progetto.liblw.DAO;

import thesis.progetto.liblw.Model.Sandbox;
import thesis.progetto.liblw.Model.Token;

public interface ITokenDAO {
    
    // MANIPULATION DATA
    //
    // method to insert token
    public Sandbox insertToken(Token token, Sandbox sandbox);
}
