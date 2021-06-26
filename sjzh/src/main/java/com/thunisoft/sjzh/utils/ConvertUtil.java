package com.thunisoft.sjzh.utils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import com.thunisoft.sjzh.model.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 *
 * ConvertUtil
 * @description 数据转换工具
 * @author huangyi-1
 * @date 2020年5月30日 下午5:26:48
 * @version v1.0
 */
public class ConvertUtil {

    /**
     * 分隔符
     */
    private static final String SEPARATE = "\\.";

    /**
     * 干涉字段
     */
    private static final String OBJINTERFERENCEFIELD = "ConvertUtil-OBJINTERFERENCEFIELD";

    /**
     *
     * ConvertUtil
     *
     * @description 转换数据-默认清空空节点
     * @param orgJson 待转换数据
     * @param convertConfigStr 转换配置
     * @return java.lang.String
     * @date 2021-4-29 16:19
     * @author huangyi-1
     * @version 1.0
     */
    public static String convertData(String orgJson,String convertConfigStr){
        return convertData(orgJson,convertConfigStr, true);
    }


    /**
     *
     * ConvertUtil
     * @description 数据转换
     * @param orgJson   原数据JSON字符串
     * @param convertConfigStr  转换配置字符串
     * @param clearAllEmpty  是否清空所有空字段
     * @return  转换后字符串
     * @author huangyi-1
     * @date 2020年5月30日 下午5:26:05
     * @version v1.0
     */
    @SuppressWarnings("rawtypes")
    public static String convertData(String orgJson,String convertConfigStr, boolean clearAllEmpty){

        /** 解析源数据 */
        ConvertNode baseNode = new ConvertNode();
        initConvertNode(baseNode,JSON.parse(orgJson));

        /** 根据模板转换数据 */
        ConvertConfig convertConfig = JSON.parseObject(convertConfigStr,ConvertConfig.class);
        Map<String,ConvertNode> nodeCacheMap = new HashMap<>();
        Object result = new ArrayList<Map<String,Object>>();
        creatResult(baseNode, nodeCacheMap,"", convertConfig,result);

        /** 过滤数据清空空数据 */
        clearEmptyValue(result, clearAllEmpty);

        /** 过滤处理空节点 */
        ClearFlag clearFlg = new ClearFlag();
        while(!clearFlg.isClear()){
            clearFlg.setClear(true);
            clearEmptyNode(result,clearFlg);
            if((result instanceof Map
                    && ((Map)result).size()==0)
                    || (result instanceof List
                            && ((List)result).size()==0)){
                break;
            }
        }

        /** 增加干涉字段 */
        if(!CollectionUtils.isEmpty(convertConfig.getArryNode())){
            addGszd(result);
        }

        /** 合并节点，重新构建数据结构 */
        ClearFlag mergeFlg = new ClearFlag();
        while(!mergeFlg.isClear()){
            mergeFlg.setClear(true);
            Object newResult = mergeRepeatData(result,mergeFlg, "", convertConfig);
            if(newResult!=null){
                result = newResult;
            }
        }

        /** 清除干涉字段节点 */
        clearGszd(result);

        return JSON.toJSON(result).toString();
    }

    /**
     *
     * ConvertUtil
     * @description 清除干涉字段
     * @param targetObj 目标对象
     * @author huangyi-1
     * @date 2020年6月3日 下午6:36:03
     * @version v1.0
     */
    @SuppressWarnings("rawtypes")
    public static void clearGszd(Object targetObj){
        if(targetObj instanceof List){
            for(Object obj:(List)targetObj){
                clearGszd(obj);
            }
        }
        else if(targetObj instanceof Map){
            ((Map)targetObj).remove(OBJINTERFERENCEFIELD);
            Iterator it = ((Map)targetObj).keySet().iterator();
            while(it.hasNext()){
                String key = (String)it.next();
                Object value = ((Map)targetObj).get(key);
                if(value instanceof Map
                        || value instanceof List){
                    clearGszd(value);
                }
            }
        }
    }

