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

import com.lodsve.boot.rdbms.annotations.SwitchDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * .
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@Service
public class HomeService {
    private final DataSource dataSource;

    public HomeService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @SwitchDataSource("test")
    public String switchTest() {
        System.out.println(dataSource);
        return queryDb();
    }

    @SwitchDataSource("demo")
    public String switchDemo() {
        System.out.println(dataSource);
        return queryDb();
    }

    public String switchNone() {
        System.out.println(dataSource);
        return queryDb();
    }

    private String queryDb() {
        try (Connection connection = dataSource.getConnection()) {
            ResultSet rs = connection.createStatement().executeQuery("select database() db");
            rs.next();
            return rs.getString("db");
        } catch (SQLException e) {
            return "";
        }
    }
}
