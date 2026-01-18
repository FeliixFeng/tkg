package com.wtu.constant;

/**
 * 信息提示常量类
 */
public class MessageConstant {

    // 用户相关
    public static final String USER_LOGIN_FAILED = "用户名或密码错误!";
    public static final String USER_ALREADY_EXISTS = "用户名已存在!";
    public static final String USER_NOT_FOUND = "用户不存在!";
    public static final String PASSWORD_ERROR = "密码错误!";

    // 实体相关
    public static final String ENTITY_NOT_FOUND = "实体不存在";

    // 文件上传相关
    public static final String UPLOAD_FAILED = "上传失败";
    public static final String FILE_EMPTY = "文件不能为空";
    public static final String FILE_TYPE_ERROR = "不支持的文件类型";

    // Token 相关
    public static final String TOKEN_INVALID = "Invalid Token";
    public static final String TOKEN_MISSING = "Authorization header missing or invalid";

    // 通用
    public static final String UNKNOWN_ERROR = "未知错误";
}
