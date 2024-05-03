import EmployeeAbstraction.Employee;
import EmployeeAbstraction.EmployeeListWrapper;
import EmployeeAbstraction.Manager;
import SalaryCalculations.MonthlySalaryHelper;
import Serialization.ToBinarySerializer;
import Serialization.ToCSVSerializer;
import Serialization.ToJsonSerializer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;

public class TestBinarySerialization {

    public static void main(String[] args)
    {
        try {
            Employee employee1 = new Employee("Luis", "Arellano", 1,
                    LocalDate.of(2021, 12, 10).toEpochSecond(LocalTime.now(ZoneId.systemDefault()), ZoneOffset.UTC),
                    1200);
            Employee employee2 = new Employee("Sebastian", "Arellano", 2,
                    LocalDate.of(2022, 4, 10).toEpochSecond(LocalTime.now(ZoneId.systemDefault()), ZoneOffset.UTC),
                    1000);
            Employee employee3 = new Employee("Santiago", "Arellano", 3,
                    LocalDate.of(2022, 6, 10).toEpochSecond(LocalTime.now(ZoneId.systemDefault()), ZoneOffset.UTC),
                    2000);
            Manager employee4 = new Manager(Manager.MAESTRIA_CONSTANT, 1400, "Luis", "Avellano", 4, LocalDate.of(2022, 8, 10).toEpochSecond(LocalTime.now(ZoneId.systemDefault()), ZoneOffset.UTC),
                    3000);
            Manager employee5 = new Manager(Manager.TERCER_NIVEL_CONSTANT, 1600, "Sofia", "Jaramillo", 5, LocalDate.of(2022, 9, 10).toEpochSecond(LocalTime.now(ZoneId.systemDefault()), ZoneOffset.UTC),
                    3240);
            Manager employee6 = new Manager(Manager.DOCTORADO_CONSTANT, 1800, "Juan", "Perez", 6, LocalDate.of(2022, 10, 10).toEpochSecond(LocalTime.now(ZoneId.systemDefault()), ZoneOffset.UTC),
                    3000);




            EmployeeListWrapper newWrapper = new EmployeeListWrapper();
            newWrapper.setM_employees(new ArrayList<Employee>());
            Collections.addAll(newWrapper.getM_employees(), employee1, employee2, employee3, employee4, employee5, employee6);
            for (Employee mEmployee : newWrapper.getM_employees()) {
                MonthlySalaryHelper.addCalculatedEntries(mEmployee);
            }
            ToBinarySerializer.serializeToBinary(new File("./results.txt"), newWrapper);
            File errorProneFile = new File("./errorProneFile.json");
            EmployeeListWrapper deserializedStructure = ToBinarySerializer.deserializeFromBinary(new File("./results.txt"));
            System.out.println("Deserialized Binary Data");
            deserializedStructure.getM_employees().forEach(System.out::println);
            //ToBinarySerializer.deserializeFromBinary(errorProneFile);


            ToJsonSerializer.serializeToFile(errorProneFile, newWrapper);
            EmployeeListWrapper wrapper = ToJsonSerializer.deserializeFromFile(errorProneFile);
            System.out.println("Deserialized JSON Data");
            wrapper.getM_employees().forEach(System.out::println);

            ToJsonSerializer.serializeToFile(errorProneFile, newWrapper);
            //ployeeListWrapper wrapper1 = ToJsonSerializer.deserializeFromFile(new File("./results.txt")); //! This line should always evaluate to an
            //! Illegal StateException runtime error, this will be eaten by the UI later on

            System.out.println("Deserialized XML Data");
            JAXBContext jaxbContext = JAXBContext.newInstance(EmployeeListWrapper.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            File errorProneFileXML = new File("./errorProneFile.xml");
            marshaller.marshal(newWrapper, errorProneFileXML);
            EmployeeListWrapper marshalledList = (EmployeeListWrapper) jaxbContext.createUnmarshaller().unmarshal(errorProneFileXML);
            marshalledList.getM_employees().forEach(System.out::println);

            File errorProneFileCSV = new File("./empleadosPrueba.csv");
            if (!errorProneFileCSV.exists()) {errorProneFileCSV.createNewFile();}
            ToCSVSerializer.serializeToFile(errorProneFileCSV, newWrapper);
            EmployeeListWrapper wrapper2 = ToCSVSerializer.deserializeFromFile(errorProneFileCSV);
            System.out.println("Deserialized CSV Data");
            wrapper2.getM_employees().forEach(System.out::println);

        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
