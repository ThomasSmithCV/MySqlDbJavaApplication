import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.sql.DriverManager;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.jdbc.JDBCCategoryDataset;
import org.jfree.ui.RefineryUtilities;

//Thomas Smith
public class StaffDisplay extends JFrame {

    //Objects for the navigation panel
    private JPanel navigationPanel;
    private JButton previousButton, nextButton;
    private JLabel recordLabel, ofLabel;
    private JTextField indexTextField, maxTextfield;
    
    // Connection URL syntax can be found at https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-configuration-properties.html
    private static final String CONNECTION_URL = "";
    private static final String USERNAME = "replaceUsername";
    private static final String PASSWORD = "replacePassword";
    private Boolean insertPressed = false;

    //Objects for the details panel
    private JPanel detailsPanel;
    private JLabel staffIdLabel, firstNameLabel, lastNameLabel, dateOfBirthLabel,
                        departmentLabel, startDateLabel, salaryLabel, fullTimeLabel;
    private JTextField staffIdTextField, firstNameTextField, lastNameTextField, dateOfBirthTextField,
                        departmentTextField, startDateTextField, salaryTextField, fullTimeTextField;

    //Objects for query panel
    private JPanel staffQueryPanel, departmentQueryPanel;
    private JButton staffQueryButton, departmentQueryButton;
    private JLabel staffQueryFirstNameLabel, staffQueryLastNameLabel, departmentQueryLabel;
    private JTextField staffQueryFirstNameTextField, staffQueryLastNameTextField,departmentQueryTextField;

    private JButton browseButton;
    private JButton insertButton;
    private JButton saveButton;
    private JButton exportButton;
    private JButton graphButton;

    //Database
    private StaffQueries staffInformation;
    private Staff currentStaffMember;
    private List<Staff> results;
    private int numberOfEntries;
    private int currentEntryIndex;
    
