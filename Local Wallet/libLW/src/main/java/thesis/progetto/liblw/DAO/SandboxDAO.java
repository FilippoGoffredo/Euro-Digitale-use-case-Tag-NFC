package thesis.progetto.liblw.DAO;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

import org.json.JSONObject;

import android.os.Build;

import thesis.progetto.liblw.DbInterface.DbConnection;
import thesis.progetto.liblw.DbInterface.IDbConnection;
import thesis.progetto.liblw.Model.Sandbox;

public class SandboxDAO implements ISandboxDAO {
    
    // VARIABLES
    //
    private static SandboxDAO instance = new SandboxDAO();
    private Sandbox sb;
    private ArrayList<String> list;
    private static IDbConnection conn;

    // CONSTRUCTORS
    //
    private SandboxDAO() {
        sb = new Sandbox();
        list = new ArrayList<>();
        conn = null;
    }

    // STATEMENTS
    //
    public static SandboxDAO getInstance() {
        return instance;
    }

    // MANIPULATION DATA
    //
    //method to create a new sandbox
    @Override
    public Sandbox generateSandbox() {
        try {
            // open the connection
            conn = DbConnection.getInstance(); 

            //create new file json for the sandbox
            File folder = new File(conn.getSandboxFile().toString());
            int newSandboxId;
            try {
                newSandboxId = Integer.parseInt(conn.listFilesForFolder(folder, list).get(list.size() - 1)) + 1;
            } catch (IndexOutOfBoundsException e){
                e.printStackTrace();
                newSandboxId = 0;
            }

            File sandboxFolder = new File(conn.getSandboxFile().toString() + "/" + newSandboxId);
            File sandboxFile = new File(sandboxFolder.toString() + "/" + newSandboxId + ".json");

            if (!sandboxFile.exists()){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Files.createDirectories(sandboxFolder.toPath());
                    Files.createFile(sandboxFile.toPath());
                }
            }
            
            // insert sandbox id into the object Sandbox sb
            // create json object
            JSONObject jo = new JSONObject();

            // getting index
            jo.put("v", "AAAAA" + newSandboxId);
            conn.write(sandboxFile, jo.toString());
                
            // insert verifiation string in sandbox Object
            sb.setId("AAAAA" + newSandboxId);
            sb.setNId(newSandboxId);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // close connection
        conn = null;

        return sb;
    }


    // method to select sandbox without key value
    @Override
    public Sandbox getAvailableKeyValue() {
        try {
            // open the connection
            conn = DbConnection.getInstance();
            // select all the sandbox
            File folder = new File(conn.getSandboxFile().toString());
            list = conn.listFilesForFolder(folder, list);

            // check all the sandbox until i found an available sandbox
            for (String file : list) {
                File sandboxFile = new File(folder.toString() + "/" + file + "/" + file + ".json");
                JSONObject jo = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                   jo = new JSONObject(Files.readAllLines(sandboxFile.toPath()).get(0));
                }


                // set sandbox
                try {
                    sb.setKey(jo.get("k").toString());
                } catch (Exception e) {
                    sb.setId(jo.get("v").toString());
                    sb.setNId(Integer.parseInt(sb.getId().replaceAll("AAAAA", "")));
                    sb.setToken(jo.get("t").toString());

                    return sb;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // close connection
        conn = null;

        return null;
    }
}
