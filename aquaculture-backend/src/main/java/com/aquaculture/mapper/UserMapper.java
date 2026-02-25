package com.aquaculture.mapper;

import com.aquaculture.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);
    
    @Select("SELECT * FROM user WHERE openid = #{openid}")
    User findByOpenid(String openid);
    
    @Insert("INSERT INTO user (openid, unionid, nickname, avatar_url, phone, role, status, created_at, updated_at) " +
            "VALUES (#{openid}, #{unionid}, #{nickname}, #{avatarUrl}, #{phone}, #{role}, #{status}, datetime('now'), datetime('now'))")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    @Update("UPDATE user SET nickname = #{nickname}, avatar_url = #{avatarUrl}, phone = #{phone}, updated_at = datetime('now') WHERE id = #{id}")
    int update(User user);
}
