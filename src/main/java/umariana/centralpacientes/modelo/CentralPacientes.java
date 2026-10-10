package umariana.centralpacientes.modelo;
public class CentralPacientes {
    private Paciente primero;
    private Paciente ultimo;
    private int numPacientes;

    public CentralPacientes() {
    primero = null;
    ultimo = null;
    numPacientes = 0;
    }
    
    public void agregarPacienteAlFinal(Paciente nuevo) {
        if (primero == null) {
            primero = nuevo;
            ultimo = nuevo;
        } else {
            ultimo.setSiguiente(nuevo);
            ultimo = nuevo;
        }
        numPacientes++;
    }

    public Paciente getPrimero() { return primero; }
    public int getNumPacientes() { return numPacientes; }
    
    public Paciente localizar(int codigo) {
        Paciente actual = primero;
        while (actual != null) {
            if (actual.getCodigo() == codigo) {
                return actual;
            }
            actual = actual.getSiguiente();
        }
        return null;
    }
    
    public void agregarPacienteAlComienzo(Paciente nuevo) {
        nuevo.setSiguiente(primero);
        primero = nuevo;
        if (ultimo == null) {
            ultimo = nuevo;
        }
        numPacientes++;
    }
    
    public void agregarPacienteDespuesDe(int codigo, Paciente nuevo) {
        Paciente actual = localizar(codigo);
        if (actual == null) {
            return;
        }
        nuevo.setSiguiente(actual.getSiguiente());
        actual.setSiguiente(nuevo);
        if (actual == ultimo) {
            ultimo = nuevo;
        }
        numPacientes++;
    }
    
    public boolean eliminarPaciente(int codigo) {
        Paciente anterior = null;
        Paciente actual = primero;

        while (actual != null && actual.getCodigo() != codigo) {
            anterior = actual;
            actual = actual.getSiguiente();
        }

        if (actual == null) {
            return false;
        }

        if (anterior == null) {
            primero = actual.getSiguiente();
        } else {
            anterior.setSiguiente(actual.getSiguiente());
        }

        if (actual == ultimo) {
            ultimo = anterior;
        }

        numPacientes--;
        return true;
    }
}
