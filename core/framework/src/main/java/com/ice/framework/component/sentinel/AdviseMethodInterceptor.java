//package com.mg.framework.component.sentinel;
//
//import org.aopalliance.intercept.MethodInterceptor;
//import org.aopalliance.intercept.MethodInvocation;
//
//import javax.annotation.Nonnull;
//import javax.annotation.Nullable;
//
///**
// * @author : tjq
// * @since : 2022-05-13
// */
//public class AdviseMethodInterceptor implements MethodInterceptor {
//    @Nullable
//    @Override
//    public Object invoke(@Nonnull MethodInvocation methodInvocation) throws Throwable {
//        Object result = null;
//        try {
////            logger.info("Before: interceptor name: {}", invocation.getMethod().getName());
////            logger.info("Arguments: {}", jsonMapper.writeValueAsString(invocation.getArguments()));
//            System.out.println("方法执行之前：" + methodInvocation.getMethod().toString());
//            result = methodInvocation.proceed();
//            System.out.println("方法执行之后：" + methodInvocation.getMethod().toString());
//            System.out.println("方法正常运行结果：" + result);
//        } catch (Exception e) {
//            System.out.println("方法出现异常:"+e.toString());
//            System.out.println("方法运行Exception结果："+result);
//            throw  e;
//        }
//        return result;
//    }
//}