    /**
     *
     * ConvertUtil
     * @description 增加干涉字段
     * @param targetObj
     * @author huangyi-1
     * @date 2020年6月3日 下午9:24:25
     * @version v1.0
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void addGszd(Object targetObj){
        if(targetObj instanceof List){
            for(Object obj:(List)targetObj){
                addGszd(obj);
            }
        }
        else if(targetObj instanceof Map){
            ((Map)targetObj).put(OBJINTERFERENCEFIELD,1);
            Iterator it = ((Map)targetObj).keySet().iterator();
            while(it.hasNext()){
                String key = (String)it.next();
                Object value = ((Map)targetObj).get(key);
                if(value instanceof Map
                        || value instanceof List){
                    addGszd(value);
                }
            }
        }
    }

    /**
     *
     * ConvertUtil
     * @description 生成数据源节点
     * @param node  节点
     * @param json  数据源JSON： Map 或者 List<Map>
     * @author huangyi-1
     * @date 2020年5月30日 下午5:02:51
     * @version v1.0
     */
    @SuppressWarnings("rawtypes")
    private static void initConvertNode(ConvertNode node, Object json){
        //对象
        if(json instanceof JSONObject){
            JSONObject obj = (JSONObject) json;
            Iterator iter = obj.keySet().iterator();

            //给父节点赋值
            if(node.getChildren()==null){
                node.setChildren(new ArrayList<>());
            }
            Map<String,ConvertNode> nodeMap = new HashMap<>();

            while(iter.hasNext()){
                String key =(String) iter.next();
                Object val = obj.get(key);

                if(val == null
                        || (val instanceof List
                                && ((List)val).size()==0)){
                    continue;
                }

                ConvertNode cNode = new ConvertNode();
                if(isValueNode(val)){
                    //数组字段拼接为字符串
                    if (val instanceof List){
                        StringBuffer sb = new StringBuffer();
                        for(Object str:(List)val){
                            if(sb.length()>0){
                                sb.append(",");
                            }
                            sb.append(str);
                        }
                        val = sb.toString();
                    }
                    cNode.setValue(val);
                }
                else {
                    initConvertNode(cNode,val);
                }

                nodeMap.put(key, cNode);
            }
            node.getChildren().add(nodeMap);
        }
        //数组
        else if(json instanceof JSONArray){
            JSONArray array = (JSONArray) json;
            for(Object aJson:array){
                if(!isValueNode(aJson)){
                    initConvertNode(node,aJson);
                }
            }
        }
    }

    /**
     *
     * ConvertUtil
     * @description 判断对象是否是值节点
     * @param node  待判断节点
     * @return  布尔值
     * @author huangyi-1
     * @date 2020年5月30日 下午5:04:15
     * @version v1.0
     */
    @SuppressWarnings("rawtypes")
    public static boolean isValueNode(Object node){
        if(node instanceof JSONObject){
            return false;
        }else if (node instanceof List){
            for(Object obj:(List)node){
                if(obj instanceof JSONObject
                        || obj instanceof JSONArray){
                    return false;
                }
            }
        }
        return true;
    }


