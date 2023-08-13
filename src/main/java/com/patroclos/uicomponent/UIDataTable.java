package com.patroclos.uicomponent;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import com.patroclos.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.patroclos.dto.BaseDTO;
import com.patroclos.exception.SystemException;
import com.patroclos.repository.IRepository;
import com.patroclos.uicomponent.UILayoutForm.ComponentMode;
import com.patroclos.uicomponent.core.ButtonType;
import com.patroclos.uicomponent.core.ColumnDefinition;
import com.patroclos.uicomponent.core.ColumnDefinitionType;
import com.patroclos.uicomponent.core.DataTableFunction;
import com.patroclos.uicomponent.core.Table;
import com.patroclos.utils.DateUtil;

import javassist.expr.FieldAccess;

import static com.patroclos.uicomponent.UIModal.*;

@Component
public class UIDataTable extends UITableBase {

	public static final String DISABLE_SELECT_COLUMN_PLACEHOLDER = "?visibility";
	public static final String EDIT_BUTTONS_PLACEHOLDER = "?EditButtons";
	public static final String TABLE_HTML_UNIQUE_ID_PLACEHOLER = "?id";
	public static final String TABLE_DATAROW_COLUMN_FIELD_CHECKBOX_PLACEHOLDER_SELECTED = "?selected";
	public static final String TABLE_DATAROW_COLUMN_FIELD_PREFIX = "tablefield";
	public static final String TABLE_DATAROW_COLUMN_FIELD_STRING_FORMAT_DELIMITER ="--";
	public static final String TABLE_DATAROW_COLUMN_FIELD_STRING_FORMAT = TABLE_DATAROW_COLUMN_FIELD_PREFIX +"_?id" 
			+ TABLE_DATAROW_COLUMN_FIELD_STRING_FORMAT_DELIMITER 
			+ "%s"
			+ TABLE_DATAROW_COLUMN_FIELD_STRING_FORMAT_DELIMITER
			+ "%s" 
			+ TABLE_DATAROW_COLUMN_FIELD_STRING_FORMAT_DELIMITER
			+ "%s";

	@Autowired
	protected IRepository Repository;

	@Autowired
	protected UIInput UIInput;

	@Autowired
	protected UIButton UIButton;

	@Autowired
	protected UIModal UIModal;

	@SuppressWarnings("unused")
	public Table draw(Table table) throws Exception {
		boolean isFromPagingAction = table.getPagingParams() != null;

		if (table == null) {
			throw new SystemException("No Table object provided!");
		}

		if (table.getTableId() == null) {
			throw new SystemException("No tableId provided!");
		}

		setSelectedRowsFromPagingParams(table);

		String tableHtml = getTableHtml(table);

		if (table.isEditable() && table.getComponentMode() == ComponentMode.EDIT || table.getComponentMode() == ComponentMode.EDIT_IN_MODAL) {
			tableHtml = getTableEditFunctionsHtml(table, tableHtml);
		}
		else
		{
			tableHtml = tableHtml.replace(com.patroclos.uicomponent.UIInput.DISABLE_PLACEHOLDER, com.patroclos.uicomponent.UIInput.DISABLE_COMPONENT);	
			tableHtml = tableHtml.replace(com.patroclos.uicomponent.UIDataTable.DISABLE_SELECT_COLUMN_PLACEHOLDER,  "visibility: collapse");
			tableHtml = tableHtml.replace(com.patroclos.uicomponent.UIDataTable.EDIT_BUTTONS_PLACEHOLDER, "");
		}

		String id = null;
		if (!isFromPagingAction) {
			id = String.format("%s_%s", table.getTableId(), UUID.randomUUID().toString());
			String uniqueHash = Integer.toString(id.hashCode());
			table.setHash(uniqueHash);
			tableHtml = tableHtml.replace("?summaryHash", id);
		}else {
			id = table.getId();
			tableHtml = tableHtml.replace("?summaryHash", table.getId());
		}

		tableHtml = tableHtml.replace(TABLE_HTML_UNIQUE_ID_PLACEHOLER, id);	
		tableHtml = tableHtml.replace("?pagingUrl", table.getPagingUrl());

		table.setHtml(tableHtml);
		table.setId(id);
		return table;
	}

