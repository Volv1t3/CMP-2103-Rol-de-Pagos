package Serialization;

/*======================================================================================================================
 ?                                                     ABOUT
 * @author         :  Carlos Villafuerte, Santiago Arellano
 * @repo           :  CMP2103 - Rol De Pagos
 * @description    :  Implementacion de Clase Auxiliar de Serializacion Por JSON
 *====================================================================================================================*/
import CustomExceptions.FileIsEmptyAlert;
import CustomExceptions.FileNotFoundAlert;
import EmployeeAbstraction.Employee;
import EmployeeAbstraction.EmployeeListWrapper;
import EmployeeAbstraction.Manager;
import org.json.simple.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


public class ToJsonSerializer {

    /**
     * <p> Este metodo se utiliza para serializar una lista de empleados a un archivo JSON. </p>
     * <p> Se espera recibir como parametro un objeto File que representa el archivo de salida y un objeto
     * EmployeeListWrapper que contiene la lista de empleados a serializar. </p>
     * <p> El metodo funciona de la siguiente manera: primero se verifica si el objeto mployeeListWrapper es nulo. <br>
     * Si es nulo, se lanza una IllegalArgumentException. <br>
     * Si no es nulo, se verifica si la lista de empleados esta vacia. Si esta vacia, se lanza una IllegalArgumentException. <br>
     * Si la lista tiene empleados, se recorre y se parsea cada empleado a un objeto JSON y se agrega a un ArrayList.
     * Finalmente, se escribe el ArrayList al archivo y se retorna true. </p>
     * <p> Este metodo puede lanzar las siguientes excepciones: <br><br>
     * FileNotFoundException, si el archivo de salida no se puede encontrar, <br>
     * IllegalArgumentException si el objeto EmployeeListWrapper es nulo o la lista de empleados esta vacia. Estas excepciones deben manejarse externamente. </p>
     *
     * @param e_OutputFile Archivo de salida en el que se escribiran los datos serializados.
     * @param e_EmployeeList Lista de empleados que se van a serializar.
     * @return true si la operacion es exitosa, false en caso contrario.
     * @throws FileNotFoundAlert Si el archivo de salida no se puede encontrar.
     * @throws IllegalArgumentException Si el objeto EmployeeListWrapper es nulo o la lista de empleados esta vacia.
     */
    public static boolean serializeToFile(File e_OutputFile, EmployeeListWrapper e_EmployeeList)
            throws FileNotFoundAlert, IllegalArgumentException
    {
        try (PrintWriter printWriter = new PrintWriter(e_OutputFile))
        {
            if (e_EmployeeList != null)
            {
                if (!(e_EmployeeList.getM_employees().isEmpty()))
                {
                    //! Paso inductivo 1: Generamos el Arreglo General Que se usara para contener a las instancias secundarias
                    JsonArray mainJSONARRAYHolder = new JsonArray();
                    ArrayList<JsonObject> internalEmployeeJSONObjects = new ArrayList<>();
                    e_EmployeeList.getM_employees().forEach(employee ->
                    {
                        internalEmployeeJSONObjects.add(parseEmployee(employee.toCSVString()));
                    });
                    mainJSONARRAYHolder.addAll(internalEmployeeJSONObjects);
                    //?Serialize the Data with the Pretty Printer
                    String formattedOutput = Jsoner.prettyPrint(mainJSONARRAYHolder.toJson());
                    try (FileWriter fileWriter = new FileWriter(e_OutputFile))
                    {
                        fileWriter.write(formattedOutput);
                        fileWriter.flush();
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    }


                    return true;
                }
                else {throw new IllegalArgumentException("El arreglo de empleados a serializar esta vacio.\n" +
                        "Favor revisar su estructura de datos e ingresar un arreglo no vacio");}
            }
            else {throw new IllegalArgumentException("El arreglo de empleados a serializar es nulo.\n" +
                    "Favor revisar su estructura de datos e ingresar un arreglo no nulo");}
        }
        catch(FileNotFoundException exception)
        {
            throw new FileNotFoundAlert("El archivo enviado al parametro Output File no pudo ser encontrado por el sistema.\n" +
                    "Favor asegurese de la existencia del archivo e intentelo de nuevo. (Error General Para soporte): "+ exception.getMessage());
        }


    }

