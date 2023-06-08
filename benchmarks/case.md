# The experiment results

https://github.com/Wechat-Group/WxJava/blob/17ba4bbc86d538bf2b1707e00490fe90d8293750/weixin-java-common/src/main/java/me/chanjar/weixin/common/util/crypto/WxCryptUtil.java#L14
https://github.com/gocd/gocd/blob/1bf99244b63c16708d14b1f30a459010d828fc66/config/config-api/src/main/java/com/thoughtworks/go/security/AESEncrypter.java#L61
https://github.com/EhsanTang/ApiManager
https://github.com/wildfirechat/im-server/blob/252204a760b00b2f2fabc6f06f18cea9c75114fc/common/src/main/java/io/moquette/spi/impl/security/AES.java#L158
https://github.com/LibrePDF/OpenPDF/blob/3b38ad8588669d24fd1f772ec10bb516e996e3c1/openpdf/src/main/java/com/lowagie/text/pdf/PdfEncryption.java#L67
https://github.com/search?q=new+ivparameterspec+language%3AJava&type=code&l=Java
https://github.com/elunez/eladmin/tree/64e608b8df55902b4f3b40165e037a21abc67c3f

### PBE

https://github.com/JumpMind/symmetric-ds/tree/cd240d91345713420a87b086459c887965da75af
https://github.com/jwpttcg66/NettyGameServer/tree/6ef370c1116bdc617578d496d5d0a578da736084


## 1 [OpenPDF](https://github.com/LibrePDF/OpenPDF)

```
OpenPDF is a Java library for creating and editing PDF files with a LGPL and MPL open source license. 
OpenPDF is the LGPL/MPL open source successor of iText, and is based on some forks of iText 4 svn tag. 
We welcome contributions from other developers. Please feel free to submit pull-requests and bugreports 
to this GitHub repository.
```

