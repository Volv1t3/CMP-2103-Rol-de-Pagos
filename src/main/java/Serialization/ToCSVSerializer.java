package Serialization;

/*======================================================================================================================
 ?                                                     ABOUT
 * @author         :  Carlos Villafuerte, Santiago Arellano
 * @repo           :  CMP2103 - Rol De Pagos
 * @description    :  Implementacion de Clase Auxiliar de Serializacion Por JSON
======================================================================================================================*/

import CustomExceptions.FileIsEmptyAlert;
import CustomExceptions.FileNotFoundAlert;
import CustomExceptions.IncorrectCSVFormat;
import EmployeeAbstraction.*;

import javax.naming.directory.InvalidAttributeValueException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.*;

public class ToCSVSerializer {

    /**
     * Constante Estatica relacionada con la longitud generica de una string CSV recortada de un Employee Basico.
     * Su valor es de seis, dados los campos que contiene. <br>
     * <ul>
     *     <li>Nombre Empleado</li> <li>Apellido Empleado</li> <li>ID Empleado</li> <li>Fecha Contratacion Empleado</li>
     *     <li>Salario Empleado</li> <li>Desglose Salarial Empleado</li>
     * </ul>
     */
    @SuppressWarnings("unused")
    public static final int AMOUNT_PARSED_FIELDS_BASE_EMPLOYEE = (6);
    /**
     * Constante Estatica relacionada con la longitud generica de una string CSV recortada de un Manager Derivado de
     * Employee. Su valor es de ocho, dados los campos que contiene
     * <ul>
     *     <li>Nombre Empleado</li> <li>Apellido Empleado</li> <li>ID Empleado</li> <li>Fecha Contratacion Empleado</li>
     *     <li>Salario Empleado</li> <li>Desglose Salarial Empleado</li> <li>Titulo Nivel Empleado</li>
     *     <li>Comision Empleado </li>
     * </ul>
     */
    @SuppressWarnings("unused")
    public static final int AMOUNT_PARSED_FIELDS_MANAGER_EMPLOYEE = 8;


    //! Implementacion de metodos base, serializeToFile, deserailizeFromFile

    /**
     * Metodo para serializar la lista de empleados a un archivo CSV.
     * Este metodo toma como entrada un objeto File y un objeto EmployeeListWrapper.
     *
     * @param e_OutputFile      Archivo donde se escribiran los datos serializados.
     *                          Este archivo debe existir, de lo contrario se lanzara una FileNotFoundAlert.
     * @param e_EmployeeList    Lista de empleados que se desean serializar.
     *                          Si la lista de empleados esta vacia, se lanzara una InvalidAttributeValueException.
     * <br><br>
     * Esta funcion recorre la lista de empleados y para cada empleado, utiliza el metodo 'toCSVString' del objeto empleado
     * para obtener una representacion de cadena del empleado en formato CSV.
     * Cada cadena de representacion de los empleados se escribe en el archivo de salida.
     * <br><br>
     * Internamente, esta funcion crea un PrintWriter en el archivo de salida y escribe en el todas las representaciones CSV
     * obtenidas utilizando un forEach para recorrer todos los objetos empleados en la lista de empleados.
     *
     * @return Devuelve 'true' si la escritura en el archivo se realizo correctamente.
     *
     * @throws FileNotFoundAlert        Se lanza si el archivo proporcionado no existe.
     * @throws InvalidAttributeValueException  Se lanza si la lista de empleados proporcionada esta vacia.
     * @throws FileNotFoundException            Se lanza si no se puede abrir el archivo para escritura.
     */
    public static boolean serializeToFile(File e_OutputFile, EmployeeListWrapper e_EmployeeList) throws


            FileNotFoundAlert, InvalidAttributeValueException, FileNotFoundException {
        if ((e_OutputFile.exists()))
        {
            if (!(e_EmployeeList.getM_employees().isEmpty()))
            {
                try (PrintWriter writerObject = new PrintWriter(e_OutputFile);) {
                    e_EmployeeList.getM_employees().forEach(employee ->
                    {
                        writerObject.println(employee.toCSVString());
                    });
                }
                return true;
            }
            else {throw Employee.InvalidAttributeGenerator("serializeToFile", "e_EmployeeList was null");}
        }
        else {throw new FileNotFoundAlert("Error Code 0x0001 - [Raised] The File Sent into method (serializeToFile) does not exist or is empty.");
        }
    }

