package Serialization;

/*=====================================================================================================================
 ?                                                     ABOUT
 * @author         :  Carlos Villafuerte, Santiago Arellano
 * @repo           :  CMP2103 - Rol De Pagos
 * @description    :  Implementacion de Clase Auxiliar de Serializacion Por XML
 *===================================================================================================================*/


import CustomExceptions.FileIsEmptyAlert;
import CustomExceptions.FileNotFoundAlert;
import EmployeeAbstraction.Employee;
import EmployeeAbstraction.EmployeeListWrapper;

import javax.xml.bind.*;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.IllegalFormatConversionException;

public class ToXMLSerializer {


    /**
     * <p>Metodo para serializar los datos de los empleados a un archivo XML.</p>
     *
     * <p> El metodo recibe un archivo y una lista de empleados empacada en EmployeeListWrapper
     * como entrada y procede a serializar estos datos en el archivo proporcionado.</p>
     *
     * <p>Primero se verifica que el archivo ingresado exista, luego se verifica que tenga contenido,
     * estas comprobaciones sirven para prevenir casos en donde se podria intentar escrbir en un archivo inexistente o vacio.</p>
     *
     * <p>Posteriormente se verifica que la lista de empleados a serializar no este vacía, en caso de tener al menos un empleado,
     * los datos de los empleados se escriben en el archivo utilizando la funcion 'marshal' del objeto Marshaller.</p>
     *
     * <p> Las excepciones que se pueden lanzar son las siguientes:<br>
     * - FileNotFoundException: Esta excepcion se lanza cuando el archivo proporcionado para la serializacion no se encuentra.<br>
     * - FileIsEmptyAlert: Esta excepcion se lanza cuando el archivo proporcionado se encuentra vacio.<br>
     * - FileNotFoundAlert: Esta excepcion se lanza cuando el archivo proporcionado no se pudo abrir para la serializacion.<br>
     * </p>
     *
     * @param e_OutputFile El archivo en el que se serializarán los datos de los empleados.
     * @param e_EmployeeList La envoltura de la lista de empleados a serializar.
     * @return Verdadero si la serializacion es exitosa, falso de otra manera.
     * @throws FileNotFoundAlert si el archivo suministrado no se puede abrir para la serializacion.
     * @throws IOException si ocurre algún error de IO durante la serializacion.
     * @throws FileIsEmptyAlert si el archivo suministrado para la serializacion se encuentra vacío.
     * @throws IllegalArgumentException si durante la serializacion existio algun error de JAXB
     */
    @SuppressWarnings("unused")
    public static boolean serializeToFile(File e_OutputFile, EmployeeListWrapper e_EmployeeList)

            throws FileNotFoundAlert, IllegalArgumentException, FileIsEmptyAlert {
        try{
            //? Paso Base 1: Generamos Dos clases, JAXBContext y Marshaller para enviar los datos hacia el archivo
            JAXBContext context = JAXBContext.newInstance(EmployeeListWrapper.class);
            Marshaller marsh = context.createMarshaller();

            if (e_OutputFile.exists()){
                if (e_OutputFile.length() != 0)
                {
                    if (e_EmployeeList != null) {
                        if (!(e_EmployeeList.getM_employees().isEmpty())) {
                            marsh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                            marsh.marshal(e_EmployeeList, e_OutputFile);
                            return true;
                        } else {
                            throw new IllegalStateException("El arreglo de empleados a serializar esta vacio.\n" +
                                    "Favor revisar su estructura de datos e ingresar un arreglo no vacio");
                        }
                    } else {
                        throw new IllegalStateException("El valor Ingresado Para el Parametro de empleados a serializar es nulo.\n" +
                                "Favor revisar su estructura de datos e ingresar un arreglo no nulo");
                    }
                }
                else {throw new IllegalStateException();}
            }
            else {throw new FileNotFoundException();}
        }
        catch(FileNotFoundException e)
        {
            throw new FileNotFoundAlert("El archivo Ingresado como parametro para serializacion no pudo ser abierto, no fue encontrado.\n" +
                    "Favor asegurese de ingresar un archivo correcto en el selector de archivos (Error Original A continuacion):"+ e.getMessage());}
        catch(IllegalStateException e)
        {
            throw new FileIsEmptyAlert("El archivo Ingresado como parametro para serializacion esta vacio.\n" +
                    "Favor asegurese de ingresar un archivo no vacio (Error Original A continuacion): " + e.getMessage());
        }catch (JAXBException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Metodo para deserializar los datos de los empleados a partir de un archivo XML.
     * <p> Este metodo recibe un archivo como entrada y procede a deserializar sus datos en una lista de empleados empacada en EmployeeListWrapper.</p>
     *
     * <p>Primero se verifica que el archivo ingresado exista y luego se verifica que tenga contenido.
     * Estas comprobaciones sirven para prevenir casos en los que se podria intentar leer a partir de un archivo inexistente o vacio.</p>
     *
     * <p> Posteriormente, los datos del archivo se leen y deserializan con JAXB, creando una nueva lista de empleados.
     * Esta lista de empleados se devuelve para su posterior uso.</p>
     *
     * <p>Las excepciones que se pueden lanzar son las siguientes:<br>
     * - IllegalStateException: Esta excepcion se lanza cuando el archivo proporcionado para la deserializacion es inexistente o vacio.<br>
     * - FileNotFoundAlert: Esta excepcion se lanza cuando el archivo proporcionado no se puede abrir para la deserializacion. Por ejemplo, si el archivo no se encuentra en la ubicacion especificada.<br>
     * - RuntimeException: Esta excepcion se lanza cuando ocurre un error inesperado durante la deserializacion.<br></p>
     *
     * @param e_InputFile El archivo del que se deserializaran los datos de los empleados.
     * @return EmployeeListWrapper La lista de empleados deserializada del archivo.
     * @throws FileNotFoundAlert si el archivo proporcionado no se puede abrir para la deserializacion.
     * @throws IllegalArgumentException si durante la deserializacion ocurrio algun error de JAXB
     */
    @SuppressWarnings("unused")
    public static EmployeeListWrapper deserializeFromFile(File e_InputFile) throws FileNotFoundAlert, IllegalArgumentException {

        try
        {
            //? Paso Base 1: Creamos dos clases JAXBCotext y Unmarshaller para sacar los datos del programa
            JAXBContext context = JAXBContext.newInstance(EmployeeListWrapper.class);
            javax.xml.bind.Unmarshaller unmarshaller = context.createUnmarshaller();

            //! Analizamos si el archivo existe
            if (e_InputFile.exists())
            {
                //! Analizamos si el archivo tiene datos
                if (e_InputFile.length() != 0)
                {
                    //! Analizamos con JAXB
                    EmployeeListWrapper results = (EmployeeListWrapper) unmarshaller.unmarshal(e_InputFile);
                    return results;
                }
                else {throw new IllegalStateException();}
            }
            else {throw new IllegalStateException();}
        }
        catch (IllegalStateException e)
        {
            throw new FileNotFoundAlert("El archivo Ingresado como parametro para serializacion no pudo ser abierto, no fue encontrado.\n" +
                    "Favor asegurese de ingresar un archivo correcto en el selector de archivos (Error Original A continuacion):"+ e.getMessage());
        }
        catch(JAXBException exception)
        {
            throw new IllegalArgumentException(exception.getMessage());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
