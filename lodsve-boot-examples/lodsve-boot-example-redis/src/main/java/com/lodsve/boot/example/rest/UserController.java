package com.lodsve.boot.example.rest;

import com.lodsve.boot.example.domain.User;
import com.lodsve.boot.example.service.UserService;
import com.lodsve.boot.utils.RandomUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Set;

/**
 * .
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version V1.0, 16/1/18 下午6:42
 */
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/{id}")
    public void save(@PathVariable Long id) {
        User user = new User();
        user.setId(id);
        user.setUserName("张三-");
        user.setTelNo(RandomUtils.randomString(5));
        user.setCreateAt(new Date());

        userService.save(user);
    }

    @GetMapping("/load_from_string")
    public User loadFromString(@RequestParam Long id) {
        return userService.loadFromString(id);
    }

    @GetMapping("/load_from_set")
    public User loadFromSet() {
        return userService.getFromSet();
    }

    @GetMapping("/load_from_list")
    public User loadFromList() {
        return userService.getFromList();
    }

    @GetMapping("/load_from_hash")
    public User loadFromHash(@RequestParam Long id) {
        return userService.getFromHash(id);
    }

    @GetMapping("/load_from_zset")
    public Set<User> loadFromZset() {
        return userService.getFromZset();
    }
}
