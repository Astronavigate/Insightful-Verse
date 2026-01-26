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

package tech.ravon.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Controller
public class RavonController {
    @RequestMapping("/index")
    public String index() {
        return "index";
    }

    @RequestMapping("/v1/index")
    public String v1Index() {
        return "v1Index";
    }

    @RequestMapping("/test")
    public String test() {
        return "test";
    }

    @RequestMapping("/about")
    public ModelAndView about() {
        ModelAndView mv = new ModelAndView("About");
        return mv;
    }

    @RequestMapping("/products")
    public ModelAndView products() {
        ModelAndView mv = new ModelAndView("Products");
        return mv;
    }

    @RequestMapping("/support")
    public ModelAndView support() {
        ModelAndView mv = new ModelAndView("Support");
        return mv;
    }

    @RequestMapping("/myInfo")
    public ModelAndView myInfo() {
        ModelAndView mv = new ModelAndView("MyInfo");
        return mv;
    }

    @RequestMapping("/login")
    public ModelAndView login() {
        ModelAndView mv = new ModelAndView("Login");
        mv.addObject("finUrl", "index");
        return mv;
    }

    @RequestMapping("/register")
    public ModelAndView register() {
        ModelAndView mv = new ModelAndView("Register");
        mv.addObject("finUrl", "index");
        return mv;
    }

    @RequestMapping("/unregister")
    public ModelAndView unregister() {
        ModelAndView mv = new ModelAndView("Unregister");
        mv.addObject("finUrl", "index");
        return mv;
    }

    @RequestMapping("/updateInfo")
    public ModelAndView updateInfo() {
        ModelAndView mv = new ModelAndView("UpdateInfo");
        mv.addObject("finUrl", "myInfo");
        return mv;
    }

    @RequestMapping("/ChangeLang")
    public String changeLang(@RequestParam(required = false) String lang,
                             HttpServletRequest request,
                             HttpServletResponse response,
                             Model model) {

        // 保存来源页（用于返回）
        String lastViewUrl = request.getHeader("Referer");
        if (lastViewUrl != null && !lastViewUrl.contains(".do") && !lastViewUrl.contains("Error")) {
            request.getSession().setAttribute("lastUrl", lastViewUrl);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("LANG".equalsIgnoreCase(cookie.getName())) {
                    lang = cookie.getValue();
                    break;
                }
            }
        }

        // 如果 Cookie 也没有，则默认英文
        if (lang == null || lang.isEmpty()) {
            lang = "en";
        }

        lang = lang.toLowerCase(Locale.ROOT);

        // 支持的 24 种语言
        Map<String, String[]> langMap = new HashMap<>();
        langMap.put("en", new String[]{"Change Language", "Please select your language"});
        langMap.put("zhs", new String[]{"切换语言", "请选择你的语言"});
        langMap.put("fr", new String[]{"Changer de langue", "Veuillez sélectionner votre langue"});
        langMap.put("de", new String[]{"Sprache ändern", "Bitte wählen Sie Ihre Sprache"});
        langMap.put("ja", new String[]{"言語を変更", "言語を選択してください"});
        langMap.put("ko", new String[]{"언어 변경", "언어를 선택하세요"});
        langMap.put("pt", new String[]{"Mudar idioma", "Por favor selecione seu idioma"});
        langMap.put("es", new String[]{"Cambiar idioma", "Por favor seleccione su idioma"});
        langMap.put("ar", new String[]{"تغيير اللغة", "يرجى اختيار لغتك"});
        langMap.put("zht", new String[]{"切換語言", "請選擇你的語言"});
        langMap.put("hi", new String[]{"भाषा बदलें", "कृपया अपनी भाषा चुनें"});
        langMap.put("it", new String[]{"Cambia lingua", "Seleziona la tua lingua"});
        langMap.put("nl", new String[]{"Taal wijzigen", "Selecteer uw taal"});
        langMap.put("pl", new String[]{"Zmień język", "Proszę wybrać język"});
        langMap.put("tr", new String[]{"Dil değiştir", "Lütfen dilinizi seçin"});
        langMap.put("sv", new String[]{"Byt språk", "Vänligen välj ditt språk"});
        langMap.put("cs", new String[]{"Změnit jazyk", "Vyberte svůj jazyk"});
        langMap.put("ro", new String[]{"Schimbă limba", "Vă rugăm să selectați limba"});
        langMap.put("da", new String[]{"Skift sprog", "Vælg venligst dit sprog"});
        langMap.put("fi", new String[]{"Vaihda kieli", "Valitse kielesi"});
        langMap.put("no", new String[]{"Endre språk", "Vennligst velg språket ditt"});
        langMap.put("th", new String[]{"เปลี่ยนภาษา", "โปรดเลือกภาษาของคุณ"});
        langMap.put("vi", new String[]{"Thay đổi ngôn ngữ", "Vui lòng chọn ngôn ngữ của bạn"});
        langMap.put("ru", new String[]{"Изменить язык", "Пожалуйста, выберите язык"});

        // 获取对应语言文本，默认英文
        String[] texts = langMap.getOrDefault(lang, langMap.get("en"));
        model.addAttribute("title", texts[0]);
        model.addAttribute("content", texts[1]);

        return "lang"; // 显示语言选择页面
    }

    @RequestMapping("/ChangeLang.do")
    public String changeLangDo(@RequestParam String lang,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        // 设置语言到 Cookie（无有效期 → 会话级）
        Cookie cookie = new Cookie("LANG", lang);
        cookie.setPath("/"); // 所有路径下生效
        response.addCookie(cookie);

        // 跳转回上次访问页面
        Object lastUrlObj = request.getSession().getAttribute("lastUrl");
        String lastViewUrl = (lastUrlObj != null) ? lastUrlObj.toString() : "/InsightfulVerse/";
        return "redirect:" + lastViewUrl;
    }
}
