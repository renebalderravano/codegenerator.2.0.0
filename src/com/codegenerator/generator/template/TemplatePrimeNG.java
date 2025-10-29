package com.codegenerator.generator.template;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.codegenerator.util.Column;
import com.codegenerator.util.FieldNameFormatter;
import com.codegenerator.util.Table;

public class TemplatePrimeNG {

	public TemplatePrimeNG() {

	}

	public static String createTable(Table table) {
		List<Column> lst = table.getColumns();
		String tableName = table.getName();
		String table1 = generateTable(lst, tableName);
		return table1;
	}

	public static String createForm(Table table) {

		Map<String, String> columnas = new LinkedHashMap();
		
		for (Column column : table.getColumns()) {
			columnas.put(column.getName(), column.getDataType());
		}
		String formulario = generatePrimeNgForm(table.getName(), table.getColumns());
		System.out.println(formulario);

		return formulario;
	}

	public void createDialog() {

	}

	private static String generatePrimeNgForm(String tableName, List<Column> columns) {
		StringBuilder formBuilder = new StringBuilder();
		formBuilder.append("<div class=\"card\">\n");
		formBuilder.append( "	<h3>" + FieldNameFormatter.splitCamelCaseToString(tableName) + "</h3>\n");
		formBuilder.append( "   <hr><p-toast />\n");
		formBuilder.append( "   <div class=\"flex flex-col gap-4\">\r\n");
		formBuilder.append("		<form [formGroup]=\"form\">\n");

		
		int i = 1;
		int count = columns.size();
		for (Column col : columns) {

			String column = col.getName();
			String type = col.getDataType().toLowerCase();
			String label = FieldNameFormatter.splitCamelCaseToString(column);

			if (col.getIsForeigKey()) {
				String fkName = "";
				if (col.getName().startsWith("id")  || col.getName().startsWith("Id"))
					fkName = col.getName().substring(2);
				else if (col.getName().endsWith("_id") || col.getName().endsWith("Id"))
					fkName = col.getName().replace("_id", "").replace("Id", "");
				
//				String foreignKeyColumn = FieldNameFormatter.formatText(fkName, false);
				column = col.getName();
				type = "select";
				label = FieldNameFormatter.splitCamelCaseToString(fkName);
			}

			if (column.equals("id")) {
				formBuilder.append("		<input type=\"hidden\" name=\"id\" formControlName=\"id\">\n");
				i = 1;
				count -= 1;
				continue;
			}

			if (i == 1)
				formBuilder.append("		<div class=\"flex flex-col md:flex-row gap-2\">\n");

			formBuilder.append("			<div class=\"flex flex-col grow basis-0 gap-2\">\n");
			formBuilder.append("    			<label for=\"" + column + "\">" + label + "</label>\n");
			formBuilder.append("    			" + getControlHtml(type, column, col) + "\n");
			formBuilder.append("			</div>\n");

			if (i == 2) {
				formBuilder.append("  	</div>\n");
				count -= 2;
				i = 1;
				continue;
			}
			i++;
		}

		if (count >= 1 && count < 2)
			formBuilder.append("  	</div>\n");

		formBuilder.append("		<br> <div class=\"flex flex-wrap gap-6 \">\r\n"
				+ "            <div class=\"flex flex-col grow basis-0 gap-4 flex-end\"></div>\r\n"
				+ "                <div class=\"flex-end\">\r\n"
//				+ "                    <p-button icon='pi pi-fw pi-arrow-left' label=\"Regresar\" severity=\"warn\" "
//				+ "						(onClick)=\"goTo" + FieldNameFormatter.toPascalCase(tableName) + "()\"\r\n"
//				+ "                         />\r\n"
				+ "                    <p-button icon='pi pi-fw pi-save' label=\"Guardar\" severity=\"info\" (onClick)=\"save()\"\r\n"
				+ "                         />\r\n" + "                </div>\r\n" + "        </div>\n");
		formBuilder.append("	</form>\n");

//		formBuilder.append("  </div>\n");
		formBuilder.append("</div>\n");
		return formBuilder.toString();
	}

