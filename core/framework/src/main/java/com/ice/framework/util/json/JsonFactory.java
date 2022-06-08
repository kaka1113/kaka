package com.ice.framework.util.json;

import com.alibaba.fastjson.JSON;
import com.ice.framework.exception.MgException;
import com.ice.framework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author : tjq
 * @since : 2022/1/20 9:48
 */
public final class JsonFactory {

    /**
     * 容器
     */
    private static final ConcurrentMap<JsonEnum, IJson> iJsonConcurrentMap = new ConcurrentHashMap<>();

    /**
     * 实例化工厂
     */
    private static final JsonFactory instance = new JsonFactory();

    /**
     * 工厂安装机器
     */
    private JsonFactory() {
        iJsonConcurrentMap.put(JsonEnum.FAST, new FastJsonUtil());
        iJsonConcurrentMap.put(JsonEnum.SPRING, new SpringJsonUtil());
        iJsonConcurrentMap.put(JsonEnum.JACKSON, new JacksonUtil());
    }

    /**
     * 默认序列化方式
     *
     * @return
     */
    public static IJson jsonTools() {
        return getIJson(JsonEnum.FAST);
    }

    /**
     * 指定序列化方式
     *
     * @param type
     * @return
     */
    public static IJson jsonTools(JsonEnum type) {
        return getIJson(type);
    }

    /**
     * 工厂加工产品
     *
     * @param type
     * @return
     */
    private static IJson getIJson(JsonEnum type) {
        IJson iJson = iJsonConcurrentMap.get(type);
        if (ObjectUtils.isNotEmpty(iJson)) {
            return iJson;
        }
        throw new MgException(10000, "不支持序列化方式");
    }


    public static void main(String[] args) {
        Test test = new Test();
        test.setTest("aa");
        Test test22 = new Test();
        test22.setTest("aa");
        Test test33 = new Test();
        test33.setTest("aa");

        Test2 test2 = new Test2();

        /**
         * 对象拷贝
         */
        JsonFactory.jsonTools(JsonEnum.FRAMEWORK).convert(test, test2);

        /**
         * 数组拷贝
         */
        List<Test> tests = JsonFactory.jsonTools().copyList(Arrays.asList(test, test22, test33), Test.class);

        /**
         * 对象转json
         */
        String json = JsonFactory.jsonTools().getJson(test);

        /**
         * 数组转JSON
         */
        String s = JsonFactory.jsonTools().listToJson(Arrays.asList(test, test22, test33));

        /**
         * 字符串转对象
         */

        Test2 object = JsonFactory.jsonTools(JsonEnum.SPRING).getObject(JSON.toJSONString(test), Test2.class);

        /**
         * 字符串转数组
         */
        List<Test2> test2s = JsonFactory.jsonTools(JsonEnum.SPRING).jsonToList(JSON.toJSONString(Arrays.asList(test, test22, test33)), Test2.class);


        System.out.println("");
    }


    static class Test {

        public Test() {
        }

        private String test;

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }
    }

    static class Test2 {

        public Test2() {
        }

        private String test;

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }
    }

}
