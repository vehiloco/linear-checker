package com.example.demo;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.StrongBoxUnavailableException;
import androidx.annotation.RequiresApi;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

class Encryptor {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    private byte[] encryption;
    private byte[] iv;

    Encryptor() {
        this.encryption = new byte[] {};
        this.iv = new byte[] {};
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    byte[] encryptText(final String alias, final String textToEncrypt)
            throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException,
                    InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException,
                    IllegalBlockSizeException {
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKey secretKey = getSecretKey(alias);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        iv = cipher.getIV();
        return (encryption = cipher.doFinal(textToEncrypt.getBytes(StandardCharsets.UTF_8)));
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private SecretKey getSecretKey(final String alias)
            throws NoSuchAlgorithmException, NoSuchProviderException,
                    InvalidAlgorithmParameterException, StrongBoxUnavailableException {

        final KeyGenerator keyGenerator =
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);

        keyGenerator.init(
                new KeyGenParameterSpec.Builder(
                                alias,
                                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setIsStrongBoxBacked(true)
                        .build());

        return keyGenerator.generateKey();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private SecretKey getSecretKeyByDES(final String alias)
            throws NoSuchAlgorithmException, NoSuchProviderException,
                    InvalidAlgorithmParameterException, StrongBoxUnavailableException {
        // Error (NoSuchAlgorithmException) (Supported by KeyGenerator, but not AndroidKeyStore)
        final KeyGenerator keyGenerator = KeyGenerator.getInstance("DES", ANDROID_KEY_STORE);

        keyGenerator.init(
                new KeyGenParameterSpec.Builder(
                                alias,
                                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        // Error in strongboxbacked checking mode
                        .setIsStrongBoxBacked(false)
                        .build());
        return keyGenerator.generateKey();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private KeyPair generateKeyPairByRSA(final String alias)
            throws NoSuchProviderException, NoSuchAlgorithmException,
                    InvalidAlgorithmParameterException, StrongBoxUnavailableException {
        KeyPairGenerator keyPairGenerator =
                KeyPairGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_RSA,
                        "WrongProvider"); // Error (NoSuchProviderException)
        keyPairGenerator.initialize(
                new KeyGenParameterSpec.Builder(
                                alias,
                                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        // Error (512 is not supported by strongbox)
                        .setKeySize(512)
                        .setIsStrongBoxBacked(true)
                        .build());
        return keyPairGenerator.generateKeyPair();
    }

    byte[] getEncryption() {
        return encryption;
    }

    byte[] getIv() {
        return iv;
    }
}
