package thesis.progetto.localwallet;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import thesis.progetto.liblw.Business.KeyBusiness;
import thesis.progetto.liblw.DbInterface.DbConnection;
import thesis.progetto.liblw.DbInterface.IDbConnection;
import thesis.progetto.liblw.Model.Key;
import thesis.progetto.liblw.Model.Sandbox;
import thesis.progetto.localwallet.databinding.ActivityMainBinding;
import thesis.progetto.liblw.Business.Comunication;

import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

//    variabili
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter writingTagFilters[];
    private boolean writeMode;
    private Tag nfcTag;
    private Context context;
    private TextView howMuchRequest;
    private TextView nReceived;
    private TextView nAmount;
    private Button button_request;
    private Button button_reject;
    private Button button_accept;
    private ActivityMainBinding binding;
    private IDbConnection conn = DbConnection.getInstance();
    private Comunication comun;
    private String msg;
    private ArrayList<String> list;


//    on create
    @SuppressLint({"MissingPermission", "HardwareIds"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

//        check permessi per modificare i file
        conn.checkPerm(this);

//        creazione cartelle per storicizzare sandboxes ed exchange
        conn.createDefaultFolders();

//       inizializzazione variabili
        howMuchRequest = findViewById(R.id.howMuchRequest);
        nReceived = findViewById(R.id.nReceived);
        nAmount = findViewById(R.id.nAmount);
        button_request = findViewById(R.id.button_request);
        button_reject = findViewById(R.id.button_reject);
        button_accept = findViewById(R.id.button_accept);
        comun = Comunication.getInstance();
        context = this;
        msg = "";
        list = new ArrayList<>();

//        conto il numero di tocken presenti nel dispositivo
        conn.listFilesForFolder(conn.getSandboxFile(), list);
        nAmount.setText(list.size() + "");

//        request button
        button_request.setOnClickListener(new View.OnClickListener(){

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                comun.requestToTag(howMuchRequest.getText().toString(), context, nfcTag);
            }
        });

//        check del supporto di NFC sul dispositivo
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter == null){
            Toast.makeText(this, "This device does not support NFC", Toast.LENGTH_LONG).show();
            finish();
        }

//        lettura dell'Intent
        readFromIntent(getIntent());
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writingTagFilters = new IntentFilter[] {tagDetected};
    }

//------------------------------------------------------------------------------------------
//    metodo per lettere l'Intent
    private void readFromIntent(Intent intent){
        String action = intent.getAction();
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if(rawMsgs != null){
                msgs = new NdefMessage[rawMsgs.length];
                for(int i = 0; i < rawMsgs.length; i++){
                    msgs[i] = (NdefMessage) rawMsgs [i];
                }
            }

//            recuperio del messaggio dal Tag
            msg = buildTagViews(msgs);

            button_accept.setVisibility(View.VISIBLE);
            button_reject.setVisibility(View.VISIBLE);

//            accettazione della storicizzazione del Tag
            button_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    button_accept.setVisibility(View.INVISIBLE);
                    button_reject.setVisibility(View.INVISIBLE);

                    int acc = comun.accept(msg, nfcTag, context);
                    if (acc == 1){
                        nReceived.setText("1" + " €D");
                        nAmount.setText(Integer.parseInt(nAmount.getText().toString()) + 1 + "");
                    }
                    if (acc == 0){
                        nReceived.setText("key inside");
                        nAmount.setText(Integer.parseInt(nAmount.getText().toString()) - 1 + "");
                    }


                }
            });

//            rifiuto della storicizzazionw del Tag
            button_reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    button_accept.setVisibility(View.INVISIBLE);
                    button_reject.setVisibility(View.INVISIBLE);
                    msg = null;
                }
            });


        }
    }

//------------------------------------------------------------------------------------------
//    metodo per la decodifica del NdefMessage
    private String buildTagViews(NdefMessage[] msgs){
        if(msgs == null || msgs.length == 0){
            return null;
        }

        String text = "";
//        String tagId = new String(msgs[0].getRecords()[0].getType());
        byte[] payload = msgs[0].getRecords()[0].getPayload();
//        get text encoding
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
//        get the language code (es: "en")
        int languageCodeLength = payload[0] & 0063;
//        String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

        try {
//            get the text
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e){
            Log.e("UnsupportedEncoding", e.toString());
        }

        nReceived.setText(text);
        return text;
    }

//------------------------------------------------------------------------------------------
//    ad ogni nunovo intent
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        readFromIntent(intent);
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            nfcTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        WriteModeOff();
    }

    @Override
    public void onResume() {
        super.onResume();
        WriteModeOn();
    }

    private void WriteModeOff() {
        writeMode = false;
        nfcAdapter.disableForegroundDispatch(this);
    }

    private void WriteModeOn() {
        writeMode = true;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writingTagFilters, null);
    }
}