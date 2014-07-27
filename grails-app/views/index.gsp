<%@ page import="com.epseelon.gfox.OauthController" %>
<!DOCTYPE html>
<html>
<head>
    <title>MyFox Test App</title>

</head>

<body>
<h1>MyFox Test App</h1>
<g:if test="${!accessToken}">
    <p><g:link controller="oauth">Click here</g:link> to connect to MyFox</p>
</g:if>
<g:if test="${accessToken}">
    <p>Access token: ${accessToken}</p>

    <p>Refresh token: ${refreshToken}</p>

    <p>Expires on: ${new Date(accessTokenExpirationTimestamp as long).toString()}</p>

    <p><g:link controller="oauth" action="refresh">Refresh token</g:link></p>
</g:if>
</body>
</html>
