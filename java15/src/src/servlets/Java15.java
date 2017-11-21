package servlets;


import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Java15 extends HttpServlet {
    private static Logger logger = Logger.getLogger(Java15.class.getName());
    private static String exceptionLiteral = "Exception";
    private static String userNameParameter = "userName";
    private static String loginURL = "/java15/login";

    private final SaverLoader data = new SaverLoader();
    private final ArrayList<ListEntry> list = new ArrayList<>();

    private Integer postNum;

    @Override
    public void init() {
        try {
            data.load();
        } catch (IOException e) {
            logger.log(Level.SEVERE, exceptionLiteral, exceptionLiteral);
        }
    }


    @Override
    public void destroy() {
        try {
            data.save();
        } catch (IOException e) {
            logger.log(Level.SEVERE, exceptionLiteral, exceptionLiteral);
        }
    }


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (PrintWriter writer = response.getWriter()) {
            String uri = request.getRequestURI();

            RequestDispatcher dispatcher;

            writer.println("<html>\n<head>\n<title>\nTemp1\n</title>\n");
            String url = "style1.css";
            writer.println(String.format("<LINK href=\"\" rel=\"stylesheet\" type=\"text/css\">",url));
            writer.println("</head>\n");

            HttpSession session;

            switch (uri) {
                case "/java15/main":
                    session = request.getSession();
                    String userName = (String) session.getAttribute(userNameParameter);


                    if (userName == null) {
                        dispatcher = request.getRequestDispatcher("/java15/login");
                        writer.println("<a href=\"/java15/register\">Register</a>");

                    } else {
                        dispatcher = request.getRequestDispatcher("/java15/welcome");
                    }

                    dispatcher.include(request, response);
                    break;
                case "/java15/register":
                    dispatcher = request.getRequestDispatcher("/java15/registerForm");
                    dispatcher.include(request, response);
                    break;
                case "/java15/logout":
                    session = request.getSession(false);

                    if (session != null) {
                        session.invalidate();
                    }

                    dispatcher = request.getRequestDispatcher("/java15/main");
                    dispatcher.forward(request, response);
                    break;
                case "/java15/list":
                    session = request.getSession();
                    String name = (String) session.getAttribute(userNameParameter);

                    boolean auth = true;

                    if (name == null) {
                        auth = false;
                        writer.println("Login to leave a new comment");
                        dispatcher =  request.getRequestDispatcher("/java15/login");
                        dispatcher.include(request, response);
                    } else {
                        writer.println("<a href = \"/java15/welcome\">Add new comment</a>");
                    }

                    writer.println(generateList(auth));

                    if (name != null) {
                        writer.println("<br><a href=\"/java15/logout\">Logout</a>");
                    }

                    break;
                case "/java15/response":
                    session = request.getSession();

                    String num = request.getParameter("num");
                    session.setAttribute("numb", num);
                    writer.println("<form method=\"POST\" action=\"/java15/addResponse\"");
                    writer.println("<br><textarea rows=\"5\" cols=\"40\" name = \"text\"></textarea>");
                    writer.println("<br><input type = \"submit\" value = \"Response\">");
                    writer.println("</form>");


                    break;
                default:
                    break;

            }
            writer.println("</html>");

        } catch (IOException e) {

            logger.log(Level.SEVERE, exceptionLiteral, exceptionLiteral);
        }
    }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (PrintWriter writer = response.getWriter()) {
            String uri = request.getRequestURI();

            RequestDispatcher dispatcher;

            writer.println("<html>\n<head>\n<title>\nTemp2\n</title>\n");
            String url = "style1.css";
            writer.println(String.format("<link rel=\"stylesheet\" type=\"text/css\" href=\"%s\"/>",url));
            writer.println("</head>\n");

            String login;
            String pword;
            HashMap<String, String> logpass = data.getData();

            switch (uri) {
                case "/java15/authenticate":
                    login = request.getParameter("login");
                    pword = request.getParameter("password");



                    if (logpass.containsKey(login) && logpass.get(login).equals(pword)) {
                        HttpSession session = request.getSession(true);
                        session.setAttribute(userNameParameter, login);
                        dispatcher = request.getRequestDispatcher("/java15/welcome");
                    } else {
                        dispatcher = request.getRequestDispatcher("/java15/login");
                        writer.println("Wrong login and/or password");
                        dispatcher.include(request, response);
                        return;
                    }

                    dispatcher.include(request, response);
                    break;
                case "/java15/registerIt":
                    login = request.getParameter("login");
                    pword = request.getParameter("password");

                    if (logpass.containsKey(login)) {
                        writer.println("Error: username already taken");
                        dispatcher = request.getRequestDispatcher("/java15/registerForm");
                        dispatcher.include(request, response);
                        break;
                    } else {
                        logpass.put(login, pword);
                        writer.println("Registration complete. You can now log in:");
                        dispatcher = request.getRequestDispatcher("/java15/login");
                        dispatcher.include(request, response);
                    }
                    break;
                case "/java15/comment":
                    HttpSession session = request.getSession();
                    String name = (String) session.getAttribute(userNameParameter);

                    if (name == null) {
                        writer.println("<br>Access error<br>");
                        writer.println("<a href = \"/java15/main\">Back to main page</a>");
                        break;
                    }

                    String title = request.getParameter("title");
                    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    Date today = Calendar.getInstance().getTime();
                    String date = df.format(today);

                    String content = request.getParameter("text");

                    ListEntry newEntry = new ListEntry(name, title, date, content);

                    list.add(newEntry);

                    writer.println(generateList(true));
                    writer.println("<a href = \"/java15/welcome\">Add new comment</a>");
                    break;
                case "/java15/addResponse":
                    HttpSession sess = request.getSession();

                    name = (String) sess.getAttribute(userNameParameter);
                    String num = (String) sess.getAttribute("numb");
                    sess.removeAttribute("numb");
                    name = (String) sess.getAttribute(userNameParameter);

                    String text = request.getParameter("text");

                    list.get(Integer.parseInt(num)).addResponse(text, name);

                    writer.println("<a href=\"/java15/list\">Response added. Click here to view list</a>");
                    break;
                default:
                    break;
            }
            writer.println("</html>");
        } catch (IOException e) {
            logger.log(Level.SEVERE, exceptionLiteral, exceptionLiteral);
        }
    }


    private String generateList(boolean auth) {
        StringBuilder listStr = new StringBuilder();
        Integer num = new Integer(list.size() - 1);

        if (auth) {
            listStr.append("<a href=\"/java15/response?num=" + num.toString() + "\">Response</a>");
        }

        for (int k = list.size() - 1; k >=0; k--) {
            ListEntry entry = list.get(k);

            listStr.append(String.format("<h1>%s</h1><h2>by %s</h2><h3>at %s</h3><br><br><h4>%s</h4>%n",
                    entry.getTitle(), entry.getAuthorName(), entry.getDate(), entry.getContent()));

            listStr.append("<h4>Comments:</h4>");
            listStr.append("<hr width=\"70%\" align = \"left\">");

            ArrayList<String> responses = entry.getResponses();
            ArrayList<String> responsesAuthours = entry.getResponsesAuthours();

            for (int i = 0; i < responses.size(); i++) {
                listStr.append(String.format("<p>%s<p><br>by <b>%s</b>", responses.get(i), responsesAuthours.get(i)));
                if (i != responses.size() - 1) {
                    listStr.append("<hr width=\"70%\" align = \"left\">");
                }
            }

            listStr.append("<br>");



            listStr.append("<hr size=\"6\" noshade>");
            num--;
        }


        return listStr.toString();
    }


    private class SaverLoader {
        private  HashMap<String, String> logpass = new HashMap<>();

        private synchronized void load() throws IOException {
            File file = new File("../../IdeaProjects/Java15/DATA/logpass.txt");

            if (!file.exists()) {
                throw new IOException("File can not be accessed");
            }


            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

                String line;

                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split(":");
                    logpass.put(tokens[0], tokens[1]);
                }

            }
        }


        private synchronized void save() throws IOException {
            File file = new File("../../IdeaProjects/Java15/DATA/logpass.txt");

            if (!file.exists()) {
                boolean ok = file.createNewFile();

                if (!ok) {
                    throw new IOException("File can not be accessed");
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (Map.Entry<String, String> entry : logpass.entrySet()){
                    writer.write(String.format("%s:%s%n", entry.getKey(), entry.getValue()));
                }
            }
        }


        private HashMap<String, String> getData() {
            return logpass;
        }

    }


    private class ListEntry {
        private String authorName;
        private String title;
        private String date;
        private String content;
        private ArrayList<String> responses = new ArrayList<>();
        private ArrayList<String> responsesAuthours = new ArrayList<>();


        private ListEntry(String name, String title, String time, String text) {
            authorName = name;
            this.title = title;
            date = time;
            content = text;
        }


        private String getAuthorName() {
            return authorName;
        }

        private String getDate() {
            return date;
        }

        private String getContent() {
            return content;
        }

        private String getTitle() {
            return title;
        }

        private void addResponse(String response, String author) {
            responses.add(response);
            responsesAuthours.add(author);
        }


        private ArrayList<String> getResponses() {
            return responses;
        }

        private ArrayList<String> getResponsesAuthours() {
            return responsesAuthours;
        }
    }
}
