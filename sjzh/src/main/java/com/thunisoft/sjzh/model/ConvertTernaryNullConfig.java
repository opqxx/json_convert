package com.thunisoft.sjzh.model;


/**
 *
 * ConvertTernaryConfig
 * @description 三元表达式配置（判空）
 * @author huangyi-1
 * @date 2020年6月10日 下午2:30:17
 * @version v1.0
 */
public class ConvertTernaryNullConfig {

    /**
     * 比较字段名
     */
    private String field;

    /**
     * 结果值
     */
    private String result;

    /**
     * 结果值字段
     */
    private String valueKey;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getValueKey() {
        return valueKey;
    }

    public void setValueKey(String valueKey) {
        this.valueKey = valueKey;
    }
}
