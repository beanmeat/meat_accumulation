package com.meat.transaction.controller;

import com.meat.transaction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tchstart
 * @data 2025-03-06
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * REQUIRED
     * spring默认的事务传播行为就是它。支持事务。如果业务方法执行时已经在一个事务中，则加入当前事务，否则重新开启一个事务。
     * 外层事务提交了，内层才会提交。内/外只要有报错，他俩会一起回滚。只要内层方法报错抛出异常，即使外层有try-catch，该事务也会回滚！
     * 因为内外层方法在同一个事务中，内层只要抛出了异常，这个事务就会被设置成rollback-only，即使外层try-catch内层的异常，该事务也会回滚。
     *
     * result:
     * 事务回滚，user表和student表都没有插入数据。
     * 因为内外层方法在同一个事务中，内层只要抛出了异常，这个事务就会被设置成rollback-only，即使外层try-catch内层的异常，该事务也会回滚。
     */
    @GetMapping("test01")
    public String test01(){
        userService.addUser();
        return "ok";
    }


    /**
     * REQUIRES_NEW
     * 支持事务。每次都是创建一个新事物，如果当前已经在事务中了，会挂起当前事务。
     * 内层事务结束，内层就提交了，不用等着外层一起提交。
     * 外层报错回滚，不影响内层。
     * 内层报错回滚，外层try-catch内层的异常，外层不会回滚。
     * 内层报错回滚，然后又会抛出异常，外层如果没有捕获处理内层抛出来的这个异常，外层还是会回滚的。这是重点！！！网上有些文章的例子给错了！！！
     *
     * result:
     * 内层的addStudent方法执行结束后，这个内层的事务就提交了，student表中就已经插入了数据了。
     * 外层addUser方法报错，只是回滚了外层的这个事务，user表中没有插入数据。
     */
    @GetMapping("test02")
    public String test02(){
        userService.addUser2();
        return "ok";
    }

    /**
     * 调用addStudent的外层方法有事务，外层无错，内层报错回滚
     */
    @GetMapping("test03")
    public String test03(){
        userService.addUser3();
        return "ok";
    }

    /**
     * NESTED
     * 该传播行为解释：支持事务。如果当前已经在一个事务中了，则嵌套在已有的事务中作为一个子事务。如果当前没在事务中则开启一个事务。
     * 内层事务结束，要等着外层一起提交。
     * 外层回滚，内层也回滚。
     * 如果只是内层回滚，不影响外层。
     * 这个内层回滚不影响外层的特性是有前提的，否则内外都回滚。
     * 1.JDK版本要在1.4以上，有java.sql.Savepoint。因为nested就是用savepoint来实现的。
     * 2.事务管理器的nestedTransactionAllowed属性为true。
     * 3.外层try-catch内层的异常。
     *
     * result:
     * studentMapper.insertSelective(student);方法执行后，数据库student表中并没有插入数据，要等到外层addUser方法的事务结束后，才一起提交。
     * 而此时，外层报错了，回滚，student表和user表都无数据插入。
     */
    @GetMapping("test04")
    public String test04(){
        userService.addUser4();
        return "ok";
    }

    /**
     * result：
     * studentMapper.insertSelective(student);方法执行后，数据库student表中并没有插入数据，要等到外层addUser方法的事务结束后，才一起提交。
     * 而此时，自己内层报错了回滚，并不影响外层addUser方法的事务。 student表无数据插入。User表数据插入成功。
     *
     * 内层是nested模式下，外层要try-catch内层的异常，外层才不会回滚，
     * 而内层是REQUIRED模式的话，即是外层try-catch内层异常，外层同样会回滚的
     */
    @GetMapping("test05")
    public String test05(){
        userService.addUser5();
        return "ok";
    }

    /**
     * SUPPORTS
     * 该传播行为解释：支持事务。当前有事务就支持。当前没有事务就算了，不会开启一个事物。
     *
     * result:
     * studentMapper.insert(student);方法执行后，
     * 数据库student表中并没有插入数据，要等到外层addUser方法的事务结束后，才一起提交。
     */
    @GetMapping("test06")
    public String test06(){
        userService.addUser6();
        return "ok";
    }

    /**
     * MANDATORY
     * 该传播行为解释：支持事务，如果业务方法执行时已经在一个事务中，则加入当前事务。否则抛出异常。
     *
     * result:
     * 抛出异常
     */
    @GetMapping("test07")
    public String test07(){
        userService.addUser7();
        return "ok";
    }

    /**
     * result:
     * studentMapper.insert(student);方法执行后，
     * 数据库student表中并没有插入数据，要等到外层addUser方法的事务结束后，才一起提交。
     */
    @GetMapping("test08")
    public String test08(){
        userService.addUser8();
        return "ok";
    }

    /**
     * NOT_SUPPORTED
     * 不支持事务，如果业务方法执行时已经在一个事务中，则挂起当前事务，等方法执行完毕后，事务恢复进行。
     *
     * result:
     * studentMapper.insert(student);方法执行后，
     * 数据库student表中已经插入了数据，不用等到自己的addStudent方法的事务结束后才提交。
     */
    @GetMapping("test09")
    public String test09(){
        userService.addUser9();
        return "ok";
    }

    /**
     * NEVER
     * 不支持事务。如果当前已经在一个事务中了，抛出异常。
     *
     * result:
     * 外层addUser方法在调内层方法addStudent的时候，因为内层方法不支持事务，而外层方法开启了事务，则报错
     */
    @GetMapping("test10")
    public String test10(){
        userService.addUser10();
        return "ok";
    }
}
