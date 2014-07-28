package com.epseelon.gfox

import grails.converters.JSON

class HomeController {

    def index() {
        def accessToken = session[OauthController.SESSION_ACCESS_TOKEN_KEY] as String
        def refreshToken = session[OauthController.SESSION_REFRESH_TOKEN_KEY] as String
        def accessTokenExpirationTimestamp = session[OauthController.SESSION_TOKEN_EXPIRATION_KEY] as Long
        def source = params.source

        if(accessToken != null){
            def now = new Date().time
            if(accessTokenExpirationTimestamp - 10*60 > now){
                //if the token expires in more than 10 minutes, we can still use it
                if(session[OauthController.SESSION_SOURCE_KEY] == 'pebble'){
                    def result = [
                            accessToken: accessToken,
                            refreshToken: refreshToken,
                            accessTokenExpirationTimestamp: accessTokenExpirationTimestamp / 1000
                    ]

                    redirect uri:"pebblejs://close#${(result as JSON).toString(false).encodeAsURL()}"
                } else {
                    render view:'/index', model: [
                            accessToken: accessToken,
                            refreshToken: refreshToken,
                            accessTokenExpirationTimestamp: accessTokenExpirationTimestamp
                    ]
                }
            } else {
                redirect controller: 'oauth', action: 'refresh'
            }
        } else {
            session[OauthController.SESSION_SOURCE_KEY] = source
            redirect controller: 'oauth', action: 'index'
        }
    }
}
