package com.codegenerator;

import java.awt.EventQueue;

import com.codegenerator.view.ConnectionDBServerFrame;

public class MainApplication {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ConnectionDBServerFrame frame = new ConnectionDBServerFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}



}
