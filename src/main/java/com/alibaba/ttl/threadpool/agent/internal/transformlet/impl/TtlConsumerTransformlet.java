package com.alibaba.ttl.threadpool.agent.internal.transformlet.impl;

import java.io.IOException;

import com.alibaba.ttl.threadpool.agent.internal.transformlet.ClassInfo;
import com.alibaba.ttl.threadpool.agent.internal.transformlet.JavassistTransformlet;

import edu.umd.cs.findbugs.annotations.NonNull;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class TtlConsumerTransformlet implements JavassistTransformlet {

    private static final String CLASS_NAME = "org.springframework.amqp.rabbit.listener.BlockingQueueConsumer";

    @Override
    public void doTransform(@NonNull final ClassInfo classInfo) throws IOException, NotFoundException, CannotCompileException {
        if (CLASS_NAME.equals(classInfo.getClassName())) {
            String before = "org.slf4j.MDC.put(\"traceId\", java.util.UUID.randomUUID().toString().replaceAll(\"-\", \"\").toUpperCase());";
            final CtClass clazz = classInfo.getCtClass();
            CtMethod method = clazz.getDeclaredMethod("handle");
            method.insertBefore(before);
            classInfo.setModified();
        }
    }
}
