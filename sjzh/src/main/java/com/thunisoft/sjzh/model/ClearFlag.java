package com.thunisoft.sjzh.model;

/**
 *
 * ClearNode
 * @description 清空标记实体
 * @author huangyi-1
 * @date 2020年5月30日 下午6:36:22
 * @version v1.0
 */
public class ClearFlag {

    /**
     * 清空标记
     */
    private boolean isClear;

    /**
     *
     * ClearNode
     * @description 构造方法
     * @author huangyi-1
     * @date 2020年5月30日 下午6:38:27
     * @version v1.0
     */
    public ClearFlag(){
        this.isClear = false;
    }

    public boolean isClear() {
        return isClear;
    }

    public void setClear(boolean clear) {
        isClear = clear;
    }
}
