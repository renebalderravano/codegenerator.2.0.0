package com.codegenerator.view;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.codegenerator.connection.JDBCManager;
import com.codegenerator.util.ComboItem;
import com.codegenerator.util.PropertiesReading;
import com.codegenerator.util.Result;
import com.codegenerator.util.Table;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import java.awt.event.ActionListener;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.awt.event.ItemEvent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class ConnectionDBServerFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtHost;
	private JTextField txtPort;
	private JTextField txtUsername;
	private JPasswordField txtPassword;
	JComboBox cbDatabase;
	private JTable tblTables;
	private JDBCManager c;
	private Object[][] data = new Object[][] {};

	Set<Object[]> tableSelected = new HashSet();

	/**
	 * Create the frame.
	 */
	public ConnectionDBServerFrame() {
		setTitle("Generador de código");
		initialize();
	}

	private void initialize() {

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(450, 150, 432, 506);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblDBServer = new JLabel("Servidor:");
		lblDBServer.setBounds(26, 11, 155, 22);
		contentPane.add(lblDBServer);

		JComboBox<ComboItem> cbDBServer = new JComboBox<ComboItem>();
		cbDBServer.addItem(new ComboItem("Seleccione...", "none"));
		cbDBServer.addItem(new ComboItem("MySQL", "mysql"));
		cbDBServer.addItem(new ComboItem("MSSQL server", "sqlserver"));

		cbDBServer.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					ComboItem i = (ComboItem) e.getItem();
					txtHost.setText(PropertiesReading.getProperty(i.getValue() + ".datasource.host"));
					txtPort.setText(PropertiesReading.getProperty(i.getValue() + ".datasource.port"));
					txtUsername.setText(PropertiesReading.getProperty(i.getValue() + ".datasource.username"));
					txtPassword.setText(PropertiesReading.getProperty(i.getValue() + ".datasource.password"));

					List<String> dbs = new ArrayList<>();
					dbs.add("Conectese al servidor de db.");
					String[] array = new String[dbs.size()];
					dbs.toArray(array); // fill the array
					cbDatabase.setModel(new DefaultComboBoxModel(array));

					createTable(new ArrayList<>(), new String[] {"Schema",  "Tabla", "Generar" });
				}
			}
		});

		cbDBServer.setBounds(130, 11, 130, 22);
		contentPane.add(cbDBServer);

		JLabel lblHost = new JLabel("Host:");
		lblHost.setBounds(26, 63, 46, 14);
		contentPane.add(lblHost);

		txtHost = new JTextField();
		// txtHost.setText("localhost");
		txtHost.setBounds(130, 60, 130, 20);
		txtHost.setColumns(10);
		contentPane.add(txtHost);

		JLabel lblPort = new JLabel("Port: ");
		lblPort.setBounds(270, 63, 46, 14);
		contentPane.add(lblPort);

		txtPort = new JTextField();
		// txtPort.setText("3306");
		txtPort.setBounds(310, 60, 86, 20);
		txtPort.setColumns(10);
		contentPane.add(txtPort);

		JLabel lblUsername = new JLabel("Usuario:");
		lblUsername.setBounds(26, 99, 74, 14);
		contentPane.add(lblUsername);

		txtUsername = new JTextField();
		// txtUsername.setText("root");
		txtUsername.setBounds(130, 96, 130, 20);
		txtUsername.setColumns(10);
		contentPane.add(txtUsername);

		JLabel lblPassword = new JLabel("Password: ");
		lblPassword.setBounds(26, 135, 74, 14);
		contentPane.add(lblPassword);

		txtPassword = new JPasswordField();
		// txtPassword.setText("root");
		txtPassword.setBounds(130, 132, 130, 20);
		txtPassword.setColumns(10);
		contentPane.add(txtPassword);

		JLabel lblCatalog = new JLabel("Base de datos:");
		lblCatalog.setBounds(26, 179, 100, 14);
		contentPane.add(lblCatalog);

		JButton btnNext = new JButton("Siguiente");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// ir al siguiente panel donde se mostrara la lista de las bases de datos
				// existentes en el server
				String serverDB = ((ComboItem) cbDBServer.getSelectedItem()).getValue();
				if (serverDB.equals("none")) {
					JOptionPane.showMessageDialog(null, "Seleccione un servidor de base de datos.");
					return;
				}
				else if (tableSelected.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Seleccione una tabla de la lista.");
					return;
				} else {
					String database = cbDatabase.getSelectedItem().toString();
					DataBaseFrame frm = new DataBaseFrame(serverDB, database, c, tableSelected);
					frm.setVisible(true);
					dispose();
				}
			}
		});
		btnNext.setBounds(309, 418, 89, 23);
		contentPane.add(btnNext);

		JButton btnTestConnection = new JButton("Conectar");
		btnTestConnection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String serverDB = ((ComboItem) cbDBServer.getSelectedItem()).getValue();
				if (serverDB.equals("none")) {
					JOptionPane.showMessageDialog(null, "Seleccione un servidor de base de datos.");
					return;
				}
				String host = txtHost.getText();
				if (host == null || host.equals("")) {
					JOptionPane.showMessageDialog(null, "Ingrese el host al cual desea conectarse.");
					return;
				}
				String port = txtPort.getText();
				if (port == null || port.equals("")) {
					JOptionPane.showMessageDialog(null, "Ingrese el puerto.");
					return;
				}
				String username = txtUsername.getText();
				if (username == null || username.equals("")) {
					JOptionPane.showMessageDialog(null, "Ingrese el nombre de usuario.");
					return;
				}
				String password = txtPassword.getText();
				if (password == null || password.equals("")) {
					JOptionPane.showMessageDialog(null, "Ingrese el password.");
					return;
				}

				c = new JDBCManager(serverDB, host, port, username, password);

				Result r = c.connect();
				if (r.isSuccess()) {
					List<String> dbs = new ArrayList<>();
					dbs.add("Seleccione...");
					dbs.addAll(c.getDataBases());
					String[] array = new String[dbs.size()];
					dbs.toArray(array); // fill the array
					cbDatabase.setModel(new DefaultComboBoxModel(array));
				} else {
					JOptionPane.showMessageDialog(null, r.getMessage());
				}
			}
		});
		btnTestConnection.setBounds(310, 131, 86, 20);
		contentPane.add(btnTestConnection);

		String[] columns = new String[] {"Schema", "Tabla", "Generar" };
		
		tblTables = new JTable(data, columns);
		tblTables.setBounds(26, 244, 372, 158);

		JScrollPane jsbTable = new JScrollPane(tblTables);
		jsbTable.setBounds(26, 244, 372, 158);
		contentPane.add(jsbTable);

		cbDatabase = new JComboBox();
		cbDatabase.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					tableSelected = new HashSet<Object[]>();
					List<Table> tablesWithSchema = c.getTableWithSchemaFromDataBase(e.getItem().toString());
					createTableWithSchema(tablesWithSchema, columns);
