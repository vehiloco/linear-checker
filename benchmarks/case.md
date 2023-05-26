# The experiment results

## 1. rapidoid

- Path: /Users/alexliu/projects/linear-checker/benchmarks/androidKeyStoreProject/rapidoid/rapidoid-crypto/src/main/java/org/rapidoid/crypto/Crypto.java
- code

```Java
	public static byte[] aes(byte @Unique("initialized") [] key, byte[] data, boolean encrypt) {
		Cipher cipher = cipher("AES");

		final SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
		try {
			cipher.init(encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, secretKey);
		} catch (InvalidKeyException e) {
			throw U.rte("Invalid key for the cypher!");
		}

		byte[] enc;
		try {
			enc = cipher.doFinal(data);
		} catch (IllegalBlockSizeException e) {
			throw U.rte("Illegal block size!");
		} catch (BadPaddingException e) {
			throw U.rte("Bad padding!");
		}

		return enc;
	}

	public static byte[] encrypt(String secret, byte[] dataToEncrypt) {
		byte[] key = md5Bytes(secret.getBytes());
		return aes(key, dataToEncrypt, true);
	}

	public static byte[] decrypt(String secret, byte[] dataToDecrypt) {
		byte[] key = md5Bytes(secret.getBytes());
		return aes(key, dataToDecrypt, false);
	}

	public static byte[] encrypt(byte[] dataToEncrypt) {
		return encrypt(secret(), dataToEncrypt);
	}
```
- description:

```
Using a cryptographic hash function like MessageDigest to transform a password or pre-shared secret into a cryptographic key isn't inherently unsafe. But there are potential vulnerabilities that make it less than ideal in many situations.

Speed: Cryptographic hash functions like those provided by MessageDigest (e.g., SHA-256) are designed to be fast. While this is a good property for many uses of hash functions, it's a disadvantage when hashing passwords or secrets. An attacker who gains access to the hash can use brute-force attacks (guessing many different inputs to see if the hash matches) or precomputed tables of hashes (rainbow tables) to try to reverse-engineer the original password. Because the hash function is fast, the attacker can try many possibilities in a short amount of time.

Lack of salting: When using a simple hash function, there's no built-in mechanism for "salting" the input (adding additional random data to the input before hashing). Salting is a key technique to defend against rainbow table attacks because it means that an attacker can't precompute a single table of hash values—they would need a separate table for each possible salt.

Lack of key stretching: Key stretching is a technique where the hash function is applied many times in a row, to increase the amount of work an attacker needs to do to guess the password. Like salting, this is a built-in feature of password hashing functions like PBKDF2, bcrypt, and scrypt, but not of simple hash functions.

In contrast, purpose-built password hashing or key derivation functions like PBKDF2, bcrypt, and scrypt are designed to mitigate these issues. They're deliberately slow (to resist brute-force attacks), include salting as part of their design (to resist rainbow table attacks), and perform key stretching (to increase the amount of work needed to guess a password). This makes them a safer choice when transforming a password or secret into a cryptographic key.

So, while it's not "unsafe" per se to use MessageDigest to create a cryptographic key from a password or pre-shared secret, it is less safe than using a tool specifically designed for the job.
```

## 2. commons-crypto

```java
    private void resetCipher() throws IOException {
        final long counter = streamOffset
                / cipher.getBlockSize();
        padding = (byte) (streamOffset % cipher.getBlockSize());
        inBuffer.position(padding); // Set proper position for input data.

        CtrCryptoInputStream.calculateIV(initIV, counter, iv);
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        } catch (final GeneralSecurityException e) {
            throw new IOException(e);
        }
        cipherReset = false;
    }
```

- description: reuse iv

## 3. RocketMq

```java
    private static byte[] sign(byte[] data, byte @Unique("initialized") [] key, SigningAlgorithm algorithm) throws AclException {
        try {
            Mac mac = Mac.getInstance(algorithm.toString());
            mac.init(new SecretKeySpec(key, algorithm.toString()));
            return mac.doFinal(data);
        } catch (Exception e) {
            String message = String.format(CAL_SIGNATURE_FAILED_MSG, CAL_SIGNATURE_FAILED, e.getMessage());
            log.error(message, e);
            throw new AclException("CAL_SIGNATURE_FAILED", CAL_SIGNATURE_FAILED, message, e);
        }
    }
        ...

public class PlainAccessResource implements AccessResource {

    // Identify the user
    private String accessKey;

    private String secretKey;

    private String whiteRemoteAddress;
    ...
    public String getSecretKey() {
        return secretKey;
    }
```

- description: set secretkey into a field and use it to calculate signature.

## 4. ignite

```java
public class SslContextFactory implements Factory<SSLContext> {
    ...

    /** Key store password */
    protected char[] keyStorePwd;
    ...
    protected KeyStore loadKeyStore(String keyStoreType, String storeFilePath, char[] keyStorePwd)
            throws SSLException {
        try {
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);

            try (InputStream input = openFileInputStream(storeFilePath)) {

                keyStore.load(input, keyStorePwd);

                return keyStore;
            }
        }
```

## 5. Commons-vfs

- path: /Users/alexliu/projects/linear-checker/benchmarks/androidKeyStoreProject/commons-vfs/commons-vfs2/src/main/java/org/apache/commons/vfs2/util/DefaultCryptor.java

- code

```java
public class DefaultCryptor implements Cryptor {
    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static final byte[] KEY_BYTES = {0x41, 0x70, 0x61, 0x63, 0x68, 0x65, 0x43, 0x6F, 0x6D, 0x6D, 0x6F, 0x6E, 0x73, 0x56, 0x46, 0x53};
    ...
    public String encrypt(final String plainKey) throws Exception {
        final byte[] input = plainKey.getBytes(StandardCharsets.UTF_8);
        final SecretKeySpec key = new SecretKeySpec(KEY_BYTES, "AES");

        final Cipher cipher = Cipher.getInstance("AES");

        // encryption pass
        cipher.init(Cipher.ENCRYPT_MODE, key);

        final byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
        int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
        ctLength += cipher.doFinal(cipherText, ctLength);
        return encode(cipherText);
    }
```

- description: hard code a security key