package SalaryCalculations;
/*======================================================================================================================
 ?                                                     ABOUT
 * @author         :  Santiago Arellano
 * @repo           :  CMP2103 - Rol De Pagos
 * @description    :  Definicion e Implementacion de la clase Helper para aportes salariales del empleador al SRI en
 *                    base al salario del empleado
 =====================================================================================================================*/

import EmployeeAbstraction.Employee;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Map;

public class MonthlySalaryHelper {

    /**
     * Este metodo calcula el aporte total al IESS basado en el salario de un empleado.
     *
     * @param e_SalarioEmpleado El salario del empleado. Debe ser un valor de tipo Float.
     * @return Un objeto Map.Entry<String, Integer> que representa el aporte total al IESS. La clave usada en el objeto entry es "AporteIESS".
     */
    private static Map.Entry<String, Integer> porcentajeAporteIESS(Float e_SalarioEmpleado)
    {
        BigDecimal aporteIESSpersonal = BigDecimal.valueOf(e_SalarioEmpleado).multiply(BigDecimal.valueOf(0.0945));
        BigDecimal aporteIESSPatronal = BigDecimal.valueOf(e_SalarioEmpleado).multiply(BigDecimal.valueOf(0.1115));
        BigDecimal aporteTotal = aporteIESSpersonal.add(aporteIESSPatronal);
        BigInteger aporteRoundedOff = BigInteger.valueOf(Math.round(aporteTotal.floatValue()));

        return Map.entry("AporteIESS", aporteRoundedOff.intValue());
    }

    /**
     * Este metodo calcula la retencion del impuesto a la renta basado en los ingresos totales de un empleado.
     *<br>
     * <p>
     * El metodo funciona de la siguiente manera : <br>
     * - Inicialmente, se inicializa un BigDecimal 'retencionCalculada' con el valor 0.<br>
     * - Se evaluan los ingresos totales del empleado dentro de varios rangos de valores.<br>
     * - Para cada rango de valor, se calcula un porcentaje correspondiente de retencion y luego se añade a 'retencionCalculada'.<br>
     * - Este proceso se repite hasta que se determina el rango correspondiente a los ingresos totales del empleado.<br>
     * - Finalmente, 'retencionCalculada' se redondea al entero más cercano y se convierte a un objeto Integer.<br>
     *</p>
     * @param e_IngresosTotalesEmpleado Los ingresos totales del empleado durante el periodo de calculo.
     * Debe ser un valor de tipo Float. <br>
     * @return Un objeto {@code Map.Entry<String, Integer>} que representa la retencion del impuesto a la renta.
     * La clave utilizada en el objeto entry es "AporteRenta".
     */
    private static Map.Entry<String, Integer> porcentajeRetencionImpuestoRenta(Float e_IngresosTotalesEmpleado)
    {
        Float ingresosTotalesAnuales = BigDecimal.valueOf(e_IngresosTotalesEmpleado).multiply(BigDecimal.valueOf(12)).floatValue();

        BigDecimal retencionCalculada = BigDecimal.valueOf(0f);
        if (0f < ingresosTotalesAnuales && ingresosTotalesAnuales < 11722f) {retencionCalculada.add(BigDecimal.valueOf(0));}
        else if (11722f < ingresosTotalesAnuales && ingresosTotalesAnuales < 14930f) {
            retencionCalculada = retencionCalculada.add(
                    BigDecimal.valueOf(ingresosTotalesAnuales).multiply(BigDecimal.valueOf(0.05)));}
        else if (14930f < ingresosTotalesAnuales && ingresosTotalesAnuales < 25638f)
        {
            retencionCalculada = retencionCalculada.add(BigDecimal.valueOf(ingresosTotalesAnuales).
                    subtract(BigDecimal.valueOf(160f)).
                    multiply(BigDecimal.valueOf(0.05)));
        }
        else if (25638f < ingresosTotalesAnuales && ingresosTotalesAnuales < 33738f)
        {
            retencionCalculada= retencionCalculada.add(BigDecimal.valueOf(ingresosTotalesAnuales).
                    subtract(BigDecimal.valueOf(606f)).
                    multiply(BigDecimal.valueOf(0.10)));
        }
        else if (33738f < ingresosTotalesAnuales && ingresosTotalesAnuales < 44721f)
        {
            retencionCalculada= retencionCalculada.add(BigDecimal.valueOf(ingresosTotalesAnuales).
                    subtract(BigDecimal.valueOf(1356f)).
                    multiply(BigDecimal.valueOf(0.12)));
        }
        else if (44721f < ingresosTotalesAnuales && ingresosTotalesAnuales < 59537f)
        {
            retencionCalculada= retencionCalculada.add(BigDecimal.valueOf(ingresosTotalesAnuales).
                    subtract(BigDecimal.valueOf(2571f)).
                    multiply(BigDecimal.valueOf(0.20)));
        }
        else if (59537f < ingresosTotalesAnuales && ingresosTotalesAnuales < 79338f)
        {
            retencionCalculada= retencionCalculada.add(BigDecimal.valueOf(ingresosTotalesAnuales).
                    subtract(BigDecimal.valueOf(4768f)).
                    multiply(BigDecimal.valueOf(0.25)));
        }
        else if (79338f < ingresosTotalesAnuales && ingresosTotalesAnuales < 105580f)
        {
            retencionCalculada= retencionCalculada.add(BigDecimal.valueOf(ingresosTotalesAnuales).
                    subtract(BigDecimal.valueOf(14427f)).
                    multiply(BigDecimal.valueOf(0.35)));
        }
        else if ( 105580f < e_IngresosTotalesEmpleado)
        {
            retencionCalculada= retencionCalculada.add(BigDecimal.valueOf(ingresosTotalesAnuales).
                    subtract(BigDecimal.valueOf(23594f)).
                    multiply(BigDecimal.valueOf(0.37)));
        }

        return Map.entry("AporteRenta", BigInteger.valueOf(Math.round(retencionCalculada.floatValue())).intValue());
    }


