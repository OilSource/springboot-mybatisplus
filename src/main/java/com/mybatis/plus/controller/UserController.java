package com.mybatis.plus.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mybatis.plus.entity.User;
import com.mybatis.plus.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * <p>
 * 'local_test.first_view' is not BASE TABLE 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2019-03-08
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    UserMapper userMapper;

    @RequestMapping("/get/{id}")
    @ResponseBody
    public User get(@PathVariable("id") Integer id){
        EntityWrapper wrapper = new EntityWrapper();
       return userMapper.selectById(id);
    }

    @RequestMapping("/insertBatch")
    @ResponseBody
    public String insertBatch(){
        List<User> userList = new ArrayList<>();
        for (int i=0;i<10000;i++){
            User user = new User();
            user.setUsername("ssss");
            user.setAge((int)Math.random()*30);
            user.setBirthday(Calendar.getInstance().getTime());
            userList.add(user);
        }
        long start = System.currentTimeMillis();
        for(User user:userList){
            userMapper.insertOne(user);
        }
        return "执行完的时间为： "+ (System.currentTimeMillis()-start);
    }

    @RequestMapping("/batchInsert")
    @ResponseBody
    public String batchInsert(){
        List<User> userList = new ArrayList<>();
        for (int i=0;i<10000;i++){
            User user = new User();
            user.setUsername("ssss");
            user.setAge((int)Math.random()*30);
            user.setBirthday(Calendar.getInstance().getTime());
            userList.add(user);
        }
        long start = System.currentTimeMillis();
        userMapper.insertBatch(userList);
        return "执行完的时间为： "+ (System.currentTimeMillis()-start);
    }

    @RequestMapping("/updateBatch/{chr}/{query}")
    @ResponseBody
    public String updateBatch(@PathVariable("chr")String chr,@PathVariable("query") String query){
        String str =StringUtils.repeat(chr.charAt(0),4);
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("username",StringUtils.repeat(query.charAt(0),4));
        List<User> userList =userMapper.selectList(entityWrapper);
        for(User user: userList){
            user.setUsername(str);
        }
        long start = System.currentTimeMillis();
        for(User user: userList){
            userMapper.updateOne(user);
        }
        return "执行完的时间为： "+ (System.currentTimeMillis()-start);
    }

    @RequestMapping("/batchUpdate/{chr}/{query}")
    @ResponseBody
    public String batchUpdate(@PathVariable("chr")String chr,@PathVariable("query") String query){
        String str =StringUtils.repeat(chr.charAt(0),4);
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("username",StringUtils.repeat(query.charAt(0),4));
        List<User> userList =userMapper.selectList(entityWrapper);
        for(User user: userList){
            user.setUsername(str);
        }
        long start = System.currentTimeMillis();
        userMapper.updateBatch(userList);
        return "执行完的时间为： "+ (System.currentTimeMillis()-start);
    }

}

