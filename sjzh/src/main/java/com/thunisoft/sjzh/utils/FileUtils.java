/**
 * @projectName jcgzglpt-commons
 * @package com.thunisoft.commons.model
 * @className com.thunisoft.commons.model.FileUtils
 * @copyright Copyright 2018 Thuisoft, Inc. All rights reserved.
 */
package com.thunisoft.sjzh.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * FileUtils
 * @description 文件工具类
 * @author huangyi-1
 * @date 2019年12月27日 下午3:44:38
 * @version v1.0
 */
public final class FileUtils {
	protected static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    private FileUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     *
     * FileUtils
     * @description 获取配置文件信息
     * @param path	文件地址
     * @return
     * @author huangyi-1
     * @date 2019年12月27日 下午8:35:28
     * @version v1.0
     */
    public static String getResourcesText(String path){
    	InputStream is = FileUtils.class.getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String s="";
        StringBuilder configContentStr = new StringBuilder();
        try {
            while((s=br.readLine())!=null) {
            	configContentStr.append(s);
            }
        } catch (IOException e) {
            logger.error("获取资源失败");
        }
        return configContentStr.toString();
    }
}