    /**
     *
     * ConvertUtil
     * @description 根据数据源及模板生成返回值
     * @param baseNode  数据源节点
     * @param nodeCacheMap  节点缓存
     * @param pName 上级节点名称
     * @param convertConfig 转换配置
     * @param result    结果集
     * @author huangyi-1
     * @date 2020年5月30日 下午5:05:55
     * @version v1.0
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void creatResult(ConvertNode baseNode, Map<String,ConvertNode> nodeCacheMap,
            String pName, ConvertConfig convertConfig,Object result){
        List<Map<String, ConvertNode>> childrens = baseNode.getChildren();
        if(childrens==null){
            return;
        }
        for(Map<String, ConvertNode> childern : childrens){
            //遍历当前层，判断是否是最底层
            Iterator<String> childIterator = childern.keySet().iterator();
            //有下一层的节点
            Map<String,ConvertNode> nextsLayerNodes = new LinkedHashMap<>();

            //循环当前层节点
            List<String> curLayerNodeNames = new ArrayList<>();
            while(childIterator.hasNext()){
                String key = (String)childIterator.next();
                ConvertNode node = childern.get(key);
                //当前节点全名
                String name = key;
                if(StringUtils.isNotBlank(pName)){
                    name = pName+"."+key;
                }

                //值节点
                if(node.getValue()!=null){
                    nodeCacheMap.put(name, node);
                    curLayerNodeNames.add(name);
                }
                //拥有下一层的节点
                else {
                    nextsLayerNodes.put(name, node);
                }
            }

            //有下一层节点的情况，进入下一层
            if(nextsLayerNodes.size()>0){
                Iterator<String> iterator = nextsLayerNodes.keySet().iterator();
                while(iterator.hasNext()){
                    String name = iterator.next();
                    creatResult(nextsLayerNodes.get(name),nodeCacheMap, name,convertConfig,result);
                }
            }
            // 当前层是最下层，进行设值
            else {
                List<String> layerKey = new ArrayList<>();

                //判断最终结果形态
                Map<String,Object> cacheResult = null;
                if(result instanceof List){
                    cacheResult = new LinkedHashMap<>();
                    ((List) result).add(cacheResult);
                }else {
                    cacheResult = (Map<String,Object>)result;
                }

                for(ConvertFieldConfig outField:convertConfig.getFieldConfig()){
                    //键
                    String fieldKey = outField.getField();
                    //值
                    String valueKey = outField.getValue();
                    ConvertNode value = null;

                    if(valueKey!=null){
                        String[] valueKeys = valueKey.split(",");
                        for(String vk:valueKeys){
                            if(nodeCacheMap.get(vk)!=null){
                                value = nodeCacheMap.get(vk);
                                break;
                            }
                        }
                    }else if(outField.getAbsoluteValue()!=null){
                        value = new ConvertNode();
                        value.setValue(outField.getAbsoluteValue());
                    }else if(!CollectionUtils.isEmpty(outField.getTernaryNull())){
                        List<ConvertTernaryNullConfig> ternaryNulls = outField.getTernaryNull();
                        value = new ConvertNode();
                        value.setValue(getTernaryNullResult(nodeCacheMap,ternaryNulls));
                    }
                    String[] skey = fieldKey.split(SEPARATE);

                    // 数据处理
                    if(value!=null && value.getValue()!=null){
                        ConvertNode convertNode = new ConvertNode();
                        //判断过滤条件
                        if(!verifyWhere(nodeCacheMap,outField.getWhere())){
                            convertFieldData(cacheResult, skey, 0, convertNode, layerKey);
                            continue;
                        }

                        convertNode.setValue(String.valueOf(value.getValue()));
                        // 码值转换处理
                        if(StringUtils.isNotBlank(outField.getCodeTranslate())){
                            String codeTranslateStr = FileUtils.getResourcesText(outField.getCodeTranslate());
                            CodeConvertConfig codeConvertConfig = JSONObject.parseObject(codeTranslateStr, CodeConvertConfig.class);
                            convertNode.setValue(String.valueOf(codeConvertConfig.convertCode(value.getValue().toString())));
                        }
                        //日期格式处理
                        else if (!CollectionUtils.isEmpty(outField.getDateFormat())){
                            try{
                                String orgFormat = outField.getDateFormat().get(0);
                                Date date = null;
                                if(StringUtils.equals(orgFormat, "long")){
                                    date = new Date(Long.parseLong(String.valueOf(value.getValue())));
                                }else {
                                    date = new SimpleDateFormat(orgFormat).parse((String)value.getValue());
                                }
                                String destFormat = outField.getDateFormat().get(1);
                                if(StringUtils.equals(destFormat, "LocalDateTime")){
                                    Instant instant = date.toInstant();
                                    ZoneId zoneId = ZoneId.systemDefault();
                                    LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
                                    convertNode.setValue(localDateTime);
                                }else if(StringUtils.equals(destFormat, "LocalDate")){
                                    Instant instant = date.toInstant();
                                    ZoneId zoneId = ZoneId.systemDefault();
                                    LocalDate localDate = instant.atZone(zoneId).toLocalDate();
                                    convertNode.setValue(localDate);
                                }else {
                                    convertNode.setValue(String.valueOf(new SimpleDateFormat(destFormat).format(date)));
                                }
                            }catch(Exception e){
                                convertNode.setValue("");
                            }
                        }
                        if(StringUtils.equals(outField.getEncodeType(), "base64")){
                            try {
                                convertNode.setValue(Base64Util.encode(value.getValue().toString()));
                            } catch (UnsupportedEncodingException e) {
                            }
                        }
                        //转换当前层数据
                        convertFieldData(cacheResult, skey, 0, convertNode, layerKey);
                    }else {
                        //转换当前层数据
                        convertFieldData(cacheResult, skey, 0, value, layerKey);
                    }
                }
            }

            //清空当前层缓存
            for(String name:curLayerNodeNames){
                nodeCacheMap.remove(name);
            }
        }
    }

    /**
     *
     * ConvertUtil
     *
     * @description 条件判断，目前仅仅简单实现，后期可更换更优方案
     * @param nodeCacheMap
     * @param where
     * @return boolean
     * @date 2021-4-29 17:00
     * @author huangyi-1
     * @version 1.0
     */
    private static boolean verifyWhere(Map<String, ConvertNode> nodeCacheMap , String where){
        if(StringUtils.isNotBlank(where)){
            String[] conditions = where.split(",");
            for(String condition:conditions){
                if(condition.contains("==")){
                    String val1 = getVarValue(nodeCacheMap,condition.split("==")[0]);
                    String val2 = getVarValue(nodeCacheMap,condition.split("==")[1]);
                    return StringUtils.equals(val1, val2);
                }else if(condition.contains("!=null")){
                    String val1 = getVarValue(nodeCacheMap,condition.split("==")[0]);
                    return StringUtils.isNotBlank(val1);
                }
            }
        }
        return true;
    }
    /**
     *
     * ConvertUtil
     *
     * @description 获取变量值
     * @param nodeCacheMap
     * @param var
     * @return java.lang.String
     * @date 2021-4-29 17:00
     * @author huangyi-1
     * @version 1.0
     */
    public static String getVarValue(Map<String, ConvertNode> nodeCacheMap, String var){
        if(var.startsWith("'")&&var.endsWith("'")){
            return var.substring(1, var.length()-1);
        }
        if(nodeCacheMap.get(var)!=null){
            return String.valueOf(nodeCacheMap.get(var).getValue());
        }
        return null;
    }

