package com.wtu.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;

/**
 * 阿里云OSS工具类 - 优化版（单例模式）
 */
@Data
@Slf4j
public class AliOssUtil {

    // 阿里云OSS端点
    private String endpoint;

    // 阿里云OSS访问密钥ID
    private String accessKeyId;

    // 阿里云OSS访问密钥
    private String accessKeySecret;

    // 阿里云OSS存储bucket名称
    private String bucketName;

    // OSS 客户端实例（单例）
    private OSS ossClient;

    // 构造函数（不包含 ossClient）
    public AliOssUtil(String endpoint, String accessKeyId, String accessKeySecret, String bucketName) {
        this.endpoint = endpoint;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.bucketName = bucketName;
    }

    /**
     * 获取 OSS 客户端实例（懒加载）
     */
    private OSS getOssClient() {
        if (ossClient == null) {
            synchronized (this) {
                if (ossClient == null) {
                    ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
                    log.info("OSS 客户端初始化成功");
                }
            }
        }
        return ossClient;
    }

    /**
     * 文件上传
     *
     * @param bytes
     * @param objectName
     * @return
     */
    public String upload(byte[] bytes, String objectName) {
        try {
            // 使用单例的 OSS 客户端
            getOssClient().putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
        } catch (OSSException oe) {
            log.error("OSS 异常 - Error Message: {}, Error Code: {}, Request ID: {}, Host ID: {}",
                    oe.getErrorMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId());
            throw new RuntimeException("文件上传失败: " + oe.getErrorMessage());
        } catch (ClientException ce) {
            log.error("OSS 客户端异常 - Error Message: {}", ce.getMessage());
            throw new RuntimeException("文件上传失败: " + ce.getMessage());
        }

        // 文件访问路径规则 https://BucketName.Endpoint/ObjectName
        String filePath = "https://" + bucketName + "." + endpoint + "/" + objectName;
        log.info("文件上传到: {}", filePath);

        return filePath;
    }

    /**
     * Bean 销毁时关闭 OSS 客户端
     */
    @PreDestroy
    public void shutdown() {
        if (ossClient != null) {
            ossClient.shutdown();
            log.info("OSS 客户端已关闭");
        }
    }
}
