package com.meat.transaction.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meat.transaction.entity.User;
import com.meat.transaction.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author tchstart
 * @data 2025-03-06
 */
@Service
@Slf4j
public class UserService extends ServiceImpl<UserMapper,User> {

    @Autowired
    private UserService userService;

    @Autowired
    private StudentService studentService;

    @Transactional
    public void addUser() {
        User user = new User("测试");
        this.save(user);
        try{
            studentService.addStudent();
        }catch (Exception e){

        }
    }

    @Transactional
    public void addUser2() {
        User user = new User("测试");
        this.save(user);
        studentService.addStudent1();
        int a = 1 / 0;
    }

    @Transactional
    public void addUser3() {
        User user = new User("测试");
        this.save(user);
        studentService.addStudent3();
    }

    @Transactional
    public void addUser4() {
        User user = new User("测试");
        this.save(user);
        studentService.addStudent4();
        int a = 1 / 0;
    }

    @Transactional
    public void addUser5() {
        User user = new User("测试");
        this.save(user);
        // 强调一下，内层是nested模式下，外层要try-catch内层的异常，外层才不会回滚
        // 而内层是REQUIRED模式的话，即是外层try-catch内层异常，外层同样会回滚的
        try{
            studentService.addStudent5();
        }catch (Exception e){

        }
    }

    @Transactional
    public void addUser6() {
        User user = new User("测试");
        this.save(user);
        studentService.addStudent6();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void addUser7() {
        User user = new User("测试");
        this.save(user);

    }

    @Transactional
    public void addUser8() {
        User user = new User("测试");
        this.save(user);
        studentService.addStudent8();
    }

    @Transactional
    public void addUser9() {
        User user = new User("测试");
        this.save(user);
        studentService.addStudent9();
    }

    @Transactional
    public void addUser10() {
        User user = new User("测试");
        this.save(user);
        studentService.addStudent10();
    }
}
