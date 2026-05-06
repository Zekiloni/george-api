<#macro registrationLayout bodyClass="" displayInfo=false displayMessage=true displayRequiredFields=false>
<!DOCTYPE html>
<html class="${properties.kcHtmlClass!}" lang="${lang}"<#if realm.internationalizationEnabled> dir="${(locale.rtl)?then('rtl','ltr')}"</#if>>

<head>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${msg("loginTitle",(realm.displayName!''))}</title>
    <link rel="icon" href="${url.resourcesPath}/img/favicon.ico" />
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <#if properties.stylesCommon?has_content>
        <#list properties.stylesCommon?split(' ') as style>
            <link href="${url.resourcesCommonPath}/${style}" rel="stylesheet" />
        </#list>
    </#if>
    <#if properties.styles?has_content>
        <#list properties.styles?split(' ') as style>
            <link href="${url.resourcesPath}/${style}" rel="stylesheet" />
        </#list>
    </#if>
    <script type="module" src="${url.resourcesPath}/js/passwordVisibility.js"></script>
    <#if scripts??>
        <#list scripts as script>
            <script src="${script}" type="text/javascript"></script>
        </#list>
    </#if>
</head>

<body class="relay-body ${bodyClass}">

<div class="relay-shell">
    <header class="relay-header">
        <a class="relay-brand" href="${url.loginUrl!'#'}">
            <span class="relay-brand-mark">R</span>
            <span class="relay-brand-name">${realm.displayName!realm.name}</span>
        </a>

        <#if realm.internationalizationEnabled  && locale.supported?size gt 1>
            <div class="relay-locale">
                <details>
                    <summary>${locale.current}</summary>
                    <ul role="menu">
                        <#list locale.supported as l>
                            <li><a href="${l.url}">${l.label}</a></li>
                        </#list>
                    </ul>
                </details>
            </div>
        </#if>
    </header>

    <main class="relay-main">
        <div class="relay-card">
            <#-- Page title (or attempted-username chip) -->
            <div class="relay-card-header">
                <#if !(auth?has_content && auth.showUsername() && !auth.showResetCredentials())>
                    <h1 id="kc-page-title"><#nested "header"></h1>
                <#else>
                    <div class="relay-attempted-user" id="kc-username">
                        <span id="kc-attempted-username">${auth.attemptedUsername}</span>
                        <a id="reset-login" href="${url.loginRestartFlowUrl}" aria-label="${msg('restartLoginTooltip')}">
                            ${msg("restartLoginTooltip")}
                        </a>
                    </div>
                </#if>
                <#if displayRequiredFields>
                    <p class="relay-required-hint"><span class="relay-required">*</span> ${msg("requiredFields")}</p>
                </#if>
            </div>

            <#-- Inline messages (errors/info/success/warning) -->
            <#if displayMessage && message?has_content && (message.type != 'warning' || !isAppInitiatedAction??)>
                <div class="relay-alert relay-alert-${message.type}" role="alert">
                    <#if message.type = 'success'><span aria-hidden="true">✓</span></#if>
                    <#if message.type = 'warning'><span aria-hidden="true">!</span></#if>
                    <#if message.type = 'error'><span aria-hidden="true">✕</span></#if>
                    <#if message.type = 'info'><span aria-hidden="true">i</span></#if>
                    <span class="relay-alert-text">${kcSanitize(message.summary)?no_esc}</span>
                </div>
            </#if>

            <#-- Page-supplied content (form, social providers, info) -->
            <div id="kc-content">
                <div id="kc-content-wrapper">
                    <#nested "form">

                    <#if auth?has_content && auth.showTryAnotherWayLink()>
                        <form id="kc-select-try-another-way-form" action="${url.loginAction}" method="post" class="relay-try-another">
                            <input type="hidden" name="tryAnotherWay" value="on"/>
                            <a href="#" id="try-another-way" onclick="document.forms['kc-select-try-another-way-form'].submit(); return false;">
                                ${msg("doTryAnotherWay")}
                            </a>
                        </form>
                    </#if>

                    <#nested "socialProviders">

                    <#if displayInfo>
                        <div id="kc-info" class="relay-info">
                            <div id="kc-info-wrapper">
                                <#nested "info">
                            </div>
                        </div>
                    </#if>
                </div>
            </div>
        </div>

        <footer class="relay-footer">
            © ${.now?string('yyyy')} ${realm.displayName!realm.name}
        </footer>
    </main>
</div>

</body>
</html>
</#macro>