    //Constructor
    public StaffDisplay() {
        super("Staff List");
        setLayout(new FlowLayout(FlowLayout.CENTER,10,10));

        //Call constructor to connect DB
        staffInformation = new StaffQueries();

        //Initialize items for navigation panel
        navigationPanel = new JPanel();
        previousButton = new JButton("Previous");
        recordLabel = new JLabel("Record");
        indexTextField = new JTextField(2);
        ofLabel = new JLabel("of");
        maxTextfield = new JTextField(2);
        nextButton = new JButton("Next");

        previousButton.setEnabled(false);
        nextButton.setEnabled(false);

        indexTextField.setHorizontalAlignment(JTextField.CENTER);
        maxTextfield.setHorizontalAlignment(JTextField.CENTER);
        maxTextfield.setEditable(false);

        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.X_AXIS));

        //Add items to the navigation panel 
        navigationPanel.add(previousButton);
        navigationPanel.add(Box.createHorizontalStrut(10));//gap
        navigationPanel.add(recordLabel);
        navigationPanel.add(Box.createHorizontalStrut(10));//gap
        navigationPanel.add(indexTextField);
        navigationPanel.add(Box.createHorizontalStrut(10));//gap
        navigationPanel.add(ofLabel);
        navigationPanel.add(Box.createHorizontalStrut(10));//gap
        navigationPanel.add(maxTextfield);
        navigationPanel.add(Box.createHorizontalStrut(10));//gap
        navigationPanel.add(nextButton);

        //Add navigation panel to JFrame to make it visible.
        add(navigationPanel);

        //Initialize the items for details panel
        detailsPanel = new JPanel();
        staffIdLabel = new JLabel("Staff ID:");
        staffIdTextField = new JTextField(10);
        staffIdTextField.setEditable(false);

        firstNameLabel = new JLabel("First Name:");
        firstNameTextField = new JTextField(10);

        lastNameLabel = new JLabel("Last Name:");
        lastNameTextField = new JTextField(10);

        dateOfBirthLabel = new JLabel("DOB:");
        dateOfBirthTextField = new JTextField(10);

        departmentLabel = new JLabel("Department:");
        departmentTextField = new JTextField(10);

        startDateLabel = new JLabel("Start Date:");
        startDateTextField = new JTextField(10);

        salaryLabel = new JLabel("Salary:");
        salaryTextField = new JTextField(10);

        fullTimeLabel = new JLabel("Is Full Time?");
        fullTimeTextField = new JTextField(10);

        detailsPanel.setLayout(new GridLayout(5,2,4,4));

        detailsPanel.add(staffIdLabel);
        detailsPanel.add(staffIdTextField);

        detailsPanel.add(firstNameLabel);
        detailsPanel.add(firstNameTextField);

        detailsPanel.add(lastNameLabel);
        detailsPanel.add(lastNameTextField);

        detailsPanel.add(dateOfBirthLabel);
        detailsPanel.add(dateOfBirthTextField);

        detailsPanel.add(departmentLabel);
        detailsPanel.add(departmentTextField);

        detailsPanel.add(startDateLabel);
        detailsPanel.add(startDateTextField);

        detailsPanel.add(salaryLabel);
        detailsPanel.add(salaryTextField);

        detailsPanel.add(fullTimeLabel);
        detailsPanel.add(fullTimeTextField);

        //Add details panel to JFrame to make it visible.

        add(detailsPanel);

        JPanel queryPanel = new JPanel();
        queryPanel.setLayout(new GridLayout(2,1,4,4));
        //Staff Query Panel
        staffQueryPanel = new JPanel();
        staffQueryPanel.setLayout(new BoxLayout(staffQueryPanel,BoxLayout.X_AXIS));

        staffQueryPanel.setBorder(BorderFactory.createTitledBorder("Search for a Staff Member"));

        staffQueryFirstNameLabel = new JLabel ("Enter First Name");
        staffQueryFirstNameTextField = new JTextField(10);
        staffQueryLastNameLabel = new JLabel ("Enter Last Name");
        staffQueryLastNameTextField = new JTextField(10);
        staffQueryButton = new JButton("Search");

        staffQueryPanel.add(Box.createHorizontalStrut(5));//gap
        staffQueryPanel.add(staffQueryFirstNameLabel);
        staffQueryPanel.add(Box.createHorizontalStrut(10));//gap
        staffQueryPanel.add(staffQueryFirstNameTextField);
        staffQueryPanel.add(Box.createHorizontalStrut(10));//gap
        staffQueryPanel.add(staffQueryLastNameLabel);
        staffQueryPanel.add(Box.createHorizontalStrut(10));//gap
        staffQueryPanel.add(staffQueryLastNameTextField);
        staffQueryPanel.add(Box.createHorizontalStrut(5));//gap
        staffQueryPanel.add(staffQueryButton);
        staffQueryPanel.add(Box.createHorizontalStrut(5));//gap

        //Add query panel to JFrame to make it visible.
        queryPanel.add(staffQueryPanel);

        //Department Query Panel
        departmentQueryPanel = new JPanel();
        departmentQueryPanel.setLayout(new BoxLayout(departmentQueryPanel,BoxLayout.X_AXIS));

        departmentQueryPanel.setBorder(BorderFactory.createTitledBorder("Search by Department"));

        departmentQueryLabel = new JLabel ("Enter Keyword");
        departmentQueryTextField = new JTextField(10);
        departmentQueryButton = new JButton("Search");

        departmentQueryPanel.add(Box.createHorizontalStrut(5));//gap
        departmentQueryPanel.add(departmentQueryLabel);
        departmentQueryPanel.add(Box.createHorizontalStrut(10));//gap
        departmentQueryPanel.add(departmentQueryTextField);
        departmentQueryPanel.add(Box.createHorizontalStrut(10));//gap
        departmentQueryPanel.add(departmentQueryButton);
        departmentQueryPanel.add(Box.createHorizontalStrut(5));//gap

        //Add query panel to JFrame to make it visible.
        queryPanel.add(departmentQueryPanel);

        add(queryPanel);

        //Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3,2,4,4));
        browseButton = new JButton ("View All");
        insertButton = new JButton ("Insert New");
        saveButton = new JButton ("Save");
        exportButton = new JButton ("Export Data");
        graphButton = new JButton ("Graph Income by Employee");

        buttonPanel.add(browseButton);
        buttonPanel.add(insertButton);
        buttonPanel.add(saveButton);
        Image docImage;
        Image graphImage;

        try{
            docImage = ImageIO.read(getClass().getResource("icon2.png"));
            graphImage = ImageIO.read(getClass().getResource("icon4.png"));
            exportButton.setIcon(new ImageIcon(docImage));
            graphButton.setIcon(new ImageIcon(graphImage));
        } catch (Exception ex){
            System.out.println("System is currently unavailable. Please try again later.");
            ex.printStackTrace();
        }

        buttonPanel.add(exportButton);
        buttonPanel.add(graphButton);

        //Add button panel to JFrame to make it visible.
        add(buttonPanel);

        previousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                previousButtonPressed(e);
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextButtonPressed(e);
            }
        });

        indexTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                indexTextFieldChanged(e);
            }
        });

        staffQueryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                staffQueryButtonPressed(e);
            }
        });

        departmentQueryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                departmentQueryButtonPressed(e);
            }
        });

        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browseButtonPressed(e);
            }
        });

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertButtonPressed(e);
                insertPressed = true;
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveButtonPressed(e);
            }
        });

        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportButtonPressed();
            }
        });

        graphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getData();
            }
        });

        //Add window listener to my frame.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                staffInformation.close();
                System.exit(0); //normal termination no errors. 
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(735, 450);
        setVisible(true);
        
        loadAllRecords();
    }

    public void previousButtonPressed(ActionEvent e) {
        if (currentEntryIndex <= 0) {
            currentEntryIndex = 1; // Don't go any further than first record.
        }
        indexTextField.setText((currentEntryIndex) + "");
        indexTextFieldChanged(e);
    }

    public void nextButtonPressed(ActionEvent e) {
        currentEntryIndex++;
        if (currentEntryIndex >= numberOfEntries) {
            currentEntryIndex = numberOfEntries - 1; // Don't go any further than last record.
        }
        indexTextField.setText((currentEntryIndex + 1) + "");
        indexTextFieldChanged(e);
    }

    public void indexTextFieldChanged(ActionEvent e) {
        try {
            currentEntryIndex = Integer.parseInt(indexTextField.getText()) -1;

            if (currentEntryIndex < numberOfEntries && currentEntryIndex >= 0 && numberOfEntries != 0) {
                currentStaffMember = results.get(currentEntryIndex);

                staffIdTextField.setText(currentStaffMember.getStaffId() + "");
                firstNameTextField.setText(currentStaffMember.getFirstName() + "");
                lastNameTextField.setText(currentStaffMember.getLastName() + "");
                dateOfBirthTextField.setText(currentStaffMember.getDateOfBirth() + "");
                departmentTextField.setText(currentStaffMember.getDepartment() + "");
                startDateTextField.setText(currentStaffMember.getStartDate() + "");
                salaryTextField.setText(currentStaffMember.getSalary() + "");
                String ft;
                if (currentStaffMember.getIsFullTime() == true) {
                    ft = "Yes";
                } else {
                    ft = "No";
                }
                fullTimeTextField.setText(ft);
            }
        } catch (Exception ex) {
            System.out.println("System is currently unavailable. Please try again later.");
            ex.printStackTrace();
        }
    }
    public void departmentQueryButtonPressed(ActionEvent e) {
        try{
            //results = staffInformation.searchStaffMembers(staffQueryTextField.getText());
            results = staffInformation.searchDepartment(departmentQueryTextField.getText());
            numberOfEntries = results.size();

            if (numberOfEntries != 0) {
                currentEntryIndex = 0;
                currentStaffMember = results.get(currentEntryIndex);

                staffIdTextField.setText(currentStaffMember.getStaffId() + "");
                firstNameTextField.setText(currentStaffMember.getFirstName() + "");
                lastNameTextField.setText(currentStaffMember.getLastName() + "");
                dateOfBirthTextField.setText(currentStaffMember.getDateOfBirth() + "");
                departmentTextField.setText(currentStaffMember.getDepartment() + "");
                startDateTextField.setText(currentStaffMember.getStartDate() + "");
                salaryTextField.setText(currentStaffMember.getSalary() + "");
                String ft;
                if (currentStaffMember.getIsFullTime() == true) {
                    ft = "Yes";
                } else {
                    ft = "No";
                }
                fullTimeTextField.setText(ft);

                indexTextField.setText((currentEntryIndex+1) + "");
                maxTextfield.setText((numberOfEntries) + "");

                previousButton.setEnabled(true);
                nextButton.setEnabled(true);

            } else {
                JOptionPane.showMessageDialog(this,"No Matching Records Found - Displaying all records");
                browseButtonPressed(e);
            }
        } catch(Exception ex) {
            System.out.println("System is currently unavailable. Please try again later.");
            ex.printStackTrace();
        }
    }

    public void staffQueryButtonPressed(ActionEvent e) {
        try{
            results = staffInformation.searchStaffMembers(staffQueryFirstNameTextField.getText(), staffQueryLastNameTextField.getText());
            numberOfEntries = results.size();

            if (numberOfEntries != 0) {
                currentEntryIndex = 0;
                currentStaffMember = results.get(currentEntryIndex);

                staffIdTextField.setText(currentStaffMember.getStaffId() + "");
                firstNameTextField.setText(currentStaffMember.getFirstName() + "");
                lastNameTextField.setText(currentStaffMember.getLastName() + "");
                dateOfBirthTextField.setText(currentStaffMember.getDateOfBirth() + "");
                departmentTextField.setText(currentStaffMember.getDepartment() + "");
                startDateTextField.setText(currentStaffMember.getStartDate() + "");
                salaryTextField.setText(currentStaffMember.getSalary() + "");
                String ft;
                if (currentStaffMember.getIsFullTime() == true) {
                    ft = "Yes";
                } else {
                    ft = "No";
                }
                fullTimeTextField.setText(ft);

                indexTextField.setText((currentEntryIndex+1) + "");
                maxTextfield.setText((numberOfEntries) + "");

                previousButton.setEnabled(true);
                nextButton.setEnabled(true);

            } else {
                JOptionPane.showMessageDialog(this,"No Matching Records Found - Displaying all records");
                browseButtonPressed(e);
            }
        } catch(Exception ex) {
            System.out.println("System is currently unavailable. Please try again later.");
            ex.printStackTrace();
        }
    }

    public void browseButtonPressed(ActionEvent e) {
        try{
            results = staffInformation.getAllStaffMembers();
            numberOfEntries = results.size();
            
            if (numberOfEntries != 0) {
                currentEntryIndex = 0;
                currentStaffMember = results.get(currentEntryIndex);
                
                staffIdTextField.setText(currentStaffMember.getStaffId() + "");
                firstNameTextField.setText(currentStaffMember.getFirstName() + "");
                lastNameTextField.setText(currentStaffMember.getLastName() + "");
                dateOfBirthTextField.setText(currentStaffMember.getDateOfBirth() + "");
                departmentTextField.setText(currentStaffMember.getDepartment() + "");
                startDateTextField.setText(currentStaffMember.getStartDate() + "");
                salaryTextField.setText(currentStaffMember.getSalary() + "");

                String ft;
                ft = currentStaffMember.getIsFullTime() == true ? "Yes" : "No";

                fullTimeTextField.setText(ft);

                indexTextField.setText((currentEntryIndex+1) + "");
                maxTextfield.setText((numberOfEntries) + "");

                previousButton.setEnabled(true);
                nextButton.setEnabled(true);
            }
        } catch(Exception ex) {
            System.out.println("System is currently unavailable. Please try again later.");
            ex.printStackTrace();
        }
    }

    public void insertButtonPressed(ActionEvent e) {
        try{
            firstNameTextField.setText("");
            lastNameTextField.setText("");
            dateOfBirthTextField.setText("");
            departmentTextField.setText("");
            salaryTextField.setText("");
            startDateTextField.setText("");
            fullTimeTextField.setText("");
            staffIdTextField.setText("");
            
            indexTextField.setText("");
            maxTextfield.setText("");

        } catch (Exception ex) {
            System.out.println("System is currently unavailable. Please try again later.");
            ex.printStackTrace();
        }
    }

    public void saveButtonPressed(ActionEvent e) {
        //insertPressed
        if (staffIdTextField.getText().equalsIgnoreCase("")) {
            try{
                int result = staffInformation.addStaffMember(
                        firstNameTextField.getText(),
                        lastNameTextField.getText(),
                        dateOfBirthTextField.getText(),
                        departmentTextField.getText(),
                        Double.parseDouble(salaryTextField.getText()),
                        startDateTextField.getText(),
                        Boolean.parseBoolean(fullTimeTextField.getText()));
                if (result == 1) {
                    JOptionPane.showMessageDialog(this,"Staff member added successfully" );
                } else {
                    JOptionPane.showMessageDialog(this, "Error occurred - Staff member not added");
                }
                browseButtonPressed(e);
            } catch (Exception ex) {
                System.out.println("System is currently unavailable. Please try again later.");
                ex.printStackTrace();
            }
        } else {
            try{
                int result = staffInformation.updateStaffMember(
                        firstNameTextField.getText(),
                        lastNameTextField.getText(),
                        dateOfBirthTextField.getText(),
                        departmentTextField.getText(),
                        Double.parseDouble(salaryTextField.getText()), 
                        startDateTextField.getText(),
                        Boolean.parseBoolean(fullTimeTextField.getText()),
                        Double.parseDouble(staffIdTextField.getText()));
                if (result == 1) {
                    JOptionPane.showMessageDialog(this,"Staff member updated successfully" );
                } else {
                    JOptionPane.showMessageDialog(this, "Error occurred - Staff member not added");
                }
                browseButtonPressed(e);
            } catch (Exception ex) {
                System.out.println("System is currently unavailable. Please try again later.");
                ex.printStackTrace();
            }
        }
        insertPressed = false;
    }

    public void exportButtonPressed() {
        try{
            staffInformation.exportTableData();
            JOptionPane.showMessageDialog(this, "A file named 'DataExport' has been created and saved to your desktop.");
        } catch (Exception ex) {
            System.out.println("System is currently unavailable. Please try again later.");
            ex.printStackTrace();
        }
    }

    public void loadAllRecords() {
        try{
            results = staffInformation.getAllStaffMembers();
            numberOfEntries = results.size();
            
            if (numberOfEntries != 0) {
                currentEntryIndex = 0;
                currentStaffMember = results.get(currentEntryIndex);
                
                staffIdTextField.setText(currentStaffMember.getStaffId() + "");
                firstNameTextField.setText(currentStaffMember.getFirstName() + "");
                lastNameTextField.setText(currentStaffMember.getLastName() + "");
                dateOfBirthTextField.setText(currentStaffMember.getDateOfBirth() + "");
                departmentTextField.setText(currentStaffMember.getDepartment() + "");
                startDateTextField.setText(currentStaffMember.getStartDate() + "");
                salaryTextField.setText(currentStaffMember.getSalary() + "");
                String ft;
                if (currentStaffMember.getIsFullTime() == true) {
                    ft = "Yes";
                } else {
                    ft = "No";
                }
                fullTimeTextField.setText(ft);

                indexTextField.setText((currentEntryIndex + 1) + "");
                maxTextfield.setText((numberOfEntries) + "");

                previousButton.setEnabled(true);
                nextButton.setEnabled(true);
            }
        } catch(Exception ex) {
            System.out.println("System is currently unavailable. Please try again later.");
            ex.printStackTrace();
        }
    }

    public void openFile() {
        try{
            File f = new File("/Users/TSmith/Desktop/myjdbcfile.csv");
            Desktop.getDesktop().edit(f);
        } catch(Exception ex) {
            System.out.println("System is currently unavailable. Please try again later.");
            ex.printStackTrace();
        }
    }

    public ChartPanel getData() {
        try {
            String query = "SELECT * FROM Staff";
            JDBCCategoryDataset dataset = new JDBCCategoryDataset(DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD), query);
            JFreeChart chart = ChartFactory.createLineChart3D(
                    "Income Chart", "Employee Id", "Income (�)", dataset, PlotOrientation.VERTICAL, false, true, true);
            ChartPanel panel = new ChartPanel(chart);
            DataDisplay dd = new DataDisplay(panel);
            return panel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}