    /**
     *
     * ConvertUtil
     * @description 三元表达式执行结果（判空版）
     * @param nodeCacheMap
     * @param ternaryNulls
     * @return
     * @author huangyi-1
     * @date 2020年6月10日 下午2:46:47
     * @version v1.0
     */
    private static Object getTernaryNullResult(Map<String, ConvertNode> nodeCacheMap, List<ConvertTernaryNullConfig> ternaryNulls) {
        for(ConvertTernaryNullConfig config:ternaryNulls){
            if(StringUtils.isBlank(config.getField())){
                return getTernaryResult(nodeCacheMap, config);
            }
            ConvertNode value = nodeCacheMap.get(config.getField());
            if(value!=null
                    && StringUtils.isNotBlank(String.valueOf(value.getValue()))
                    && !StringUtils.equals(String.valueOf(value.getValue()), "null")
                    && !StringUtils.equals(String.valueOf(value.getValue()), "NULL")){
                return getTernaryResult(nodeCacheMap, config);
            }
        }
        return null;
    }

    /**
     *
     * ConvertUtil
     * @description 获取三元表达式结果值
     * @param nodeCacheMap
     * @param config
     * @return
     * @author huangyi-1
     * @date 2020年6月17日 上午11:42:12
     * @version v1.0
     */
    private static Object getTernaryResult(Map<String, ConvertNode> nodeCacheMap, ConvertTernaryNullConfig config){
        if(StringUtils.isNotBlank(config.getValueKey())){
            String[] valueKeys = config.getValueKey().split(",");
            for(String vk:valueKeys){
                if(nodeCacheMap.get(vk)!=null){
                    return nodeCacheMap.get(vk).getValue();
                }
            }
        }else{
            return config.getResult();
        }
        return null;
    }


