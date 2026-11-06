/* ===========================================================
 * Proyecto       : WaterControl
 * Entidad        : Ubicacion
 * Programado por : Andres Felipe Valencia Palomino
 * Fecha          : 05/11/2026
 * -----------------------------------------------------------
 * HU-08 Como administrador quiero gestionar ubicaciones
 *       para asociarlas correctamente a los reportes.
 * RF-08 Cuando un administrador administra ubicaciones
 *       institucionales, el sistema registra y actualiza la
 *       información de las ubicaciones asociadas a los reportes.
 * RN-09 No se permite registrar ubicaciones duplicadas
 *       dentro de la misma sede.
 * -----------------------------------------------------------
 * Casos de uso: CU-11 . CU-12 . CU-13 . CU-14 . CU-15 . CU-16
 * Criterios: CA-08.1 (bueno) CA-08.2 (falla) CA-08.3 (falla)
 * Estructuras: Arreglo con contador . Pila propia
 * =========================================================== */

import java.io.*;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);

    static Ubicacion[] ubicaciones = new Ubicacion[100];
    static int contador = 0;

    static PilaUbicacion pila = new PilaUbicacion(100);

    static final String ARCHIVO = "ubicaciones.txt";

    // Valor que indica que un dato numerico no se pudo leer
    static final int SIN_DATO = Integer.MIN_VALUE;

    public static void main(String[] args) {

        cargarArchivo();

        int opcion;

        do {

            System.out.println("\n==================================");
            System.out.println("          WATERCONTROL");
            System.out.println("      GESTION DE UBICACIONES");
            System.out.println("==================================");
            System.out.println("1. Crear ubicacion");
            System.out.println("2. Consultar ubicacion");
            System.out.println("3. Buscar por texto");
            System.out.println("4. Actualizar ubicacion");
            System.out.println("5. Eliminar ubicacion");
            System.out.println("6. Listar ubicaciones");
            System.out.println("7. Deshacer eliminacion");
            System.out.println("8. Salir");

            opcion = leerEntero("Seleccione una opcion: ");

            switch (opcion) {

                case 1:
                    crearUbicacion();
                    break;

                case 2:
                    consultarUbicacion();
                    break;

                case 3:
                    buscarPorTexto();
                    break;

                case 4:
                    actualizarUbicacion();
                    break;

                case 5:
                    eliminarUbicacion();
                    break;

                case 6:
                    listarUbicaciones();
                    break;

                case 7:
                    deshacerEliminacion();
                    break;

                case 8:
                    guardarArchivo();
                    System.out.println("Datos guardados correctamente.");
                    break;

                default:
                    System.out.println("Opcion invalida.");
            }

        } while (opcion != 8);
    }

    // ---------- Utilidades de lectura y validacion ----------

    // CU-13 · 2a — lee un entero; si esta vacio vuelve a solicitar el dato
    static int leerEntero(String mensaje) {

        while (true) {

            System.out.print(mensaje);

            String texto = sc.nextLine().trim();

            if (texto.isEmpty()) {

                System.out.println("Este dato es obligatorio. Ingreselo nuevamente.");
                continue;
            }

            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un numero entero.");
            }
        }
    }

    // RF-08 · CA-08.3 — rechaza el registro y avisa que dato obligatorio falta
    static void faltaDato(String campo) {

        System.out.println("\nERROR RF-08 CA-08.3");
        System.out.println("Falta el dato obligatorio: " + campo + ".");
    }

    // RF-08 · CA-08.3 — lee el piso; avisa si esta vacio o no es numero (devuelve SIN_DATO)
    static int leerPiso(String mensaje) {

        System.out.print(mensaje);

        String texto = sc.nextLine().trim();

        if (texto.isEmpty()) {

            faltaDato("piso");
            return SIN_DATO;
        }

        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {

            System.out.println("El piso debe ser un numero entero.");
            return SIN_DATO;
        }
    }

    // RF-08 — acepta solo Activa/Inactiva, si no devuelve null
    static String normalizarEstado(String estado) {

        if (estado.equalsIgnoreCase("Activa")) return "Activa";
        if (estado.equalsIgnoreCase("Inactiva")) return "Inactiva";

        return null;
    }

    // RF-08 — acepta solo Torre 1/Torre 2, si no devuelve null
    static String normalizarTorre(String torre) {

        if (torre.equalsIgnoreCase("Torre 1")) return "Torre 1";
        if (torre.equalsIgnoreCase("Torre 2")) return "Torre 2";

        return null;
    }

    // RF-08 — baño max. 30: Masculino/Femenino/Mixto u otra identificacion
    static String normalizarBano(String bano) {

        if (bano.length() > 30 || bano.contains(";")) return null;

        if (bano.equalsIgnoreCase("Masculino")) return "Masculino";
        if (bano.equalsIgnoreCase("Femenino")) return "Femenino";
        if (bano.equalsIgnoreCase("Mixto")) return "Mixto";

        return bano;
    }

    // RF-08 — rechaza pisos fuera del rango de la torre
    static boolean pisoValido(String torre, int piso) {

        if (torre.equals("Torre 1")) {

            if (piso < 1 || piso > 20) {
                System.out.println("La Torre 1 solo tiene pisos entre 1 y 20.");
                return false;
            }
        }

        if (torre.equals("Torre 2")) {

            if (piso < 1 || piso > 11) {
                System.out.println("La Torre 2 solo tiene pisos entre 1 y 11.");
                return false;
            }
        }

        return true;
    }

    // Busca la posicion de una ubicacion por id; devuelve -1 si no existe
    static int buscarIndice(int id) {

        for (int i = 0; i < contador; i++) {

            if (ubicaciones[i].id_ubicacion == id) {

                return i;
            }
        }

        return -1;
    }

    // ---------- Captura de datos (usada por crear y actualizar) ----------

    // RF-08 · CA-08.2 · CA-08.3 · RN-09 — pide y valida los datos; devuelve null si algo falla
    static Ubicacion pedirDatos(int id, boolean nuevo) {

        System.out.print(nuevo ? "Nueva zona: " : "Zona: ");
        String zona = sc.nextLine().trim();

        if (zona.isEmpty()) {

            faltaDato("zona");
            return null;
        }

        if (zona.contains(";") || zona.length() > 50) {

            System.out.println("La zona no puede superar 50 caracteres ni contener ';'.");
            return null;
        }

        System.out.print(nuevo ? "Nuevo estado (Activa/Inactiva): "
                               : "Estado (Activa/Inactiva): ");
        String texto = sc.nextLine().trim();

        if (texto.isEmpty()) {

            faltaDato("estado");
            return null;
        }

        String estado = normalizarEstado(texto);

        if (estado == null) {

            System.out.println("Estado invalido.");
            return null;
        }

        System.out.print(nuevo ? "Nueva torre (Torre 1/Torre 2): "
                               : "Torre (Torre 1/Torre 2): ");
        texto = sc.nextLine().trim();

        if (texto.isEmpty()) {

            faltaDato("torre");
            return null;
        }

        String torre = normalizarTorre(texto);

        if (torre == null) {

            System.out.println("Torre invalida.");
            return null;
        }

        int piso = leerPiso(nuevo ? "Nuevo piso: " : "Piso: ");

        if (piso == SIN_DATO || !pisoValido(torre, piso)) {
            return null;
        }

        // CA-08.2 · RN-09 — misma zona, torre y piso ya registrados
        if (existeDuplicado(zona, torre, piso, id)) {

            System.out.println("\nERROR RF-08 CA-08.2");
            System.out.println("La ubicacion ya existe.");

            return null;
        }

        System.out.print(nuevo ? "Nuevo baño (Masculino/Femenino/Mixto u otra identificacion): "
                               : "Baño (Masculino/Femenino/Mixto u otra identificacion): ");
        texto = sc.nextLine().trim();

        if (texto.isEmpty()) {

            faltaDato("baño");
            return null;
        }

        String bano = normalizarBano(texto);

        if (bano == null) {

            System.out.println("Baño invalido: maximo 30 caracteres y sin ';'.");
            return null;
        }

        return new Ubicacion(id, zona, estado, torre, piso, bano);
    }

    // ---------- Operaciones CRUDL ----------

    // CU-12 · RF-08 · CA-08.1 — registra la ubicacion; CA-08.2 y CA-08.3 la rechazan
    static void crearUbicacion() {

        if (contador >= ubicaciones.length) {

            System.out.println("No hay espacio para registrar mas ubicaciones.");
            return;
        }

        int id_ubicacion = leerEntero("Id ubicacion: ");

        if (id_ubicacion <= 0) {

            System.out.println("El id debe ser un numero positivo.");
            return;
        }

        // CA-08.2 — rechaza si el identificador ya existe
        if (existeId(id_ubicacion)) {

            System.out.println("\nERROR RF-08 CA-08.2");
            System.out.println("La ubicacion ya existe.");

            return;
        }

        Ubicacion u = pedirDatos(id_ubicacion, false);

        if (u == null) {
            return;
        }

        ubicaciones[contador] = u;
        contador++;

        System.out.println("Ubicacion creada correctamente.");
    }

    // CU-13 · RF-08 — consulta por identificador; avisa si no existe
    static void consultarUbicacion() {

        int id = leerEntero("Ingrese id: ");

        int pos = buscarIndice(id);

        if (pos == -1) {

            System.out.println("La ubicacion no existe.");
            return;
        }

        System.out.println("\n" + ubicaciones[pos]);
    }

    // RF-08 — busqueda parcial por texto en la zona
    static void buscarPorTexto() {

        System.out.print("Texto a buscar: ");

        String texto = sc.nextLine().trim().toLowerCase();

        boolean encontrado = false;

        for (int i = 0; i < contador; i++) {

            if (ubicaciones[i].zona.toLowerCase().contains(texto)) {

                System.out.println(ubicaciones[i]);

                encontrado = true;
            }
        }

        if (!encontrado) {

            System.out.println("No se encontraron coincidencias.");
        }
    }

    // CU-14 · RF-08 — muestra los datos actuales y actualiza; CA-08.2 y CA-08.3 rechazan
    static void actualizarUbicacion() {

        int id = leerEntero("Id ubicacion: ");

        int pos = buscarIndice(id);

        if (pos == -1) {

            System.out.println("La ubicacion no existe.");
            return;
        }

        System.out.println("Datos actuales:");
        System.out.println(ubicaciones[pos]);

        Ubicacion u = pedirDatos(id, true);

        if (u == null) {
            return;
        }

        ubicaciones[pos] = u;

        System.out.println("Ubicacion actualizada.");
    }

    // CU-16 · RF-08 — confirma, guarda en la pila y elimina desplazando el arreglo
    static void eliminarUbicacion() {

        int id = leerEntero("Id ubicacion: ");

        int pos = buscarIndice(id);

        if (pos == -1) {

            System.out.println("La ubicacion no existe.");
            return;
        }

        System.out.println(ubicaciones[pos]);

        System.out.print("Confirma la eliminacion (S/N): ");
        String respuesta = sc.nextLine().trim();

        if (!respuesta.equalsIgnoreCase("S")) {

            System.out.println("Eliminacion cancelada.");
            return;
        }

        if (!pila.apilar(ubicaciones[pos])) {

            System.out.println("La pila de deshacer esta llena. No se puede eliminar.");
            return;
        }

        for (int j = pos; j < contador - 1; j++) {

            ubicaciones[j] = ubicaciones[j + 1];
        }

        contador--;
        ubicaciones[contador] = null;

        System.out.println("Ubicacion eliminada.");
    }

    // CU-11 · RF-08 — restaura la ultima eliminacion; si hay conflicto no la saca de la pila
    static void deshacerEliminacion() {

        Ubicacion recuperada = pila.cima();

        if (recuperada == null) {

            System.out.println("No existe eliminacion para deshacer.");
            return;
        }

        if (contador >= ubicaciones.length) {

            System.out.println("No hay espacio para restaurar la ubicacion.");
            return;
        }

        // CU-11 · 4a — conflicto de identificador
        if (existeId(recuperada.id_ubicacion)) {

            System.out.println("No se puede restaurar: ya existe una");
            System.out.println("ubicacion con el id " + recuperada.id_ubicacion + ".");
            return;
        }

        // CU-11 · 4a — RN-09: ya existe la misma zona, torre y piso
        if (existeDuplicado(recuperada.zona, recuperada.torre, recuperada.piso, -1)) {

            System.out.println("No se puede restaurar: ya existe una");
            System.out.println("ubicacion en la misma zona, torre y piso.");
            return;
        }

        pila.desapilar();

        ubicaciones[contador] = recuperada;
        contador++;

        System.out.println("Ubicacion restaurada correctamente.");
        System.out.println("Elementos restantes en pila: " + pila.tamano());
    }

    // CU-15 · RF-08 · CA-08.6 — lista por torre y piso (con filtros) y calcula el resumen
    static void listarUbicaciones() {

        if (contador == 0) {

            System.out.println("No existen registros.");
            return;
        }

        System.out.print("Filtrar por torre (Enter = todas): ");
        String texto = sc.nextLine().trim();

        String filtroTorre = null;

        if (!texto.isEmpty()) {

            filtroTorre = normalizarTorre(texto);

            if (filtroTorre == null) {

                System.out.println("Torre invalida.");
                return;
            }
        }

        int filtroPiso = leerEntero("Filtrar por piso (0 = todos): ");

        // Posiciones de los registros que cumplen el filtro
        int[] pos = new int[contador];
        int n = 0;

        for (int i = 0; i < contador; i++) {

            boolean cumpleTorre = filtroTorre == null
                    || ubicaciones[i].torre.equals(filtroTorre);

            boolean cumplePiso = filtroPiso == 0
                    || ubicaciones[i].piso == filtroPiso;

            if (cumpleTorre && cumplePiso) {

                pos[n] = i;
                n++;
            }
        }

        // CU-15 · 3a — el filtro no da resultados
        if (n == 0) {

            System.out.println("No se encontraron ubicaciones.");
            return;
        }

        // Ordena las posiciones por torre y luego por piso (insercion)
        for (int i = 1; i < n; i++) {

            int actual = pos[i];
            int j = i - 1;

            while (j >= 0 && vaAntes(ubicaciones[actual], ubicaciones[pos[j]])) {

                pos[j + 1] = pos[j];
                j--;
            }

            pos[j + 1] = actual;
        }

        int sumaPisos = 0;
        int pisoMayor = 0;

        System.out.println("\n===== LISTADO (por torre y piso) =====");

        for (int i = 0; i < n; i++) {

            Ubicacion u = ubicaciones[pos[i]];

            System.out.println(u);

            sumaPisos += u.piso;

            if (u.piso > pisoMayor) {

                pisoMayor = u.piso;
            }
        }

        double promedio = (double) sumaPisos / n;

        System.out.println("\n===== RESUMEN =====");
        System.out.println("Total ubicaciones: " + n);
        System.out.println("Promedio pisos: " + String.format("%.2f", promedio));
        System.out.println("Piso mas alto: " + pisoMayor);
    }

    // CU-15 — indica si a debe mostrarse antes que b (torre y luego piso)
    static boolean vaAntes(Ubicacion a, Ubicacion b) {

        int comparacion = a.torre.compareTo(b.torre);

        if (comparacion != 0) {
            return comparacion < 0;
        }

        return a.piso < b.piso;
    }

    // RF-08 · CA-08.2 — indica si el identificador ya esta registrado
    static boolean existeId(int id) {

        return buscarIndice(id) != -1;
    }

    // RF-08 · CA-08.2 · RN-09 — indica si ya existe la misma zona, torre y piso (idExcluir evita compararse consigo misma)
    static boolean existeDuplicado(String zona, String torre, int piso, int idExcluir) {

        for (int i = 0; i < contador; i++) {

            Ubicacion u = ubicaciones[i];

            if (u.id_ubicacion != idExcluir
                    && u.zona.equalsIgnoreCase(zona)
                    && u.torre.equalsIgnoreCase(torre)
                    && u.piso == piso) {

                return true;
            }
        }

        return false;
    }

    // ---------- Archivo TXT ----------

    // Guarda los datos en el archivo TXT al salir
    static void guardarArchivo() {

        try (PrintWriter pw = new PrintWriter(ARCHIVO, "UTF-8")) {

            for (int i = 0; i < contador; i++) {

                Ubicacion u = ubicaciones[i];

                pw.println(
                        u.id_ubicacion + ";" +
                        u.zona + ";" +
                        u.estado + ";" +
                        u.torre + ";" +
                        u.piso + ";" +
                        u.bano);
            }

        } catch (Exception e) {

            System.out.println("Error guardando archivo.");
        }
    }

    // Carga los datos desde el archivo TXT o crea los datos quemados si no existe
    static void cargarArchivo() {

        File archivo = new File(ARCHIVO);

        if (!archivo.exists()) {

            ubicaciones[contador++] =
                    new Ubicacion(1, "Cafeteria", "Activa", "Torre 1", 2, "Masculino");

            ubicaciones[contador++] =
                    new Ubicacion(2, "Biblioteca", "Activa", "Torre 1", 7, "Femenino");

            ubicaciones[contador++] =
                    new Ubicacion(3, "Facultad Ingenierias", "Activa", "Torre 1", 9, "Masculino");

            ubicaciones[contador++] =
                    new Ubicacion(4, "Sala Descanso", "Activa", "Torre 1", 12, "Femenino");

            return;
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(archivo), "UTF-8"))) {

            String linea;

            while ((linea = br.readLine()) != null
                    && contador < ubicaciones.length) {

                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";");

                if (datos.length < 6) {
                    continue; // linea danada, se ignora
                }

                ubicaciones[contador++] =
                        new Ubicacion(
                                Integer.parseInt(datos[0]),
                                datos[1],
                                datos[2],
                                datos[3],
                                Integer.parseInt(datos[4]),
                                datos[5]);
            }

        } catch (Exception e) {

            System.out.println("Error cargando archivo.");
        }
    }
}

