package modelo;

public class Habitacion {
    private int id;
    private String numero;
    private String tipo;
    private double precio;
    private String estado;

    public Habitacion() {}

    // getters & setters
    public int getId(){ return id; }
    public void setId(int id){ this.id = id; }
    public String getNumero(){ return numero; }
    public void setNumero(String numero){ this.numero = numero; }
    public String getTipo(){ return tipo; }
    public void setTipo(String tipo){ this.tipo = tipo; }
    public double getPrecio(){ return precio; }
    public void setPrecio(double precio){ this.precio = precio; }
    public String getEstado(){ return estado; }
    public void setEstado(String estado){ this.estado = estado; }
}