	private String getTableEditFunctionsHtml(Table table, String tableHtml) {

		tableHtml = tableHtml.replace(com.patroclos.uicomponent.UIInput.DISABLE_PLACEHOLDER, com.patroclos.uicomponent.UIInput.ENABLE_INPUT);
		tableHtml = tableHtml.replace(com.patroclos.uicomponent.UIDataTable.DISABLE_SELECT_COLUMN_PLACEHOLDER, com.patroclos.uicomponent.UIInput.ENABLE_INPUT);		
		tableHtml += "\n<input type='hidden' id='dataFunction_?summaryHash' name='dataFunction_?summaryHash' val=''/>";

		if (table.getComponentMode() == ComponentMode.EDIT && !table.isDisableAddRemoveButtons()) {
			tableHtml = tableHtml.replace(com.patroclos.uicomponent.UIDataTable.EDIT_BUTTONS_PLACEHOLDER, getDataFunctionButtons());			
			tableHtml += getTableDataFuctions(table);
		}
		else {
			// for cases where table is opened in a modal, hold the tableid of the parent table which called this modal	
			tableHtml = tableHtml.replace(com.patroclos.uicomponent.UIDataTable.EDIT_BUTTONS_PLACEHOLDER, "");				
			tableHtml += getTableDataFuctionAddConfirm(table);		
		}

		String addRemoveFuncId = UUID.randomUUID().toString().replace("_", "").replace("-", "").substring(0, 12);
		tableHtml = tableHtml.replace("?addremoveid", addRemoveFuncId);
		tableHtml = tableHtml.replace(MODAL_CONFIRM_BUTTON_PLACEHOLDER_UNIQUE_ID, addRemoveFuncId);

		if (table.getParentTableId() != null) {		
			tableHtml = tableHtml.replace(MODAL_TABLE_REFERENCE_PARENT_PLACEHOLDER_ID, table.getParentTableId());
		}

		return tableHtml;
	}

	private String getDataFunctionButtons() {	
		String editButtons = 
				"\n" 
						+ UIButton.draw("Add", ButtonType.Javascript, "executeTableAdd_?addremoveid();")
						+ UIButton.draw("Remove", ButtonType.Javascript, "executeTableRemove_?addremoveid();")	
						+ "\n";

		return editButtons;
	}

	private String getTableColumnsHtml(Class<? extends BaseDTO> datalistType, Map<String, ColumnDefinition> columnDefinitions) {
		StringBuilder tableRows = new StringBuilder();

		List<Field> allFields = getFieldList(datalistType);	
		int c = allFields.size();
		for (int i = 0; i < c; i++) {	

			if (i == 0)
				tableRows.append("\n<th></th>"); // add an empty header for selectable row checkboxes

			Field field = allFields.get(i);

			//if (field.getType().getPackageName().startsWith(SystemUtil.ENUMS_PACKAGE)) continue;

			final String columnName = field.getName();
			String column = columnName;
			if (columnDefinitions != null && columnDefinitions.size() > 0) {
				Optional<ColumnDefinition> definition = columnDefinitions.values().stream().filter(f -> f.getColumnDbName().equals(columnName)).findFirst();
				if (!definition.isEmpty()) 
					column = definition.get().getColumnAlias() != null ? definition.get().getColumnAlias() : columnName;
				else
					continue; // if definitions are present, display only these columns defined
			}

			tableRows.append("\n<th>" + column.toUpperCase() + "</th>");
		}

		return tableRows.toString();
	}

