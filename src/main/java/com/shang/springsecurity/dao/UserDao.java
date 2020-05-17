package com.shang.springsecurity.dao;

import com.shang.springsecurity.model.UserWithJpa;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <p></p>
 *
 * @Author: 尚家朋
 * @Email: 835091332@qq.com
 * @Date: 2020/4/22 16:07
 */
public interface UserDao extends JpaRepository<UserWithJpa,Long> {

    /**
     * 根据用户名查询
     * @param username
     * @return
     */
    UserWithJpa findUserWithJpaByUsername(String username);

}