    /**
     *
     * ConvertUtil
     * @description 转换字段数据
     * @param result    转换后结果集
     * @param sKey  转后后生成字段名拆分
     * @param level 当前处理层级
     * @param value 数据源值
     * @param layerKey  已处理层级键
     * @author huangyi-1
     * @date 2020年5月30日 下午5:10:14
     * @version v1.0
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void convertFieldData(Map<String,Object> result, String[] sKey, int level, ConvertNode value, List<String> layerKey) {
        if(sKey.length<=level){
            return;
        }

        //当前层全名
        String curLayerName = sKey[0];
        for(int i=1;i<=level;i++){
            curLayerName = curLayerName+"."+sKey[i];
        }

        //是否最底层
        String key = sKey[level];
        if(sKey.length==level+1){
            if(value==null){
                result.put(key, "");
            }else {
                result.put(key, value.getValue());
            }
        }else {
            //未建立当前层级
            Object orgValue = result.get(key);
            if(orgValue == null){
                result.put(key, new LinkedHashMap<String,Object>());
                layerKey.add(curLayerName);
            }

            //已建立当前层级，并判断当前层是否是本次循环创建，否则新增队列
            else if(!layerKey.contains(curLayerName)){
                Map<String,Object> newItem = new LinkedHashMap<>();
                if(!(orgValue instanceof List)){
                    List<Map<String,Object>> list = new ArrayList<>();
                    list.add((Map<String,Object>)result.get(key));
                    list.add(newItem);
                    result.put(key, list);
                }else {
                    List<Map<String,Object>> list = (List)result.get(key);
                    list.add(newItem);
                }
                layerKey.add(curLayerName);
            }

            //递归下一层级设值
            Object curLayer = result.get(key);
            Map<String,Object> nextLayer = null;
            if(curLayer instanceof List){
                nextLayer = (Map<String,Object>)((List)curLayer).get(((List)curLayer).size()-1);
            }else {
                nextLayer = (Map<String,Object>)curLayer;
            }
            convertFieldData(nextLayer, sKey, level+1, value, layerKey);

        }
    }

    /**
     *
     * ConvertUtil
     * @description 清空对象中空数据
     * @param targetObj   目标对象
     * @author huangyi-1
     * @date 2020年5月30日 下午5:12:06
     * @version v1.0
     */
    @SuppressWarnings("rawtypes")
    private static void clearEmptyValue(Object targetObj, boolean clearAllEmpty){
        if(targetObj instanceof List){
            for(Object obj:(List)targetObj){
                clearEmptyValue(obj, clearAllEmpty);
            }
        }
        else if(targetObj instanceof Map){
            Iterator it = ((Map)targetObj).keySet().iterator();
            //当前层如果有其它值，则空基础值不清空
            List<String> emptyObjKeys = new ArrayList<>();
            List<String> emptyValKeys = new ArrayList<>();
            boolean isEmptyLayer = true;
            while(it.hasNext()){
                String key = (String)it.next();
                Object value = ((Map)targetObj).get(key);
                if(value instanceof String
                        && StringUtils.isBlank(((String)value))){
                    emptyValKeys.add(key);
                }else if(value instanceof List){
                    if(((List)value).size()==0){
                        emptyObjKeys.add(key);
                    }else {
                        clearEmptyValue(value, clearAllEmpty);
                    }
                }else if(value instanceof Map){
                    if(((Map)value).size()==0){
                        emptyObjKeys.add(key);
                    }else {
                        clearEmptyValue(value, clearAllEmpty);
                    }
                }else if(value == null){
                    emptyValKeys.add(key);
                }

                // 判断当前层是否有基础值
                if(isEmptyLayer && !(value instanceof List || value instanceof Map)
                        && ((value instanceof String
                                && !StringUtils.isBlank(((String)value)))
                                || (!(value instanceof String)
                                        && value!=null))){
                    isEmptyLayer = false;
                }
            }
            for(String emptyKey:emptyObjKeys){
                ((Map)targetObj).remove(emptyKey);
            }
            if(isEmptyLayer || clearAllEmpty){
                for(String emptyKey:emptyValKeys){
                    ((Map)targetObj).remove(emptyKey);
                }
            }
        }
    }