	private synchronized String getTableHtml(Table table) throws Exception {
		StringBuilder tableRows = new StringBuilder();
		int rowCount = 0;
		int selectedPagingIndex = SUMMARY_DEFAULT_PAGING_INDEX;

		if (table.getPagingParams() != null)
			selectedPagingIndex = Integer.parseInt(
					table.getPagingParams().entrySet().stream()
					.filter(k -> k.toString().startsWith("summarySelectedIndex"))
					.findFirst().get().getValue());

		tableRows.append("\n<div id='?id' class='" + DISABLE_PLACEHOLDER + "' style='z-index: -1000'>");
		tableRows.append(EDIT_BUTTONS_PLACEHOLDER);	
		tableRows.append("\n<div style='overflow-x:auto !important; height:270px; margin: 10px'>");
		tableRows.append(getTableUniqueHash());
		tableRows.append("\n<table id='table_?id' class='table'>");
		tableRows.append("\n<thead>");
		tableRows.append(getTableColumnsHtml(table.getDataListType(), table.getColumnDefinitions()));
		tableRows.append("\n</thead>");
		tableRows.append("\n<tbody>");

		if (table.getDatalist() == null || table.getDatalist().isEmpty()) {
			tableRows.append("<tr>");
			tableRows.append("<td><span>Empty data</span></td>");
			tableRows.append("</tr>");
			tableRows.append("<tr>");
			tableRows.append("</tr>");
			tableRows.append("</tbody>");
			tableRows.append("</table>");
			tableRows.append("</div>");
			tableRows.append("</div>");
			return tableRows.toString();
		}


		rowCount = table.getDatalist().size();

		int recordIndex = selectedPagingIndex * PAGING_ROWS - PAGING_ROWS + 1;
		int currentRow = 0;
		int rowsToDisplay = 0;

		for (@SuppressWarnings("unused") var dataItem : table.getDatalist()) {
			currentRow++;

			if (selectedPagingIndex != SUMMARY_DEFAULT_PAGING_INDEX && currentRow < recordIndex)
			{
				continue;
			}

			rowsToDisplay++;

			tableRows.append("<tr class='accordion-toggle visiblerow'>");

			List<Field> allFields = getFieldList(table.getDataListType());
			int c = allFields.size();
			for (int i = 0; i < c; i++) {	

				int arrayIndex = currentRow - 1;

				// The first column is the Selected Checkbox column
				if (i == 0) {
					String checkBoxColumnHtml = getSelectCheckBoxColumn(arrayIndex);
					if (table.getSelectedRows() != null) {				
						if (table.getSelectedRows().get(arrayIndex) != null && table.getSelectedRows().get(arrayIndex) == true)
							checkBoxColumnHtml = checkBoxColumnHtml.replace(TABLE_DATAROW_COLUMN_FIELD_CHECKBOX_PLACEHOLDER_SELECTED, "checked");
					}
					else
						checkBoxColumnHtml = checkBoxColumnHtml.replace(TABLE_DATAROW_COLUMN_FIELD_CHECKBOX_PLACEHOLDER_SELECTED, "");

					tableRows.append(checkBoxColumnHtml);
				}


				Field field = allFields.get(i);
				final String columnName = field.getName();
				String column = columnName;
				ColumnDefinition colDefinition = null;
				if (table.getColumnDefinitions() != null && table.getColumnDefinitions().size() > 0) {
					Optional<ColumnDefinition> definition = table.getColumnDefinitions().values().stream().filter(f -> f.getColumnDbName().equals(columnName)).findFirst();
					if (definition.isEmpty()) 
						continue; // if definitions are present, display only these columns defined
					else
						colDefinition = definition.get();
				}

				tableRows.append(setColumnValue(field, colDefinition, table.getDatalist().get(arrayIndex), table));
			}

			tableRows.append("\n</tr>");

			if (rowsToDisplay == PAGING_ROWS)
				break;
		}

		if (rowCount == 0) {
			tableRows.append("\n<tr>");
			tableRows.append("\n<td><span>No data</span></td>");
			tableRows.append("\n</tr>");
		}

		tableRows.append("\n</tbody>");
		tableRows.append("\n</table>");
		tableRows.append("\n</div>");

		String pagination = getPagination(rowCount, selectedPagingIndex, table);

		int displayFromRecordIndex = rowCount > 0 ? recordIndex : 0;
		int displayToRecordIndex = recordIndex + PAGING_ROWS - 1;
		displayToRecordIndex = displayToRecordIndex > rowCount ? rowCount : displayToRecordIndex;
		String footerText = String.format("Displaying %d to %d of total %d record(s)", displayFromRecordIndex, displayToRecordIndex, rowCount);
		tableRows.append("<div><div style='float:left; padding-top: 10px; padding-left:10px'><span style='font-weight: normal'>" + footerText + "</span></div><div style='float:right; padding-right: 10px'>" + pagination + "</div></div>");
		tableRows.append("</div>");
		return tableRows.toString();
	}

	private List<Field> getFieldList(Class<? extends BaseDTO> datalistType){			
		return com.patroclos.utils.ReflectionUtil.getFieldListOfDTO(datalistType);
	}

	private String getSelectCheckBoxColumn(int index) {
		return String.format("<td><input type='checkbox' "
				+" name='selected_?id_%s' id='selected_?id_%s' "
				+ TABLE_DATAROW_COLUMN_FIELD_CHECKBOX_PLACEHOLDER_SELECTED
				+ " style='" + DISABLE_SELECT_COLUMN_PLACEHOLDER + "'/></td>", 
				Integer.toString(index), Integer.toString(index));
	}

