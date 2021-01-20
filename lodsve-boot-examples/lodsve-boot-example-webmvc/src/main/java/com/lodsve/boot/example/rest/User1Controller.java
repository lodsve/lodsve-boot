package com.lodsve.boot.example.rest;

import com.lodsve.boot.example.domain.User;
import com.lodsve.boot.example.dto.UserDTO;
import com.lodsve.boot.example.enums.Gender;
import com.lodsve.boot.example.service.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * .
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version V1.0, 16/1/18 下午6:42
 */
@Api(value = "Mybatis的CRUD测试", description = "Mybatis的CRUD测试")
@RestController
@RequestMapping("/group1/user")
public class User1Controller {
    @Autowired
    private UserService userService;

    @ApiOperation(value = "获取用户", notes = "获取用户")
    @ApiResponses({@ApiResponse(code = 200, message = "获取用户成功")})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public User loadUser(@ApiParam(name = "id", value = "用户ID") @PathVariable("id") Long id) {
        return this.userService.loadUser(id);
    }

    @ApiOperation(value = "删除用户", notes = "删除用户")
    @ApiResponses({@ApiResponse(code = 200, message = "删除用户成功")})
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public boolean delete(@ApiParam(name = "id", value = "用户ID") @RequestParam("id") Long id) {
        return this.userService.delete(id);
    }

    @ApiOperation(value = "更新用户", notes = "更新用户")
    @ApiResponses({@ApiResponse(code = 200, message = "更新用户成功")})
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public boolean update(@ApiParam(name = "userDto", value = "需要更新的用户") @RequestBody UserDTO userDto) {
        return this.userService.update(userDto) != null;
    }

    @ApiOperation(value = "保存用户", notes = "保存审核用户")
    @ApiResponses({@ApiResponse(code = 200, message = "审核用户成功")})
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public User save(@ApiParam(name = "userDto", value = "需要保存的用户") @RequestBody UserDTO userDto) {
        return this.userService.save(userDto);
    }

    @ApiOperation(value = "列出用户", notes = "列出用户")
    @ApiResponses({@ApiResponse(code = 200, message = "列出用户成功")})
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Page<User> findAll(@ApiParam(name = "pageable", value = "分页信息,传参方式：?page=0&size=10") @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return this.userService.findAll(pageable);
    }

    @ApiOperation(value = "测试枚举", notes = "测试枚举")
    @ApiResponses({@ApiResponse(code = 200, message = "测试枚举")})
    @RequestMapping(value = "/test/enums", method = RequestMethod.GET)
    public Gender testEnums(@ApiParam(name = "gender", value = "性别") @RequestParam("gender") Gender gender) {
        return gender;
    }

    @ApiOperation(value = "测试时间", notes = "测试时间")
    @ApiResponses({@ApiResponse(code = 200, message = "测试时间")})
    @RequestMapping(value = "/test/date", method = RequestMethod.GET)
    public Date testDate(@ApiParam(name = "date", value = "时间") @RequestParam("date") Date date) {
        return date;
    }
}
