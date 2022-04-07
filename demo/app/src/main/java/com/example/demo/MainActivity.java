package com.example.demo;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity {

    private static final String ALIAS = "demo";
    private Encryptor mEncrypt;
    private Decryptor mDecrypt;
    private EditText input;
    private TextView decrypted;
    private TextView encrypted;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.P)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        input = findViewById(R.id.input);
        encrypted = findViewById(R.id.encrypted);
        decrypted = findViewById(R.id.decrypted);
        Button toEncrypt = findViewById(R.id.encrypt);
        Button toDecrypt = findViewById(R.id.decrypt);
        mEncrypt = new Encryptor();
        try {
            mDecrypt = new Decryptor();
        } catch (CertificateException
                | NoSuchAlgorithmException
                | KeyStoreException
                | IOException e) {
            e.printStackTrace();
        }
        toEncrypt.setOnClickListener(view -> encryptText());
        toDecrypt.setOnClickListener(view -> decryptText());
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void encryptText() {

        try {
            final byte[] encryptedText = mEncrypt.encryptText(ALIAS, input.getText().toString());
            String encryptedTextString = Arrays.toString(encryptedText);
            Log.d(ALIAS, "Encrypted text:" + encryptedTextString);
            String text = getString(R.string.encrypted, encryptedTextString);
            encrypted.setText(text);
        } catch (InvalidAlgorithmParameterException
                | IllegalBlockSizeException
                | BadPaddingException
                | InvalidKeyException
                | NoSuchPaddingException
                | NoSuchProviderException
                | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void decryptText() {
        try {
            String text = mDecrypt.decryptData(ALIAS, mEncrypt.getEncryption(), mEncrypt.getIv());
            Log.d(ALIAS, "Decrypted text:" + text);
            decrypted.setText(getString(R.string.decrypted, text));
        } catch (UnrecoverableEntryException
                | NoSuchAlgorithmException
                | KeyStoreException
                | NoSuchPaddingException
                | InvalidKeyException
                | IllegalBlockSizeException
                | BadPaddingException
                | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }
}