	private static String getControlHtml(String sqlType, String fieldName, Column column) {
		String validator = "";
		if (!column.getIsNullable())
			validator = "[class.invalid]=\"form.get('" + fieldName + "')?.invalid && form.get('" + fieldName
					+ "')?.touched\" ";

		if (column.getIsForeigKey()) {
			String fkName = "";
			if (column.getName().startsWith("id")  || column.getName().startsWith("Id"))
				fkName = column.getName().substring(2);
			else if (column.getName().endsWith("_id") || column.getName().endsWith("Id"))
				fkName = column.getName().replace("_id", "").replace("Id", "");
			
			return "<p-select [options]=\"opts" + fkName+ "\" formControlName=\""
					+ fieldName + "\" optionValue=\"id\" optionLabel=\"nombre\"  placeholder=\"Seleccione " + fieldName
					+ "\" class=\"w-full md:w-56\" />";
		}

		switch (column.getDataType()) {
		case "int":
		case "bigint":
		case "smallint":
		case "tinyint":
		case "decimal":
		case "numeric":
		case "float":
		case "real":
			return "<p-inputNumber id=\"" + fieldName + "\" formControlName=\"" + fieldName + "\" "

					+ validator

					+ "></p-inputNumber>";
		case "bit":
			return "<p-checkbox id=\"" + fieldName + "\" formControlName=\"" + fieldName
					+ "\" [binary]=\"true\"></p-checkbox>";
		case "char":
		case "nchar":
		case "varchar":
		case "nvarchar":
		case "text":
		case "ntext":
			return "<input pInputText id=\"" + fieldName + "\" formControlName=\"" + fieldName + "\" " + validator
					+ " />";
		case "date":
		case "datetime":
		case "datetime2":
		case "smalldatetime":
		case "time":
		case "timestamp":
			return "<p-datepicker id=\"" + fieldName + "\" formControlName=\"" + fieldName
					+ "\" dateFormat=\"yy-mm-dd\" " + validator + "></p-datepicker>";
		case "uniqueidentifier":
			return "<input pInputText id=\"" + fieldName + "\" formControlName=\"" + fieldName + "\" " + validator
					+ "/>";
		case "binary":
		case "varbinary":
		case "image":
			return "<p-fileUpload name=\"" + fieldName + "\" url=\"uploadUrl\"></p-fileUpload>";

		default:
			return "<!-- Tipo no reconocido: " + sqlType + " -->";
		}
	}

