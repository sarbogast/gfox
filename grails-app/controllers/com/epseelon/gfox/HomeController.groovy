package com.epseelon.gfox

class HomeController {

    def index() {

        render view:'/index', model: [
                accessToken: session[OauthController.SESSION_ACCESS_TOKEN_KEY],
                refreshToken: session[OauthController.SESSION_REFRESH_TOKEN_KEY],
                accessTokenExpirationTimestamp: session[OauthController.SESSION_TOKEN_EXPIRATION_KEY]
        ]
    }
}
