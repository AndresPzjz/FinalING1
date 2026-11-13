import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * ===========================================================
 * Proyecto      : WaterControl
 * Entidad       : Reportes_Fugas
 * Programado por: Estudiante de Ingeniería de Software I
 * Fecha         : 06/11/2026
 * -----------------------------------------------------------
 * Resumen de validaciones y requisitos implementados:
 * HU-01 / RF-01 / CA-01.1, CA-01.2, CA-01.3 : Crear reporte validando ID único y campos válidos.
 * HU-02 / RF-02 / CA-02.1, CA-02.2, CA-02.3 : Consultar por identificador.
 * HU-03 / RF-03 / CA-03.1, CA-03.2, CA-03.3 : Búsqueda por texto parcial en descripciones.
 * HU-04 / RF-04 / CA-04.1, CA-04.2, CA-04.3 : Actualizar datos permitidos del reporte.
 * HU-06 / RF-06 / CA-06.1, CA-06.2, CA-06.3 : Eliminar con desplazamiento estricto y respaldo en pila.
 * HU-12 / RF-11 / CA-12.1, CA-12.2, CA-12.3 : Deshacer la última eliminación usando Pila en arreglo.
 * Estructuras   : Arreglos estáticos con contador y Pila propia basada en arreglos.
 * ===========================================================
 */


public class Main {

    static Scanner entrada = new Scanner(System.in);
    
    static final int MAX = 5;
    static Reporte[] reportes = new Reporte[MAX];
    static int contador = 0;

    static Reporte[] pilaEliminados = new Reporte[MAX];
    static int topePila = -1;

    static String archivoNombre = "reportes.txt";

    public static void main(String[] args) {

        cargar();
        int opcion = 0;

        do {
            System.out.println("");
            System.out.println("===============================");
            System.out.println("===============================");
            System.out.println("WATER CONTROL");
            System.out.println("1- Crear un reporte");
            System.out.println("2- Buscar por ID");
            System.out.println("3- Buscar reporte por descripcion parcial");
            System.out.println("4- Acutualizar reporte");
            System.out.println("5- Eliminar reporte");
            System.out.println("6- Listar reporte");
            System.out.println("7- Deshacer ultima eliminacion");
            System.out.println("8- Salir");
            System.out.println("===============================");
            System.out.println("===============================");
            
            System.out.print("Ingresa opcion: ");
            while (!entrada.hasNextInt()) {
                System.out.println("Error: Debe ingresar un numero entero.");
                entrada.nextLine();
                System.out.print("Ingresa opcion: ");
            }

            opcion = entrada.nextInt();
            entrada.nextLine();

            switch (opcion) {
                case 1 -> crearReporte();
                case 2 -> consultarID();
                case 3 -> buscarDescripcion();
                case 4 -> actualizarReporte();
                case 5 -> eliminarReporte();
                case 6 -> mostrarReporte();
                case 7 -> deshacerEliminacion();
                case 8 -> guardar();
                
                default -> System.out.println("Opcion no valida");
            }
            if (opcion!=8) {
                opcion=0;
            }
        } while (opcion<7);
    }

    // 1. Está vacía
    public static boolean estaVacia() {
        return topePila == -1;
    }

    // 2. Tamaño
    public static int tamañoPila() {
        return topePila + 1;
    }

    // 3. Cima (Devuelve el elemento del tope sin desapilarlo)
    public static Reporte cimaPila() {
        if (estaVacia()) {
            return null;
        }
        return pilaEliminados[topePila];
    }

    // 4. Apilar (Inserta un elemento en el tope de la pila)
    public static void apilar(Reporte r) {
        if (topePila >= MAX - 1) {
            System.out.println("Error: La pila de eliminados está llena.");
            return;
        }
        topePila++;
        pilaEliminados[topePila] = r;
    }

    // 5. Desapilar (Saca el elemento del tope y lo devuelve)
    public static Reporte desapilar() {
        if (estaVacia()) {
            return null;
        }
        Reporte r = pilaEliminados[topePila];
        pilaEliminados[topePila] = null; // Limpiar referencia
        topePila--;
        return r;
    }


