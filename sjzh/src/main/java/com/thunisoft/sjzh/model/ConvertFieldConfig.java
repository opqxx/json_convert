package com.thunisoft.sjzh.model;

import java.util.List;

/**
 *
 * ConvertConfig
 * @description 数据转换配置
 * @author huangyi-1
 * @date 2020年5月30日 下午4:57:33
 * @version v1.0
 */
public class ConvertFieldConfig {

    /**
     * 字段名称
     */
    String name;

    /**
     * 待转换字段
     */
    String field;

    /**
     * 值对应字段名称
     */
    String value;

    /**
     * 绝对值
     */
    Object absoluteValue;

    /**
     * 码值转换
     */
    String codeTranslate;

    /**
     * 日期格式
     */
    List<String> dateFormat;

    /**
     * 三元表达式（判空版）
     */
    List<ConvertTernaryNullConfig> ternaryNull;

    /**
     * 过滤条件
     */
    String where;

    /**
     * 编码类型(base64)
     */
    String encodeType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Object getAbsoluteValue() {
        return absoluteValue;
    }

    public void setAbsoluteValue(Object absoluteValue) {
        this.absoluteValue = absoluteValue;
    }

    public String getCodeTranslate() {
        return codeTranslate;
    }

    public void setCodeTranslate(String codeTranslate) {
        this.codeTranslate = codeTranslate;
    }

    public List<String> getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(List<String> dateFormat) {
        this.dateFormat = dateFormat;
    }

    public List<ConvertTernaryNullConfig> getTernaryNull() {
        return ternaryNull;
    }

    public void setTernaryNull(List<ConvertTernaryNullConfig> ternaryNull) {
        this.ternaryNull = ternaryNull;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getEncodeType() {
        return encodeType;
    }

    public void setEncodeType(String encodeType) {
        this.encodeType = encodeType;
    }
}
