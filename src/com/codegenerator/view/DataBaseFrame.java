package com.codegenerator.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.codegenerator.connection.JDBCManager;
import com.codegenerator.generator.BackEndGenerator;
import com.codegenerator.generator.FrontEndGenerator;
import com.codegenerator.generator.FrontEndGenerator2;
import com.codegenerator.util.ComboItem;
import com.codegenerator.util.FileManager;
import com.codegenerator.util.PropertiesReading;

public class DataBaseFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	private JTextField txtProjectName;
	private JTable tblTables;
	Set<Object[]> tables;
	JDBCManager jdbcManager;
	String databaseName;
	private JTextField txtPaquetePrincipal;
	private JFileChooser fcWorkspace;
	private JTextField txtWorkspace;
	
	private String server;
	
	public DataBaseFrame(String server, String databaseName, JDBCManager jdbcManager, Set<Object[]> tableSelected) {
		setTitle("Generador de código");
		this.jdbcManager = jdbcManager;
		this.tables = tableSelected;
		this.databaseName = databaseName;
		this.server = server;
		initialize();
	}
	
	private void initialize() {
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(350, 150, 750, 506);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		JLabel lblWorkspace = new JLabel("Workspace: ");
		lblWorkspace.setBounds(23, 26, 101, 14);
		contentPane.add(lblWorkspace);
		
		txtWorkspace = new JTextField();
		txtWorkspace.setBounds(159, 23, 123, 20);
		contentPane.add(txtWorkspace);
		txtWorkspace.setColumns(10);
		
		JButton btnBrowse = new JButton("Browse...");		
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				JFileChooser fc = new JFileChooser("C:\\Users\\retro\\eclipse-workspace");
				 fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);				
				int r = fc.showOpenDialog(null);
								 
	            if (r == JFileChooser.APPROVE_OPTION) {
	                // set the label to the path of the selected directory
	                txtWorkspace.setText(fc.getSelectedFile().getAbsolutePath());
	            }
	            // if the user cancelled the operation
	            else
	            	txtWorkspace.setText("the user cancelled the operation");
				
				System.out.println("Mostrar directorio...");
			}
		});
		
		btnBrowse.setBounds(306, 22, 89, 23);
		contentPane.add(btnBrowse);		
		
		JLabel lblProjectName = new JLabel("Nombre del proyecto: ");
		lblProjectName.setBounds(23, 63, 131, 14);
		contentPane.add(lblProjectName);
		
		txtProjectName = new JTextField(databaseName);
		txtProjectName.setBounds(159, 60, 158, 20);
		contentPane.add(txtProjectName);
		txtProjectName.setColumns(10);
		
		JLabel lblPaquetePrincipal = new JLabel("Paquete Principal:");
		lblPaquetePrincipal.setBounds(23, 94, 126, 14);
		contentPane.add(lblPaquetePrincipal);
		
		txtPaquetePrincipal = new JTextField("com."+ databaseName.toLowerCase());
		txtPaquetePrincipal.setBounds(159, 91, 158, 20);
		contentPane.add(txtPaquetePrincipal);
		txtPaquetePrincipal.setColumns(10);
		
		JLabel lblArquitectura = new JLabel("Arquitectura:");
		lblArquitectura.setBounds(23, 137, 155, 22);
		contentPane.add(lblArquitectura);
		
		JCheckBox chkAddSecurity = new JCheckBox("Agregar Spring Security Oauth");
		chkAddSecurity.setSelected(false);
		chkAddSecurity.setBounds(23, 169, 212, 23);
		contentPane.add(chkAddSecurity);
//		

		JComboBox<ComboItem> cbArquitectura = new JComboBox<ComboItem>();
		cbArquitectura.addItem(new ComboItem("MVC", "mvc"));
		cbArquitectura.addItem(new ComboItem("Hexagonal", "hexagonal"));

		cbArquitectura.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				
				String arquitectura = ((ComboItem) cbArquitectura.getSelectedItem()).getValue();
				if (arquitectura.equals("hexagonal")) {
					chkAddSecurity.setVisible(false);
				}
				else {
					chkAddSecurity.setVisible(true);
				}
			}
		});

		cbArquitectura.setBounds(159, 137, 130, 22);
		contentPane.add(cbArquitectura);
		
//		JCheckBox chkBackEnd = new JCheckBox("Generar Spring Boot Project");
//		chkBackEnd.setSelected(true);
//		chkBackEnd.setBounds(23, 169, 212, 23);
//		contentPane.add(chkBackEnd);
//		
//		JCheckBox chkFrontEnd = new JCheckBox("Generar Front-End");
//		chkFrontEnd.setBounds(23, 198, 137, 23);
//		contentPane.add(chkFrontEnd);
		
		String[] columns = new String[] {"Schema", "Tabla", "Generar", "Generar lista", "Generar Form", "Generar form en Pop-Up"};
		
		Object[] d = tables.toArray();		
		Object[][] data = new Object[d.length][columns.length];		
		for (int i = 0; i < d.length; i++) {
			data[i]= (Object[]) d[i];
			data[i][2]= true;
			data[i][3]= true;
			data[i][4]= false;
		}
		
		final Class[] columnClass = new Class[] {String.class, String.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class   };
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
		tblTables = new JTable(model);
		tblTables.setBounds(0, 0, 700, 158);
		JScrollPane jsbTable = new JScrollPane(tblTables);
		jsbTable.setBounds(23, 239, 700, 158);
		contentPane.add(jsbTable);
		
		JButton btnRegresar = new JButton("Regresar");
		btnRegresar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ConnectionDBServerFrame frame = new ConnectionDBServerFrame();
				frame.setVisible(true);
				dispose();
			}
		});
		btnRegresar.setBounds(210, 414, 89, 23);
		contentPane.add(btnRegresar);
		
		JFrame frame = this;
		JButton btnGenerar = new JButton("Generar");
		btnGenerar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
								
				String workspace = txtWorkspace.getText();
				
				FileManager.createFolder(workspace, txtProjectName.getText());
				
				workspace = workspace + "\\" + txtProjectName.getText();
				String backendName = txtProjectName.getText()+ "Backend";
				
				String arquitectura = ((ComboItem) cbArquitectura.getSelectedItem()).getValue();
				BackEndGenerator backEndGenerator = new BackEndGenerator(server,
																		databaseName,
																		tables, 
																		jdbcManager, 
																		workspace, 
																		backendName, 
																		txtPaquetePrincipal.getText(), 
																		arquitectura);				
//				boolean isGenerated = backEndGenerator.generar();
				
				
				String frontendName = txtProjectName.getText()+ "Frontend";
				
				FrontEndGenerator2 frontEndGenerator = new FrontEndGenerator2(server,
						databaseName,
						tables,
						jdbcManager, 
						workspace, 
						frontendName, 
						txtPaquetePrincipal.getText(), 
						chkAddSecurity.isSelected(),
						arquitectura);	
				
				
//				boolean isFrontGenerated = frontEndGenerator.generate();
//				
//				JOptionPane.showMessageDialog(null, isGenerated ? "Proyecto Generado Exitosamente": "Error al generar el proyecto");
//				
//				System.out.println("Proyecto generado!!!");
				
				
				GeneratorInitializer generatorLog = new GeneratorInitializer(txtProjectName.getText(),workspace,backEndGenerator, frontEndGenerator);
				generatorLog.setModal(true);
				generatorLog.setVisible(true);
				generatorLog.setLocationRelativeTo(frame);
			}
		});
		btnGenerar.setBounds(306, 414, 89, 23);
		contentPane.add(btnGenerar);
		
		
	}
}
