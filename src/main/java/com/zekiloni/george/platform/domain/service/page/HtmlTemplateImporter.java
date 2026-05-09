package com.zekiloni.george.platform.domain.service.page;

import com.zekiloni.george.platform.domain.model.page.PageTemplate;
import com.zekiloni.george.platform.domain.model.page.TemplateSource;
import com.zekiloni.george.platform.domain.model.page.definition.PageDefinition;
import com.zekiloni.george.platform.domain.model.page.template.FieldType;
import com.zekiloni.george.platform.domain.model.page.template.FormConfig;
import com.zekiloni.george.platform.domain.model.page.template.FormField;
import com.zekiloni.george.platform.domain.model.page.template.TemplateField;
import com.zekiloni.george.platform.domain.model.page.template.TemplateManifest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Parses an uploaded HTML file into a draft PageTemplate. Nothing is persisted —
// the caller decides what to do with the returned object (typically: hand it
// back to the page builder so the user can edit before saving).
//
// What we extract:
//   • <body> innerHtml         → PageDefinition.html
//   • all <style> blocks       → PageDefinition.css (concatenated)
//   • <title>                  → template title (filename used as fallback)
//   • {{var}} placeholders     → manifest.fields[] (default type=TEXT)
//   • <form> + <input>/<select>/<textarea> with `name` → manifest.form.fields[]
@Component
public class HtmlTemplateImporter {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{\\s*([a-zA-Z0-9_]+)\\s*\\}\\}");

    public PageTemplate parse(String html, String filenameHint) {
        Document doc = Jsoup.parse(html);
        doc.outputSettings().prettyPrint(false);

        String title = pickTitle(doc, filenameHint);
        String css = extractCss(doc);
        String body = extractBody(doc);
        TemplateManifest manifest = inferManifest(body);

        PageDefinition definition = PageDefinition.builder()
                .html(body)
                .css(css)
                .form(manifest.getForm())
                .build();

        return PageTemplate.builder()
                .title(title)
                .slug(slugify(title))
                .source(TemplateSource.USER)
                .definition(definition)
                .manifest(manifest)
                .build();
    }

    private static String pickTitle(Document doc, String filenameHint) {
        String fromTitle = doc.selectFirst("title") != null ? doc.selectFirst("title").text() : null;
        if (fromTitle != null && !fromTitle.isBlank()) return fromTitle.trim();
        if (filenameHint != null && !filenameHint.isBlank()) {
            return filenameHint.replaceFirst("\\.[Hh][Tt][Mm][Ll]?$", "")
                    .replace('-', ' ').replace('_', ' ').trim();
        }
        return "Imported template";
    }

    private static String extractCss(Document doc) {
        StringBuilder out = new StringBuilder();
        for (Element style : doc.select("style")) {
            String css = style.data();
            if (css != null && !css.isBlank()) {
                if (out.length() > 0) out.append("\n\n");
                out.append(css);
            }
            style.remove();
        }
        // Inline <link rel="stylesheet" href="..."> isn't fetched here — too
        // much network exposure. We keep the <link> tags in the body so the
        // designer can decide to inline manually or accept the external load.
        return out.toString();
    }

    private static String extractBody(Document doc) {
        Element body = doc.body();
        return body == null ? doc.outerHtml() : body.html();
    }

    private static TemplateManifest inferManifest(String html) {
        List<TemplateField> fields = inferFields(html);
        FormConfig form = inferFormConfig(html);
        return TemplateManifest.builder()
                .fields(fields)
                .form(form)
                .build();
    }

    private static List<TemplateField> inferFields(String html) {
        Set<String> seen = new HashSet<>();
        List<TemplateField> fields = new ArrayList<>();
        Matcher m = PLACEHOLDER.matcher(html);
        while (m.find()) {
            String key = m.group(1);
            if (!seen.add(key)) continue;
            fields.add(TemplateField.builder()
                    .key(key)
                    .label(humanize(key))
                    .type(FieldType.TEXT)
                    .build());
        }
        return fields;
    }

    private static FormConfig inferFormConfig(String html) {
        // Re-parse the body so we can walk form descendants. Light-weight; runs
        // once at import time only.
        Document body = Jsoup.parseBodyFragment(html);
        Element form = body.selectFirst("form");
        if (form == null) return null;

        List<FormField> fields = new ArrayList<>();
        for (Element el : form.select("input[name], select[name], textarea[name]")) {
            String name = el.attr("name");
            String type = "input".equalsIgnoreCase(el.tagName())
                    ? el.attr("type").toLowerCase(Locale.ROOT)
                    : el.tagName().toLowerCase(Locale.ROOT);
            if (type.isBlank() || "hidden".equals(type) || "submit".equals(type) || "button".equals(type)) continue;
            String label = !el.attr("aria-label").isBlank() ? el.attr("aria-label")
                    : !el.attr("placeholder").isBlank() ? el.attr("placeholder")
                    : humanize(name);
            fields.add(FormField.builder()
                    .name(name)
                    .label(label)
                    .type(type)
                    .required(el.hasAttr("required"))
                    .placeholder(el.attr("placeholder"))
                    .build());
        }
        if (fields.isEmpty()) return null;
        return FormConfig.builder().fields(fields).build();
    }

    private static String humanize(String key) {
        if (key == null || key.isEmpty()) return key;
        String spaced = key.replaceAll("([a-z])([A-Z])", "$1 $2")
                .replace('_', ' ').replace('-', ' ').trim();
        return spaced.substring(0, 1).toUpperCase(Locale.ROOT) + spaced.substring(1);
    }

    private static String slugify(String title) {
        return title.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
}
