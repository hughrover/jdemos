package agent.demo.agent.tools.tencent;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.TreeMap;

/**
 * 腾讯云API签名工具类
 * 实现TC3-HMAC-SHA256签名算法
 *
 * @author Diego
 * @since 2024
 * @see <a href="https://cloud.tencent.com/document/product/1806/121811">腾讯云API签名文档</a>
 */
public class TencentCloudSigner {

    private static final String ALGORITHM = "TC3-HMAC-SHA256";
    private static final String SERVICE = "wsa";
    private static final String HOST = "wsa.tencentcloudapi.com";

    private final String secretId;
    private final String secretKey;

    public TencentCloudSigner(String secretId, String secretKey) {
        this.secretId = secretId;
        this.secretKey = secretKey;
    }

    /**
     * 生成Authorization头
     *
     * @param action   API动作名称
     * @param payload  请求体JSON
     * @param timestamp Unix时间戳（秒）
     * @return Authorization头值
     */
    public String sign(String action, String payload, long timestamp) {
        try {
            // 1. 拼接规范请求串
            String canonicalRequest = buildCanonicalRequest(action, payload, timestamp);

            // 2. 拼接待签名字符串
            String date = java.time.Instant.ofEpochSecond(timestamp)
                    .atZone(java.time.ZoneOffset.UTC)
                    .toLocalDate()
                    .toString();
            String credentialScope = date + "/" + SERVICE + "/tc3_request";
            String stringToSign = buildStringToSign(canonicalRequest, timestamp, credentialScope);

            // 3. 计算签名
            String signature = calculateSignature(stringToSign, date);

            // 4. 拼接Authorization
            return String.format("%s Credential=%s/%s, SignedHeaders=content-type;host, Signature=%s",
                    ALGORITHM, secretId, credentialScope, signature);

        } catch (Exception e) {
            throw new RuntimeException("Failed to sign request: " + e.getMessage(), e);
        }
    }

    /**
     * 构建规范请求串
     * 参考Python实现，确保格式一致
     */
    private String buildCanonicalRequest(String action, String payload, long timestamp) {
        String httpRequestMethod = "POST";
        String canonicalUri = "/";
        String canonicalQueryString = "";
        // canonical headers格式：每行以换行符结尾
        String canonicalHeaders = "content-type:application/json\nhost:" + HOST + "\n";
        String signedHeaders = "content-type;host";

        // 对payload进行SHA256哈希
        String payloadHash = sha256Hex(payload);

        // 拼接规范请求串
        // 格式：HTTP方法\nURI\n查询字符串\nHeaders\n签名Headers\nPayload哈希
        return httpRequestMethod + "\n" +
               canonicalUri + "\n" +
               canonicalQueryString + "\n" +
               canonicalHeaders + "\n" +  // canonicalHeaders已包含末尾换行，再加一个换行形成空行
               signedHeaders + "\n" +
               payloadHash;
    }

    /**
     * 构建待签名字符串
     */
    private String buildStringToSign(String canonicalRequest, long timestamp, String credentialScope) {
        String hashedCanonicalRequest = sha256Hex(canonicalRequest);

        return String.format("%s\n%d\n%s\n%s",
                ALGORITHM, timestamp, credentialScope, hashedCanonicalRequest);
    }

    /**
     * 计算签名
     */
    private String calculateSignature(String stringToSign, String date) throws Exception {
        byte[] secretDate = hmac256(("TC3" + secretKey).getBytes(StandardCharsets.UTF_8), date);
        byte[] secretService = hmac256(secretDate, SERVICE);
        byte[] secretSigning = hmac256(secretService, "tc3_request");
        byte[] signature = hmac256(secretSigning, stringToSign);

        return bytesToHex(signature);
    }

    /**
     * HMAC-SHA256计算
     */
    private byte[] hmac256(byte[] key, String msg) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(msg.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * SHA256哈希并转为十六进制
     */
    private String sha256Hex(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(d);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    /**
     * 字节数组转十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * 获取当前Unix时间戳（秒）
     */
    public static long currentTimestamp() {
        return Instant.now().getEpochSecond();
    }

    /**
     * 从环境变量读取SecretId和SecretKey
     * 环境变量：
     * - TENCENT_API_SECRET_ID: 腾讯云API的SecretId
     * - TENCENT_API_SECRET_KEY: 腾讯云API的SecretKey
     *
     * @return TencentCloudSigner实例
     * @throws IllegalArgumentException 如果环境变量未设置
     */
    public static TencentCloudSigner fromEnvironment() {
        String secretId = System.getenv("TENCENT_API_SECRET_ID");
        if (secretId == null || secretId.isEmpty()) {
            throw new IllegalArgumentException("环境变量 TENCENT_API_SECRET_ID 未设置");
        }

        String secretKey = System.getenv("TENCENT_API_SECRET_KEY");
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("环境变量 TENCENT_API_SECRET_KEY 未设置");
        }

        return new TencentCloudSigner(secretId, secretKey);
    }
}