    // RF-01 · CA-01.1, CA-01.2, CA-01.3: Valida que el identificador no esté repetido y almacena el reporte en el arreglo.
    public static void crearReporte(){

        if(contador >= MAX){
            System.out.println("Error Arreglo completo");
            return;
        }
        
        int id;
        while (true) {
            System.out.print("Ingrese el ID del reporte: ");

            if (entrada.hasNextInt()) {
                id = entrada.nextInt();
                entrada.nextLine();
                break;
            } else {
                System.out.println("Error: Debe ingresar un número entero.");
                entrada.nextLine();
            }
        }

        // Validar que el identificador no esté repetido en pila y en reporte
        for (int i = 0; i < contador; i++) {
            if (reportes[i].getId_reporte() == id) {
                System.out.println("Error: Ya existe un reporte registrado con ese ID.");
                return;
            }
        }

        for (int x = 0; x<=topePila; x++){
            if(pilaEliminados[x].getId_reporte() == id){
                System.out.println("Error: Ya hay un reporte registrado con ese ID en la pila.");
                return;
            }
        }

        int idUsuario;
        while (true) {

            System.out.print("Ingrese ID del usuario: ");

            if (entrada.hasNextInt()) {
                idUsuario = entrada.nextInt();
                entrada.nextLine();
                break;
            } else {
                System.out.println("Error: Debe ingresar un número entero.");
                entrada.nextLine();
            }
        }

        int idUbicacion;
        while (true) {

            System.out.print("Ingrese ID de la Ubicacion: ");

            if (entrada.hasNextInt()) {
                idUbicacion = entrada.nextInt();
                entrada.nextLine();
                break;
            } else {
                System.out.println("Error: Debe ingresar un número entero.");
                entrada.nextLine();
            }
        }

        if(idUbicacion != 1){
            System.out.println("");
            System.out.println("=================================");
            System.out.println("Error la ubicacion no existe");
            System.out.println("Utilizar la ubicacion de prueba << id=1 >>");
            System.out.println("=================================");
            System.out.println("");
            return;
        }

        System.out.print("Ingrese descripción de la fuga: ");
        String descripcion = entrada.nextLine();
        System.out.print("Ingrese fecha (DD/MM/AAAA): ");
        String fecha = entrada.nextLine();
        System.out.print("Ingrese hora (HH:MM): ");
        String hora = entrada.nextLine();
        System.out.print("Ingrese intensidad (Baja/Media/Alta): ");
        String intensidad = entrada.nextLine();

        reportes[contador] = new Reporte(id, idUsuario, idUbicacion, descripcion, fecha, hora, intensidad);
        contador++;
        System.out.println("¡Reporte creado exitosamente!");
    }

    // Listar todos los reportes recorriendo estrictamente hasta el contador
    public static void  mostrarReporte(){
        if (contador == 0) {
            System.out.println("Aviso: No hay reportes registrados en el sistema.");
            return;
        }

        System.out.println("\n--- LISTADO DE REPORTES REGISTRADOS (" + contador + ") ---");
        for (int i = 0; i < contador; i++) {
            reportes[i].mostrar();
        }
    }

    // RF-02 · CA-02.1, CA-02.2, CA-02.3: Recorre el arreglo buscando por ID y muestra el detalle o avisa si no existe.
    public static void  consultarID(){
        if (contador == 0) {
            System.out.println("No hay reportes registrados");
            return;
        }

        int idBuscado;
        while (true) {
            System.out.print("Ingrese el ID del reporte a consultar: ");

            if (entrada.hasNextInt()) {
                idBuscado = entrada.nextInt();
                entrada.nextLine();
                break;
            } else {
                System.out.println("Error: Debe ingresar un número entero.");
                entrada.nextLine();
            }
        }

        for (int i = 0; i < contador; i++) {
            if (reportes[i].getId_reporte() == idBuscado) {
                reportes[i].mostrar();
                return;
            }
        }
        System.out.println("Aviso: No se encontró ningún reporte con el ID " + idBuscado);
    }

    // RF-03 · CA-03.1, CA-03.2, CA-03.3: Filtra y muestra los reportes que contienen el texto parcial indicado.
    public static void buscarDescripcion(){
        if (contador == 0) {
            System.out.println("No hay reportes registrados.");
            return;
        }

        System.out.print("Ingrese texto o palabra clave a buscar en la descripción o intensidad: ");
        String texto = entrada.nextLine().toLowerCase();
       
        for (int i = 0; i < contador; i++) {
            if (reportes[i].getDescripcion().toLowerCase().contains(texto) || reportes[i].getIntensidad().toLowerCase().contains(texto)) {
                reportes[i].mostrar();
                return;
            }
        }

        System.out.println("Aviso: No se encontraron reportes que coincidan con la búsqueda.");
    }

