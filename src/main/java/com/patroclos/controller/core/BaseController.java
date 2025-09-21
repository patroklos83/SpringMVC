package com.patroclos.controller.core;

import com.patroclos.facade.*;
import static com.patroclos.uicomponent.UIModal.*;
import com.patroclos.utils.CustomModelMapper;

import com.patroclos.process.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.patroclos.configuration.converterformatter.InstantToStringFormatter;
import com.patroclos.dto.BaseDTO;
import com.patroclos.exception.SystemException;
import com.patroclos.utils.WebUtils;

import com.patroclos.uicomponent.UISummaryForm;
import com.patroclos.uicomponent.UIInput;
import com.patroclos.uicomponent.UITable;
import com.patroclos.uicomponent.UILayoutForm.ComponentMode;
import com.patroclos.uicomponent.core.DataTableFunction;
import com.patroclos.uicomponent.core.Table;
import com.patroclos.utils.Base64Util;
import com.patroclos.uicomponent.*;

public class BaseController {

	@Autowired
	protected SystemFacade SystemFacade;
	@Autowired
	protected Facade Facade;
	@Autowired
	protected AuthenticationFacade AuthenticationFacade;
	@Autowired
	protected CustomModelMapper CustomModelMapper;
	@Autowired
	protected DataHolder DataHolder;

	@Autowired
	protected CRUDProcess CRUDProcess;

	@Autowired
	protected UICheckbox UICheckbox;
	@Autowired
	protected UITable UITable;
	@Autowired
	protected UIDataTable UIDataTable;
	@Autowired
	protected UIInput UIInput;
	@Autowired
	protected UISummaryForm UISummaryForm;
	@Autowired
	protected UILayoutForm UILayoutForm;
	@Autowired
	protected UIModal UIModal;

	@Autowired
	protected InstantToStringFormatter InstantToStringFormatter;

	@Autowired
	protected WebUtils WebUtils;

	protected ModelAndView pageLoad(String view, ModelMap model)
	{
		model.addAttribute("contextPath", WebUtils.getContextPath());
		return new ModelAndView(view, model);
	}

	protected void setMasterPageInnerForm(ModelMap model, String formName) {		
		model.addAttribute("innerContentform", Base64Util.encode(formName));
	}

	/***
	 * Method to handle table paging 
	 * @param pagingParams
	 * @param model
	 * @return
	 * @throws Exception
	 */
	protected String getTablePaging(@RequestParam Map<String,String> params, ModelMap model) throws Exception {

		String tableId = params.entrySet().stream().filter(k -> k.toString().startsWith("summaryHash"))
				.findFirst().get().getValue();

		Table table = (Table)DataHolder.getDataFromMap(tableId);

		if (table == null)
			throw new Exception(String.format("No table item found in session with id %s", tableId));

		if (table.getComponentMode() == ComponentMode.EDIT) {
			String formHashId = params.entrySet().stream().filter(k -> k.toString().startsWith("hash"))
					.findFirst().get().getValue();

			com.patroclos.controller.core.Form form = (Form) DataHolder.getDataFromMap(formHashId);

			if (form == null)
				throw new Exception(String.format("No form item found in session with hash [%s]", formHashId));	

			if (form.getDirtyDto() == null)
				form.setDirtyDto(form.getDto());

			BaseDTO dirtyDTO = MapTableValuesToDTO(form.getDirtyDto(), params);
			form.setDirtyDto(dirtyDTO);
			DataHolder.addDataToMap(formHashId, form);	
			table = (Table)DataHolder.getDataFromMap(tableId); 
		}

		table.setPagingParams(params);
		table = table.getIsDataTable() ? UIDataTable.draw(table) : UITable.draw(table); // using same tableId as previous [only for paging]

		DataHolder.removeDataFromMap(tableId); // remove older table version
		if (DataHolder.getDataFromMap(table.getId()) == null)
			DataHolder.addDataToMap(table.getId(), table); // add new table version

		return table.getHtml();
	}

