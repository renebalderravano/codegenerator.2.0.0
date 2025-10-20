package com.codegenerator.generator.template;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
		
		for(Column column : table.getColumns()) {
			columnas.put(column.getName(), column.getDataType());
		}
		String formulario = generatePrimeNgForm(table.getName(),columnas);
		System.out.println(formulario);
		
		return formulario;
	}

	public void createDialog() {

	}

	private static String generatePrimeNgForm(String tableName, Map<String, String> columnTypeMap) {
		StringBuilder formBuilder = new StringBuilder();
		formBuilder.append("<div class=\"card\">\r\n"
				+ "    <div class=\"card flex flex-col gap-4\">\r\n"
				+ "        <div class=\"font-semibold text-xl\"><h3>"+FieldNameFormatter.splitCamelCaseToString(tableName) +"</h3></div>\r\n"
				+ "        <hr>");
		formBuilder.append("<form [formGroup]=\"form\">\n");

		for (Map.Entry<String, String> entry : columnTypeMap.entrySet()) {
			String column = entry.getKey();
			String type = entry.getValue().toLowerCase();
			String label = FieldNameFormatter.splitCamelCaseToString(column);
			
			formBuilder.append("  <div class=\"flex flex-col grow basis-0 gap-2\">\n");
			formBuilder.append("    <label for=\"" + column + "\">" + label + "</label>\n");
			formBuilder.append("    " + getControlHtml(type, column) + "\n");
			formBuilder.append("  </div>\n");
		}

		formBuilder.append("  <button pButton type=\"submit\" label=\"Guardar\"></button>\n");
		formBuilder.append("</form>");
		formBuilder.append("  </div>\n");
		formBuilder.append("</div>\n");
		return formBuilder.toString();
	}

	private static String getControlHtml(String sqlType, String fieldName) {
		switch (sqlType) {
		case "int":
		case "bigint":
		case "smallint":
		case "tinyint":
		case "decimal":
		case "numeric":
		case "float":
		case "real":
			return "<p-inputNumber id=\"" + fieldName + "\" formControlName=\"" + fieldName + "\"></p-inputNumber>";
		case "bit":
			return "<p-inputSwitch id=\"" + fieldName + "\" formControlName=\"" + fieldName + "\"></p-inputSwitch>";
		case "char":
		case "nchar":
		case "varchar":
		case "nvarchar":
		case "text":
		case "ntext":
			return "<input pInputText id=\"" + fieldName + "\" formControlName=\"" + fieldName + "\" />";
		case "date":
		case "datetime":
		case "datetime2":
		case "smalldatetime":
		case "time":
		case "timestamp":
			return "<p-calendar id=\"" + fieldName + "\" formControlName=\"" + fieldName
					+ "\" dateFormat=\"yy-mm-dd\"></p-calendar>";
		case "uniqueidentifier":
			return "<input pInputText id=\"" + fieldName + "\" formControlName=\"" + fieldName + "\" />";
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

	private String getPrimeNgHtml(String controlType, String fieldName, String label) {
		if (controlType == null || fieldName == null || label == null)
			return null;

		switch (controlType.toLowerCase()) {
		case "input":
		case "textbox":
			return "<label for=\"" + fieldName + "\">" + label + "</label>\n" + "<input pInputText id=\"" + fieldName
					+ "\" formControlName=\"" + fieldName + "\" />";
		case "textarea":
			return "<label for=\"" + fieldName + "\">" + label + "</label>\n" + "<textarea pInputTextarea id=\""
					+ fieldName + "\" rows=\"5\" cols=\"30\" formControlName=\"" + fieldName + "\"></textarea>";
		case "dropdown":
		case "select":
			return "<label for=\"" + fieldName + "\">" + label + "</label>\n" + "<p-dropdown id=\"" + fieldName
					+ "\" [options]=\"" + fieldName + "Options\" formControlName=\"" + fieldName + "\"></p-dropdown>";
		case "multiselect":
			return "<label for=\"" + fieldName + "\">" + label + "</label>\n" + "<p-multiSelect id=\"" + fieldName
					+ "\" [options]=\"" + fieldName + "Options\" formControlName=\"" + fieldName
					+ "\"></p-multiSelect>";
		case "calendar":
		case "date":
			return "<label for=\"" + fieldName + "\">" + label + "</label>\n" + "<p-calendar id=\"" + fieldName
					+ "\" formControlName=\"" + fieldName + "\" dateFormat=\"yy-mm-dd\"></p-calendar>";
		case "checkbox":
			return "<p-checkbox inputId=\"" + fieldName + "\" formControlName=\"" + fieldName + "\"></p-checkbox>\n"
					+ "<label for=\"" + fieldName + "\">" + label + "</label>";
		case "radiobutton":
		case "radio":
			return "<p-radioButton name=\"" + fieldName + "\" inputId=\"" + fieldName
					+ "\" value=\"optionValue\" formControlName=\"" + fieldName + "\"></p-radioButton>\n"
					+ "<label for=\"" + fieldName + "\">" + label + "</label>";
		case "toggle":
		case "switch":
			return "<label for=\"" + fieldName + "\">" + label + "</label>\n" + "<p-inputSwitch id=\"" + fieldName
					+ "\" formControlName=\"" + fieldName + "\"></p-inputSwitch>";
		case "slider":
			return "<label for=\"" + fieldName + "\">" + label + "</label>\n" + "<p-slider id=\"" + fieldName
					+ "\" formControlName=\"" + fieldName + "\" [style]=\"{width:'14em'}\"></p-slider>";
		case "spinner":
			return "<label for=\"" + fieldName + "\">" + label + "</label>\n" + "<p-inputNumber id=\"" + fieldName
					+ "\" formControlName=\"" + fieldName + "\"></p-inputNumber>";
		case "password":
			return "<label for=\"" + fieldName + "\">" + label + "</label>\n" + "<p-password id=\"" + fieldName
					+ "\" formControlName=\"" + fieldName + "\"></p-password>";
		case "autocomplete":
			return "<label for=\"" + fieldName + "\">" + label + "</label>\n" + "<p-autoComplete id=\"" + fieldName
					+ "\" formControlName=\"" + fieldName + "\" [suggestions]=\"" + fieldName
					+ "Suggestions\"></p-autoComplete>";
		case "fileupload":
			return "<label for=\"" + fieldName + "\">" + label + "</label>\n" + "<p-fileUpload name=\"" + fieldName
					+ "\" url=\"uploadUrl\"></p-fileUpload>";
		case "button":
			return "<button pButton type=\"submit\" label=\"" + label + "\"></button>";
		default:
			return "<!-- Control no reconocido: " + controlType + " -->";
		}
	}
	
    private static String generateTable(List<Column> columns, String tableVarName) {
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
        html.append("    [showGridlines]=\"true\"\n");
        html.append("    [paginator]=\"true\"\n");
        html.append("    responsiveLayout=\"scroll\"\n");
        html.append("    stripedRows\n");
        html.append("  >\n");

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
        html.append("      <tr>\n");
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
                return "<p-columnFilter type=\"text\" field=\"" + field + "\" display=\"menu\" placeholder=\"Search\"></p-columnFilter>";
            case "int":
            case "decimal":
            case "float":
                return "<p-columnFilter type=\"numeric\" field=\"" + field + "\" display=\"menu\"></p-columnFilter>";
            case "bit":
                return "<p-columnFilter type=\"boolean\" field=\"" + field + "\" display=\"menu\"></p-columnFilter>";
            case "date":
            case "datetime":
                return "<p-columnFilter type=\"date\" field=\"" + field + "\" display=\"menu\" placeholder=\"mm/dd/yyyy\"></p-columnFilter>";
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
        return input.replaceAll("([a-z])([A-Z])", "$1 $2")
                    .replace("_", " ")
                    .toLowerCase();
    }


}




