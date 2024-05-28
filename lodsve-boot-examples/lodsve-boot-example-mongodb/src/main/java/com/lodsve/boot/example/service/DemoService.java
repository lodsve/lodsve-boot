package com.lodsve.boot.example.service;

import com.lodsve.boot.example.domain.Demo;
import com.lodsve.boot.example.repository.DemoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * .
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version 1.0 2017/12/7 16:00
 */
@Service
public class DemoService {
    private final DemoRepository demoRepository;

    public DemoService(DemoRepository demoRepository) {
        this.demoRepository = demoRepository;
    }

    public void save(Demo demo) {
        demoRepository.save(demo);
    }

    public Demo get(Long id) {
        Optional<Demo> optional = demoRepository.findById(id);
        return optional.orElseGet(optional::get);
    }

    public List<Demo> list() {
        return demoRepository.findAll();
    }
}
