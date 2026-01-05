package com.codegenerator.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.DefaultRowSorter;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

import com.codegenerator.connection.JDBCManager;
import com.codegenerator.generator.BackEndGenerator;
import com.codegenerator.generator.FrontEndGenerator2;
import com.codegenerator.util.Column;
import com.codegenerator.util.ComboItem;
import com.codegenerator.util.FileManager;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;

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
	JLabel lbl;
	private String server;
	DefaultTreeTableModel model;
	JXTreeTable treeTable;
	Boolean existProject= false;

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
		txtProjectName.setColumns(10);
		txtProjectName.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				String workspace = txtWorkspace.getText() + "\\" + txtProjectName.getText();
				System.out.println(workspace);
				Path path = Paths.get(workspace);
				if (Files.exists(path)) {
					existProject= true;
					lbl.setText("Proyecto existente.");
					Path projectDir = Paths.get(workspace);
					try {
						Set<Object[]> tablesAux = new HashSet<Object[]>();
						for (Iterator iterator = tables.iterator(); iterator.hasNext();) {
							Object[] table = (Object[]) iterator.next();
							if (table[1].toString().equals("InvestmentFund")) {
								System.out.println();
							}
							Optional<Path> entityExists = Files.walk(projectDir)
									.filter(p -> p.getFileName().toString().equals(table[1] + "Entity.java"))
									.findFirst();

//							Optional<Path> serviceExists = Files.walk(projectDir)
//									.filter(p -> p.toString().contains(table[1] + "Service")).findFirst();
//
//							Optional<Path> serviceImplExists = Files.walk(projectDir)
//									.filter(p -> p.toString().contains(table[1] + "ServiceImpl")).findFirst();
//
//							Optional<Path> repositoryExists = Files.walk(projectDir)
//									.filter(p -> p.toString().contains(table[1] + "Repository")).findFirst();
//
//							Optional<Path> repositoryImplExists = Files.walk(projectDir)
//									.filter(p -> p.toString().contains(table[1] + "RepositoryImpl")).findFirst();
//
//							Optional<Path> controllerExists = Files.walk(projectDir)
//									.filter(p -> p.toString().contains(table[1] + "Controller")).findFirst();

							if (entityExists.isPresent()) {
								System.out.println(entityExists.get().toString());

								for (int row = 0; row < treeTable.getRowCount(); row++) {
									Object node = treeTable.getPathForRow(row).getLastPathComponent();

									if (node instanceof DefaultMutableTreeTableNode) {
										DefaultMutableTreeTableNode treeNode = (DefaultMutableTreeTableNode) node;
										Object[] values = (Object[]) treeNode.getUserObject();
//										values[2] = true;
//										System.out.println(values[0] + " | " + values[1] + " | " + values[2]);										
										if (values[1].toString().equals(table[1].toString())) {
											System.out.println();
											Object[] tableAux = recorrerNodos(treeNode, entityExists);
											tablesAux.add(tableAux);
										}
									}
								}
							} else {
								Object[] valuesAux = new Object[4];
								valuesAux[0] = table[0];
								valuesAux[1] = table[1];
								valuesAux[2] = true;

								tablesAux.add(valuesAux);
							}

						}

//						treeTable.setSortable(true);
//						RowSorter<? extends TableModel> rs = treeTable.getRowSorter();
//						if (rs instanceof DefaultRowSorter) {
//						    @SuppressWarnings("unchecked")
//						    DefaultRowSorter<TableModel, Integer> drs = (DefaultRowSorter<TableModel, Integer>) rs;
//						    drs.setComparator(2, Comparator.comparing(Boolean::booleanValue));
//						    drs.setSortKeys(List.of(new RowSorter.SortKey(2, SortOrder.DESCENDING)));
//						}

						// Convertir a lista
						List<Object[]> lista = new ArrayList<>(tablesAux);

						// Ordenar por el booleano en la posición 2
						lista.sort(Comparator.comparing(arr -> (Boolean) arr[2]));

						// Si quieres true primero (descendente)
						lista.sort(Comparator.comparing(arr -> (Boolean) arr[2], Comparator.reverseOrder()));

						setData(lista);

					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				} else {
					existProject= false;
					lbl.setText("");
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {

			}

		});

		contentPane.add(txtProjectName);
		lbl = new JLabel();
		lbl.setBounds(317, 63, 131, 14);
		contentPane.add(lbl);

		JLabel lblPaquetePrincipal = new JLabel("Paquete Principal:");
		lblPaquetePrincipal.setBounds(23, 94, 126, 14);
		contentPane.add(lblPaquetePrincipal);

		txtPaquetePrincipal = new JTextField("com." + databaseName.toLowerCase());
		txtPaquetePrincipal.setBounds(159, 91, 158, 20);
		txtPaquetePrincipal.setColumns(10);
		contentPane.add(txtPaquetePrincipal);

		JLabel lblArquitectura = new JLabel("Arquitectura:");
		lblArquitectura.setBounds(23, 137, 155, 22);
		contentPane.add(lblArquitectura);

		JCheckBox chkAddSecurity = new JCheckBox("Agregar Spring Security Oauth");
		chkAddSecurity.setSelected(false);
		chkAddSecurity.setBounds(23, 169, 212, 23);

		contentPane.add(chkAddSecurity);
		
		JCheckBox chkAddTablesSecurity = new JCheckBox("Agregar Tablas Spring Security Oauth");
		chkAddTablesSecurity.setSelected(false);
		chkAddTablesSecurity.setBounds(23, 192, 242, 23);

		contentPane.add(chkAddTablesSecurity);
		
		

		JComboBox<ComboItem> cbArquitectura = new JComboBox<ComboItem>();
		cbArquitectura.addItem(new ComboItem("MVC", "mvc"));
		cbArquitectura.addItem(new ComboItem("Hexagonal", "hexagonal"));

		cbArquitectura.addItemListener(new ItemListener() { 

			public void itemStateChanged(ItemEvent e) {
				String arquitectura = ((ComboItem) cbArquitectura.getSelectedItem()).getValue();
				if (arquitectura.equals("hexagonal")) {
					chkAddSecurity.setVisible(false);
				} else {
					chkAddSecurity.setVisible(true);
				}
			}
		});

		cbArquitectura.setBounds(159, 137, 130, 22);
		contentPane.add(cbArquitectura);

		JScrollPane jsbTable = new JScrollPane(createJxTreeTable());
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

				String backendName = txtProjectName.getText() + "Backend";
				String arquitectura = ((ComboItem) cbArquitectura.getSelectedItem()).getValue();

				for (int row = 0; row < treeTable.getRowCount(); row++) {
					Object node = treeTable.getPathForRow(row).getLastPathComponent();

					if (node instanceof DefaultMutableTreeTableNode) {
						DefaultMutableTreeTableNode treeNode = (DefaultMutableTreeTableNode) node;
						Object[] values = (Object[]) treeNode.getUserObject();

						for (Object[] table : tables) {
							if (values[1].toString().equals(table[1].toString()))
								table[2] = values[2];
						}

					}
				}

				Set<Object[]> tablesSelected = tables.stream()
											.filter(arr -> arr.length > 0 && (arr[2]).equals(true))
					//						.map(arr -> arr[1])
											.collect(Collectors.toSet());
					//						.findFirst();

				BackEndGenerator backEndGenerator = new BackEndGenerator(
																		 server, databaseName, tables, jdbcManager
																		 , workspace, backendName, txtPaquetePrincipal.getText(), arquitectura
																		 , chkAddSecurity.isSelected(), existProject, chkAddTablesSecurity.isSelected());

				String frontendName = txtProjectName.getText() + "Frontend";

				FrontEndGenerator2 frontEndGenerator = new FrontEndGenerator2(server, databaseName, tablesSelected,
						jdbcManager, workspace, frontendName, txtPaquetePrincipal.getText(),
						chkAddSecurity.isSelected(), arquitectura);

