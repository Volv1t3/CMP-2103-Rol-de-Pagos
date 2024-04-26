package CustomExceptions;

public class FileIsEmptyAlert extends Exception{

    //? Internal Variables Dedicated To Directing GUI
    private final String m_ErrorTitle = "The Processed File Is Empty";
    private final String m_ErrorHeader = "File Is Empty Error";
    private String m_errorContent;


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

    public String getM_ErrorTitle()  {return this.m_ErrorTitle;}

    public String getM_ErrorHeader() {return this.m_ErrorHeader;}

    public String getM_errorContent(){return this.m_errorContent;}

}