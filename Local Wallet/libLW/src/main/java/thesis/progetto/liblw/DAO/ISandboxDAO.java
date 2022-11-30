package thesis.progetto.liblw.DAO;

import java.util.ArrayList;

import thesis.progetto.liblw.Model.Sandbox;

public interface ISandboxDAO {

    // MANIPULATION DATA
    //
    //method to create a new sandbox
    public Sandbox generateSandbox();

    // method to select sandbox without key value
    public Sandbox getAvailableKeyValue();
}
