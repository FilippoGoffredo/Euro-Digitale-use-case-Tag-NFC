package thesis.progetto.liblw.Model;

public class Key{

    // VARIABLES
    //
    private int nIdKey;
    private String idKey;
    private String key;
    
    // CONSTRUCTORS
    //
    // costructor not empty
    public Key (String key) {
        this.key = key;
    }

    // costructor empty
    public Key () {
        this.key = "";
    }

    // GETTER 
    //
    // number id getter
    public int getNId() {
        return nIdKey;
    }

    // id getter
    public String getId() {
        return idKey;
    }

    // key getter
    public String getKey() {
        return key;
    }

    // SETTER 
    //
    // number id setter
    public void setNId(int nIdKey) {
        this.nIdKey = nIdKey;
    }

    // id setter
    public void setId(String idKey) {
        this.idKey = idKey;
    }

    // key setter
    public void setKey(String key) {
        this.key = key;
    }
}