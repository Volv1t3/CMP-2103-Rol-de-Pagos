import CustomExceptions.FileNotFoundAlert;
import EmployeeAbstraction.*;

import javax.naming.directory.InvalidAttributeValueException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

public class TestCSVFormatting {

    public static void main(String[] args) throws InvalidAttributeValueException {
        long time = System.currentTimeMillis();
        Employee employee = new Employee(); //? Testing No Arg Constructor
        try {
            if (employee.setM_codigoEmployee(10)) {
                System.out.println("Employee Code: " + employee.getM_codigoEmployee());
            }
            if (employee.setM_apellidoEmployee("Arellano")) {
                System.out.println("Employee Last Name: " + employee.getM_apellidoEmployee());
            }
            if (employee.setM_nombreEmployee("Juan")) {
                System.out.println("Employee First Name: " + employee.getM_nombreEmployee());
            }
            if (employee.setM_sueldoEmployee(2400f)) {
                System.out.println("Employee Salary: " + employee.getM_sueldoEmployee());
            }

            if (employee.setM_fechaContratacionEmployee(time)) {
                System.out.println("Employee Contract Date: " + employee.getM_fechaContratacionEmployee());
            }
            if (employee.setM_MapEntry(Map.entry("DecimoTercero", BigDecimal.valueOf(2400f)))) {
                System.out.println("Employee Map Entry: " + employee.getM_MapEntry("DecimoTercero"));
            }
            System.out.printf("toString() Normal de Employee: %s\n", employee);
            System.out.printf("toCSVString() Normal de Employee : %s\n", employee.toCSVString());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        //? Probemos lo mismo pero en un Manager
        Employee manager = new Manager("3ro", 2400, "Luis", "Avellan", 240, time, 3500f);
        try {
            System.out.printf("toString() Normal de manager: %s\n", manager);
            System.out.printf("toCSVString() Normal de manager : %s\n", manager.toCSVString());

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        //? Since Serialization works, I would like to try out how we might deserialize these things. Lets begin by
        /*?
        ? When we are deserializing data we have two cases, either our deserialized string (Line) will have 6
        ? elements or 8 elements, employee or manager respectively. From here, most fields are straightforward and
        ? standard, until you come to the salary desglose. In here we will have an special string, wrapped on {} that
        ? will hold values separated by =, on one side the key and on the other the value, guessing from the amount
        ?of values that we have to serialize, we can use a two pointer approach and iterate over.
        ?*/

        //! Assume that the employee toCSV method returns a read in line from a file
        String line = manager.toCSVString(); // this has a manager line, therefore we have to enter a switch

        try {
            String[] strings = line.split(",");
            switch (strings.length) //? Splitting by the amount of items present in the array of values
            {
                case 6 /*Can Be replaced by Constant*/: {
                    System.out.println("Base Employee Identified");
                    // Create a new instance to work with
                    Employee parsedEmployee = new Employee();
                    if (parsedEmployee.setM_nombreEmployee(strings[0])) {
                        System.out.println("Name Correctly Read, value is " + parsedEmployee.getM_nombreEmployee());
                    }
                    if (parsedEmployee.setM_apellidoEmployee(strings[1])) {
                        System.out.println("LName Correctly Read, value is" + parsedEmployee.getM_apellidoEmployee());
                    }
                    if (parsedEmployee.setM_codigoEmployee(Integer.parseInt(strings[2]))) {
                        System.out.println("ID Correctly Read, value is " + parsedEmployee.getM_codigoEmployee());
                    }
                    if (parsedEmployee.setM_fechaContratacionEmployee(Long.parseLong(strings[3]))) {
                        System.out.println("Date Correctly Read, value is " + parsedEmployee.getM_fechaContratacionEmployee());
                    }
                    if (parsedEmployee.setM_sueldoEmployee(Float.parseFloat(strings[4]))) {
                        System.out.println("Salary Correctly Read, value is " + parsedEmployee.getM_sueldoEmployee());
                    }
                    //? Now comes the interesting part, you see when we reach the element five in this case we have to parse
                    //? an internal string containing the keys of the array,for this we can do

                    String[] internalMapKeyValuePairs = strings[5].substring(1, strings[5].length() - 1).split("=");
                    System.out.println(Arrays.stream(internalMapKeyValuePairs).toList().toString());
                    if (internalMapKeyValuePairs.length != 0) {
                        for (int i = 0; i < internalMapKeyValuePairs.length - 1; i++) {
                            if (parsedEmployee.setM_MapEntry(Map.entry(internalMapKeyValuePairs[i], BigDecimal.valueOf(Double.parseDouble(internalMapKeyValuePairs[i + 1]))))) {
                                System.out.println("Replaced previous value bound to key");
                            } else {
                                System.out.println("Addition of New Key");
                            }

                        }
                    }
                    System.out.println(parsedEmployee.toString());
                    break;
                }
                case 8 /*Can be replaced by a constant*/: {
                    System.out.println("Manager Employee Identified");
                    Manager dummyManager = new Manager();
                    if (dummyManager.setM_nombreEmployee(strings[0])) {
                        System.out.println("Name Correctly Read, value is " + dummyManager.getM_nombreEmployee());
                    }
                    if (dummyManager.setM_apellidoEmployee(strings[1])) {
                        System.out.println("LName Correctly Read, value is" + dummyManager.getM_apellidoEmployee());
                    }
                    if (dummyManager.setM_codigoEmployee(Integer.parseInt(strings[2]))) {
                        System.out.println("ID Correctly Read, value is " + dummyManager.getM_codigoEmployee());
                    }
                    if (dummyManager.setM_fechaContratacionEmployee(Long.parseLong(strings[3]))) {
                        System.out.println("Date Correctly Read, value is " + dummyManager.getM_fechaContratacionEmployee());
                    }
                    if (dummyManager.setM_sueldoEmployee(Float.parseFloat(strings[4]))) {
                        System.out.println("Salary Correctly Read, value is " + dummyManager.getM_sueldoEmployee());
                    }

                    String[] internalMapKeyValuePairs = strings[5].substring(1, strings[5].length() - 1).split("=");
                    System.out.println(Arrays.stream(internalMapKeyValuePairs).toList().toString());
                    if (internalMapKeyValuePairs.length != 0) {
                        for (int i = 0; i < internalMapKeyValuePairs.length - 1; i++) {
                            if (dummyManager.setM_MapEntry(Map.entry(internalMapKeyValuePairs[i], BigDecimal.valueOf(Double.parseDouble(internalMapKeyValuePairs[i + 1]))))) {
                                System.out.println("Replaced previous value bound to key");
                            } else {
                                System.out.println("Addition of New Key");
                            }
                        }
                    }

                    if (dummyManager.setM_TituloNivelManager(strings[6])) {
                        System.out.println("Titulo Correctly Read, value is " + dummyManager.getM_TituloNivelManager());
                    }
                    if (dummyManager.setM_ComisionManager(Float.parseFloat(strings[7]))) {
                        System.out.println("Comision Correctly Read, value is " + dummyManager.getM_ComisionManager());
                    }

                    System.out.println(dummyManager.toCSVString());
                    break;
                }


            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        try {
            throw new FileNotFoundAlert();

        } catch (FileNotFoundAlert e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.out.println(e.getM_errorContent());
            System.out.println(e.getM_ErrorTitle());
            System.out.println(e.getM_ErrorHeader());
        }
    }
}
