package com.epseelon.gfox

class HomeController {

    def index() {
        def accessToken = session[OauthController.SESSION_ACCESS_TOKEN_KEY] as String
        def refreshToken = session[OauthController.SESSION_REFRESH_TOKEN_KEY] as String
        def accessTokenExpirationTimestamp = session[OauthController.SESSION_TOKEN_EXPIRATION_KEY] as Long


        if(accessToken != null){
            def now = new Date().time
            if(accessTokenExpirationTimestamp - 10*60 > now){
                //if the token expires in more than 10 minutes, we can still use it
                render view:'/index', model: [
                        accessToken: accessToken,
                        refreshToken: refreshToken,
                        accessTokenExpirationTimestamp: accessTokenExpirationTimestamp
                ]
            } else {
                redirect controller: 'oauth', action: 'refresh'
            }
        } else {
            redirect controller: 'oauth', action: 'index'
        }
    }
}
