package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
Реализовать сервлет для работы с записной книжкой. В записной книжке для каждого человека хранится его имя и список
телефонов (их может быть несколько). При старте сервлет загружает записную книжку из текстового файла. Сервлет должен
позволять:
Просматривать список записей
Добавить нового пользователя
Добавить новый телефон

На главной странице сервлет находится список записей. Вверху страницы ссылки --- добавить. Каждая из ссылок ведет на
отдельную страницу, где с помощью элементов <input type="text" name="username" /> в форме вводятся необходимые данные.
Для отправки данных сервлету есть кнопка submit.

В качестве контейнера сервлетов рекомендуется использовать либо сервер Tomcat, либо сервер Jetty

NB: Синхронизация при работе нескольких пользователей с одной записной книжкой.
*/

public class Java13 extends HttpServlet {
    private static Logger logger = Logger.getLogger(Java13.class.getName());
    private static String exceptionLiteral = "Exception";

    private static HashMap<String, ArrayList<String>> phoneBook = new HashMap<>();
    private static HashMap<String, String> avatars = new HashMap<>();


    @Override
    public void init() {
        try {
            load();
        } catch (IOException e) {
            logger.log(Level.SEVERE, exceptionLiteral, e);
        }
    }


    @Override
    public void destroy() {
        try {
            save();
        } catch (IOException e) {
            logger.log(Level.SEVERE, exceptionLiteral, e);
        }
    }


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();

        try (PrintWriter out = response.getWriter()) {
            out.println("<html>\n<body>\n");
            out.println("\n<br>\n");
            String name;
            String numbers;
            String avatar;
            String[] nums;

            switch (uri) {
                case "/java-13/phonebook":
                    out.println(generateList());
                    break;
                case "/java-13/phonebook/form":
                    out.println(generateForm());
                    break;
                case "/java-13/phonebook/add":
                    name = request.getParameter("name");
                    numbers = request.getParameter("numbers");
                    avatar = request.getParameter("avatar");


                if (numbers.contains(":")) {
                    nums = numbers.split(":");
                } else {
                    nums = new String[1];
                    nums[0] = numbers;
                }

                boolean allNums = true;

                for (String i : nums) {
                    if (!isNum(i)) {
                        allNums = false;
                        break;
                    }
                }

                if (!allNums || name.equals("")) {
                    out.println("Error in input<br>");
                    out.println("<a href=\"/java-13/phonebook\">Back to list</a>\n");
                } else {
                    add(name, nums);

                    avatars.put(name, avatar);
                    out.println(generateList());
                }

                break;
                default:
                    out.println("Nothing here!");
                    out.println("<a href=\"/java-13/phonebook/form\">Add</a>\n");
            }

            out.println("</body>\n</html>");
        } catch (IOException e) {
            logger.log(Level.SEVERE, exceptionLiteral, e);
        }
    }


    private String generateList() {
        StringBuilder list = new StringBuilder();

        list.append("<a href=\"/java-13/phonebook/form\">Add</a>\n");

        for (Map.Entry<String, ArrayList<String>> entry : phoneBook.entrySet()) {
            String name = entry.getKey();
            ArrayList<String> numbers = entry.getValue();

            list.append("<p><img src=\"http://localhost:8080/java-13/data/" + avatars.get(name) + ".jpg\" width=\"50\" height = \"50\">" + name + "</p>");
            list.append("<ul style=\"list-style=type:circle\">");

            for (String i : numbers) {
                list.append("<li>" + i + "</li>\n");
            }

            list.append("</ul>\n");
            list.append("<hr>\n");
        }

        return list.toString();
    }


    private String generateForm() {
        String form =  "<form method=\"GET\" action=\"/java-13/phonebook/add\">\n" +
                "Name: <input type=\"text\" name=\"name\">\n" +
                "Number: <input type=\"text\" name=\"numbers\">\n" +
                "<br>You can add multiple numbers by seperating them with \':\' symbol <br<br>>Chose avatar for new entry or chose new for existing entry" +
                "<br>\n" +
                "<input name=\"avatar\" type=\"radio\" value=\"cj\">" +
                "<img src=\"http://localhost:8080/java-13/data/cj.jpg\" width=\"100\" height = \"100\"><br>" +
                "<input name=\"avatar\" type=\"radio\" value=\"bob\">" +
                "<img src=\"http://localhost:8080/java-13/data/bob.jpg\" width=\"100\" height = \"100\"><br>" +
                "<input name=\"avatar\" type=\"radio\" value=\"bobby\">" +
                "<img src=\"http://localhost:8080/java-13/data/bobby.jpg\" width=\"100\" height = \"100\"><br>" +
                "<input name=\"avatar\" type=\"radio\" value=\"hmm\">" +
                "<img src=\"http://localhost:8080/java-13/data/hmm.jpg\" width=\"100\" height = \"100\"><br>" +
                "<input name=\"avatar\" type=\"radio\" value=\"skinner\">" +
                "<img src=\"http://localhost:8080/java-13/data/skinner.jpg\" width=\"100\" height = \"100\"><br>" +
                "<input type=\"submit\" value=\"Submit\"><br>\n" +
                "</form>\n" +
                "<a href=\"/java-13/phonebook\">Go back to list</a>\n";
        return form;

    }


    private synchronized void add(String name, String[] nums) {
        ArrayList<String> numbers;
        ArrayList<String> tmp = new ArrayList<>(Arrays.asList(nums));

        if (phoneBook.containsKey(name)) {
            numbers = phoneBook.get(name);
        } else {
            numbers = new ArrayList<>();
        }

        numbers.addAll(tmp);

        phoneBook.put(name, numbers);
    }


    private void load() throws IOException {
        File data = new File("../webapps/java-13/data/data.txt");

        if (!data.exists()) {
            logger.log(Level.SEVERE, exceptionLiteral, "Data not found");
        }


        try (BufferedReader reader = new BufferedReader(new FileReader(data))) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(":");
                String name = tokens[0];
                avatars.put(name, tokens[1]);
                String[] nums = tokens[2].split(";");

                add(name, nums);
            }

        }
    }


    private void save() throws IOException {
        File data = new File("../webapps/java-13/data/data.txt");

        if (!data.exists()) {
            boolean ok = data.createNewFile();

            if (!ok) {
                throw new IOException("Can't create file");
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(data))) {

            for (Map.Entry<String, ArrayList<String>> entry : phoneBook.entrySet()) {
                String name = entry.getKey();
                ArrayList<String> numbers = entry.getValue();

                writer.write(name + ":" + avatars.get(name) + ":");

                for (String i : numbers) {
                    writer.write(i + ";");
                }

                writer.write("\n");
            }

        }
    }


    private boolean isNum(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) < '0' || str.charAt(i) > '9') {
                return false;
            }
        }

        return true;
    }
}