    // RF-04 · CA-04.1, CA-04.2, CA-04.3: Permite actualizar los campos permitidos de un reporte existente.
    public static void actualizarReporte(){
        if (contador == 0) {
            System.out.println("Aviso: No hay reportes registrados.");
            return;
        }

        int idBuscado;
        while (true) {
            System.out.print("Ingrese el ID del reporte que desea actualizar: ");

            if (entrada.hasNextInt()) {
                idBuscado = entrada.nextInt();
                entrada.nextLine();
                break;
            } else {
                System.out.println("Error: Debe ingresar un número entero.");
                entrada.nextLine();
            }
        }

        int index = -1;
        for (int i = 0; i < contador; i++) {
            if (reportes[i].getId_reporte() == idBuscado) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            System.out.println("Aviso: El reporte con ID " + idBuscado + " no existe.");
            return;
        }

        System.out.println("Reporte encontrado. Ingrese los nuevos datos permitidos:");
        System.out.print("Nueva descripción: ");
        String nuevaDesc = entrada.nextLine();
        System.out.print("Nueva intensidad (Baja/Media/Alta): ");
        String nuevaInt = entrada.nextLine();

        reportes[index].setDescripcion(nuevaDesc);
        reportes[index].setIntensidad(nuevaInt);
        System.out.println("¡Reporte actualizado correctamente!");
    }

    // RF-06 · CA-06.1, CA-06.2, CA-06.3: Elimina desplazando elementos a la izquierda y apila el registro eliminado.
    public static void eliminarReporte(){
        if (contador == 0) {
            System.out.println("No hay reportes registrados para eliminar.");
            return;
        }

        int idBuscado;
        while (true) {
            System.out.print("Ingrese el ID del reporte a eliminar: ");

            if (entrada.hasNextInt()) {
                idBuscado = entrada.nextInt();
                entrada.nextLine();
                break;
            } else {
                System.out.println("Error: Debe ingresar un número entero.");
                entrada.nextLine();
            }
        }

        int index = -1;
        for (int i = 0; i < contador; i++) {
            if (reportes[i].getId_reporte() == idBuscado) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            System.out.println("El reporte no existe.");
            return;
        }

        // Verificar que la pila tenga espacio
        if (topePila >= MAX - 1) {
            System.out.println("Error: La pila de eliminados está llena.");
            return;
        }

        // 1. Guardar el reporte antes de eliminarlo
        Reporte eliminado = reportes[index];

        // 2. Apilar el reporte eliminado
        apilar(eliminado);

        // 3. Desplazar los elementos a la izquierda
        for (int i = index; i < contador - 1; i++) {
            reportes[i] = reportes[i + 1];
        }

        // 4. Limpiar última posición
        reportes[contador - 1] = null;

        // 5. Disminuir contador
        contador--;

        System.out.println("Reporte eliminado y guardado en la pila correctamente.");
    }

    // RF-11 · CA-12.1, CA-12.2, CA-12.3: Restaura el último reporte eliminado utilizando la pila propia basada en arreglos.
    public static void deshacerEliminacion(){
        if (topePila == -1) {
            System.out.println("Aviso: No hay ninguna eliminación previa para deshacer (la pila está vacía).");
            return;
        }

        if (contador >= MAX) {
            System.out.println("Error: No se puede restaurar, el arreglo de reportes está lleno.");
            return;
        }

        Reporte reportRestaurado = desapilar();

        // Colocarlo al final del arreglo activo y aumentar el contador
        reportes[contador] = reportRestaurado;
        contador++;
        System.out.println("¡Último reporte eliminado restaurado con éxito! (CA-12.1)");
    }

    // Guardar registros en archivo de texto plano al salir
    public static void guardar(){
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivoNombre))) {
            for (int i = 0; i < contador; i++) {
                Reporte r = reportes[i];
                pw.println(r.getId_reporte() + ";" + 
                           r.getId_usuario() + ";" + 
                           r.getId_ubicacion() + ";" + 
                           r.getDescripcion() + ";" + 
                           r.getFecha() + ";" + 
                           r.getHora() + ";" + 
                           r.getIntensidad());
            }
        } catch (IOException e) {
            System.out.println("Error al guardar los datos en el archivo: " + e.getMessage());
        }
    }

    // Cargar registros desde el archivo plano al iniciar la aplicación
    public static void cargar() {
        File archivo = new File(archivoNombre);
        if (!archivo.exists()) {
            return; 
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null && contador < MAX) {
                String[] partes = linea.split(";");
                if (partes.length == 7) {
                    int id = Integer.parseInt(partes[0]);
                    int idUsu = Integer.parseInt(partes[1]);
                    int idUbi = Integer.parseInt(partes[2]);
                    String desc = partes[3];
                    String fecha = partes[4];
                    String hora = partes[5];
                    String intensidad = partes[6];

                    reportes[contador] = new Reporte(id, idUsu, idUbi, desc, fecha, hora, intensidad);
                    contador++;
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error al cargar los datos del archivo: " + e.getMessage());
        }
    }

}