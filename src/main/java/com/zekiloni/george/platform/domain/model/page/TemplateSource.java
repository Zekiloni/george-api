package com.zekiloni.george.platform.domain.model.page;

public enum TemplateSource {
    /** Seeded from a classpath HTML file by the dev support loader. */
    BUILTIN,
    /** Created or edited by an end user via the API. */
    USER
}
