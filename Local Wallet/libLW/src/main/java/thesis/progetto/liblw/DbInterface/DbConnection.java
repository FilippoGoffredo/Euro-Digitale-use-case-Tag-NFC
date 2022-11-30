package thesis.progetto.liblw.DbInterface;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Stream;

import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.FileUtils;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class DbConnection implements thesis.progetto.liblw.DbInterface.IDbConnection {

    // VARIABLES
    //
    private static File exchangesFile;
    private static File sandboxesFile;
    private static File numberOfFile;

    private static DbConnection instance = new DbConnection();

    // CONSTRUCTORS
    //
    private DbConnection() {
        exchangesFile = new File("/sdcard/localwallet/exchanges");
        sandboxesFile = new File("/sdcard/localwallet/sandboxes");
        numberOfFile = new File("/sdcard/localwallet/numberOf");
    }

    // INSTANCE
    //
    // method to get an instance of DbConnection
    public static DbConnection getInstance() {
        return instance;
    }

    // MANIPULATION DATA
    //
    // method to check permissione to write/read files
    @Override
    public void checkPerm(Activity activity){
        try {
            if (!(ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    // method to create default folders
    @Override
    public void createDefaultFolders(){
        try {
            if (!(sandboxesFile.exists() && exchangesFile.exists() && numberOfFile.exists())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Files.createDirectories(sandboxesFile.toPath());
                    Files.createDirectories(exchangesFile.toPath());
                    Files.createDirectories(numberOfFile.toPath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // method to take all files in a directory
    @Override
    public ArrayList<String> listFilesForFolder(File folder, ArrayList<String> list) {
        try {
            for (File fileEntry : folder.listFiles()) {
                if ((fileEntry.isDirectory()) && (fileEntry.getName().charAt(0) != '.')) {
                    listFilesForFolder(fileEntry, list);
                } else if (fileEntry.getName().charAt(0) != '.') {
                    list.add(fileEntry.getName().substring(0, fileEntry.getName().indexOf(".")));
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    // method to write file
    @Override
    public int write(File destinationFile, String stringToWrite) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Files.write(Paths.get(destinationFile.getPath()), stringToWrite.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    
    // method to delete file
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int delete(File directoryToBeDeleted) {
        // read java doc, Files.walk need close the resources.
        // try-with-resources to ensure that the stream's open directories are closed
        try (Stream<Path> walk = Files.walk(directoryToBeDeleted.toPath())) {
            walk
                    .sorted(Comparator.reverseOrder())
                    .forEach(DbConnection::deleteDirectoryJava8Extract);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // extract method to handle exception in lambda
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void deleteDirectoryJava8Extract(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            System.err.printf("Unable to delete this path : %s%n%s", path, e);
        }
    }


    // GETTER PATH TYPE
    //
    // method to get key path
    @Override
    public File getKeyFile() {
        return exchangesFile;
    }


    // method to get sandbox path
    @Override
    public File getSandboxFile() {
        return sandboxesFile;
    }

    //method to get n file path
    @Override
    public File getNFileFile() {
        return numberOfFile;
    }
}