package thesis.progetto.liblw.DAO;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

import org.json.JSONObject;

import android.os.Build;

import thesis.progetto.liblw.DbInterface.DbConnection;
import thesis.progetto.liblw.DbInterface.IDbConnection;
import thesis.progetto.liblw.Model.Sandbox;
import thesis.progetto.liblw.Model.Token;

public class TokenDAO implements ITokenDAO{
    
    // VARIABLES
    //
    private static TokenDAO instance = new TokenDAO();
    private Token t;
    private ArrayList<String> list;
    private static IDbConnection conn;

    // CONSTRUCTORS
    //
    private TokenDAO() {
        t = null;
        conn = null;
        list  = new ArrayList<>();
    }

    // STATEMENTS
    //
    public static TokenDAO getInstance() {
        return instance;
    }

    // MANIPULATION DATA
    //
    // method to insert token
    @Override
    public Sandbox insertToken(Token token, Sandbox newSandbox) {
        try {
            // open the connection
            conn = DbConnection.getInstance();

            //search sandbox for the token
            File sandboxFile = new File(conn.getSandboxFile().toString() + "/" + newSandbox.getNId() + "/" + newSandbox.getNId() + ".json");
                
            // create JSONObject
            JSONObject jo = new JSONObject();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                jo = new JSONObject(Files.readAllLines(sandboxFile.toPath()).get(0));
            }

            // getting index
            jo.put("t", token.getId());
            conn.write(sandboxFile, jo.toString());

            newSandbox.setToken(token.getId());
        } catch (Exception e) {
           e.printStackTrace();
        }

        // close connection
        conn = null;

        return newSandbox;
    }
}
