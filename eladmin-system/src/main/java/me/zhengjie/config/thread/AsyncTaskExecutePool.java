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
package me.zhengjie.config.thread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务线程池装配类
 * 此处实现线程的自定义配置(也可以使用默认的配置)，使得@Async以如下配置进行异步任务
 * https://juejin.im/entry/5abb8f6951882555677e9da2
 * @date 2019年10月31日15:06:18
 */
// 如果不想每次都写private  final Logger logger = LoggerFactory.getLogger(当前类名.class); 可以用注解@Slf4j,再使用log打印日志;
@Slf4j
@Configuration
public class AsyncTaskExecutePool implements AsyncConfigurer {

    /** 注入配置类 */
    private final AsyncTaskProperties config;

    // 注意：此处为了能够让properties实体类进行自动装配，才需要构造器显式调用，若删除此构造器则无法自动装配properties实体类，
    // fixme：此处是异步任务的自定义配置，似乎不是创建pool的地方？
    public AsyncTaskExecutePool(AsyncTaskProperties config) {
        this.config = config;
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程池大小
        executor.setCorePoolSize(config.getCorePoolSize());
        //最大线程数
        executor.setMaxPoolSize(config.getMaxPoolSize());
        //队列容量
        executor.setQueueCapacity(config.getQueueCapacity());
        //活跃时间
        executor.setKeepAliveSeconds(config.getKeepAliveSeconds());
        //线程名字前缀
        executor.setThreadNamePrefix("el-async-");
        // setRejectedExecutionHandler：当pool已经达到max size的时候，如何处理新任务
        // CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Override
    // https://blog.csdn.net/adayabetter/article/details/51146673
    // 捕获程序异常退出并做相应操作
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> {
            log.error("===="+throwable.getMessage()+"====", throwable);
            log.error("exception method:"+method.getName());
        };
    }
}
