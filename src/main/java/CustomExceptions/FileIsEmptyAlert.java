package CustomExceptions;

/*======================================================================================================================
 ?                                                     ABOUT
 * @author         :  Santiago Arellano
 * @repo           :  CMP2103 - Rol De Pagos
 * @description    :  Definicion e Implementacion de la Custom Exception FileIsEmptyAlert, que permite enviar un mensaje
 *                    dentro del programa para informar de archivos vacios.
 ====================================================================================================================**/

public class FileIsEmptyAlert extends Exception{

    private final String m_errorContent;


    //? Public Constructors

    public FileIsEmptyAlert() {
        this.m_errorContent = "El archivo seleccionado, fue abierto pero se encuentra vacio. Al no haber datos, el proeso de deserializacion" +
                " no puede continuar.\nConsidere probar otro archivo, si el problema persiste, reportelo a Sistemas.";
    }
    public FileIsEmptyAlert(String e_Message)
    {
        this.m_errorContent = e_Message;
    }

    //? Public Getters

    public String getM_ErrorTitle()  {//? Internal Variables Dedicated To Directing GUI
        return "The Processed File Is Empty";}

    public String getM_ErrorHeader() {
        return "File Is Empty Error";}

    public String getM_errorContent(){return this.m_errorContent;}

}