//				boolean isFrontGenerated = frontEndGenerator.generate();
//				JOptionPane.showMessageDialog(null, isGenerated ? "Proyecto Generado Exitosamente": "Error al generar el proyecto");
//				System.out.println("Proyecto generado!!!");

				GeneratorInitializer generatorLog = new GeneratorInitializer(txtProjectName.getText(), workspace,
						backEndGenerator, frontEndGenerator);
				generatorLog.setModal(true);
				generatorLog.setVisible(true);
				generatorLog.setLocationRelativeTo(frame);
			}
		});
		btnGenerar.setBounds(306, 414, 89, 23);
		contentPane.add(btnGenerar);

	}

	private DefaultMutableTreeTableNode getDefaultMutableTreeTableNode(Object[] row) {
		return new DefaultMutableTreeTableNode(row) {
			@Override
			public String toString() {
				Object[] values = (Object[]) getUserObject();
				return values[0].toString(); // Mostrar solo la primera columna
			}
		};
	}

	boolean existField = false;
	boolean existName = false;
	int countColumns = 0;

	public Object[] recorrerNodos(DefaultMutableTreeTableNode node, Optional<Path> entity) {

		Object[] values = (Object[]) node.getUserObject();// FILA TABLA PRINCIPAL
//		values[2] = true;
//		System.out.println(values[0] + " | " + values[1] + " | " + values[2]);

		Object[] valuesAux = new Object[values.length + 1];
		if (entity.get().toString().equals("InvestmentFund")) {
			System.out.println();
		}

		this.countColumns = node.getChildCount();

		File archivo = entity.get().toFile();
		try {
			CompilationUnit cu = StaticJavaParser.parse(archivo);
			List<Object[]> columns = new ArrayList<Object[]>();
			cu.findAll(ClassOrInterfaceDeclaration.class).forEach(cls -> {
//				System.out.println("Clase/Interfaz: " + cls.getName());
//				if( cls.getName().toString().equals("ClientEntity")){
				System.out.println("Clase/Interfaz: " + cls.getName());

				for (int i = 0; i < node.getChildCount(); i++) {
					Object[] values2 = (Object[]) ((DefaultMutableTreeTableNode) node.getChildAt(i)).getUserObject(); // FILA
																														// SUBTABLA
					columns.add(values2);
					System.out.println("Buscando campo: " + values2[0].toString());

					this.existField = false;
					cls.getFields().forEach(field -> {

						this.existName = false;
						field.getVariables().forEach(var -> {
//								if(var.getName().toString().equals(values2[0].toString())) {
							for (AnnotationExpr annotation : field.getAnnotations()) {
								if (annotation.getName().toString().equals("Column")
										|| annotation.getName().toString().equals("JoinColumn")) {
									if (annotation instanceof NormalAnnotationExpr) {
										NormalAnnotationExpr normal = (NormalAnnotationExpr) annotation;
										for (MemberValuePair pair : normal.getPairs()) {
											if (pair.getName().toString().equals("name")) {

												if (values2[0].toString()
														.equals(pair.getValue().toString().replace("\"", ""))) {
													System.out
															.println("Annotation value: " + pair.getValue().toString());
													System.out.println("Campo Encontrado: " + values2[0].toString());
													this.existName = true;
													this.existField = true;
													countColumns--;
													return;
												}
											}
										}
									}
								}
							}

							if (existField)
								return;
						});
					});
					values2[2] = !this.existField;
				}
//				}
			});

			if (countColumns == 0) {
				valuesAux[0] = values[0];
				valuesAux[1] = values[1];
				valuesAux[2] = false;
				valuesAux[3] = columns;
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return valuesAux;

	}

	private JXTreeTable createJxTreeTable() {

		DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode(new Object[] { "", "", "" });
		DefaultMutableTreeTableNode schemaA = getDefaultMutableTreeTableNode(new Object[] { "", "", true });

		root.add(schemaA);
		treeTable = new JXTreeTable(createDefaultTreeTableModel(root));
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);

		// Asignar renderer directamente al JXTreeTable
		treeTable.setTreeCellRenderer(renderer);

		// Configurar renderer/editor para la última columna
		TableColumn flagColumn = treeTable.getColumnModel().getColumn(2);
		flagColumn.setCellRenderer(treeTable.getDefaultRenderer(Boolean.class));
		flagColumn.setCellEditor(treeTable.getDefaultEditor(Boolean.class));

		// Enable sorting
		treeTable.setSortable(true);

		// Create a custom sorter
		TableRowSorter<?> sorter = new TableRowSorter<>(treeTable.getModel());

		// Ensure Boolean column is sorted as true > false
		sorter.setComparator(2, Comparator.comparing(Boolean::booleanValue));

		treeTable.setRowSorter(sorter);

		setData(new ArrayList<Object[]>(tables));

		return treeTable;
	}

	private void setData(List<Object[]> tables2) {
		Thread hilo2 = new Thread(() -> {

			DefaultMutableTreeTableNode root2 = new DefaultMutableTreeTableNode(new Object[] { "", "", "" });

			for (Object[] table : tables2) {
				DefaultMutableTreeTableNode schema = getDefaultMutableTreeTableNode(
						new Object[] { table[0], table[1], table[2] });

				if (!(table[3] instanceof List)) {
					List<Column> columns = jdbcManager.getColumnsByTable(databaseName, table[1].toString());
					for (Column column : columns) {
						schema.add(getDefaultMutableTreeTableNode(
								new Object[] { column.getName(), column.getDataType(), true }));
					}
				} else {
					List<Object[]> columns = (List) table[3];
					if (table[1].toString().equals("StructureElementPayrollConfig"))
						System.out.println(table[1]);
					for (Object[] column : columns) {
						schema.add(getDefaultMutableTreeTableNode(new Object[] { column[0], column[1], column[2] }));
					}
				}

				root2.add(schema);
			}

			model.setRoot(root2);
			// Notificar al modelo que la estructura cambió
//			treeTable = new JXTreeTable(createDefaultTreeTableModel(root2));
			DefaultTreeCellRenderer renderer2 = new DefaultTreeCellRenderer();
			renderer2.setLeafIcon(null);
			renderer2.setClosedIcon(null);
			renderer2.setOpenIcon(null);

			// Asignar renderer directamente al JXTreeTable
			treeTable.setTreeCellRenderer(renderer2);

			// Configurar renderer/editor para la última columna
			TableColumn flagColumn2 = treeTable.getColumnModel().getColumn(2);
			flagColumn2.setCellRenderer(treeTable.getDefaultRenderer(Boolean.class));
			flagColumn2.setCellEditor(treeTable.getDefaultEditor(Boolean.class));
		});

		hilo2.start();

	}

	private DefaultTreeTableModel createDefaultTreeTableModel(DefaultMutableTreeTableNode root) {
		// Definir columnas
		String[] columnNames = { "Schema", "TableName", "Genarate" };

		return model = new DefaultTreeTableModel(root, java.util.Arrays.asList(columnNames)) {

			@Override
			public int getColumnCount() {

				return columnNames.length;
			}

			@Override
			public String getColumnName(int column) {
				return columnNames[column];
			}

			@Override
			public Object getValueAt(Object node, int column) {
				if (node instanceof DefaultMutableTreeTableNode) {
					Object userObject = ((DefaultMutableTreeTableNode) node).getUserObject();
					if (userObject instanceof Object[]) {
						Object[] values = (Object[]) userObject;
						if (column < values.length) {
							return values[column];
						}
					}
				}
				return null;
			}

			@Override
			public boolean isCellEditable(Object node, int column) {
				// Ejemplo: solo columnas 1 y 2 editables
				return column == 2;
			}

			@Override
			public void setValueAt(Object value, Object node, int column) {

				Object[] values = (Object[]) ((DefaultMutableTreeTableNode) node).getUserObject();

				values[2] = !((Boolean) values[2]);
				// Notificar cambios
				modelSupport.firePathChanged(new TreePath(getPathToRoot((TreeTableNode) node)));
			}

			@Override
			public Object getChild(Object parent, int index) {
				return ((DefaultMutableTreeTableNode) parent).getChildAt(index);
			}

			@Override
			public int getChildCount(Object parent) {
				return ((DefaultMutableTreeTableNode) parent).getChildCount();
			}

			@Override
			public int getIndexOfChild(Object parent, Object child) {
				return ((DefaultMutableTreeTableNode) parent).getIndex((DefaultMutableTreeTableNode) child);
			}
		};
	}

}
