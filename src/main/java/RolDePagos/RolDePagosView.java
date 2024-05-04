package RolDePagos;

/*======================================================================================================================
 ?                                                     ABOUT
 * @author         :  Santiago Arellano
 * @repo           :  CMP2103 - Rol De Pagos
 * @description    :  Definicion e Implementacion del MainEntryPoint de la aplicacion, controlador visual del sistema.
 ====================================================================================================================**/

import CustomCellFactories.EmployeeCellFactory;
import CustomCellFactories.ManagerCellFactory;
import CustomExceptions.FileIsEmptyAlert;
import CustomExceptions.FileNotFoundAlert;
import CustomExceptions.IncorrectCSVFormat;
import EmployeeAbstraction.Employee;
import EmployeeAbstraction.EmployeeListWrapper;
import EmployeeAbstraction.Manager;
import SalaryCalculations.MoneyPresentationHelper;
import SalaryCalculations.MonthlySalaryHelper;
import Serialization.*;
import com.sun.xml.bind.v2.model.core.ID;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.naming.directory.InvalidAttributeValueException;
import javax.swing.plaf.multi.MultiInternalFrameUI;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class RolDePagosView extends Application {

    //! Private execution varibles
    private EmployeeListWrapper m_WrapperList;
    private List<Employee>  ManagerHolder;
    private List<Employee> EmployeeHolder;
    private TextField mColaboradorNameTextField;
    private TextField mColaboradorApellidoTextField;
    private TextField mColaboradorIDTextField;
    private DatePicker mColaboradorFechaContratoDatePicker;
    private TextField mColaboradorSalarioTextField;
    private TextField mManagerBonificacion;
    private ComboBox<String> mManagerTituloNivelComboBox;
    private ListView<Employee> mListViewColaboradores;
    private ListView<Employee> mListVIewColaboradoresManagers;
    private MenuItem mSortingAlfabeticoNombreMenuItem;
    private MenuItem mSortingAlfabeticoApellidosMenuItem;
    private MenuItem  smSortingFechaContratoMenuItem;
    private MenuItem mSortingSueldoMenuItem;
    private MenuItem mSortingSueldoYBonoMenuItem;
    private RadioButton mColaboradorRadioButton;
    private RadioButton mGerenteRadioButton;
    private ToggleGroup colaboradorSelector;
    private Button addEmployeeButton;
    private Button deleteEmployeeButton;
    private MenuBar sortingMenuBar;
    //! Alerta generalizada
    private Alert generalAlert = new Alert(Alert.AlertType.ERROR);
    private Alert fileNotFoundAlert = new Alert(Alert.AlertType.ERROR);
    //! Variables de Entorno para manejar input y output de datos
    private Button importarDatosBinario;
    private Button exportarDatosBinario;
    private Button importarDatosCSV;
    private Button exportarDatosCSV;
    private Button importarDatosXML;
    private Button exportarDatosXML;
    private Button importarDatosJSON;
    private Button exportarDatosJSON;
    private Button eliminarDatos;
    private Button exportarDatosTributarios;
    private RadioButton descargaInformacionTributariaRadioButton;
    private RadioButton descargaInformacionSueldosLiquidosMenoresRadioButton;
    private RadioButton descargaInformacionTributariaYSueldosRadioButton;
    private RadioButton descargaGerentesToggleButton;
    private RadioButton descargaGeneralToggleButton;
    private RadioButton descargaColaboradoresToggleButton;
    private MenuBar selectorMenuItemsEmpleados;
    private TableView<Employee> tableDesgloseSalario;
    private TableView<Employee> desgloseIESSEmpleados;

    @Override
    public void init() throws Exception {
        //? Vamos A Cargar Un Archivo CSV Directamente a la aplicacion para su manipulacion
        String employeeCSVFilePath = Paths.get(System.getProperty("user.dir"), "src","main","java", "RolDePagos", "empleadosPrueba.csv").toString();
        this.m_WrapperList = ToCSVSerializer.deserializeFromFile( new File(employeeCSVFilePath));
        this.ManagerHolder = new ArrayList<>();
        this.EmployeeHolder = new ArrayList<>();
        this.m_WrapperList.getM_employees().forEach(employee ->
        {
            if(employee.getClass().equals(Manager.class))
            {
                this.ManagerHolder.add(employee);
            }
            else {
                this.EmployeeHolder.add(employee);
            }
        });
    }

    public void start(Stage primaryStage) throws Exception {

        //? Configuracion Inicial Primary Stage
        primaryStage.setMinWidth(600); primaryStage.setWidth(1600); primaryStage.setMaxWidth(1700);
        primaryStage.setMinHeight(600); primaryStage.setHeight(800); primaryStage.setMaxHeight(800);
        ; primaryStage.setTitle("eVolvLabs - Rol de Pagos 1.0");

        //? Cargamos el Scene desde FXMl
        String FXMLsource = Paths.get(System.getProperty("user.dir"), "src","main","java", "RolDePagos", "RolDePagosView.fxml").toString();
        File rootcontent = new File(FXMLsource);
        Scene root = FXMLLoader.load((rootcontent.toURI().toURL()));
        primaryStage.setScene(root); primaryStage.show();

        this.tableDesgloseSalario = (TableView<Employee>) root.lookup("#tableDesgloseSalario");
        this.desgloseIESSEmpleados = (TableView<Employee>) root.lookup("#desgloseIESSEmpleados");
        updateIESSTable(this.m_WrapperList.getM_employees());
        this.selectorMenuItemsEmpleados = (MenuBar) root.lookup("#selectorMenuItemsEmpleados");
        for(Employee em : this.m_WrapperList.getM_employees())
        {
            MonthlySalaryHelper.addCalculatedEntries(em);
            updateTables(em);
        }
        //? Cargamos dependiendo del tipo de selector
        this.mColaboradorRadioButton = (RadioButton) root.lookup("#mColaboradorRadioButton");
        this.mGerenteRadioButton = (RadioButton) root.lookup("#mGerenteRadioButton");
        this.mListViewColaboradores = (ListView<Employee>) root.lookup("#mListViewColaboradores");
        this.mListVIewColaboradoresManagers =(ListView<Employee>) root.lookup("#mListViewColaboradores");
        //? Adaptamos la pantalla a nuestra necesidad: Mostrar empleados que tienen sulo los campos base
        this.mColaboradorRadioButton.selectedProperty().addListener(il ->
        {
            try {
                this.mListViewColaboradores.setItems(FXCollections.observableList(this.EmployeeHolder));
                //? Cargamos los datos al list view
                this.mListViewColaboradores.setCellFactory(new Callback<ListView<Employee>, ListCell<Employee>>() {
                    @Override
                    public ListCell<Employee> call(ListView<Employee> param) {
                        return new EmployeeCellFactory();
                    }
                });
            } catch(Exception ignored) {;}
        });

        //? Adaptamos la pantalla a nuestra necesidad: Mostrar empleados que tienen campos complejos


        this.mGerenteRadioButton.selectedProperty().addListener(InvalidationListener ->
        {
            try{
                this.mListVIewColaboradoresManagers.setItems(FXCollections.observableList(this.ManagerHolder));
                this.mListVIewColaboradoresManagers.setCellFactory(new Callback<ListView<Employee>, ListCell<Employee>>() {
                    @Override
                    public ListCell<Employee> call(ListView<Employee> param) {
                        return new ManagerCellFactory();
                    }
                });
            }
            catch(Exception ignored) {;}
        });


        //? Connecting method for adding employee right into the Button
        this.mColaboradorNameTextField = (TextField) root.lookup("#mColaboradorNameTextField");
        this.mColaboradorApellidoTextField = (TextField) root.lookup("#mColaboradorApellidoTextField");
        this.mColaboradorIDTextField = (TextField) root.lookup("#mColaboradorIDTextField");
        this.mColaboradorFechaContratoDatePicker = (DatePicker) root.lookup("#mColaboradorFechaContratoDatePicker");
        this.mColaboradorSalarioTextField = (TextField) root.lookup("#mColaboradorSalarioTextField");
        this.mManagerBonificacion = (TextField) root.lookup("#mManagerBonificacion");
        this.mManagerTituloNivelComboBox = (ComboBox<String>) root.lookup("#mManagerTituloNivelComboBox");
        this.deleteEmployeeButton = (Button) root.lookup("#deleteEmployeeButton");

        this.addEmployeeButton = (Button) root.lookup("#addEmployeeButton");
        this.addEmployeeButton.setOnMouseClicked(mouseClickedEvent ->
        {
            //? Paso Base Revisamos que tipo de Empleado esta seleccionado para revisar campos
                boolean error_Flag = false;
                if (this.mColaboradorRadioButton.isSelected() && this.mListViewColaboradores.getSelectionModel().isEmpty()) {
                    Employee dummyEmployee = new Employee();
                    //! Agarramos primero el nombre
                    if (!(this.mColaboradorNameTextField.getText().isEmpty()))
                    {
                        try {
                            dummyEmployee.setM_nombreEmployee(this.mColaboradorNameTextField.getText());
                        }
                        catch (InvalidAttributeValueException e) {
                            this.generalAlert.setTitle("Nombre Invalido");
                            this.generalAlert.setHeaderText("El nombre ingresado no es valido");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar solo un nombre por empleado.");
                            this.mColaboradorNameTextField.clear();
                            this.generalAlert.showAndWait();
                            error_Flag = true;
                        }
                        catch (NullPointerException e) {
                            this.generalAlert.setTitle("Campo Vacio");
                            this.generalAlert.setHeaderText("El campo nombre esta vacio");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un nombre por empleado.");
                            error_Flag = true;
                            this.generalAlert.showAndWait();
                        }
                    }
                    else {
                        this.generalAlert.setTitle("Campo Vacio");
                        this.generalAlert.setHeaderText("El campo nombre esta vacio");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un nombre por empleado.");
                        error_Flag = true;
                        this.generalAlert.showAndWait();
                    }
                    //! Agarramos el Apellido
                    if (!(this.mColaboradorApellidoTextField.getText().isEmpty())) {
                        try {
                            dummyEmployee.setM_apellidoEmployee(this.mColaboradorApellidoTextField.getText());
                        } catch (InvalidAttributeValueException e) {
                            this.generalAlert.setTitle("Apellido Invalido");
                            this.generalAlert.setHeaderText("El apellido ingresado no es valido");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar solo un apellido por empleado.");
                            this.mColaboradorApellidoTextField.clear();
                            this.generalAlert.showAndWait();
                            error_Flag = true;
                        }
                        catch (NullPointerException e)
                            {
                                this.generalAlert.setTitle("Campo Vacio");
                                this.generalAlert.setHeaderText("El campo apellido esta vacio");
                                this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un apellido por empleado.");
                                error_Flag = true;
                                this.generalAlert.showAndWait();
                            }
                    }
                    else {
                        this.generalAlert.setTitle("Campo Vacio");
                        this.generalAlert.setHeaderText("El campo apellido esta vacio");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un apellido por empleado.");
                        error_Flag = true;
                        this.generalAlert.showAndWait();
                    }
                    //! Agarramos el Codigo
                    if (!(this.mColaboradorIDTextField.getText().isEmpty())) {
                        //! Anaalizamos si el codigo es duplicado
                        try {
                            this.m_WrapperList.getM_employees().forEach(item ->
                            {
                                if (item.getM_codigoEmployee() == Integer.parseInt(this.mColaboradorIDTextField.getText())) {
                                    throw new IllegalStateException();
                                }
                            });
                            dummyEmployee.setM_codigoEmployee(Integer.parseInt(this.mColaboradorIDTextField.getText()));
                        } catch (IllegalStateException e) {
                            this.generalAlert.setTitle("Codigo Duplicado");
                            this.generalAlert.setHeaderText("El codigo ingresado ya existe en la base de datos");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un codigo unico por empleado.");
                            this.generalAlert.showAndWait();
                            this.mColaboradorIDTextField.clear();
                            error_Flag = true;
                        } catch (InvalidAttributeValueException e) {
                            this.generalAlert.setTitle("Codigo Invalido");
                            this.generalAlert.setHeaderText("El codigo ingresado no es valido");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar solo un codigo por empleado.");
                            this.generalAlert.showAndWait();
                            error_Flag = true;
                        }
                        catch (NullPointerException e)
                        {
                            this.generalAlert.setTitle("Campo Vacio");
                            this.generalAlert.setHeaderText("El campo codigo esta vacio");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un codigo por empleado.");
                            this.generalAlert.showAndWait();
                            error_Flag = true;
                        }

                    }
                    else {
                        this.generalAlert.setTitle("Campo Vacio");
                        this.generalAlert.setHeaderText("El campo codigo esta vacio");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un codigo por empleado.");
                        error_Flag = true;
                        this.generalAlert.showAndWait();
                    }
                    //! Revisamos Selector de Fechas
                    try {
                        dummyEmployee.setM_fechaContratacionEmployee(Date.valueOf(this.mColaboradorFechaContratoDatePicker.getValue()).getTime());

                    } catch (InvalidAttributeValueException e)
                    {
                        this.generalAlert.setTitle("Fecha Invalida");
                        this.generalAlert.setHeaderText("La fecha ingresada no es valida");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar una fecha valida por empleado.");
                        this.generalAlert.showAndWait();
                        error_Flag = true;
                    }
                    catch (NullPointerException e)
                    {
                        this.generalAlert.setTitle("Campo Vacio");
                        this.generalAlert.setHeaderText("El campo fecha esta vacio");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar una fecha por empleado.");
                        this.generalAlert.showAndWait();
                        error_Flag = true;
                    }
                    //! Revisamos el Salario
                    if (!(this.mColaboradorSalarioTextField.getText().isEmpty())) {
                        //! Convert to Float
                        Float readInSalary = Float.parseFloat(this.mColaboradorSalarioTextField.getText());
                        if (readInSalary < 800 || readInSalary > 3500) {
                            this.generalAlert.setTitle("Salario Invalido");
                            this.generalAlert.setHeaderText("El salario ingresado no es valido");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un salario entre 800 y 3500 por empleado.");
                            this.mColaboradorSalarioTextField.clear();
                            error_Flag = true;
                            this.generalAlert.showAndWait();
                        } else {
                            try {
                                dummyEmployee.setM_sueldoEmployee(readInSalary);
                            } catch (InvalidAttributeValueException e) {
                                this.generalAlert.setTitle("Salario Invalido");
                                this.generalAlert.setHeaderText("El salario ingresado no es valido");
                                this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un salario entre 800 y 3500 por empleado.");
                                this.mColaboradorSalarioTextField.clear();
                                error_Flag = true;
                                this.generalAlert.showAndWait();
                            }
                        }

                    }
                    if (!error_Flag) {
                        this.m_WrapperList.getM_employees().add(dummyEmployee); // General Holder
                        this.EmployeeHolder.add(dummyEmployee); // Specialized Holder
                        this.mListViewColaboradores.requestFocus();
                        this.mListViewColaboradores.refresh();
                        updateTables(dummyEmployee);
                        updateIESSTable(this.m_WrapperList.getM_employees());
                    }

                }
                else if (this.mGerenteRadioButton.isSelected() && this.mListVIewColaboradoresManagers.getSelectionModel().isEmpty()) {
                    error_Flag = false;
                    Manager dummyManager = new Manager();
                    //! Agarramos primero el nombre
                    if (!(this.mColaboradorNameTextField.getText().isEmpty())) {
                        try {
                            dummyManager.setM_nombreEmployee(this.mColaboradorNameTextField.getText());
                        } catch (InvalidAttributeValueException e) {
                            this.generalAlert.setTitle("Nombre Invalido");
                            this.generalAlert.setHeaderText("El nombre ingresado no es valido");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar solo un nombre por empleado.");
                            this.mColaboradorNameTextField.clear();
                            this.generalAlert.showAndWait();
                            error_Flag = true;
                        } catch (NullPointerException e) {
                            this.generalAlert.setTitle("Campo Vacio");
                            this.generalAlert.setHeaderText("El campo nombre esta vacio");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un nombre por empleado.");
                            error_Flag = true;
                            this.generalAlert.showAndWait();
                        }
                    }
                    else {
                        this.generalAlert.setTitle("Campo Vacio");
                        this.generalAlert.setHeaderText("El campo nombre esta vacio");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un nombre por empleado.");
                        error_Flag = true;
                        this.generalAlert.showAndWait();
                    }
                    //! Agarramos el Apellido
                    if (!(this.mColaboradorApellidoTextField.getText().isEmpty())) {
                        try {
                            dummyManager.setM_apellidoEmployee(this.mColaboradorApellidoTextField.getText());
                        } catch (InvalidAttributeValueException e) {
                            this.generalAlert.setTitle("Apellido Invalido");
                            this.generalAlert.setHeaderText("El apellido ingresado no es valido");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar solo un apellido por empleado.");
                            this.mColaboradorApellidoTextField.clear();
                            this.generalAlert.showAndWait();
                            error_Flag = true;
                        } catch (NullPointerException e) {
                            this.generalAlert.setTitle("Campo Vacio");
                            this.generalAlert.setHeaderText("El campo apellido esta vacio");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un apellido por empleado.");
                            error_Flag = true;
                            this.generalAlert.showAndWait();
                        }
                    }
                    else {
                        this.generalAlert.setTitle("Campo Vacio");
                        this.generalAlert.setHeaderText("El campo apellido esta vacio");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un apellido por empleado.");
                        error_Flag = true;
                        this.generalAlert.showAndWait();
                    }
                    //! Agarramos el Codigo
                    if (!(this.mColaboradorIDTextField.getText().isEmpty())) {
                        //! Anaalizamos si el codigo es duplicado
                        try {
                            this.m_WrapperList.getM_employees().forEach(item ->
                            {
                                if (item.getM_codigoEmployee() == Integer.parseInt(this.mColaboradorIDTextField.getText())) {
                                    throw new IllegalStateException();
                                }
                            });
                            dummyManager.setM_codigoEmployee(Integer.parseInt(this.mColaboradorIDTextField.getText()));
                        } catch (IllegalStateException e) {
                            this.generalAlert.setTitle("Codigo Duplicado");
                            this.generalAlert.setHeaderText("El codigo ingresado ya existe en la base de datos");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un codigo unico por empleado.");
                            this.generalAlert.showAndWait();
                            this.mColaboradorIDTextField.clear();
                            error_Flag = true;
                        } catch (InvalidAttributeValueException e) {
                            this.generalAlert.setTitle("Codigo Invalido");
                            this.generalAlert.setHeaderText("El codigo ingresado no es valido");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar solo un codigo por empleado.");
                            this.generalAlert.showAndWait();
                            error_Flag = true;
                        } catch (NullPointerException e) {
                            this.generalAlert.setTitle("Campo Vacio");
                            this.generalAlert.setHeaderText("El campo codigo esta vacio");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un codigo por empleado.");
                            this.generalAlert.showAndWait();
                            error_Flag = true;
                        }

                    }
                    else {
                        this.generalAlert.setTitle("Campo Vacio");
                        this.generalAlert.setHeaderText("El campo codigo esta vacio");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un codigo por empleado.");
                        error_Flag = true;
                        this.generalAlert.showAndWait();
                    }
                    //! Revisamos Selector de Fechas
                    try {
                        dummyManager.setM_fechaContratacionEmployee(Date.valueOf(this.mColaboradorFechaContratoDatePicker.getValue()).getTime());

                    } catch (InvalidAttributeValueException e)
                    {
                        this.generalAlert.setTitle("Fecha Invalida");
                        this.generalAlert.setHeaderText("La fecha ingresada no es valida");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar una fecha valida por empleado.");
                        this.generalAlert.showAndWait();
                        error_Flag = true;
                    } catch (NullPointerException e)
                    {
                        this.generalAlert.setTitle("Campo Vacio");
                        this.generalAlert.setHeaderText("El campo fecha esta vacio");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar una fecha por empleado.");
                        this.generalAlert.showAndWait();
                        error_Flag = true;
                    }
                    //! Agarramos EL salario
                    if (!(this.mColaboradorSalarioTextField.getText().isEmpty())) {
                        //! Convert to Float
                        Float readInSalary = Float.parseFloat(this.mColaboradorSalarioTextField.getText());
                        if (readInSalary < 800 || readInSalary > 3500) {
                            this.generalAlert.setTitle("Salario Invalido");
                            this.generalAlert.setHeaderText("El salario ingresado no es valido");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un salario entre 800 y 3500 por empleado.");
                            this.mColaboradorSalarioTextField.clear();
                            error_Flag = true;
                            this.generalAlert.showAndWait();
                        } else {
                            try {
                                dummyManager.setM_sueldoEmployee(readInSalary);
                            } catch (InvalidAttributeValueException e) {
                                this.generalAlert.setTitle("Salario Invalido");
                                this.generalAlert.setHeaderText("El salario ingresado no es valido");
                                this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un salario entre 800 y 3500 por empleado.");
                                this.mColaboradorSalarioTextField.clear();
                                error_Flag = true;
                                this.generalAlert.showAndWait();
                            }
                        }
                    }
                    else {
                        this.generalAlert.setTitle("Campo Vacio");
                        this.generalAlert.setHeaderText("El campo salario esta vacio");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un salario por empleado.");
                        error_Flag = true;
                        this.generalAlert.showAndWait();
                    }
                    //! Leemos El valor del Combo Box
                    try {
                        switch (this.mManagerTituloNivelComboBox.getSelectionModel().getSelectedItem()) {
                            case "Maestria": {
                                dummyManager.setM_TituloNivelManager(Manager.MAESTRIA_CONSTANT);
                                break;
                            }
                            case "Doctorado": {
                                dummyManager.setM_TituloNivelManager(Manager.DOCTORADO_CONSTANT);
                                break;
                            }
                            case "Titulo Tercer Nivel": {
                                dummyManager.setM_TituloNivelManager(Manager.TERCER_NIVEL_CONSTANT);
                                break;
                            }
                        }
                    } catch (InvalidAttributeValueException e)
                    {
                        this.generalAlert.setTitle("Titulo Invalido");
                        this.generalAlert.setHeaderText("El titulo ingresado no es valido");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un titulo valido por empleado.");
                        this.mManagerTituloNivelComboBox.getSelectionModel().clearSelection();
                        error_Flag = true;
                        this.generalAlert.showAndWait();
                    } catch (NullPointerException e)
                    {
                        this.generalAlert.setTitle("Campo Vacio");
                        this.generalAlert.setHeaderText("El campo titulo esta vacio");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un titulo por empleado.");
                        error_Flag = true;
                        this.generalAlert.showAndWait();
                    }
                    //! Recolectamos el Valor de Comision
                    if (!(this.mManagerBonificacion.getText().isEmpty())) {
                        Float value = Float.parseFloat(this.mManagerBonificacion.getText());
                        if (value < 0 || value > 5000) {
                            this.generalAlert.setTitle("Comision Invalida");
                            this.generalAlert.setHeaderText("La comision ingresada no es valida");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar una comision entre 0 y 5000 por empleado.");
                            this.mManagerBonificacion.clear();
                            error_Flag = true;
                            this.generalAlert.showAndWait();
                        } else {
                            try {
                                dummyManager.setM_ComisionManager(value);
                            } catch (InvalidAttributeValueException e) {
                                this.generalAlert.setTitle("Comision Invalida");
                                this.generalAlert.setHeaderText("La comision ingresada no es valida");
                                this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar una comision entre 0 y 5000 por empleado.");
                                this.mManagerBonificacion.clear();
                                error_Flag = true;
                                this.generalAlert.showAndWait();
                            } catch (NullPointerException e) {
                                this.generalAlert.setTitle("Campo Vacio");
                                this.generalAlert.setHeaderText("El campo comision esta vacio");
                                this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar una comision por empleado.");
                                error_Flag = true;
                                this.generalAlert.showAndWait();
                            }
                        }
                    }
                    else
                    {
                        this.generalAlert.setTitle("Campo Vacio");
                        this.generalAlert.setHeaderText("El campo comision esta vacio");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar una comision por empleado.");
                        error_Flag = true;
                        this.generalAlert.showAndWait();
                    }

                    if (!error_Flag) {
                        this.m_WrapperList.getM_employees().add(dummyManager); //Holder Principal
                        this.ManagerHolder.add(dummyManager); //Holder Especializado
                        this.mListVIewColaboradoresManagers.requestFocus();
                        this.mListVIewColaboradoresManagers.refresh();
                        updateTables(dummyManager);
                        updateIESSTable(this.m_WrapperList.getM_employees());
                    }
                }
                if (this.mColaboradorRadioButton.isSelected() && !(this.mListViewColaboradores.getSelectionModel().isEmpty()))
                {
                    //! Si el modelo de seleccion no esta vacio, tenemos que usar exactamente el mismo metodo anterior solo que para reemplzar esa seleccion.
                    Employee toModify = this.mListViewColaboradores.getSelectionModel().getSelectedItem();
                    //! Agarramos primero el nombre
                    if (!(this.mColaboradorNameTextField.getText().isEmpty())) {
                        try {
                            toModify.setM_nombreEmployee(this.mColaboradorNameTextField.getText());
                        } catch (InvalidAttributeValueException e) {
                            this.generalAlert.setTitle("Nombre Invalido");
                            this.generalAlert.setHeaderText("El nombre ingresado no es valido");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar solo un nombre por empleado.");
                            this.mColaboradorNameTextField.clear();
                            this.generalAlert.showAndWait();
                            error_Flag = true;
                        }
                        catch (NullPointerException e)
                        {
                            this.generalAlert.setTitle("Campo Vacio");
                            this.generalAlert.setHeaderText("El campo nombre esta vacio");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un nombre por empleado.");
                            error_Flag = true;
                            this.generalAlert.showAndWait();
                        }
                    } else {
                        this.generalAlert.setTitle("Campo Vacio");
                        this.generalAlert.setHeaderText("El campo nombre esta vacio");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un nombre por empleado.");
                        error_Flag = true;
                        this.generalAlert.showAndWait();
                    }
                    //! Agarramos el Apellido
                    if (!(this.mColaboradorApellidoTextField.getText().isEmpty())) {
                        try {
                            toModify.setM_apellidoEmployee(this.mColaboradorApellidoTextField.getText());
                        } catch (InvalidAttributeValueException e) {
                            this.generalAlert.setTitle("Apellido Invalido");
                            this.generalAlert.setHeaderText("El apellido ingresado no es valido");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar solo un apellido por empleado.");
                            this.mColaboradorApellidoTextField.clear();
                            this.generalAlert.showAndWait();
                            error_Flag = true;
                        }
                        catch (NullPointerException e)
                        {
                            this.generalAlert.setTitle("Campo Vacio");
                            this.generalAlert.setHeaderText("El campo apellido esta vacio");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un apellido por empleado.");
                            error_Flag = true;
                            this.generalAlert.showAndWait();
                        }
                    } else {
                        this.generalAlert.setTitle("Campo Vacio");
                        this.generalAlert.setHeaderText("El campo apellido esta vacio");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un apellido por empleado.");
                        error_Flag = true;
                        this.generalAlert.showAndWait();
                    }
                    //! Revisamos Selector de Fechas
                    try {
                        toModify.setM_fechaContratacionEmployee(Date.valueOf(this.mColaboradorFechaContratoDatePicker.getValue()).getTime());

                    } catch (InvalidAttributeValueException e) {
                        this.generalAlert.setTitle("Fecha Invalida");
                        this.generalAlert.setHeaderText("La fecha ingresada no es valida");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar una fecha valida por empleado.");
                        this.generalAlert.showAndWait();
                        error_Flag = true;
                    }
                    catch (NullPointerException e)
                    {
                        this.generalAlert.setTitle("Campo Vacio");
                        this.generalAlert.setHeaderText("El campo fecha esta vacio");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar una fecha por empleado.");
                        this.generalAlert.showAndWait();
                        error_Flag = true;
                    }

                    if (!(this.mColaboradorSalarioTextField.getText().isEmpty())) {
                        //! Convert to Float
                        Float readInSalary = Float.parseFloat(this.mColaboradorSalarioTextField.getText());
                        if (readInSalary < 800 || readInSalary > 3500) {
                            this.generalAlert.setTitle("Salario Invalido");
                            this.generalAlert.setHeaderText("El salario ingresado no es valido");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un salario entre 800 y 3500 por empleado.");
                            this.mColaboradorSalarioTextField.clear();
                            error_Flag = true;
                            this.generalAlert.showAndWait();
                        } else {
                            try {
                                toModify.setM_sueldoEmployee(readInSalary);
                            } catch (InvalidAttributeValueException e) {
                                this.generalAlert.setTitle("Salario Invalido");
                                this.generalAlert.setHeaderText("El salario ingresado no es valido");
                                this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un salario entre 800 y 3500 por empleado.");
                                this.mColaboradorSalarioTextField.clear();
                                error_Flag = true;
                                this.generalAlert.showAndWait();
                            }
                        }

                        this.mListViewColaboradores.refresh();
                        this.mListVIewColaboradoresManagers.refresh();

                    }
                    updateIESSTable(this.m_WrapperList.getM_employees());
                    removeEmployee(toModify);
                    updateTables(toModify);
                }
                else if (this.mGerenteRadioButton.isSelected() && !(this.mListVIewColaboradoresManagers.getSelectionModel().isEmpty()))
                {
                    error_Flag = false;
                    Manager toModify = (Manager) this.mListVIewColaboradoresManagers.getSelectionModel().getSelectedItem();
                    //! Agarramos primero el nombre
                    if (!(this.mColaboradorNameTextField.getText().isEmpty())) {
                        try {
                            toModify.setM_nombreEmployee(this.mColaboradorNameTextField.getText());
                        } catch (InvalidAttributeValueException e) {
                            this.generalAlert.setTitle("Nombre Invalido");
                            this.generalAlert.setHeaderText("El nombre ingresado no es valido");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar solo un nombre por empleado.");
                            this.mColaboradorNameTextField.clear();
                            this.generalAlert.showAndWait();
                            error_Flag = true;
                        } catch (NullPointerException e) {
                            this.generalAlert.setTitle("Campo Vacio");
                            this.generalAlert.setHeaderText("El campo nombre esta vacio");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un nombre por empleado.");
                            error_Flag = true;
                            this.generalAlert.showAndWait();
                        }
                    } else {
                        this.generalAlert.setTitle("Campo Vacio");
                        this.generalAlert.setHeaderText("El campo nombre esta vacio");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un nombre por empleado.");
                        error_Flag = true;
                        this.generalAlert.showAndWait();
                    }
                    //! Agarramos el Apellido
                    if (!(this.mColaboradorApellidoTextField.getText().isEmpty())) {
                        try {
                            toModify.setM_apellidoEmployee(this.mColaboradorApellidoTextField.getText());
                        } catch (InvalidAttributeValueException e) {
                            this.generalAlert.setTitle("Apellido Invalido");
                            this.generalAlert.setHeaderText("El apellido ingresado no es valido");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar solo un apellido por empleado.");
                            this.mColaboradorApellidoTextField.clear();
                            this.generalAlert.showAndWait();
                            error_Flag = true;
                        } catch (NullPointerException e) {
                            this.generalAlert.setTitle("Campo Vacio");
                            this.generalAlert.setHeaderText("El campo apellido esta vacio");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un apellido por empleado.");
                            error_Flag = true;
                            this.generalAlert.showAndWait();
                        }
                    } else {
                        this.generalAlert.setTitle("Campo Vacio");
                        this.generalAlert.setHeaderText("El campo apellido esta vacio");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un apellido por empleado.");
                        error_Flag = true;
                        this.generalAlert.showAndWait();
                    }
                    //! Revisamos Selector de Fechas
                    try {
                        toModify.setM_fechaContratacionEmployee(Date.valueOf(this.mColaboradorFechaContratoDatePicker.getValue()).getTime());

                    }
                    catch (InvalidAttributeValueException e)
                    {
                        this.generalAlert.setTitle("Fecha Invalida");
                        this.generalAlert.setHeaderText("La fecha ingresada no es valida");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar una fecha valida por empleado.");
                        this.generalAlert.showAndWait();
                        error_Flag = true;
                    }
                    catch (NullPointerException e)
                    {
                        this.generalAlert.setTitle("Campo Vacio");
                        this.generalAlert.setHeaderText("El campo fecha esta vacio");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar una fecha por empleado.");
                        this.generalAlert.showAndWait();
                        error_Flag = true;
                    }
                    //! Agarramos EL salario
                    if (!(this.mColaboradorSalarioTextField.getText().isEmpty())) {
                        //! Convert to Float
                        Float readInSalary = Float.parseFloat(this.mColaboradorSalarioTextField.getText());
                        if (readInSalary < 800 || readInSalary > 3500) {
                            this.generalAlert.setTitle("Salario Invalido");
                            this.generalAlert.setHeaderText("El salario ingresado no es valido");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un salario entre 800 y 3500 por empleado.");
                            this.mColaboradorSalarioTextField.clear();
                            error_Flag = true;
                            this.generalAlert.showAndWait();
                        } else {
                            try {
                                toModify.setM_sueldoEmployee(readInSalary);
                            } catch (InvalidAttributeValueException e) {
                                this.generalAlert.setTitle("Salario Invalido");
                                this.generalAlert.setHeaderText("El salario ingresado no es valido");
                                this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un salario entre 800 y 3500 por empleado.");
                                this.mColaboradorSalarioTextField.clear();
                                error_Flag = true;
                                this.generalAlert.showAndWait();
                            }
                        }
                    }
                    else {
                        this.generalAlert.setTitle("Campo Vacio");
                        this.generalAlert.setHeaderText("El campo salario esta vacio");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un salario por empleado.");
                        error_Flag = true;
                        this.generalAlert.showAndWait();
                    }
                    //! Leemos El valor del Combo Box
                    try {
                        switch (this.mManagerTituloNivelComboBox.getSelectionModel().getSelectedItem()) {
                            case "Maestria": {
                                toModify.setM_TituloNivelManager(Manager.MAESTRIA_CONSTANT);
                                break;
                            }
                            case "Doctorado": {
                                toModify.setM_TituloNivelManager(Manager.DOCTORADO_CONSTANT);
                                break;
                            }
                            case "Titulo Tercer Nivel": {
                                toModify.setM_TituloNivelManager(Manager.TERCER_NIVEL_CONSTANT);
                                break;
                            }
                        }
                    } catch (InvalidAttributeValueException e) {
                        this.generalAlert.setTitle("Titulo Invalido");
                        this.generalAlert.setHeaderText("El titulo ingresado no es valido");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un titulo valido por empleado.");
                        this.mManagerTituloNivelComboBox.getSelectionModel().clearSelection();
                        error_Flag = true;
                        this.generalAlert.showAndWait();
                    } catch (NullPointerException e) {
                        this.generalAlert.setTitle("Campo Vacio");
                        this.generalAlert.setHeaderText("El campo titulo esta vacio");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un titulo por empleado.");
                        error_Flag = true;
                        this.generalAlert.showAndWait();
                    }
                    //! Recolectamos el Valor de Comision
                    if (!(this.mManagerBonificacion.getText().isEmpty())) {
                        Float value = Float.parseFloat(this.mManagerBonificacion.getText());
                        if (value < 0 || value > 5000) {
                            this.generalAlert.setTitle("Comision Invalida");
                            this.generalAlert.setHeaderText("La comision ingresada no es valida");
                            this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar una comision entre 0 y 5000 por empleado.");
                            this.mManagerBonificacion.clear();
                            error_Flag = true;
                            this.generalAlert.showAndWait();
                        } else {
                            try {
                                toModify.setM_ComisionManager(value);
                            } catch (InvalidAttributeValueException e) {
                                this.generalAlert.setTitle("Comision Invalida");
                                this.generalAlert.setHeaderText("La comision ingresada no es valida");
                                this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar una comision entre 0 y 5000 por empleado.");
                                this.mManagerBonificacion.clear();
                                error_Flag = true;
                                this.generalAlert.showAndWait();
                            } catch (NullPointerException e) {
                                this.generalAlert.setTitle("Campo Vacio");
                                this.generalAlert.setHeaderText("El campo comision esta vacio");
                                this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar una comision por empleado.");
                                error_Flag = true;
                                this.generalAlert.showAndWait();
                            }
                        }
                    }
                    else
                    {
                        this.generalAlert.setTitle("Campo Vacio");
                        this.generalAlert.setHeaderText("El campo comision esta vacio");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar una comision por empleado.");
                        error_Flag = true;
                        this.generalAlert.showAndWait();
                    }

                    if (!error_Flag) {
                        this.mListVIewColaboradoresManagers.requestFocus();
                        this.mListVIewColaboradoresManagers.refresh();
                        updateIESSTable(this.m_WrapperList.getM_employees());
                        removeEmployee(toModify);
                        updateTables(toModify);
                    }
                }
        });

        //Connecting the method for deleting an employee into the Button
        this.deleteEmployeeButton.setOnMouseClicked(mouseClickedEvent ->
        {
            Alert ConfirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            ConfirmationAlert.setTitle("Boton Para Elminar Empleado Presionado");
            ConfirmationAlert.setHeaderText("Se esta eliminando un empleado de la lista");
            ConfirmationAlert.setContentText("Desea continuar?");
            Optional<ButtonType> result = ConfirmationAlert.showAndWait();
            if (result.get().equals(ButtonType.OK))
            {
               if (this.mColaboradorRadioButton.isSelected())
               {
                   this.EmployeeHolder.remove(this.mListViewColaboradores.getSelectionModel().getSelectedItem());
                   this.m_WrapperList.getM_employees().remove(this.mListViewColaboradores.getSelectionModel().getSelectedItem());
                   this.mListViewColaboradores.requestFocus();
                   removeEmployee(this.mListViewColaboradores.getSelectionModel().getSelectedItem());
                   updateIESSTable(this.m_WrapperList.getM_employees());
               }
               else if (this.mGerenteRadioButton.isSelected())
               {
                   this.ManagerHolder.remove(this.mListViewColaboradores.getSelectionModel().getSelectedItem());
                   this.m_WrapperList.getM_employees().remove(this.mListViewColaboradores.getSelectionModel().getSelectedItem());
                   this.mListVIewColaboradoresManagers.requestFocus();
                   removeEmployee(this.mListVIewColaboradoresManagers.getSelectionModel().getSelectedItem());
                   updateIESSTable(this.m_WrapperList.getM_employees());
               }
            }
            else if (result.get().equals(ButtonType.CANCEL))
            {
                this.mListViewColaboradores.requestFocus();
            }
            else if (result.get().equals(ButtonType.CLOSE))
            {
                this.mListViewColaboradores.requestFocus();
            }
        });

        //? Anadimos mouse movements para seleccion y deselecion de empleados
        ArrayList<ListView<Employee>> collectionOfViews = new ArrayList<>();
        Collections.addAll(collectionOfViews, this.mListViewColaboradores, this.mListVIewColaboradoresManagers);

        collectionOfViews.forEach(consumerAction ->
        {
            consumerAction.setOnMouseClicked(mouseClicked ->
            {
                if (mouseClicked.getClickCount() == 2 && !(consumerAction.getSelectionModel().isEmpty()))
                {

                    this.mListViewColaboradores.getSelectionModel().clearSelection();
                    this.addEmployeeButton.setText("Añadir Empleado");
                    this.addEmployeeButton.requestFocus();
                    this.mListViewColaboradores.requestFocus();
                }
                else if (mouseClicked.getClickCount() == 1 && !(consumerAction.getSelectionModel().isEmpty()))
                {
                    this.addEmployeeButton.setText("Modificar Empleado");
                    this.addEmployeeButton.setBorder(Border.EMPTY);
                    this.addEmployeeButton.requestFocus();
                }
            });
        });
        //! Anadimos funcionalidad para modificar empleado, trabajamos en conjunto con varios items en pantalla
        // * Este primer selecctor anade funcionalidad para empleados normales, cuyos campos son faciles de registrar
        // * Este segundo selector, anade funciionalidad al list view de los managers, cuyos campos son mas complicados
        collectionOfViews.get(1).getSelectionModel().selectedItemProperty().addListener(listener ->
        {
            if (!(collectionOfViews.get(1).getSelectionModel().isEmpty())){
                String[] strings = collectionOfViews.get(1).getSelectionModel().getSelectedItem().toCSVString().split(",");
                switch( strings.length)
                {
                    case 6:
                    {
                        this.mColaboradorNameTextField.setText(strings[0]);
                        this.mColaboradorApellidoTextField.setText(strings[1]);
                        this.mColaboradorIDTextField.setText(strings[2]);
                        this.mColaboradorFechaContratoDatePicker.setValue(LocalDate.ofInstant(
                                Instant.ofEpochMilli(Long.parseLong(strings[3])),
                                ZoneId.systemDefault()).atStartOfDay().toLocalDate());
                        this.mColaboradorSalarioTextField.setText(strings[4]);
                        break;
                    }
                    case 8:
                    {
                        this.mColaboradorNameTextField.setText(strings[0]);
                        this.mColaboradorApellidoTextField.setText(strings[1]);
                        this.mColaboradorIDTextField.setText(strings[2]);
                        this.mColaboradorFechaContratoDatePicker.setValue(LocalDate.ofInstant(
                                Instant.ofEpochMilli(Long.parseLong(strings[3])),
                                ZoneId.systemDefault()).atStartOfDay().toLocalDate());
                        this.mColaboradorSalarioTextField.setText(strings[4]);
                        if (strings[6].equals("TercerNivel")) {
                            this.mManagerTituloNivelComboBox.getSelectionModel().select("Titulo Tercer Nivel");
                        } else {
                            this.mManagerTituloNivelComboBox.getSelectionModel().select(strings[6]);
                        }
                        this.mManagerBonificacion.setText(strings[7]);
                        break;
                    }

                }


            }
            else
            {
                this.mColaboradorNameTextField.clear();
                this.mColaboradorApellidoTextField.clear();
                this.mColaboradorSalarioTextField.clear();
                this.mColaboradorIDTextField.clear();
                this.mColaboradorFechaContratoDatePicker.setValue(null);
                this.mManagerTituloNivelComboBox.getSelectionModel().clearSelection();
                this.mManagerBonificacion.clear();
            }
        });


        //? Anadimos un listener a las selecciones del panel general para esto vemos cuando editar
        //Procedemos a conectar los formatos de filtrado de datos.
        this.sortingMenuBar = (MenuBar) root.lookup("#sortingMenuBar");
        // ! Primer Metodo, Sorting a ambas listas internas con respecto al nombre, orden alfabetico
        this.mSortingAlfabeticoNombreMenuItem =   this.sortingMenuBar.getMenus().getFirst().getItems().get(0);
        this.mSortingAlfabeticoNombreMenuItem.setOnAction(event ->
        {
            if (this.mColaboradorRadioButton.isSelected())
            {
                Collections.sort(this.EmployeeHolder, new Comparator<Employee>() {
                    @Override
                    public int compare(Employee o1, Employee o2) {
                        return o1.getM_nombreEmployee().compareToIgnoreCase(o2.getM_nombreEmployee());
                    }
                });
                this.mListViewColaboradores.refresh();
                this.mListViewColaboradores.requestFocus();
            }
            else if (this.mGerenteRadioButton.isSelected())
            {
                Collections.sort(this.ManagerHolder, new Comparator<Employee>() {
                    @Override
                    public int compare(Employee o1, Employee o2) {
                        return o1.getM_nombreEmployee().compareToIgnoreCase(o2.getM_nombreEmployee());
                    }
                });
                this.mListVIewColaboradoresManagers.refresh();
                this.mListVIewColaboradoresManagers.requestFocus();
            }
        });
        // ! Segundo Metodo, Sorting a ambas listas internas con respecto al apellido, orden alfabetico
        this.mSortingAlfabeticoApellidosMenuItem = this.sortingMenuBar.getMenus().getFirst().getItems().get(1);
        this.mSortingAlfabeticoApellidosMenuItem.setOnAction(event ->
        {
            if (this.mColaboradorRadioButton.isSelected())
            {
                this.EmployeeHolder.sort(new Comparator<Employee>() {
                    @Override
                    public int compare(Employee o1, Employee o2) {
                        return o1.getM_apellidoEmployee().compareToIgnoreCase(o2.getM_apellidoEmployee());
                    }
                });
                this.mListViewColaboradores.refresh();
                this.mListViewColaboradores.requestFocus();
            }
             else if  (this.mGerenteRadioButton.isSelected())
             {
                 this.ManagerHolder.sort(new Comparator<Employee>() {
                     @Override
                     public int compare(Employee o1, Employee o2) {
                         return o1.getM_apellidoEmployee().compareToIgnoreCase(o2.getM_apellidoEmployee());
                     }
                 });
                 this.mListVIewColaboradoresManagers.refresh();
                 this.mListVIewColaboradoresManagers.requestFocus();
             }

        });

        //! Tercer Metodo, Sorting La Fecha de Ingreso de los Empleados
        this.smSortingFechaContratoMenuItem = this.sortingMenuBar.getMenus().getFirst().getItems().get(2);
        this.smSortingFechaContratoMenuItem.setOnAction(event ->
        {
            if (this.mColaboradorRadioButton.isSelected())
            {
                this.EmployeeHolder.sort(new Comparator<Employee>() {
                    @Override
                    public int compare(Employee o1, Employee o2) {
                        return -1*Objects.compare(o1.getM_DateEnNumero(), o2.getM_DateEnNumero(), Long::compare);
                    }
                });
                this.mListViewColaboradores.refresh();
                this.mListViewColaboradores.requestFocus();
            }
            else if (this.mGerenteRadioButton.isSelected())
            {
                this.ManagerHolder.sort(new Comparator<Employee>() {
                    @Override
                    public int compare(Employee o1, Employee o2) {
                        return -1*Objects.compare(o1.getM_DateEnNumero(), o2.getM_DateEnNumero(), Long::compare);
                    }
                });
                this.mListVIewColaboradoresManagers.refresh();
                this.mListVIewColaboradoresManagers.requestFocus();
            }
        });
        //! Cuarto Metodo, Usando sueldo como comparativo, mayor a menor
        this.mSortingSueldoMenuItem = this.sortingMenuBar.getMenus().getFirst().getItems().get(3);
        this.mSortingSueldoMenuItem.setOnAction(event ->
        {
            if (this.mColaboradorRadioButton.isSelected())
            {
                this.EmployeeHolder.sort(new Comparator<Employee>() {
                    @Override
                    public int compare(Employee o1, Employee o2) {
                        return -1*Float.compare(o1.getM_sueldoEmployee(), o2.getM_sueldoEmployee());
                    }
                });
                this.mListViewColaboradores.refresh();
                this.mListViewColaboradores.requestFocus();
            }
            else if (this.mGerenteRadioButton.isSelected())
            {
                this.ManagerHolder.sort(new Comparator<Employee>() {
                    @Override
                    public int compare(Employee o1, Employee o2) {
                        return -1*Float.compare(o1.getM_sueldoEmployee(), o2.getM_sueldoEmployee());
                    }
                });
                this.mListVIewColaboradoresManagers.refresh();
                this.mListVIewColaboradoresManagers.requestFocus();
            }
        });
        //! Ultimo Metodo, solo para Gerentes, usamos su propio sorting
        this.mSortingSueldoYBonoMenuItem = this.sortingMenuBar.getMenus().getFirst().getItems().get(4);
        this.mSortingSueldoYBonoMenuItem.setOnAction(event ->
        {
            if (this.mGerenteRadioButton.isSelected())
            {
                this.ManagerHolder.sort(new Comparator<Employee>() {
                    @Override
                    public int compare(Employee o1, Employee o2) {
                        return -1*o1.compareTo(o2);
                    }
                });
                this.mListVIewColaboradoresManagers.refresh();
                this.mListVIewColaboradoresManagers.requestFocus();
            }
        });



        //! OJO Seccion encarga de carga y descarga de archivos del sistema
        this.importarDatosBinario = (Button) root.lookup("#importarDatosBinario");
        this.exportarDatosBinario = (Button) root.lookup("#exportarDatosBinario");
        this.importarDatosCSV = (Button) root.lookup("#importarDatosCSV");
        this.exportarDatosCSV = (Button) root.lookup("#exportarDatosCSV");
        this.importarDatosXML = (Button) root.lookup("#importarDatosXML");
        this.exportarDatosXML = (Button) root.lookup("#exportarDatosXML");
        this.importarDatosJSON = (Button) root.lookup("#importarDatosJSON");
        this.exportarDatosJSON = (Button) root.lookup("#exportarDatosJSON");
        this.eliminarDatos = (Button) root.lookup("#eliminarDatos");
        this.exportarDatosTributarios = (Button) root.lookup("#exportarDatosTributarios");
        this.descargaInformacionTributariaRadioButton = (RadioButton) root.lookup("#descargaInformacionTributariaRadioButton");
        this.descargaInformacionTributariaYSueldosRadioButton = (RadioButton) root.lookup("#descargaInformacionTributariaYSueldosRadioButton");
        this.descargaInformacionSueldosLiquidosMenoresRadioButton = (RadioButton) root.lookup("#descargaInformacionSueldosLiquidosMenoresRadioButton");
        this.descargaGerentesToggleButton = (RadioButton) root.lookup("#descargaGerentesToggleButton");
        this.descargaGeneralToggleButton = (RadioButton) root.lookup("#descargaGeneralToggleButton");
        this.descargaColaboradoresToggleButton = (RadioButton) root.lookup("#descargaColaboradoresToggleButton");

        //? Comenzamos con eliminar los datos
        this.eliminarDatos.setOnMouseClicked( mouseHasBeenClickedToDelete ->
        {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Eliminacion de Datos Inminente");
            confirmationAlert.setHeaderText("Estas seguro que quieres eliminar los datos?");
            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && (result.get() == ButtonType.OK))
            {
                this.EmployeeHolder.clear();
                this.ManagerHolder.clear();
                this.m_WrapperList.getM_employees().clear();
                this.mListViewColaboradores.refresh();
                this.mListVIewColaboradoresManagers.refresh();
                updateIESSTable(this.m_WrapperList.getM_employees());
            }
        });
        //? Carga de Datos Binario
        this.importarDatosBinario.setOnMouseClicked(loadingBinaryData ->
        {
            FileChooser internalOpenFileChooser = new FileChooser();
            internalOpenFileChooser.setTitle("Seleccione su archivo Binario");
            internalOpenFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("txt binary files","*.txt"));
            try
            {
                File openedFile = internalOpenFileChooser.showOpenDialog(primaryStage);
                if (openedFile != null) {
                    //? Analizamos archivo abierto
                    EmployeeListWrapper wrapper = ToBinarySerializer.deserializeFromBinary(openedFile);
                    if (!this.m_WrapperList.getM_employees().isEmpty()) {
                        this.m_WrapperList = null;
                        this.EmployeeHolder.clear();
                        this.ManagerHolder.clear();
                        this.m_WrapperList = wrapper;
                    } else {
                        this.m_WrapperList = wrapper;
                    }
                    updateIESSTable(this.m_WrapperList.getM_employees());
                    extracted();
                }
                
            }
            catch(FileNotFoundAlert fileNotFoundAlert)
            {
                this.fileNotFoundAlert.setTitle("Archivo no encontrado");
                this.fileNotFoundAlert.setContentText("El archivo que intenta abrir no fue encontrado");
                this.fileNotFoundAlert.showAndWait();
            }
            catch(IOException ioException)
            {
                this.fileNotFoundAlert.setTitle("Error de lectura");
                this.fileNotFoundAlert.setContentText("El archivo fue encontrado, se pudo abrir, se pudo cargar al lector, pero su formato no corresponde con Binario.");
                this.fileNotFoundAlert.showAndWait();
            }
            catch(ClassNotFoundException foundException)
            {
                this.fileNotFoundAlert.setTitle("Error de lectura");
                this.fileNotFoundAlert.setContentText("El archivo pudo ser leido pero su contenido no es el correcto, y requerido por la aplicacion");
                this.fileNotFoundAlert.showAndWait();
            }
            catch(FileIsEmptyAlert fileIsEmptyAlert)
            {
                this.fileNotFoundAlert.setTitle("Archivo Vacio");
                this.fileNotFoundAlert.setContentText("El archivo que intenta abrir esta vacio");
                this.fileNotFoundAlert.showAndWait();
            }
        });

        this.exportarDatosBinario.setOnMouseClicked(exportingBinaryData ->
        {
            FileChooser internalFileSaving = new FileChooser();
            internalFileSaving.setTitle("Escoga donde guardar su documento");
            internalFileSaving.getExtensionFilters().add(new FileChooser.ExtensionFilter("txt binary file", "*.txt"));
            try
            {

                File savedFile = internalFileSaving.showSaveDialog(primaryStage);
                if (savedFile != null) {
                    ToBinarySerializer.serializeToBinary(savedFile, this.m_WrapperList);
                }
            }
            catch(FileNotFoundAlert alert)
            {
                this.fileNotFoundAlert.setTitle("Archivo no encontrado");
                this.fileNotFoundAlert.setContentText("El archivo que intenta abrir no fue encontrado dentro del sistema operativo, y por tanto fallo la escritura.");
                this.fileNotFoundAlert.showAndWait();
            }
            catch(IOException alert)
            {
                this.fileNotFoundAlert.setTitle("Error de escritura");
                this.fileNotFoundAlert.setContentText("El archivo que se intento abrir fue encontrado, pero el sistema operativo \n" +
                        "tuvo un error al escribir el header del archivo y fallo la escritura.");
                this.fileNotFoundAlert.showAndWait();
            }
            catch(IllegalStateException alert )
            {
                this.fileNotFoundAlert.setTitle("Error de escritura");
                this.fileNotFoundAlert.setContentText("El archivo seleccionado fue encontrado, sin embargo, el proceso de serializacion fallo. Esto se puede dar por \n" +
                        "1) Falta de Datos Internos de Empleados Para Serializar\n" +
                        "2) Ha habido un error grave dentro del sistema y los empleados no se han registrado correctamente");
                this.fileNotFoundAlert.showAndWait();
            }
        });
        //? Procedemos con exporte e importe de datos en CSV
        this.importarDatosCSV.setOnMouseClicked(importingCSVData ->
        {
            FileChooser helperImportingCSV = new FileChooser();
            helperImportingCSV.setTitle("Seleccione su archivo CSV");
            helperImportingCSV.getExtensionFilters().add(new FileChooser.ExtensionFilter("csv files", "*.csv"));
            try
            {

                File openedFile = helperImportingCSV.showOpenDialog(primaryStage);
                if (openedFile != null){
                    EmployeeListWrapper wrapper = ToCSVSerializer.deserializeFromFile(openedFile);
                    if (!this.m_WrapperList.getM_employees().isEmpty()) {
                        this.m_WrapperList = null;
                        this.EmployeeHolder.clear();
                        this.ManagerHolder.clear();
                        this.m_WrapperList = wrapper;
                    } else {
                        this.m_WrapperList = wrapper;
                    }
                    updateIESSTable(this.m_WrapperList.getM_employees());
                    extracted();
                }
            }
            catch(FileNotFoundAlert | FileNotFoundException alert)
            {
                this.fileNotFoundAlert.setTitle("Archivo no encontrado");
                this.fileNotFoundAlert.setContentText("El archivo que intenta abrir no fue encontrado dentro del sistema operativo,\n" +
                        "y por tanto fallo la lectura.");
                this.fileNotFoundAlert.showAndWait();
            }
            catch(FileIsEmptyAlert alert)
            {
                this.fileNotFoundAlert.setTitle("Archivo Vacio");
                this.fileNotFoundAlert.setHeaderText("El archivo que intenta abrir fue encontrado por el sistema operativo, pero su extension es nula.\n" +
                        "Por tanto, la deserializacion de CSV no puede continuar");
                this.fileNotFoundAlert.showAndWait();
            }
            catch (InvalidAttributeValueException e)
            {
                this.fileNotFoundAlert.setTitle("Error de lectura");
                this.fileNotFoundAlert.setContentText("El archivo que se intento abrir fue encontrado, pero el sistema operativo \n" +
                        "tuvo un error al leer el header del archivo y fallo la lectura.");
            }
            catch( IncorrectCSVFormat alert)
            {
                this.fileNotFoundAlert.setTitle("Error de lectura");
                this.fileNotFoundAlert.setContentText("El archivo que se intento abrir fue encontrado, pero el formato del archivo es incorrecto.\n" +
                        "Por tanto, fallo la lectura de datos");
            }
        });

        this.exportarDatosCSV.setOnMouseClicked(exportingCSVData ->
        {
            FileChooser helperExportingCSV = new FileChooser();
            helperExportingCSV.setTitle("Escoga donde guardar su documento");
            helperExportingCSV.getExtensionFilters().add(new FileChooser.ExtensionFilter("csv files", "*.csv"));
            try
            {

                File savedFile = helperExportingCSV.showSaveDialog(primaryStage);
                if (savedFile != null) {
                    ToCSVSerializer.serializeToFile(savedFile, this.m_WrapperList);
                }
            }
            catch(FileNotFoundAlert alert)
            {
                this.fileNotFoundAlert.setTitle("Archivo no encontrado");
                this.fileNotFoundAlert.setContentText("El archivo que intenta abrir no fue encontrado dentro del sistema operativo,\n" +
                        "y por tanto fallo la escritura.");
                this.fileNotFoundAlert.showAndWait();
            }
            catch(InvalidAttributeValueException alert)
            {
                this.fileNotFoundAlert.setTitle("Error de escritura");
                this.fileNotFoundAlert.setContentText("El archivo seleccionado fue abierto correctamente. " +
                        "Sin embargo, el sistema detecto,\n" +
                        "que la base de datos de empleados esta vacia y por tanto no pudo continuar la serializacion");
                this.fileNotFoundAlert.showAndWait();
            }
            catch(FileNotFoundException e)
            {
                this.fileNotFoundAlert.setTitle("Erro de escritura");
                this.fileNotFoundAlert.setContentText("El archivo fue encontrado y creado. No obstante, el sistema operativo no \n" +
                        "propicio de headers y buffers para su escritura por lo que la serializacion fallo.");
                this.fileNotFoundAlert.showAndWait();
            }
        });

        //! Procedemos con la serializacion a XML
        this.exportarDatosXML.setOnMouseClicked(exportingXMLData ->
        {
            FileChooser helperExportingXML = new FileChooser();
            helperExportingXML.setTitle("Escoga donde guardar su documento");
            helperExportingXML.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
            try
            {

                File savedFile = helperExportingXML.showSaveDialog(primaryStage);
                if (savedFile != null) {
                    if (!savedFile.exists()){savedFile.createNewFile();}
                    ToXMLSerializer.serializeToFile(savedFile, this.m_WrapperList);
                }
            }
            catch(FileIsEmptyAlert alert)
            {
                this.fileNotFoundAlert.setTitle("Error de escritura");
                this.fileNotFoundAlert.setContentText("El archivo enviado fue recibido correctamente, pero al momento de\n" +
                        "serialziar, el sistema determino que la base de datos estaba vacia y por tanto el proceso fallo.");
                this.fileNotFoundAlert.showAndWait();
            }
            catch(IllegalArgumentException e)
            {
                this.fileNotFoundAlert.setTitle("Error de escritura");
                this.fileNotFoundAlert.setContentText("El presente error representa una falla grave dentro de la serializacion a XML.\n" +
                        "Si el error persiste, comuniquese con IT, informe de un error de JAXB en el proceso de serializacion");
                this.fileNotFoundAlert.showAndWait();
                e.printStackTrace();
            }

            catch(FileNotFoundAlert alert)
            {
                this.fileNotFoundAlert.setTitle("Archivo no encontrado");
                this.fileNotFoundAlert.setContentText("El archivo que intenta abrir no fue encontrado dentro del sistema operativo,\n" +
                        "y por tanto fallo la escritura.");
                this.fileNotFoundAlert.showAndWait();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        this.importarDatosXML.setOnMouseClicked(importingXMLData ->
        {
            FileChooser helperImportingXML = new FileChooser();
            helperImportingXML.setTitle("Seleccione su archivo XML");
            helperImportingXML.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
            try
            {

                File openedFile = helperImportingXML.showOpenDialog(primaryStage);
                if (openedFile != null){
                    EmployeeListWrapper wrapper = ToXMLSerializer.deserializeFromFile(openedFile);
                    if (!this.m_WrapperList.getM_employees().isEmpty()) {
                        this.m_WrapperList = null;
                        this.EmployeeHolder.clear();
                        this.ManagerHolder.clear();
                        this.m_WrapperList = wrapper;
                    } else {
                        this.m_WrapperList = wrapper;
                    }
                    updateIESSTable(this.m_WrapperList.getM_employees());
                    extracted();
                }
            }
            catch(FileNotFoundAlert alert)
            {
                this.fileNotFoundAlert.setTitle("Archivo no encontrado");
                this.fileNotFoundAlert.setContentText("El archivo regsitrado en el sistema no pudo ser analizado correctamente.\n " +
                        "Un error comun es que el archivo seleccionado haya sido eliminado por ser temporal en el Sistema operativo, sino\n" +
                        "es muy probable que el archivo haya estado vacio y el sistema no pudo leerlo.");
                this.fileNotFoundAlert.showAndWait();
            }
            catch(IllegalArgumentException e)
            {
                this.fileNotFoundAlert.setTitle("Error de escritura");
                this.fileNotFoundAlert.setContentText("El presente error representa una falla grave dentro de la serializacion a XML.\n" +
                        "Si el error persiste, comuniquese con IT, informe de un error de JAXB en el proceso de serializacion");
                this.fileNotFoundAlert.showAndWait();
                e.printStackTrace();
            }
        });
        //! Procedemos con serializacion JSON
        this.importarDatosJSON.setOnMouseClicked(importingJSONData ->
        {
            FileChooser helperImportingJSON = new FileChooser();
            helperImportingJSON.setTitle("Seleccione su archivo JSON");
            helperImportingJSON.getExtensionFilters().add(new FileChooser.ExtensionFilter("json files", "*.json"));
            try
            {

                File openedFile = helperImportingJSON.showOpenDialog(primaryStage);
                if (openedFile != null){
                    EmployeeListWrapper wrapper = ToJsonSerializer.deserializeFromFile(openedFile);
                    if (!this.m_WrapperList.getM_employees().isEmpty()) {
                        this.m_WrapperList = null;
                        this.EmployeeHolder.clear();
                        this.ManagerHolder.clear();
                        this.m_WrapperList = wrapper;
                    } else {
                        this.m_WrapperList = wrapper;
                    }
                    updateIESSTable(this.m_WrapperList.getM_employees());
                    extracted();
                }
            }
            catch(FileNotFoundAlert alert)
            {
                this.fileNotFoundAlert.setTitle("Archivo no encontrado");
                this.fileNotFoundAlert.setContentText("El archivo regsitrado en el sistema no pudo ser analizado correctamente.\n " +
                        "Un error comun es que el archivo seleccionado haya sido eliminado por ser temporal en el Sistema operativo, sino\n" +
                        "es muy probable que el archivo haya estado vacio y el sistema no pudo leerlo.");
                this.fileNotFoundAlert.showAndWait();
            }
            catch(FileIsEmptyAlert alert)
            {
                this.fileNotFoundAlert.setTitle("Error de lectura");
                this.fileNotFoundAlert.setContentText("El archivo fue encontrado dentro del sistema, pero se encontro vacio,\n" +
                        "por lo tanto no se pudo realizar una deserializacion y el proceso fallo.");
                this.fileNotFoundAlert.showAndWait();
            }
            catch(IllegalStateException e)
            {
                this.fileNotFoundAlert.setTitle("Error de lectura");
                this.fileNotFoundAlert.setContentText("El presente error informa de un error dentro del proceso de deserializacion del archivo, " +
                        "muy probablemente los datos fueron corrompidos, editados manualmente o se cargo un archivo no proveniente de la aplicacion");
                this.fileNotFoundAlert.showAndWait();
            }
        });
        this.exportarDatosJSON.setOnMouseClicked(exportingJSONData ->
        {
            FileChooser helperExportingJSON = new FileChooser();
            helperExportingJSON.setTitle("Escoga donde guardar su documento");
            helperExportingJSON.getExtensionFilters().add(new FileChooser.ExtensionFilter("json files", "*.json"));
            try
            {

                File savedFile = helperExportingJSON.showSaveDialog(primaryStage);
                if (savedFile != null) {
                    ToJsonSerializer.serializeToFile(savedFile, this.m_WrapperList);
                }
            }
            catch(FileNotFoundAlert alert)
            {
                this.fileNotFoundAlert.setTitle("Archivo no encontrado");
                this.fileNotFoundAlert.setContentText("El archivo que intenta abrir no fue encontrado dentro del sistema operativo,\n" +
                        "y por tanto fallo la escritura.");
                this.fileNotFoundAlert.showAndWait();
            }
            catch(IllegalArgumentException e)
            {
                this.fileNotFoundAlert.setTitle("Error Durante Serializacion");
                this.fileNotFoundAlert.setHeaderText("Error Durante Serializacion");
                this.fileNotFoundAlert.setContentText("El presente error muestra que hubo un mal manejo de datos durante la serializacion.\n" +
                        "En general esto puede suceder si la base de datos de empleados esta  vacia. Favor revisar los datos.");
            }
        });

        //! Procedemos a implementar la primera segmentacion de datos, impresion de sueldos desglosados a archivo
        this.exportarDatosTributarios.setOnMouseClicked(eventOnClick ->
        {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Escoga donde guardar su archivo txt.");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("txt files", "*.txt"));
            if (this.descargaInformacionTributariaRadioButton.isSelected())
            {
                File selectedFile = chooser.showSaveDialog(primaryStage);
                if (this.descargaGeneralToggleButton.isSelected())
                {
                    if (selectedFile != null)
                    {
                        try{
                            ToSalaryReport.serializeTributaryDesgloseToFile(selectedFile, this.m_WrapperList.getM_employees());
                        }
                        catch(FileNotFoundAlert | FileNotFoundException e)
                        {
                            this.fileNotFoundAlert.setTitle("Error de escritura");
                            this.fileNotFoundAlert.setContentText("El archivo que intenta abrir no fue encontrado dentro del sistema operativo,\n" +
                                    "y por tanto fallo la escritura.");
                            this.fileNotFoundAlert.showAndWait();
                        }
                    }
                }
                else if (this.descargaColaboradoresToggleButton.isSelected())
                {
                    if (selectedFile != null)
                    {
                        try{
                            ToSalaryReport.serializeTributaryDesgloseToFile(selectedFile, this.EmployeeHolder);
                        }
                        catch(FileNotFoundAlert | FileNotFoundException e)
                        {
                            this.fileNotFoundAlert.setTitle("Error de escritura");
                            this.fileNotFoundAlert.setContentText("El archivo que intenta abrir no fue encontrado dentro del sistema operativo,\n" +
                                    "y por tanto fallo la escritura.");
                            this.fileNotFoundAlert.showAndWait();
                            e.printStackTrace();
                        }
                    }
                }
                else if (this.descargaGerentesToggleButton.isSelected())
                {
                    if (selectedFile != null)
                    {
                        try{
                            ToSalaryReport.serializeTributaryDesgloseToFile(selectedFile, this.ManagerHolder);
                        }
                        catch(FileNotFoundAlert | FileNotFoundException e)
                        {
                            this.fileNotFoundAlert.setTitle("Error de escritura");
                            this.fileNotFoundAlert.setContentText("El archivo que intenta abrir no fue encontrado dentro del sistema operativo,\n" +
                                    "y por tanto fallo la escritura.");
                            this.fileNotFoundAlert.showAndWait();
                        }
                    }
                }
            }
            else if (this.descargaInformacionTributariaYSueldosRadioButton.isSelected())
            {
                File selectedFile = chooser.showSaveDialog(primaryStage);
                if (this.descargaGeneralToggleButton.isSelected())
                {
                    if (selectedFile != null)
                    {
                        try{
                            ToSalaryReport.serializeSalaryDesgloseToFile(selectedFile, this.m_WrapperList.getM_employees());
                        }
                        catch(FileNotFoundAlert | FileNotFoundException e)
                        {
                            this.fileNotFoundAlert.setTitle("Error de escritura");
                            this.fileNotFoundAlert.setContentText("El archivo que intenta abrir no fue encontrado dentro del sistema operativo,\n" +
                                    "y por tanto fallo la escritura.");
                            this.fileNotFoundAlert.showAndWait();
                        }
                    }
                }
                else if (this.descargaColaboradoresToggleButton.isSelected())
                {
                    if (selectedFile != null)
                    {
                        try{
                            ToSalaryReport.serializeSalaryDesgloseToFile(selectedFile, this.EmployeeHolder);
                        }
                        catch(FileNotFoundAlert | FileNotFoundException e)
                        {
                            this.fileNotFoundAlert.setTitle("Error de escritura");
                            this.fileNotFoundAlert.setContentText("El archivo que intenta abrir no fue encontrado dentro del sistema operativo,\n" +
                                    "y por tanto fallo la escritura.");
                            this.fileNotFoundAlert.showAndWait();
                            e.printStackTrace();
                        }
                    }
                }
                else if (this.descargaGerentesToggleButton.isSelected())
                {
                    if (selectedFile != null)
                    {
                        try{
                            ToSalaryReport.serializeSalaryDesgloseToFile(selectedFile, this.ManagerHolder);
                        }
                        catch(FileNotFoundAlert | FileNotFoundException e)
                        {
                            this.fileNotFoundAlert.setTitle("Error de escritura");
                            this.fileNotFoundAlert.setContentText("El archivo que intenta abrir no fue encontrado dentro del sistema operativo,\n" +
                                    "y por tanto fallo la escritura.");
                            this.fileNotFoundAlert.showAndWait();
                        }
                    }
                }
            }
            else if (this.descargaInformacionSueldosLiquidosMenoresRadioButton.isSelected())
            {
                File selectedFile = chooser.showSaveDialog(primaryStage);
                if (this.descargaGeneralToggleButton.isSelected())
                {
                    if (selectedFile != null)
                    {
                        List<Employee> filteredEmployees = new ArrayList<>();
                        for(Employee em : this.m_WrapperList.getM_employees())
                        {
                            if (em.getM_SueldoMensual() < 800f)
                            {
                                filteredEmployees.add(em);
                            }
                        }
                        try {
                            ToSalaryReport.serializeSalaryDesgloseToFile(selectedFile, filteredEmployees);
                        }
                        catch(FileNotFoundAlert | FileNotFoundException e)
                        {
                            this.fileNotFoundAlert.setTitle("Error de escritura");
                            this.fileNotFoundAlert.setContentText("El archivo que intenta abrir no fue encontrado dentro del sistema operativo,\n" +
                                    "y por tanto fallo la escritura.");
                            this.fileNotFoundAlert.showAndWait();
                        }

                    }
                }
                else if (this.descargaColaboradoresToggleButton.isSelected())
                {
                    if (selectedFile != null)
                    {
                        List<Employee> filteredEmployees = new ArrayList<>();
                        for(Employee em : this.EmployeeHolder)
                        {
                            if (em.getM_SueldoMensual() < 800f)
                            {
                                filteredEmployees.add(em);
                            }
                        }
                        try {
                            ToSalaryReport.serializeSalaryDesgloseToFile(selectedFile, filteredEmployees);
                        }
                        catch(FileNotFoundAlert | FileNotFoundException e)
                        {
                            this.fileNotFoundAlert.setTitle("Error de escritura");
                            this.fileNotFoundAlert.setContentText("El archivo que intenta abrir no fue encontrado dentro del sistema operativo,\n" +
                                    "y por tanto fallo la escritura.");
                            this.fileNotFoundAlert.showAndWait();
                        }
                    }
                }
                else if (this.descargaGerentesToggleButton.isSelected())
                {
                    if (selectedFile != null)
                    {
                        List<Employee> filteredEmployees = new ArrayList<>();
                        for(Employee em : this.ManagerHolder)
                        {
                            if (em.getM_SueldoMensual() < 800f)
                            {
                                filteredEmployees.add(em);
                            }
                        }
                        try {
                            ToSalaryReport.serializeSalaryDesgloseToFile(selectedFile, filteredEmployees);
                        }
                        catch(FileNotFoundAlert | FileNotFoundException e)
                        {
                            this.fileNotFoundAlert.setTitle("Error de escritura");
                            this.fileNotFoundAlert.setContentText("El archivo que intenta abrir no fue encontrado dentro del sistema operativo,\n" +
                                    "y por tanto fallo la escritura.");
                            this.fileNotFoundAlert.showAndWait();
                        }
                    }
                }
            }
        });





    }

    private void extracted() {
        this.m_WrapperList.getM_employees().forEach(employee ->
        {
            if(employee.toCSVString().split(",").length == 8)
            {
                this.ManagerHolder.add(employee);
            }
            else {
                this.EmployeeHolder.add(employee);
            }
        });


    }

    private void updateIESSTable(List<Employee> employees)
    {
        employees.forEach(MonthlySalaryHelper::addCalculatedEntries);
        this.desgloseIESSEmpleados.getItems().clear();
        this.desgloseIESSEmpleados.getColumns().clear();

        //? Anadimos las columnas y los empleados
        TableColumn<Employee, String> IDcolumn = new TableColumn<>("ID");
        TableColumn<Employee, String> NombreColumn = new TableColumn<>("Nombre");
        TableColumn<Employee, String> ApellidoColumn = new TableColumn<>("Apellido");
        TableColumn<Employee, String> SueldoColumn = new TableColumn<>("Sueldo");
        TableColumn<Employee, String> AporteIESS = new TableColumn<>("Aporte Al IESS");
        TableColumn<Employee, String> AporteRenta = new TableColumn<>("Aporte Impuesto Renta");
        TableColumn<Employee, String> AporteReserva = new TableColumn<>("Aporte Fondo de Reserva");
        TableColumn<Employee, String> AporteDecimoTercero = new TableColumn<>("Aporte Decimo Tercero");
        TableColumn<Employee, String> AporteDecimoCuarto = new TableColumn<>("Aporte Decimo Cuarto");
        TableColumn<Employee, String> sueldoLiquido = new TableColumn<>("Sueldo Liquido");
        TableColumn<Employee, String> sueldoTexto = new TableColumn<>("Sueldo En Palabras");

        //! Anadimos property mappings
        IDcolumn.setCellValueFactory(data -> new SimpleStringProperty(
                String.valueOf(data.getValue().getM_codigoEmployee())));
        NombreColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getM_nombreEmployee()));
        ApellidoColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getM_apellidoEmployee()));
        SueldoColumn.setCellValueFactory(data ->
        {
            String[] split = data.getValue().toCSVString().split(",");
            if (split.length == ToCSVSerializer.AMOUNT_PARSED_FIELDS_BASE_EMPLOYEE) {
                return new SimpleStringProperty(data.getValue().getM_sueldoEmployee().toString());
            } else {
                return new SimpleStringProperty(
                        Float.valueOf(data.getValue().getM_sueldoEmployee() + Float.parseFloat(split[7])).toString()
                );
            }
        });

        AporteIESS.setCellValueFactory(data  -> new SimpleStringProperty(data.getValue().getM_MapEntry("AporteIESS").toString()));
        AporteRenta.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getM_MapEntry("AporteRenta").toString()));
        AporteReserva.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getM_MapEntry("AporteFondoReserva").toString()));
        AporteDecimoTercero.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getM_MapEntry("AporteDecimoTercero").toString()));
        AporteDecimoCuarto.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getM_MapEntry("AporteDecimoCuarto").toString()));
        sueldoLiquido.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getM_SueldoMensual().toString()));
        sueldoTexto.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getM_SueldoTexto()));

        //! Anadimos los datos
        this.desgloseIESSEmpleados.getColumns().addAll(IDcolumn, NombreColumn, ApellidoColumn, SueldoColumn,
                AporteIESS, AporteRenta, AporteReserva, AporteDecimoTercero, AporteDecimoCuarto, sueldoLiquido, sueldoTexto);
        this.desgloseIESSEmpleados.getItems().addAll(employees);
    }
    private void updateTables(Employee mEmployee)
    {
        MenuItem item = new MenuItem(String.format(
                "[%s]:%s,%s", String.valueOf(mEmployee.getM_codigoEmployee()), mEmployee.getM_apellidoEmployee(),
                mEmployee.getM_nombreEmployee()));
        item.setUserData(mEmployee);
        this.selectorMenuItemsEmpleados.getMenus().getFirst().getItems().add(item);
        this.selectorMenuItemsEmpleados.getMenus().getFirst().getItems().forEach(menuItem -> {
            menuItem.setOnAction(actionEvent ->
            {
                // Clear previous content
                tableDesgloseSalario.getItems().clear();
                tableDesgloseSalario.getColumns().clear();

                // Create columns for each attribute of Employee
                TableColumn<Employee, String> IDcolumn = new TableColumn<>("ID");
                TableColumn<Employee, String> NombreColumn = new TableColumn<>("Nombre");
                TableColumn<Employee, String> ApellidoColumn = new TableColumn<>("Apellido");
                TableColumn<Employee, String> SueldoColumn = new TableColumn<>("Sueldo");
                TableColumn<Employee, String> sueldoLiquidoColumn = new TableColumn<>("Salario Liquido Mensual");
                TableColumn<Employee, String> billetes20 = new TableColumn<>("Billetes de 20$");
                TableColumn<Employee, String> billetes10 = new TableColumn<>("Billetes de 10$");
                TableColumn<Employee, String> billetes5 = new TableColumn<>("Billetes de 5$");
                TableColumn<Employee, String> billetes1 = new TableColumn<>("Billetes de 1$");
                TableColumn<Employee, String> salarioTexto = new TableColumn<>("Salario Total Textual");
                // Set how to populate each cell in the columns
                IDcolumn.setCellValueFactory(data -> new SimpleStringProperty(
                        String.valueOf(data.getValue().getM_codigoEmployee())));
                NombreColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getM_nombreEmployee()));
                ApellidoColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getM_apellidoEmployee()));
                SueldoColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getM_sueldoEmployee().toString()));
                sueldoLiquidoColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getM_SueldoMensual().toString()));
                Map<String, Integer> desglosePorBilletes = MoneyPresentationHelper.calculateBills(( (Employee) menuItem.getUserData()).getM_sueldoEmployee());
                billetes20.setCellValueFactory(data -> new SimpleStringProperty(desglosePorBilletes.get("20").toString()));
                billetes10.setCellValueFactory(data -> new SimpleStringProperty(desglosePorBilletes.get("10").toString()));
                billetes5.setCellValueFactory(data -> new SimpleStringProperty(desglosePorBilletes.get("5").toString()));
                billetes1.setCellValueFactory(data -> new SimpleStringProperty(desglosePorBilletes.get("1").toString()));
                salarioTexto.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getM_SueldoTexto()));

                tableDesgloseSalario.getColumns().addAll(IDcolumn, NombreColumn, ApellidoColumn,
                        SueldoColumn, sueldoLiquidoColumn, billetes20, billetes10, billetes5, billetes1, salarioTexto);
                this.tableDesgloseSalario.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

                //? Anadimos empleados
                tableDesgloseSalario.getItems().add((Employee) menuItem.getUserData());
            });
        });
    }

    private void removeEmployee(Employee mEmployee) {
        ListIterator<MenuItem> iterator = this.selectorMenuItemsEmpleados.getMenus().getFirst().getItems().listIterator();

        while (iterator.hasNext()) {
            MenuItem currentItem = iterator.next();

            if (currentItem.getUserData().equals(mEmployee)) {
                iterator.remove();
                break; // Assuming each Employee appears once
            }
        }
        this.selectorMenuItemsEmpleados.requestFocus();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}

