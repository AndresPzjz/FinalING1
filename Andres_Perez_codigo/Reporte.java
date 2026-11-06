/**
 * ===========================================================
 * Proyecto      : WaterControl
 * Entidad       : Reportes_Fugas
 * Programado por: Andres Perez Jimenez
 * Fecha         : 06/11/2026
 * -----------------------------------------------------------
 * HU-01         : Registrar un reporte de fuga con ubicación, descripción e intensidad.
 * HU-02         : Consultar el estado de un reporte realizado.
 * RF-01         : Almacenamiento de reportes asociados a ubicaciones activas.
 * RF-02         : Consulta de información, ubicación y estado actual del reporte.
 * Criterios     : CA-01.1, CA-01.2, CA-01.3, CA-02.1, CA-02.2, CA-02.3
 * Estructuras   : Arreglo de objetos y Pila propia con arreglos nativos.
 * ===========================================================
 */

public class Reporte {
    
    private int id_reporte;
    private int id_usuario;
    private int id_ubicacion;
    private String descripcion;
    private String fecha;
    private String hora;
    private String intensidad;


    public Reporte(int id_reporte, int id_usuario, int id_ubicacion, String descripcion, String fecha, String hora, String intensidad) {
        this.id_reporte = id_reporte;
        this.id_usuario = id_usuario;
        this.id_ubicacion = id_ubicacion;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.hora = hora;
        this.intensidad = intensidad;
    }

    

    public int getId_reporte() {
        return id_reporte;
    }



    public void setId_reporte(int id_reporte) {
        this.id_reporte = id_reporte;
    }



    public int getId_usuario() {
        return id_usuario;
    }



    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }



    public int getId_ubicacion() {
        return id_ubicacion;
    }



    public void setId_ubicacion(int id_ubicacion) {
        this.id_ubicacion = id_ubicacion;
    }



    public String getDescripcion() {
        return descripcion;
    }



    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }



    public String getFecha() {
        return fecha;
    }



    public void setFecha(String fecha) {
        this.fecha = fecha;
    }



    public String getHora() {
        return hora;
    }



    public void setHora(String hora) {
        this.hora = hora;
    }



    public String getIntensidad() {
        return intensidad;
    }



    public void setIntensidad(String intensidad) {
        this.intensidad = intensidad;
    }


    // RF-02 · CA-02.1: Muestra la información completa y estado actual del reporte
    public void mostrar(){
        System.out.println("--------------------------------");
        System.out.println("ID reporte: " + id_reporte);
        System.out.println("ID usuario: " + id_usuario);
        System.out.println("ID ubicación: " + id_ubicacion);
        System.out.println("Descripción: " + descripcion);
        System.out.println("Fecha: " + fecha);
        System.out.println("Hora: " + hora);
        System.out.println("Intensidad: " + intensidad);
        System.out.println("--------------------------------");
    }

    
}