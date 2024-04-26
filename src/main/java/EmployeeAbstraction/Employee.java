package EmployeeAbstraction;

/*======================================================================================================================
 ?                                                     ABOUT
 * @author         :  Carlos Villafuerte, Santiago Arellano
 * @repo           :  CMP2103 - Rol De Pagos
 * @description    :  Definicion e Implementacion de la Clase Base Employee
 *===================================================================================================================**/

/*? Directivas de Preprocesamiento*/
import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.directory.InvalidAttributesException;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;


/*? Declaraciones de Clase y XML*/
@XmlRootElement(name = "Employee")
@XmlType(propOrder = {"m_nombreEmployee", "m_apellidoEmployee","m_codigoEmployee", "m_fechaContratacionEmployee", "m_sueldoEmployee", "m_DesgloceSalarioEmployee"
,"m_DateEnNumero" })
@XmlSeeAlso({Manager.class})
public class Employee implements Comparator<Employee>, Comparable<Employee>, Serializable {


    public static InvalidAttributeValueException InvalidAttributeGenerator(String ErrorFunctionName, String ErrorValue)
    {
        return new InvalidAttributeValueException(String.format("Error Code 0x0001 - [Raised] - El valor enviado a la funcion" +
                "%10s, es incorrecto {Valor ingresado: %10s}. Referirse a la documentacion.", ErrorFunctionName, ErrorValue));
    }
    /* ? Variables Internas de la Clase*/

    /**
     * String representativa del nombre del Empleado a Registrar.
     * */
    private String m_nombreEmployee;

    /**
     * String representativa del apellido del Empleado a Registrar.
     */
    private String m_apellidoEmployee;

    /**
     * Integer representativa del codigo del Empleado a Registrar.
     */

    private Integer m_codigoEmployee;

    /**
     * String representativa de la fecha de contratacion del Empleado a Registrar.
     */

    private String m_fechaContratacionEmployee;

    /**
     * Float representativa del sueldo del Empleado a Registrar. El valor del sueldo debe estar en el
     * intervalo cerrado de [800,3500].
     */

    private Float m_sueldoEmployee;

    /**
     * Mapa representativo de los rubros involucrados en el sueldo del trabajador, desgloces por ingreso adicional
     */
    @XmlElement(name = "DesgloceSalarial")
    private Map<String, BigDecimal> m_DesgloceSalarioEmployee = new HashMap<>();

    /**
     * Long representativa de la fecha en format numerico estandar del Empleado a Registrar.
     */

    private Long m_DateEnNumero;

    /**
     * Instancia de la clase Date representativa de la minima fecha admisible dentro del programa para fecha de contratacion.
     * <br> Valor ajustado a 1.º de Enero de 1970
      */
    @XmlTransient
    private static final Date minHiringDate = Date.from(Instant.EPOCH);

    /**
     * Instancia de la clase Date representativa de la maxima fecha admisible dentro del programa para fecha de contratacion
     */
    @XmlTransient
    private static final Date maxHiringDate = Date.from(Instant.now());


    /*? Metodos Publicos de la Clase */

    // * Constructor sin valores requerido por JAXB para inicializar objetos

    /**
     * Constructor vacío requerido por JAXB para la inicialización de objetos.
     */
    public Employee() {
    }



    //* Constructor con valores requerido por JavaFX para componentes visuales y UI
    /**
     * Este es un constructor que se utiliza para crear un objeto de la clase Empleado con valores específicos.
     *
     * @param e_NombreEmpleado Un {@code String} que representa el nombre del empleado.
     * @param e_ApellidoEmpleado Un {@code String} que representa el apellido del empleado.
     * @param e_CodigoEmpleado Un {@code int} que representa el código único del empleado.
     * @param e_FechaContratacionEmpleado Un objeto {@code Date} que representa la fecha en que se contrató al empleado.
     * @param e_sueldoEmpleado Un {@code float} que representa el salario del empleado. El valor del salario debe estar en el intervalo cerrado de [800,3500].
     *
     * @throws InvalidAttributeValueException si alguna de las entradas no cumple con los criterios especificados.
     */
    public Employee(String e_NombreEmpleado, String e_ApellidoEmpleado, int e_CodigoEmpleado, long e_FechaContratacionEmpleado,
                    float e_sueldoEmpleado) throws InvalidAttributeValueException {
        this.setM_nombreEmployee(e_NombreEmpleado);
        this.setM_apellidoEmployee(e_ApellidoEmpleado);
        this.setM_codigoEmployee(e_CodigoEmpleado);
        this.setM_fechaContratacionEmployee(e_FechaContratacionEmpleado);
        this.setM_sueldoEmployee(e_sueldoEmpleado);

    }

