package EmployeeAbstraction;

/*======================================================================================================================
 ?                                                     ABOUT
 * @author         :  Santiago Arellano
 * @repo           :  CMP2103 - Rol De Pagos
 * @description    :  Definicion e Implementacion de la Clase Derivada Manager
 *===================================================================================================================**/

import javax.naming.directory.InvalidAttributeValueException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Objects;

@XmlRootElement(name = "Manager")
@XmlType(propOrder ={"m_TituloNivelManager","m_ComisionManager"})
public class Manager extends Employee implements Serializable {

    /**
     * String representativa del titulo de nivel superior del Manager a Registrar.
     */

    private String m_TituloNivelManager;
    /**
     * Float representativa de la comision del Manager a Registrar. Utiliza un valor estandar, 15%.
     */

    private Float m_ComisionManager;

    /*Constantes para analisis interno de clases y programa en general*/
    public static final String DOCTORADO_CONSTANT = "Doctorado";
    public static final String MAESTRIA_CONSTANT = "Maestria";
    public static final String TERCER_NIVEL_CONSTANT = "TercerNivel";
    //? Constructor Simple sin Argumentos Para JAXB
    /**
     * Constructor de la clase Manager.
     * Este constructor no toma argumentos y se utiliza principalmente para la inicializacion de objetos con JAXB.
     * Llama explícitamente al constructor de la clase base Employee.
     */
    @SuppressWarnings("unused")
    public Manager(){super();}

    //? Constructor Con Cinco Argumentos para JAXB
    /**
     * Este es un constructor de la clase Manager con cinco argumentos especificos, utilizado principalmente para JAXB.
     * Este constructor llama explicitamente al constructor de la superclase Employee con algunos de los argumentos proporcionados.
     *
     * @param e_TituloNivelManager Un {@code String} que representa el titulo de nivel superior del Manager a registrar. No puede estar vacio.
     * @param e_ComisionManager Un {@code float} que representa la comision del Manager a registrar. El valor de comision debe estar en el intervalo abierto de (0,1).
     * @param e_NombreEmpleado Un {@code String} que representa el nombre del empleado.
     * @param e_ApellidoEmpleado Un {@code String} que representa el apellido del empleado.
     * @param e_CodigoEmpleado Un {@code int} que representa el código único del empleado.
     * @param e_FechaContratacionEmpleado Un {@code long} que representa la fecha en que se contrató al empleado.
     * @param e_sueldoEmpleado Un {@code float} que representa el salario del empleado. El valor del salario debe estar en el intervalo cerrado de [800,3500].
     *
     * @throws InvalidAttributeValueException si alguna de las entradas no cumple con los criterios especificos para cada parametro.
     */
    public Manager(String e_TituloNivelManager, float e_ComisionManager, String e_NombreEmpleado, String e_ApellidoEmpleado,
                   int e_CodigoEmpleado, long e_FechaContratacionEmpleado, float e_sueldoEmpleado) throws InvalidAttributeValueException
    {
        super(e_NombreEmpleado, e_ApellidoEmpleado, e_CodigoEmpleado, e_FechaContratacionEmpleado, e_sueldoEmpleado);
        this.setM_TituloNivelManager(e_TituloNivelManager);
        this.setM_ComisionManager(e_ComisionManager);
    }



    //? Getters y Setters en Base A Beans Syntaxis para JAXB
    /**
     * Este metodo ajusta el Titulo del Nivel de Gerencia para el objeto Manager.
     * Este metodo espera recibir un String no vacio que sera utilizado para actualizar el Titulo de Nivel de Gerencia de este objeto Manager.
     *
     * @param e_TituloNivelManager Un {@code String} que representa el Titulo de Nivel de Gerencia para el Manager a registrar. No puede estar vacio.
     * @return Un {@code boolean} que indica si el Titulo de Nivel de Gerencia fue configurado correctamente.
     * @throws InvalidAttributeValueException si el String proporcionado esta vacio.
     *
     */

    public boolean setM_TituloNivelManager(String e_TituloNivelManager) throws InvalidAttributeValueException {

        if (!(e_TituloNivelManager.isEmpty()))
        {
            this.m_TituloNivelManager = e_TituloNivelManager;
            return true;
        }
        else {throw Employee.InvalidAttributeGenerator("setM_TituloNivelManager", e_TituloNivelManager);}
    }
    /**
     * Esta funcion devuelve el Titulo de Nivel de Gerencia del objeto Manager.
     * <br><br>
     * Invoca al constructor de Employee subyacente para obtener la informacion del objeto Manager instanciado.
     * No se necesita ningun aporte para esta funcion, ya que solo se trata de un getter que devuelve un valor preexistente.
     *<br><br>
     * @return Un {@code String} que representa el titulo de nivel superior del Manager registrado.
     */
    @XmlElement(name = "TituloNivelManager")
    public String getM_TituloNivelManager() {return this.m_TituloNivelManager;}

