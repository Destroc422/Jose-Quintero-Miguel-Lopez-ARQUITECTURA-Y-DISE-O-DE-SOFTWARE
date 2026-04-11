package dao;

import modelo.ConexionBD;
import modelo.Reserva;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    public List<Reserva> listarPorUsuario(int usuarioId){
        List<Reserva> lista = new ArrayList<>();
        try(Connection con = new ConexionBD().getConexion()){
            String sql = "SELECT id, habitacion_id, usuario_id, entrada, salida, estado FROM reservas WHERE usuario_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Reserva r = new Reserva();
                r.setId(rs.getInt("id"));
                r.setHabitacionId(rs.getInt("habitacion_id"));
                r.setUsuarioId(rs.getInt("usuario_id"));
                r.setEntrada(rs.getDate("entrada"));
                r.setSalida(rs.getDate("salida"));
                r.setEstado(rs.getString("estado"));
                lista.add(r);
            }
        } catch(Exception e){ e.printStackTrace(); }
        return lista;
    }
}