    // ? Setters y Getters Para Nombre Empleado Usando Beans Syntax Requirements Por JAXB
    /**
     * Establece el nombre del empleado, siguiendo la sintaxis de Beans requerida por JAXB.
     *
     * @param e_NombreEmpleado Un {@code String} que representa el nombre del empleado.
     * Este valor no puede estar vacío. Si el valor proporcionado está vacío, la función
     * lanza una excepción de InvalidAttributeValueException.
     *
     * @return Un booleano indicando el éxito de la operación. Retorna verdadero si el nombre
     * se establece exitosamente, de lo contrario, se lanza una excepción.
     *
     * @throws InvalidAttributeValueException si el valor proporcionado para el nombre del empleado
     * está vacío. La excepción lleva un mensaje detallando el motivo y el valor proporcionado que
     * causó el error.
     */

    public boolean setM_nombreEmployee(String e_NombreEmpleado) throws InvalidAttributeValueException {

        if (!(e_NombreEmpleado.isEmpty()))
        {
            Employee.this.m_nombreEmployee = e_NombreEmpleado;
            return true;
        }
        else
        {
            throw (InvalidAttributeGenerator("setM_nombreEmployee", e_NombreEmpleado));
        }
    }


    /**
     * Obtiene el nombre del empleado.
     * Este es un método de JAXB Bean que permite la extracción de nombres de empleados de una instancia de Employee.
     *
     * @return Un {@code String} que representa el nombre del empleado puesto que la administración de empleados fue configurada durante su instanciacion.
     * puede retornar Null si el nombre del empleado no fue establecido previamente.
     */
    @XmlElement(name = "NameEmployee")
    public String getM_nombreEmployee() {return this.m_nombreEmployee;}

    // ? Setters y Getters Para Apellido Empleado Usando Beans Syntax Requirements Por JAXB


    /**
     * Establece el apellido del empleado, siguiendo la sintaxis de Beans requerida por JAXB.
     *
     * @param e_ApellidoEmpleado Un {@code String} que representa el apellido del empleado.
     * <br><br>
     *
     * Los posibles escenarios de excepción son:
     * - Si se proporciona un valor nulo para e_ApellidoEmpleado.
     * - Si se proporciona una cadena vacía para e_ApellidoEmpleado.
     *
     * @return Un booleano indicando el éxito de la operación. Retorna verdadero si el apellido
     * se establece con éxito, en caso contrario, lanza una excepción.
     *
     * @throws InvalidAttributeValueException si el valor proporcionado para el apellido del empleado
     * está vacío o es nulo. La excepción lleva un mensaje detallando el motivo y el valor proporcionado que
     * causó el error.
     */
    public boolean setM_apellidoEmployee(String e_ApellidoEmpleado) throws InvalidAttributeValueException {

        if (!(e_ApellidoEmpleado.isEmpty()))
        {
            Employee.this.m_apellidoEmployee = e_ApellidoEmpleado;
            return true;
        }
        else
        {
            throw (InvalidAttributeGenerator("setM_apellidoEmployee", e_ApellidoEmpleado));
        }
    }
    /**
     * Obtiene el apellido del empleado.
     * Este es un método de JAXB Bean que permite la extracción de apellidos de empleados de una instancia de Employee.
     *
     * @return Un {@code String} que representa el apellido del empleado, que se configuró durante su instanciacion.
     * Puede retornar null en caso de que el apellido del empleado no haya sido previamente establecido.
     */
    @XmlElement(name = "LNameEmployee")
    public String getM_apellidoEmployee() {return Employee.this.m_apellidoEmployee;}


    //? Setters y Getters Para Codigo Empleado Usando Beans Syntax Requirements Por JAXB


