package com.epseelon.gfox

import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import org.springframework.http.HttpStatus
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

class OauthController {
    def rest = new RestBuilder()

    def index() {
        def clientId = grailsApplication.config.gfox.client.id
        def uuid = UUID.randomUUID().toString()
        session["myfoxUUID"] = uuid
        def redirectUrl = createLink(controller:'oauth', action:'callback', absolute:true)
        def url = "https://api.myfox.me/oauth2/authorize?response_type=code&client_id=${clientId.encodeAsURL()}&redirect_uri=${redirectUrl.encodeAsURL()}&state=${uuid}"
        redirect(url: url)
    }

    def callback() {
        def clientId = grailsApplication.config.gfox.client.id
        def clientSecret = grailsApplication.config.gfox.client.secret

        if(params.state?.equals(session["myfoxUUID"])){
            println "code: ${params.code}"

            def url = "https://api.myfox.me/oauth2/token"

            MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>()
            form.add("grant_type", "authorization_code")
            form.add("code", params.code)
            form.add("redirect_uri", createLink(controller:'oauth', action:'callback', absolute:true).toString())

            RestResponse resp = rest.post(url, form) {
                auth clientId, clientSecret
                contentType "application/x-www-form-urlencoded"
                body form
            }
            println resp.json
            if(resp.responseEntity.statusCode == HttpStatus.OK && resp.json.access_token != null){
                session["access_token"] = resp.json.access_token
                session["refresh_token"] = resp.json.refresh_token
                Date now = new Date()
                session["access_token_expiration"] = new Date(now.time + resp.json.expires_in * 1000 as long).time
            }
        }
        redirect uri:'/'
    }
}
