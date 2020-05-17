package com.shang.springsecurity.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * <p>角色</p>
 *
 * @Author: 尚家朋
 * @Email: 835091332@qq.com
 * @Date: 2020/4/22 15:29
 */
@Entity(name = "t_role")  //项目启动后自动创建表
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//id

    private String  name;//角色名字
    private String nameZh;//角色中文名字


}

