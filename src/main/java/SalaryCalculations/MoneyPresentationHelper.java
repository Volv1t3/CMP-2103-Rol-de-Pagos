package SalaryCalculations;

/*======================================================================================================================
 ?                                                     ABOUT
 * @author         :  Santiago Arellano
 * @repo           :  CMP2103 - Rol De Pagos
 * @description    :  Definicion e Implementacion de la clase Helper para presentacion textual y en desglose de billetes
 *                    del sueldo de un empleado
======================================================================================================================*/

import java.math.BigInteger;
import java.util.Map;

public class MoneyPresentationHelper {

    private static final Map<Integer, String> enterosMapping = Map.ofEntries(
            Map.entry(0, ""),
            Map.entry(1, " y Uno"),
            Map.entry(2, " y Dos"),
            Map.entry(3, " y Tres"),
            Map.entry(4, " y Cuatro"),
            Map.entry(5, " y Cinco"),
            Map.entry(6, " y Seis"),
            Map.entry(7, " y Siete"),
            Map.entry(8, " y Ocho"),
            Map.entry(9, " y Nueve")
    );
    private static final Map<Integer, String> decimasMapping = Map.ofEntries(
            Map.entry(0, ""),
            Map.entry(1, "Diez"),
            Map.entry(2, "Veinte"),
            Map.entry(3, "Treinta"),
            Map.entry(4, "Cuarenta"),
            Map.entry(5, "Cincuenta"),
            Map.entry(6, "Sesenta"),
            Map.entry(7, "Setenta"),
            Map.entry(8, "Ochenta"),
            Map.entry(9, "Noventa")
    );
    private static final Map<Integer, String> centenasMapping = Map.ofEntries(
            Map.entry(0, ""),
            Map.entry(1, "Ciento"),
            Map.entry(2, "Doscientos"),
            Map.entry(3, "Trescientos"),
            Map.entry(4, "Cuatrocientos"),
            Map.entry(5, "Quinientos"),
            Map.entry(6, "Seiscientos"),
            Map.entry(7, "Setecientos"),
            Map.entry(8, "Ochocientos"),
            Map.entry(9, "Novecientos")
    );
    private static final Map<Integer, String> milesMapping = Map.ofEntries(
            Map.entry(0, ""),
            Map.entry(1, "Mil"),
            Map.entry(2, "Dos Mil"),
            Map.entry(3, "Tres Mil"),
            Map.entry(4, "Cuatro Mil"),
            Map.entry(5, "Cinco Mil"),
            Map.entry(6, "Seis Mil"),
            Map.entry(7, "Siete Mil"),
            Map.entry(8, "Ocho Mil"),
            Map.entry(9, "Nueve Mil")
    );

    /**
     * Este metodo convierte un numero flotante que representa un salario en una representacion textual de dicho salario.
     * <br>
     * Internamente, este metodo toma el salario proporcionado y lo redondea al entero mas cercano. Luego, descompone ese salario redondeado
     * en unidades, decimas, centenas y millares. El metodo crea un StringBuilder y para cada componente del salario (unidades, decimas,
     * centenas y millares), verifica si no es cero. Si no es cero, se recupera su representacion textual correspondiente a traves de los
     * mapeos de enteros, decimas, centenas y miles y se adjunta a StringBuilder.
     *<br>
     * @param e_Salary la cantidad de dinero que necesita ser transformada a texto, expresada como un Float.
     * Debe ser un numero positivo.
     * @return una cadena que contiene la representacion textual del salario.
     */
    public static String transformMoneyToText(Float e_Salary) {
        BigInteger roundedUpSalary = BigInteger.valueOf(Math.round(e_Salary));
        int mUnidades = roundedUpSalary.intValue() % 10;
        int mDecimas = (roundedUpSalary.intValue() / 10) % 10;
        int mCentenas = (roundedUpSalary.intValue() / 100) % 10;
        int mMillares = (roundedUpSalary.intValue() / 1000) % 10;

        StringBuilder result = new StringBuilder();

        if (mMillares != 0) {
            result.append(milesMapping.get(mMillares)).append(" ");
        }

        if (mCentenas != 0) {
            if (mCentenas == 1 && mDecimas == 0 && mUnidades == 0) {
                result.append("Cien ");
            } else {
                result.append(centenasMapping.get(mCentenas)).append(" ");
            }
        }

        if (mDecimas != 0) {
            switch (mDecimas) {
                case 2:
                    if (mUnidades != 0) {
                        result.append(decimasMapping.get(mDecimas)).append(enterosMapping.get(mUnidades)).append(" ");
                    } else {
                        result.append(decimasMapping.get(mDecimas)).append(" ");
                    }
                    break;
                default:
                    result.append(decimasMapping.get(mDecimas)).append(" ");
                    if (mUnidades != 0) {
                        result.append(enterosMapping.get(mUnidades)).append(" ");
                    }
                    break;
            }
        } else if (mUnidades != 0) {
            result.append(enterosMapping.get(mUnidades)).append(" ");
        }

        return result.toString().trim();
    }

    public static Map<String, Integer> calculateBills(Float e_Salary) {

        int amount = BigInteger.valueOf(Math.round((e_Salary))).intValue();
        int numberOf20 = amount / 20;
        int remainingAmount = amount % 20;

        int numberOf10 = remainingAmount / 10;
        remainingAmount %= 10;

        int numberOf5 = remainingAmount / 5;
        remainingAmount %= 5;

        int numberOf1 = remainingAmount;

        Map<String,Integer> results = Map.ofEntries(
                Map.entry("20", numberOf20),
                Map.entry("10", numberOf10),
                Map.entry("5", numberOf5),
                Map.entry("1", numberOf1)
        );

        return results;
    }

    public static void main(String[] args)
    {
        System.out.println(transformMoneyToText(574f));
        System.out.println(calculateBills(574f).get("20"));
    }
}

