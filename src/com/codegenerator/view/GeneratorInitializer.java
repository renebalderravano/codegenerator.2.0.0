package com.codegenerator.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.codegenerator.generator.BackEndGenerator;
import com.codegenerator.generator.FrontEndGenerator;


public class GeneratorInitializer extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextArea textArea = new JTextArea();
	String projectName;
	private String projectPath;
	
	public GeneratorInitializer(String projectName,String projectPath, BackEndGenerator backEndGenerator, FrontEndGenerator frontEndGenerator) {
		this.projectName = projectName;
		this.projectPath = projectPath;
		init(backEndGenerator, frontEndGenerator);
	}

	private void init(BackEndGenerator backEndGenerator, FrontEndGenerator frontEndGenerator) {

		setTitle("Proceso...");

		// evita cambio de tamaño
		setResizable(false);

		// dimensiones que ocupa en la pantalla
		setBounds(100, 150, 1000, 500);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel currentText = new JLabel("Incia proceso");
		currentText.setFont(new Font("Arial", Font.BOLD, 16));
		currentText.setBounds(25, 25, 325, 20);

		contentPane.add(currentText);

		// Create a JProgressBar
		JProgressBar progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true); // Display percentage text
		progressBar.setBounds(25, 50, 325, 20);
		contentPane.add(progressBar);
				
		//contentPane.add(createResultPanel(this.projectName));
		
		JPanel resultPanel = new JPanel();
		Border lineBorder = BorderFactory.createLineBorder(Color.black);

		// Set the border to the panel
		resultPanel.setBorder(lineBorder);
		
		resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
		
		JLabel projectNameLbl = new JLabel("Proyecto: "+projectName +"\n\n");
		projectNameLbl.setFont(new Font("Arial", Font.BOLD, 16));
		projectNameLbl.setPreferredSize(new Dimension(200, 80));
		projectNameLbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
		projectNameLbl.setToolTipText("Click para abrir carpeta del proyecto.");
		projectNameLbl.addMouseListener(new MouseAdapter() {
			
			@Override
            public void mouseEntered(MouseEvent e) {
				projectNameLbl.setForeground(Color.GREEN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
            	projectNameLbl.setForeground(Color.BLACK);
            }	
			
            @Override
            public void mouseClicked(MouseEvent e) {
                           	
            	try {
					Runtime.getRuntime().exec("explorer.exe /select /open,\"" + projectPath + "\"");	
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}            	
				
				System.out.println("Mostrar directorio...");
            }
        });
		resultPanel.add(projectNameLbl);
		
		JLabel projectEstatus = new JLabel("      Estatus: Generado ✓" +"\n\n");		
		projectEstatus.setPreferredSize(new Dimension(200, 80));
		resultPanel.add(projectEstatus);
		
		JLabel backendProjectName  = new JLabel("Backend: " +projectName+ "Backend\n\n\n");
		backendProjectName.setFont(new Font("Arial", Font.BOLD, 16));
		backendProjectName.setPreferredSize(new Dimension(200, 80));
		backendProjectName.setCursor(new Cursor(Cursor.HAND_CURSOR));
		backendProjectName.setToolTipText("Click para abrir carpeta del proyecto.");
		backendProjectName.addMouseListener(new MouseAdapter() {
			
			@Override
            public void mouseEntered(MouseEvent e) {
				backendProjectName.setForeground(Color.GREEN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
            	backendProjectName.setForeground(Color.BLACK);
            }	
            
            @Override
            public void mouseClicked(MouseEvent e) {            	
            	try {            		
            		File f = new File(projectPath + "\\"+projectName+ "Backend");
            		
            		if(f.exists()) {
            		
					Runtime.getRuntime().exec("explorer.exe /select /open,\"" + projectPath + "\\"+projectName+ "Backend\"");
            		}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}           	
				
				System.out.println("Mostrar directorio...");
            }
        });
		resultPanel.add(backendProjectName);
		
		JLabel backendProjectEstatus =  new JLabel("      Estatus: 0%");//"      Estatus: Generando ✓"+"\n\n");			
		backendProjectEstatus.setPreferredSize(new Dimension(200, 80));
		resultPanel.add(backendProjectEstatus);
		JLabel backendProjectQuantity = new JLabel("      Cantidad de servicios generados: 2"+"\n\n");		
		backendProjectQuantity.setPreferredSize(new Dimension(200, 80));
		resultPanel.add(backendProjectQuantity);
		
		
		JLabel frontendProjectName  = new JLabel("Frontend: "+projectName+ "Frontend\n\n\n");
		
		frontendProjectName.setFont(new Font("Arial", Font.BOLD, 16));
		frontendProjectName.setPreferredSize(new Dimension(200, 100));
		frontendProjectName.setCursor(new Cursor(Cursor.HAND_CURSOR));
		frontendProjectName.setToolTipText("Click para abrir carpeta del proyecto.");
		frontendProjectName.addMouseListener(new MouseAdapter() {
						
			@Override
            public void mouseEntered(MouseEvent e) {
				frontendProjectName.setForeground(Color.GREEN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
            	frontendProjectName.setForeground(Color.BLACK);
            }			
			
            @Override
            public void mouseClicked(MouseEvent e) {
            	
            	try {
            		File f = new File(projectPath + "\\"+projectName+ "Frontend");
            		
            		if(f.exists()) {            		
            			Runtime.getRuntime().exec("explorer.exe /select /open,\"" + projectPath + "\\"+projectName+ "Frontend\"");
            		}
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("Mostrar directorio...");
            }
        });
		resultPanel.add(frontendProjectName);
		
		JLabel frontendProjectEstatus = new JLabel("          Estatus: 0%");//"      Estatus: Generando ✓"+"\n\n");		
		frontendProjectEstatus.setPreferredSize(new Dimension(200, 80));
		resultPanel.add(frontendProjectEstatus);
		JLabel frontendProjectQuantity = new JLabel("          Cantidad de componentes generados: 2"+"\n\n");		
		frontendProjectQuantity.setPreferredSize(new Dimension(200, 80));
		resultPanel.add(frontendProjectQuantity);
		
		resultPanel.setBounds(25, 80, 325, 270);
		
		contentPane.add(resultPanel);
		
		// Create a JTextArea
		textArea = new JTextArea();
		textArea.setEditable(true);
		textArea.append("\n");
		textArea.append("*******************************************************************************************************************\n");
		textArea.append("                                                                  PROYECTO "+ this.projectName+"\n");
		textArea.append("*******************************************************************************************************************\n");

		// Add the JTextArea to a JScrollPane
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBounds(370, 50, 580, 300);
		contentPane.add(scrollPane);

		// Add a line of text to the JTextArea
		JButton btnGenerar = new JButton("Iniciar");
		btnGenerar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				Thread hilo1 = new Thread(() -> {
					backEndGenerator.generar();
				});
				hilo1.start();
				Thread hilo2 = new Thread(() -> {
					int i = 0;
					String oldLog = "";
					while (i < 100) {
						i = backEndGenerator.getProcessProgress();
						backendProjectEstatus.setText("          Estatus: " + i + "%...");
						setTitle("Procesando " + i + "%...");
						currentText.setText("Generando backend...");
						String log = backEndGenerator.getLog();
						if (!log.equals(oldLog)) {
							printLog(log);
							oldLog = log;
						}
						progressBar.setValue(i);
					}
					setTitle("Proceso Finalizado");
					currentText.setText("Proceso finalizado");

				});
				hilo2.start();

				try {
					hilo1.join();

					Thread hilo3 = new Thread(() -> {
						frontEndGenerator.generate();
					});
					hilo3.start();
					Thread hilo4 = new Thread(() -> {
						int i = 0;
						String oldLog = "";
						while (i < 100) {
							i = frontEndGenerator.getProcessProgress();
							frontendProjectEstatus.setText("          Estatus " + i + "%...");     
							setTitle("Procesando " + i + "%...");
							currentText.setText("Generando Frontend...");
							String log = frontEndGenerator.getLog();
							if (!log.equals(oldLog)) {
								printLog(log);
								oldLog = log;
							}
							progressBar.setValue(i);
						}
						setTitle("Proceso Finalizado");
						currentText.setText("Proceso finalizado");

					});
					hilo4.start();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

		btnGenerar.setBounds(500, 400, 100, 30);
		contentPane.add(btnGenerar);

		// Add a line of text to the JTextArea
		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		btnCancelar.setBounds(390, 400, 100, 30);
		contentPane.add(btnCancelar);

	}
	
	
	private JPanel createResultPanel(String projName) {

		
		return null;
	}

	private void printLog(String textLog) {
		textArea.append(textLog);
		textArea.setCaretPosition(textArea.getDocument().getLength());
		textArea.requestFocusInWindow();
	}

	public String getProjectPath() {
		return projectPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

}
