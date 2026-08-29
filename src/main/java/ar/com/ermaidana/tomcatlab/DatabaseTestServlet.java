package ar.com.ermaidana.tomcatlab;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/db-test")
public class DatabaseTestServlet extends HttpServlet {

	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void doGet(
	            HttpServletRequest request,
	            HttpServletResponse response)
	            throws ServletException, IOException {

	        response.setContentType("text/html;charset=UTF-8");

	        String host = System.getenv("DB_HOST");
	        String port = System.getenv("DB_PORT");
	        String database = System.getenv("MARIADB_DATABASE");
	        String user = System.getenv("MARIADB_USER");
	        String password = System.getenv("MARIADB_PASSWORD");

	        String url = "jdbc:mariadb://"
	                + host + ":"
	                + port + "/"
	                + database;

	        try (PrintWriter out = response.getWriter()) {

	            out.println("<html>");
	            out.println("<head><title>Database Test</title></head>");
	            out.println("<body>");

	            out.println("<h1>Prueba Tomcat → MariaDB</h1>");

	            out.println("<p>Host: " + host + "</p>");
	            out.println("<p>Port: " + port + "</p>");
	            out.println("<p>Database: " + database + "</p>");

	            try (Connection connection =
	                    DriverManager.getConnection(url, user, password)) {

	                out.println("<h2>CONEXIÓN OK</h2>");

	                Statement statement = connection.createStatement();

	                statement.executeUpdate("""
	                    CREATE TABLE IF NOT EXISTS prueba (
	                        id INT AUTO_INCREMENT PRIMARY KEY,
	                        mensaje VARCHAR(200),
	                        fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
	                    )
	                """);

	                statement.executeUpdate("""
	                    INSERT INTO prueba (mensaje)
	                    VALUES ('Hola desde Tomcat en OpenShift')
	                """);

	                ResultSet rs = statement.executeQuery(
	                    "SELECT id, mensaje, fecha FROM prueba ORDER BY id"
	                );

	                out.println("<h2>Datos encontrados</h2>");
	                out.println("<table border='1'>");
	                out.println("<tr>");
	                out.println("<th>ID</th>");
	                out.println("<th>Mensaje</th>");
	                out.println("<th>Fecha</th>");
	                out.println("</tr>");

	                while (rs.next()) {

	                    out.println("<tr>");
	                    out.println("<td>" + rs.getInt("id") + "</td>");
	                    out.println("<td>" + rs.getString("mensaje") + "</td>");
	                    out.println("<td>" + rs.getTimestamp("fecha") + "</td>");
	                    out.println("</tr>");
	                }

	                out.println("</table>");

	            } catch (Exception e) {

	                out.println("<h2>ERROR DE BASE DE DATOS</h2>");
	                out.println("<pre>");
	                e.printStackTrace(out);
	                out.println("</pre>");
	            }

	            out.println("</body>");
	            out.println("</html>");
	        }
	    }
	
}
