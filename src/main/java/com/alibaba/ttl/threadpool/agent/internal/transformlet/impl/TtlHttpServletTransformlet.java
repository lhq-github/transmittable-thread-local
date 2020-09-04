package com.alibaba.ttl.threadpool.agent.internal.transformlet.impl;

import java.io.IOException;

import com.alibaba.ttl.threadpool.agent.internal.transformlet.ClassInfo;
import com.alibaba.ttl.threadpool.agent.internal.transformlet.JavassistTransformlet;

import edu.umd.cs.findbugs.annotations.NonNull;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class TtlHttpServletTransformlet implements JavassistTransformlet {

    private static final String CLASS_NAME = "javax.servlet.http.HttpServlet";

    @Override
    public void doTransform(@NonNull final ClassInfo classInfo) throws IOException, NotFoundException, CannotCompileException {
        if (CLASS_NAME.equals(classInfo.getClassName())) {
            final CtClass clazz = classInfo.getCtClass();
            CtMethod method = clazz.getDeclaredMethod("service");
            StringBuilder buffer = new StringBuilder();
            buffer.append("if (null == org.slf4j.MDC.get(\"traceId\")) {");
            buffer.append("    String traceId = req.getHeader(\"traceId\");");
            buffer.append("    if (null == traceId || \"\".equals(traceId)) {");
            buffer.append("        org.slf4j.MDC.put(\"traceId\", java.util.UUID.randomUUID().toString().replaceAll(\"-\", \"\").toUpperCase());");
            buffer.append("    } else {");
            buffer.append("        org.slf4j.MDC.put(\"traceId\", traceId);");
            buffer.append("    }");
            buffer.append("}");
            method.insertBefore(buffer.toString());
            classInfo.setModified();
        }
    }
}
