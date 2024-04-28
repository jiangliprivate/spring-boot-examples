package com.neo.mapper;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Mapper;

@Mapper // 使用 Mapper 注解
public interface FooMapper {

    /**
     * 获取数据库的当前时间
     * @return
     */
    LocalDateTime now();
}