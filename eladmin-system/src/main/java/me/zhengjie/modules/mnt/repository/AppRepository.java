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
package me.zhengjie.modules.mnt.repository;

import me.zhengjie.modules.mnt.domain.App;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
* SpringDataJpa之JpaRepository<App, Long> https://www.cnblogs.com/amberbar/p/10261599.html
* JpaRepository和JpaSpecificationExecutor接口的简单使用 https://cloud.tencent.com/developer/article/1429349
* .JpaRepository支持接口规范方法名查询 https://www.cnblogs.com/suizhikuo/p/9412825.html
* 1 此是JPA提供的核心接口，又@Table(name="mnt_app")，即该该自定义接口是对 mnt_app表 进行操作，jap也封装了findAll，findALLById等简易方法
* 2 若需指定方法查询，准守jpa的命名准则： findBy + 属性名（首字母大写） + 查询条件(首字母大写 Is Equals)
* JpaSpecificationExecutor<App>
* 1 主要提供了多条件查询的支持，并且可以在查询中添加分页和排序，因为这个接口单独存在(无继承其他)，因此需要配合以上说的接口使用；
* @author zhanghouying
* @date 2019-08-24
*/
public interface AppRepository extends JpaRepository<App, Long>, JpaSpecificationExecutor<App> {
}
