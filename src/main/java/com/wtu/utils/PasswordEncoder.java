package com.wtu.utils;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码加密工具类
 * 使用简单的加盐 SHA-256 加密（兼容性更好，不需要额外依赖）
 */
@Component
public class PasswordEncoder {

    private static final int SALT_LENGTH = 16;

    /**
     * 加密密码
     */
    public String encode(String rawPassword) {
        try {
            // 生成随机盐
            byte[] salt = generateSalt();
            // 加密密码
            byte[] hash = hash(rawPassword, salt);
            // 将盐和哈希值组合并编码为Base64
            byte[] combined = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hash, 0, combined, salt.length, hash.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    /**
     * 验证密码
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        try {
            // 解码存储的密码
            byte[] combined = Base64.getDecoder().decode(encodedPassword);
            // 提取盐
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(combined, 0, salt, 0, SALT_LENGTH);
            // 提取哈希值
            byte[] storedHash = new byte[combined.length - SALT_LENGTH];
            System.arraycopy(combined, SALT_LENGTH, storedHash, 0, storedHash.length);
            // 使用相同的盐加密输入的密码
            byte[] computedHash = hash(rawPassword, salt);
            // 比较哈希值
            return MessageDigest.isEqual(storedHash, computedHash);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 生成随机盐
     */
    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * 使用 SHA-256 加密
     */
    private byte[] hash(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        return md.digest(password.getBytes());
    }
}
