package com.mybatis.plus.job;

import com.mybatis.plus.cache.CachingMap;
import com.mybatis.plus.dto.Info;
import com.mybatis.plus.dto.UserDto;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class DataSyncJob implements Runnable {

    private CachingMap cachingMap;

    private long total =30000;

    private Info info =new Info(total);

    private long time=0;

    @Override
    public void run() {
        time++;
        List<UserDto> remoteUserList = getRemoteUserList();
        long start =System.currentTimeMillis();
        if(null==cachingMap){
            List<UserDto> localUserList = getLocalUserList();
            cachingMap = new CachingMap(localUserList.size());
            cachingMap.putAllObjectData(localUserList);
        }
        if(cachingMap.size()==0){
            List<UserDto> localUserList = getLocalUserList();
            cachingMap.putAllObjectData(localUserList);
        }
        cachingMap.setAllGroupId(remoteUserList,info);
        cachingMap.clearInvalidData();
        log.info("当前最大个数为："+info.getMaxGroupId());
        log.info(String.format("第%s次执行,缓存数量：%s,所花费的时间为：%s",
                time,cachingMap.size(),(System.currentTimeMillis()-start)));

    }

    public List<UserDto> getLocalUserList(){
        List<UserDto> localUserDtoList = new ArrayList<>();
        for(long i=0;i<total;i++){
            UserDto userDto = new UserDto();
            userDto.setUsername("uuu"+ UUID.randomUUID().toString());
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
        return localUserDtoList;
    }

    public List<UserDto> getRemoteUserList(){
        List<UserDto> remoteUserDtoList = new ArrayList<>();
        for(long i=0;i<60000;i++){
            UserDto userDto = new UserDto();
            userDto.setUsername("uuu"+UUID.randomUUID().toString());
            userDto.setPassword("pppp"+UUID.randomUUID().toString());
            userDto.setIp("iiii"+UUID.randomUUID().toString().replace("-",""));
            if((int)(Math.random()*10+1)%10==0){
                userDto.setEmail("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"+i);
            }
            userDto.setCountry(UUID.randomUUID().toString().replace("-",""));
            userDto.setTelephone(UUID.randomUUID().toString().replace("-",""));
            userDto.setAddress(UUID.randomUUID().toString().replace("-",""));
            userDto.setPhraseId("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            userDto.setUuid(UUID.randomUUID().toString().replace("-",""));
            remoteUserDtoList.add(userDto);
        }
        return remoteUserDtoList;
    }
}
