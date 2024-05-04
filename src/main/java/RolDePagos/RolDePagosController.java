package RolDePagos;

/*======================================================================================================================
 ?                                                     ABOUT
 * @author         :  Santiago Arellano
 * @repo           :  CMP2103 - Rol De Pagos
 * @description    :  Definicion e Implementacion del controlador visual de la Aplicacion Rol De Pagos
 *====================================================================================================================*/



import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

import java.util.*;

public class RolDePagosController {

    // ! Prívate variables: Seccion Employee Control
    @FXML
    private TextField mColaboradorSalarioTextField;
    @FXML
    private ImageView mColaboratorIcon;
    @FXML
    private RadioButton mColaboradorRadioButton;
    @FXML
    private RadioButton mGerenteRadioButton;
    @FXML
    private Text mWelcomeColaboratorMessage;
    @FXML
    private TextField mColaboradorNameTextField;
    @FXML
    private TextField mManagerBonificacion;
    @FXML
    private ComboBox mManagerTituloNivelComboBox;
    @FXML
    private MenuItem mSortingSueldoYBonoMenuItem;
    @FXML
    private ToggleGroup colaboradorSelector;
    @FXML
    private Button addEmployeeButton;
    @FXML
    private Button deleteEmployeeButton;

    public void initialize()
    {
        //? Initialization Methods for Section Employee Control
        // * Initialize the Radio Button's user data
        this.mColaboradorRadioButton.setUserData(Integer.valueOf(0));
        this.mGerenteRadioButton.setUserData(Integer.valueOf(1));

        this.colaboradorSelector.selectedToggleProperty().addListener(invalidationListener ->
        {
           switch (Integer.parseInt(this.colaboradorSelector.getSelectedToggle().getUserData().toString()))
           {
               case 0 /*This is the case for Collaborator*/:{

                   //! Paso base: Desactivamos tanto el combobox del titulo universitario y el de la comision
                   this.mManagerTituloNivelComboBox.setDisable(true);
                   this.mManagerTituloNivelComboBox.getSelectionModel().clearSelection();
                   this.mManagerBonificacion.setDisable(true);
                   this.mManagerBonificacion.clear();

                   //! Paso Base: Desactivamos el sorting propio de la clase manager
                   this.mSortingSueldoYBonoMenuItem.setDisable(true);
                   break;
               }
               case 1 /*This is the case for Managers*/:
               {
                   //! Paso Base: Activamos el combobox del titulo universitario y el de la comision
                   this.mManagerBonificacion.setDisable(false);
                   this.mManagerTituloNivelComboBox.setDisable(false);
                   this.mSortingSueldoYBonoMenuItem.setDisable(false);
                   break;
               }
               default :
               {
                   throw new IllegalStateException("You are not supposed to select something different on the selector for type of collaborator");
               }
           }
        });

        // * Inicializamos el ComboBox con una lista de strings
        List<String> internalList= new ArrayList<>();
        internalList.addAll(Arrays.stream(("Titulo Tercer Nivel;Maestria;Doctorado").split(";")).toList());
        ObservableList<String> internalFXarray = FXCollections.observableArrayList(internalList);
        this.mManagerTituloNivelComboBox.setItems(internalFXarray);


        // * Interconnect the registry of a name to the message up above
        this.mColaboradorNameTextField.textProperty().addListener(invalidationListener ->
        {
            this.mWelcomeColaboratorMessage.setText("Datos Sobre #"+ this.mColaboradorNameTextField.getText().toUpperCase());

        });



    }

    @FXML
    public void checkIfInputIsText(KeyEvent event)
    {
        TextField dummyTextField = (TextField) event.getSource();

        if (!(dummyTextField.getText().matches("[A-Za-z]{0,20}?")))
        {
            Alert nonAlphabeticAlert = new Alert(Alert.AlertType.WARNING);
            nonAlphabeticAlert.setTitle("Texto Ingresado es Incorrecto");
            nonAlphabeticAlert.setHeaderText("El Texto Ingresado No Es Alfabetico ");
            nonAlphabeticAlert.setContentText("El texto que fue ingresado en el campo, no corresponde a un ingreso alfabetico por lo que no fue aceptado.\n" +
                    "Por favor, revise sus datos y asegurese de ingresar solo un nombre por empleado.");
            dummyTextField.clear();
            dummyTextField.requestFocus();
            nonAlphabeticAlert.showAndWait();
        }
    }

    @FXML
    public void checkIfInputIsNumeric(KeyEvent event)
    {
        TextField dummyTextField = (TextField) event.getSource();
        if (!(dummyTextField.getText().matches("[0-9]{0,20}?")))
        {
            Alert nonNumericAlert = new Alert(Alert.AlertType.WARNING);
            nonNumericAlert.setTitle("Texto Ingresado es Incorrecto");
            nonNumericAlert.setHeaderText("El Texto Ingresado No Es Numerico");
            nonNumericAlert.setContentText("El texto que fue ingresado en el campo, no corresponde a un ingreso numerico por lo que no fue aceptado.\n" +
                    "Por favor, revise sus datos y asegurese de ingresar un valor numerico.");
            dummyTextField.clear();
            dummyTextField.requestFocus();
            nonNumericAlert.showAndWait();
        }
    }

}

