<br>

<% if (request.getRequestURI().equals("/java15/list") || request.getRequestURI().equals("/java15/main")
        || request.getRequestURI().equals("/java15/authenticate")) {
    out.println("<form method=\"POST\" action=\"/java15/authenticate\">");
}
else if (request.getRequestURI().equals("/java15/registerForm")) {
    out.println("<form method=\"POST\" action=\"/java15/registerIt\">");
}

    %>
    <br><a href="/java15/list">View list</a><br>
    Login: <input type = "text" name = "login">
    Password: <input type = "password" name = "password">
    <br><br>
    <% if (request.getRequestURI().equals("/java15/list") || request.getRequestURI().equals("/java15/main")
            || request.getRequestURI().equals("/java15/authenticate")) {
        out.println("<input type = \"submit\" value = \"Login\">");
    }
    else if (request.getRequestURI().equals("/java15/register")) {
        out.println("<input type = \"submit\" value = \"Register\">");
    }
    %>
</form>
