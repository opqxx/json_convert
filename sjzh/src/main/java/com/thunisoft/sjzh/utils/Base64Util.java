package com.thunisoft.sjzh.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 *
 * Base64Util
 * @description base64工具类
 * @author huangyi-1
 * @date 2020年6月3日 上午10:41:45
 * @version v1.0
 */
public class Base64Util {

    private Base64Util(){}


    /**
     *
     * Base64Util
     * @description 获取xml中response
     * @param xmlStr    参数
     * @return  json
     * @throws UnsupportedEncodingException
     * @author huangyi-1
     * @date 2020年6月12日 上午11:06:16
     * @version v1.0
     */
//    public static JSONObject getDecodeXmlResponse(String xmlStr) throws UnsupportedEncodingException{
//        JSONObject resultXmlObj = (JSONObject)Base64Util.decodeBeanXml(Base64Util.decode(xmlStr));
//        return resultXmlObj.getJSONObject("response");
//    }

    /**
     *
     * Base64Util
     * @description base64编码
     * @param text  待处理字符
     * @return  返回编码后字符
     * @throws UnsupportedEncodingException
     * @author huangyi-1
     * @date 2020年6月4日 下午3:00:54
     * @version v1.0
     */
    public static String encode(String text) throws UnsupportedEncodingException{
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] textByte = text.getBytes("UTF-8");
        return encoder.encodeToString(textByte);
    }

    /**
     *
     * Base64Util
     * @description BASE64 解码
     * @param text  待解码字符串
     * @return  返回值
     * @throws UnsupportedEncodingException
     * @author huangyi-1
     * @date 2020年6月4日 下午3:02:13
     * @version v1.0
     */
    public static String decode(String text) throws UnsupportedEncodingException{
        Base64.Decoder decoder = Base64.getDecoder();
        return new String(decoder.decode(text), "UTF-8");
    }

    /**
     *
     * Base64Util
     * @description 编码bean
     * @param bean  bean
     * @return  返回
     * @throws UnsupportedEncodingException
     * @author huangyi-1
     * @date 2020年6月3日 上午10:40:36
     * @version v1.0
     */
    public static Object encodeBean(Object bean) throws UnsupportedEncodingException{
        Object json = JSON.toJSON(bean);
        encodeJson(json);
        return JSON.toJavaObject((JSON)json, bean.getClass());
    }

    /**
     *
     * Base64Util
     * @description 解码beanstr
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     * @author huangyi-1
     * @date 2020年6月8日 下午2:08:07
     * @version v1.0
     */
//    public static JSON decodeBeanXml(String xmlStr) throws UnsupportedEncodingException{
//        String jsonStr = XML.toJSONObject(xmlStr).toString();
//        return  (JSON)decodeBean(JSON.parse(jsonStr));
//    }

    /**
     *
     * Base64Util
     * @description 解码bean
     * @param bean  bean
     * @return  返回
     * @throws UnsupportedEncodingException
     * @author huangyi-1
     * @date 2020年6月3日 上午10:41:22
     * @version v1.0
     */
    public static Object decodeBean(Object bean) throws UnsupportedEncodingException{
        Object json = JSON.toJSON(bean);
        decodeJson(json);
        return JSON.toJavaObject((JSON)json, bean.getClass());
    }

    /**
     *
     * Base64Util
     * @description 编码json
     * @param json  json
     * @throws UnsupportedEncodingException
     * @author huangyi-1
     * @date 2020年6月3日 上午10:38:02
     * @version v1.0
     */
    @SuppressWarnings("rawtypes")
    public static void encodeJson(Object json) throws UnsupportedEncodingException{
        if(json instanceof JSONObject){
            JSONObject obj = (JSONObject) json;
            Iterator iter = obj.keySet().iterator();
            while(iter.hasNext()){
                String key =(String) iter.next();
                Object val = obj.get(key);
                if(val==null){
                    continue;
                }
                if(ConvertUtil.isValueNode(val)){
                    obj.put(key, encode(val.toString()));
                }else {
                    encodeJson(val);
                }
            }
        }
        else if(json instanceof JSONArray){
            JSONArray array = (JSONArray) json;
            for(Object aJson:array){
                encodeJson(aJson);
            }
        }
    }

    /**
     *
     * Base64Util
     * @description 解码json
     * @param json  json
     * @throws UnsupportedEncodingException
     * @author huangyi-1
     * @date 2020年6月3日 上午10:38:22
     * @version v1.0
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void decodeJson(Object json) throws UnsupportedEncodingException{
        if(json instanceof JSONObject){
            JSONObject obj = (JSONObject) json;
            Iterator iter = obj.keySet().iterator();
            while(iter.hasNext()){
                String key =(String) iter.next();
                Object val = obj.get(key);
                if(val==null){
                    continue;
                }
                if(ConvertUtil.isValueNode(val)){
                    if(val instanceof String
                            && StringUtils.isNotBlank((String)val)){
                        obj.put(key, decode(val.toString()));
                    }else if (val instanceof List
                            && CollectionUtils.isNotEmpty((List)val)){
                        List<String> list = (List<String>) val;
                        List<String> cacheList = new ArrayList<>();
                        for(String sqbh:list){
                            cacheList.add(decode(sqbh));
                        }
                        list.clear();
                        list.addAll(cacheList);
                    }
                }else {
                    decodeJson(val);
                }
            }
        }
        else if(json instanceof JSONArray){
            JSONArray array = (JSONArray) json;
            for(Object aJson:array){
                decodeJson(aJson);
            }
        }
    }
}
