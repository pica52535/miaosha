package com.pica.miaosha.dao;

import com.pica.miaosha.domian.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDao {

    @Select("select * from user where id = #{id}")
    public User getUserById(Integer id);

    @Insert("insert into user(id,name) values(#{id},#{name})")
    int insert(User user);
}