```Java
    /**
     * implements Algorithm 8: Computing the encryption dictionary’s U (user password) and
     * UE (user encryption) values (Security handlers of revision 6) - ISO 32000-2 section 7.6.4.4.7
     */
    void computeUAndUeAlg8(byte[] userPassword) throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");

        if (userPassword == null)
            userPassword = new byte[0];
        else if (userPassword.length > 127)
            userPassword = Arrays.copyOf(userPassword, 127);

        byte[] userSalts = IVGenerator.getIV(16);

        userKey = new byte[48];
        System.arraycopy(userSalts, 0, userKey, 32, 16);
        byte[] hashAlg2B = hashAlg2B(userPassword, Arrays.copyOf(userSalts, 8), null);
        System.arraycopy(hashAlg2B, 0, userKey, 0, 32);

        hashAlg2B = hashAlg2B(userPassword, Arrays.copyOfRange(userSalts, 8, 16), null);
        cipher.init(Cipher.ENCRYPT_MODE,
                new SecretKeySpec(hashAlg2B, "AES"),
                new IvParameterSpec(new byte[16]));
        ueKey = cipher.update(key, 0, keySize);
    }

// gets keylength and revision and uses revision to choose the initial values
// for permissions
public void setupAllKeys(byte[] userPassword, byte[] ownerPassword,
        int permissions) {
        if (ownerPassword == null || ownerPassword.length == 0)
        ownerPassword = md5.digest(createDocumentId());
        permissions |= (revision == STANDARD_ENCRYPTION_128 || revision == AES_128 || revision == AES_256_V3) ? 0xfffff0c0
        : 0xffffffc0;
        permissions &= 0xfffffffc;
        this.permissions = permissions;
        documentID = createDocumentId();
        if (revision < AES_256_V3)
        {
        // PDF reference 3.5.2 Standard Security Handler, Algorithm 3.3-1
        // If there is no owner password, use the user password instead.
        byte[] userPad = padPassword(userPassword);
        byte[] ownerPad = padPassword(ownerPassword);

        this.ownerKey = computeOwnerKey(userPad, ownerPad);
        setupByUserPad(this.documentID, userPad, this.ownerKey, permissions);
        } else {
        try {
        key = IVGenerator.getIV(32);
        keySize = 32;
        computeUAndUeAlg8(userPassword);
        computeOAndOeAlg9(ownerPassword);
        computePermsAlg10(permissions);
        } catch (GeneralSecurityException e) {
        throw new ExceptionConverter(e);
        }
        }
        }

/**
 * A <CODE>DocWriter</CODE> class for PDF.
 * <P>
 * When this <CODE>PdfWriter</CODE> is added
 * to a certain <CODE>PdfDocument</CODE>, the PDF representation of every Element
 * added to this Document will be written to the outputstream.</P>
 */

public class PdfWriter extends DocWriter implements
        PdfViewerPreferences,
        PdfEncryptionSettings,
        PdfVersion,
        PdfDocumentActions,
        PdfPageActions,
        PdfXConformance,
        PdfRunDirection,
        PdfAnnotations {
    /** @see com.lowagie.text.pdf.interfaces.PdfEncryptionSettings#setEncryption(byte[], byte[], int, int) */
    public void setEncryption(byte[] userPassword, byte[] ownerPassword, int permissions, int encryptionType) throws DocumentException {
        if (pdf.isOpen())
            throw new DocumentException(MessageLocalization.getComposedMessage("encryption.can.only.be.added.before.opening.the.document"));
        crypto = new PdfEncryption();
        crypto.setCryptoMode(encryptionType, 0);
        crypto.setupAllKeys(userPassword, ownerPassword, permissions);
    }
    
    
}

class PdfStamperImp extends PdfWriter {}

/** Applies extra content to the pages of a PDF document.
 * This extra content can be all the objects allowed in PdfContentByte
 * including pages from other Pdfs. The original PDF will keep
 * all the interactive elements including bookmarks, links and form fields.
 * <p>
 * It is also possible to change the field values and to
 * flatten them. New fields can be added but not flattened.
 * @author Paulo Soares (psoares@consiste.pt)
 */
public class PdfStamper {
    /** Sets the encryption options for this document. The userPassword and the
     *  ownerPassword can be null or have zero length. In this case the ownerPassword
     *  is replaced by a random string. The open permissions for the document can be
     *  AllowPrinting, AllowModifyContents, AllowCopy, AllowModifyAnnotations,
     *  AllowFillIn, AllowScreenReaders, AllowAssembly and AllowDegradedPrinting.
     *  The permissions can be combined by ORing them.
     * @param userPassword the user password. Can be null or empty
     * @param ownerPassword the owner password. Can be null or empty
     * @param permissions the user permissions
     * @param strength128Bits <code>true</code> for 128 bit key length, <code>false</code> for 40 bit key length
     * @throws DocumentException if anything was already written to the output
     */
    public void setEncryption(byte[] userPassword, byte[] ownerPassword, int permissions, boolean strength128Bits) throws DocumentException {
        if (stamper.isAppend())
            throw new DocumentException(MessageLocalization.getComposedMessage("append.mode.does.not.support.changing.the.encryption.status"));
        if (stamper.isContentWritten())
            throw new DocumentException(MessageLocalization.getComposedMessage("content.was.already.written.to.the.output"));
        stamper.setEncryption(userPassword, ownerPassword, permissions, strength128Bits ? PdfWriter.STANDARD_ENCRYPTION_128 : PdfWriter.STANDARD_ENCRYPTION_40);
    }
}

public final class PdfEncryptor {

    private PdfEncryptor(){
    }

    /** Entry point to encrypt a PDF document. The encryption parameters are the same as in
     * <code>PdfWriter</code>. The userPassword and the
     *  ownerPassword can be null or have zero length. In this case the ownerPassword
     *  is replaced by a random string. The open permissions for the document can be
     *  AllowPrinting, AllowModifyContents, AllowCopy, AllowModifyAnnotations,
     *  AllowFillIn, AllowScreenReaders, AllowAssembly and AllowDegradedPrinting.
     *  The permissions can be combined by ORing them.
     * @param reader the read PDF
     * @param os the output destination
     * @param userPassword the user password. Can be null or empty
     * @param ownerPassword the owner password. Can be null or empty
     * @param permissions the user permissions
     * @param strength128Bits <code>true</code> for 128 bit key length, <code>false</code> for 40 bit key length
     * @throws DocumentException on error
     * @throws IOException on error */
    public static void encrypt(PdfReader reader, OutputStream os, byte[] userPassword, byte[] ownerPassword, int permissions, boolean strength128Bits) throws DocumentException, IOException {
        PdfStamper stamper = new PdfStamper(reader, os);
        stamper.setEncryption(userPassword, ownerPassword, permissions, strength128Bits);
        stamper.close();
    }


    public final class PdfEncryptor {

        private PdfEncryptor(){
        }

        /** Entry point to encrypt a PDF document. The encryption parameters are the same as in
         * <code>PdfWriter</code>. The userPassword and the
         *  ownerPassword can be null or have zero length. In this case the ownerPassword
         *  is replaced by a random string. The open permissions for the document can be
         *  AllowPrinting, AllowModifyContents, AllowCopy, AllowModifyAnnotations,
         *  AllowFillIn, AllowScreenReaders, AllowAssembly and AllowDegradedPrinting.
         *  The permissions can be combined by ORing them.
         * @param reader the read PDF
         * @param os the output destination
         * @param userPassword the user password. Can be null or empty
         * @param ownerPassword the owner password. Can be null or empty
         * @param permissions the user permissions
         * @param strength128Bits <code>true</code> for 128 bit key length, <code>false</code> for 40 bit key length
         * @throws DocumentException on error
         * @throws IOException on error */
        public static void encrypt(PdfReader reader, OutputStream os, byte[] userPassword, byte[] ownerPassword, int permissions, boolean strength128Bits) throws DocumentException, IOException {
            PdfStamper stamper = new PdfStamper(reader, os);
            stamper.setEncryption(userPassword, ownerPassword, permissions, strength128Bits);
            stamper.close();
        }

        /**
         * Encrypts a PDF document.
         *
         * @param args input_file output_file user_password owner_password permissions 128|40 [new info string pairs]
         */
        public static void main(String[] args) {
            System.out.println("PDF document encryptor");
            if (args.length <= STRENGTH || args[PERMISSIONS].length() != 8) {
                usage();
                return;
            }
            try {
                int permissions = 0;
                String p = args[PERMISSIONS];
                for (int k = 0; k < p.length(); ++k) {
                    permissions |= (p.charAt(k) == '0' ? 0 : permit[k]);
                }
                System.out.println("Reading " + args[INPUT_FILE]);
                PdfReader reader = new PdfReader(args[INPUT_FILE]);
                System.out.println("Writing " + args[OUTPUT_FILE]);
                Map<String, String> moreInfo = new HashMap<>();
                for (int k = MOREINFO; k < args.length - 1; k += 2) {
                    moreInfo.put(args[k], args[k + 1]);
                }
                PdfEncryptor.encrypt(reader, new FileOutputStream(args[OUTPUT_FILE]),
                        args[USER_PASSWORD].getBytes(), args[OWNER_PASSWORD].getBytes(), permissions,
                        args[STRENGTH].equals("128"), moreInfo);
                System.out.println("Done.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
```