    /**
     * <p> Este metodo se utiliza para deserializar un archivo JSON regresando una lista de empleados. </p>
     * <p> El metodo toma como parametro un objeto File que se asume es un archivo JSON representando una lista
     * de empleados serializada. </p>
     * <p> Internamente el metodo funciona de la siguiente forma: </p>
     * <ul>
     *   <li> Primero, verifica si el archivo proporcionado en los parametros de entrada existe.
     *   De no ser así, arroja una FileNotFoundException. </li>
     *   <li> Luego, chequea si el archivo no esta vacio. En caso de estar vacio, genera una IllegalArgumentException. </li>
     *   <li> Despues, intenta deserializar el archivo JSON a un objeto JsonArray. </li>
     *   <li> Ahora itera por cada objeto en este JsonArray. Cada objeto es examinado para ver si puede ser
     *   una instancia de un Empleado o de un Manager, dependiendo de las claves en el objeto. </li>
     *   <li> Si el objeto no tiene ninguna de las claves esperadas, se lanza una IllegalStateException -
     *   esto implica que los datos en el archivo no tienen el formato correcto para esta aplicacion. </li>
     *   <li> Si todo es exitoso, se retorna un objeto EmployeeListWrapper conteniente todos los empleados extraidos. </li>
     * </ul>
     * <p> Este metodo puede arrojar las siguientes excepciones, las cuales deben ser manejadas de manera externa: </p>
     * <ul>
     *   <li> FileNotFoundException, esta excepcion ocurre si el archivo no se puede encontrar. </li>
     *   <li> IllegalArgumentException, esta excepcion se lanza si el archivo esta vacio. </li>
     *   <li> IllegalStateException, esta excepcion se lanza si durante la deserializacion ocurre un error inesperado
     *   o si los datos no tienen el formato correcto para esta aplicacion. </li>
     * </ul>
     * @param e_InputFile Archivo que se va a deserializar.
     * @return Un objeto EmployeeListWrapper que contiene una lista de los empleados deserealizados.
     * @throws FileNotFoundAlert Lanzada si el archivo no se pudo encontrar.
     * @throws FileIsEmptyAlert Lanzada si el archivo esta vacio y por ende no se puede deserializar.
     * @throws IllegalStateException Lanzada si ocurre un error durante la deserialización o si los datos
     * del archivo no tienen el formato correcto para esta aplicacion.
     */
    public static EmployeeListWrapper deserializeFromFile(File e_InputFile)
            throws FileNotFoundAlert, FileIsEmptyAlert, IllegalStateException{

        try{
            if (e_InputFile.exists()) {
                if (e_InputFile.length() != 0)
                {
                    //! Intentamos generar un JsonArray deserializado
                    JsonArray deserializedMainArrayHolder = (JsonArray) Jsoner.deserialize(new FileReader(e_InputFile));
                    EmployeeListWrapper results = new EmployeeListWrapper(new ArrayList<>());
                    /*
                    Dentro de este arreglo, se encuetran registrados los grupos de datos del usuario, distribuidos con keys de employee o Manager,
                    Lo mas facil podria ser iterar sobre el arreglo y agrupar aquellos que tienen una key de employee o manager

                     */
                    for (Iterator<Object> it = deserializedMainArrayHolder.iterator(); it.hasNext(); ) {
                        JsonObject object = (JsonObject) it.next();
                        JsonObject maybeEmployee = (JsonObject) object.get("Employee");
                        JsonObject maybeManager = (JsonObject) object.get("Manager");

                        if (maybeEmployee != null)
                        {
                            Employee abstractEmployee = new Employee();
                            abstractEmployee.setM_nombreEmployee((String) maybeEmployee.get("NombreEmpleado"));
                            abstractEmployee.setM_apellidoEmployee((String) maybeEmployee.get("ApellidoEmpleado"));
                            abstractEmployee.setM_codigoEmployee( Integer.parseInt((String) maybeEmployee.get("IDEmpleado")));
                            abstractEmployee.setM_fechaContratacionEmployee(Long.parseLong( (String)maybeEmployee.get("FechaContratoEmpleado")));
                            abstractEmployee.setM_sueldoEmployee(Float.parseFloat( (String) maybeEmployee.get("SalarioEmpleado")));
                            //? Warning: analysis interno sobre el desglose salarial del empleado.
                            JsonObject internalSalaryHolder = (JsonObject) maybeEmployee.get("DesgloseSalarioEmpleado");
                            if (!(internalSalaryHolder.isEmpty()))
                            {
                                //TODO: Necesitamos las Keys con las que se van a guardar los valores de los sueldos, sin esos metodos no puedo avanzar aqui
                            }
                            results.getM_employees().add(abstractEmployee);
                        }
                        else if (maybeManager != null)
                        {
                            Manager abstractManager = new Manager();
                            abstractManager.setM_nombreEmployee((String) maybeManager.get("NombreEmpleado"));
                            abstractManager.setM_apellidoEmployee((String) maybeManager.get("ApellidoEmpleado"));
                            abstractManager.setM_codigoEmployee( Integer.parseInt((String) maybeManager.get("IDEmpleado")));
                            abstractManager.setM_fechaContratacionEmployee(Long.parseLong( (String)maybeManager.get("FechaContratoEmpleado")));
                            abstractManager.setM_sueldoEmployee(Float.parseFloat( (String) maybeManager.get("SalarioEmpleado")));
                            //? Warning: analysis interno sobre el desglose salarial del empleado.
                            JsonObject internalSalaryHolder = (JsonObject) maybeManager.get("DesgloseSalarioEmpleado");
                            if (!(internalSalaryHolder.isEmpty()))
                            {
                                //TODO: Necesitamos las Keys con las que se van a guardar los valores de los sueldos, sin esos metodos no puedo avanzar aqui
                            }
                            abstractManager.setM_TituloNivelManager((String) maybeManager.get("TituloNivelEmpleado"));
                            abstractManager.setM_ComisionManager(Float.parseFloat((String)maybeManager.get("ComisionEmpleado")));
                            results.getM_employees().add(abstractManager);
                        }
                        else {throw new IllegalStateException("EL valor del Campo JsonObject que se ha intentado" +
                                " deserializar no corresponde, ni con manager ni con employee classes.");}
                    }
                    return results;
                }
                else {throw new IllegalArgumentException();}
            }
            else {throw new FileNotFoundException();}
        }
        catch (FileNotFoundException e)
        {
            throw new FileNotFoundAlert("El archivo enviado al parametro Input File no pudo ser encontrado por el sistema.\n" +
                    "Favor asegurese de la existencia del archivo e intentelo de nuevo. (Error General Para soporte): "+ e.getMessage());
        }
        catch(IllegalArgumentException e)
        {
            throw new FileIsEmptyAlert("El archivo enviado al parametro Input File no contiene contenido.\n" +
                    "Favor asegurese de que el archivo no este vacio e intentelo de nuevo. (Error General Para soporte): "+ e.getMessage());
        } catch (DeserializationException e) {
            throw new IllegalStateException("Error Deserializando el archivo. (Error General Para soporte): "+ e.getMessage());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }


    }



