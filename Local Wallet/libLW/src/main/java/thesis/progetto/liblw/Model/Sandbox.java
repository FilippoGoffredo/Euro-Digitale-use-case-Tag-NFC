package thesis.progetto.liblw.Model;

public class Sandbox {

    // VARIABLES
    //
    private int nIdSandbox;
    private String idSandbox;
    private String token;
    private String key;
    
    // CONSTRUCTORS
    //
    // costructor not empty
    public Sandbox (String token, String key) {
        this.token = token;
        this.key = key;
        this.idSandbox = "";
    }

    // costructor empty
    public Sandbox () {
        this.token = "";
        this.key = "";
        this.idSandbox = "";
    }

    // GETTER 
    //
    // number id getter
    public int getNId() {
        return nIdSandbox;
    }

    // id getter
    public String getId() {
        return idSandbox;
    }

    // token getter
    public String getToken() {
        return token;
    }

    // key getter
    public String getKey() {
        return key;
    }

    // SETTER 
    //
    // number id setter
    public void setNId(int nIdSandbox) {
        this.nIdSandbox = nIdSandbox;
    }

    // id setter
    public void setId(String idSandbox) {
        this.idSandbox = idSandbox;
    }

    // token setter
    public void setToken(String token) {
        this.token = token;
    }

    // key setter
    public void setKey(String key) {
        this.key = key;
    }
}