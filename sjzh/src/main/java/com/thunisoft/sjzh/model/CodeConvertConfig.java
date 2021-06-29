package com.thunisoft.sjzh.model;

import org.apache.commons.lang3.StringUtils;

import java.util.List;



/**
 *
 * CodeConvertConfig
 * @description 码值转换
 * @author huangyi-1
 * @date 2020年6月4日 上午9:51:38
 * @version v1.0
 */
public class CodeConvertConfig {

    private String name;

    private List<CodesConfig> codes;


    /**
     *
     * CodeConvertConfig
     * @description 代码转换方法
     * @param code
     * @return
     * @author huangyi-1
     * @date 2020年6月4日 上午9:51:01
     * @version v1.0
     */
    public String convertCode(String code){
        for(CodesConfig codeConfig:codes){
            if(StringUtils.equals(codeConfig.getCode(),code)){
                return codeConfig.getConvertCode();
            }
        }
        return code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CodesConfig> getCodes() {
        return codes;
    }

    public void setCodes(List<CodesConfig> codes) {
        this.codes = codes;
    }
}