    /**
     * Establece el código del empleado, siguiendo la sintaxis de Beans requerida por JAXB.
     *
     * @param e_CodigoEmpleado Un {@code int} que representa el codigo unico del empleado.
     * Este valor no puede ser menor o igual a cero. Si el valor proporcionado es menor o igual a cero, la funcion
     * lanza una excepcion de InvalidAttributeValueException.

     * Escenarios probables de excepcion:
     * - Si se proporciona un valor igual o menor a cero para e_CodigoEmpleado.
     *
     * @return Un booleano indicando el exito de la operacion. Retorna verdadero si el código
     * se establece exitosamente, de lo contrario, se lanza una excepcion.
     *
     * @throws InvalidAttributeValueException si el valor proporcionado para el codigo del empleado
     * es menor o igual a cero. La excepcion lleva un mensaje detallando el motivo y el valor proporcionado que
     * causo el error.
     */
    public boolean setM_codigoEmployee(int e_CodigoEmpleado) throws InvalidAttributeValueException {

        if (e_CodigoEmpleado > 0 )
        {
            Employee.this.m_codigoEmployee = e_CodigoEmpleado;
            return true;
        }
        else
        {
            throw (InvalidAttributeGenerator("setM_codigoEmployee", String.valueOf(e_CodigoEmpleado)));
        }
    }

    /**
     * Obtiene el codigo unico del empleado.

     * Este es un metodo de jaxb bean que permite la extraccion del codigo del empleado de una instancia del objeto employee.
     *
     * @return un entero que representa el codigo unico del empleado, este valor fue configurado durante la instanciacion del objeto.
     * si el codigo del empleado no fue establecido previamente, este metodo puede retornar null.
     */
    @XmlElement(name = "IDEmployee")
    public int getM_codigoEmployee() {return Employee.this.m_codigoEmployee;}



    //? Setters y Getters Para Fecha Contratacion Empleado Usando Beans Syntax Requirements Por JAXB


    /**
     * Establece la fecha de contratacion del empleado, siguiendo la sintaxis de Beans requerida por JAXB.
     *
     * @param e_FechaContratacionEmpleado Un {@code long} que representa la fecha de contratacion del empleado en milisegundos desde la epoca Unix (1 de enero de 1970).<br>
     * Esta fecha es transformada internamente a un objeto {@link java.util.Date}.<br>
     * - Si el valor proporcionado es menor a Long.MIN_VALUE o mayor a Long.MAX_VALUE, se lanza una excepcion de InvalidAttributeValueException.<br>
     * - Si la fecha generada es anterior a la minFechaContratacion (1 de enero de 1970) o posterior a maxFechaContratacion (la fecha actual al momento de la creacion del objeto), tambien se lanza una excepcion.<br>
     *
     * @return Un booleano indicando el exito de la operacion. Retorna verdadero si la fecha de contratacion
     * se establece exitosamente, de lo contrario, se lanza una excepcion.
     *
     * @throws InvalidAttributeValueException si el valor proporcionado para la fecha de contratacion del empleado
     * es menor a Long.MIN_VALUE, mayor a Long.MAX_VALUE, o no cae dentro del intervalo permitido de fechas..
     * La excepcion lleva un mensaje detallando el motivo y el valor proporcionado que causo el error.
     */
    public boolean setM_fechaContratacionEmployee(Long e_FechaContratacionEmpleado) throws InvalidAttributeValueException {

        if (Long.MIN_VALUE < e_FechaContratacionEmpleado && e_FechaContratacionEmpleado < Long.MAX_VALUE)
        {
            Date internalDate = new Date(e_FechaContratacionEmpleado);
            if (!internalDate.before(minHiringDate))
            {
                this.m_fechaContratacionEmployee = new SimpleDateFormat("dd/MM/yyy HH:mm:ss").format(internalDate);
                this.m_DateEnNumero = e_FechaContratacionEmpleado;
                return true;
            }
        }
        else
        {
            throw (InvalidAttributeGenerator("setM_fechaContratacionEmployee", String.valueOf(e_FechaContratacionEmpleado)));
        }
        return false;
    }
    /**
     * Obtiene la fecha de contratacion del empleado.
     * Este es un método de JAXB Bean que permite la extracción de la fecha de contratacion de una instancia del objeto empleado.
     *
     * @return Un {@code Long} que representa la fecha de contratacion del empleado. Esta fecha fue configurada durante la instanciacion del objeto.
     * Si la fecha de contratacion del empleado no fue establecida previamente, este metodo puede retornar null.
     */
    @XmlElement(name = "DateHired")
    public Long getM_fechaContratacionEmployee() {return Employee.this.m_DateEnNumero;}