    /**
     * Metodo para deserializar un archivo CSV a una lista de empleados.
     * Este metodo recibe como entrada un objeto de tipo File.
     * <br><br>
     * @param e_InputFile Archivo de donde se obtendran los datos deserializados.
     *                 Este archivo debe existir y no debe estar vacio, de lo contrario se lanzaran excepciones.
     *                 La estructura del archivo debe seguir el formato especificado del CSV para que los datos se puedan deserializar correctamente.
     * <br><br>
     * Internamente, este metodo verifica si el archivo existe y si no esta vacio. Si alguna de estas condiciones no se cumple,
     * se lanza una excepcion de tipo FileNotFoundAlert o FileIsEmptyAlert, respectivamente.
     * Luego, se crea una instancia de EmployeeListWrapper y se llena con los empleados deserializados del archivo. Esto se hace
     * mediante un Scanner que recorre cada linea del archivo, deserializa el empleado llamando al metodo parseLine() y luego
     * agrega el empleado a la lista. Finalmente, la lista llena de empleados se establece en la instancia de EmployeeListWrapper y
     * esta se retorna.
     * <br><br>
     *
     * @return devuelve un EmployeeListWrapper que contiene los empleados deserializados del archivo.
     *
     * @throws FileNotFoundAlert        se lanza si el archivo proporcionado no existe.
     * @throws FileIsEmptyAlert         se lanza si el archivo proporcionado esta vacio.
     * @throws FileNotFoundException    se lanza si no se puede abrir el archivo para lectura.
     * @throws InvalidAttributeValueException se lanza si los datos del archivo no se pueden deserializar a un empleado.
     * @throws IncorrectCSVFormat se lanza si el formato interno de las lineas del archivo no siguen el formato del Programa.
     */
    public static EmployeeListWrapper deserializeFromFile(File e_InputFile) throws
            FileNotFoundAlert,
            FileIsEmptyAlert,
            FileNotFoundException,
            InvalidAttributeValueException,
            IncorrectCSVFormat {
        if ((e_InputFile.exists()))
        {
            if(!(Long.valueOf(e_InputFile.length()).equals(Long.valueOf(0))))
            {
                EmployeeListWrapper dummyWrapper = new EmployeeListWrapper();
                List<Employee> dummyInternalList = new ArrayList<>();
                Scanner FileScanner = new Scanner(e_InputFile);

                while (FileScanner.hasNext())
                {
                    try {
                        Employee deserializedEmployee = ToCSVSerializer.parseLine(FileScanner.next());
                        dummyInternalList.add(deserializedEmployee);
                    }
                    catch (IllegalStateException illegalStateException)
                    {
                        throw new IncorrectCSVFormat();
                    }
                }
                dummyWrapper.setM_employees(dummyInternalList);

                return dummyWrapper;
            }
            else
            {
                FileIsEmptyAlert alert = new FileIsEmptyAlert("Error Code 0x0001 - [Raised] The File sent into method (deserializeFromFile) " +
                        " does not contain any data to deserialize");
                StackTraceElement[] elements = new StackTraceElement[1];
                StackTraceElement element = new StackTraceElement("ToCSVSerializer", "deserializeFromFile",e_InputFile.getName(), 69);
                alert.setStackTrace(elements);
                throw alert;
            }
        }
        else
        {throw new FileNotFoundAlert("Error Code 0x0001 - [Raised] " +
                "The File sent into method (serializeToFile) does not exist or is empty.");}

    }

