package com.shang.springsecurity.service;

import com.shang.springsecurity.dao.UserDao;
import com.shang.springsecurity.model.UserWithJpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * <p></p>
 *
 * @Author: 尚家朋
 * @Email: 835091332@qq.com
 * @Date: 2020/4/22 16:10
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserWithJpa user = userDao.findUserWithJpaByUsername(username);
        if (user==null){
            throw new  UsernameNotFoundException("用户名不存在");
        }
        return user;
    }
}
