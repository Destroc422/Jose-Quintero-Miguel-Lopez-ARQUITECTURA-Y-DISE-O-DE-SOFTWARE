package controladores;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name="CheckRoleServlet", urlPatterns={"/CheckRoleServlet"})
public class CheckRoleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession ses = req.getSession(false);
        String rol = "";
        if(ses != null && ses.getAttribute("rol") != null){
            rol = ses.getAttribute("rol").toString();
        }
        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().write(rol);
    }
}
