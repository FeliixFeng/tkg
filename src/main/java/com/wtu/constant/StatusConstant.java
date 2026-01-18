package com.wtu.constant;

/**
 * 状态常量类
 */
public class StatusConstant {

    // 实体审核状态
    public static final Integer ENTITY_STATUS_PENDING = 0;     // 待审核
    public static final Integer ENTITY_STATUS_APPROVED = 1;    // 已通过
    public static final Integer ENTITY_STATUS_REJECTED = -1;   // 已拒绝

    // 用户类型
    public static final Integer USER_TYPE_NORMAL = 0;          // 普通用户
    public static final Integer USER_TYPE_EXPERT = 1;          // 专家用户
    public static final Integer USER_TYPE_ADMIN = 2;           // 管理员

    // 响应状态码
    public static final Integer SUCCESS_CODE = 1;              // 成功
    public static final Integer ERROR_CODE = 0;                // 失败
}
