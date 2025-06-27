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

package tech.ravon.service.iviep.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.ravon.mapper.UserDao;
import tech.ravon.model.iviep.User;
import tech.ravon.service.iviep.MailService;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    UserDao userDao;

    private static final String USERNAME = "1078959112@qq.com";
    private static final String PASSWORD = "dchgxqefkyazjjcc";

    @Override
    public String getCaptcha(HttpServletRequest request){
        String email = request.getParameter("email");
        String errMessage = null;
        if (email == null || email.isEmpty()) {
            User user = (User) request.getSession().getAttribute("user");
            if (user == null || user.getUserId() == null) {
                errMessage = "Send failed, please check your login status.";
            } else {
                email = user.getEmail();
                System.out.println(user);
                System.out.println(user.getEmail());
            }
        }
        System.out.println(email);
        String Captcha = generateCaptcha();

        saveCaptchaInSession(request, Captcha, email);
        String sendResult = sendCaptchaEmail(email, Captcha);
        if (sendResult != null && errMessage != null) {
            errMessage = sendResult;
        }

        return errMessage;
    }

    private String generateCaptcha() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(8);
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    private void saveCaptchaInSession(HttpServletRequest request, String Captcha, String email) {
        HttpSession session = request.getSession();
        session.setAttribute("Captcha", Captcha);
        session.setAttribute("CaptchaCreateTime", System.currentTimeMillis());
        session.setAttribute("VerifyEmail", email);
    }

    private String sendCaptchaEmail(String email, String Captcha) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true"); // Use SSL connection
        props.put("mail.smtp.host", "smtp.qq.com"); // Replace with your SMTP server
        props.put("mail.smtp.port", "465"); // SSL port

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME, "Insightful Verse"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Your verification code");

            // Construct email content
            String content = "<h2>Hello!</h2>"
                    + "<p>Your verification code is: <strong>" + Captcha + "</strong></p>"
                    + "<p>This code will expire in 5 minutes.</p>"
                    + "<p>Thank you for using our service.</p><br>"
                    + "<p>From <a href=\"https://eagloxis.tech/InsightfulVerse.run\">Insightful Verse Integrated Educating Platform</a></p>";

            message.setContent(content, "text/html");

            Transport.send(message);

            System.out.println("Email sent successfully to " + email);
            return null;

        } catch (Exception e) {
            System.err.println("Error sending email to " + email + ": " + e.getMessage());
            System.out.println("Captcha is " + Captcha);
            return "Failed to send email: " + e.getMessage();
        }
    }

    @Override
    public String check(HttpServletRequest request, String input) {
        HttpSession session = request.getSession();
        String savedCaptcha = (String) session.getAttribute("Captcha");
        Long createTime = (Long) session.getAttribute("CaptchaCreateTime");

        if (savedCaptcha != null && createTime != null) {
            long currentTime = System.currentTimeMillis();
            long timeElapsed = currentTime - createTime;
            if (timeElapsed <= 5 * 60 * 1000) {
                if (input != null && input.equals(savedCaptcha)) {
                    session.setAttribute("CaptchaCreateTime", (long) 0);
                    return null;
                } else {
                    return("Verify failed, the email verification code was entered incorrectly.");
                }
            } else {
                return("Verify failed, the email verification code has expired, please try again.");
            }
        } else {
            return("Verify failed, please re-enter the email verification code.");
        }
    }

    @Override
    public String check(HttpServletRequest request, String input, String email) {
        HttpSession session = request.getSession();
        String savedCaptcha = (String) session.getAttribute("Captcha");
        String savedEmail = (String) session.getAttribute("VerifyEmail");
        Long createTime = (Long) session.getAttribute("CaptchaCreateTime");

        if (savedCaptcha != null && createTime != null) {
            long currentTime = System.currentTimeMillis();
            long timeElapsed = currentTime - createTime;
            if (timeElapsed <= 5 * 60 * 1000) {
                if (input != null && input.equals(savedCaptcha)) {
                    session.setAttribute("CaptchaCreateTime", (long) 0);
                    if (!email.equals(savedEmail)) {
                        return "Verify failed, the email verification code is invalid, please send again.";
                    }
                    return null;
                } else {
                    return("Verify failed, the email verification code was entered incorrectly.");
                }
            } else {
                return("Verify failed, the email verification code has expired, please try again.");
            }
        } else {
            return("Verify failed, please re-enter the email verification code.");
        }
    }
}
