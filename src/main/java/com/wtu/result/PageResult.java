package com.wtu.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


/**
 * 分页结果类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResult {

    // 总共条数
    private long total;

    // 数据列表
    private List record;
}