    /**
     *
     * ConvertUtil
     * @description 删除空节点
     * @param targetObj 目标对象
     * @author huangyi-1
     * @date 2020年5月30日 下午6:01:19
     * @version v1.0
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static boolean clearEmptyNode(Object targetObj, ClearFlag clearFlg){
        if(targetObj instanceof List){
            if(((List) targetObj).size()==0){
                clearFlg.setClear(false);
                return false;
            }
            List newObj = new ArrayList<>();
            for(Object obj:(List)targetObj){
                if(clearEmptyNode(obj,clearFlg)){
                    newObj.add(obj);
                }else {
                    clearFlg.setClear(false);
                }
            }
            ((List) targetObj).removeAll((List)targetObj);
            ((List) targetObj).addAll(newObj);
        }
        else if(targetObj instanceof Map){
            if(((Map)targetObj).size()==0){
                clearFlg.setClear(false);
                return false;
            }
            Iterator it = ((Map)targetObj).keySet().iterator();
            List<String> removeKeys = new ArrayList<>();
            while(it.hasNext()){
                String key = (String)it.next();
                Object value = ((Map)targetObj).get(key);
                if((value instanceof Map
                        || value instanceof List)
                        &&!clearEmptyNode(value,clearFlg)){
                    removeKeys.add(key);
                }
            }
            for(String emptyKey:removeKeys){
                ((Map)targetObj).remove(emptyKey);
            }
        }

        return true;
    }

    /**
     *
     * ConvertUtil
     * @description 合并重复数据
     * @param targetObj
     * @author huangyi-1
     * @date 2020年6月1日 上午10:45:49
     * @version v1.0
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Object mergeRepeatData(Object targetObj, ClearFlag clearFlg, String pKey, ConvertConfig convertConfig){
        if(targetObj instanceof List){
            //合并当前层
            Object newObj = mergeList((List)targetObj, pKey, convertConfig);
            if(newObj!=null){
                clearFlg.setClear(false);
                 return newObj;
            }

            //处理下级
            List newList = new ArrayList<>();
            for(Object obj:(List)targetObj){
                Object subObj = mergeRepeatData(obj, clearFlg, pKey, convertConfig);
                newList.add(subObj);
            }

            ((List) targetObj).removeAll((List)targetObj);
            ((List) targetObj).addAll(newList);
            return newList;
        }
        else if(targetObj instanceof Map){
            //先处理当前层，判断是否需要拆分为数组
            Object newObj = mergeMap((Map)targetObj, pKey, convertConfig);
            if(newObj!=null){
                clearFlg.setClear(false);
                return newObj;
            }

            //处理下级元素
            Iterator it = ((Map)targetObj).keySet().iterator();
            while(it.hasNext()){
                String key = (String)it.next();
                Object value = ((Map)targetObj).get(key);
                String curPkey = pKey;
                if(StringUtils.isEmpty(curPkey)){
                    curPkey = key;
                }else {
                    curPkey = curPkey+"."+key;
                }
                if(value instanceof Map
                        || value instanceof List){
                    ((Map)targetObj).put(key, mergeRepeatData(value,clearFlg, curPkey, convertConfig));
                }
            }
        }

        return targetObj;
    }


    /**
     *
     * ConvertUtil
     * @description 合并数组
     * @param targetList    目标集合
     * @return  结果
     * @author huangyi-1
     * @date 2020年6月1日 下午3:39:51
     * @version v1.0
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Object mergeList(List targetList , String pKey, ConvertConfig convertConfig){
        if(CollectionUtils.isEmpty(targetList)){
            return null;
        }

        List<Map<Object,Object>> resultList = new ArrayList<>();
        for(Object obj:(List)targetList){
            if(obj instanceof Map){
                //遍历每一条数据，根节点值与父节点分开存放
                Iterator it = ((Map)obj).keySet().iterator();
                Map<Object, Object> headLayer = new LinkedHashMap<>();
                Map<Object, Object> bodyLayer = new LinkedHashMap<>();
                while(it.hasNext()){
                    String key = (String)it.next();
                    Object value = ((Map)obj).get(key);
                    if(value instanceof Map
                            || value instanceof List){
                        bodyLayer.put(key, value);
                    }else {
                        headLayer.put(key, value);
                    }
                }
                if(headLayer.size()==0){
                    resultList.add(bodyLayer);
                    continue;
                }

                //比较结果集中是否包含当前数据根节点,并合并
                boolean isContains = false;
                for(Map dest:resultList){
                    if(!isContansMap(headLayer,dest)){
                        continue;
                    }
                    Iterator bodys = bodyLayer.keySet().iterator();
                    while(bodys.hasNext()){
                        String key = (String)bodys.next();
                        Object value = bodyLayer.get(key);
                        if(value instanceof List){
                            dest.put(key, value);
                            continue;
                        }
                        List destValue = (List)dest.get(key);
                        if(destValue==null){
                            destValue = new ArrayList<>();
                            dest.put(key, destValue);
                        }
                        destValue.add(value);
                    }
                    isContains=true;
                }
                if(!isContains){
                    Iterator bodys = bodyLayer.keySet().iterator();
                    while(bodys.hasNext()){
                        String key = (String)bodys.next();
                        Object value = bodyLayer.get(key);
                        if(value instanceof List){
                            headLayer.put(key, value);
                            continue;
                        }
                        List destValue = new ArrayList<>();
                        destValue.add(value);
                        headLayer.put(key, destValue);
                    }
                    resultList.add(headLayer);
                }
            }
        }

        if(resultList.size()==1 && !CollectionUtils.isEmpty(convertConfig.getArryNode())
                && !convertConfig.getArryNode().contains(pKey)){
            Map<Object, Object> result = resultList.get(0);
            sortMapByConfig(pKey, convertConfig, result);
            return result;
        }

        //返回处理结果
        if(resultList.size() == targetList.size()){
            return null;
        }

        sortListMapByConfig(pKey, convertConfig, resultList);
        return resultList;
    }

    /**
     *
     * ConvertUtil
     * @description map按照配置字段进行排序
     * @param pKey
     * @param convertConfig
     * @param list
     * @author huangyi-1
     * @date 2020年7月1日 下午3:12:40
     * @version v1.0
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void sortListMapByConfig(String pKey, ConvertConfig convertConfig, List list){
        for(Object obj:list){
            if(obj instanceof Map){
                sortMapByConfig(pKey, convertConfig, (Map<Object, Object>)obj);
            }
        }
    }

    /**
     *
     * ConvertUtil
     * @description map按照配置字段进行排序
     * @param pKey
     * @param convertConfig
     * @param map
     * @author huangyi-1
     * @date 2020年7月1日 下午3:10:21
     * @version v1.0
     */
    private static void sortMapByConfig(String pKey, ConvertConfig convertConfig, Map<Object, Object> map){
        Map<Object, Object> cacheMap = new LinkedHashMap<>();
        for(ConvertFieldConfig config:convertConfig.getFieldConfig()){
            String field = getCurField(config.getField(),pKey);
            if(StringUtils.isNotBlank(field)){
                cacheMap.put(field, map.get(field));
            }
        }
        map.clear();
        Iterator<Object> iterator = cacheMap.keySet().iterator();
        while(iterator.hasNext()){
            Object key = iterator.next();
            Object value = cacheMap.get(key);
            map.put(key, value);
        }
    }

