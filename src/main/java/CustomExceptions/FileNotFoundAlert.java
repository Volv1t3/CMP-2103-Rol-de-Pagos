package CustomExceptions;

import java.io.FileNotFoundException;

public class FileNotFoundAlert extends Exception{

    //? Internal Variables Dedicated To Directing GUI
    private final String m_ErrorTitle = "The Processed File Has Not Been Opened Correctly";
    private final String m_ErrorHeader = "File Not Found Error";
    private String m_errorContent;


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

    public String getM_ErrorTitle()  {return this.m_ErrorTitle;}

    public String getM_ErrorHeader() {return this.m_ErrorHeader;}

    public String getM_errorContent(){return this.m_errorContent;}

}
