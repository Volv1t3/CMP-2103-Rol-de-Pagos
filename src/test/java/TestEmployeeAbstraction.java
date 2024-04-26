import EmployeeAbstraction.*;

import javax.naming.directory.InvalidAttributeValueException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestEmployeeAbstraction {

    public static void main(String[] args){//? Lets create a single instance of said employee
        long time = System.currentTimeMillis();
        Employee employee = new Employee(); //? Testing No Arg Constructor
        try {
            if (employee.setM_codigoEmployee(10))
            {
                System.out.println("Employee Code: " + employee.getM_codigoEmployee());
            }
            if (employee.setM_apellidoEmployee("Arellano"))
            {
                System.out.println("Employee Last Name: " + employee.getM_apellidoEmployee());
            }
            if (employee.setM_nombreEmployee("Juan"))
            {
                System.out.println("Employee First Name: " + employee.getM_nombreEmployee());
            }
            if (employee.setM_sueldoEmployee(2400f))
            {
                System.out.println("Employee Salary: " + employee.getM_sueldoEmployee());
            }

            if (employee.setM_fechaContratacionEmployee(time))
            {
                System.out.println("Employee Contract Date: " + employee.getM_fechaContratacionEmployee());
            }
            if (employee.setM_MapEntry(Map.entry("DecimoTercero", BigDecimal.valueOf(2400f))))
            {
                System.out.println("Employee Map Entry: " + employee.getM_MapEntry("DecimoTercero"));
            }
            System.out.println(employee);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        List<Employee> employees = new ArrayList<>();
        //?Lets attempt to create  single fully functional employee
        try {
            Employee employee1 = new Employee("Sofia", "Jaramillo",20,time, 2400f);
            if (employee1.equals(employee)) {
                System.out.println("Employee 1 is equal to Employee 2");
            }
            else {
                System.out.println("Employee 1 is not equal to Employee 2");
            }
            //? Okay comparativa de sueldos y de tiempo funciona, si la variacion de tiempo es lo mas minimo desigual no son iguales los empleados. Esto porque compara contrataciones
            //? hasta por el segundo de contratacion. Compara el long representativo de la fecha de contratacion
            Employee manager = new Manager("3ro", 2400, "Luis", "Avellan",240, time, 3500f);

            employees.add(employee);
            employees.add(employee1);
            employees.add(manager);
            employees.forEach(System.out::println);
        } catch (InvalidAttributeValueException e) {
            throw new RuntimeException(e);
        }


        try {
            JAXBContext context = JAXBContext.newInstance(EmployeeListWrapper.class);
            Marshaller marshaller = context.createMarshaller();

            EmployeeListWrapper newListWrapper = new EmployeeListWrapper(employees);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(newListWrapper, new File("./Results.xml"));

            EmployeeListWrapper unwrapped= (EmployeeListWrapper) context.createUnmarshaller().unmarshal(new File("./Results.xml"));
            System.err.println("Imprimiendo Objtos del XML");
            unwrapped.getM_employees().forEach(System.out::println);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }


    }

}
