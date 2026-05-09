package com.zekiloni.george.platform.infrastructure.support.dev;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zekiloni.george.platform.application.port.in.page.PageTemplateUseCase;
import com.zekiloni.george.platform.domain.model.page.PageTemplate;
import com.zekiloni.george.platform.domain.model.page.TemplateSource;
import com.zekiloni.george.platform.domain.model.page.definition.PageDefinition;
import com.zekiloni.george.platform.domain.model.page.template.TemplateManifest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;

// Dev-profile only. Each template lives in its own directory:
//   page-templates/<id>/template.html
//   page-templates/<id>/template.css
//   page-templates/<id>/manifest.json
// On startup we walk every directory, build a PageTemplate, and upsert it as
// BUILTIN. USER templates (created via API) are never touched.
@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DevPageTemplateLoader {

    private static final String LOCATION = "classpath:page-templates/*/manifest.json";

    private final PageTemplateUseCase templateUseCase;
    private final ObjectMapper objectMapper;

    @EventListener(ApplicationReadyEvent.class)
    public void seed() {
        Resource[] manifests;
        try {
            manifests = new PathMatchingResourcePatternResolver().getResources(LOCATION);
        } catch (IOException e) {
            log.warn("Could not scan {} for page templates: {}", LOCATION, e.getMessage());
            return;
        }
        if (manifests.length == 0) {
            log.info("No page templates found at {}", LOCATION);
            return;
        }

        int seeded = 0;
        for (Resource manifestResource : manifests) {
            try {
                String dir = templateDirName(manifestResource);
                if (dir == null) continue;

                TemplateManifest manifest = readJson(manifestResource, TemplateManifest.class);
                String html = readSibling(manifestResource, "template.html");
                String css = readSibling(manifestResource, "template.css");

                if (html == null || html.isBlank()) {
                    log.warn("Skipping template {}: missing template.html", dir);
                    continue;
                }

                String title = humanize(dir);
                PageDefinition definition = PageDefinition.builder()
                        .html(html)
                        .css(css)
                        .variables(defaultVariables(manifest))
                        .form(manifest != null ? manifest.getForm() : null)
                        .build();

                PageTemplate template = PageTemplate.builder()
                        .title(title)
                        .slug(dir)
                        .description(null)
                        .source(TemplateSource.BUILTIN)
                        .definition(definition)
                        .manifest(manifest)
                        .build();
                templateUseCase.upsertBuiltin(title, template);
                seeded++;
            } catch (IOException e) {
                log.warn("Skipping template {}: {}", manifestResource.getFilename(), e.getMessage());
            }
        }
        log.info("Seeded {} BUILTIN page template(s)", seeded);
    }

    private <T> T readJson(Resource resource, Class<T> type) throws IOException {
        try (InputStream in = resource.getInputStream()) {
            return objectMapper.readValue(in.readAllBytes(), type);
        }
    }

    private static String readSibling(Resource manifestResource, String filename) throws IOException {
        Resource sibling = manifestResource.createRelative(filename);
        if (!sibling.exists()) return null;
        try (InputStream in = sibling.getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    // Pull the template directory name out of the manifest URL — robust to both
    // jar:file: and file: URLs.
    private static String templateDirName(Resource manifestResource) {
        try {
            String url = manifestResource.getURL().toString();
            int end = url.lastIndexOf("/manifest.json");
            if (end < 0) return null;
            int start = url.lastIndexOf('/', end - 1);
            if (start < 0) return null;
            return url.substring(start + 1, end);
        } catch (IOException e) {
            return null;
        }
    }

    private static String humanize(String dir) {
        return dir.replace('-', ' ').replace('_', ' ').toLowerCase(Locale.ROOT);
    }

    private static java.util.Map<String, String> defaultVariables(TemplateManifest manifest) {
        java.util.Map<String, String> out = new HashMap<>();
        if (manifest == null || manifest.getFields() == null) return out;
        manifest.getFields().forEach(f -> {
            if (f.getDefaultValue() != null) out.put(f.getKey(), f.getDefaultValue());
        });
        return out;
    }
}
