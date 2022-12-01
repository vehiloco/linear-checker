# Benchmarks

## [rocketmq-streams](https://github.com/apache/rocketmq-streams.git)

### Error Usage

- Hardcode encrypt key and store it into the field

```java
    private static final String PRIVATE_KEY = "f835mnga013mb39c";

    private static final String CHARSET = "UTF-8";

    private static byte[] encrypt(String content, String strKey) throws Exception {
        SecretKeySpec skeySpec = getKey(strKey);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec(PRIVATE_KEY.getBytes(CHARSET));
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(content.getBytes(CHARSET));
        return encrypted;
    }
```

```shell
  found   : @Shared byte @Shared []
  required: @Shared byte @Unique("initialized") []
```

### False Positives

- Also detected decrypt code.

## [CacheManage](https://github.com/ronghao/CacheManage)

### Error Usage

- Use IV without initialization
- Use Field to store IV for encryption
```java
    @Override
    public String encrypt(String str) {
        try {
            return Des3Util.encode(str, this.secretKey, this.iv);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
```

```java
    public static String encode(String plainText, String secretKey, @Unique String iv) throws Exception {
        if (TextUtils.isEmpty(secretKey) || TextUtils.isEmpty(iv)) {
            throw new NullPointerException("u should init first");
        }
        DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        SecretKey deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance(DESEDE_CBC_PKCS5_PADDING);
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
        cipher.init(1, deskey, ips);
        byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
        return Base64Util.encode(encryptData);
    }
```

```shell
  IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
  found   : @Shared byte @Shared []
  required: @Shared byte @Unique("initialized") []
```

### FalsePositive

- Also detect the usage of `decode`

```java
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
```

## [Fingerprint](https://github.com/al3xliu/Fingerprint.git)

### Error Usage

### False Positives

```java
    private boolean initDecryptionCipher(byte[] ivBytes){
        try {
            cipher.init(Cipher.DECRYPT_MODE, cipherKey, new IvParameterSpec(ivBytes));
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }
```

## [druid](https://github.com/apache/druid.git)

Passed.

### Error Usage

### False Positives

## [kylin](https://github.com/apache/kylin.git)

### Error Usage

### False Positives

```shell
        IvParameterSpec ivSpec = new IvParameterSpec(KylinConfig.getInstanceFromEnv().getEncryptCipherIvSpec().getBytes("UTF-8"));
```

