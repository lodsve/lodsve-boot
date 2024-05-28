package com.lodsve.boot.example.repository;

import com.lodsve.boot.example.domain.Demo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * .
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version 1.0 2017/12/7 16:01
 */
@Repository
public interface DemoRepository extends MongoRepository<Demo, Long> {

}
