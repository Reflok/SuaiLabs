import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;


public class Java16 extends HttpServlet {
	private static String dataFileName = "../webapps/java16/list1.txt";
	private ArrayList<ArrayList<String>> list = new ArrayList<>();

	public void init() {
		File file = new File(dataFileName);

		if (!file.exists()) {
			System.out.println("Fail");
			return;
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {
                /*String[] tokens = line.split(":");
                ArrayList<String> entry = new ArrayList<>();
                entry.add(tokens[0]);
                String[] subentries = tokens[1].split(";");

                for (int i = 0; i < subentries.length; i++) {
                	entry.add(subentries[i]);
                 
                }
                list.add(entry);*/
                if (line.startsWith("*")) {
                	list.add(new ArrayList<String>());
                	list.get(list.size() - 1).add(line.substring(line.lastIndexOf("*") + 1, line.length()));
                } else if (line.startsWith("    *")) {
                	list.get(list.size() - 1).add(line.substring(line.lastIndexOf("*") + 1, line.length()));
                } else {
                	System.out.println("Fail!");
                }
            }

        } catch (Exception e) {
        	e.printStackTrace();
        }
	}

	public void destroy() {
		File file = new File(dataFileName);

		if (!file.exists()) {
			System.out.println("Fail");
			return;
		}

		String fourSpaces = "    *";

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

            for (ArrayList<String> entry : list) {
            	writer.write("*" + entry.get(0) + "\n");

            	for (int i = 1; i < entry.size(); i++) {
            		writer.write(fourSpaces + entry.get(i) + "\n");
            	}
            }

            writer.flush();
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();

		out.println("<html>");

		out.println("<head><title>List</title><script src=\"script.js\"></script></head>");

		out.println(generateList());

		out.println("</html>");
	}

	public String generateList() {
		StringBuilder str = new StringBuilder();

		str.append("<div id=\"main\">");
		str.append("<button onclick=\"addView(this)\">Add view</button>");
		str.append("<ol type=\"a\">");

		for (int i = 0; i < list.size(); i++) {
			ArrayList<String> entry = list.get(i);

			str.append("<li>");
			str.append(entry.get(0));
			str.append("<a href=\"#\"  onclick = \"onClick(this)\">[-]</a>");
			str.append("<ol display=\"block\">");

			for (int j = 1; j < entry.size(); j++) {
					str.append("<li>");
					str.append(entry.get(j));
					str.append("</li>");
			}

			str.append("</ol>");
			str.append("</li>");
		}

		str.append("</ol>");
		str.append("</div>");
		return str.toString();
	}

}