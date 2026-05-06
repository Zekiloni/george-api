<#import "template.ftl" as layout>
<#import "field.ftl" as field>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('username','password') displayInfo=realm.password && realm.registrationAllowed && !registrationDisabled??; section>
    <#if section = "header">
        ${msg("loginAccountTitle")}
    <#elseif section = "form">
        <#if realm.password>
            <form id="kc-form-login" action="${url.loginAction}" method="post" novalidate="novalidate"
                  onsubmit="login.disabled = true; return true;" class="relay-form">

                <#if !usernameHidden??>
                    <#assign label>
                        <#if !realm.loginWithEmailAllowed>${msg("username")}<#elseif !realm.registrationEmailAsUsername>${msg("usernameOrEmail")}<#else>${msg("email")}</#if>
                    </#assign>
                    <@field.input name="username" label=label autofocus=true autocomplete="username"
                        value=login.username!''
                        error=kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc />
                </#if>

                <#-- error="" so the combined username/password message renders only
                     once (under the username field) instead of duplicating here. -->
                <@field.password name="password" label=msg("password") error=""
                    forgotPassword=realm.resetPasswordAllowed
                    autofocus=usernameHidden??
                    autocomplete="current-password">
                    <#if realm.rememberMe && !usernameHidden??>
                        <@field.checkbox name="rememberMe" label=msg("rememberMe") value=login.rememberMe?? />
                    </#if>
                </@field.password>

                <div id="kc-form-buttons">
                    <input type="hidden" id="id-hidden-input" name="credentialId"
                           <#if auth?has_content && auth.selectedCredential?has_content>value="${auth.selectedCredential}"</#if>/>
                    <input class="relay-btn-primary" name="login" id="kc-login" type="submit" value="${msg('doLogIn')}"/>
                </div>
            </form>
        </#if>
    <#elseif section = "info">
        <#if realm.password && realm.registrationAllowed && !registrationDisabled??>
            <div id="kc-registration">
                <span>${msg("noAccount")} <a href="${url.registrationUrl}">${msg("doRegister")}</a></span>
            </div>
        </#if>
    <#elseif section = "socialProviders">
        <#if realm.password && social?? && social.providers?has_content>
            <div id="kc-social-providers">
                <h2>${msg("identity-provider-login-label")}</h2>
                <ul>
                    <#list social.providers as p>
                        <li>
                            <a id="social-${p.alias}" class="kc-social-provider-link"
                               href="${p.loginUrl}">
                                <#if p.iconClasses?has_content>
                                    <i class="${properties.kcCommonLogoIdP!} ${p.iconClasses!}" aria-hidden="true"></i>
                                </#if>
                                <span>${p.displayName!}</span>
                            </a>
                        </li>
                    </#list>
                </ul>
            </div>
        </#if>
    </#if>
</@layout.registrationLayout>
