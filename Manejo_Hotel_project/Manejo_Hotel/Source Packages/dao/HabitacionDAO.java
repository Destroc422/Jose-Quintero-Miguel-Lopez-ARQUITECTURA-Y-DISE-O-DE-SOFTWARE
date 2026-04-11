package dao;

import modelo.ConexionBD;
import modelo.Habitacion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HabitacionDAO {

    public List<Habitacion> listar(){
        List<Habitacion> lista = new ArrayList<>();
        try(Connection con = new ConexionBD().getConexion()){
            String sql = "SELECT id, numero, tipo, precio, estado FROM habitaciones";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Habitacion h = new Habitacion();
                h.setId(rs.getInt("id"));
                h.setNumero(rs.getString("numero"));
                h.setTipo(rs.getString("tipo"));
                h.setPrecio(rs.getDouble("precio"));
                h.setEstado(rs.getString("estado"));
                lista.add(h);
            }
        } catch(Exception e){ e.printStackTrace(); }
        return lista;
    }
}
