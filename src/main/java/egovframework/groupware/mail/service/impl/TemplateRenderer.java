package egovframework.groupware.mail.service.impl;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {{key}} 형태의 단순 변수 치환기. 외부 라이브러리 의존 없이 안전하게 처리.
 */
@Component
public class TemplateRenderer {

    private static final Pattern VAR = Pattern.compile("\\{\\{\\s*([\\w\\.]+)\\s*\\}\\}");

    public String render(String template, Map<String, Object> vars) {
        if (template == null || template.isBlank() || vars == null || vars.isEmpty()) return template;
        Matcher m = VAR.matcher(template);
        StringBuilder out = new StringBuilder();
        while (m.find()) {
            Object val = vars.get(m.group(1));
            m.appendReplacement(out, Matcher.quoteReplacement(val == null ? "" : val.toString()));
        }
        m.appendTail(out);
        return out.toString();
    }
}
