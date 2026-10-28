ONLINE LIBRARY MANAGEMENT SYSTEM - AIP Mini Project
Servlet + JSP + JDBC + MySQL (Tomcat 10/11, jakarta.servlet)

1. Run sql/library_db.sql in MySQL Workbench / MySQL command line.
2. Open util/DBConnection.java and set your MySQL password (PASS).
3. Put these two JARs in src/main/webapp/WEB-INF/lib:
     - mysql-connector-j-8.x.jar
     - jakarta.servlet.jsp.jstl-api-3.0.x.jar
     - jakarta.servlet.jsp.jstl-3.0.x.jar (implementation)
4. Right-click project > Run As > Run on Server (Tomcat 11).
5. Open http://localhost:8080/LibraryManagement/

Admin login : admin@library.com / admin123
New users   : use the Register page (they get the USER role)
