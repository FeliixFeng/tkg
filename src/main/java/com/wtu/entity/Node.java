package com.wtu.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 节点类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Node {

    // 节点ID
    private Long id;

    // 节点名
    private String name;

    // 父节点ID
    private Integer parentId;

    // 子节点列表
    @Builder.Default
    private List<Node> children = new ArrayList<Node>();

}
