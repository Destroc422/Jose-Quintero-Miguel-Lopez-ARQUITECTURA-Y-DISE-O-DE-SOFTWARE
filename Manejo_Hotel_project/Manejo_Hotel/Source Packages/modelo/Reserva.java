package modelo;

import java.util.Date;

public class Reserva {
    private int id;
    private int habitacionId;
    private int usuarioId;
    private java.sql.Date entrada;
    private java.sql.Date salida;
    private String estado;

    public Reserva() {}

    // getters & setters
    public int getId(){ return id; }
    public void setId(int id){ this.id = id; }
    public int getHabitacionId(){ return habitacionId; }
    public void setHabitacionId(int habitacionId){ this.habitacionId = habitacionId; }
    public int getUsuarioId(){ return usuarioId; }
    public void setUsuarioId(int usuarioId){ this.usuarioId = usuarioId; }
    public java.sql.Date getEntrada(){ return entrada; }
    public void setEntrada(java.sql.Date entrada){ this.entrada = entrada; }
    public java.sql.Date getSalida(){ return salida; }
    public void setSalida(java.sql.Date salida){ this.salida = salida; }
    public String getEstado(){ return estado; }
    public void setEstado(String estado){ this.estado = estado; }
}