## 2 [WxJava](https://github.com/Wechat-Group/WxJava)

```java
public class WxCryptUtil {

    private static final Base64 BASE64 = new Base64();
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final ThreadLocal<DocumentBuilder> BUILDER_LOCAL = ThreadLocal.withInitial(() -> {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setExpandEntityReferences(false);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            return factory.newDocumentBuilder();
        } catch (ParserConfigurationException exc) {
            throw new IllegalArgumentException(exc);
        }
    });

    protected byte[] aesKey;
    protected String token;
    protected String appidOrCorpid;

    public WxCryptUtil() {
    }

    /**
     * 构造函数.
     *
     * @param token          公众平台上，开发者设置的token
     * @param encodingAesKey 公众平台上，开发者设置的EncodingAESKey
     * @param appidOrCorpid  公众平台appid/corpid
     */
    public WxCryptUtil(String token, String encodingAesKey, String appidOrCorpid) {
        this.token = token;
        this.appidOrCorpid = appidOrCorpid;
        this.aesKey = Base64.decodeBase64(StringUtils.remove(encodingAesKey, " "));
    }

    /**
     * 将公众平台回复用户的消息加密打包.
     * <ol>
     * <li>对要发送的消息进行AES-CBC加密</li>
     * <li>生成安全签名</li>
     * <li>将消息密文和安全签名打包成xml格式</li>
     * </ol>
     *
     * @param plainText 公众平台待回复用户的消息，xml格式的字符串
     * @return 加密后的可以直接回复用户的密文，包括msg_signature, timestamp, nonce, encrypt的xml格式的字符串
     */
    public String encrypt(String plainText) {
        // 加密
        String encryptedXml = encrypt(genRandomStr(), plainText);

        // 生成安全签名
        String timeStamp = Long.toString(System.currentTimeMillis() / 1000L);
        String nonce = genRandomStr();

        String signature = SHA1.gen(this.token, timeStamp, nonce, encryptedXml);
        return generateXml(encryptedXml, signature, timeStamp, nonce);
    }
    
    /**
     * 对明文进行加密.
     *
     * @param plainText 需要加密的明文
     * @return 加密后base64编码的字符串
     */
    public String encrypt(String randomStr, String plainText) {
        ByteGroup byteCollector = new ByteGroup();
        byte[] randomStringBytes = randomStr.getBytes(CHARSET);
        byte[] plainTextBytes = plainText.getBytes(CHARSET);
        byte[] bytesOfSizeInNetworkOrder = number2BytesInNetworkOrder(plainTextBytes.length);
        byte[] appIdBytes = this.appidOrCorpid.getBytes(CHARSET);

        // randomStr + networkBytesOrder + text + appid
        byteCollector.addBytes(randomStringBytes);
        byteCollector.addBytes(bytesOfSizeInNetworkOrder);
        byteCollector.addBytes(plainTextBytes);
        byteCollector.addBytes(appIdBytes);

        // ... + pad: 使用自定义的填充方式对明文进行补位填充
        byte[] padBytes = PKCS7Encoder.encode(byteCollector.size());
        byteCollector.addBytes(padBytes);

        // 获得最终的字节流, 未加密
        byte[] unencrypted = byteCollector.toBytes();

        try {
            // 设置加密模式为AES的CBC模式
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(this.aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(this.aesKey, 0, 16);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

            // 加密
            byte[] encrypted = cipher.doFinal(unencrypted);

            // 使用BASE64对加密后的字符串进行编码
            return BASE64.encodeToString(encrypted);
        } catch (Exception e) {
            throw new WxRuntimeException(e);
        }
    }
}

public abstract class WxMpXmlOutMessage implements Serializable {
    private static final long serialVersionUID = -381382011286216263L;

    /**
     * 转换成加密的结果
     */
    public WxMpXmlOutMessage toEncrypted(WxMpConfigStorage wxMpConfigStorage) {
        String plainXml = toXml();
        WxMpCryptUtil pc = new WxMpCryptUtil(wxMpConfigStorage);
        WxCryptUtil.EncryptContext context = pc.encryptContext(plainXml);
        WxMpXmlOutMessage res = new WxMpXmlOutMessage() {};
        res.setNonce(context.getNonce());
        res.setEncrypt(context.getEncrypt());
        res.setTimeStamp(context.getTimeStamp());
        res.setMsgSignature(context.getSignature());
        return res;
    }
}

public class WxMpEndpointServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected WxMpConfigStorage wxMpConfigStorage;
    protected WxMpService wxMpService;
    protected WxMpMessageRouter wxMpMessageRouter;

    public WxMpEndpointServlet(WxMpConfigStorage wxMpConfigStorage, WxMpService wxMpService,
                               WxMpMessageRouter wxMpMessageRouter) {
        this.wxMpConfigStorage = wxMpConfigStorage;
        this.wxMpService = wxMpService;
        this.wxMpMessageRouter = wxMpMessageRouter;
    }
    
        if ("aes".equals(encryptType)) {
        // 是aes加密的消息
        String msgSignature = request.getParameter("msg_signature");
        WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(request.getInputStream(), this.wxMpConfigStorage, timestamp, nonce, msgSignature);
        WxMpXmlOutMessage outMessage = this.wxMpMessageRouter.route(inMessage);
        response.getWriter().write(outMessage.toEncryptedXml(this.wxMpConfigStorage));
        return;
    }
}


```
