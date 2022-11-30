package thesis.progetto.liblw.Business;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class Encryption {
    
    // VARIABLES
    //
    private static Encryption instance;
    private String string;
    private String hashedString;

    // CONSTRUCTORS
    //
    public Encryption() {
        this.string = "";
        this.hashedString = "";
    }

    // lines up all methods that want to use this method
    public static synchronized Encryption getInstance() {
        if (instance == null) {
            instance = new Encryption();
        }
        return instance;
    }

    // SETTERS
    //
    // method to set string that will be hashed
    public void setString(String string) {
        this.string = string;
        this.hashedString = BCrypt.hashpw(this.string, BCrypt.gensalt(10));
    }

    // method to set only the string
    public void setOnlyString(String onlyString) {
        this.string = onlyString;
    }

    // method to set only the hashed string
    public void setOnlyHashedString(String onlyHashedString) {
        this.hashedString = onlyHashedString;
    }

    // GETTERS
    //
    // method to get hashed string
    public String getHashedString() {
        return hashedString;
    }

    // method to get the string
    public String getString() {
        return string;
    }
    
    // CHECK
    //
    // method to check string = hashedString
    public boolean check(String string) {
        try {
            if (BCrypt.checkpw(string, hashedString)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
}
