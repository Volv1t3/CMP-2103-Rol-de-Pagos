package CustomExceptions;

/*======================================================================================================================
 ?                                                     ABOUT
 * @author         :  Santiago Arellano
 * @repo           :  CMP2103 - Rol De Pagos
 * @description    :  Definicion e Implementacion de la Custom Exception IncorrectCSVFormat, que permite enviar un mensaje
 *                    dentro del programa para informar de archivos con Formatos CSV incorrectos.
 ====================================================================================================================**/

public class IncorrectCSVFormat extends Exception{

    private final String m_errorContent;


    //? Public Constructors

    public IncorrectCSVFormat() {
        this.m_errorContent = "El archivo seleccionado, fue abierto y leido. Pero el archivo tenia un formato de CSV incorrecto o" +
                " corrompido.\nConsidere probar otro archivo, si el problema persiste, reportelo a Sistemas.";
    }
    public IncorrectCSVFormat(String e_Message)
    {
        this.m_errorContent = e_Message;
    }

    //? Public Getters

    public String getM_ErrorTitle()  {//? Internal Variables Dedicated To Directing GUI
        return "CSV Format Error";}

    public String getM_ErrorHeader() {
        return "The CSV Format Parsed Was Incorrect For The Application";}

    public String getM_errorContent(){return this.m_errorContent;}

}
