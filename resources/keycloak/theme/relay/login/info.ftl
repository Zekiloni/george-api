<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false; section>
    <#if section = "header">
        <#if messageHeader??>
            ${messageHeader}
        <#else>
            ${message.summary}
        </#if>
    <#elseif section = "form">
        <div id="kc-info-message">
            <p class="instruction" style="font-size: 0.95rem; line-height: 1.5; color: var(--relay-text-muted);">
                ${message.summary?no_esc}
                <#if requiredActions??>
                    <#list requiredActions>: <#items as reqActionItem>${msg("requiredAction.${reqActionItem}")}<#sep>, </#sep></#items></#list>
                </#if>
            </p>
            <#if skipLink??>
            <#else>
                <#if pageRedirectUri?has_content>
                    <div id="kc-form-buttons" style="margin-top: 1.25rem;">
                        <a class="relay-btn-primary" href="${pageRedirectUri}" style="text-decoration: none;">
                            ${kcSanitize(msg("backToApplication"))?no_esc}
                        </a>
                    </div>
                <#elseif actionUri?has_content>
                    <div id="kc-form-buttons" style="margin-top: 1.25rem;">
                        <a class="relay-btn-primary" href="${actionUri}" style="text-decoration: none;">
                            ${kcSanitize(msg("proceedWithAction"))?no_esc}
                        </a>
                    </div>
                <#elseif client?? && client.baseUrl?has_content>
                    <div id="kc-form-buttons" style="margin-top: 1.25rem;">
                        <a class="relay-btn-primary" href="${client.baseUrl}" style="text-decoration: none;">
                            ${kcSanitize(msg("backToApplication"))?no_esc}
                        </a>
                    </div>
                </#if>
            </#if>
        </div>
    </#if>
</@layout.registrationLayout>
