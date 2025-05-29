package tech.ravon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import tech.ravon.model.iviep.User;

@Mapper
public interface UserDao extends BaseMapper<User> {

    User checkUserLogin(String identifier, String password);

    User getUserInfo(String identifier);

    int doUserRegister(String username, String email, String phone, String password, String authority);

    int updateUserInfo(String userId, String username, String email, String phone, String password);

    int unregisterUser(String userId);

}
