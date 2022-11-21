// 1. FingerPrint
//  found   : @Shared byte @Unique []
//  required: @Shared byte @Unique("initialized") []
// TODO; 1. fix default for arrays
// TODO: 1. try post condition 2.automatically do it
private boolean initDecryptionCipher(byte @Unique("initialized")[] ivBytes){
        try {
        cipher.init(Cipher.DECRYPT_MODE, cipherKey, new IvParameterSpec(ivBytes));
        return true;
        } catch (KeyPermanentlyInvalidatedException e) {
        return false;
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
        throw new RuntimeException("Failed to init Cipher", e);
        }
}

public FingerprintManager.CryptoObject getDecryptionCryptoObject(byte @Unique("initialized")[] ivBytes){
        loadKeyStore();
        if(!hasKey()){
        generateNewKey();
        }

        createCipher();
        if (initDecryptionCipher(ivBytes)) {
        return new FingerprintManager.CryptoObject(cipher);
        } else {
        return null;
        }
}

// 2.androidKeyStore: crashed

/*
*   Exception: org.checkerframework.javacutil.BugInCF: Error when invoking constructor org.checkerframework.checker.linear.LinearAnnotatedTypeFactory(class org.checkerframework.common.basetype.BaseTypeChecker) on args [org.checkerframework.checker.linear.LinearChecker@3906d0f]; cause: JDK not found; org.checkerframework.javacutil.BugInCF: Error when invoking constructor org.checkerframework.checker.linear.LinearAnnotatedTypeFactory(class org.checkerframework.common.basetype.BaseTypeChecker) on args [org.checkerframework.checker.linear.LinearChecker@3906d0f]; cause: JDK not found
        at org.checkerframework.common.basetype.BaseTypeChecker.invokeConstructorFor(BaseTypeChecker.java:360)
        at org.checkerframework.common.basetype.BaseTypeVisitor.createTypeFactory(BaseTypeVisitor.java:305)
        at org.checkerframework.common.basetype.BaseTypeVisitor.<init>(BaseTypeVisitor.java:268)
        at org.checkerframework.common.basetype.BaseTypeVisitor.<init>(BaseTypeVisitor.java:257)
        at org.checkerframework.checker.linear.LinearVisitor.<init>(LinearVisitor.java:36)
        at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)*/

// 3.CacheManage

//  found   : @Shared byte @Unique []
//  required: @Shared byte @Unique("initialized") []
// TODO: find cases use emtpy array directly
public static String encode(String plainText, String secretKey, @Unique String iv) throws Exception {
        if (TextUtils.isEmpty(secretKey) || TextUtils.isEmpty(iv)) {
        throw new NullPointerException("u should init first");
        }
        DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        SecretKey deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance(DESEDE_CBC_PKCS5_PADDING);
        //this line
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
        cipher.init(1, deskey, ips);
        byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
        return Base64Util.encode(encryptData);
}

public static String decode(String encryptText, String secretKey, @Unique String iv) throws Exception {
        if (TextUtils.isEmpty(secretKey) || TextUtils.isEmpty(iv)) {
        throw new NullPointerException("u should init first");
        }
        DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        SecretKey deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance(DESEDE_CBC_PKCS5_PADDING);
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
        cipher.init(2, deskey, ips);
        byte[] decryptData = cipher.doFinal(Base64Util.decode(encryptText));
        return new String(decryptData, encoding);
}
//  found   : @Shared String
//  required: @Unique String

public String encrypt(String str) {
        try {
        return Des3Util.encode(str, this.secretKey, this.iv);
        } catch (Exception e) {
        e.printStackTrace();
        return "";
        }
}

@Override
public String decode(String str) {
        try {
        return Des3Util.decode(str, this.secretKey, this.iv);
        } catch (Exception e) {
        e.printStackTrace();
        return "";
        }
}

// 4. druid
//  found   : @Shared byte @Unique TODO @Shared []
//  required: @Shared byte @Unique("initialized") []
{
        try {
        EncryptedData encryptedData = EncryptedData.fromByteArray(data);

        SecretKey tmp = getKeyFromPassword(passPhrase, encryptedData.getSalt());
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), cipherAlgName);

// error-prone warns if the transformation is not a compile-time constant
// since it cannot check it for insecure combinations.
@SuppressWarnings("InsecureCryptoUsage")
      Cipher dcipher = Cipher.getInstance(transformation);
              dcipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(encryptedData.getIv()));
              return dcipher.doFinal(encryptedData.getCipher());
              }
              catch (InvalidKeySpecException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
              throw new RuntimeException(ex);
              }
}

// 5. rapidoid

// warning: LinearChecker did not find annotation file or directory None on classpath or within current directory

// 6. kylin
