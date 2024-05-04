package CustomExceptions;

/*======================================================================================================================
 ?                                                     ABOUT
 * @author         :  Santiago Arellano
 * @repo           :  CMP2103 - Rol De Pagos
 * @description    :  Definicion e Implementacion de la Custom Exception FileNotFoundAlert, que permite enviar un mensaje
 *                    dentro del programa para informar de archivos que no pudieron ser abiertos en el sistema.
 ====================================================================================================================**/

public class FileNotFoundAlert extends Exception{

    private final String m_errorContent;


    //? Public Constructors

    public FileNotFoundAlert() {
        this.m_errorContent = "El archivo seleccionado, no pudo ser abierto. El archivo puede haberse eliminado, o encontrarse" +
                " corrompido.\nConsidere probar otro archivo, si el problema persiste, reportelo a Sistemas.";
    }
    public FileNotFoundAlert(String e_Message)
    {
        this.m_errorContent = e_Message;
    }

    //? Public Getters

    public String getM_ErrorTitle()  {//? Internal Variables Dedicated To Directing GUI
        return "The Processed File Has Not Been Opened Correctly";}

    public String getM_ErrorHeader() {
        return "File Not Found Error";}

    public String getM_errorContent(){return this.m_errorContent;}

}
