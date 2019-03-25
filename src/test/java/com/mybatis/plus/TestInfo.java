package com.mybatis.plus;

public class TestInfo {

    private static final Info info= new Info();

    public static void main(String[] args) {
        info.setId("1");
        System.out.println(info);
    }
}
