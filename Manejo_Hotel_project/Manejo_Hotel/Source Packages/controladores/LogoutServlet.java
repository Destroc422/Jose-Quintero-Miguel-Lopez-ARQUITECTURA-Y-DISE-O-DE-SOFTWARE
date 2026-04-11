package controladores;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name="LogoutServlet", urlPatterns={"/LogoutServlet"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession ses = req.getSession(false);
        if(ses != null) ses.invalidate();
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
