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
package me.zhengjie.modules.system.repository;

import me.zhengjie.modules.system.domain.Dept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Set;

/**
* @author Zheng Jie
* @date 2019-03-25
*/
public interface DeptRepository extends JpaRepository<Dept, Long>, JpaSpecificationExecutor<Dept> {

    /**
     * 根据 PID 查询
     * @param id pid
     * @return /
     */
    List<Dept> findByPid(Long id);

    /**
     * 获取顶级部门
     * @return /
     */
    List<Dept> findByPidIsNull();

    /**
     * 根据角色ID 查询
     * @param roleId 角色ID
     * @return /
     */
    // @Query的参数 https://blog.csdn.net/weixin_38297879/article/details/84985197
    // nativeQuery = true 允许使用原生的SQL进行查询，即如下value值；？1是占位符，且从1开始(即大于等于1)
    @Query(value = "select d.* from sys_dept d, sys_roles_depts r where " +
            "d.dept_id = r.dept_id and r.role_id = ?1", nativeQuery = true)
    Set<Dept> findByRoleId(Long roleId);

    /**
     * 判断是否存在子节点
     * @param pid /
     * @return /
     */
    int countByPid(Long pid);

    /**
     * 根据ID更新sub_count
     * @param count /
     * @param id /
     */
    // 通过@Modifying或实现CRUDResotory接口来编写sql语句 https://blog.csdn.net/weixin_33910434/article/details/86712773
    // @Modifying原理分析 https://www.cnblogs.com/wuhenzhidu/p/jpa.html
    // 通过@Modifying和@Query两者注解才可以实现Update或者Delete操作，不支持insert
    // 其解决办法(支持原生sql和赋予@Query语句为可修改的权限) https://blog.csdn.net/gm371200587/article/details/80827483
    @Modifying
    @Query(value = " update sys_dept set sub_count = ?1 where dept_id = ?2 ",nativeQuery = true)
    void updateSubCntById(Integer count, Long id);
}