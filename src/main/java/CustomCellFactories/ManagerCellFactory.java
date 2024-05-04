package CustomCellFactories;

/*======================================================================================================================
 ?                                                     ABOUT
 * @author         :  Santiago Arellano
 * @repo           :  CMP2103 - Rol De Pagos
 * @description    :  Definicion e Implementacion de la Clase ManagerCellFactory para mostrar managers en listViews
 ====================================================================================================================**/

import EmployeeAbstraction.Employee;
import EmployeeAbstraction.Manager;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class ManagerCellFactory extends ListCell<Employee> {

    private GridPane m_GridPaneHolder;

    public ManagerCellFactory()
    {
        this.m_GridPaneHolder = new GridPane(50,10);
        this.m_GridPaneHolder.setAlignment(Pos.CENTER);
    }

    @Override
    protected void updateItem(Employee item, boolean empty) {
        //? Limpiamos Holder Original
        super.updateItem(item, empty);
        this.m_GridPaneHolder.getChildren().clear();

        if (empty || item == null) {
        }
        else
        {
            String[] strings = item.toCSVString().split(",");
            if (strings.length == 8) {
                int counter = 0;
                //? Creamos la primera fila con los campos que se van a colocar
                Label idLabel = new Label("ID Empleado");
                Label nameLabel = new Label("Nombre Empleado");
                Label lNameLabel = new Label("Apellido Empleado");
                Label salaryLabel = new Label("Salario Empleado");
                Label hireDateLabel = new Label("Fecha Contratacion Empleado");
                Label tituloNivelLabel = new Label("Titulacion Empleado");
                Label bonoLabel = new Label("Comision Empleado");
                ArrayList<Label> labels = new ArrayList<>();
                Collections.addAll(labels, idLabel, nameLabel, lNameLabel, salaryLabel, hireDateLabel, tituloNivelLabel,
                        bonoLabel);
                labels.forEach(consumer ->
                {
                    consumer.setTextAlignment(TextAlignment.CENTER);
                    consumer.setTextFill(Color.valueOf("#636161"));
                    consumer.setWrapText(true);
                    consumer.setFont(Font.font("Bookshelf Symbol 7", 14));

                });
                for (; counter < labels.size(); counter++) {
                    this.m_GridPaneHolder.add(labels.get(counter), counter, 0);
                    GridPane.setHalignment(this.m_GridPaneHolder.getChildren().get(counter), HPos.CENTER);
                    GridPane.setValignment(this.m_GridPaneHolder.getChildren().get(counter), VPos.CENTER);
                }

                //? Creamos la segunda fila con los campos extraidos del manager
                Label nameValue = new Label(strings[0]);
                Label lNameValue = new Label(strings[1]);
                Label idValue = new Label(String.valueOf(strings[2]));
                Label salaryValue = new Label(String.valueOf(strings[4]));
                Label hireDateValue = new Label(new SimpleDateFormat("dd/MM/yyy").format(Long.valueOf(strings[3])));
                Label tituloValue = new Label();
                switch (strings[6]) {
                    case Manager.MAESTRIA_CONSTANT: {
                        tituloValue.setText("Maestria");
                        break;
                    }
                    case Manager.DOCTORADO_CONSTANT: {
                        tituloValue.setText("Doctorado");
                        break;
                    }
                    case Manager.TERCER_NIVEL_CONSTANT: {
                        tituloValue.setText("Tercer Nivel");
                        break;
                    }
                }
                Label bonoValue = new Label(String.valueOf(strings[7]));
                ArrayList<Label> labels1 = new ArrayList<>();
                Collections.addAll(labels1, idValue, nameValue, lNameValue, salaryValue, hireDateValue,
                        tituloValue, bonoValue);
                labels1.forEach(consumer ->
                {
                    consumer.setTextAlignment(TextAlignment.CENTER);
                    consumer.setTextFill(Color.valueOf("#000000"));
                    consumer.setWrapText(true);
                    consumer.setFont(Font.font("Bookshelf Symbol 7", 14));
                });

                for (int i = 0; i < labels1.size(); i++, counter++) {
                    this.m_GridPaneHolder.add(labels1.get(i), i, 1);
                    GridPane.setHalignment(this.m_GridPaneHolder.getChildren().get(counter), HPos.CENTER);
                    GridPane.setValignment(this.m_GridPaneHolder.getChildren().get(counter), VPos.CENTER);

                }



            }
        }
        setGraphic(this.m_GridPaneHolder);
    }
}
