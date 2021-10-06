/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.aspect;

import com.google.common.collect.ImmutableList;
import me.zhengjie.annotation.Limit;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.utils.RequestHolder;
import me.zhengjie.utils.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author /
 */
@Aspect
@Component
public class LimitAspect {

    private final RedisTemplate<Object,Object> redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(LimitAspect.class);

    public LimitAspect(RedisTemplate<Object,Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Pointcut("@annotation(me.zhengjie.annotation.Limit)")
    public void pointcut() {
    }
    // JointPoint和ProceedingJoinPoint使用详解 https://blog.csdn.net/kouryoushine/article/details/105299956
    // pointcut()是joinPoint的“集合”；ProceedingJoinPoint比joinPoint多一个proceed方法：用于启动目标方法执行的；
    // getSignature(),是获取到这样的命名信息对象 :修饰符+ 包名+组件名(类名) +方法名
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = RequestHolder.getHttpServletRequest();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method signatureMethod = signature.getMethod();
        // 通过注解信息判断方法是否为ip类型，是则通过该请求获取ip地址
        Limit limit = signatureMethod.getAnnotation(Limit.class);
        LimitType limitType = limit.limitType();
        String key = limit.key();
        if (StringUtils.isEmpty(key)) {
            if (limitType == LimitType.IP) {
                key = StringUtils.getIp(request);
            } else {
                key = signatureMethod.getName();
            }
        }
        // ImmutableList使用示例 https://blog.csdn.net/yaomingyang/article/details/80903780
        // StringUtils.join详解 https://www.cnblogs.com/fenghh/p/12175368.html
        // ImmutableList 是一个不可变、线程安全的列表集合，它只会获取传入对象的一个副本，即复制内容到集合中
        // StringUtils.join 将数组或集合以某拼接符拼接到一起形成新的字符串
        ImmutableList<Object> keys = ImmutableList.of(StringUtils.join(limit.prefix(), "_", key, "_", request.getRequestURI().replaceAll("/","_")));

        String luaScript = buildLuaScript();
        // redis之lua脚本 https://blog.csdn.net/liubenlong007/article/details/53816087
        // 由于lua脚本可以加载到redis缓存，通过构建lua脚本，使用redis的efaultRedisScript<>()来加载lua脚本，再redisTemplate执行脚本(lua脚本，keys，受限访问次数，时效时长)
        // RedisScript<Number>泛型限定类后，返回值只能是number类，保证后续执行完脚本后，通过返回值count判断受限次数的关系
        RedisScript<Number> redisScript = new DefaultRedisScript<>(luaScript, Number.class);
        Number count = redisTemplate.execute(redisScript, keys, limit.count(), limit.period());
        if (null != count && count.intValue() <= limit.count()) {
            logger.info("第{}次访问key为 {}，描述为 [{}] 的接口", count, keys, limit.name());
            return joinPoint.proceed();
        } else {
            throw new BadRequestException("访问次数受限制");
        }
    }

    /**
     * 限流脚本(lua知识)
     */
    private String buildLuaScript() {
        return "local c" +
                "\nc = redis.call('get',KEYS[1])" +
                "\nif c and tonumber(c) > tonumber(ARGV[1]) then" +
                "\nreturn c;" +
                "\nend" +
                "\nc = redis.call('incr',KEYS[1])" +
                "\nif tonumber(c) == 1 then" +
                "\nredis.call('expire',KEYS[1],ARGV[2])" +
                "\nend" +
                "\nreturn c;";
    }
}
