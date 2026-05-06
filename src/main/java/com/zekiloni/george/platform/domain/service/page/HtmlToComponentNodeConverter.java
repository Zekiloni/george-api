package com.zekiloni.george.platform.domain.service.page;

import com.zekiloni.george.platform.domain.model.page.definition.ComponentNode;
import com.zekiloni.george.platform.domain.model.page.definition.PageDefinition;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Pure HTML → {@link PageDefinition} mapping. Every DOM element becomes a
 * {@link ComponentNode} whose {@code type} is the lower-cased tag name and
 * {@code props} are the element's attributes (with {@code style} parsed to a
 * map and the raw declaration kept under {@code styleRaw}).
 *
 * <p>Special-cased:
 * <ul>
 *   <li>{@code <script>} elements are dropped (logged at debug); we don't run
 *       arbitrary third-party JS in builder pages — the renderer handles its
 *       own form submit interception.</li>
 *   <li>{@code <style>} blocks contribute to {@code globalStyles.css} (raw
 *       concatenated CSS) so the renderer can dump them into a {@code <style>}
 *       tag in {@code <head>}.</li>
 *   <li>{@code <link rel="stylesheet">} hrefs are collected into
 *       {@code globalStyles.externalStylesheets} for the renderer to hoist.</li>
 *   <li>Text nodes become {@link ComponentNode}s with {@code type="text"} and
 *       {@code props.value} carrying the literal text.</li>
 * </ul>
 */
@Service
@Slf4j
public class HtmlToComponentNodeConverter {

    public PageDefinition convert(String html) {
        Document doc = Jsoup.parse(html == null ? "" : html);
        Map<String, Object> globalStyles = new HashMap<>();
        StringBuilder cssBuffer = new StringBuilder();
        List<String> externalStylesheets = new ArrayList<>();

        // <head> never renders inline — peel its <style>/<link rel=stylesheet>
        // into globalStyles so the renderer can hoist them, and discard the
        // rest (title/meta/etc. — page-level metadata lives on Page itself).
        if (doc.head() != null) {
            for (Element styleEl : doc.head().select("style")) {
                cssBuffer.append(styleEl.data()).append('\n');
            }
            for (Element linkEl : doc.head().select("link[rel=stylesheet]")) {
                String href = linkEl.attr("href");
                if (!href.isBlank()) externalStylesheets.add(href);
            }
        }

        // Render root = <body>. Skipping the Document wrapper avoids emitting
        // a node whose `type` is jsoup's "#root" sentinel, which the frontend
        // renderer can't resolve.
        Element bodyEl = doc.body() != null ? doc.body() : doc;
        ComponentNode root = mapElement(bodyEl, cssBuffer, externalStylesheets);

        if (!cssBuffer.isEmpty()) globalStyles.put("css", cssBuffer.toString());
        if (!externalStylesheets.isEmpty()) globalStyles.put("externalStylesheets", externalStylesheets);

        return PageDefinition.builder()
                .root(root)
                .globalStyles(globalStyles)
                .variables(new HashMap<>())
                .build();
    }

    private ComponentNode mapElement(Element el, StringBuilder cssBuffer, List<String> externalStylesheets) {
        String tag = el.tagName().toLowerCase(Locale.ROOT);

        // Special handling for stylesheet sources: capture their content/href
        // and emit a placeholder node so the renderer can decide whether to
        // re-insert (we hoist via globalStyles, so a placeholder is fine).
        if ("style".equals(tag)) {
            cssBuffer.append(el.data()).append('\n');
            return ComponentNode.builder()
                    .id(UUID.randomUUID().toString())
                    .type("style-placeholder")
                    .props(new HashMap<>())
                    .children(new ArrayList<>())
                    .build();
        }
        if ("link".equals(tag) && "stylesheet".equalsIgnoreCase(el.attr("rel"))) {
            String href = el.attr("href");
            if (!href.isBlank()) externalStylesheets.add(href);
            // fall through — let the link node still render so head metadata is preserved.
        }

        Map<String, Object> props = new HashMap<>();
        for (Attribute attr : el.attributes()) {
            String name = attr.getKey();
            String value = attr.getValue();
            if ("style".equalsIgnoreCase(name)) {
                props.put("styleRaw", value);
                Map<String, String> styleMap = parseStyleAttribute(value);
                if (!styleMap.isEmpty()) props.put("style", styleMap);
                continue;
            }
            props.put(name, value);
        }

        List<ComponentNode> children = new ArrayList<>();
        int order = 0;
        for (Node child : el.childNodes()) {
            ComponentNode mapped = mapNode(child, cssBuffer, externalStylesheets);
            if (mapped == null) continue;
            mapped.setDisplayOrder(order++);
            children.add(mapped);
        }

        return ComponentNode.builder()
                .id(UUID.randomUUID().toString())
                .type(tag)
                .props(props)
                .children(children)
                .build();
    }

    private ComponentNode mapNode(Node node, StringBuilder cssBuffer, List<String> externalStylesheets) {
        if (node instanceof TextNode text) {
            String value = text.getWholeText();
            if (value.isBlank()) return null; // strip pure whitespace runs
            Map<String, Object> props = new HashMap<>();
            props.put("value", value);
            return ComponentNode.builder()
                    .id(UUID.randomUUID().toString())
                    .type("text")
                    .props(props)
                    .children(new ArrayList<>())
                    .build();
        }
        if (node instanceof Element element) {
            String tag = element.tagName().toLowerCase(Locale.ROOT);
            if ("script".equals(tag)) {
                log.debug("Stripped <script> from page template ({} chars)", element.data().length());
                return null;
            }
            return mapElement(element, cssBuffer, externalStylesheets);
        }
        // Comments, doctypes, etc. are dropped silently.
        return null;
    }

    private static Map<String, String> parseStyleAttribute(String style) {
        Map<String, String> result = new HashMap<>();
        if (style == null || style.isBlank()) return result;
        for (String declaration : style.split(";")) {
            int colon = declaration.indexOf(':');
            if (colon <= 0) continue;
            String property = declaration.substring(0, colon).trim();
            String value = declaration.substring(colon + 1).trim();
            if (!property.isEmpty() && !value.isEmpty()) {
                result.put(property, value);
            }
        }
        return result;
    }
}