//					List<String> tables = c.getTableFromDataBase(e.getItem().toString());
//					createTable(tables, columns);
				}
			}
		});
		cbDatabase.setBounds(130, 175, 200, 22);
		contentPane.add(cbDatabase);

		JCheckBox selectAllCheckbox = new JCheckBox("Seleccionar todo", true);
        selectAllCheckbox.addActionListener(e -> {
            boolean isSelected = selectAllCheckbox.isSelected();
            tableSelected.clear();
            for (int i = 0; i < tblTables.getRowCount(); i++) {
            	  tblTables.setValueAt(isSelected, i, 2);
            	  data[i][2] = isSelected;
            	  data[i][3] = "Cero";
            	  if(isSelected) {
            		  Object[] row = new Object[6];
            		  row[0] = tblTables.getValueAt(i, 0);
            			row[1] = tblTables.getValueAt(i, 1);
            			row[2] = true;
            			row[3] = "Cero";
            			tableSelected.add(row);
            	  }
            }
            
            System.out.println("Cantidad de seleccionados: " + tableSelected.size());
            System.out.println(tableSelected);
        });
		selectAllCheckbox.setBounds(260, 210, 150, 20);
		contentPane.add(selectAllCheckbox);

	}

	public void createTableWithSchema(List<Table> rows, String[] columns) {

		data = new Object[rows.size()][];

		int i = 0;
		for (Table t : rows) {
			Object[] row = new Object[8];
			row[0] = t.getSchema();
			row[1] = t.getName();
			row[2] = true;
			row[3] = "Cero";
			data[i] = row;
			tableSelected.add(data[i]);
			i++;
		}

		final Class[] columnClass = new Class[] {String.class, String.class, Boolean.class };
		// create table model with data
		DefaultTableModel model = new DefaultTableModel(data, columns) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return true;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return columnClass[columnIndex];
			}
		};

		tblTables.setModel(model);

		for (i = 0; i < tblTables.getRowCount(); i++) {
			tblTables.setValueAt(true, i, 2);
		}

		TableColumnModel columnModel = tblTables.getColumnModel();
		TableColumn column = columnModel.getColumn(2);
		column.setCellEditor(tblTables.getDefaultEditor(Boolean.class));
		column.setCellRenderer(tblTables.getDefaultRenderer(Boolean.class));
		

		JCheckBox checkBox = new JCheckBox();
		checkBox.setSelected(true);
		
		checkBox.addItemListener(new ItemListener() {
			
			public void itemStateChanged(ItemEvent e) {
				
				int row = tblTables.getSelectedRow();
				int column = tblTables.getSelectedColumn();

				try {
					
					if(row != -1) {
						if(data[row][3].equals("Cero")) {
							if(((boolean) data[row][column])) {
								tableSelected.removeIf(r -> r[1] == data[row][1] );
								data[row][column] = false;
							}
							else {
								data[row][column] = true;
								tableSelected.add(data[row]);
							}
							data[row][3] = "uno";
						}
						else if(data[row][3].equals("uno")) {
							data[row][3] = "Cero";
						}	
					}
					
//					if (e.getStateChange() == ItemEvent.SELECTED) {
//						data[row][column] = true;
//						tableSelected.add(data[row]);
//					} else if (e.getStateChange() == ItemEvent.DESELECTED) {
//						tableSelected.remove(data[row]);
//						data[row][column] = false;
//					}
					System.out.println("Cantidad de seleccionados: " + tableSelected.size());
				} catch (Exception e2) {
					// TODO: handle exception
				}

			}
		});
		column.setCellEditor(new DefaultCellEditor(checkBox));
	}

	
	boolean isInitializing = true;
	@Deprecated
	public void createTable(List<String> rows, String[] columns) {

		data = new Object[rows.size()][];

		int i = 0;
		for (String t : rows) {
			Object[] row = new Object[6];
			row[0] = t;
			row[1] = true;
			row[2] = "Cero";
			data[i] = row;
			tableSelected.add(data[i]);
			i++;
		}

		final Class[] columnClass = new Class[] { String.class, Boolean.class };
		// create table model with data
		DefaultTableModel model = new DefaultTableModel(data, columns) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return true;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return columnClass[columnIndex];
			}
		};

		tblTables.setModel(model);

		for (i = 0; i < tblTables.getRowCount(); i++) {
			tblTables.setValueAt(true, i, 1);
		}

		TableColumnModel columnModel = tblTables.getColumnModel();
		TableColumn column = columnModel.getColumn(1);
		column.setCellEditor(tblTables.getDefaultEditor(Boolean.class));
		column.setCellRenderer(tblTables.getDefaultRenderer(Boolean.class));
		

		JCheckBox checkBox = new JCheckBox();
		checkBox.setSelected(true);
		
		checkBox.addItemListener(new ItemListener() {
			
			public void itemStateChanged(ItemEvent e) {
				
				int row = tblTables.getSelectedRow();
				int column = tblTables.getSelectedColumn();

				try {
					
					if(row != -1) {
						if(data[row][2].equals("Cero")) {
							if(((boolean) data[row][column])) {
								tableSelected.removeIf(r -> r[0] == data[row][0] );
								data[row][column] = false;
							}
							else {
								data[row][column] = true;
								tableSelected.add(data[row]);
							}
							data[row][2] = "uno";
						}
						else if(data[row][2].equals("uno")) {
							data[row][2] = "Cero";
						}	
					}
					
//					if (e.getStateChange() == ItemEvent.SELECTED) {
//						data[row][column] = true;
//						tableSelected.add(data[row]);
//					} else if (e.getStateChange() == ItemEvent.DESELECTED) {
//						tableSelected.remove(data[row]);
//						data[row][column] = false;
//					}
					System.out.println("Cantidad de seleccionados: " + tableSelected.size());
				} catch (Exception e2) {
					// TODO: handle exception
				}

			}
		});
		column.setCellEditor(new DefaultCellEditor(checkBox));
	}

}
