<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false; section>
    <#if section = "header">
        ${msg("errorTitle")}
    <#elseif section = "form">
        <div id="kc-error-message">
            <p class="instruction" style="font-size: 0.95rem; line-height: 1.5; color: var(--relay-text-muted);">
                ${kcSanitize(message.summary)?no_esc}
            </p>

            <#if skipLink??>
            <#else>
                <#if client?? && client.baseUrl?has_content>
                    <div id="kc-form-buttons" style="margin-top: 1.25rem;">
                        <a id="backToApplication" class="relay-btn-primary" href="${client.baseUrl}" style="text-decoration: none;">
                            ${kcSanitize(msg("backToApplication"))?no_esc}
                        </a>
                    </div>
                </#if>
            </#if>
        </div>
    </#if>
</@layout.registrationLayout>
