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
package me.zhengjie.config;

import me.zhengjie.utils.SecurityUtils;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import java.util.Optional;

/**
 * 基于Spring Data的AuditorAware审计功能 https://blog.csdn.net/wiselyman/article/details/84917143
 * @description  : 设置审计
 * @author  : Dong ZhaoYang
 * @date : 2019/10/28
 */
@Component("auditorAware")
// AuditorAware实现类
public class AuditorConfig implements AuditorAware<String> {

    /**
     * 返回操作员标志信息(此处重写的方法是配合create by使用 https://www.jianshu.com/p/3f2df336e37a)
     *
     * @return /
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            // 这里应根据实际业务情况获取具体信息
            return Optional.of(SecurityUtils.getCurrentUsername());
        }catch (Exception ignored){}
        // 用户定时任务，或者无Token调用的情况
        return Optional.of("System");
    }
}
