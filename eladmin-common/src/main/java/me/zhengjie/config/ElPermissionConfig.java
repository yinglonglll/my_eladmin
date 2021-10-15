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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用于检验权限对否，即所有的权限检查都通过"el"来检查即可。
 * @author Zheng Jie
 */
// 给注册到spring容器的Service起名为el，通过 @el 可以获取该服务并调用里面的check方法
@Service(value = "el")
public class ElPermissionConfig {

    public Boolean check(String ...permissions){
        // collection.stream()用法 https://blog.csdn.net/qq_37131111/article/details/99546357
        // stream().map().collect()用法简要 https://www.cnblogs.com/ngy0217/p/11080840.html
        // 如下作用为获取用户信息列表UserDetails后，从中遍历获取所有的权限角色到elPermissions集合中
        // java中双冒号::是lambda语法中的方法引用 https://blog.csdn.net/zhoufanyang_china/article/details/87798829
        // 获取当前用户的所有权限
        List<String> elPermissions = SecurityUtils.getCurrentUser().getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        // 判断当前用户的所有权限是否包含接口上定义的权限
        return elPermissions.contains("admin") || Arrays.stream(permissions).anyMatch(elPermissions::contains);
    }
}
