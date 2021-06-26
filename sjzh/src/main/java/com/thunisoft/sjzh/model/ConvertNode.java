package com.thunisoft.sjzh.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * ConvertNode
 * @description 数据转换节点
 * @author huangyi-1
 * @date 2020年5月30日 下午4:59:41
 * @version v1.0
 */
public class ConvertNode {

    /**
     * 子节点
     */
    List<Map<String,ConvertNode>> children;

    /**
     * 值
     */
    Object value;

    public List<Map<String, ConvertNode>> getChildren() {
        return children;
    }

    public void setChildren(List<Map<String, ConvertNode>> children) {
        this.children = children;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
