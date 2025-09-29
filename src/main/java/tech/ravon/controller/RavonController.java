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

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
public class RavonController {
    @RequestMapping("/index")
    public ModelAndView Index() {
        ModelAndView mv = new ModelAndView("index");
        return  mv;
    }

    @RequestMapping("/about")
    public ModelAndView About() {
        ModelAndView mv = new ModelAndView("About");
        return  mv;
    }

    @RequestMapping("/products")
    public ModelAndView Products() {
        ModelAndView mv = new ModelAndView("Products");
        return  mv;
    }

    @RequestMapping("/support")
    public ModelAndView Support() {
        ModelAndView mv = new ModelAndView("Support");
        return  mv;
    }

    @RequestMapping("/myInfo")
    public ModelAndView MyInfo() {
        ModelAndView mv = new ModelAndView("MyInfo");
        return mv;
    }

    @RequestMapping("/login")
    public ModelAndView Login() {
        ModelAndView mv = new ModelAndView("Login");
        mv.addObject("finUrl", "index");
        return mv;
    }

    @RequestMapping("/register")
    public ModelAndView Register() {
        ModelAndView mv = new ModelAndView("Register");
        mv.addObject("finUrl", "index");
        return mv;
    }

    @RequestMapping("/unregister")
    public ModelAndView Unregister() {
        ModelAndView mv = new ModelAndView("Unregister");
        mv.addObject("finUrl", "index");
        return mv;
    }

    @RequestMapping("/updateInfo")
    public ModelAndView UpdateInfo() {
        ModelAndView mv = new ModelAndView("UpdateInfo");
        mv.addObject("finUrl", "myInfo");
        return mv;
    }

    @RequestMapping("/ChangeLang")
    public String ChangeLang(@RequestParam(defaultValue = "en") String lang, HttpServletRequest request, Model model) {
        String lastViewUrl = request.getHeader("Referer");
        if (!lastViewUrl.contains(".do") && !lastViewUrl.contains("Error")) {
            request.getSession().setAttribute("lastUrl", lastViewUrl);
        }
        // 统一小写，避免大小写问题
        lang = lang.toLowerCase();

        // 完整24种语言映射
        Map<String, String[]> langMap = new HashMap<>();
        langMap.put("ar", new String[]{"تغيير اللغة", "يرجى اختيار لغتك"});
        langMap.put("cs", new String[]{"Změnit jazyk", "Prosím vyberte jazyk"});
        langMap.put("da", new String[]{"Skift sprog", "Vælg venligst dit sprog"});
        langMap.put("de", new String[]{"Sprache ändern", "Bitte wählen Sie Ihre Sprache"});
        langMap.put("en", new String[]{"Change Language", "Please select your language"});
        langMap.put("es", new String[]{"Cambiar idioma", "Por favor seleccione su idioma"});
        langMap.put("fi", new String[]{"Vaihda kieltä", "Valitse kieli"});
        langMap.put("fr", new String[]{"Changer de langue", "Veuillez sélectionner votre langue"});
        langMap.put("hi", new String[]{"भाषा बदलें", "कृपया अपनी भाषा चुनें"});
        langMap.put("it", new String[]{"Cambia lingua", "Si prega di selezionare la lingua"});
        langMap.put("ja", new String[]{"言語を変更", "言語を選択してください"});
        langMap.put("ko", new String[]{"언어 변경", "언어를 선택하세요"});
        langMap.put("nl", new String[]{"Taal wijzigen", "Selecteer uw taal"});
        langMap.put("no", new String[]{"Bytt språk", "Vennligst velg språk"});
        langMap.put("pl", new String[]{"Zmień język", "Proszę wybrać język"});
        langMap.put("pt", new String[]{"Mudar idioma", "Por favor selecione seu idioma"});
        langMap.put("ro", new String[]{"Schimbă limba", "Vă rugăm să selectați limba"});
        langMap.put("ru", new String[]{"Сменить язык", "Пожалуйста, выберите ваш язык"});
        langMap.put("sv", new String[]{"Ändra språk", "Vänligen välj ditt språk"});
        langMap.put("th", new String[]{"เปลี่ยนภาษา", "กรุณาเลือกภาษาของคุณ"});
        langMap.put("tr", new String[]{"Dili değiştir", "Lütfen dilinizi seçin"});
        langMap.put("vi", new String[]{"Thay đổi ngôn ngữ", "Vui lòng chọn ngôn ngữ của bạn"});
        langMap.put("zhs", new String[]{"切换语言", "请选择你的语言"});
        langMap.put("zht", new String[]{"切換語言", "請選擇你的語言"});

        // fallback 语言
        String[] fallback = langMap.get(lang);

        // 获取对应翻译（没有就用英文）
        String[] texts = langMap.getOrDefault(lang, fallback);

        String title = texts[0];
        String content = texts[1];

        // 放到 model
        model.addAttribute("title", title);
        model.addAttribute("content", content);
        return "lang";
    }

    @RequestMapping("/ChangeLang.do")
    public String ChangeLangDo(@RequestParam String lang, HttpServletRequest request) {
        Object lastUrlObj = request.getSession().getAttribute("lastUrl");
        String lastViewUrl = (lastUrlObj != null) ? lastUrlObj.toString() : "";

        if (lastViewUrl.isEmpty()) {
            // 默认跳转语言首页
            return "redirect:/InsightfulVerse/" + lang + "/";
        }

        // 去掉协议和域名部分，只保留相对路径
        try {
            java.net.URI uri = new java.net.URI(lastViewUrl);
            if (uri.getPath() != null) {
                lastViewUrl = uri.getPath();
            }
        } catch (Exception ignored) {
            // 如果不是合法 URL，保持原值
        }

        // 去掉开头的 /
        if (lastViewUrl.startsWith("/")) {
            lastViewUrl = lastViewUrl.substring(1);
        }

        // 如果 URL 已经是 InsightfulVerse/xxx 开头
        if (lastViewUrl.startsWith("InsightfulVerse")) {
            // 检查是否已有语言段
            String[] parts = lastViewUrl.split("/", 3);
            // parts[0] = "InsightfulVerse"
            // parts[1] = 语言 (可能是 en/zh-cn/...)
            // parts[2] = 剩下的路径
            if (parts.length >= 2 && (parts[1].equals("en") || parts[1].equals("zh-cn"))) {
                // 替换语言部分
                return "redirect:/InsightfulVerse/" + lang + "/" + (parts.length >= 3 ? parts[2] : "");
            } else {
                // 没有语言部分，补一个
                return "redirect:/InsightfulVerse/" + lang + "/" + (parts.length >= 2 ? parts[1] : "");
            }
        } else {
            return "redirect:/" + lang + "/" + lastViewUrl;
        }
    }
}
