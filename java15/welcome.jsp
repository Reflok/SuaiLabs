<% out.println(String.format("Hello %s!", session.getAttribute("userName"))); %>

<form method="POST" action="/java15/comment">
    Title:<br> <input type = "text" size="40" name = "title">
    Text: <br><textarea rows="5" cols="40" name = "text"></textarea>
    <br><br><input type = "submit" value = "Make new comment">
</form>

<br><a href="/java15/list">View list</a>
<br><a href="/java15/logout">Logout</a>
