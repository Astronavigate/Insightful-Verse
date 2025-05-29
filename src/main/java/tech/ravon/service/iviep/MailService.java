package tech.ravon.service.iviep;

import jakarta.servlet.http.HttpServletRequest;

public interface MailService {
    String getCaptcha(HttpServletRequest request);
    String check(HttpServletRequest request, String input);
    String check(HttpServletRequest request, String input, String email);
}
