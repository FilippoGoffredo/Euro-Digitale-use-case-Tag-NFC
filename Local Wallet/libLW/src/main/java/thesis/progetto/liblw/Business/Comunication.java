package thesis.progetto.liblw.Business;

import android.app.Activity;
import android.content.Context;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import thesis.progetto.liblw.DAO.IKeyDAO;
import thesis.progetto.liblw.DAO.KeyDAO;
import thesis.progetto.liblw.DbInterface.DbConnection;
import thesis.progetto.liblw.DbInterface.IDbConnection;
import thesis.progetto.liblw.Model.Key;
import thesis.progetto.liblw.Model.Sandbox;
import thesis.progetto.liblw.Model.Token;


public class Comunication {
    
    // VARIABLES
    //
    public static final String ERROR_DETECTED = "No NFC Tag Detected";
    public static final String WRITE_SUCCESS = "Text Written Successfully!";
    public static final String WRITE_ERROR = "Error duing Writing, Try Again!";

    private static Comunication instance;
    private static int nTokenRequest;
    private static IKeyDAO keyDao;
    private static String msg;
    private static IDbConnection conn;
    private KeyBusiness keyBus;
    private static TokenBusiness tokenBus;

    // STATEMENTS
    //

    // lines up all methods that want to use this method
    public static synchronized Comunication getInstance() {
        if (instance == null) {
            instance = new Comunication();
        }
        return instance;
    }

