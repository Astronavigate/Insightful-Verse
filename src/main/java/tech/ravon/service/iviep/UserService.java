package tech.ravon.service.iviep;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tech.ravon.model.iviep.User;

public interface UserService extends IService<User> {

    User Login(HttpServletRequest request, HttpServletResponse response);

    void Logout(HttpServletRequest request, HttpServletResponse response);

    String Register(HttpServletRequest request, HttpServletResponse response);

    String Unregister(HttpServletRequest request, HttpServletResponse response);

    User Userinfo(HttpServletRequest request);

    String UpdateUser(HttpServletRequest request, HttpServletResponse response);

    User verifyPerm(HttpServletRequest request, HttpServletResponse response);

}