    /**
     * <p>Este metodo toma una representacion en cadena de caracteres CSV de un empleado y la transforma
     * en un objeto JSON para su posterior serializacion.</p>
     * <p>
     * Internamente, el metodo parsea la cadena de caracteres CSV, examina cada campo y, en funcion
     * del numero de campos, determina si el empleado es un empleado base o un gerente, ya que ambos
     * contienen un numero de campos distinto (el gerente tiene campos adicionales).</p>
     * <p>
     * Para un empleado base, se espera que la cadena de caracteres CSV contenga los siguientes campos:
     *<br>
     * - NombreEmpleado: El nombre del empleado.<br>
     * - ApellidoEmpleado: El apellido del empleado.<br>
     * - IDEmpleado: El identificador unico del empleado.<br>
     * - FechaContratoEmpleado: La fecha en la que el empleado fue contratado.<br>
     * - SalarioEmpleado: El salario base del empleado.<br>
     * </p>
     * <p>
     * Adicionalmente, se aplica un proceso especial al ultimo campo de la cadena de caracteres CSV del
     * empleado (el salario desglosado), ya que este es en realidad una representacion de cadena de
     * caracteres de un mapa. Este mapa se parsea y se transforma en un objeto JSONObject antes de
     * almacenarlo en el objeto JSON del empleado.
     * </p>
     * <p>
     * Finalmente, todo esto se empaqueta dentro de un objeto JSONObject superior, bajo la clave "Employee".
     * </p><p>
     * En caso de que la cadena de caracteres CSV contenga un numero distinto de campos a los esperados,
     * el metodo lanzara una IllegalArgumentException.
     * </p>
     * @param e_CSVRepresentation La representacion en cadena de caracteres CSV del empleado.
     * @return Un objeto JSONObject representando al empleado.
     * @throws IllegalArgumentException Si la cadena de caracteres CSV no se puede parsear correctamente.
 */
    private static JsonObject parseEmployee(String e_CSVRepresentation)
    {
        //? Paso Base, reducimos la String representativa a un arreglo menor
        String[] strings = e_CSVRepresentation.split(",");
        JsonObject EmployeeWrapper = new JsonObject();
        //? Switch de acuerdo a la longitud para determinar tamano de arreglo
        switch(strings.length)
        {
            case ToCSVSerializer.AMOUNT_PARSED_FIELDS_BASE_EMPLOYEE :
            {
                //! Creamos un Arreglo Interno
                JsonObject employeeJSON = new JsonObject();
                employeeJSON.put("NombreEmpleado", strings[0]);
                employeeJSON.put("ApellidoEmpleado", strings[1]);
                employeeJSON.put("IDEmpleado", strings[2]);
                employeeJSON.put("FechaContratoEmpleado", strings[3]);
                employeeJSON.put("SalarioEmpleado", strings[4]);
                //? Tal como se hizo para deserializar un archivo en CSV, tenemos que pasar ese mismo arreglo a una serie
                //? de llaves y valores que se serializen dentro de otro Arreglo interno.
                JsonObject internalSalaryDesglose = new JsonObject();
                String[] internalMapKeyValuePairs = strings[5].substring(1, strings[5].length() - 1).split("=");
                System.out.println(Arrays.stream(internalMapKeyValuePairs).toList().toString());
                if (internalMapKeyValuePairs.length != 0) {
                    for (int i = 0; i < internalMapKeyValuePairs.length - 1; i++) {
                        internalSalaryDesglose.put(internalMapKeyValuePairs[i], internalMapKeyValuePairs[i+1]);
                    }
                }
                employeeJSON.put("DesgloseSalarioEmpleado", internalSalaryDesglose);
                //En este momento los campos se acabaron y retornamos el objeto
                EmployeeWrapper.put("Employee", employeeJSON);
                return EmployeeWrapper;
            }
            case ToCSVSerializer.AMOUNT_PARSED_FIELDS_MANAGER_EMPLOYEE:
            {
                //! Creamos un Arreglo Inteerno
                JsonObject managerJSON = new JsonObject();
                managerJSON.put("NombreEmpleado", strings[0]);
                managerJSON.put("ApellidoEmpleado", strings[1]);
                managerJSON.put("IDEmpleado", strings[2]);
                managerJSON.put("FechaContratoEmpleado", strings[3]);
                managerJSON.put("SalarioEmpleado", strings[4]);
                JsonObject internalSalaryDesglose = new JsonObject();
                String[] internalMapKeyValuePairs = strings[5].substring(1, strings[5].length() - 1).split("=");
                System.out.println(Arrays.stream(internalMapKeyValuePairs).toList().toString());
                if (internalMapKeyValuePairs.length != 0) {
                    for (int i = 0; i < internalMapKeyValuePairs.length - 1; i++) {
                        internalSalaryDesglose.put(internalMapKeyValuePairs[i], internalMapKeyValuePairs[i+1]);
                    }
                }
                managerJSON.put("DesgloseSalarioEmpleado", internalSalaryDesglose);
                managerJSON.put("TituloNivelEmpleado", strings[6]);
                managerJSON.put("ComisionEmpleado", strings[7]);
                EmployeeWrapper.put("Manager", managerJSON);
                return EmployeeWrapper;
            }
            default:
            {
                throw new IllegalStateException("El Objeto que fue enviado no es el correcto, el formato puede estar danado o corrompido.\n" +
                        "Favor revisar el arreglo original.");
            }
        }
    }
}


