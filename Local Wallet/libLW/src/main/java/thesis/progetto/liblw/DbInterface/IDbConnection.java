package thesis.progetto.liblw.DbInterface;

import android.app.Activity;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public interface IDbConnection {

    // MANIPULATION DATA
    //
    // method to check permissione to write/read files
    public void checkPerm(Activity activity);

    // method to create default folders
    public void createDefaultFolders();

    // method to take all files in a directory
    public ArrayList<String> listFilesForFolder(File folder, ArrayList<String> list);

    // method to write file
    public int write(File destinationFile, String stringToWrite);

    // method to delete file
    public int delete(File destinationFile);

    // GETTER FILE TYPE
    //
    // method to get key file
    public File getKeyFile();

    // method to get sandbox file
    public File getSandboxFile();

    //method to get n file file
    public File getNFileFile();
}
