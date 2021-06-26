package com.thunisoft.sjzh.model;


/**
 *
 * CodesConfig
 * @description 代码配置
 * @author huangyi-1
 * @date 2020年6月4日 上午9:48:42
 * @version v1.0
 */
public class CodesConfig {

    /**
     * 码值
     */
    private String code;

    /**
     * 转换后码值
     */
    private String convertCode;

    /**
     * 名称
     */
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getConvertCode() {
        return convertCode;
    }

    public void setConvertCode(String convertCode) {
        this.convertCode = convertCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