	/**
	 * Method to handle data tables Add/Remove button calls
	 * [Add] functionality new elements, works in 2 steps
	 * First step is to Open and Display modal with a datatable
	 * with the available entities for selection
	 * Second step, is to Add [Confirm] the selected entities 
	 * from the modal and update the parent datatable and dirtyDTO.
	 * @param params
	 * @param model
	 * @return
	 * @throws Exception
	 */
	protected String executeTableDataFunc(@RequestParam Map<String,String> params) throws Exception {

		String tableId = params.entrySet().stream().filter(k -> k.toString().startsWith("summaryHash"))
				.findFirst().get().getValue();

		String dataFunction = params.entrySet().stream().filter(k -> k.toString().startsWith("dataFunction_"))
				.findFirst().get().getValue();

		DataTableFunction function = DataTableFunction.valueOf(dataFunction);

		Table table = getTableFromMemoryByParams(params);

		String formHashId = params.entrySet().stream().filter(k -> k.toString().startsWith("hash"))
				.findFirst().get().getValue();

		com.patroclos.controller.core.Form form = (Form) DataHolder.getDataFromMap(formHashId);

		if (form == null)
			throw new Exception(String.format("No form item found in session with hash [%s]", formHashId));	

		BaseDTO dto = form.getDto();
		BaseDTO dirtyDTO = form.getDirtyDto();
		if (dirtyDTO == null) {
			dirtyDTO = CustomModelMapper.mapDTOtoDTO(dto); //copy original dto to dirtyDto
		}

		// Update selected Rows [if any existing on the table object]
		table.setPagingParams(params);
		table = UIDataTable.setSelectedRowsFromPagingParams(table);
		NavigableMap<Integer, Boolean> selectedRows = table.getSelectedRows();

		if (function == DataTableFunction.Remove)
		{
			if (selectedRows != null && selectedRows.size() > 0)
				table = removeDataItem(dirtyDTO, selectedRows, table);
		}
		else if (function == DataTableFunction.DisplayAddModal)
		{
			Table tableInModal = displayAddModal(dirtyDTO, selectedRows, table);
			tableInModal.setComponentMode(ComponentMode.EDIT_IN_MODAL);
			tableInModal = UIDataTable.draw(tableInModal);
			DataHolder.addDataToMap(tableInModal.getId(), tableInModal); // add new table version
			return tableInModal.getHtml();
		}
		else if (function == DataTableFunction.Add)
		{
			String parentTableId = params.entrySet().stream().filter(k -> k.toString().startsWith(MODAL_TABLE_REFERENCE_PARENT_PLACEHOLDER_PREFIX_NAME))
					.findFirst().get().getValue();

			Table parentTable = (Table)DataHolder.getDataFromMap(parentTableId);
			if (parentTable == null)
				throw new Exception(String.format("No parentTable item found in session with id [%s]", parentTableId));

			if (selectedRows != null && selectedRows.size() > 0)
				parentTable = addItem(dirtyDTO, selectedRows, table, parentTable);

			DataHolder.addDataToMap(parentTable.getId(), parentTable); // add new table version

			table = parentTable;
		}
		else
		{
			throw new SystemException("Unknown DataTable Function");
		}

		form.setDirtyDto(dirtyDTO);
		DataHolder.addDataToMap(formHashId, form);

		return redrawTableAndSave(table);
	}

	protected Table getTableFromMemoryByParams(@RequestParam Map<String,String> params) throws Exception {

		String tableId = params.entrySet().stream().filter(k -> k.toString().startsWith("summaryHash"))
				.findFirst().get().getValue();

		Table table = (Table)DataHolder.getDataFromMap(tableId);

		if (table == null)
			throw new Exception(String.format("No table item found in session with id [%s]", tableId));

		return table;
	}

	protected String redrawTableAndSave(Table table) throws Exception {	
		// reset-clear table
		table.setSelectedRows(null);
		table.setPagingParams(null);
		table.setComponentMode(ComponentMode.EDIT);
		table = table.getIsDataTable() ? UIDataTable.draw(table) : UITable.draw(table); // using same tableId as previous [only for paging]

		DataHolder.addDataToMap(table.getId(), table); // add new table version

		return table.getHtml();
	}

