import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Third
{
    public static void main(String[] args)
    {
        List<School> schools = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("D:\\Java\\Lab1\\Java\\classes.txt"));

            String line;
            while ((line = reader.readLine()) != null)
            {
                String[] parts = line.split(",");

                String name = parts[0];
                String adress = parts[1];
                int year = Integer.parseInt(parts[2]);
                int number = Integer.parseInt(parts[3]);
                int studentCount = Integer.parseInt(parts[4]);

                School school = new School(name, adress, year, number, studentCount);
                schools.add(school);
            }

            reader.close();

            for (School school : schools) {
                System.out.println("School name: " + school.getName() + ", school adress: " + school.getAdress() + ", school year: " + school.getYear() + ", school №: " + school.getNumber() + ", student count: " + school.getStudentCount());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<University> universities = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("D:\\Java\\Lab1\\Java\\universities.txt"));

            String line;
            while ((line = reader.readLine()) != null)
            {
                String[] parts = line.split(",");

                String name = parts[0];
                String adress = parts[1];
                int year = Integer.parseInt(parts[2]);
                int accreditationLevel = Integer.parseInt(parts[3]);
                int facultyCount = Integer.parseInt(parts[4]);

                // Створіть об'єкт класу School і додайте його до списку
                University university = new University(name, adress, year, accreditationLevel, facultyCount);
                universities.add(university);
            }

            reader.close();

            for (University university : universities) {
                System.out.println("University name: " + university.getName() + ", university adress: " + university.getAdress() + ", university year: " + university.getYear() + ", accreditation level: " + university.getAccreditationLevel() + ", faculty count: " + university.getFacultyCount());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print("\n-------------------------------------------\n");
        //1
        EducationalInstitution[] institutions = new EducationalInstitution[schools.size() + universities.size()];
        int index = 0;
        for (School school : schools) {
            institutions[index++] = school;
        }
        for (University university : universities) {
            institutions[index++] = university;
        }

        Arrays.sort(institutions);

        System.out.println("    Sort by institution year:");
        for (EducationalInstitution institution : institutions) {
            System.out.println("Institution name: " + institution.getName() + ", Institution year: " + institution.getYear());
        }

        System.out.print("\n-------------------------------------------\n");
        //2
        int minStudentCount = schools.get(0).getStudentCount();
        System.out.println("    Min student count:");
        for (School school : schools)
        {
            if(school.getStudentCount() == minStudentCount)
                System.out.println("School name: " + school.getName() + ", school adress: " + school.getAdress() + ", school year: " + school.getYear() + ", school №: " + school.getNumber() + ", student count: " + school.getStudentCount());
        }

        System.out.print("\n-------------------------------------------\n");
        //3
        Scanner scanner = new Scanner(System.in);
        System.out.print("   Input accreditation level: ");
        int level = scanner.nextInt();
        boolean info = false;
        System.out.print("   Accreditation level: ");
        for (University university : universities)
        {
            if(university.getAccreditationLevel() == level)
            {
                System.out.println("University name: " + university.getName() + ", university adress: " + university.getAdress() + ", university year: " + university.getYear() + ", accreditation level: " + university.getAccreditationLevel() + ", faculty count: " + university.getFacultyCount());
                info = true;
            }
        }
        if(!info)
        {
            System.out.print("University with this level doesn't exist!");
        }
    }
}

abstract class EducationalInstitution implements Comparable<EducationalInstitution>
{
    protected String name;
    protected String adress;
    protected int year;

    EducationalInstitution(String name, String address, int year)
    {
        setName(name);
        setAdress(address);
        setYear(year);
    }

    public String getName()
    {
        return name;
    }
    public String getAdress()
    {
        return adress;
    }
    public int getYear()
    {
        return year;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public void setAdress(String adress)
    {
        this.adress = adress;
    }
    public void setYear(int year)
    {
        this.year = year;
    }

    @Override
    public int compareTo(EducationalInstitution other) {
        return Integer.compare(this.year, other.year);
    }
}

class School extends EducationalInstitution
{
    private int number;
    private int studentCount;

    School(String name, String address, int year, int number, int studentCount)
    {
        super(name, address, year);
        setNumber(number);
        setStudentCount(studentCount);
    }

    public int getNumber()
    {
        return number;
    }
    public int getStudentCount()
    {
        return studentCount;
    }
    public void setNumber(int number)
    {
        this.number = number;
    }
    public void setStudentCount(int studentCount)
    {
        this.studentCount = studentCount;
    }
}

class University extends EducationalInstitution
{
    private int accreditationLevel;
    private int facultyCount;

    University(String name, String address, int year, int accreditationLevel, int facultyCount)
    {
        super(name, address, year);
        setAccreditationLevel(accreditationLevel);
        setFacultyCount(facultyCount);
    }

    public int getAccreditationLevel()
    {
        return accreditationLevel;
    }
    public int getFacultyCount()
    {
        return facultyCount;
    }
    public void setAccreditationLevel(int accreditationLevel)
    {
        this.accreditationLevel = accreditationLevel;
    }
    public void setFacultyCount(int facultyCount)
    {
        this.facultyCount = facultyCount;
    }
}
