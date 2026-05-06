package com.zekiloni.george.platform.infrastructure.support.dev;

import com.zekiloni.george.platform.application.port.in.page.PageTemplateUseCase;
import com.zekiloni.george.platform.domain.model.page.PageTemplate;
import com.zekiloni.george.platform.domain.model.page.TemplateSource;
import com.zekiloni.george.platform.domain.model.page.definition.PageDefinition;
import com.zekiloni.george.platform.domain.service.page.HtmlToComponentNodeConverter;
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
import java.util.Locale;

/**
 * Dev-profile only. On startup, scans {@code classpath:page-templates/*.html},
 * converts each to a {@link PageDefinition}, and upserts a BUILTIN
 * {@link PageTemplate} per file. User-created templates (source=USER) are
 * never touched.
 *
 * <p>Drop a new file in {@code src/main/resources/page-templates/} and restart
 * the dev server to seed it.
 */
@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DevPageTemplateLoader {

    private static final String LOCATION = "classpath:page-templates/*.html";

    private final HtmlToComponentNodeConverter converter;
    private final PageTemplateUseCase templateUseCase;

    @EventListener(ApplicationReadyEvent.class)
    public void seed() {
        Resource[] resources;
        try {
            resources = new PathMatchingResourcePatternResolver().getResources(LOCATION);
        } catch (IOException e) {
            log.warn("Could not scan {} for page templates: {}", LOCATION, e.getMessage());
            return;
        }
        if (resources.length == 0) {
            log.info("No page templates found at {}", LOCATION);
            return;
        }

        int seeded = 0;
        for (Resource resource : resources) {
            String filename = resource.getFilename();
            if (filename == null) continue;
            String title = humanizeFilename(filename);
            try (InputStream in = resource.getInputStream()) {
                String html = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                PageDefinition definition = converter.convert(html);
                PageTemplate template = PageTemplate.builder()
                        .title(title)
                        .slug(slugify(filename))
                        .description("Seeded from " + filename)
                        .source(TemplateSource.BUILTIN)
                        .definition(definition)
                        .build();
                templateUseCase.upsertBuiltin(title, template);
                seeded++;
            } catch (IOException e) {
                log.warn("Skipping page template {}: {}", filename, e.getMessage());
            }
        }
        log.info("Seeded {} BUILTIN page template(s) from {}", seeded, LOCATION);
    }

    private static String humanizeFilename(String filename) {
        String base = filename.replaceFirst("\\.html$", "");
        return base.replace('-', ' ').replace('_', ' ').toLowerCase(Locale.ROOT);
    }

    private static String slugify(String filename) {
        return filename.replaceFirst("\\.html$", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
}
