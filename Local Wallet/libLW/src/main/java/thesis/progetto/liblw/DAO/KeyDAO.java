package thesis.progetto.liblw.DAO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import org.json.JSONObject;

import android.os.Build;
import thesis.progetto.liblw.DbInterface.DbConnection;
import thesis.progetto.liblw.DbInterface.IDbConnection;
import thesis.progetto.liblw.Model.Key;
import thesis.progetto.liblw.Model.Sandbox;

public class KeyDAO implements IKeyDAO{
    
    // VARIABLES
    //
    private static KeyDAO instance = new KeyDAO();
    private ArrayList<Key> k;
    private ArrayList<String> list;
    private static IDbConnection conn;
    private int newKeyId;

    // CONSTRUCTORS
    //
    private KeyDAO() {
        k = new ArrayList<>();
        list = new ArrayList<>();
        conn = DbConnection.getInstance();
        newKeyId = 0;
    }

    // STATEMENTS
    //
    public static KeyDAO getInstance() {
        return instance;
    }

    // MANIPULATION DATA
    //
    // method to add key
    @Override
    public ArrayList<Key> generateKeys(int numberOfKeys) {

        for (int i = 0; i < numberOfKeys; i++) {
            //create new file json for the key
            File folder = new File(conn.getKeyFile().toString());
            try {
                newKeyId = Integer.parseInt(conn.listFilesForFolder(folder, list).get(list.size()-1))+1;
            } catch (Exception e) {
                e.printStackTrace();
            }

            File keyFolder = new File(conn.getKeyFile().toString() + "/" + newKeyId);
            File keyFile = new File(keyFolder.toString() + "/" + newKeyId + ".json");

            try {
                if (!keyFile.exists()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Files.createDirectories(keyFolder.toPath());
                        Files.createFile(keyFile.toPath());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // insert key id into the object Key k
            // create json object
            JSONObject jo = new JSONObject();

            // add the verification string for this sandbox
            try {
                jo.put("v", "EXAAA" + newKeyId);
                jo.put("e", newKeyId);
            } catch (Exception e) {
                e.printStackTrace();
            }

            conn.write(keyFile, jo.toString());

            // insert verifiation string in sandbox Object
            k.add(new Key());
            k.get(i).setId("EXAAA" + newKeyId);
            k.get(i).setNId(newKeyId);
            k.get(i).setKey("e" + newKeyId);
        }


        // close connection
        conn = null;

        return k;
    }


    // method to create json nukber of keys
    @Override
    public int generateNKeys(ArrayList<Key> arrKeys) {
        try {
            // open the connection
            conn = DbConnection.getInstance();

            //create new file json for the nkey
            File nFileFile = new File(conn.getNFileFile().toString() + "/nexchange.json");

            // create json object
            JSONObject jo = new JSONObject();

            // add the keys name in the file
            int i = 0;
            for (Key k : arrKeys) {
                jo.put("n" + i, k.getId().replace("EXAAA", "") + ".json");
                conn.write(nFileFile, jo.toString());
                i++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // close connection
        conn = null;

        return 0;
    }


    // method to insert key into a sandbox
    @Override
    public Sandbox insertKey(Key key, Sandbox sandbox) {
        try {
            // open the connection
            conn = DbConnection.getInstance();

            //sandbox for the key
            File sandboxFile = new File(conn.getSandboxFile().toString() + "/" + sandbox.getNId() + "/" + sandbox.getNId() + ".json");
            
            // typecasting obj to JSONObject
            JSONObject jo = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               jo = new JSONObject(Files.readAllLines(sandboxFile.toPath()).get(0));
            }

            // getting index
            jo.put("k", key.getId());
            conn.write(sandboxFile, jo.toString());

            sandbox.setKey(key.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // close connection
        conn = null;

        return sandbox;
    }

    // method to take a key
    @Override
    public int getKey(){
        int keyId = -1;
        try {
            // open the connection
            conn = DbConnection.getInstance();

            // get a key
            ArrayList<String> list = new ArrayList<>();
            keyId = Integer.parseInt(conn.listFilesForFolder(conn.getKeyFile(), list).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return keyId;
    }
}
