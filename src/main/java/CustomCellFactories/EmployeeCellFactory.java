package CustomCellFactories;

import EmployeeAbstraction.Employee;
import EmployeeAbstraction.EmployeeListWrapper;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class EmployeeCellFactory extends ListCell<Employee> {

    //? Instance Variables, Including Grid Pane
    private GridPane m_GridPaneHolder;

    public EmployeeCellFactory()
    {
        this.m_GridPaneHolder = new GridPane(50,10);
        this.m_GridPaneHolder.setAlignment(Pos.CENTER);
    }
    /**
     *
     * @param item The new item for the cell.
     * @param empty whether or not this cell represents data from the list. If it
     *        is empty, then it does not represent any domain data, but is a cell
     *        being used to render an "empty" row.
     */
    @Override
    protected void updateItem(Employee item, boolean empty) {
        //? first thing First, let us clear the original holder
        super.updateItem(item, empty);
        this.m_GridPaneHolder.getChildren().clear();

        if (empty || item == null) {
        }
        else
        {
            String[] strings = item.toCSVString().split(",");
            if (strings.length == 6){
                int counter = 0;
                //? Create the First Row of Items For the Employee, We need ID, Name, LName, Salary, Hire Date
                Label idLabel = new Label("ID Empleado");
                Label nameLabel = new Label("Nombre Empleado");
                Label lNameLabel = new Label("Apellido Empleado");
                Label salaryLabel = new Label("Salario Empleado");
                Label hireDateLabel = new Label("Fecha Contratacion Empleado");
                ArrayList<Label> labels = new ArrayList<>();
                Collections.addAll(labels, idLabel, nameLabel, lNameLabel, salaryLabel, hireDateLabel);
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


                //? Create the Second row containing the actual values
                Label idValue = new Label(String.valueOf(item.getM_codigoEmployee()));
                Label nameValue = new Label(item.getM_nombreEmployee());
                Label lNameValue = new Label(item.getM_apellidoEmployee());
                Label salaryValue = new Label(String.valueOf(item.getM_sueldoEmployee()));
                Label hireDateValue = new Label(new SimpleDateFormat("dd/MM/yyy").format(item.getM_DateEnNumero()));
                ArrayList<Label> labels1 = new ArrayList<>();
                Collections.addAll(labels1, idValue, nameValue, lNameValue, salaryValue, hireDateValue);
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
