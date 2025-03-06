package com.meat.transaction;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.meat.transaction.entity.User;
import com.meat.transaction.mapper.UserMapper;
import org.apache.ibatis.javassist.tools.reflect.Sample;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
class TransactionPropagationApplicationTests {

	@Autowired
	private UserMapper userMapper;

	@Test
	public void testSelect() {
		System.out.println(("----- selectAll method test ------"));
		List<User> userList = userMapper.selectList(null);
		Assert.isTrue(5 == userList.size(), "");
		userList.forEach(System.out::println);
	}


}
