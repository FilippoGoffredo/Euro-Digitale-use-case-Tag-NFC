package thesis.progetto.liblw.DAO;

import java.util.ArrayList;

import thesis.progetto.liblw.Model.Key;
import thesis.progetto.liblw.Model.Sandbox;

public interface IKeyDAO {
    
    // MANIPULATION DATA
    //
    // method to create keys
    public ArrayList<Key> generateKeys(int numberOfKeys);

    // method to create json nukber of keys
    public int generateNKeys(ArrayList<Key> arrKeys);

    // method to insert key into a sandbox
    public Sandbox insertKey(Key key, Sandbox sandbox);

    // method to take a key
    public int getKey();
}
