package bank.management.system;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Security utility for cryptographic hashing and verification of PINs and passwords.
 * Uses PBKDF2 with HMAC-SHA256 and salted hashes to prevent dictionary and rainbow table attacks.
 */
public class SecurityUtil {

    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a cryptographically secure 16-byte random salt encoded in Base64.
     */
    public static String generateSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hashes a plain-text PIN or password with the given Base64 salt using PBKDF2WithHmacSHA256.
     *
     * @param input Plain text string (PIN or password)
     * @param saltBase64 Base64-encoded salt
     * @return Base64-encoded hash
     */
    public static String hash(String input, String saltBase64) {
        if (input == null || saltBase64 == null) {
            throw new IllegalArgumentException("Input and salt must not be null");
        }
        try {
            byte[] salt = Base64.getDecoder().decode(saltBase64);
            PBEKeySpec spec = new PBEKeySpec(input.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            // Fallback to SHA-256 with salt if PBKDF2 is unavailable
            return fallbackSha256(input, saltBase64);
        }
    }

    /**
     * Verifies a plain-text input against a stored salt and hash using constant-time comparison.
     */
    public static boolean verify(String input, String saltBase64, String expectedHashBase64) {
        if (input == null || saltBase64 == null || expectedHashBase64 == null) {
            return false;
        }
        String calculatedHash = hash(input, saltBase64);
        return MessageDigest.isEqual(
                calculatedHash.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                expectedHashBase64.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );
    }

    private static String fallbackSha256(String input, String saltBase64) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(saltBase64));
            byte[] bytes = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
