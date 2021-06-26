package com.thunisoft.sjzh.model;

import java.util.List;

/**
 *
 * ConvertConfig
 * @description 转换工具配置
 * @author huangyi-1
 * @date 2020年6月3日 下午7:53:35
 * @version v1.0
 */
public class ConvertConfig {

    /**
     * 数组节点
     */
    List<String> arryNode;

    /**
     * 字段配置
     */
    List<ConvertFieldConfig> fieldConfig;

    public List<String> getArryNode() {
        return arryNode;
    }

    public void setArryNode(List<String> arryNode) {
        this.arryNode = arryNode;
    }

    public List<ConvertFieldConfig> getFieldConfig() {
        return fieldConfig;
    }

    public void setFieldConfig(List<ConvertFieldConfig> fieldConfig) {
        this.fieldConfig = fieldConfig;
    }
}
