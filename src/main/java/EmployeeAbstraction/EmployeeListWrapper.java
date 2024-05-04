package EmployeeAbstraction;
/*======================================================================================================================
 ?                                                     ABOUT
 * @author         :  Santiago Arellano
 * @repo           :  CMP2103 - Rol De Pagos
 * @description    :  Definicion e Implementacion de la clase EmployeListWrapper
 =====================================================================================================================*/

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "Empleados")
@XmlAccessorType(XmlAccessType.FIELD)
public class EmployeeListWrapper implements Serializable {

    /**
     * Lista de Empleados a Registrar.
     */
    @XmlElement(name = "Empleado")
    private List<Employee> m_employees;

    //? Main No Args constructor requerido por JAXB
    @SuppressWarnings("unused")
    public EmployeeListWrapper() {}

    /**
     * Constructor sin parametros.
     * Este constructor es requerido por JAXB (Java Architecture for XML Binding) y
     * sirve para la deserializacion de XML y el objeto EmployeeListWrapper.
     */

    @SuppressWarnings("unused")
    public EmployeeListWrapper(List<Employee> e_employees) {
        this.m_employees = e_employees;
    }

    /**
     * Metodo para obtener la lista de empleados
     *<br><br>
     * Este metodo devuelve una lista de objetos del tipo 'Employee'. No recibe ningun
     * parametro como entrada.
     *<br><br>
     * El metodo `getM_employees` se usa principalmente para obtener la lista
     * de empleados que esta siendo manejada dentro de la instancia de la clase
     * 'EmployeeListWrapper' en la que se llama.
     *<br>
     *<br>
     * Ejemplo de uso tipico:
     * <br><br>
     *
     * <code>
     * EmployeeListWrapper wrapper = new EmployeeListWrapper(someEmployeeList); <br>
     * List<Employee> employeeList = wrapper.getM_employees() </code>
     *
     * @return <code> List< Employee > </code> Una lista de objetos 'Employee'
     */
    @SuppressWarnings("unused")
    public List<Employee> getM_employees() {
        return m_employees;
    }

    /**
     * Establece la lista de empleados.
     *<br><br>
     * Este método recibe como entrada una lista de objetos del tipo 'Employee' y los
     * asigna a la lista interna 'm_employees'.
     * Ejemplo de uso tipico:
     *<br><br>
     *
     * <code>
     * EmployeeListWrapper wrapper = new EmployeeListWrapper(); <br>
     * wrapper.setM_employees(someEmployeeList); </code>
     *<br><br>
     * @param e_employees <code> List< Employee > </code> Una lista de objetos 'Employee'
     */
    @SuppressWarnings("unused")
    public void setM_employees(List<Employee> e_employees) {
        this.m_employees = e_employees;
    }

}
