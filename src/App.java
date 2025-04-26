import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App implements Serializable {
    static class registroClientc implements Serializable {
        private String nombre;
        private String direccion;
        private double sueldo;
        private String fechaNacimiento;

        public registroClientc(String nombre, String direccion, double sueldo, String fechaNacimiento) {
            this.nombre = nombre;
            this.direccion = direccion;
            this.sueldo = sueldo;
            this.fechaNacimiento = fechaNacimiento;
        }

        public String lectura() {
            return this.nombre + "...." + this.direccion + "...." + this.sueldo + "...." + this.fechaNacimiento;
        }
    }

    // Método para ingresar datos
    public static void ingresarDatos(List<registroClientc> rClientes) {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;
        int i = 1;
        String continuar = "s";
        
        while (!salir && (continuar.startsWith("s") || continuar.startsWith("S"))) {
            System.out.println("Ingrese el nombre [" + i + "]:");
            String nombre = scanner.nextLine();
            System.out.println("Ingrese la dirección [" + i + "]:");
            String direccion = scanner.nextLine();
            System.out.println("Ingrese la fecha de nacimiento [" + i + "](yyyy-mm-dd):");
            String fe_string = scanner.nextLine();
            System.out.println("Ingrese el sueldo [" + i + "]:");
            double sueldo = scanner.nextDouble();
            scanner.nextLine(); // Limpiar buffer
            
            rClientes.add(new registroClientc(nombre, direccion, sueldo, fe_string));
            
            System.out.println("¿Continuar? (s/n)");
            continuar = scanner.nextLine();
            i++;
        }
    }

    // Método para mostrar datos
    public static void mostrarDatos(List<registroClientc> rClients) {
        System.out.println("Mostrando Datos");
        System.out.println("Nombre          Dirección           Sueldo       Fecha Nacimiento");
        rClients.forEach(cliente -> {
            System.out.print(repetirCaracter(" ", 3) + cliente.nombre + repetirCaracter(" ", 15 - cliente.nombre.length()));
            System.out.print(cliente.direccion + repetirCaracter(" ", 20 - cliente.direccion.length()));
            System.out.print("$" + cliente.sueldo + repetirCaracter(" ", 15 - String.valueOf(cliente.sueldo).length()));
            System.out.println(cliente.fechaNacimiento);
        });
    }

    // Métodos para archivos de texto
    public static void grabarTexto(List<registroClientc> clientes) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("datos.txt"))) {
            clientes.forEach(c -> pw.println(c.lectura()));
        } catch (IOException e) {
            System.err.println("Error escribiendo texto: " + e.getMessage());
        }
    }

    public static List<registroClientc> leerTexto() {
        List<registroClientc> clientes = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File("datos.txt"))) {
            while (scanner.hasNextLine()) {
                String[] datos = scanner.nextLine().split("\\.\\.\\.\\.");
                if (datos.length != 4) {
                    System.err.println("Línea corrupta: formato incorrecto");
                    continue;
                }
                try {
                    double sueldo = Double.parseDouble(datos[2].trim());
                    clientes.add(new registroClientc(
                        datos[0].trim(), 
                        datos[1].trim(), 
                        sueldo, 
                        datos[3].trim()
                    ));
                } catch (NumberFormatException e) {
                    System.err.println("Sueldo inválido en línea: " + datos[2]);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Archivo no encontrado: " + e.getMessage());
        }
        return clientes;
    }

    // Métodos para binario 1 (ObjectOutputStream)
    public static void grabarBin1(List<registroClientc> clientes) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("datos.bin1"))) {
            oos.writeObject(clientes);
        } catch (IOException e) {
            System.err.println("Error escribiendo bin1: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<registroClientc> leerBin1() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("datos.bin1"))) {
            return (List<registroClientc>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error leyendo bin1: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    // Métodos para binario 2 (DataOutputStream)
    public static void grabarBin2(List<registroClientc> clientes) {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream("datos.bin2"))) {
            for (registroClientc c : clientes) {
                dos.writeUTF(c.nombre);
                dos.writeUTF(c.direccion);
                dos.writeDouble(c.sueldo);
                dos.writeUTF(c.fechaNacimiento);
            }
        } catch (IOException e) {
            System.err.println("Error escribiendo bin2: " + e.getMessage());
        }
    }

    public static List<registroClientc> leerBin2() {
        List<registroClientc> clientes = new ArrayList<>();
        try (DataInputStream dis = new DataInputStream(new FileInputStream("datos.bin2"))) {
            while (dis.available() > 0) {
                clientes.add(new registroClientc(
                    dis.readUTF(),
                    dis.readUTF(),
                    dis.readDouble(),
                    dis.readUTF()
                ));
            }
        } catch (IOException e) {
            System.err.println("Error leyendo bin2: " + e.getMessage());
        }
        return clientes;
    }

    // Método auxiliar
    private static String repetirCaracter(String caracter, int cantidad) {
        return new String(new char[cantidad]).replace("\0", caracter);
    }

    public static void main(String[] args) {
        List<registroClientc> clientes = new ArrayList<>();
        ingresarDatos(clientes);
        
        // Grabar en archivos (sin leerlos después)
        grabarTexto(clientes);
        grabarBin1(clientes);
        grabarBin2(clientes);
        
        // Mostrar solo los datos originales
        System.out.println("\nDatos ingresados:");
        mostrarDatos(clientes);
    }
}