package tech.ravon.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import tech.ravon.service.unres.UniResService;


@RequiredArgsConstructor
@Controller
@Slf4j
public class UniResController {

    private final UniResService uniResService;

    @RequestMapping("/UniRes/{code}")
    public String ursIndex(
            @PathVariable String code, HttpServletRequest request) {

        // 1. 基础校验：拒绝非法的编码格式（如长度不足、非法字符）
        if (code == null || code.length() != 10 || code.length() != 15 || code.length() != 24) {
            return "/error";
        } else {

        // 2. 调用 Service 获取数据
        // Service 内部已实现“查询当前节点 + 获取直接下级”的逻辑
        return "/UniRes/index";
    }
}
}

/*
URC（36进制）：
1-2位是地区代码，3-5位是厂商代码，6-9位是产品代码，第10位校验，、
（可选）11-14位批次代码（子批次，最大32 x 周数，千年循环），15位校验（1-14位），
（可选）16-20位是流水代码，21-24位认证码

兼容EAN/UPC，
第一位小于等于3即使UPC/EAN代码，这9位转10进制即是原始代码，10位照常校验，11位之后与URC无异

认证码根据上次的认证码+工厂ID+操作ID加权计算得出，使用Argon2id保护
*/