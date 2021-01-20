/*
 * Copyright © 2020 Sun.Hao(https://www.crazy-coder.cn/)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