    /**
     * Este metodo calcula el porcentaje del fondo de reserva basado en el sueldo de un empleado.
     *
     * <p>
     * El funcionamiento interno del metodo es el siguiente:
     * <br>
     * - Se calcula un BigDecimal, 'porcentajeCalculado', en el que se multiplica el sueldo del empleado por el numero 0.0833.
     * - Se redondea 'porcentajeCalculado' al entero mas cercano y se convierte en un objeto BigInteger llamado 'porcentajeTruncated'.<br>
     * </p>
     * @param e_SueldoEmpleado representa el sueldo del empleado. Debe ser un valor de tipo Float.
     * @return Un objeto Map.Entry<String, Integer> que representa el porcentaje del fondo de reserva calculado.
     *         La clave usada en el objeto entry es "AporteFondoReserva". El valor es el resultado de la operacion calculada.
     *
     */
    private static Map.Entry<String, Integer> porcentajeFondoDeReserva(Float e_SueldoEmpleado)
    {
        BigDecimal porcentajeCalculado = BigDecimal.valueOf(e_SueldoEmpleado).multiply(BigDecimal.valueOf(0.0833));
        BigInteger porcentajeTruncated = BigInteger.valueOf(Math.round(porcentajeCalculado.floatValue()));

        return Map.entry("AporteFondoReserva", porcentajeTruncated.intValue());
    }

    /**
     * Este metodo calcula el porcentaje del decimo tercero basado en los ingresos totales de un empleado.
     * <p>
     * El funcionamiento interno del metodo es el siguiente:
     * - Primero se calcula un BigDecimal, 'aporteDecimoTercero', mediante la division de los ingresos totales del empleado entre 12.
     * - Luego, 'aporteDecimoTercero' se redondea al entero mas cercano y se convierte en un objeto BigInteger llamado 'aporteDecimoTerceroTruncated'.
     *</p>
     * @param e_IngresosTotalesEmpleado representa los ingresos totales del empleado. Debe ser un valor de tipo Float.
     * @return Un objeto Map.Entry<String, Integer> que representa el porcentaje del decimo tercero calculado.
     *         La clave usada en el objeto entry es "AporteDecimoTercero". El valor es el resultado de la operacion calculada.
     */
    private static Map.Entry<String, Integer> porcentajeDecimoTercero(Float e_IngresosTotalesEmpleado)
    {
        BigDecimal aporteDecimoTercero = BigDecimal.valueOf(e_IngresosTotalesEmpleado).divide(BigDecimal.valueOf(12f), RoundingMode.CEILING);
        BigInteger aporteDecimoTerceroTruncated = BigInteger.valueOf(Math.round(aporteDecimoTercero.floatValue()));

        return Map.entry("AporteDecimoTercero", aporteDecimoTerceroTruncated.intValue());
    }

    private static Map.Entry<String, Integer> porcentajeDecimoCuarto()
    {
        return Map.entry("AporteDecimoCuarto", 460 / 12 );
    }

    public static void addCalculatedEntries(Employee e_Empleado)
    {
        String[] strings = e_Empleado.toCSVString().split(",");
        if (strings.length == 6)
        {
            e_Empleado.setM_MapEntry(porcentajeAporteIESS(e_Empleado.getM_sueldoEmployee()));
            e_Empleado.setM_MapEntry(porcentajeRetencionImpuestoRenta(e_Empleado.getM_sueldoEmployee()));
            e_Empleado.setM_MapEntry(porcentajeFondoDeReserva(e_Empleado.getM_sueldoEmployee()));
            e_Empleado.setM_MapEntry(porcentajeDecimoTercero(e_Empleado.getM_sueldoEmployee()));
            e_Empleado.setM_MapEntry(porcentajeDecimoCuarto());
        }
        else if (strings.length == 8)
        {
            e_Empleado.setM_MapEntry(porcentajeAporteIESS(e_Empleado.getM_sueldoEmployee()));
            e_Empleado.setM_MapEntry(porcentajeRetencionImpuestoRenta(e_Empleado.getM_sueldoEmployee() + Float.parseFloat(strings[7])));
            e_Empleado.setM_MapEntry(porcentajeFondoDeReserva(e_Empleado.getM_sueldoEmployee()));
            e_Empleado.setM_MapEntry(porcentajeDecimoTercero(e_Empleado.getM_sueldoEmployee() + Float.parseFloat(strings[7])));
            e_Empleado.setM_MapEntry(porcentajeDecimoCuarto());
        }
    }

}
