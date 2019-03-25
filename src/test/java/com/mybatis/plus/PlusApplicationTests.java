package com.mybatis.plus;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class PlusApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void test1(){
        List<UserDto> newUserDto = new ArrayList<>();
        List<UserDto> modifyUserDto = new ArrayList<>();
        List<UserDto> newUserDto2 = new LinkedList<>();
        List<UserDto> modifyUserDto2 = new LinkedList<>();
        long total =7000;
        List<UserDto> userDtos =new ArrayList<>();
        List<UserDto> users2=new ArrayList<>();
        addUser(total, userDtos);
        addUser(total, users2);
        long start = System.currentTimeMillis();
        boolean flag ;
        for(UserDto userDtoA : userDtos){
            flag = true;
            for(UserDto userDtoB : users2){
                if(StringUtils.equals(userDtoA.getName(), userDtoB.getName())){
                    modifyUserDto.add(userDtoA);
                    flag = false;
                    break;
                }
            }
            if(flag){
                newUserDto.add(userDtoA);
            }
        }
        System.out.println("newUserDto :" + newUserDto.size()+ ", modifyUserDto: "+ modifyUserDto.size());
        System.out.println("执行花费时间："+(System.currentTimeMillis()-start));
        System.out.println("==============================");
        start = System.currentTimeMillis();
        for(UserDto userDtoA : userDtos){
            flag = true;
            for(UserDto userDtoB : users2){
                if(StringUtils.equals(userDtoA.getName(), userDtoB.getName())){
                    modifyUserDto2.add(0, userDtoA);
                    flag = false;
                    break;
                }
            }
            if(flag){
                newUserDto2.add(0, userDtoA);
            }
        }
        System.out.println("newUserDto :" + newUserDto2.size()+ ", modifyUserDto: "+ modifyUserDto2.size());
        System.out.println("执行花费时间："+(System.currentTimeMillis()-start));
    }

    private void addUser(long total, List<UserDto> userDtos) {
        for(long i=0;i<total;i++){
            UserDto userDto = new UserDto();
            userDto.setUsername("xi");
            userDto.setAddress("啊手动阀手动阀手动阀");
            userDto.setPassword("xxxxxxxxx");
            userDto.setAge((long)(Math.random()*total));
            userDto.setName("生巅峰"+(long)(Math.random()*total));
            userDto.setHobby("打羽毛球");
            userDto.setTelephone("13578562451");
            userDto.setCountry("中国");
            userDto.setIp("192.168.123.52");
            userDtos.add(userDto);
        }
    }


//    @Test
//    public void test3(){
//        long total = 100000;
//        List<UserDto> users=new ArrayList<>();
//        for(long i=0;i<total;i++){
//            UserDto user = new UserDto();
//            user.setUsername("xi");
//            user.setAddress("aaaaaaaaaaaaaa");
//            user.setPassword("xxxxxxxxx");
//            user.setAge((long)(Math.random()*total));
//            users.add(user);
//        }
//        List<UserDto> users2=new ArrayList<>();
//        for(long i=0;i<total;i++){
//            UserDto user = new UserDto();
//            user.setUsername("xi");
//            user.setAddress("aaaaaaaaaaaaaa");
//            user.setPassword("xxxxxxxxx");
//            user.setAge((long)(Math.random()*total));
//            users2.add(user);
//        }
//        long start = System.currentTimeMillis();
//        boolean flag = true;
//        Set<Long> ages= users2.stream().map(UserDto::getAge).collect(Collectors.toSet());
//        List<UserDto> modifyUser =users.stream().filter(s->ages.contains(s.getAge())).collect(Collectors.toList());
//        users.removeAll(modifyUser);
//        System.out.println("newUser :" +users.size()+ ", modifyUser: "+ modifyUser.size());
//        System.out.println("执行花费时间："+(System.currentTimeMillis()-start));
//    }



}