    //? Setters y Getters Para Sueldo Empleado Usando Beans Syntax Requirements Por JAXB

    /**
     * Este metodo establece el sueldo del empleado siguiendo la sintesis de Beans requerida por JAXB.
     *
     * @param e_SueldoEmpleado Un {@code float} que representa el sueldo del empleado.
     * El valor del sueldo debe estar en el rango cerrado de [800, 3500].
     * Si el valor proporcionado es menor a 800 o mayor a 3500, la funcion lanza una excepcion de InvalidAttributeValueException.
     *  <br><br>
     * Los posibles escenarios que pueden causar una excepcion son:<br>
     * - Si se proporciona un valor menor a 800 para e_SueldoEmpleado.<br>
     * - Si se proporciona un valor mayor a 3500 para e_SueldoEmpleado.<br>
     *
     * @return Un booleano que indica el exito de la operacion.
     * Retorna verdadero si el sueldo se establece con exito,
     * de lo contrario, se lanza una excepcion.
     *
     * @throws InvalidAttributeValueException si el valor proporcionado para el sueldo del empleado
     * es menor a 800 o mayor a 3500. La excepcion contiene un mensaje detallando
     * el motivo y el valor proporcionado que causo el error.
     */
    public boolean setM_sueldoEmployee(Float e_SueldoEmpleado) throws InvalidAttributeValueException {

        if (e_SueldoEmpleado >= 800 && e_SueldoEmpleado <= 3500)
        {
            Employee.this.m_sueldoEmployee = e_SueldoEmpleado;
            return true;
        }
        else
        {
            throw (InvalidAttributeGenerator("setM_sueldoEmployee", String.valueOf(e_SueldoEmpleado)));
        }
    }

    /**
     * Este metodo obtiene el sueldo del empleado.

     * Este es un metodo de JAXB Bean que permite la extraccion del sueldo de una instancia del objeto empleado.

     * Posibles casos donde se podria lanzar una excepcion:
     * - Si el sueldo del empleado no fue establecido previamente, este metodo puede retornar null, lo cual puede causar una NullPointerException si no se maneja adecuadamente.
     *
     * @return Un {@code float} que representa el sueldo del empleado. Este sueldo fue configurado durante la instanciacion del objeto.
     * Si el sueldo del empleado no fue establecido previamente, este metodo puede retornar null.
     */
    @XmlElement(name = "SalaryEmployee")
    public Float getM_sueldoEmployee() {return Employee.this.m_sueldoEmployee;}


    // ? Setters y Getters Para Campos de Desgloce de Salario

    /**
     * Establece un par clave-valor en el mapa m_DesgloceSalarioEmployee del empleado. El par clave-valor representa una entrada en el desglose de salario del empleado.
     *
     * @param e_MapEntry Es un par clave-valor que representa una entrada en el desglose de salario del empleado. El objeto Map.Entry proporcionado deberia contener una clave String y un valor BigDecimal.<br>
     *        La clave String representa el nombre del elemento en el desglose salarial y el valor BigDecimal representa el monto asignado a ese elemento. <br>
     *          <br>
     *        Los posibles escenarios que pueden causar una excepcion son:<br>
     *        - Si se proporciona un valor nulo para e_MapEntry, Java puede lanzar una NullPointerException.<br>
     *        - Si se proporciona un valor nulo para la clave o el valor en e_MapEntry, el metodo put de la clase Map puede lanzar una NullPointerException.<br>
     *
     * @return Un booleano que indica si el valor fue actualizado en el mapa.
     *         Retorna verdadero si la clave ya existia en el mapa (por lo tanto, el valor fue actualizado),
     *         y falso si la clave no existia en el mapa (por lo tanto, se inserto una nueva entrada en el mapa).
     */
    public boolean setM_MapEntry(Map.Entry<String, BigDecimal> e_MapEntry)

