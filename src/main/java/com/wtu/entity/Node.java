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

    // 子节点列表
    private List<Node> children = new ArrayList<Node>();

}
