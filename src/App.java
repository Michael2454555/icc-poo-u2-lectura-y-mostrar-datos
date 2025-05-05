import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class App {
    static class RegistroCliente implements Serializable {
        String nombre;
        String direccion;
        double sueldo;
        String fechaNacimiento;

        RegistroCliente(String nombre, String direccion, double sueldo, String fechaNacimiento) {
            this.nombre = nombre;
            this.direccion = direccion;
            this.sueldo = sueldo;
            this.fechaNacimiento = fechaNacimiento;
        }
    }

    private static Scanner sc = new Scanner(System.in);

    private static String leerConPatron(String mensaje, String regex, String errorMsg) {
        String valor;
        while (true) {
            System.out.print(mensaje);
            valor = sc.nextLine().trim();
            if (valor.matches(regex)) {
                return valor;
            }
            System.out.println(errorMsg);
        }
    }

    public static void ingresarDatos(List<RegistroCliente> lista) {
        int i = lista.size() + 1;
        String cont = "s";
        while (cont.equalsIgnoreCase("s")) {
            String nombre = leerConPatron(
                "Ingrese nombre [" + i + "]: ",
                "[A-Za-z ]+",
                "Nombre inválido, solo letras y espacios permitidos."
            );

            String direccion = leerConPatron(
                "Ingrese dirección [" + i + "]: ",
                "[A-Za-z0-9 #\\.\\-]+",
                "Dirección inválida, solo letras, números, espacios, #, . y - permitidos."
            );

            String fecha;
            while (true) {
                System.out.print("Ingrese fecha [" + i + "] (yyyy-MM-dd): ");
                fecha = sc.nextLine().trim();
                if (fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    try {
                        LocalDate.parse(fecha);
                        break;
                    } catch (DateTimeParseException ex) {
                        System.out.println("Fecha inválida (mes/día fuera de rango). Intente de nuevo.");
                    }
                } else {
                    System.out.println("Formato de fecha inválido. Use yyyy-MM-dd.");
                }
            }

            double sueldo;
            while (true) {
                System.out.print("Ingrese sueldo [" + i + "]: ");
                try {
                    sueldo = Double.parseDouble(sc.nextLine());
                    break;
                } catch (NumberFormatException ex) {
                    System.out.println("Sueldo inválido, ingrese un número válido.");
                }
            }

            lista.add(new RegistroCliente(nombre, direccion, sueldo, fecha));
            System.out.println("Registro de cliente creado: " + nombre);
            System.out.print("¿Continuar? (s/n): ");
            cont = sc.nextLine();
            i++;
        }
    }

    public static void mostrarDatos(List<RegistroCliente> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay datos para mostrar.");
            return;
        }
        System.out.printf("%-5s %-15s %-20s %-10s %s%n", "#", "Nombre", "Dirección", "Sueldo", "Fecha");
        int i = 1;
        for (RegistroCliente c : lista) {
            System.out.printf("%-5d %-15s %-20s $%-9.2f %s%n",
                i++, c.nombre, c.direccion, c.sueldo, c.fechaNacimiento);
        }
    }

    public static void grabarTexto(List<RegistroCliente> lista) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("datos.txt"), StandardCharsets.UTF_8))) {
            for (RegistroCliente c : lista) {
                bw.write(c.nombre + "...." + c.direccion + "...." + c.sueldo + "...." + c.fechaNacimiento);
                bw.newLine();
            }
            System.out.println("Grabación de datos de texto terminada correctamente.");
        } catch (IOException e) {
            System.err.println("Error al grabar texto: " + e.getMessage());
        }
    }

    public static void grabarBin1(List<RegistroCliente> lista) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("datos_bin1.bin"))) {
            for (RegistroCliente c : lista) {
                oos.writeObject(c);
            }
            System.out.println("Grabación bin1 terminada correctamente.");
        } catch (IOException e) {
            System.err.println("Error al grabar bin1: " + e.getMessage());
        }
    }

    public static void grabarBin2(List<RegistroCliente> lista) {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream("datos_bin2.bin"))) {
            for (RegistroCliente c : lista) {
                dos.writeUTF(c.nombre);
                dos.writeUTF(c.direccion);
                dos.writeDouble(c.sueldo);
                dos.writeUTF(c.fechaNacimiento);
            }
            System.out.println("Grabación bin2 terminada correctamente.");
        } catch (IOException e) {
            System.err.println("Error al grabar bin2: " + e.getMessage());
        }
    }

    public static void leerTexto() {
        System.out.println("Leyendo datos de datos.txt...");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream("datos.txt"), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\.\\.\\.");
                System.out.printf("Nombre: %s, Dirección: %s, Sueldo: %s, Fecha: %s%n",
                    parts[0], parts[1], parts[2], parts[3]);
            }
        } catch (FileNotFoundException e) {
            System.err.println("El archivo datos.txt no existe.");
        } catch (IOException e) {
            System.err.println("Error al leer texto: " + e.getMessage());
        }
    }

    public static void leerBin1() {
        System.out.println("Leyendo datos de datos_bin1.bin...");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("datos_bin1.bin"))) {
            while (true) {
                RegistroCliente c = (RegistroCliente) ois.readObject();
                System.out.printf("Nombre: %s, Dirección: %s, Sueldo: %.2f, Fecha: %s%n",
                    c.nombre, c.direccion, c.sueldo, c.fechaNacimiento);
            }
        } catch (EOFException e) {
            // fin de archivo
        } catch (FileNotFoundException e) {
            System.err.println("El archivo datos_bin1.bin no existe.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al leer bin1: " + e.getMessage());
        }
    }

    public static void leerBin2() {
        System.out.println("Leyendo datos de datos_bin2.bin...");
        try (DataInputStream dis = new DataInputStream(new FileInputStream("datos_bin2.bin"))) {
            while (true) {
                String nombre = dis.readUTF();
                String direccion = dis.readUTF();
                double sueldo = dis.readDouble();
                String fecha = dis.readUTF();
                System.out.printf("Nombre: %s, Dirección: %s, Sueldo: %.2f, Fecha: %s%n",
                    nombre, direccion, sueldo, fecha);
            }
        } catch (EOFException e) {
            // fin de archivo
        } catch (FileNotFoundException e) {
            System.err.println("El archivo datos_bin2.bin no existe.");
        } catch (IOException e) {
            System.err.println("Error al leer bin2: " + e.getMessage());
        }
    }

    // ----- MÉTODOS DE ORDENAMIENTO -----

    public static void ordenarPorSueldoDesc(List<RegistroCliente> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay clientes para ordenar.");
            return;
        }
        lista.sort((c1, c2) -> Double.compare(c2.sueldo, c1.sueldo));
        System.out.println("Clientes ordenados por sueldo (mayor a menor):");
        mostrarDatos(lista);
    }

    public static void ordenarPorSueldoAsc(List<RegistroCliente> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay clientes para ordenar.");
            return;
        }
        lista.sort(Comparator.comparingDouble(c -> c.sueldo));
        System.out.println("Clientes ordenados por sueldo (menor a mayor):");
        mostrarDatos(lista);
    }

    public static void ordenarPorNombreAZ(List<RegistroCliente> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay clientes para ordenar.");
            return;
        }
        lista.sort(Comparator.comparing(c -> c.nombre.toLowerCase()));
        System.out.println("Clientes ordenados por nombre (A-Z):");
        mostrarDatos(lista);
    }

    public static void ordenarPorNombreZA(List<RegistroCliente> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay clientes para ordenar.");
            return;
        }
        lista.sort((c1, c2) -> c2.nombre.toLowerCase().compareTo(c1.nombre.toLowerCase()));
        System.out.println("Clientes ordenados por nombre (Z-A):");
        mostrarDatos(lista);
    }

    // ----- ELIMINAR CLIENTE -----

    public static void eliminarCliente(List<RegistroCliente> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay clientes para eliminar.");
            return;
        }
        mostrarDatos(lista);
        System.out.print("Ingrese el número del cliente a eliminar (1 a " + lista.size() + "): ");
        try {
            int indice = Integer.parseInt(sc.nextLine());
            if (indice >= 1 && indice <= lista.size()) {
                RegistroCliente eliminado = lista.remove(indice - 1);
                System.out.println("Cliente eliminado: " + eliminado.nombre);
            } else {
                System.out.println("Índice fuera de rango.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
        }
    }

    public static void main(String[] args) {
        List<RegistroCliente> clientes = new ArrayList<>();
        int opcion;
        do {
            System.out.println("\n=== MENÚ ===");
            System.out.println("1. Ingresar datos");
            System.out.println("2. Mostrar datos en memoria");
            System.out.println("3. Grabar texto");
            System.out.println("4. Grabar bin1");
            System.out.println("5. Grabar bin2");
            System.out.println("6. Leer texto");
            System.out.println("7. Leer bin1");
            System.out.println("8. Leer bin2");
            System.out.println("9. Salir");
            System.out.println("10. Ordenar clientes por sueldo (descendente)");
            System.out.println("11. Ordenar clientes por sueldo (ascendente)");
            System.out.println("12. Ordenar clientes por nombre (A-Z)");
            System.out.println("13. Ordenar clientes por nombre (Z-A)");
            System.out.println("14. Eliminar cliente atendido");
            System.out.print("Seleccione una opcion: ");

            try {
                opcion = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                opcion = -1;
            }

            switch (opcion) {
                case 1:  ingresarDatos(clientes);         break;
                case 2:  mostrarDatos(clientes);          break;
                case 3:  grabarTexto(clientes);           break;
                case 4:  grabarBin1(clientes);            break;
                case 5:  grabarBin2(clientes);            break;
                case 6:  leerTexto();                     break;
                case 7:  leerBin1();                      break;
                case 8:  leerBin2();                      break;
                case 9:  System.out.println("Saliendo del programa. ¡Hasta luego!"); break;
                case 10: ordenarPorSueldoDesc(clientes);  break;
                case 11: ordenarPorSueldoAsc(clientes);   break;
                case 12: ordenarPorNombreAZ(clientes);    break;
                case 13: ordenarPorNombreZA(clientes);    break;
                case 14: eliminarCliente(clientes);       break;
                default: System.out.println("Opción inválida. Intente de nuevo.");
            }
        } while (opcion != 9);
    }
}