    {
        if (this.m_DesgloceSalarioEmployee.containsKey(e_MapEntry.getKey()))
        {
            this.m_DesgloceSalarioEmployee.put(e_MapEntry.getKey(), e_MapEntry.getValue());
            return true;
        }
        else
        {
            this.m_DesgloceSalarioEmployee.put(e_MapEntry.getKey(), e_MapEntry.getValue());
            return false;
        }
    }
    public BigDecimal getM_MapEntry(String e_Key)
    {
        return this.m_DesgloceSalarioEmployee.get(e_Key);
    }


    //? Setters y Getters Para Fecha de Contratacion Empleado Usando Beans Syntax Requirements Por JAXB


    /**
     * Este metodo obtiene la representacion numerica de la fecha de contratacion del empleado.
     *
     * Este es un metodo JAXB Bean que permite obtener la fecha de contratacion del empleado en su forma numerica de una instancia de Employee.

     * @return Un 'long' que representa la representacion numerica de la fecha de contratacion del empleado en milisegundos desde la epoca de Unix (1 de enero de 1970).
     * Esta fecha se configuro durante la creacion de la instancia del objeto empleado. Si la fecha de contratacion no fue establecida previamente, este metodo puede retornar null.
     */
    //? Metodo get Para Long value de fecha de contratacion
    @XmlElement(name = "DateNumericFormat")
    public Long getM_DateEnNumero() {return Employee.this.m_DateEnNumero;}


    //? Procedemos a Implementar el Metodo to String
    /**
     * Este método sobreescribe el método toString y devuelve una representación string de un empleado.
     *  <br><br>
     * Los atributos del empleado se presentan en el siguiente orden:<br>
     * - Nombre del empleado<br>
     * - Apellido del empleado<br>
     * - ID del empleado<br>
     * - Fecha de contratacion<br>
     * - Salario del empleado<br>
     * - Desglose del salario<br>
     *<br>
     * Cada atributo se muestra con su nombre y su valor, separados por dos puntos (:).<br>
     * Los atributos están separados por caracteres de tabulación (\t).<br>
     *
     * @return Una cadena formateada que representa el estado actual del empleado.
     */
    @Override
    public String toString() {

        return String.format("['Nombre Empleado': %s;\t'Apellido Empleado': %s;\t'ID Empleado': %s\t" +
                "\'Fecha de Contratacion:\' %s;\t\'Salario Empleado\': %s;\t" +
                "\'Desglose Salario\': %s]", this.m_nombreEmployee,
                this.m_apellidoEmployee, this.m_codigoEmployee, this.m_fechaContratacionEmployee,
                String.valueOf(this.m_sueldoEmployee),  this.m_DesgloceSalarioEmployee.toString());
    }


    //? Procedemos a Implementar el Metodo equals



    /**
     * Este metodo verifica la igualdad entre el objeto actual y el objeto proporcionado como argumento.
     * Comparara el sueldo y la fecha de contratacion. Si ambos son iguales
     * en el objeto proporcionado y en el actual, los dos objetos se consideraran iguales.
     *
     *
     * @param obj Un objeto a comparar con el objeto actual.
     * Este obj debería ser una instancia de la clase Employee para que la comparacion pueda ocurrir. Si no es una instancia de Employee,
     * el método devolvera false sin lanzar una excepcion.
     *
     * @return Un booleano indicando si los dos objetos se consideran iguales.
     * Devuelve verdadero si los objetos son iguales, y falso en caso contrario.
     */
    @Override
    public boolean equals(Object obj) {

        //? Paso Base, si el objeto a comparar esta vacio o no es de la misma clase, retornamos falso
        if (obj == null || obj.getClass() != this.getClass()) {return false;}

        //? Paso Base 2 Si el objeto apunta al mismo objeto retornamos verdadero
        if (obj == this) {return true;}

        //! Paso Inductivo: Si los otros casos pasaron, entonces tenemos que analizar con respecto a especificacion,
        //! si los campos de sueldo y fecha de contratacion son los mismos.

        Employee dummyEmployee = ((Employee) obj);
        return this.getM_sueldoEmployee().equals(dummyEmployee.getM_sueldoEmployee()) &&
            this.getM_DateEnNumero().equals(dummyEmployee.getM_DateEnNumero());
    }


    //? Procedemos a implementar el metodo compare de la clase Comparator

