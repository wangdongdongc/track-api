package wdd.api.track.service.impl;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.crypto.cipher.CryptoCipher;
import org.apache.commons.crypto.utils.Utils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import wdd.api.track.service.BehaviorService;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Properties;

/**
 * 基于对称加密的 {@link BehaviorService} 实现
 * @deprecated getTypeIdByBehaviorId 方法可能抛出 java.lang.InternalError，可能 apache-commons-crypto 存在缺陷
 *  <i>e.g. behaviorId="b73f1df47f26c490c8cbece10fdb4065"<i/>
 */
@Service
@Deprecated
public class SymmetricEncryptedBehaviorServiceImpl implements BehaviorService {

    private static final Logger LOG = LoggerFactory.getLogger(SymmetricEncryptedBehaviorServiceImpl.class);

    private static final String DELIMITER = ":";
    private static final String transform = "AES/CBC/PKCS5Padding";

    @Value("${behavior.secretKey}")
    private String secretKey;

    @Value("${behavior.ivKey}")
    private String ivKey;

    @Override
    public String getBehaviorIdByTypeId(long typeId, long subTypeId) {

        final String input = typeId + DELIMITER + subTypeId;
        final byte[] inputBytes = getUTF8Bytes(input);
        final byte[] outputBytes = new byte[inputBytes.length + 16];

        final SecretKeySpec key = new SecretKeySpec(getUTF8Bytes(secretKey), "AES");
        final IvParameterSpec iv = new IvParameterSpec(getUTF8Bytes(ivKey));

        int updateBytes;
        int finalBytes;
        try {
            CryptoCipher encryptCipher = Utils.getCipherInstance(transform, new Properties());
            encryptCipher.init(Cipher.ENCRYPT_MODE, key, iv);

            updateBytes = encryptCipher.update(inputBytes, 0, inputBytes.length, outputBytes, 0);
            finalBytes = encryptCipher.doFinal(inputBytes, 0, 0, outputBytes, updateBytes);

            encryptCipher.close();
        } catch (Exception e) {
            //
            return null;
        }

        return bytesToString(Arrays.copyOf(outputBytes, updateBytes + finalBytes));
    }

    @Override
    public Pair<Long, Long> getTypeIdByBehaviorId(String behaviorId) {
        final byte[] inputBytes = stringToBytes(behaviorId);
        final byte[] outputBytes = new byte[inputBytes.length + 16];

        final SecretKeySpec key = new SecretKeySpec(getUTF8Bytes(secretKey), "AES");
        final IvParameterSpec iv = new IvParameterSpec(getUTF8Bytes(ivKey));

        long typeId;
        long subTypeId;

        try {
            CryptoCipher encryptCipher = Utils.getCipherInstance(transform, new Properties());
            encryptCipher.init(Cipher.DECRYPT_MODE, key, iv);

            int updateBytes = encryptCipher.update(inputBytes, 0, inputBytes.length, outputBytes, 0);
            int finalBytes = encryptCipher.doFinal(inputBytes, 0, 0, outputBytes, updateBytes);
            encryptCipher.close();

            String[] types = new String(Arrays.copyOf(outputBytes, updateBytes + finalBytes), Charset.forName("UTF-8"))
                    .split(DELIMITER);
            Assert.isTrue(types.length == 2, "invalid behaviorId");

            typeId = Long.parseLong(types[0]);
            subTypeId = Long.parseLong(types[1]);

        } catch (Exception e) {
            throw new IllegalArgumentException("invalid behaviorId");
        }

        return new ImmutablePair<>(typeId, subTypeId);
    }

    private static byte[] stringToBytes(String input) {
        try {
            return Hex.decodeHex(input);
        } catch (DecoderException e) {
            throw new IllegalArgumentException("Fail to decode " + input);
        }
    }

    private static String bytesToString(byte[] bytes) {
        return Hex.encodeHexString(bytes);
    }

    private static byte[] getUTF8Bytes(String input) {
        return input.getBytes(Charset.forName("UTF-8"));
    }
}
