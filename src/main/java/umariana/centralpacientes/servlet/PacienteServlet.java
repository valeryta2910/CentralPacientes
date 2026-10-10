package umariana.centralpacientes.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import umariana.centralpacientes.modelo.CentralPacientes;
import umariana.centralpacientes.modelo.Paciente;
import umariana.centralpacientes.persistencia.ArchivoPacientes;

@WebServlet(name = "PacienteServlet", urlPatterns = {"/pacientes"})
public class PacienteServlet extends HttpServlet {
    private CentralPacientes central;
    private ArchivoPacientes archivo;
    
    @Override
    public void init() throws ServletException {
        archivo = new ArchivoPacientes();
        try {
            central = archivo.cargar();
        } catch (IOException e) {
            central = new CentralPacientes();
        }
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet PacienteServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet PacienteServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String codigoEditarStr = request.getParameter("codigo");
        if (codigoEditarStr != null && !codigoEditarStr.trim().isEmpty()) {
            int codigoEditar = Integer.parseInt(codigoEditarStr);
            Paciente pacienteEditar = central.localizar(codigoEditar);
            request.setAttribute("pacienteEditar", pacienteEditar);
        }
        
        String codigoBuscarStr = request.getParameter("buscarCodigo");
        if (codigoBuscarStr != null && !codigoBuscarStr.trim().isEmpty()) {
            int codigoBuscar = Integer.parseInt(codigoBuscarStr.trim());
            Paciente encontrado = central.localizar(codigoBuscar);
            if (encontrado != null) {
                request.setAttribute("pacienteEncontrado", encontrado);
            } else {
                request.setAttribute("mensajeBusqueda", "No se encontró ningún paciente con el código " + codigoBuscar);
            }
        }

        request.setAttribute("primero", central.getPrimero());
        request.getRequestDispatcher("/pacientes.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");
        if (accion == null) accion = "agregar";
        if ("eliminar".equals(accion)) {
            try {
                int codigoEliminar = Integer.parseInt(request.getParameter("codigo").trim());
                if (central.eliminarPaciente(codigoEliminar)) {
                    try {
                        archivo.guardar(central);
                        request.getSession().setAttribute("mensajeExito", "Paciente eliminado correctamente.");
                    } catch (IOException e) {
                        request.getSession().setAttribute("mensajeError", "Error al actualizar archivo: " + e.getMessage());
                    }
                } else {
                    request.getSession().setAttribute("mensajeError", "No existe un paciente con ese código.");
                }
            } catch (NumberFormatException | NullPointerException e) {
                request.getSession().setAttribute("mensajeError", "Error: el código debe ser un número válido.");
            }
            response.sendRedirect("pacientes");
            return;
        }
        int codigo;
        int sexo;
        try {
            codigo = Integer.parseInt(request.getParameter("codigo").trim());
            sexo = Integer.parseInt(request.getParameter("sexo").trim());
        } catch (NumberFormatException | NullPointerException e) {
            request.getSession().setAttribute("mensajeError",
                    "Error: el código y el sexo deben ser números válidos.");
            response.sendRedirect("pacientes");
            return;
        }
        String nombre = request.getParameter("nombre");
        String clinica = request.getParameter("clinica");
        String infoMedica = request.getParameter("informacionMedica");

        switch (accion) {
            case "editar":
                Paciente p = central.localizar(codigo);
                if (p != null) {
                    p.setNombre(nombre);
                    p.setClinica(clinica);
                    p.setInformacionMedica(infoMedica);
                    p.setSexo(sexo);
                    
                    try {
                        archivo.guardar(central);
                        request.getSession().setAttribute("mensajeExito", "Paciente actualizado correctamente.");
                    } catch (IOException e) {
                        request.getSession().setAttribute("mensajeError", "Error al actualizar archivo: " + e.getMessage());
                    }
                }
                break;

            case "agregar":
            default:
                if (central.localizar(codigo) != null) {
                    request.getSession().setAttribute("mensajeError", "¡Error! El código " + codigo + " ya existe.");
                } else {
                    Paciente nuevo = new Paciente(codigo, nombre, clinica, infoMedica, sexo);
                    
                    String posicion = request.getParameter("posicion");
                    
                    if("comienzo".equals(posicion)){
                        central.agregarPacienteAlComienzo(nuevo);
                    }
                    else if ("despues".equals(posicion)){
                            String codigoRefStr = request.getParameter("codigoRef");
                            if (codigoRefStr != null && !codigoRefStr.trim().isEmpty()) {
                                int codigoRef = Integer.parseInt(codigoRefStr.trim());

                                if (central.localizar(codigoRef) != null) {
                                    central.agregarPacienteDespuesDe(codigoRef, nuevo);
                                } else {
                                    request.getSession().setAttribute("mensajeError", "Error: El código de referencia " + codigoRef + " no existe.");
                                    response.sendRedirect("pacientes");
                                    return;
                                }
                            } else {
                                central.agregarPacienteAlFinal(nuevo);
                            }
                    } else {
                        central.agregarPacienteAlFinal(nuevo);
                    }

                    try {
                        archivo.guardar(central);
                        request.getSession().setAttribute("mensajeExito", "Paciente registrado correctamente.");
                    } catch (IOException e) {
                        request.getSession().setAttribute("mensajeError", "Error al guardar el archivo: " + e.getMessage());
                    }
                }
                break;
        }
        response.sendRedirect("pacientes");
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
