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
package me.zhengjie.modules.system.service.dto;

import lombok.Data;
import me.zhengjie.annotation.DataPermission;
import me.zhengjie.annotation.Query;
import java.sql.Timestamp;
import java.util.List;

/**
 * DeptQueryCriteria：Dept实体类 QueryCriteria 查询标准，此处注解中id是Dept实体类的主键
 * 即该类定义了实体类查询的标准：类中的属性是实体类中的字段名属性，通过其来制定不同的查询条件
 * QueryCriteria的作用：在service层调用复数个repository的方法来实现动态的多条件查询，而不是以往教学式的一dao对一service；
* @author Zheng Jie
* @date 2019-03-25
*/
@Data
// fixme：Dept中无字段名id，只有dept_id，那这个id表示是什么呢？默认值？
@DataPermission(fieldName = "id")
public class DeptQueryCriteria{

    @Query(type = Query.Type.INNER_LIKE)
    private String name;

    @Query
    private Boolean enabled;

    @Query
    private Long pid;

    @Query(type = Query.Type.IS_NULL, propName = "pid")
    private Boolean pidIsNull;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}