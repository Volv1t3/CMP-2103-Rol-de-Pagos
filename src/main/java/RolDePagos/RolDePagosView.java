package RolDePagos;

import CustomCellFactories.EmployeeCellFactory;
import CustomCellFactories.ManagerCellFactory;
import EmployeeAbstraction.Employee;
import EmployeeAbstraction.EmployeeListWrapper;
import EmployeeAbstraction.Manager;
import Serialization.ToCSVSerializer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.directory.InvalidAttributesException;
import java.io.File;
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
    @Override
    public void init() throws Exception {
        //? Vamos A Cargar Un Archivo CSV Directamente a la aplicacion para su manipulacion
        this.m_WrapperList =  ToCSVSerializer.deserializeFromFile(new File("src/main/java/RolDePagos/Results.csv"));
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
        File rootcontent = new File("src/main/java/RolDePagos/RolDePagosView.fxml");
        Scene root = FXMLLoader.load((rootcontent.toURI().toURL()));
        primaryStage.setScene(root); primaryStage.show();

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
                    if (!(this.mColaboradorNameTextField.getText().isEmpty())) {
                        try {
                            dummyEmployee.setM_nombreEmployee(this.mColaboradorNameTextField.getText());
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
                    } else {
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

                    } else {
                        this.generalAlert.setTitle("Campo Vacio");
                        this.generalAlert.setHeaderText("El campo codigo esta vacio");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un codigo por empleado.");
                        error_Flag = true;
                        this.generalAlert.showAndWait();
                    }
                    //! Revisamos Selector de Fechas
                    try {
                        dummyEmployee.setM_fechaContratacionEmployee(Date.valueOf(this.mColaboradorFechaContratoDatePicker.getValue()).getTime());

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
                    } else {
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

                    } else {
                        this.generalAlert.setTitle("Campo Vacio");
                        this.generalAlert.setHeaderText("El campo codigo esta vacio");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar un codigo por empleado.");
                        error_Flag = true;
                        this.generalAlert.showAndWait();
                    }
                    //! Revisamos Selector de Fechas
                    try {
                        dummyManager.setM_fechaContratacionEmployee(Date.valueOf(this.mColaboradorFechaContratoDatePicker.getValue()).getTime());

                    } catch (InvalidAttributeValueException e) {
                        this.generalAlert.setTitle("Fecha Invalida");
                        this.generalAlert.setHeaderText("La fecha ingresada no es valida");
                        this.generalAlert.setContentText("Por favor, revise sus datos y asegurese de ingresar una fecha valida por empleado.");
                        this.generalAlert.showAndWait();
                        error_Flag = true;
                    } catch (NullPointerException e) {
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
                    } else {
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
                                dummyManager.setM_TituloNivelManager("Maestria");
                                break;
                            }
                            case "Doctorado": {
                                dummyManager.setM_TituloNivelManager("Doctorado");
                                break;
                            }
                            case "Titulo Tercer Nivel": {
                                dummyManager.setM_TituloNivelManager("Titulo Tercer Nivel");
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
               }
               else if (this.mGerenteRadioButton.isSelected())
               {
                   this.ManagerHolder.remove(this.mListViewColaboradores.getSelectionModel().getSelectedItem());
                   this.m_WrapperList.getM_employees().remove(this.mListViewColaboradores.getSelectionModel().getSelectedItem());
                   this.mListVIewColaboradoresManagers.requestFocus();
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

        //? Anadimos mouse movements
        this.mListViewColaboradores.setOnMouseClicked(mouseClicked ->
        {
            if (mouseClicked.getClickCount() == 2)
            {
                this.mListViewColaboradores.getSelectionModel().clearSelection();
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
    }




    public static void main(String[] args)
    {
        launch(args);
    }
}