/* ===========================================================
 * Proyecto       : WaterControl
 * Entidad        : Ubicacion
 * Programado por : Andres Felipe Valencia Palomino
 * Fecha          : 05/11/2026
 * -----------------------------------------------------------
 * HU-08 Como administrador quiero gestionar ubicaciones
 *       para asociarlas correctamente a los reportes.
 * RF-08 Cuando un administrador administra ubicaciones
 *       institucionales, el sistema registra y actualiza la
 *       información de las ubicaciones asociadas a los reportes.
 * RN-09 No se permite registrar ubicaciones duplicadas
 *       dentro de la misma sede.
 * -----------------------------------------------------------
 * Casos de uso: CU-11 . CU-12 . CU-13 . CU-14 . CU-15 . CU-16
 * Criterios: CA-08.1 (bueno) CA-08.2 (falla) CA-08.3 (falla)
 * Estructuras: Arreglo con contador . Pila propia
 * =========================================================== */
class Ubicacion {

    int id_ubicacion;
    String zona;
    String estado;
    String torre;
    int piso;
    String bano;

    // RF-08 — construye una ubicacion con los campos del diccionario
    public Ubicacion(
            int id_ubicacion,
            String zona,
            String estado,
            String torre,
            int piso,
            String bano) {

        this.id_ubicacion = id_ubicacion;
        this.zona = zona;
        this.estado = estado;
        this.torre = torre;
        this.piso = piso;
        this.bano = bano;
    }

