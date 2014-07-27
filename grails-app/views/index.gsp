<!DOCTYPE html>
<html>
<head>
    <title>MyFox Test App</title>

</head>

<body>
<h1>MyFox Test App</h1>
<g:if test="${!session["access_token"]}">
    <p><g:link controller="oauth">Click here</g:link> to connect to MyFox</p>
</g:if>
<g:if test="${session["access_token"]}">
    <p>Access token: ${session["access_token"]}</p>

    <p>Refresh token: ${session["refresh_token"]}</p>

    <p>Expires on: ${new Date(session["access_token_expiration"] as long).toString()}</p>
</g:if>
</body>
</html>