    // CONSTRUCTORS
    //
    private Comunication() {
        nTokenRequest = 0;
        keyDao = KeyDAO.getInstance();
        msg = "";
        conn = DbConnection.getInstance();
        keyBus = KeyBusiness.getInstance();
        tokenBus = TokenBusiness.getInstance();
    }


//------------------------------------------------------------------------------------------
//    metodo per richiedere dati al tag nfc
    @RequiresApi(api = Build.VERSION_CODES.O)
    public int requestToTag(String howMuchRequest, Context context, Tag nfcTag) {
        String data = getRequestMsg(howMuchRequest);
        try {
//        check della visualizzazione del TAG da parte del dispositivo
            if (nfcTag == null) {
                Toast.makeText(context, ERROR_DETECTED, Toast.LENGTH_LONG).show();
            } else {
//                check della scrittura del TAG da parte del dispositivo
//                capire il numero di token che si vogliono inviare
                nTokenRequest = Integer.parseInt(howMuchRequest);
                write(data, nfcTag);
                Toast.makeText(context, WRITE_SUCCESS, Toast.LENGTH_LONG).show();
            }

//            eliminare keys e numberOf file
            File deleteFile;
            for (int i = 0; i < nTokenRequest; i++) {
                deleteFile = new File(conn.getKeyFile() + "/" + i);
                conn.delete(deleteFile);
            }
            deleteFile = new File(conn.getNFileFile() + "/nexchange.json");
            conn.delete(deleteFile);

        } catch (IOException | FormatException e) {
            Toast.makeText(context, WRITE_ERROR, Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return 1;
        }
        return 0;
    }


//------------------------------------------------------------------------------------------
    //    metodo per scrivere sul Tag
    private void write(String text, Tag tag) throws IOException, FormatException {
        NdefRecord[] records = {createRecord(text)};
        NdefMessage message = new NdefMessage(records);
//        get an instance of ndef for the tag
        Ndef ndef = Ndef.get(tag);
//        enable I/O
        ndef.connect();
//        write the message
        ndef.writeNdefMessage(message);
//        close the connection
        ndef.close();
    }


//------------------------------------------------------------------------------------------
    //    metodo per creare un nuovo record
    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textlength = textBytes.length;
        byte[] payload = new byte[1 + langLength + textlength];

//        set status byte (see NDEF spec for actual bits)
        payload[0] = (byte) langLength;

//        copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textlength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);

        return recordNFC;
    }



//------------------------------------------------------------------------------------------
//    metodo per prende key e numberof file
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getRequestMsg (String howMuchRequest){
//        verificare quante key si vogliono mandare
        try {
            nTokenRequest = Integer.parseInt(howMuchRequest);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

//        creazione delle keys e del rispettivo numberOf
        keyDao.generateNKeys(keyDao.generateKeys(nTokenRequest));

//        lettura numberOf
        File nFile = new File(conn.getNFileFile() + "/nexchange.json");
        try {
            msg = Files.readAllLines(nFile.toPath()).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        lettura exchanges
        for (int i = 0; i < nTokenRequest; i++) {
            File exchange = new File(conn.getKeyFile() + "/" + i + "/" + i + ".json");
            try {
//                creazione messaggio
                msg = msg.concat(";" + Files.readAllLines(exchange.toPath()).get(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return msg;
    }



//------------------------------------------------------------------------------------------
//    metodo per decidere se stiamo accettando un token o una key
    public int accept(String recivedMsg, Tag nfcTag, Context context){
        String[] check = recivedMsg.split(";");
        if (check.length > 1){
            return acceptKey(recivedMsg, nfcTag, context);
        } else {
            return acceptToken(recivedMsg, nfcTag, context);
        }
    }



//------------------------------------------------------------------------------------------
//   metodo nel caso accettazione della key
    public int acceptKey(String recivedMsg, Tag nfcTag, Context context) {
        try {
//            check della lettura del token
            String[] msgSplit = recivedMsg.split(";"); // [0] = numberOf
            JSONObject nExchange = new JSONObject(msgSplit[0]);
            JSONObject exchange = new JSONObject(msgSplit[1]);

            if (nExchange.length() == exchange.length() / 2) {
//                            creazione della key passata dal Tag
                Key k = new Key();
                k.setNId(Integer.parseInt(exchange.getString("e")));
                k.setId(exchange.getString("v"));
                k.setKey(exchange.getString("e"));


//                incapsulo la key
                Sandbox sandbox = keyBus.encapsulationKey(k);

                if (sandbox == null) {
                    Toast.makeText(context, "No token here", Toast.LENGTH_LONG).show();
                } else {
//                restituisco il token
                    try {
                        if (nfcTag == null) {
                            Toast.makeText(context, ERROR_DETECTED, Toast.LENGTH_LONG).show();
                        } else {
                            write(sandbox.getToken(), nfcTag);
                            Toast.makeText(context, WRITE_SUCCESS, Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException | FormatException e) {
                        Toast.makeText(context, WRITE_ERROR, Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }


//                elimino la sandbox
                conn.delete(new File(conn.getSandboxFile() + "/" + sandbox.getNId()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }



//------------------------------------------------------------------------------------------
//   metodo nel caso accettazione del token
    public int acceptToken(String returnedMsg, Tag nfcTag, Context context) {
        try {
            Token t = new Token();
            t.setId(returnedMsg);

//                incapsulo il token
            Sandbox sandbox = tokenBus.encapsulationToken(t);

            if (sandbox == null) {
                Toast.makeText(context, "Error to generate a new sandbox", Toast.LENGTH_LONG).show();
            } else {
//                sovrascrivo il contenuto del token
                try {
                    if (nfcTag == null) {
                        Toast.makeText(context, ERROR_DETECTED, Toast.LENGTH_LONG).show();
//                    elimino la sandbox creata per evitare duplicazione del token da parte dell'utente
                        int i = sandbox.getNId();
                        conn.delete(new File(conn.getSandboxFile() + "/" + sandbox.getNId()));
                    } else {
                        write("", nfcTag);
                        Toast.makeText(context, WRITE_SUCCESS, Toast.LENGTH_LONG).show();
                    }
                } catch (IOException | FormatException e) {
                    Toast.makeText(context, WRITE_ERROR, Toast.LENGTH_LONG).show();
                    conn.delete(new File(conn.getSandboxFile() + "/" + sandbox.getNId()));
                    e.printStackTrace();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            return -1;
        }
        return 1;
    }

}