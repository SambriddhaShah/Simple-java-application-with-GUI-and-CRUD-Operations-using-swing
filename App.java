import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class App extends JFrame {
    private static final long serialVersionUID = 1L;
	String gender, covid;
    JMenuBar menuBar;
    JPanel upperPanel;
    JPanel tablePanel;
    JPanel dataPanel;
    JPanel btnPanel;
    DefaultTableModel model;
    JTable table;
    JTextField txtName, txtAddress;
    JRadioButton rdMale, rdFemale;
    ButtonGroup bgGroup;
    JCheckBox chkPositive;
    JButton btnSave, btnUpdate, btnDelete, btnClear;
    Connection con;

    App() {
        try {
            con = DBUtility.getDbConnection();
            System.out.println(con);
            System.out.println("Connection Successful");
        } 
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        setTitle("CRUD App");
        setVisible(true);
        setMinimumSize(new Dimension(300, 500));
        ;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(getMenu());

        setLayout(new GridLayout(1, 1));
        JPanel subPanel = new JPanel(new GridLayout(2, 1));
        subPanel.add(upperPanel());
        subPanel.add(tableUI());
        add(subPanel);
        pack();
        setLocationRelativeTo(null);

    }

    private JPanel upperPanel() {
        upperPanel = new JPanel(new GridLayout(1, 1));
        upperPanel.add(dataUI());
        upperPanel.add(buttonUI());

        return upperPanel;
    }

    private Component buttonUI() {
        btnPanel = new JPanel();
        btnPanel.setBorder(BorderFactory.createTitledBorder("Buttons Funtionality"));

        btnPanel.add(btnSave = new JButton("Add"));
        btnPanel.add(btnUpdate = new JButton("Update"));
        btnPanel.add(btnDelete = new JButton("Delete"));
        btnPanel.add(btnClear = new JButton("Clear"));

        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = txtName.getText().trim();
                String address = txtAddress.getText().trim();

                if (rdFemale.isSelected())
                    gender = "Female";
                else if (rdMale.isSelected())
                    gender = "Male";
                covid = chkPositive.isSelected() ? "Positive" : "Negative";
                if (name.isEmpty() || gender == null) {
                    JOptionPane.showMessageDialog(dataPanel, "Some of the fields are empty", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        PreparedStatement statement = con
                                .prepareStatement(
                                        "INSERT INTO covid(P_Name,P_address,P_Gender,Covid_status) values (?,?,?,?)");
                        statement.setString(1, name);
                        statement.setString(2, address);
                        statement.setString(3, gender);
                        statement.setString(4, covid);

                        statement.executeUpdate();

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    refreshTable();
                    btnClear.doClick();
                    JOptionPane.showMessageDialog(dataPanel, "New record is added successfully", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String row = JOptionPane.showInputDialog(dataPanel, "Please enter ID number to delete?", "Queries",
                        JOptionPane.QUESTION_MESSAGE);
                int confirm = JOptionPane.showConfirmDialog(dataPanel, "Are you sure want to delete row?", "Warning",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        int rowDelete = Integer.parseInt(row);
                        String query = "DELETE FROM covid WHERE Id = ?";

                        try {
                            PreparedStatement statement = con.prepareStatement(query);
                            statement.setInt(1, rowDelete);
                            statement.execute();
                            refreshTable();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                    } catch (NumberFormatException exception) {
                        JOptionPane.showMessageDialog(dataPanel, "You must enter valid number.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (ArrayIndexOutOfBoundsException exception) {
                        JOptionPane.showMessageDialog(dataPanel,
                                "Provided row doesn't exist. Please enter valid row number.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                String name = txtName.getText().trim();
                String address = txtAddress.getText().trim();
                if (rdFemale.isSelected())
                    gender = "Female";
                else if (rdMale.isSelected())
                    gender = "Male";
                covid = chkPositive.isSelected() ? "Positive" : "Negative";

                if (name.isEmpty() || gender == null) {
                    JOptionPane.showMessageDialog(dataPanel, "Some of the fields are empty", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {

                    int id = Integer.parseInt((model.getValueAt(selectedRow, 0)).toString());
                    String query = "UPDATE covid SET P_name = ?, P_address = ?, P_gender = ?, Covid_status = ? WHERE Id = ?";

                    try {
                        PreparedStatement statement = con.prepareStatement(query);
                        statement.setString(1, name);
                        statement.setString(2, address);
                        statement.setString(3, gender);
                        statement.setString(4, covid);
                        statement.setInt(5, id);

                        statement.executeUpdate();
                        refreshTable();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    btnClear.doClick();
                    JOptionPane.showMessageDialog(dataPanel,
                            "Record at row " + selectedRow + " is updated successfully", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                }

            }
        });

        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                txtName.setText("");
                txtAddress.setText("");
            }
        });

        btnPanel.setLayout(new GridLayout(2, 2));

        return btnPanel;
    }

    private JPanel tableUI() {
        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("List of data"));
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[] { "Id", "P_name", "P_address", "P_gender", "Covid_status" });
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        tablePanel.add(scrollPane);

        table.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                txtName.setText(model.getValueAt(selectedRow, 1).toString());
                txtAddress.setText(model.getValueAt(selectedRow, 2).toString());
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }

        });

        refreshTable();

        return tablePanel;
    }

    private void refreshTable() {
        model.setRowCount(0);
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM covid.covid");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                model.addRow(new Object[] {
                        resultSet.getInt("Id"),
                        resultSet.getString("P_name"),
                        resultSet.getString("P_address"),
                        resultSet.getString("P_gender"),
                        resultSet.getString("covid_status"),
                });
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private JPanel dataUI() {
        dataPanel = new JPanel();
        dataPanel.setLayout(new GridBagLayout());
        dataPanel.setBorder(BorderFactory.createTitledBorder("Data Entry"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        gbc.gridx = 0;
        gbc.gridy = 0;
        dataPanel.add(new JLabel("Name"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        dataPanel.add(new JLabel("Address"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        dataPanel.add(new JLabel("Gender"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        dataPanel.add(new JLabel("Positive"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;

        gbc.gridx = 1;
        gbc.gridy = 0;
        txtName = new JTextField(20);
        dataPanel.add(txtName, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        txtAddress = new JTextField(20);
        dataPanel.add(txtAddress, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JPanel rdbtn = new JPanel();
        rdbtn.setLayout(new GridLayout());
        rdMale = new JRadioButton("Male");
        rdFemale = new JRadioButton("Female");
        rdbtn.add(rdMale);
        rdbtn.add(rdFemale);

        dataPanel.add(rdbtn, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        chkPositive = new JCheckBox();
        dataPanel.add(chkPositive, gbc);

        ButtonGroup bg = new ButtonGroup();
        bg.add(rdFemale);
        bg.add(rdMale);

        return dataPanel;
    }

    private JMenuBar getMenu() {
        menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu viewMenu = new JMenu("View");

        JMenuItem newItem = new JMenuItem("New");
        JMenuItem exitItem = new JMenuItem("Exit");

        fileMenu.add(newItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);

        return menuBar;
    }

    public static void main(String[] args) {
        new App();

    }
}