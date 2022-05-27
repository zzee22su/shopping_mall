package com.shop.mapper;

import com.shop.domain.model.UserInfo;
import com.shop.domain.request.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper {
    int insertUser(User user);

    List<User> getUserList();

    User getFindByEmail(String email);

    UserInfo getUserInfo(String email);
}
