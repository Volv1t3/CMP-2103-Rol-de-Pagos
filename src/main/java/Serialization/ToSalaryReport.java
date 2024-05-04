package Serialization;

/*======================================================================================================================
 ?                                                     ABOUT
 * @author         :  Santiago Arellano
 * @repo           :  CMP2103 - Rol De Pagos
 * @description    :  Implementacion de Clase Auxiliar de Serializacion Por JSON
 *====================================================================================================================*/


import CustomExceptions.FileIsEmptyAlert;
import CustomExceptions.FileNotFoundAlert;
import CustomExceptions.IncorrectCSVFormat;
import EmployeeAbstraction.Employee;
import EmployeeAbstraction.EmployeeListWrapper;
import SalaryCalculations.MoneyPresentationHelper;
import SalaryCalculations.MonthlySalaryHelper;

import javax.naming.directory.InvalidAttributesException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ToSalaryReport {

    public static boolean serializeSalaryDesgloseToFile(File e_OutputFile, List<Employee> e_EmployeeList)

        throws
                FileNotFoundAlert,
            FileNotFoundException
    {
            try{
                if (!e_OutputFile.exists()){e_OutputFile.createNewFile();}
            }
            catch(IOException e)
            {
                throw new FileNotFoundAlert();
            }

            try (PrintWriter pt = new PrintWriter(e_OutputFile))
            {
                pt.printf("%-15s %-20s %-20s %-20s %-20s %-20s %-10s %-10s %-10s %-10s %-50s%n", "ID Empleado", "Nombre Empleado", "Apellido Empleado", "Salario Empleado",
                        "Salario Liquido","Desglose:", "20","10","5","1", "Salario Textual");

                for(Employee empleado: e_EmployeeList)
                {
                    Map<String, Integer> salarioDesplose = MoneyPresentationHelper.calculateBills(empleado.getM_sueldoEmployee());

                    pt.printf("%-15s %-20s %-20s %-20s %-20s %-20s %-10s %-10s %-10s %-10s %-50s%n",
                            empleado.getM_codigoEmployee(), empleado.getM_nombreEmployee(), empleado.getM_apellidoEmployee(),
                            empleado.getM_sueldoEmployee(), empleado.getM_SueldoMensual(), "Desglose:",
                            salarioDesplose.get("20"), salarioDesplose.get("10"), salarioDesplose.get("5"), salarioDesplose.get("1"),
                            MoneyPresentationHelper.transformMoneyToText(empleado.getM_sueldoEmployee()));
                }

            }
            catch(FileNotFoundException e)
            {
                throw new FileNotFoundException();
            }
            return true;


    }


    public static boolean serializeTributaryDesgloseToFile(File e_OutputFile, List<Employee> e_EmployeeList)
            throws FileNotFoundAlert, FileNotFoundException {
        try{
            if (!e_OutputFile.exists()){e_OutputFile.createNewFile();}
        }
        catch(IOException e)
        {
            throw new FileNotFoundAlert();
        }

        try (PrintWriter pt = new PrintWriter(e_OutputFile))
        {
            pt.printf("%-15s %-20s %-20s%-20s %-30s %-30s %-30s %-30s\n", "ID Empleado", "Nombre Empleado","Apellido Empleado", "Aporte Al IESS", "Aporte Impuesto Renta",
                    "Aporte Fondo De Reserva","Aporte Decimo Tercero", "Aporte Decimo Cuarto");

            for(Employee empleado: e_EmployeeList)
            {
                Map<String, Integer> salarioDesplose = MoneyPresentationHelper.calculateBills(empleado.getM_sueldoEmployee());

                pt.printf("%-15s %-20s %-20s%-20s %-30s %-30s %-30s %-30s\n",
                        empleado.getM_codigoEmployee(), empleado.getM_nombreEmployee(), empleado.getM_apellidoEmployee(),
                        empleado.getM_MapEntry("AporteIESS"), empleado.getM_MapEntry("AporteRenta"),
                        empleado.getM_MapEntry("AporteFondoReserva"), empleado.getM_MapEntry("AporteDecimoTercero"),
                        empleado.getM_MapEntry("AporteDecimoCuarto")
                        );
            }

        }
        catch(FileNotFoundException e)
        {
            throw new FileNotFoundException();
        }
        return true;
    }
}
