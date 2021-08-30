package com.pica.miaosha.service;

import com.pica.miaosha.dao.UserDao;
import com.pica.miaosha.domian.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public User getById(Integer id){
        return userDao.getUserById(id);
    }

    @Transactional
    public boolean tx() {
        User user = new User();
        user.setId(3);
        user.setName("jack");
        userDao.insert(user);

        User kitty = new User(1, "kitty");
        userDao.insert(kitty);

        return true;
    }
}
