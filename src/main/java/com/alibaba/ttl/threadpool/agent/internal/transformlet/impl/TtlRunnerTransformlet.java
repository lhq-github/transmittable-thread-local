package com.alibaba.ttl.threadpool.agent.internal.transformlet.impl;

import java.io.IOException;

import com.alibaba.ttl.threadpool.agent.internal.transformlet.ClassInfo;
import com.alibaba.ttl.threadpool.agent.internal.transformlet.JavassistTransformlet;

import edu.umd.cs.findbugs.annotations.NonNull;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class TtlRunnerTransformlet implements JavassistTransformlet {

    private static final String CLASS_NAME = "org.springframework.boot.SpringApplication";

    @Override
    public void doTransform(@NonNull final ClassInfo classInfo) throws IOException, NotFoundException, CannotCompileException {
        if (CLASS_NAME.equals(classInfo.getClassName())) {
            String before = "org.slf4j.MDC.put(\"traceId\", java.util.UUID.randomUUID().toString().replaceAll(\"-\", \"\").toUpperCase());";
            String after = "org.slf4j.MDC.clear();";
            final CtClass clazz = classInfo.getCtClass();
            final CtClass applicationRunnerClass = clazz.getClassPool().get("org.springframework.boot.ApplicationRunner");
            final CtClass commandLineRunnerClass = clazz.getClassPool().get("org.springframework.boot.CommandLineRunner");
            final CtClass applicationArgumentsClass = clazz.getClassPool().get("org.springframework.boot.ApplicationArguments");

            CtMethod method = clazz.getDeclaredMethod("callRunner", new CtClass[] { applicationRunnerClass, applicationArgumentsClass });
            method.insertBefore(before);
            method.insertAfter(after);

            method = clazz.getDeclaredMethod("callRunner", new CtClass[] { commandLineRunnerClass, applicationArgumentsClass });
            method.insertBefore(before);
            method.insertAfter(after);
            classInfo.setModified();
        }
    }
}
