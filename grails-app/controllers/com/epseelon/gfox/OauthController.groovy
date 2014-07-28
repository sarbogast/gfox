package com.epseelon.gfox

import grails.converters.JSON
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import org.springframework.http.HttpStatus
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

class OauthController {
    static final MYFOX_OAUTH_URL = "https://api.myfox.me/oauth2/token"
    static final MYFOX_AUTHORIZE_URL = "https://api.myfox.me/oauth2/authorize"
    static final SESSION_MYFOX_UUID_KEY = "myfoxUUID"
    static final SESSION_ACCESS_TOKEN_KEY = "access_token"
    static final SESSION_REFRESH_TOKEN_KEY = "refresh_token"
    static final SESSION_TOKEN_EXPIRATION_KEY = "access_token_expiration"
    static final SESSION_SOURCE_KEY = "source"

    def rest = new RestBuilder()

    def index() {
        def clientId = grailsApplication.config.gfox.client.id
        def uuid = UUID.randomUUID().toString()
        session[SESSION_MYFOX_UUID_KEY] = uuid
        def redirectUrl = createLink(controller:'oauth', action:'callback', absolute:true)
        def url = "${MYFOX_AUTHORIZE_URL}?response_type=code&client_id=${clientId.encodeAsURL()}&redirect_uri=${redirectUrl.encodeAsURL()}&state=${uuid}"
        redirect(url: url)
    }

    def refresh() {
        def clientId = grailsApplication.config.gfox.client.id
        def clientSecret = grailsApplication.config.gfox.client.secret

        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>()
        form.add("grant_type", "refresh_token")
        form.add("refresh_token", session["refresh_token"] as String)
        form.add("redirect_uri", createLink(controller:'oauth', action:'callback', absolute:true).toString())

        RestResponse resp = rest.post(MYFOX_OAUTH_URL) {
            auth clientId, clientSecret
            contentType "application/x-www-form-urlencoded"
            body form
        }

        if(resp.responseEntity.statusCode == HttpStatus.OK && resp.json.access_token != null){
            session[SESSION_ACCESS_TOKEN_KEY] = resp.json.access_token
            session[SESSION_REFRESH_TOKEN_KEY] = resp.json.refresh_token
            Date now = new Date()
            session[SESSION_TOKEN_EXPIRATION_KEY] = new Date(now.time + resp.json.expires_in * 1000 as long).time
        }

        redirect controller:'home'
    }

    def callback() {
        def clientId = grailsApplication.config.gfox.client.id
        def clientSecret = grailsApplication.config.gfox.client.secret
        def authorizationCode = params.code

        if(params.state?.equals(session[SESSION_MYFOX_UUID_KEY])){
            MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>()
            form.add("grant_type", "authorization_code")
            form.add("code", authorizationCode)
            form.add("redirect_uri", createLink(controller:'oauth', action:'callback', absolute:true).toString())

            RestResponse resp = rest.post(MYFOX_OAUTH_URL) {
                auth clientId, clientSecret
                contentType "application/x-www-form-urlencoded"
                body form
            }
            if(resp.responseEntity.statusCode == HttpStatus.OK && resp.json.access_token != null){
                session[SESSION_ACCESS_TOKEN_KEY] = resp.json.access_token
                session[SESSION_REFRESH_TOKEN_KEY] = resp.json.refresh_token
                Date now = new Date()
                session[SESSION_TOKEN_EXPIRATION_KEY] = new Date(now.time + resp.json.expires_in * 1000 as long).time
            } else if(resp.json.error){
                log.error "${resp.json.error} : ${resp.json.error_description}"
                flash.message = resp.json.error_description
            }
            redirect controller:'home'
        }
    }

    def logout(){
        session.invalidate()
        redirect controller: 'home'
    }
}