    /**
     * Este metodo se usa para parsear una linea de un archivo CSV y convertirla en un objeto Employee.
     *
     * @param e_LineToParse es la linea del archivo CSV que se va a analizar.
     * <br><br>
     * La funcion inicia dividiendo la linea de entrada en un array de cadenas utilizando el metodo split y una coma como delimitador.
     * A continuacion, se realiza un switch sobre la longitud del array de cadenas y se controlan dos casos: <br>
     * 1- AMOUNT_PARSED_FIELDS_BASE_EMPLOYEE: en este caso, se crea un nuevo objeto Employee y se asignan los valores correspondientes
     * a los atributos del objeto Employee usando los valores del array de cadenas. <br>
     * 2- AMOUNT_PARSED_FIELDS_MANAGER_EMPLOYEE: en este caso, se crea un nuevo objeto Manager (que es una clase derivada de Employee)
     * y se asignan los valores correspondientes a los atributos del objeto Manager usando los valores del array de cadenas. <br>
     * Despues de cada asignación, se retorna el objeto Employee o Manager. <br>
     * En caso de que la longitud del array de cadenas no coincida con ninguno de los casos anteriores, se lanza una excepción de tipo IllegalStateException. <br>
     *<br>
     * @return un objeto Employee o un objeto Manager dependiendo del tipo de línea parseada.
     *
     * @throws InvalidAttributeValueException cuando no es posible asignar un valor a un atributo de uno de los objetos
     * Employee o Manager debido a un tipo de dato invalido en la línea de entrada.
     *
     * @throws IllegalStateException cuando el número de campos en la línea de entrada no corresponde con ninguno de los tipos esperados
     * de empleados (Empleado Básico o Manager).
     */
    protected static Employee parseLine(String e_LineToParse) throws
            InvalidAttributeValueException, IllegalStateException {
            String[] strings = e_LineToParse.split("[,]");
            switch (strings.length) //? Splitting by the amount of items present in the array of values
            {
                case AMOUNT_PARSED_FIELDS_BASE_EMPLOYEE /*! Casoe Empleado Particular*/:
                {
                    // Create a new instance to work with
                    Employee parsedEmployee = new Employee();
                    parsedEmployee.setM_nombreEmployee(strings[0]);
                    parsedEmployee.setM_apellidoEmployee(strings[1]);
                    parsedEmployee.setM_codigoEmployee(Integer.parseInt(strings[2]));
                    parsedEmployee.setM_fechaContratacionEmployee(Long.parseLong(strings[3]));
                    parsedEmployee.setM_sueldoEmployee(Float.parseFloat(strings[4]));

                    String[] internalMapKeyValuePairs = strings[5].substring(1, strings[5].length() - 1).split("=");
                    System.out.println(Arrays.stream(internalMapKeyValuePairs).toList().toString());
                    if (internalMapKeyValuePairs.length != 0) {
                        for (int i = 0; i < internalMapKeyValuePairs.length - 1; i++) {
                            parsedEmployee.setM_MapEntry(
                                    Map.entry(internalMapKeyValuePairs[i],
                                            BigDecimal.valueOf(Double.parseDouble(internalMapKeyValuePairs[i + 1]))));
                        }
                    }
                    return parsedEmployee;
                }
                case AMOUNT_PARSED_FIELDS_MANAGER_EMPLOYEE /*! Caso de Managers*/: {
                    Manager dummyManager = new Manager();
                    dummyManager.setM_nombreEmployee(strings[0]);
                    dummyManager.setM_apellidoEmployee(strings[1]);
                    dummyManager.setM_codigoEmployee(Integer.parseInt(strings[2]));
                    dummyManager.setM_fechaContratacionEmployee(Long.parseLong(strings[3]));
                    dummyManager.setM_sueldoEmployee(Float.parseFloat(strings[4]));

                    String[] internalMapKeyValuePairs = strings[5].substring(1, strings[5].length() - 1).split("=");
                    System.out.println(Arrays.stream(internalMapKeyValuePairs).toList().toString());
                    if (internalMapKeyValuePairs.length != 0) {
                        for (int i = 0; i < internalMapKeyValuePairs.length - 1; i++) {
                            dummyManager.setM_MapEntry(Map.entry(
                                    internalMapKeyValuePairs[i],
                                    BigDecimal.valueOf(Double.parseDouble(internalMapKeyValuePairs[i + 1]))));
                        }
                    }
                    dummyManager.setM_TituloNivelManager(strings[6]);
                    dummyManager.setM_ComisionManager(Float.parseFloat(strings[7]));
                    return dummyManager;
                }
                default:
                    throw new IllegalStateException("Array Leido Incorrecto, Revisar Formato");
            }
    }
}
