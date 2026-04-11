package controladores;

import dao.UsuarioDAO;
import modelo.Usuario;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp)
            throws ServletException, IOException {

        String user = req.getParameter("usuario");
        String pass = req.getParameter("clave");

        UsuarioDAO dao = new UsuarioDAO();
        Usuario u = dao.validar(user, pass);

        if (u != null) {
            HttpSession ses = req.getSession();
            ses.setAttribute("usuario", u);
            // set role attribute
            ses.setAttribute("rol", u.getRol());
            if("admin".equals(u.getRol())){
                resp.sendRedirect("admin.html");
            } else {
                resp.sendRedirect("cliente.html");
            }
        } else {
            resp.sendRedirect("login.html?error=1");
        }
    }
}
