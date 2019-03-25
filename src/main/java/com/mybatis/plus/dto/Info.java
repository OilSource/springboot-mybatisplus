package com.mybatis.plus.dto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Info {

    private long maxGroupId;

    public Info(long maxGroupId) {
        this.maxGroupId = maxGroupId;
    }

    public long getMaxGroupId(){
        this.maxGroupId =this.maxGroupId+1;
        return maxGroupId;
    }

}
