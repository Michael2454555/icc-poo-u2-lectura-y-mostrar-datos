import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

    /**
     * Lee una línea que debe cumplir el regex, mostrando mensaje de error si no.
     */
    private static String leerConPatron(Scanner sc, String mensaje, String regex, String errorMsg) {
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
        Scanner sc = new Scanner(System.in);
        int i = 1;
        String cont = "s";
        while (cont.equalsIgnoreCase("s")) {
            String nombre = leerConPatron(sc,
                "Ingrese nombre [" + i + "]: ",
                "[A-Za-z ]+", 
                "Nombre inválido, solo letras y espacios permitidos.");

            String direccion = leerConPatron(sc,
                "Ingrese dirección [" + i + "]: ",
                "[A-Za-z0-9 #\\.\\-]+", 
                "Dirección inválida, solo letras, números, espacios, #, . y - permitidos.");

            String fecha;
            while (true) {
                System.out.print("Ingrese fecha [" + i + "](yyyy-MM-dd): ");
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

    public static void grabarTexto(List<RegistroCliente> lista) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("datos.txt"), StandardCharsets.UTF_8));
            for (RegistroCliente c : lista) {
                bw.write(c.nombre + "...." + c.direccion + "...." + c.sueldo + "...." + c.fechaNacimiento);
                bw.newLine();
            }
            System.out.println("Grabación de datos terminada correctamente.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void grabarBin1(List<RegistroCliente> lista) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream("datos_bin1.bin"));
            for (RegistroCliente c : lista) {
                oos.writeObject(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void grabarBin2(List<RegistroCliente> lista) {
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(new FileOutputStream("datos_bin2.bin"));
            for (RegistroCliente c : lista) {
                dos.writeUTF(c.nombre);
                dos.writeUTF(c.direccion);
                dos.writeDouble(c.sueldo);
                dos.writeUTF(c.fechaNacimiento);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        List<RegistroCliente> clientes = new ArrayList<>();
        ingresarDatos(clientes);
        grabarTexto(clientes);
        grabarBin1(clientes);
        grabarBin2(clientes);

        System.out.println();
        System.out.printf("%-15s %-20s %-10s %s%n", "Nombre", "Dirección", "Sueldo", "Fecha");
        for (RegistroCliente c : clientes) {
            System.out.printf("%-15s %-20s $%-9.2f %s%n", c.nombre, c.direccion, c.sueldo, c.fechaNacimiento);
        }
    }
}