    /**
     *
     * ConvertUtil
     * @description 获取当前层级field
     * @param field
     * @param pKey
     * @return
     * @author huangyi-1
     * @date 2020年7月1日 下午3:05:33
     * @version v1.0
     */
    private static String getCurField(String field, String pKey){
        if(StringUtils.isBlank(field)){
            return null;
        }else if(!field.contains(".")){
            return field;
        }else if(StringUtils.isBlank(pKey)){
            return field.substring(0, field.indexOf("."));
        } else if(field.startsWith(pKey)&&field.length()>pKey.length()){
            String curField = field.substring(pKey.length()+1,field.length());
            if(curField.contains(".")){
                curField = curField.substring(0,curField.indexOf("."));
            }
            return curField;
        }
        return null;
    }

    /**
     *
     * ConvertUtil
     * @description 判断集合是否包含集合
     * @param target   目标集合
     * @param dest     对比集合
     * @return  结果
     * @author huangyi-1
     * @date 2020年6月1日 下午3:28:03
     * @version v1.0
     */
    @SuppressWarnings({ "rawtypes" })
    private static boolean isContansMap(Map target,Map dest){
        if(target==null || dest==null
                || target.size()<=0 || dest.size()<=0){
            return false;
        }
        Iterator it = target.keySet().iterator();
        while(it.hasNext()){
            String key = (String)it.next();
            Object targetValue = target.get(key);
            Object destValue = dest.get(key);
            if(targetValue==null && destValue==null){
                continue;
            }
            if(targetValue==null || destValue==null
                    || !targetValue.equals(destValue)){
                return false;
            }
        }
        return true;
    }

