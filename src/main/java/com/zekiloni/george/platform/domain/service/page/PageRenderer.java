package com.zekiloni.george.platform.domain.service.page;

import com.zekiloni.george.platform.domain.model.page.definition.PageDefinition;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Substitutes {{var}} placeholders in a PageDefinition's html with values from
// its `variables` map (overrides allowed at render time), then wraps the body
// in a full HTML document with the page-scoped CSS inlined as a <style> block.
@Component
public class PageRenderer {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{\\s*([a-zA-Z0-9_]+)\\s*\\}\\}");

    public String render(PageDefinition definition, Map<String, String> overrides) {
        if (definition == null || definition.getHtml() == null) return "";
        Map<String, String> values = mergeVariables(definition.getVariables(), overrides);
        String body = substitute(definition.getHtml(), values);
        String css = definition.getCss() == null ? "" : definition.getCss();
        return wrapDocument(body, css);
    }

    public String renderBodyOnly(PageDefinition definition, Map<String, String> overrides) {
        if (definition == null || definition.getHtml() == null) return "";
        Map<String, String> values = mergeVariables(definition.getVariables(), overrides);
        return substitute(definition.getHtml(), values);
    }

    private static Map<String, String> mergeVariables(Map<String, String> base, Map<String, String> overrides) {
        if (base == null && overrides == null) return Map.of();
        if (overrides == null) return base;
        if (base == null) return overrides;
        java.util.HashMap<String, String> merged = new java.util.HashMap<>(base);
        merged.putAll(overrides);
        return merged;
    }

    private static String substitute(String html, Map<String, String> values) {
        Matcher matcher = PLACEHOLDER.matcher(html);
        StringBuilder out = new StringBuilder(html.length());
        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement = values.getOrDefault(key, "");
            matcher.appendReplacement(out, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(out);
        return out.toString();
    }

    private static String wrapDocument(String body, String css) {
        return """
                <!doctype html>
                <html>
                <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width,initial-scale=1">
                <style>%s</style>
                </head>
                <body>%s</body>
                </html>
                """.formatted(css, body);
    }
}
