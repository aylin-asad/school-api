package az.iktex.organization.structure.testders.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import java.util.Locale;

public class LanguageLocaleResolver extends AcceptHeaderLocaleResolver {
    @Override public Locale resolveLocale(HttpServletRequest request) {
        String lang = request.getParameter("lang");
        if (lang == null || lang.isBlank()) lang = request.getHeader("Accept-Language");
        if (lang == null || lang.isBlank()) return Locale.forLanguageTag("az");
        String primary = lang.split(",", 2)[0].split(";", 2)[0].trim().toLowerCase(Locale.ROOT);
        String code = primary.split("[-_]", 2)[0];
        return switch (code) {
            case "az", "aze" -> Locale.forLanguageTag("az");
            case "en", "eng" -> Locale.ENGLISH;
            case "ru", "rus" -> Locale.forLanguageTag("ru");
            default -> Locale.forLanguageTag("az");
        };
    }
}