    // RF-08 — texto que se muestra en consultas y listados
    @Override
    public String toString() {

        return id_ubicacion +
                " | " +
                zona +
                " | " +
                estado +
                " | " +
                torre +
                " | Piso " +
                piso +
                " | " +
                bano;
    }
}

/* ===========================================================
 * Proyecto       : WaterControl
 * Entidad        : Ubicacion
 * Programado por : Andres Felipe Valencia Palomino
 * Fecha          : 05/11/2026
 * -----------------------------------------------------------
 * HU-08 Como administrador quiero gestionar ubicaciones
 *       para asociarlas correctamente a los reportes.
 * RF-08 Cuando un administrador administra ubicaciones
 *       institucionales, el sistema registra y actualiza la
 *       información de las ubicaciones asociadas a los reportes.
 * RN-09 No se permite registrar ubicaciones duplicadas
 *       dentro de la misma sede.
 * -----------------------------------------------------------
 * Casos de uso: CU-11 (Deshacer ultima eliminacion) . CU-16
 * Criterios: CA-08.1 (bueno) CA-08.2 (falla) CA-08.3 (falla)
 * Estructuras: Pila propia (sobre arreglo, sin java.util.Stack)
 * =========================================================== */
class PilaUbicacion {

    private Ubicacion[] pila;
    private int cima;

    // Crea la pila vacia con la capacidad indicada
    public PilaUbicacion(int capacidad) {

        pila = new Ubicacion[capacidad];
        cima = -1;
    }

    // CU-16 · RF-08 — apila una ubicacion eliminada; devuelve false si la pila esta llena
    public boolean apilar(Ubicacion u) {

        if (cima < pila.length - 1) {

            cima++;
            pila[cima] = u;

            return true;
        }

        return false;
    }

    // CU-11 · RF-08 — desapila y devuelve la ultima eliminada; null si la pila esta vacia
    public Ubicacion desapilar() {

        if (estaVacia()) {
            return null;
        }

        Ubicacion eliminado = pila[cima];
        pila[cima] = null;
        cima--;

        return eliminado;
    }

    // CU-11 · RF-08 — devuelve la ubicacion de la cima sin sacarla; null si la pila esta vacia
    public Ubicacion cima() {

        if (estaVacia()) {
            return null;
        }

        return pila[cima];
    }

    // Indica si la pila no tiene elementos
    public boolean estaVacia() {

        return cima == -1;
    }

    // Devuelve la cantidad de elementos apilados
    public int tamano() {

        return cima + 1;
    }
}