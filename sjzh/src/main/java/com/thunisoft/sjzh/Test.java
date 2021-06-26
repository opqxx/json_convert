/**
 * @projectName sjzh
 * @package com.thunisoft.sjzh
 * @className com.thunisoft.sjzh.Test
 * @copyright Copyright 2020 Thunisoft, Inc All rights reserved.
 */
package com.thunisoft.sjzh;

import com.thunisoft.sjzh.utils.ConvertUtil;
import com.thunisoft.sjzh.utils.FileUtils;

/**
 * Test
 * @description 测试类
 * @author lichengyong
 * @date 2021-4-29 16:13
 * @version 1.0
 */
public class Test {

    public  static void main(String[] args){
        String orgJson = FileUtils.getResourcesText("/data/test1.json");
        String convertConf = FileUtils.getResourcesText("/template/sjzh_test1.json");
        System.out.println(orgJson);
        System.out.println(convertConf);
        System.out.println(ConvertUtil.convertData(orgJson,convertConf));
    }
}