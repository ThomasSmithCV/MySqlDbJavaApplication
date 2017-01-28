import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mysql.jdbc.ResultSetMetaData;

// Thomas Smith
public class StaffQueries {

    // Connection URL syntax can be found at https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-configuration-properties.html
    private static final String CONNECTION_URL = "";
    private static final String USERNAME = "replaceUsername";
    private static final String PASSWORD = "replacePassword";

    private Connection con;
    private PreparedStatement browseAllStmt;
    private PreparedStatement searchStaffMemberStmt;
    private PreparedStatement searchDepartmentStmt;
    private PreparedStatement insertStmt;
    private PreparedStatement saveStmt;
    private PreparedStatement exportStmt;

    public StaffQueries(){
        try{
            // ADD EXTERNAL JARS TO CLASSPATH OR ELSE THIS WILL NEVER LOAD DRIVERS
            // Load up the mySQL drivers, Always needed at the start.
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            System.out.println("Drivers loaded succesfully.");

            con = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);

            String insertQuery =
                    " INSERT into Staff" +
                        "(FirstName, LastName, DateOfBirth, Department, Salary, StartDate, FullTime)" +
                    " values (?, ?, ?, ?, ?, ?, ?)"; // SQL command must have space, see whitespace before values.

            String viewAllQuery =
                    " SELECT * " +
                    " FROM Staff " +
                    " ORDER BY StaffID ASC";

            String searchStaffMemberQuery =
                    " SELECT * " +
                    " FROM Staff" +
                    " WHERE FirstName like ?" +
                    " AND LastName like ?";

            String searchDepartmentQuery =
                    " SELECT * " +
                    " FROM Staff" +
                    " WHERE Department like ?";

            String saveQuery =
                    " UPDATE Staff" +
                    " SET FirstName = ?, LastName = ?, DateOfBirth = ?, Department = ?, Salary = ?, StartDate = ?, FullTime = ?" +
                    " WHERE StaffId = ?";

            String exportQuery =
                    " SELECT * " +
                    " FROM Staff";

            // Create the mySQL prepared statements
            insertStmt = con.prepareStatement(insertQuery);
            browseAllStmt = con.prepareStatement(viewAllQuery);
            searchStaffMemberStmt = con.prepareStatement(searchStaffMemberQuery);
            searchDepartmentStmt = con.prepareStatement(searchDepartmentQuery);
            saveStmt = con.prepareStatement(saveQuery);
            exportStmt = con.prepareStatement(exportQuery);

            // Fancy return of database name to show user database being used.
            Pattern pattern = Pattern.compile("[^/]*$");
            Matcher matcher = pattern.matcher(CONNECTION_URL);
            if (matcher.find())
                System.out.println("Succesfully Connected to Database: " + matcher.group(0));
            else
                System.out.println("Succesfully Connected to Database.");
        } catch(SQLException ex) {
            System.out.println("Problem with DB");
            ex.printStackTrace();
            //terminate the program
            System.exit(1);
        } catch(Exception ex2) {
            System.out.println("Problem loading drivers");
            ex2.printStackTrace();
            System.exit(2);
        }
    }

    public void close(){
        try{
            con.close();
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<Staff> getAllStaffMembers() {
        List<Staff> results = null;
        ResultSet rs = null;
        try {
            rs = browseAllStmt.executeQuery();
            results = new ArrayList<Staff>();

            while(rs.next()) {
                results.add(getStaffInformation(rs));
            }
        } catch (SQLException ex) {
            System.out.println("Lost connection to DB");
            ex.printStackTrace();
        } finally {
            try{
                rs.close();
            } catch(SQLException e) {
                e.printStackTrace();
                close();
            }
        }
        return results;
    }

    public List<Staff> searchStaffMembers(String fname, String lname) {
        List<Staff> results = null;
        ResultSet rs = null;

        try {
            searchStaffMemberStmt.setString(1, "%" + fname + "%");
            searchStaffMemberStmt.setString(2, "%" + lname + "%");

            rs = searchStaffMemberStmt.executeQuery();
            results = new ArrayList<Staff>();

            while(rs.next()){
                results.add(getStaffInformation(rs));
            }
        } catch (SQLException ex) {
                System.out.println("Lost connection to DB");
                ex.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch(SQLException e) {
                e.printStackTrace();
                close();
            }
        }
        return results;
    }

    public List<Staff> searchDepartment(String department) {
        List<Staff> results = null;
        ResultSet rs = null;

        try {
            searchDepartmentStmt.setString(1, "%" + department + "%");

            rs = searchDepartmentStmt.executeQuery();
            results = new ArrayList<Staff>();

            while(rs.next()){
                results.add(getStaffInformation(rs));
            }
        } catch (SQLException e) {
                System.out.println("Lost connection to DB");
                e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch(SQLException e) {
                e.printStackTrace();
                close();
            }
        }
        return results;
    }

    public int addStaffMember(String fn, String ln, String dob, String dept, Double sal, String sdate, Boolean ftime) {
        int result = 0;

        try{
            insertStmt.setString(1, fn);
            insertStmt.setString(2, ln);
            insertStmt.setString(3, dob);
            insertStmt.setString(4, dept);
            insertStmt.setDouble(5, sal);
            insertStmt.setString(6, sdate);
            insertStmt.setBoolean(7, ftime);

            result = insertStmt.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
            close();
        }
        return result;
    }

    public int updateStaffMember(String fn, String ln, String dob, String dept, Double sal, String sdate, Boolean ftime, Double id) {
        int result = 0;

        try{
            saveStmt.setString(1, fn);
            saveStmt.setString(2, ln);
            saveStmt.setString(3, dob);
            saveStmt.setString(4, dept);
            saveStmt.setDouble(5, sal);
            saveStmt.setString(6, sdate);
            saveStmt.setBoolean(7, ftime);
            saveStmt.setDouble(8, id);

            result = saveStmt.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
            close();
        }
        return result;
    }

    public void exportTableData() {
        File desktop = new File(System.getProperty("user.home"), "Desktop");
        String filename = desktop + "/DataExport.csv";

        try {
            ResultSet rs = exportStmt.executeQuery();
            ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();

            FileWriter fw = new FileWriter(filename);

            for (int i = 1; i <= numberOfColumns; i++) {
                String name = rsmd.getColumnName(i);
                fw.append(name);
                if (i != numberOfColumns) fw.append(',');
            }

            while (rs.next()) {
                fw.append('\n');
                fw.append(rs.getString(1));
                fw.append(',');
                fw.append(rs.getString(2));
                fw.append(',');
                fw.append(rs.getString(3));
                fw.append(',');
                fw.append(rs.getString(4));
                fw.append(',');
                fw.append(rs.getString(5));
                fw.append(',');
                fw.append(rs.getString(6));
                fw.append(',');
                fw.append(rs.getString(7));
                fw.append(',');
                fw.append(rs.getInt(8) == 1 ? "Yes" : "No");
            }
            fw.flush();
            fw.close();
            System.out.println("CSV File created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Staff getStaffInformation(ResultSet rs) {
        try {
            Staff staff = new Staff(
                rs.getInt("StaffID"),
                rs.getString("FirstName"),
                rs.getString("LastName"),
                rs.getString("DateOfBirth"),
                rs.getString("Department"),
                rs.getDouble("Salary"),
                rs.getString("StartDate"),
                rs.getBoolean("FullTime")
                );
            return staff;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
