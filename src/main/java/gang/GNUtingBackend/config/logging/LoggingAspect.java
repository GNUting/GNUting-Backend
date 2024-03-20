package gang.GNUtingBackend.config.logging;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.eclipse.jetty.util.ajax.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(public * gang.GNUtingBackend..controller..*(..))")
    public void onRequest(){}

    /* Pointcut 과 매칭되는 메서드의 실행 전, 후에 실행
     *  @Around advice 는 꼭 proceed()가 필요하다. */
    @Around("onRequest()")
    public Object logAction(ProceedingJoinPoint joinPoint) throws Throwable{
        Class clazz = joinPoint.getTarget().getClass();
        Logger logger = LoggerFactory.getLogger(clazz);
        Object result = null;
        try {
            result = joinPoint.proceed(joinPoint.getArgs());
            return result;
        } finally {
            logger.info(getRequestUrl(joinPoint, clazz));
            logger.info("parameters: " + JSON.toString(params(joinPoint)));
            logger.info("response: " + JSON.toString(result));
        }
    }

    private String getRequestUrl(JoinPoint joinPoint, Class clazz) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        RequestMapping requestMapping = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
        String baseUrl = requestMapping.value()[0];

        String url = Stream.of( GetMapping.class, PutMapping.class, PostMapping.class,
                        PatchMapping.class, DeleteMapping.class, RequestMapping.class)
                .filter(mappingClass -> method.isAnnotationPresent(mappingClass))
                .map(mappingClass -> getUrl(method, mappingClass, baseUrl))
                .findFirst().orElse(null);
        return url;
    }

    /* httpMETHOD + requestURI 를 반환 */
    private String getUrl(Method method, Class<? extends Annotation> annotationClass, String baseUrl){
        Annotation annotation = method.getAnnotation(annotationClass);
        String[] value;
        String httpMethod = null;
        try {
            value = (String[])annotationClass.getMethod("value").invoke(annotation);
            httpMethod = (annotationClass.getSimpleName().replace("Mapping", "")).toUpperCase();
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            return null;
        }
        return String.format("%s %s%s", httpMethod, baseUrl, value.length > 0 ? value[0] : "") ;
    }

    /* printing request parameter or request body */
    private Map params(JoinPoint joinPoint) {
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        String[] parameterNames = codeSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < parameterNames.length; i++) {
            params.put(parameterNames[i], args[i]);
        }
        return params;
    }
}
