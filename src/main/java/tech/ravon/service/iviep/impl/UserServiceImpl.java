package tech.ravon.service.iviep.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import tech.ravon.model.iviep.User;
import tech.ravon.service.iviep.MailService;
import tech.ravon.service.iviep.UserService;
import tech.ravon.mapper.UserDao;
import tech.ravon.service.iviep.ViewRecordService;
import tech.ravon.lib.Hash;
import org.springframework.stereotype.Service;

/**
* @author Anubis
* @description 针对表【users】的数据库操作Service实现
* @createDate 2024-11-03 21:56:02
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User>
    implements UserService{

    @Autowired
    UserDao userDao;
    @Autowired
    MailService mailService;
    @Autowired
    ViewRecordService viewRecordService;

    @Override
    public User Login(HttpServletRequest request, HttpServletResponse response) {
        String identifier = request.getParameter("identifier");
        String password = request.getParameter("password");
        User user = null;
        user = userDao.getUserInfo(identifier);
        if (Hash.verify(user.getPassword(), password)) {
            return user;
        } else {
            System.out.println("VERIFY RESULT " + Hash.verify(user.getPassword(), password));
            return null;
        }
    }

    @Override
    public void Logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().removeAttribute("user");
    }

    @Override
    public String Register(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String rePassword = request.getParameter("rePassword");
        String captcha = request.getParameter("captcha");

        if (!password.equals(rePassword)) {
            return "Register failed, the two passwords entered do not match.";
        }

        String errInfo = mailService.check(request, captcha);
        if (errInfo != null) {
            return errInfo;
        }

        int result = 0;

        try {
            result = userDao.doUserRegister(username, email, phone, Hash.calculate(password), null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result > 0) {
            User user = userDao.getUserInfo(email);
            request.getSession().setAttribute("user", user);
        } else {
            return  "Register failed, please check if your email and phone number are unique.";
        }
        return null;
    }

    @Override
    public String Unregister(HttpServletRequest request, HttpServletResponse response) {
        String password = request.getParameter("password");
        String captcha = request.getParameter("captcha");
        String userId = null;
        userId = String.valueOf(((User) request.getSession().getAttribute("user")).getUserId());
        String errInfo = mailService.check(request, captcha);
        if (errInfo != null) {
            return errInfo;
        }
        if (userId == null) {
            return "Login invalid, please login again.";
        }
        User user = userDao.getUserInfo(userId);
        if (Hash.verify(user.getPassword(), password)) {
            int result = 0;
            viewRecordService.delRecordByUserId(userId);
            result = userDao.unregisterUser(userId);
            if (result < 1) {
                return "Delete account error, just try again.";
            }
            return null;
        } else {
            return "Delete account error, please check your password.";
        }
    }

    @Override
    public User Userinfo(HttpServletRequest request) {
        String identifier = null;
        User user = (User) request.getSession().getAttribute("user");
        if (user != null && user.getUserId() != null) {
            identifier = String.valueOf(user.getUserId());
        }
        if (identifier == null) {
            identifier = request.getParameter("userId");
        }
        if (identifier == null) {
            identifier = request.getParameter("identifier");
        }
        user = userDao.getUserInfo(identifier);
        return user;
    }

    @Override
    public String UpdateUser(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String rePassword = request.getParameter("rePassword");
        String oldPassword = request.getParameter("oldPassword");
        String captcha = request.getParameter("captcha");
        User user = Userinfo(request);

        User originUser = (User) request.getSession().getAttribute("user");
        String userId = String.valueOf(originUser.getUserId());

        String errInfo = mailService.check(request, captcha, user.getEmail());

        if (errInfo != null) {
            return errInfo;
        }
        if (userId == null) {
            return "Login invalid, please login again.";
        }
        if (!password.equals(rePassword)) {
            return "Register failed, the two passwords entered do not match.";
        }
        if (Hash.verify(originUser.getPassword(), oldPassword)) {
            int result = userDao.updateUserInfo(userId, username, email, phone, Hash.calculate(password));

            if (result <= 0) {
                return "Update failed, just try again.";
            } else {
                request.getSession().setAttribute("user", user);
            }
            return null;
        } else {
            return "Update failed, please check your password and try again.";
        }
    }

    @Override
    public User verifyPerm(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return null;
        } else if (user.getAuthority().equals("infinite")) {
            String captcha = request.getParameter("captcha");
            String result = mailService.check(request, captcha, user.getEmail());
            if (result == null) {
                return user;
            }
        }
        return null;
    }
}