	/***
	 * Removes selected items by index, on a datatable
	 * @param dirtyDTO
	 * @param selectedRows
	 * @param table
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Table removeDataItem(BaseDTO dirtyDTO, NavigableMap<Integer, Boolean> selectedRows, Table table) throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = dirtyDTO.getClass().getDeclaredFields();
		for(var field : fields)
		{
			if (field.getType().equals(List.class))
			{
				field.setAccessible(true);

				// Get list type arguments
				ParameterizedType listType = (ParameterizedType) field.getGenericType();
				Class<? extends BaseDTO> dataListType = (Class<? extends BaseDTO>) listType.getActualTypeArguments()[0];
				if (dataListType == table.getDataListType() && table.getTableId().equals(field.getName())) {

					//start from the biggest index to start removing from the List
					selectedRows = selectedRows.descendingMap();

					ArrayList newList = new ArrayList<>();	
					for (int i=0; i < table.getDatalist().size(); i++) {

						if (selectedRows.get(i) != null) // skip do not add in new list the selected to be removed item
							continue;

						newList.add(table.getDatalist().get(i));
					}

					table.setDatalist(newList);
					field.set(dirtyDTO, newList);
				}	
			}
		}

		return table;
	}

	/***
	 * Open the modal on page to display datatable with
	 * available selection of existing items
	 * @param dirtyDTO
	 * @param selectedRows
	 * @param table
	 * @return
	 * @throws Exception 
	 */
	private Table displayAddModal(BaseDTO dirtyDTO, NavigableMap<Integer, Boolean> selectedRows, Table table) throws Exception {

		table = Table.Builder.newInstance()
				.setName("modalDataTable")				
				.setDatalist(CRUDProcess.loadAll(table.getDataListType()))
				.setDatalistType(table.getDataListType()) // get dataType [entity] from datatable which the modal is being opened for
				.setTableId(table.getTableId())
				.setParentTableId(table.getId())
				.build();

		return table;
	}

	/***
	 * Adds new selected items, from a datatable
	 * displayed on a modal
	 * @param dirtyDTO
	 * @param selectedRows
	 * @param table
	 * @param parentTable
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Table addItem(BaseDTO dirtyDTO, NavigableMap<Integer, Boolean> selectedRows, Table table, Table parentTable) throws IllegalArgumentException, IllegalAccessException {

		Field[] fields = dirtyDTO.getClass().getDeclaredFields();
		for(var field : fields)
		{
			if (field.getType().equals(List.class))
			{
				field.setAccessible(true);

				// Get list type arguments
				ParameterizedType listType = (ParameterizedType) field.getGenericType();
				@SuppressWarnings("unchecked")
				Class<? extends BaseDTO> dataListType = (Class<? extends BaseDTO>) listType.getActualTypeArguments()[0];
				if (dataListType == table.getDataListType() && table.getTableId().equals(field.getName())) {

					//start from the biggest index to start removing from the List
					selectedRows = selectedRows.descendingMap();

					ArrayList datalistNew = new ArrayList();
					ArrayList<BaseDTO> datalist = (ArrayList<BaseDTO>) parentTable.getDatalist();
					ArrayList<BaseDTO> datalistPool = (ArrayList<BaseDTO>) table.getDatalist();
					for (Map.Entry<Integer, Boolean> selected : selectedRows.entrySet()){
						int arraySelectedIndex = selected.getKey();
						var itemToAdd = (BaseDTO) datalistPool.get(arraySelectedIndex);
						itemToAdd.setIsNew(false);
						datalistNew.add(itemToAdd);
					}

					if (datalist != null)
						for (BaseDTO entity : datalist) {
							BaseDTO itemToAdd = table.getDatalist().stream().filter(e -> e.getId() == entity.getId()).findFirst().get();
							itemToAdd.setIsNew(false);

							// If the table consists only of 1 item, force check and
							// add only 1 item to the table
							if (parentTable.getIsSingletonDatalist()) {
								if (datalistNew.size() >= 1) {
									if (datalistNew.size() > 1) {
										var itemToRemain = datalistNew.get(0);
										datalistNew.clear();
										datalistNew.add(itemToRemain);
									}
									break;
								}
							}

							if (!parentTable.getIsContainsDuplicates()) {
								if (datalistNew.stream().filter(e -> ((BaseDTO)e).getId() == itemToAdd.getId()).count() == 0)
									datalistNew.add(itemToAdd);
							}	
							else
								datalistNew.add(itemToAdd);
						}

					field.set(dirtyDTO, datalistNew);

					parentTable.setDatalist(datalistNew);
				}	
			}
		}

		return parentTable;
	}


	class DataTableColumnValue {
		Long id;
		String ColumnName;
		Object value;
		String type;
		String tableId;
	}


	/***
	 * Function to map and update UI table values to corresponding DTO ManyToMany,
	 * OneToMany value fields
	 * @param params
	 * @throws Exception 
	 */
	public BaseDTO MapTableValuesToDTO(BaseDTO dto, Map<String, String> params) throws Exception {

		if (params == null) return dto;
		if (params.size() == 0) return dto;

		List<String> tableIdsFound = new ArrayList<>();

		// Step 1. Collect posted params to a Map as to be easy to manipulate
		Map<String, DataTableColumnValue> tableFields = params.entrySet().stream()
				.filter(k -> k.toString().startsWith(com.patroclos.uicomponent.UIDataTable.TABLE_DATAROW_COLUMN_FIELD_PREFIX))
				.collect(Collectors.toMap(
						p -> 
						{ 
							return p.getKey();
						}		
						, p -> 
						{
							DataTableColumnValue key = new DataTableColumnValue();
							String[] keyArr = p.getKey().toString().split(com.patroclos.uicomponent.UIDataTable.TABLE_DATAROW_COLUMN_FIELD_STRING_FORMAT_DELIMITER);
							key.tableId = keyArr[0].replace(com.patroclos.uicomponent.UIDataTable.TABLE_DATAROW_COLUMN_FIELD_PREFIX + "_", "");
							key.type = keyArr[1];
							key.id = Long.valueOf(keyArr[2]);
							key.value = p.getValue() != null ? p.getValue() : null;
							key.ColumnName = keyArr[3];

							if (tableIdsFound == null || !tableIdsFound.contains(key.tableId)) {
								tableIdsFound.add(key.tableId);
							}

							return key;
						}));


		if (tableIdsFound == null || tableIdsFound.size() == 0) return dto;

		for (String tableId : tableIdsFound) {
			Table table = (Table) DataHolder.getDataFromMap(tableId);
			if (table == null)
				throw new Exception(String.format("No table item found in session with id %s", tableId));

			var tableRelatedUpdatedFields = tableFields.values().stream().filter(f -> f.tableId.equals(tableId)).collect(Collectors.toList());
			dto = MapNewColumnValues(dto, tableRelatedUpdatedFields, table);

			DataHolder.addDataToMap(tableId, table);
		}

		return dto;
	}


