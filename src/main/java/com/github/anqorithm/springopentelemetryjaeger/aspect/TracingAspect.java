package com.github.anqorithm.springopentelemetryjaeger.aspect;

import com.github.anqorithm.springopentelemetryjaeger.annotation.Traced;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Component
public class TracingAspect {

    @Autowired
    private Tracer tracer;

    @Around("@annotation(traced)")
    public Object traceMethod(ProceedingJoinPoint joinPoint, Traced traced) throws Throwable {
        return createSpanAndExecute(joinPoint, traced);
    }

    @Around("@within(traced) && !@annotation(com.github.anqorithm.springopentelemetryjaeger.annotation.Traced)")
    public Object traceClass(ProceedingJoinPoint joinPoint, Traced traced) throws Throwable {
        return createSpanAndExecute(joinPoint, traced);
    }

    @Around("execution(* com.github.anqorithm.springopentelemetryjaeger.controller.*.*(..))")
    public Object traceControllers(ProceedingJoinPoint joinPoint) throws Throwable {
        return createSpanAndExecute(joinPoint, null);
    }

    @Around("execution(* com.github.anqorithm.springopentelemetryjaeger.service.*.*(..))")
    public Object traceServices(ProceedingJoinPoint joinPoint) throws Throwable {
        return createSpanAndExecute(joinPoint, null);
    }

    @Around("execution(* com.github.anqorithm.springopentelemetryjaeger.repository.*.*(..))")
    public Object traceRepositories(ProceedingJoinPoint joinPoint) throws Throwable {
        return createSpanAndExecute(joinPoint, null);
    }

    @Around("execution(* com.github.anqorithm.springopentelemetryjaeger.mapper.*.*(..))")
    public Object traceMappers(ProceedingJoinPoint joinPoint) throws Throwable {
        return createSpanAndExecute(joinPoint, null);
    }

    private Object createSpanAndExecute(ProceedingJoinPoint joinPoint, Traced traced) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();

        String spanName;
        if (traced != null && !traced.value().isEmpty()) {
            spanName = traced.value();
        } else {
            spanName = className.toLowerCase() + "." + methodName;
        }

        Span span = tracer.spanBuilder(spanName).startSpan();

        try {
            span.setAttribute("class.name", className);
            span.setAttribute("method.name", methodName);

            if (traced != null && !traced.operation().isEmpty()) {
                span.setAttribute("operation", traced.operation());
            }

            String packageName = joinPoint.getTarget().getClass().getPackage().getName();
            if (packageName.contains(".controller")) {
                span.setAttribute("layer", "controller");
                span.setAttribute("component", "http");
            } else if (packageName.contains(".service")) {
                span.setAttribute("layer", "service");
                span.setAttribute("component", "business-logic");
            } else if (packageName.contains(".repository")) {
                span.setAttribute("layer", "repository");
                span.setAttribute("component", "database");
            } else if (packageName.contains(".mapper")) {
                span.setAttribute("layer", "mapper");
                span.setAttribute("component", "mybatis");
                span.setAttribute("db.system", "h2");
                span.setAttribute("db.name", "testdb");
            }

            span.setAttribute("thread.id", String.valueOf(Thread.currentThread().getId()));
            span.setAttribute("thread.name", Thread.currentThread().getName());

            addMethodParameters(span, method, joinPoint.getArgs());

            span.addEvent("Method execution started");

            Object result = joinPoint.proceed();

            span.addEvent("Method execution completed successfully");

            if (result != null) {
                span.setAttribute("result.type", result.getClass().getSimpleName());
                if (result instanceof java.util.Collection) {
                    span.setAttribute("result.size", ((java.util.Collection<?>) result).size());
                } else if (result instanceof Boolean) {
                    span.setAttribute("result.value", result.toString());
                }
            }

            return result;
        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            span.setAttribute("error.message", e.getMessage());
            span.addEvent("Method execution failed");
            throw e;
        } finally {
            span.end();
        }
    }

    private void addMethodParameters(Span span, Method method, Object[] args) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length && i < args.length; i++) {
            Parameter param = parameters[i];
            Object arg = args[i];

            if (arg != null) {
                String paramName = param.getName();
                String value = arg.toString();

                if (value.length() > 100) {
                    value = value.substring(0, 97) + "...";
                }

                span.setAttribute("param." + paramName, value);
            }
        }
    }
}