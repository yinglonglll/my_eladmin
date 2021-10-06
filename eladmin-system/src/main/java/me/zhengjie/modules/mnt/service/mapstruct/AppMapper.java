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
package me.zhengjie.modules.mnt.service.mapstruct;

import me.zhengjie.base.BaseMapper;
import me.zhengjie.modules.mnt.domain.App;
import me.zhengjie.modules.mnt.service.dto.AppDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
* @author zhanghouying
* @date 2019-08-24
*/
// @Mapper
// 简：http://www.tianshouzhi.com/api/tutorials/mapstruct/291 及 https://www.it1352.com/992006.html (unmappedTargetPolicy)
// 全：https://dreamchan.cn/posts/26652.html#MapStruct%E7%9A%84%E4%BB%8B%E7%BB%8D
// 1 注册到spring
// 2 @Mapper的 uses 可以使用另一个 @Mapper映射器 ，空则无
// 3 MapStruct自动忽略未映射的属性(unmappedTargetPolicy = ReportingPolicy.IGNORE)，无用的映射属性会自动忽略
// 通过@Mapper自动生成实现类impl，即实体类entity数据转存为dto类中；BaseMapper含所有转化的方法；
@Mapper(componentModel = "spring",uses = {},unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppMapper extends BaseMapper<AppDto, App> {

}
