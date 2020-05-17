package com.shang.springsecurity;

import com.shang.springsecurity.dao.UserDao;
import com.shang.springsecurity.model.Role;
import com.shang.springsecurity.model.UserWithJpa;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class SpringSecurityApplicationTests {

    @Test
    void contextLoads() {


    }
    @Test
    void createEncodePassword(){

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("123"));
        System.out.println(encoder.encode("123"));
    }

    @Autowired
    UserDao userDao;

    @Test
    void testAddUser() {
        UserWithJpa user1 = new UserWithJpa();
        user1.setUsername("shang");
        user1.setPassword("123");
        List<Role> roles1 = new ArrayList<>();
        Role role1 = new Role();
        role1.setName("ROLE_admin");
        role1.setNameZh("管理员");
        roles1.add(role1);
        user1.setRoles(roles1);
        user1.setAccountNonExpired(true);
        user1.setAccountNonLocked(true);
        user1.setCredentialsNonExpired(true);
        user1.setEnabled(true);
        userDao.save(user1);
        
        UserWithJpa user2 = new UserWithJpa();
        user2.setUsername("kong");
        user2.setPassword("123");
        List<Role> roles2 = new ArrayList<>();
        Role role2 = new Role();
        role2.setName("ROLE_user");
        role2.setNameZh("普通用户");
        roles2.add(role2);
        user2.setRoles(roles2);
        user2.setAccountNonExpired(true);
        user2.setAccountNonLocked(true);
        user2.setCredentialsNonExpired(true);
        user2.setEnabled(true);
        userDao.save(user2);

    }
}
