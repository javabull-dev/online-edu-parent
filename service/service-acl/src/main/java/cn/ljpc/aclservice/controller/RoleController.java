package cn.ljpc.aclservice.controller;

import cn.ljpc.aclservice.entity.Role;
import cn.ljpc.aclservice.service.RoleService;
import cn.ljpc.commonutils.CommonResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author testjava
 * @since 2020-01-12
 */
@RestController
@RequestMapping("/admin/acl/role")
//@CrossOrigin
public class RoleController {

    @Autowired
    private RoleService roleService;

    @ApiOperation(value = "获取角色分页列表")
    @GetMapping("{page}/{limit}")
    public CommonResult index(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit,
            Role role) {
        Page<Role> pageParam = new Page<>(page, limit);
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(role.getRoleName())) {
            wrapper.like("role_name",role.getRoleName());
        }
        roleService.page(pageParam,wrapper);
        return CommonResult.ok().data("items", pageParam.getRecords()).data("total", pageParam.getTotal());
    }

    @ApiOperation(value = "获取角色")
    @GetMapping("get/{id}")
    public CommonResult get(@PathVariable String id) {
        Role role = roleService.getById(id);
        return CommonResult.ok().data("item", role);
    }

    @ApiOperation(value = "新增角色")
    @PostMapping("save")
    public CommonResult save(@RequestBody Role role) {
        roleService.save(role);
        return CommonResult.ok();
    }

    @ApiOperation(value = "修改角色")
    @PutMapping("update")
    public CommonResult updateById(@RequestBody Role role) {
        roleService.updateById(role);
        return CommonResult.ok();
    }

    @ApiOperation(value = "删除角色")
    @DeleteMapping("remove/{id}")
    public CommonResult remove(@PathVariable String id) {
        roleService.removeById(id);
        return CommonResult.ok();
    }

    @ApiOperation(value = "根据id列表删除角色")
    @DeleteMapping("batchRemove")
    public CommonResult batchRemove(@RequestBody List<String> idList) {
        roleService.removeByIds(idList);
        return CommonResult.ok();
    }
}

