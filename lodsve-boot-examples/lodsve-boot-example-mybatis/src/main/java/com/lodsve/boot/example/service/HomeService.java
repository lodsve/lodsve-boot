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
