package Serialization;

/*======================================================================================================================
 ?                                                     ABOUT
 * @author         :  Santiago Arellano
 * @repo           :  CMP2103 - Rol De Pagos
 * @description    :  Implementacion de Clase Auxiliar de Serializacion Binaria
 *====================================================================================================================*/

import CustomExceptions.FileIsEmptyAlert;
import CustomExceptions.FileNotFoundAlert;
import EmployeeAbstraction.Employee;
import EmployeeAbstraction.EmployeeListWrapper;
import java.io.*;
import java.util.ArrayList;

public class ToBinarySerializer {


    /**
     * Este metodo se utiliza para serializar a binario un lista de empleados.
     *
     * <p> Lo realiza creando una estructura de FileOutputStream y ObjectOutputStream, luego
     * verifica si la lista de empleados no es nula y si no esta vacia para escribir cada empleado en el archivo binario.
     * En otras palabras, transforma los objetos de empleado en una secuencia de bytes que se almacena en el archivo. </p>
     *
     * <p> Si el arreglo de empleados a serializar esta vacio, lanza una IllegalStateException.
     * Esto significa que se esta intentando serializar un arreglo vacio, lo que indica que algun punto anterior del codigo
     * no trato correctamente los datos.</p>
     *
     * <p> Si el valor ingresado para el parametro de empleados a serializar es nulo, lanza una IllegalStateException.
     * Esto significa que se esta intentando serializar una referencia nula, lo que puede llevar a problemas
     * irreparables en la estructura de datos del programa.</p>
     *
     * <p> Si el archivo ingresado como parametro para serializacion no pudo ser abierto o no fue encontrado,
     * lanza una FileNotFoundAlert, lo que indica que se debe verificar la ruta y la existencia del archivo
     * que se esta intentando abrir</p>
     *
     * <p> Si ocurre un error de entrada/salida durante la serializacion, el metodo lanza una IOException.
     * Esto se ve comúnmente cuando se tiene un problema irreparable con el archivo o se encuentra
     * corrupto el archivo que se esta intentando abrir. </p>
     *
     * @param e_OutputFile el archivo en el que se escribiran los empleados serializados
     * @param e_EmployeesToSerialize la lista de empleados a serializar
     * @return verdadero si la serializacion se realiza con exito, false de lo contrario
     * @throws FileNotFoundAlert si el archivo proporcionado no se encuentra
     * @throws IOException si se produce un error de entrada/salida durante la serializacion
     * @throws IllegalStateException si el valor ingresado de los empleados es nulo o si la lista de empleados está vacia
     */
    @SuppressWarnings("unused")
    public static boolean serializeToBinary(File e_OutputFile, EmployeeListWrapper e_EmployeesToSerialize)
            throws FileNotFoundAlert, IOException, IllegalStateException
    {
        try{
            if (!e_OutputFile.exists()) {e_OutputFile.createNewFile();}
        }
        catch (IOException e) {throw new RuntimeException(e);}
        //? Paso Base: Creamos dos estructuras, fileOutputStream y Object Output Stream
        try (FileOutputStream fos = new FileOutputStream(e_OutputFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos)){

            if (e_EmployeesToSerialize != null)
            {
                if (!(e_EmployeesToSerialize.getM_employees().isEmpty()))
                {
                    for(Employee empleado: e_EmployeesToSerialize.getM_employees())
                    {
                        oos.writeObject(empleado);
                    }
                    return true;
                }
                else
                {
                    throw new IllegalStateException("El arreglo de empleados a serializar esta vacio.\n" +
                            "Favor revisar su estructura de datos e ingresar un arreglo no vacio");
                }
            }
            else
            {
                throw new IllegalStateException("El valor Ingresado Para el Parametro de empleados a serializar es nulo.\n" +
                        "Favor revisar su estructura de datos e ingresar un arreglo no nulo");
            }
        }
        catch(FileNotFoundException e)
        {
            throw new FileNotFoundAlert("El archivo Ingresado como parametro para serializacion no pudo ser abierto, no fue encontrado.\n" +
                    "Favor asegurese de ingresar un archivo correcto en el selector de archivos");
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * Este metodo se utiliza para deserializar un archivo binario a una lista de empleados.
     *
     * <p>
     * Dado un archivo como entrada, este metodo usara FileInputStream y ObjectInputStream para
     * deserializar la estructura del archivo. Si el archivo existe y no esta vacio,
     * se leera el archivo utilizando el metodo readObject de la clase ObjectInputStream.
     * </p>
     *
     * <p>
     * La estructura del archivo se deserializara a un objeto de tipo EmployeeListWrapper,
     * el cual se devolvera como resultado del metodo.
     * </p>
     *
     * <p>
     * En caso de varias excepciones durante la deserializacion del archivo, la llamada
     * a este metodo resultara en las siguientes excepciones:
     * </p>
     *
     * <ul>
     *     <li>FileNotFoundAlert: Esta excepcion se lanza si el archivo proporcionado no existe.</li>
     *     <li>FileIsEmptyAlert: Esta excepcion se lanza si el archivo proporcionado esta vacio.</li>
     *     <li>IOException: Esta excepcion se lanza si se produce cualquier error de entrada / salida
     *     durante la deserializacion.</li>
     *     <li>RuntimeException: Esta excepcion se lanza si se produce cualquier otra excepcion durante
     *     la deserializacion.</li>
     * </ul>
     *
     * @param e_InputFile El archivo que se deserializara a la lista de empleados
     * @return EmployeeListWrapper Contiene la lista de empleados deserializada del archivo
     * @throws FileNotFoundAlert Si el archivo proporcionado no se encuentra
     * @throws FileIsEmptyAlert Si el archivo proporcionado esta vacio
     * @throws IOException Si se produce un error de entrada / salida durante la deserializacion
     */
    @SuppressWarnings("unused")
    public static EmployeeListWrapper deserializeFromBinary(File e_InputFile)

            throws FileNotFoundAlert, IOException, ClassNotFoundException, FileIsEmptyAlert {
        //? Paso Base 1: Creamos dos clases, FileInputStream y ObjectInputStream para deserializar la estructura del archivo
        try (FileInputStream fis = new FileInputStream(e_InputFile);
             ObjectInputStream ois = new ObjectInputStream(fis)){
            if (e_InputFile.exists())
            {
                if (e_InputFile.length() == 0)
                {
                    throw new FileIsEmptyAlert("El archivo Ingresado como parametro para deserializacion esta vacio.\n" +
                            "Favor asegurese de ingresar un archivo no vacio");
                }
                else
                {
                    EmployeeListWrapper  results = new EmployeeListWrapper(new ArrayList<>());
                    try {
                        while (true) {
                            results.getM_employees().add((Employee) ois.readObject());
                        }
                    }
                    catch(EOFException eofException)
                    {
                        //Ignored
                        System.Logger logger = System.getLogger("INFO");
                        logger.log(System.Logger.Level.DEBUG, "End Of Binary File Reached");
                    }
                    return results;
                }

            } else {
                throw new FileNotFoundAlert("El archivo Ingresado como parametro para deserializacion no pudo ser abierto, no fue encontrado.\n" +
                        "Favor asegurese de ingresar un archivo correcto en el selector de archivos");
            }
        }
        catch(FileNotFoundException fileNotFoundException)
        {
            throw new FileNotFoundAlert("El archivo Ingresado como parametro para deserializacion no pudo ser abierto, no fue encontrado.\n" +
                    "Favor asegurese de ingresar un archivo correcto en el selector de archivos");
        }
        catch(IOException ioException)
        {
            throw new IOException(ioException);
        }
        catch(ClassNotFoundException e)
        {
            throw new ClassNotFoundException();
        }

    }
}