	private String setColumnValue(Field field, ColumnDefinition colDefinition, Object object, Table table) throws IllegalArgumentException, IllegalAccessException, SecurityException {
		field.setAccessible(true);
		Class<?> fieldType = field.getType();

		if (fieldType == List.class) {
			return ""; //Omit these types
		}

		Object value = field.get(object);	

		// if it is a DTO object and has a 'name' field
		if (BaseDTO.class.isAssignableFrom(fieldType))
		{
			if (object != null) {
				BaseDTO dto = (BaseDTO) field.get(object);
				if (dto != null) {
					Field nameField = null;
					try {
						nameField = dto.getClass().getDeclaredField("name");
					} catch (NoSuchFieldException e) {
					} catch (SecurityException e) {
					}
					
					// Display either the getName() implementation fo the referenced Object
					// or the Primary Key id
					if (nameField != null) {
						nameField.setAccessible(true);
						value = nameField.get(dto);
					}
					else
						value = dto.getId();
				}
			}
		}

		if (value != null)
		{
			if (fieldType == String.class || fieldType == Long.class) {
				String stringValue = (String) fieldType.cast(field.get(object));
				// do something with the string value
			}
			else if (fieldType == LocalDateTime.class) { //DateTime column type
				value = DateUtil.convertDbDateToUiDateFormat(value.toString());
			}

		}
		else
			value = "";

		String fieldNameFormat = String.format(TABLE_DATAROW_COLUMN_FIELD_STRING_FORMAT, table.getTableId(), ((BaseDTO)object).getId(), field.getName());
		String cValue = "<td>" + value.toString() + "</td>";

		if (colDefinition != null && colDefinition.getType() == ColumnDefinitionType.EDITABLE && table.getComponentMode() == ComponentMode.EDIT) {
			String strfieldType = fieldType.getSimpleName();
			switch (strfieldType.toUpperCase()) {
				case "BOOLEAN" -> cValue = String.format("<td><input type='checkbox' class='valuedatafield' name='"+ fieldNameFormat +"' %s /></td>", (Boolean)value == true ? "checked" : "");				
				case "STRING" -> cValue = "<td><input type='text' class='valuedatafield' name='"+ fieldNameFormat +"' value='" + value.toString() + "'/></td>";
			}
		}

		return cValue;
	}

	/***
	 * Each DataTable has a column for selecting each row
	 * In order to hold the selected rows, when changing 
	 * table paging, update these values and hold them 
	 * in the Table object
	 * @param table
	 */
	public Table setSelectedRowsFromPagingParams(Table table) {

		TreeMap<Integer, Boolean> selectedRows = table.getSelectedRows();

		if (table.getPagingParams() == null || table.getPagingParams().size() == 0) return table;

		Map<Integer, Boolean> selectedRowCheckBoxes = table.getPagingParams()
				.entrySet()
				.stream().filter(e -> e.getKey().startsWith("selected_"))
				.collect(Collectors.toMap(e -> Integer.parseInt(e.getKey().split("_")[3]), 
						e -> e.getValue() != null && e.getValue().equals("on") ? true : false));


		if (selectedRowCheckBoxes == null || selectedRowCheckBoxes.size() == 0) return table;

		if (selectedRows == null)
			selectedRows = new TreeMap<Integer, Boolean>();

		for (Map.Entry<Integer, Boolean> entry : selectedRowCheckBoxes.entrySet()) {		
			selectedRows.put(entry.getKey(), entry.getValue());
		}

		table.setSelectedRows(selectedRows);

		return table;
	}

