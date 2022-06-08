package com.ice.framework.util.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author : tjq
 * @since : 2022-05-11
 */
public class JdkProxy {


    //目标对象
    private Object target = null;

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaceClass, final String serviceVersion) {
        // 创建动态代理对象
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        //执行前before

                        //执行中 invoke
                        Object invoke = method.invoke(target, args);//执行原有的功能

                        //执行后 after


                        return invoke;
                    }
                }
        );
    }

}
