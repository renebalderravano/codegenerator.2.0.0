package com.codegenerator;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import com.codegenerator.connection.JDBCManager;
import com.codegenerator.util.Column;
import com.codegenerator.util.Table;

import java.util.List;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultTreeCellRenderer;

public class TreeTableDemo {
	public static void main(String[] args) {
		// Crear nodos jerárquicos con datos
		DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode(new Object[] { "", "", "" });
		
		JDBCManager c = new JDBCManager("sqlserver", "localhost", "1433", "sa", "Passw0rd");

		c.connect();
		List<Table> tablesWithSchema = c.getTableWithSchemaFromDataBase("DigiRet_db2");
		
		for (Table table : tablesWithSchema) {
			DefaultMutableTreeTableNode schemaA = getDefaultMutableTreeTableNode(new Object[] { table.getSchema(), table.getName(), true });
			
			List<Column> columns = c.getColumnsByTable("DigiRet_db2", table.getName());
			
			for (Column column : columns) {
				
				schemaA.add(getDefaultMutableTreeTableNode(new Object[] { column.getName(), column.getDataType(), true }));
				
			}
			
			root.add(schemaA);
		}
		
		
		
//		DefaultMutableTreeTableNode schemaA = getDefaultMutableTreeTableNode(new Object[] { "SchemaA", "tableOne", true });
//		schemaA.add(getDefaultMutableTreeTableNode(new Object[] { "columnOne", "dataType", true }));
//		schemaA.add(getDefaultMutableTreeTableNode(new Object[] { "columnTwo", "dataType", true }));
//
//		root.add(schemaA);
//
//		DefaultMutableTreeTableNode schemaB = getDefaultMutableTreeTableNode(new Object[] { "SchemaB", "tableThree", "" }) ;
//		schemaB.add(getDefaultMutableTreeTableNode(new Object[] { "columnOne", "dataType", true }));
//		root.add(schemaB);

		// Definir columnas
		String[] columnNames = { "Schema", "TableName", "Genarate" };

		// Crear modelo personalizado
		DefaultTreeTableModel model = new DefaultTreeTableModel(root, java.util.Arrays.asList(columnNames)) {
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

		};

		// Crear TreeTable
		JXTreeTable treeTable = new JXTreeTable(model);

		// Configurar renderer para ocultar iconos
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

		// Mostrar en ventana
		JFrame frame = new JFrame("TreeTable con 3 columnas");
		frame.add(new JScrollPane(treeTable));
		frame.setSize(500, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private static DefaultMutableTreeTableNode getDefaultMutableTreeTableNode(Object[] row) {
		return new DefaultMutableTreeTableNode(row) {
			@Override
			public String toString() {
				Object[] values = (Object[]) getUserObject();
				return values[0].toString(); // Mostrar solo la primera columna
			}
		};
	}
}