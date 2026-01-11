package com.wtu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 *  后端启动类
 * </p>
 *
 * @since 2026-01-11
 */
// 扫描 Mapper 接口所在的包
@MapperScan("com.wtu.mapper")
@SpringBootApplication
public class TkgBackendApplication {

    private static final Logger logger = LoggerFactory.getLogger(TkgBackendApplication.class);

    public static void main(String[] args) {
        // 启动 Spring Boot 应用
        SpringApplication.run(TkgBackendApplication.class, args);
        logger.info("TkgBackendApplication 已成功启动！");
    }

}
