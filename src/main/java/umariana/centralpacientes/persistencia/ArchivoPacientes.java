/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package umariana.centralpacientes.persistencia;
import umariana.centralpacientes.modelo.CentralPacientes;
import umariana.centralpacientes.modelo.Paciente;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Tatiana Acosta
 */
public class ArchivoPacientes {
    private final Path ruta = Paths.get("data", "pacientes.txt");

    public void guardar(CentralPacientes central) throws IOException {
        Files.createDirectories(ruta.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8)) {
            Paciente actual = central.getPrimero();
            while (actual != null) {
                bw.write(actual.getCodigo() + "|" + actual.getNombre() + "|" +
                          actual.getClinica() + "|" +
                          actual.getInformacionMedica() + "|" +
                          actual.getSexo());
                bw.newLine();
                actual = actual.getSiguiente();
            }
        }
    }
    
    public CentralPacientes cargar() throws IOException {
        CentralPacientes central = new CentralPacientes();
        if (!Files.exists(ruta)) return central;

        for (String linea : Files.readAllLines(ruta, StandardCharsets.UTF_8)) {
            if (linea.trim().isEmpty()) continue;
            
            String[] d = linea.split("\\|", -1);
            Paciente p = new Paciente(
                Integer.parseInt(d[0]), d[1], d[2], d[3],
                Integer.parseInt(d[4]));
            central.agregarPacienteAlFinal(p);
        }
        return central;
    }
}
