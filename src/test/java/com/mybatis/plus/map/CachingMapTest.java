package com.mybatis.plus.map;

import com.mybatis.plus.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CachingMapTest {

    public static void main(String[] args) {
        test1();
//        test2();
    }

    public static void test1(){
        long total = 1000000;
        Info info = new Info(7000);
        List<UserDto> localUserDtoList = new ArrayList<>();
        for(long i=0;i<total;i++){
            UserDto userDto = new UserDto();
            userDto.setUsername("uuu"+UUID.randomUUID().toString());
            userDto.setPassword("pppp"+UUID.randomUUID().toString());
            userDto.setIp("iiii"+UUID.randomUUID().toString());
            userDto.setEmail("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"+i);
            userDto.setCountry(UUID.randomUUID().toString().replace("-",""));
            userDto.setTelephone(UUID.randomUUID().toString().replace("-",""));
            userDto.setAddress(UUID.randomUUID().toString().replace("-",""));
            userDto.setPhraseId("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            userDto.setUuid(UUID.randomUUID().toString().replace("-",""));
            userDto.setGroupId(""+i);
            localUserDtoList.add(userDto);
        }
        List<UserDto> remoteUserDtoList = new ArrayList<>();
        for(long i=0;i<20000;i++){
            UserDto userDto = new UserDto();
            userDto.setUsername("uuu"+UUID.randomUUID().toString());
            userDto.setPassword("pppp"+UUID.randomUUID().toString());
            userDto.setIp("iiii"+UUID.randomUUID().toString().replace("-",""));
            if(i%10==0){
                userDto.setEmail("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"+i);
            }
            userDto.setCountry(UUID.randomUUID().toString().replace("-",""));
            userDto.setTelephone(UUID.randomUUID().toString().replace("-",""));
            userDto.setAddress(UUID.randomUUID().toString().replace("-",""));
            userDto.setPhraseId("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            userDto.setUuid(UUID.randomUUID().toString().replace("-",""));
            remoteUserDtoList.add(userDto);
        }
        long start = System.currentTimeMillis();
        CachingMap cachingMap = new CachingMap(localUserDtoList.size());
        for(UserDto userDto : localUserDtoList){
            cachingMap.putObjectData(userDto);
        }
        long start2 = System.currentTimeMillis();
        for(UserDto userDto : remoteUserDtoList){
            cachingMap.setGroupId(userDto,info);
        }
        System.out.println("封装数据的时间："+(start2-start));
        System.out.println("匹配结束："+(System.currentTimeMillis()-start2));
        List<Long> newList = remoteUserDtoList.stream().map(s-> Long.valueOf(s.getGroupId())).sorted().collect(Collectors.toList());
        System.out.println("========================执行完毕");
        for(Long exiryValue: cachingMap.getExpiryMap().keySet()){
            System.out.println(exiryValue);
        }
    }

    public static void test2(){
        long total = 600000;
        Info info = new Info(7000);
        List<UserDto> localUserDtoList = new ArrayList<>();
        for(long i=0;i<total;i++){
            UserDto userDto = new UserDto();
            userDto.setUsername("uuu"+UUID.randomUUID().toString());
            userDto.setPassword("pppp"+UUID.randomUUID().toString());
            userDto.setIp("iiii"+UUID.randomUUID().toString());
            userDto.setEmail("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"+i);
            userDto.setCountry(UUID.randomUUID().toString().replace("-",""));
            userDto.setTelephone(UUID.randomUUID().toString().replace("-",""));
            userDto.setAddress(UUID.randomUUID().toString().replace("-",""));
            userDto.setPhraseId("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            userDto.setUuid(UUID.randomUUID().toString().replace("-",""));
            userDto.setGroupId(""+i);
            localUserDtoList.add(userDto);
        }
        List<UserDto> remoteUserDtoList = new ArrayList<>();
        for(long i=0;i<total;i++){
            UserDto userDto = new UserDto();
            userDto.setUsername("uuu"+UUID.randomUUID().toString());
            userDto.setPassword("pppp"+UUID.randomUUID().toString());
            userDto.setIp("iiii"+UUID.randomUUID().toString().replace("-",""));
            if(i%10==0){
                userDto.setEmail("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"+i);
            }
            userDto.setCountry(UUID.randomUUID().toString().replace("-",""));
            userDto.setTelephone(UUID.randomUUID().toString().replace("-",""));
            userDto.setAddress(UUID.randomUUID().toString().replace("-",""));
            userDto.setPhraseId("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            userDto.setUuid(UUID.randomUUID().toString().replace("-",""));
            remoteUserDtoList.add(userDto);
        }
        long start = System.currentTimeMillis();
        CachingMap2 cachingMap2 = new CachingMap2(localUserDtoList.size());
        for(UserDto userDto : localUserDtoList){
            cachingMap2.putObjectData(userDto);
        }
        System.out.println();
        long start2 = System.currentTimeMillis();
        for(UserDto userDto : remoteUserDtoList){
            cachingMap2.setGroupId(userDto,info);
        }
        System.out.println("封装数据的时间："+(start2-start));
        System.out.println("匹配结束："+(System.currentTimeMillis()-start2));
        List<Long> newList = remoteUserDtoList.stream().map(s-> Long.valueOf(s.getGroupId())).sorted().collect(Collectors.toList());
        System.out.println("========================执行完毕");
    }
}