	@SuppressWarnings("unchecked")
	private BaseDTO MapNewColumnValues(BaseDTO dto, List<DataTableColumnValue> tableRelatedUpdatedFields, Table table) throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = dto.getClass().getDeclaredFields();	
		for(var field : fields)
		{
			if (field.getType().equals(List.class) && field.getName().equalsIgnoreCase(table.getName()))
			{
				field.setAccessible(true);

				List<BaseDTO> childObjects = (List<BaseDTO>) field.get(dto);
				if (childObjects == null || childObjects.size() == 0) continue;
				for (BaseDTO child : childObjects) {

					Field[] fieldsOfChild = child.getClass().getDeclaredFields();
					for(var f : fieldsOfChild)
					{		
						f.setAccessible(true);

						// do not proceed and update values of fields with 
						// other type expect these ...
						if (f.getType() != String.class && f.getType() != Integer.class && f.getType() != Boolean.class
								&& f.getType() != int.class && f.getType() != boolean.class) continue;

						Optional<DataTableColumnValue> updatedValue = tableRelatedUpdatedFields.stream()
								.filter(p -> p.ColumnName.equalsIgnoreCase(f.getName()) && p.id == child.getId())
								.findFirst();

						if (updatedValue.isEmpty()) continue;

						switch (f.getType().toString().toUpperCase()) {
						case "STRING" : 	
							String newValueStr = (String) updatedValue.get().value;
							String prevValueStr = (String) f.get(child);
							f.set(child, newValueStr);
						case "BOOLEAN" :
							String newValueBoolean = (String) updatedValue.get().value;
							boolean newValueTobool = "TRUE".equals(newValueBoolean.toUpperCase()) 
									|| "ON".equals(newValueBoolean.toUpperCase()) ? true : false;
							boolean prevValueBool = (boolean) f.get(child);
							f.set(child, newValueTobool);
						}
					}
				}

				// Update table DataList with the new Column Values
				table.setDatalist(childObjects);
			}
		}

		return dto;
	}

}
