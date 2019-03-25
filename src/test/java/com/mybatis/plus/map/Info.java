package com.mybatis.plus.map;

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
