<#import "template.ftl" as layout>
<#import "field.ftl" as field>
<#import "password-commons.ftl" as passwordCommons>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('password','password-confirm'); section>
    <#if section = "header">
        ${msg("updatePasswordTitle")}
    <#elseif section = "form">
        <form id="kc-passwd-update-form" action="${url.loginAction}" method="post" class="relay-form">
            <input readonly value="this is not a login form" style="display: none;" type="text">
            <input readonly value="this is not a password" style="display: none;" type="password" autocomplete="new-password">

            <@field.password name="password-new" label=msg("passwordNew") autocomplete="new-password" autofocus=true
                error=kcSanitize(messagesPerField.get('password'))?no_esc />

            <@field.password name="password-confirm" label=msg("passwordConfirm") autocomplete="new-password"
                error=kcSanitize(messagesPerField.get('password-confirm'))?no_esc />

            <@passwordCommons.logoutOtherSessions/>

            <div id="kc-form-buttons">
                <#if isAppInitiatedAction??>
                    <input class="relay-btn-primary" type="submit" value="${msg('doSubmit')}"/>
                    <button class="relay-btn-secondary" type="submit" name="cancel-aia" value="true" style="margin-top: 0.5rem;">
                        ${msg("doCancel")}
                    </button>
                <#else>
                    <input class="relay-btn-primary" type="submit" value="${msg('doSubmit')}"/>
                </#if>
            </div>
        </form>
    </#if>
</@layout.registrationLayout>
