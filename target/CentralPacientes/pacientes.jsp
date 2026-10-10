<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="umariana.centralpacientes.modelo.Paciente" %>
<%
    Paciente actual = (Paciente) request.getAttribute("primero");
    Paciente editar = (Paciente) request.getAttribute("pacienteEditar");
    boolean modoEditar = (editar != null);

    String exito = (String) session.getAttribute("mensajeExito");
    String error = (String) session.getAttribute("mensajeError");
    session.removeAttribute("mensajeExito");
    session.removeAttribute("mensajeError");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Pacientes</title>
</head>
<body>
    <h1>Central de Pacientes</h1>

    <% if (exito != null) { %>
        <p style="color:green;"><%= exito %></p>
    <% } %>
    <% if (error != null) { %>
        <p style="color:red;"><%= error %></p>
    <% } %>

    <h2><%= modoEditar ? "Editar paciente" : "Registrar paciente" %></h2>
    <form action="pacientes" method="post">
        <input type="hidden" name="accion" value="<%= modoEditar ? "editar" : "agregar" %>">

        <label>Código:</label>
        <input type="number" name="codigo" required
               value="<%= modoEditar ? editar.getCodigo() : "" %>"
               <%= modoEditar ? "readonly" : "" %>><br>

        <label>Nombre:</label>
        <input type="text" name="nombre" required
               value="<%= modoEditar ? editar.getNombre() : "" %>"><br>

        <label>Clínica:</label>
        <input type="text" name="clinica" required
               value="<%= modoEditar ? editar.getClinica() : "" %>"><br>

        <label>Información médica:</label>
        <input type="text" name="informacionMedica" required
               value="<%= modoEditar ? editar.getInformacionMedica() : "" %>"><br>

        <label>Sexo:</label>
        <select name="sexo">
            <option value="1" <%= modoEditar && editar.getSexo() == 1 ? "selected" : "" %>>1 - Masculino</option>
            <option value="2" <%= modoEditar && editar.getSexo() == 2 ? "selected" : "" %>>2 - Femenino</option>
        </select><br>

        <% if (!modoEditar) { %>
            <br>
            <label>Ubicación de inserción:</label>
            <select name="posicion" id="posicionSelect" onchange="mostrarCodigoRef()">
                <option value="final">Al final de la lista</option>
                <option value="comienzo">Al comienzo de la lista</option>
                <option value="despues">Después de un código</option>
            </select><br>

            <div id="divCodigoRef" style="display:none; margin-top:5px;">
                <label>Código del paciente de referencia:</label>
                <input type="number" name="codigoRef" placeholder="Ej: 101"><br>
            </div>

            <script>
                function mostrarCodigoRef() {
                    var select = document.getElementById("posicionSelect");
                    var divRef = document.getElementById("divCodigoRef");
                    divRef.style.display = (select.value === "despues") ? "block" : "none";
                }
            </script>
        <% } %>
        
        <button type="submit"><%= modoEditar ? "Guardar cambios" : "Registrar" %></button>
        <% if (modoEditar) { %>
            <a href="pacientes">Cancelar</a>
        <% } %>
    </form>

    <form action="pacientes" method="get" style="margin-bottom: 15px;">
        <label>Buscar paciente por código:</label>
        <input type="number" name="buscarCodigo" placeholder="Ej: 101" required>
        <button type="submit">Buscar</button>
        <a href="pacientes">Ver todos</a>
    </form>
    <% 
        Paciente encontrado = (Paciente) request.getAttribute("pacienteEncontrado");
        String mensajeBusqueda = (String) request.getAttribute("mensajeBusqueda");
        if (encontrado != null) { 
    %>
        <h3 style="color: blue;">Resultado de la búsqueda:</h3>
        <table border="1" cellpadding="5" style="margin-bottom: 20px; background-color: #e8f4f8;">
            <tr>
                <th>Código</th>
                <th>Nombre</th>
                <th>Clínica</th>
                <th>Información médica</th>
                <th>Sexo</th>
                <th>Acciones</th>
            </tr>
            <tr>
                <td><%= encontrado.getCodigo() %></td>
                <td><%= encontrado.getNombre() %></td>
                <td><%= encontrado.getClinica() %></td>
                <td><%= encontrado.getInformacionMedica() %></td>
                <td><%= (encontrado.getSexo() == 1) ? "1 - Masculino" : "2 - Femenino" %></td>
                <td>
                    <a href="pacientes?codigo=<%= encontrado.getCodigo() %>">Editar</a>
                    <form action="pacientes" method="post" style="display:inline;"
                          onsubmit="return confirm('¿Eliminar este paciente?');">
                        <input type="hidden" name="accion" value="eliminar">
                        <input type="hidden" name="codigo" value="<%= encontrado.getCodigo() %>">
                        <button type="submit">Eliminar</button>
                    </form>
                </td>
            </tr>
        </table>
    <% } else if (mensajeBusqueda != null) { %>
        <p style="color: red; font-weight: bold;"><%= mensajeBusqueda %></p>
    <% } %>

    <h2>Listado de pacientes</h2>
    <table border="1" cellpadding="5">
        <tr>
            <th>Código</th><th>Nombre</th><th>Clínica</th>
            <th>Información médica</th><th>Sexo</th><th>Acciones</th>
        </tr>
        <% while (actual != null) { %>
        <tr>
            <td><%= actual.getCodigo() %></td>
            <td><%= actual.getNombre() %></td>
            <td><%= actual.getClinica() %></td>
            <td><%= actual.getInformacionMedica() %></td>
            <td><%= actual.getSexo() %></td>
            <td>
                <a href="pacientes?codigo=<%= actual.getCodigo() %>">Editar</a>
                <form action="pacientes" method="post" style="display:inline;"
                      onsubmit="return confirm('¿Eliminar este paciente?');">
                    <input type="hidden" name="accion" value="eliminar">
                    <input type="hidden" name="codigo" value="<%= actual.getCodigo() %>">
                    <button type="submit">Eliminar</button>
                </form>
            </td>
        </tr>
        <% actual = actual.getSiguiente(); } %>
    </table>
</body>
</html>
