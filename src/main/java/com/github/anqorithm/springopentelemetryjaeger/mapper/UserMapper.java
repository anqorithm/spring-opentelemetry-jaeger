package com.github.anqorithm.springopentelemetryjaeger.mapper;

import com.github.anqorithm.springopentelemetryjaeger.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT id, name, email, created_at, updated_at FROM users WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "email", column = "email"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    User findById(@Param("id") String id);

    @Select("SELECT id, name, email, created_at, updated_at FROM users ORDER BY created_at DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "email", column = "email"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    List<User> findAll();

    @Select("SELECT id, name, email, created_at, updated_at FROM users WHERE name ILIKE CONCAT('%', #{name}, '%') ORDER BY created_at DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "email", column = "email"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    List<User> findByName(@Param("name") String name);

    @Select("SELECT id, name, email, created_at, updated_at FROM users WHERE email = #{email}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "email", column = "email"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    User findByEmail(@Param("email") String email);

    @Insert("INSERT INTO users (id, name, email, created_at, updated_at) VALUES (#{id}, #{name}, #{email}, #{createdAt}, #{updatedAt})")
    int insert(User user);

    @Update("UPDATE users SET name = #{name}, email = #{email}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(User user);

    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteById(@Param("id") String id);

    @Select("SELECT COUNT(*) FROM users")
    long count();
}