    /**
     * Este metodo establece la comision del Manager.<br>
     * Este metodo invoca al constructor subyacente de la clase Employee para establecer la comision<br>
     * del objeto Manager si el valor proporcionado esta en el rango aceptable de (0,5000].<br>
     *
     * @param e_ComisionManager Un {@code float} que representa la comision del Manager a registrar. El valor de la comision debe estar en el intervalo abierto de (0,1).
     * @return Un {@code boolean} que indica si la comision fue configurada correctamente.
     * @throws InvalidAttributeValueException si el valor proporcionado no esta en el intervalo abierto de (0,5000].
     */
    public boolean setM_ComisionManager(Float e_ComisionManager) throws InvalidAttributeValueException {

        if (e_ComisionManager > 0 && e_ComisionManager <= 5000)
        {
            this.m_ComisionManager = e_ComisionManager;
            return true;
        }
        else {throw Employee.InvalidAttributeGenerator("setM_ComisionManager", String.valueOf(e_ComisionManager));}
    }
    /**
     * Esta funcion devuelve la comision del objeto Manager.
     * <br><br>
     *
     * @return Un {@code float} que representa la comision del Manager registrado.
     */
    @XmlElement(name = "ComisionManager")
    public Float getM_ComisionManager() {return this.m_ComisionManager;}


    //? Implementacion de la clase Comparable con el Sueldo y la Comision

    /**
     * Este metodo realiza una comparacion sobre el objeto 'Manager' en base al salario total (sueldo y comision).
     * Se realiza una llamada al método compareTo de la clase Float para llevar a cabo la comparación.
     *  <br><br>
     * @param other El objeto 'Employee' al que se le hara la comparacion. Este no puede ser nulo.
     * @return Un valor entero que indica el resultado de la comparacion. Si el valor es
     *         0, indica que ambas instancias son iguales.
     *         Si el valor es menor que 0, indica que 'this' es menor que 'other'.
     *         Si el valor es mayor que 0, indica que 'this' es mayor que 'other'.
     * @throws NullPointerException si el parametro 'other' es nulo.
     */
    @Override
    public int compareTo(Employee other) {

        //? Paso Base revision de null values en other
        if (other == null) {throw new NullPointerException("Error Code 0x0001 - [Raised] Invalid Argument passed into compareTo method, second arg. is null");}
        //! Paso inductivo: usamos el compareTo robusto de Float, evitamos implementacion propia
        return Objects.compare(this.getM_sueldoEmployee() + this.getM_ComisionManager(),
                other.getM_sueldoEmployee() + ((Manager) other).getM_ComisionManager(), Float::compareTo);
    }

    //? Implementacion del metodo toString()

    /**
     * Este metodo sobreescribe el metodo toString de la superclase Empleado y devuelve una representacion de String de un Manager.
     * <br><br>
     * Los atributos del Manager se presentan en el siguiente orden:<br>
     * - Titulo de Nivel de Gerencia del Manager<br>
     * - Bonificacion del Manager<br>
     * - Atributos de la superclase Empleado:<br>
     *   - Nombre del empleado<br>
     *   - Apellido del empleado<br>
     *   - ID del empleado<br>
     *   - Fecha de contratacion<br>
     *   - Salario del empleado<br>
     *   - Desglose del salario<br>
     * <br>
     * Cada atributo se muestra con su nombre y su valor, separados por dos puntos (:).<br>
     * Los atributos están separados por caracteres de tabulación (\t).<br>
     * @return Una cadena formateada que representa el estado actual del Manager.
     */
    @Override
    public String toString() {

        String newManagerRep = String.format("['Titulo Manager: %s;\t'Bono Manager': %s;\t",
                this.getM_TituloNivelManager(), this.getM_ComisionManager());
        return newManagerRep + super.toString().replace('[', ' ');
    }

    //? Implementamos un overwrite del metodo para serializacion CSV

    @Override
    public String toCSVString() {
        String originalEmployee = super.toCSVString();
        StringBuilder additionalString = new StringBuilder(originalEmployee);
        additionalString.append(",").append(this.getM_TituloNivelManager()).append(",");
        additionalString.append(this.getM_ComisionManager());
        return additionalString.toString();
    }
}
