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

import lombok.Data;
import me.zhengjie.utils.ElAdminConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 根据当前操作系统来获取全局的路径信息
 * @author Zheng Jie
 */
@Data
@Configuration
// 在当前类中自动装配application-dev.yml中file对象中所有属性值的配置信息，如类中的path、avatar等
// 诸如从配置文件中获取信息，装配到实体类中称为Properties，与从数据库中取出数据装配到实体类为entity的称呼不一样
@ConfigurationProperties(prefix = "file")
public class FileProperties {

    /** 文件大小限制 */
    private Long maxSize;

    /** 头像大小限制 */
    private Long avatarMaxSize;

    private ElPath mac;

    private ElPath linux;

    private ElPath windows;

    // 获取当前操作系统的路径对象
    public ElPath getPath(){
        // System.getProperty()的getProperty方法获得系统参数 https://blog.csdn.net/qq_35893120/article/details/80858654
        // os.name 操作系统的名称
        String os = System.getProperty("os.name");
        if(os.toLowerCase().startsWith(ElAdminConstant.WIN)) {
            return windows;
        } else if(os.toLowerCase().startsWith(ElAdminConstant.MAC)){
            return mac;
        }
        return linux;
    }

    @Data
    // 子类
    public static class ElPath{

        private String path;

        private String avatar;
    }
}
