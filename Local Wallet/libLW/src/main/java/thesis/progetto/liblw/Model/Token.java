package thesis.progetto.liblw.Model;

public class Token {

    // VARIABLES
    //
    private String idToken;
    
    // CONSTRUCTORS
    //
    // costructor empty
    public Token () {
    }

    public Token (String idToken){
        this.idToken = idToken;
    }

    // GETTER 
    //
    // id getter
    public String getId() {
        return idToken;
    }

    // SETTER 
    //
    // id setter
    public void setId(String idToken) {
        this.idToken = idToken;
    }
}