Proyecto Manejo_Hotel - Esqueleto entregable
Contenido:
- Web Pages/: login.html, admin.html, cliente.html, index.html
- Source Packages/: modelo (ConexionBD, Usuario, Habitacion, Reserva),
                     dao (UsuarioDAO, HabitacionDAO, ReservaDAO),
                     controladores (LoginServlet, CheckRoleServlet, LogoutServlet)
- Web Pages/WEB-INF/web.xml

Instrucciones rápidas:
1) Coloca el proyecto en NetBeans como un proyecto Web (o importa los archivos).
2) Configura tu base de datos MySQL y crea la base 'hotel' ejecutando el script 'script_db.sql'.
3) Ajusta las credenciales en modelo/ConexionBD.java
4) Añade conector JDBC (mysql-connector-java) a las librerías del proyecto.
5) Build & Run en Tomcat 11+.

Nota: Este es un esqueleto para arrancar. Implementa más validaciones y seguridad en producción.
