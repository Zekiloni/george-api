<#import "template.ftl" as layout>
<#import "field.ftl" as field>
<#import "register-commons.ftl" as registerCommons>
<#import "user-profile-commons.ftl" as userProfileCommons>
<@layout.registrationLayout displayMessage=messagesPerField.exists('global') displayRequiredFields=true; section>
    <#if section = "header">
        ${msg("registerTitle")}
    <#elseif section = "form">
        <form id="kc-register-form" action="${url.registrationAction}" method="post" class="relay-form">
            <@userProfileCommons.userProfileFormFields/>

            <#if recaptchaRequired?? && (recaptchaVisible!false)>
                <div class="form-group">
                    <div class="g-recaptcha" data-size="compact" data-sitekey="${recaptchaSiteKey}" data-action="${recaptchaAction}"></div>
                </div>
            </#if>

            <@registerCommons.termsAcceptance/>

            <#if recaptchaRequired?? && !(recaptchaVisible!false)>
                <script>
                    function onSubmitRecaptcha(token) {
                        document.getElementById("kc-register-form").submit();
                    }
                </script>
                <div id="kc-form-buttons">
                    <button class="relay-btn-primary g-recaptcha"
                            data-sitekey="${recaptchaSiteKey}" data-callback='onSubmitRecaptcha'
                            data-action='${recaptchaAction}' type="submit">
                        ${msg("doRegister")}
                    </button>
                </div>
            <#else>
                <div id="kc-form-buttons">
                    <input class="relay-btn-primary" type="submit" value="${msg('doRegister')}"/>
                </div>
            </#if>

            <div id="kc-form-options" style="margin-top: 1rem;">
                <a href="${url.loginUrl}">${kcSanitize(msg("backToLogin"))?no_esc}</a>
            </div>
        </form>
    </#if>
</@layout.registrationLayout>
