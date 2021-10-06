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
package me.zhengjie.modules.system.domain;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import me.zhengjie.base.BaseEntity;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
* @author Zheng Jie
* @date 2019-03-25
*/
@Entity
@Getter
@Setter
@Table(name="sys_dept")
public class Dept extends BaseEntity implements Serializable {

    @Id
    @Column(name = "dept_id")
    // @NotNull等区别 https://blog.csdn.net/weixin_49770443/article/details/109772162 及 https://blog.csdn.net/weixin_45599277/article/details/114918397
    // 常用注解验证 https://www.cnblogs.com/zhengjinsheng/p/11202436.html
    // NotNull内groups的作用 https://blog.csdn.net/qq_38849191/article/details/108573128
    // 在属性更新时进行检查属性是否满足注解的验证要求(NotNull)
    @NotNull(groups = Update.class)
    @ApiModelProperty(value = "ID", hidden = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JSONField(serialize = false)
    // @MappedBy https://blog.csdn.net/qq_29954971/article/details/79088079
    // jpa关系大全(重要) https://www.cnblogs.com/jpfss/p/11056939.html
    // 不建中间表进行实体关联，那就通过外键进行实体关联。但多对多关系一般是通过生成中间表来关联的(其他对应关系则不用，提高效率)，其中joinColumns和inverseJoinColumns是指定两表关联的主键对应关系，
    @ManyToMany(mappedBy = "depts")
    @ApiModelProperty(value = "角色")
    private Set<Role> roles;

    @ApiModelProperty(value = "排序")
    private Integer deptSort;

    @NotBlank
    @ApiModelProperty(value = "部门名称")
    private String name;

    @NotNull
    @ApiModelProperty(value = "是否启用")
    private Boolean enabled;

    @ApiModelProperty(value = "上级部门")
    private Long pid;

    @ApiModelProperty(value = "子节点数目", hidden = true)
    private Integer subCount = 0;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Dept dept = (Dept) o;
        return Objects.equals(id, dept.id) &&
                Objects.equals(name, dept.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

/*
1.只有OneToOne,OneToMany,ManyToMany上才有mappedBy属性，为什么ManyToOne不存在该属性？
答：因为规范要求在双向OneToMany / ManyToOne关联中，Many方必须是所有者方（因此不需要mappedBy属性），并且One方必须是反方（因此需要一个mappedBy属性
即mappedBy标签一定是定义在the owned side（被拥有方的），他指向theowning side（拥有方）
如下
@OneToMany(mappedBy = "dict",cascade={CascadeType.PERSIST,CascadeType.REMOVE})
private List<DictDetail> dictDetails;
其中"dict"指向被标注类型里面的实体对象


2.关系的拥有方负责关系的维护，在拥有方建立外键。所以用到@JoinColumn，其中通过JoinColumn来约束两表的关联的两种情况如下
要么在联表当中：
@ManyToMany
@JoinTable（name = "t_game_user",
    joinColumns = {@JoinColumn（name = "game_id",referencedColumnName="gameId"）},
    inverseJoinColumns = {@JoinColumn（name = "user_id",referencedColumnName="id"）}）
其中"t_game_user"是中间表，通过建立外键，中间表的game_id指向当前表(拥有方)的gameId、中间表的user_id指向另外表(被拥有方)的id
要么不在联表当中(通过外键)：
@Entity(name = "user")
public class User implements Serializable {
@Id
@GeneratedValue
private Long id;
private String username;
private String password;
@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
@JoinColumn(name = "user_id")
private Set<Address> addresses;
}
-----------------
@Entity(name = "address")
public class Address implements Serializable {
@Id
@GeneratedValue
private Long id;
private String name;
private String province;
private String city;
private String area;
private String detail;
// getters and setters
}
-----------------
这样多的一方(Address)通过外键"user_id"(由Address生成的外键)直接与一的一方(User)发生关联，不需要中间表。外键名根据name命名，外键指向的位置根据name：表名_字段名进行匹配；
}

3.mappedBy跟JoinColumn/JoinTable总是处于互斥的一方
答：因为mappedBy≈被拥有方 JoinColumn≈拥有方，故一定互斥
*/
}