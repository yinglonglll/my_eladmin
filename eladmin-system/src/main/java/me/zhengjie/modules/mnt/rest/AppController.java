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
package me.zhengjie.modules.mnt.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.mnt.domain.App;
import me.zhengjie.modules.mnt.service.AppService;
import me.zhengjie.modules.mnt.service.dto.AppQueryCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
* @author zhanghouying
* @date 2019-08-24
*/
// return 的数据为json
@RestController
// @RequiredArgsConstructor https://blog.csdn.net/a88328734/article/details/108147670 及 https://blog.csdn.net/qq_37192800/article/details/79785906
// 1 写在类上可以代替@AutoWired注解，需要注意的是在注入时需要用final定义，或者使用@notnull注解
// 2 会生成一个包含常量，和标识了NotNull的变量的构造方法。生成的构造方法是私有的private
@RequiredArgsConstructor
@Api(tags = "运维：应用管理")
@RequestMapping("/api/app")
public class AppController {
    // 此处就是因为@RequiredArgsConstructor而不需@Autowired，也可以自动装配
    private final AppService appService;

    @ApiOperation("导出应用数据")
    @GetMapping(value = "/download")
    // @PreAuthorize https://blog.csdn.net/weixin_39220472/article/details/80873268
    // 类似于@ConditionalOnMissingClass ，此为获取当前所有权限并判断是否拥有app:list权限，无则抛出异常
    @PreAuthorize("@el.check('app:list')")
    public void download(HttpServletResponse response, AppQueryCriteria criteria) throws IOException {
        appService.download(appService.queryAll(criteria), response);
    }

    @ApiOperation(value = "查询应用")
    @GetMapping
	@PreAuthorize("@el.check('app:list')")
    public ResponseEntity<Object> query(AppQueryCriteria criteria, Pageable pageable){
        // 统一封装返回对象ResponseEntity<>
        return new ResponseEntity<>(appService.queryAll(criteria,pageable),HttpStatus.OK);
    }
    // 每次执行方法，都打印该日志信息
    @Log("新增应用")
    @ApiOperation(value = "新增应用")
    @PostMapping
	@PreAuthorize("@el.check('app:add')")
    // @Validated：对实体类进行数据格式检验，若实体类内又包含其他实体类，则在属性处的其他实体类上再次添上注解
    // @RequestBody https://blog.csdn.net/justry_deng/article/details/80972817/
    // @RequestBody获取请求体中 json对象(自动装配到实体类，无对应值则默认为空)、@RequestParam()取请求头中 key-value
    public ResponseEntity<Object> create(@Validated @RequestBody App resources){
        appService.create(resources);
        // HttpStatus状态： https://blog.csdn.net/liufeifeihuawei/article/details/99676040
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("修改应用")
    @ApiOperation(value = "修改应用")
    // put与DeleteMapping 意义大于作用(与get/post大致相同)，体现restful风格
    @PutMapping
	@PreAuthorize("@el.check('app:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody App resources){
        appService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除应用")
    @ApiOperation(value = "删除应用")
	@DeleteMapping
	@PreAuthorize("@el.check('app:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids){
        appService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