    /**
     * Este metodo implementa el metodo compare de la interfaz Comparator.
     * Esta implementacion compara los apellidos ('m_apellidoEmployee') de dos objetos 'Employee'
     * proporcionados como inputs. Los apellidos son comparados sin tener en cuenta su caso.
     *
     * @param left  El primer objeto 'Employee' que se va a comparar. Si 'left' es nulo, se
     * lanzara una excepcion NullPointerException.
     *
     * @param right El segundo objeto 'Employee' que se va a comparar. Si 'right' es nulo, se
     * lanzara una excepcion NullPointerException.
     *
     * @return Un int que es el resultado de comparar los apellidos del objeto 'Employee' 'left' y 'Employee' 'right'.
     * Devuelve un numero negativo si el apellido de 'left' es lexicograficamente menor que el apellido de 'right',
     * cero si son lexicograficamente iguales (independientemente de su caso),
     * y un numero positivo si el apellido de 'left' es lexicograficamente mayor que el apellido de 'right'.
     *
     * @throws NullPointerException si los parametros 'left' o 'right' son nulos.
     */
    @Override
    public int compare(Employee left, Employee right) {

        //? Paso Base 1: Objetos Nulos?
        if (left == null || right == null){
            throw (new NullPointerException("Error Code 0x0001 - [Raised] Uno o Ambos argumentos enviados al metodo compare fueron null"));}
        return Objects.compare(left.getM_apellidoEmployee(), right.m_apellidoEmployee, String::compareToIgnoreCase);
    }

    //? Procedemos a implementar el metodo compareTo de la clase comparable

    /**
     * Este metodo implementa el metodo compareTo de la interfaz Comparable.
     * Esta implementacion compara el sueldo ('m_sueldoEmployee') del objeto actual 'Employee' con el sueldo del objeto 'Employee' pasado como argumento
     *
     * @param other El objeto 'Employee' con el que se esta comparando el objeto actual.
     * Si 'other' es nulo, se lanzara una excepcion NullPointerException.
     *
     * @return Un int que es la comparacion del salario actual del empleado con el del empleado 'other'.
     * Devuelve un numero negativo si el sueldo del objeto actual es menor que el sueldo del 'other', cero si son iguales, y un numero positivo si el sueldo del objeto actual es mayor que el sueldo del 'other'.
     *
     * @throws NullPointerException si el parametro 'other' es nulo.
     */
    @Override
    public int compareTo(Employee other) {

        //? Paso Base Revision de Valores Nulos
        if (other == null) {throw new NullPointerException("Error Code 0x0001 - [Raised] Invalid Argument passed into compareTo method, second arg. is null");}
        //! Paso inductivo: usamos el compareTo robusto de Float, evitamos implementacion propia
        return Objects.compare(this.getM_sueldoEmployee(), other.getM_sueldoEmployee(), Float::compareTo);
    }

    //? Implementation of a  CSV Serialization Method
    /**
     * Este metodo implementa una representacion de serializacion CSV de los campos internos del objeto.
     * <br> Se utiliza un StringBuilder para concatenar los campos del objeto, siguiendo este orden:
     * <code> 'm_nombreEmployee', 'm_apellidoEmployee', 'm_codigoEmployee', 'm_fechaContratacionEmployee',
     * 'm_sueldoEmployee', y 'm_DesgloceSalarioEmployee'. </code> Cada campo es seguido por una coma.
     *
     * @return Un string que representa al objeto Employee en formato CSV.
     * Este string incluye representaciones de los campos internos del objeto, concatenados con comas.
     */
    public String toCSVString()

    {
        //? First Create a String Builder
        StringBuilder CSVrepresentation = new StringBuilder();
        CSVrepresentation.append(this.getM_nombreEmployee()).append(",");
        CSVrepresentation.append(this.getM_apellidoEmployee()).append(",");
        CSVrepresentation.append(this.getM_codigoEmployee()).append(",");
        CSVrepresentation.append(this.getM_fechaContratacionEmployee()).append(",");
        CSVrepresentation.append(this.getM_sueldoEmployee()).append(",");
        CSVrepresentation.append(this.m_DesgloceSalarioEmployee.toString());
        return CSVrepresentation.toString();
    }
}
