

(function (exp, $) {

    // This dumps all cached tokens to console, for easyer debugging.
//    jso_dump();


    $.oauth = function (settings) {
         console.log("====================="+settings.config.client_id);
         console.log("====================="+settings.config.authorization_url);
         console.log("====================="+settings.config.redirect_uri);
         console.log("====================="+settings.config.scope);
        jso_configure({
            "liferay": {
                client_id: settings.config.client_id,
                redirect_uri: settings.config.redirect_uri,
                authorization: settings.config.authorization_url,
                scope: settings.config.scope,
                isDefault: true
            }
        });


        // Make sure that you have
        jso_ensureTokens({
            "liferay": settings.config.scope
        });

        $.oajax({
            url: settings.url,
            jso_provider: "liferay",
            jso_allowia: false,
            jso_scopes: settings.config.scope,
            dataType: 'json',
            type:settings.type,
            data:settings.data,
            success: settings.success,
            error: settings.error

        });
    }


})(window, window.jQuery);