	private static String toTitleCase(String input) {
		String[] words = input.split(" ");
		StringBuilder title = new StringBuilder();
		for (String word : words) {
			if (word.length() > 0) {
				title.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase()).append(" ");
			}
		}
		return title.toString().trim();
	}

	private static String generateTable(List<Column> columns, String tableVarName) {
		
		String[] filters = columns.stream().map(Column::getName).toArray(String[]::new);
		
		String filterLst = Arrays.stream(filters)
			    .map(filter -> "'" + filter + "'")
			    .collect(Collectors.joining(", "));		
		
		StringBuilder html = new StringBuilder();
		html.append("<div class=\"card\">\n");
		html.append("  <p-table\n");
		html.append("    #dt1\n");
		html.append("    [value]=\"" + FieldNameFormatter.toCamelCase(tableVarName) + "s\"\n");
		html.append("    dataKey=\"id\"\n");
		html.append("    [rows]=\"5\"\n");
		html.append("    [rowsPerPageOptions]=\"[5, 10, 20]\"\n");
		html.append("    [loading]=\"loading\"\n");
		html.append("    [rowHover]=\"true\"\n");
		html.append("    [globalFilterFields]=\"["+filterLst+"]\"\n");
		html.append("    [showGridlines]=\"true\"\n");
		html.append("    [paginator]=\"true\"\n");
		html.append("    responsiveLayout=\"scroll\"\n");
		html.append("    stripedRows\n");
		html.append("  >\n");
		
		html.append("<ng-template #caption>\n");
		html.append("	<div class=\"flex justify-between items-center flex-column sm:flex-row\">\n");
		html.append("	<button pButton label=\"Clear\" class=\"p-button-outlined mb-2\" icon=\"pi pi-filter-slash\" (click)=\"clear(dt1)\"></button>\n");
		html.append("    	<p-iconfield iconPosition=\"left\" class=\"ml-auto\"> \n");
		html.append("        	<p-inputicon> \n");
		html.append("           <i class=\"pi pi-search\"></i> \n");
		html.append("        	</p-inputicon> \n");
		html.append("        	<input pInputText type=\"text\" (input)=\"onGlobalFilter(dt1, $event)\" placeholder=\"Search keyword\" /> \n");
		html.append("    	</p-iconfield> \n");
		html.append("	&nbsp;&nbsp;&nbsp;<button pButton label=\"Descargar\" class=\"p-button-outlined mb-2\" icon=\"pi pi-download\" (click)=\"download()\"></button>\n");
		html.append("	</div> \n");
		html.append("</ng-template> \n");

		// Header
		html.append("    <ng-template #header>\n");
		html.append("      <tr>\n");
		for (Column col : columns) {
			html.append("        <th style=\"min-width: 12rem\">\n");
			html.append("          <div class=\"flex justify-between items-center\">\n");
			html.append("            " + toLabel(col.getName()) + "\n");
			html.append("            " + generateColumnFilter(col) + "\n");
			html.append("          </div>\n");
			html.append("        </th>\n");
		}
		html.append("      </tr>\n");
		html.append("    </ng-template>\n");

		// Body
		html.append("    <ng-template #body let-row>\n");
		html.append("      <tr  (dblclick)=\"update(row)\">\n");
		for (Column col : columns) {
			html.append("        <td>\n");
			html.append("          " + generateBodyCell(col, "row") + "\n");
			html.append("        </td>\n");
		}
		html.append("      </tr>\n");
		html.append("    </ng-template>\n");

		// Empty and loading templates
		html.append("    <ng-template #emptymessage>\n");
		html.append("      <tr><td colspan=\"" + columns.size() + "\">No data found.</td></tr>\n");
		html.append("    </ng-template>\n");
		html.append("    <ng-template #loadingbody>\n");
		html.append("      <tr><td colspan=\"" + columns.size() + "\">Loading data. Please wait.</td></tr>\n");
		html.append("    </ng-template>\n");

		html.append("  </p-table>\n");
		html.append("</div>");
		return html.toString();
	}

	private static String generateColumnFilter(Column col) {
		String field = col.getName();
		switch (col.getDataType().toLowerCase()) {
		case "varchar":
		case "nvarchar":
		case "text":
			return "<p-columnFilter type=\"text\" field=\"" + field
					+ "\" display=\"menu\" placeholder=\"Search\"></p-columnFilter>";
		case "int":
		case "decimal":
		case "float":
			return "<p-columnFilter type=\"numeric\" field=\"" + field + "\" display=\"menu\"></p-columnFilter>";
		case "bit":
			return "<p-columnFilter type=\"boolean\" field=\"" + field + "\" display=\"menu\"></p-columnFilter>";
		case "date":
		case "datetime":
			return "<p-columnFilter type=\"date\" field=\"" + field
					+ "\" display=\"menu\" placeholder=\"mm/dd/yyyy\"></p-columnFilter>";
		default:
			return "<p-columnFilter field=\"" + field + "\" display=\"menu\"></p-columnFilter>";
		}
	}

	private static String generateBodyCell(Column col, String rowVar) {
		String field = rowVar + "." + col.getName();
		switch (col.getDataType().toLowerCase()) {
		case "bit":
			return "<p-tag [value]=\"" + field + " ? 'Yes' : 'No'\" />";
		case "date":
		case "datetime":
			return "{{ " + field + " | date: 'MM/dd/yyyy' }}";
		case "decimal":
		case "float":
			return "{{ " + field + " | currency:'USD':'symbol' }}";
		default:
			return "{{ " + field + " }}";
		}
	}

	private static String toLabel(String input) {
		return input.replaceAll("([a-z])([A-Z])", "$1 $2").replace("_", " ").toLowerCase();
	}

}