	private String getTableDataFuctions(Table table) {

		String javascriptDataFucntions = ""
				+ "<script>"
				+ "\n"
				+ "\n"
				+ "\n"
				+ "\n";


		if (table.getAddButtonGetUILayoutUrl() != null) {		
			javascriptDataFucntions += "" 
					+ "\n"
					+ "\n"
					+ "\n function executeTableAdd_?addremoveid(){"
					+ "\n"
					+ "\n    $('" + MODAL_BODY_DIV_ID + "').empty();";

			if (table.getAddButtonUILayoutTitle() != null && !table.getAddButtonUILayoutTitle().isEmpty()) {
				javascriptDataFucntions += "\n 	 $('" + MODAL_TITLE_ID + "').text('" + table.getAddButtonUILayoutTitle() + "'); ";
			}

			javascriptDataFucntions += "\n 	 $('" + MODAL_ID + "').modal('show'); "
					+ "\n"
					+ "\n    $('" + MODAL_CONFIRM_BUTTON_ID + "').removeAttr('onclick');"
					+ "\n    $('" + MODAL_CONFIRM_BUTTON_ID + "').attr('onClick', 'executeTableAddConfirm_" + MODAL_CONFIRM_BUTTON_PLACEHOLDER_UNIQUE_ID + "();');"
					+ "\n"
					+ "\n    $('input[name=\"summaryHash_?summaryHash\"]').val('?summaryHash');"
					+ "\n	 var formValues= $('input[name*=\"selected_?id\"], input[id=\"hash\"], input[name*=\"summaryHash_?summaryHash\"], input[name=\"dataFunction_?summaryHash\"]').serialize();"
					+ "\n	 executeProcess('" + table.getAddButtonGetUILayoutUrl() + "', formValues, false, '" + MODAL_BODY_DIV_ID + "');"
					+ "\n }"
					+ "\n";
		}
		else {
			javascriptDataFucntions += ""
					+ "\n"
					+ "\n function executeTableAdd_?addremoveid(){"
					+ "\n"
					+ "\n    $('" + MODAL_BODY_DIV_ID + "').empty();";

			if (table.getAddButtonUILayoutTitle() != null && !table.getAddButtonUILayoutTitle().isEmpty()) {
				javascriptDataFucntions += "\n 	 $('" + MODAL_TITLE_ID + "').text('" + table.getAddButtonUILayoutTitle() + "'); ";
			}

			javascriptDataFucntions +=  "\n    $('" + MODAL_ID + "').modal('show');"
					+ "\n"
					+ "\n    $('input[name=\"summaryHash_?summaryHash\"]').val('?summaryHash');"
					+ "\n    $('input[name=\"dataFunction_?summaryHash\"]').val('" + DataTableFunction.DisplayAddModal + "');"
					+ "\n	 var formValues= $('input[name*=\"selected_?id\"], input[id=\"hash\"], input[name*=\"summaryHash_?summaryHash\"], input[name=\"dataFunction_?summaryHash\"]').serialize();"
					+ "\n	 executeProcess('?dataFunctionUrl', formValues, false, '" + MODAL_BODY_DIV_ID + "');"
					+ "\n"
					+ "\r\n  }";
		}

		javascriptDataFucntions += "" 
				+ "\n"
				+ "\n"
				+ "\n"
				+ "\n"
				+ "\n function executeTableRemove_?addremoveid(){"
				+ "\n    $('input[name=\"summaryHash_?summaryHash\"]').val('?summaryHash');"
				+ "\n    $('input[name=\"dataFunction_?summaryHash\"]').val('" + DataTableFunction.Remove + "');"
				+ "\n	 var formValues= $('input[name*=\"selected_?id\"], input[id=\"hash\"], input[name*=\"summaryHash_?summaryHash\"], input[name=\"dataFunction_?summaryHash\"]').serialize();"
				+ "\n	 executeProcess('?dataFunctionUrl', formValues, false, '#?id');"
				+ "\n }"
				+ "\r\n"
				+ "\n"
				+ "\n"
				+ "</script>";


		javascriptDataFucntions = javascriptDataFucntions.replace("?dataFunctionUrl", "tableDataFunc");
		javascriptDataFucntions += getTableDataFuctionAddConfirm(table);

		return javascriptDataFucntions;

	}

	/***
	 * Call this javascript method to confirm the addition
	 * of selected rows from the modal DataTable available
	 * selections.
	 * @return
	 */
	private String getTableDataFuctionAddConfirm(Table table) {
		String javascriptDataFucntions = UIModal.getModalConfirmButtonScript();
		if (table.getAddButtonActionUrl() != null) {
			javascriptDataFucntions = javascriptDataFucntions.replace("?dataFunctionUrl", table.getAddButtonActionUrl())
					.replace(MODAL_TABLE_REFERENCE_PARENT_PLACEHOLDER_ID, TABLE_HTML_UNIQUE_ID_PLACEHOLER);
		}
		else
		{
			javascriptDataFucntions = javascriptDataFucntions.replace("?dataFunctionUrl", "tableDataFunc");
		}

		return javascriptDataFucntions;

	}
}
