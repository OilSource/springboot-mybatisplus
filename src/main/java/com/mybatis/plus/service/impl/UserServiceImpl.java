package com.mybatis.plus.service.impl;

import com.mybatis.plus.entity.User;
import com.mybatis.plus.mapper.UserMapper;
import com.mybatis.plus.service.UserService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 'local_test.first_view' is not BASE TABLE 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2019-03-08
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
