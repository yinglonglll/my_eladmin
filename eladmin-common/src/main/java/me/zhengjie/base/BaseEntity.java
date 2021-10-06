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
package me.zhengjie.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Timestamp;

/**
 * 通用字段， is_del 根据需求自行添加
 * @author Zheng Jie
 * @Date 2019年10月24日20:46:32
 */
@Getter
@Setter
// @MappedSuperclass、@EntityListeners的用法 https://blog.csdn.net/qq_38181949/article/details/91979332
// @MappedSuperclass 1 用来标识为父类 2 其不能映射到数据库且被标识的类的属性必须通过子类来映射 3 其不能再有@Entity和@Table注解标识该类，即父类用于继承
@MappedSuperclass
// @EntityListeners(AuditingEntityListener.class(审计实体监听)) 1 该注解用于监听实体类(监视值变则修改值)，在save、update之后的状态 2 在Application启动类上加@EnableJpaAuditing 3 配置AuditorAware实现类
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity implements Serializable {
    // @CreatedDate、@CreatedBy、@LastModifiedDate、@LastModifiedBy等 https://www.jianshu.com/p/14cb69646195
    // 作为所有entity的父类，继承父类时父类的属性则根据子类映射的数据库@table表名得到对应具体的字段名位置
    @CreatedBy
    @Column(name = "create_by", updatable = false)
    @ApiModelProperty(value = "创建人", hidden = true)
    private String createBy;

    @LastModifiedBy
    @Column(name = "update_by")
    @ApiModelProperty(value = "更新人", hidden = true)
    private String updateBy;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    @ApiModelProperty(value = "创建时间", hidden = true)
    private Timestamp createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    @ApiModelProperty(value = "更新时间", hidden = true)
    private Timestamp updateTime;

    /* 分组校验 */
    public @interface Create {}

    /* 分组校验 */
    public @interface Update {}

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        Field[] fields = this.getClass().getDeclaredFields();
        try {
            for (Field f : fields) {
                f.setAccessible(true);
                builder.append(f.getName(), f.get(this)).append("\n");
            }
        } catch (Exception e) {
            builder.append("toString builder encounter an error");
        }
        return builder.toString();
    }
}