    /**
     *
     * ConvertUtil
     * @description 合并集合
     * @param targetMap 集合对象
     * @return  结果
     * @author huangyi-1
     * @date 2020年6月1日 下午3:54:52
     * @version v1.0
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Object mergeMap(Map targetMap, String pKey, ConvertConfig convertConfig){
        Iterator it = targetMap.keySet().iterator();
        Map<Object, Object> headLayer = new LinkedHashMap<>();
        Map<Object, Object> bodyLayer = new LinkedHashMap<>();
        Map<Object, Object> listLayer = new LinkedHashMap<>();
        while(it.hasNext()){
            String key = (String)it.next();
            Object value = targetMap.get(key);
            String curKey = pKey+"."+key;
            if(value instanceof Map && convertConfig.getArryNode().contains(curKey)){
                bodyLayer.put(key, value);
            }else if(value instanceof List){
                listLayer.put(key, value);
            }else {
                headLayer.put(key, value);
            }
        }
        if(headLayer.size()==0 || bodyLayer.size()==0){
            return null;
        }

        Iterator bodyIt = bodyLayer.keySet().iterator();
        while(bodyIt.hasNext()){
            String key = (String)bodyIt.next();
            Object value = bodyLayer.get(key);
            List newBody = new ArrayList<>();
            newBody.add(value);
            headLayer.put(key, newBody);
        }

        Iterator listIt = listLayer.keySet().iterator();
        while(listIt.hasNext()){
            String key = (String)listIt.next();
            Object value = listLayer.get(key);
            headLayer.put(key, value);
        }
        sortMapByConfig(pKey, convertConfig, headLayer);
        return headLayer;
    }
}
