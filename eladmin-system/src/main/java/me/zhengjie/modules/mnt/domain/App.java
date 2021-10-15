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
package me.zhengjie.modules.mnt.domain;

import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.Getter;
import lombok.Setter;
import me.zhengjie.base.BaseEntity;
import javax.persistence.*;
import java.io.Serializable;

/**
 * mnt是部署模块
* @author zhanghouying
* @date 2019-08-24
*/
@Entity
@Getter
@Setter
// 声明此对象映射到数据库的数据表
@Table(name="mnt_app")
public class App extends BaseEntity implements Serializable {

	// @Id 注解可将实体Bean中某个属性定义为主键
    @Id
	// @Column注解来标识实体类中属性与数据表中字段的对应关系
	@Column(name = "app_id")
	// swagger2中 对model属性的说明或者数据操作更改：注释的意思
	@ApiModelProperty(value = "ID", hidden = true)
	// @GeneratedValue https://blog.csdn.net/sswqzx/article/details/84337921
	// 给一个实体生成一个唯一标识的主键(id)后、@GeneratedValue提供了主键的生成策略,其中 IDENTITY：主键由数据库生成, 采用数据库自增长, Oracle不支持这种方式
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@ApiModelProperty(value = "名称")
    private String name;

	@ApiModelProperty(value = "端口")
	private int port;

	@ApiModelProperty(value = "上传路径")
	private String uploadPath;

	@ApiModelProperty(value = "部署路径")
	private String deployPath;

	@ApiModelProperty(value = "备份路径")
	private String backupPath;

	@ApiModelProperty(value = "启动脚本")
	private String startScript;

	@ApiModelProperty(value = "部署脚本")
	private String deployScript;

	// BeanUtils.copyProperties https://blog.csdn.net/dongyuxu342719/article/details/90242904
	// 实现对象之间的多属性赋值，将resource赋值到本类对象
    public void copy(App source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
