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
package me.zhengjie.modules.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;

/**
 * Vo、Dto、Entity：各自数据的含义：
 * 1、entity：自动装配数据库的原始数据，数据全但并非请求需要实体类中全部的数据，即entity实体
 * 2、Dto：根据前端请求在数据全面的entity中取出需要的数据，不需要的则不取，取出有效数据的封装对象即为Dto实体
 * 3、Vo:若需对获取的有效数据dto进行二次处理，则根据获取到的有效数据封装对象Dto进行所谓的二次处理，处理过后的封装对象为Vo实体
 * 场景：一群人吃饭，点了一堆饮品且饮品一些要求(请求端)，商户根据要求进行反馈(服务端)：
 * entity：是商户支持的全部饮品(可乐、雪碧、加多宝、椰汁、...)，
 * dto：是提供给客户端的饮品，但因为饮品有一些要求，所有还需进行处理，如dto是可乐、椰汁(也可以是其他)；(dto对象也常绑定在service层)
 * vo：可乐是加冰的，椰汁是需要加果粒的，因为有一些要求，所有vo才是提供给人(前端)的实体对象
 * @author Zheng Jie
 * @date 2018-12-20
 */
@Data
@AllArgsConstructor
public class MenuMetaVo implements Serializable {

    private String title;

    private String icon;

    private Boolean noCache;
}
