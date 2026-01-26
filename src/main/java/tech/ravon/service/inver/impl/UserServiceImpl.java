/**
 * Copyright 2025 Astronavigate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.ravon.service.inver.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.ravon.model.inver.User;
import tech.ravon.service.inver.MailService;
import tech.ravon.service.inver.UserService;
import tech.ravon.mapper.UserDao;
import tech.ravon.service.inver.ViewRecordService;
import tech.ravon.lib.Hash;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserDao, User>
    implements UserService{

    private final UserDao userDao;
    private final MailService mailService;
    private final ViewRecordService viewRecordService;

    @Override
    public User login(String identifier, String password) {
        User user = null;
        user = userDao.getUserInfo(identifier);
        if (user == null){
            return null;
        }
        if (Hash.verify(user.getPassword(), password)) {
            return user;
        } else {
            log.warn("Password verify result: {}", Hash.verify(user.getPassword(), password));
            return null;
        }
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().removeAttribute("user");
    }

    @Override
    public String register(HttpServletRequest request, HttpServletResponse response) {
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

        try {
            userDao.registerUser(username, email, phone, Hash.calculate(password), null);
            User user = userDao.getUserInfo(email);
            request.getSession().setAttribute("user", user);
        } catch (Exception e) {
            log.error("Failed to register user. username={}, email={}, phone={}", username, email, phone, e);
            return  "Register failed, please check if your email and phone number are unique.\nError Detail: " + e;
        }
        return null;
    }

    @Override
    public String deleteAccount(HttpServletRequest request) {
        String password = request.getParameter("password");
        String captcha = request.getParameter("captcha");
        Long userId = null;
        userId = ((User) request.getSession().getAttribute("user")).getUserId();
        String errInfo = mailService.check(request, captcha);
        if (errInfo != null) {
            return errInfo;
        }
        if (userId == null) {
            return "Login invalid, please login again.";
        }
        User user = userDao.getUserInfo(userId.toString());
        if (Hash.verify(user.getPassword(), password)) {
            int result = 0;
            viewRecordService.delRecordByUserId(userId);
            result = userDao.deleteUser(userId);
            if (result < 1) {
                return "Delete account error, just try again.";
            }
            return null;
        } else {
            return "Delete account error, please check your password.";
        }
    }

    @Override
    public User userinfo(String identifier) {
        return userDao.getUserInfo(identifier);
    }

    @Override
    public String updateUser(HttpServletRequest request) {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String rePassword = request.getParameter("rePassword");
        String oldPassword = request.getParameter("oldPassword");
        String captcha = request.getParameter("captcha");

        User user = userinfo(email);

        User originUser = (User) request.getSession().getAttribute("user");

        if (!Objects.equals(user.getUserId(), originUser.getUserId())) {
            return "No permission operation other account.";
        }
        Long userId = originUser.getUserId();

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
