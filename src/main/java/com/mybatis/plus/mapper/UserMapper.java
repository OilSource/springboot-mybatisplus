package com.mybatis.plus.mapper;

import com.mybatis.plus.entity.User;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 'local_test.first_view' is not BASE TABLE Mapper 接口
 * </p>
 *
 * @author Terry
 * @since 2019-03-08
 */
public interface UserMapper extends BaseMapper<User> {

    int insertOne(User user);

    int insertBatch(@Param("userList") List<User> userList);

    int updateBatch(@Param("userList")List<User> userList);

    int updateOne(User user);
}
