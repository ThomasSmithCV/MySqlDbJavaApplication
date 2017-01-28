// Thomas Smith
public class Staff {

    private int staffID;
    private String firstName, lastName, dateOfBirth, department, startDate;
    private Double salary;
    private Boolean fullTime;

    public Staff() {
        
    }

    public Staff(String fname, String lname) {
        firstName = fname;
        lastName = lname;
    }
    public Staff(int id, String fname, String lname, String dob,
                        String dept, Double sal, String sdate, Boolean ftime) {
        staffID = id;
        firstName = fname;
        lastName = lname;
        dateOfBirth = dob;
        department = dept;
        startDate = sdate;
        salary = sal;
        fullTime = ftime;
    }

    // Getter.
    public int getStaffId() {
        return staffID;
    }

    // Setter
    public void setStaffId(int id) {
        staffID = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String fname) {
        firstName = fname;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lname) {
        lastName = lname;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dob) {
        dateOfBirth = dob;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String dept) {
        department = dept;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String sdate) {
        startDate = sdate;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double sal) {
        salary = sal;
    }

    public Boolean getIsFullTime() {
        return fullTime;
    }

    public void setIsFullTime(Boolean ftime) {
        fullTime = ftime;
    }

    @Override
    public String toString() {
        String s = ("Staff\n");
        s += String.format("Id: %s \n", staffID);
        s += String.format("Name: %s %s\n", firstName, lastName);
        s += String.format("Date Of Birth: %s \n", dateOfBirth);
        s += String.format("Department: %s \n", department);
        s += String.format("Start Date: %s \n", startDate);
        s += String.format("Salary: €%.,2f \n", salary);
        s += String.format("Full Time: %s \n", fullTime);
        return s;
    }
}
