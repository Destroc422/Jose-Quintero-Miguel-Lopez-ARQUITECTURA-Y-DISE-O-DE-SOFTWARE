package dao;

import modelo.ConexionBD;
import modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsuarioDAO {

    public Usuario validar(String usuario, String clave){
        Usuario u = null;
        try (Connection con = new ConexionBD().getConexion()){
            String sql = "SELECT id, usuario, clave, rol FROM usuarios WHERE usuario = ? AND clave = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, usuario);
            ps.setString(2, clave);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                u = new Usuario(rs.getInt("id"), rs.getString("usuario"), rs.getString("clave"), rs.getString("rol"));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return u;
    }
}
