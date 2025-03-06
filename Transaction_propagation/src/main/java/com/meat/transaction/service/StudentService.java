package com.meat.transaction.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meat.transaction.entity.Student;
import com.meat.transaction.entity.User;
import com.meat.transaction.mapper.StudentMapper;
import com.meat.transaction.mapper.UserMapper;
import jakarta.annotation.Resource;
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
public class StudentService extends ServiceImpl<StudentMapper,Student> {
    @Resource
    private StudentService studentService;

    @Transactional
    public void addStudent() {
        Student student = new Student("student");
        studentService.save(student);
        int j =  10/ 0;  // 内层报错抛出异常
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addStudent1() {
        Student student = new Student("student1");
        studentService.save(student);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addStudent3() {
        Student student = new Student("student");
        studentService.save(student);
        int j =  10/ 0;  // 内层报错抛出异常
    }

    @Transactional(propagation = Propagation.NESTED)
    public void addStudent4() {
        Student student = new Student("student");
        studentService.save(student);
    }

    @Transactional(propagation = Propagation.NESTED)
    public void addStudent5() {
        Student student = new Student("student");
        studentService.save(student);
        int j =  10/ 0;  // 内层报错抛出异常
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void addStudent6() {
        Student student = new Student("student");
        studentService.save(student);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void addStudent8() {
        Student student = new Student("student");
        studentService.save(student);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void addStudent9() {
        Student student = new Student("student");
        studentService.save(student);
    }

    @Transactional(propagation = Propagation.NEVER)
    public void addStudent10() {
        Student student = new Student("student");
        studentService.save(student);
    }
}
