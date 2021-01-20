package com.lodsve.boot.example.service;

import com.lodsve.boot.example.domain.User;
import com.lodsve.boot.example.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * .
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version V1.0, 16/1/18 下午6:41
 */
@Service
public class UserService {

    public User loadUser(Long id) {
        return new User("name", "telNO");
    }

    public User save(UserDTO userDto) {
        return new User("name", "telNO");
    }

    public boolean delete(Long id) {
        return true;
    }

    public User update(UserDTO userDto) {
        return new User("name", "telNO");
    }

    public Page<User> findAll(Pageable pageable) {
        return new PageImpl<>(Collections.singletonList(new User("name", "telNO")), pageable, 1);
    }
}
