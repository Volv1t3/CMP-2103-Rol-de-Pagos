package CustomExceptions;

public class IncorrectCSVFormat extends Exception{

    //? Internal Variables Dedicated To Directing GUI
    private final String m_ErrorTitle = "CSV Format Error";
    private final String m_ErrorHeader = "The CSV Format Parsed Was Incorrect For The Application";
    private String m_errorContent;


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

    public String getM_ErrorTitle()  {return this.m_ErrorTitle;}

    public String getM_ErrorHeader() {return this.m_ErrorHeader;}

    public String getM_errorContent(){return this.m_errorContent;}

}
