package com.patroclos.uicomponent;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Component;

import com.patroclos.exception.SystemException;
import com.patroclos.repository.IRepository;
import com.patroclos.uicomponent.UIInput.Input;
import com.patroclos.uicomponent.core.ColumnDefinition;
import com.patroclos.uicomponent.core.ColumnDefinitionType;
import com.patroclos.uicomponent.core.DbFieldType;
import com.patroclos.uicomponent.core.Table;
import com.patroclos.utils.DateUtil;
import com.patroclos.utils.WebUtils;

@Component
public class UITable extends UIComponentTemplate{

	@Autowired
	private IRepository Repository;
	@Autowired
	private WebUtils WebUtils;

	private final int PAGING_ROWS = 4;
	public static int SUMMARY_DEFAULT_PAGING_INDEX = 1;
	public static String SUMMARY_PAGING_MAPPING = "summaryPaging";
	private static int MAX_NUMBER_OF_PAGING_BUTTONS = 7;

	public Table draw(Table table) {
		try
		{
			return drawTable(table, null);
		}
		catch (Exception e)
		{
			throw new SystemException(e);
		}
	}

	public Table draw(Table table, Map<String,String> searchParams) {
		try
		{
			return drawTable(table, searchParams);
		}
		catch (Exception e)
		{
			throw new SystemException(e);
		}
	}

	private Table drawTable(Table table, Map<String,String> searchParams) throws Exception {
		if (table == null) {
			throw new SystemException("No Table object provided!");
		}

		if (table.getSqlQuery() == null && table.getSqlRowSet() == null) {
			throw new SystemException("No sql or resultSet to populate table!");
		}

		if (table.getTableId() == null) {
			throw new SystemException("No tableId provided!");
		}

		SqlRowSet resultSet = null;
		boolean isFromPagingAction = false;

	//	int selectedPagingIndex = SUMMARY_DEFAULT_PAGING_INDEX;
		if (table.getSqlRowSet() != null) {
			resultSet = table.getSqlRowSet();
//			if (table.getPagingParams() != null)
//				selectedPagingIndex = Integer.parseInt(
//						table.getPagingParams().entrySet().stream()
//						.filter(k -> k.toString().startsWith("summarySelectedIndex"))
//						.findFirst().get().getValue());
			isFromPagingAction = true;
		}else
		{	
			//set expandable row foreign-link parameter first
			MapSqlParameterSource sqlParams = new MapSqlParameterSource();
			if (table.getColumnDefinitions() != null && table.getColumnDefinitions().size() > 0) {					
				var expandableRowsDefinitions = table.getColumnDefinitions().values().stream().filter(c -> c.getType() == ColumnDefinitionType.EXPANDABLE_ROW
						&& c.getValue() != null).collect(Collectors.toList());

				if (expandableRowsDefinitions != null && expandableRowsDefinitions.size() > 0) {
					for(var colDef : expandableRowsDefinitions) {
						sqlParams.addValue(colDef.getColumnDbName().toUpperCase(), colDef.getValue().toString());
					}
				}
			}

			StringBuilder sql = new StringBuilder();
			sqlParams = setWhereStatement(table.getSqlQuery(), sqlParams, searchParams, table.getInputFilters(), table.getSqlRowSet(), sql);

			resultSet = Repository.customNativeQuery(sql.toString(), sqlParams);
			table.setSqlRowSet(resultSet);
		}

		String tableHtml = getTableHtml(resultSet, table.getPagingParams(), table.getColumnDefinitions());
		String id = null;
		if (!isFromPagingAction) {
			id = String.format("%s_%s", table.getTableId(), UUID.randomUUID().toString());
			String uniqueHash = Integer.toString(id.hashCode());
			table.setHash(uniqueHash);
			tableHtml = tableHtml.replace("?summaryHash", id);
			tableHtml = wrapInCard(tableHtml);
		}else {
			id = table.getId();
			tableHtml = tableHtml.replace("?summaryHash", table.getId());
		}

		tableHtml = tableHtml.replace("?id", id);		
		//String baseUrl = WebUtils.getBaseUrl();
		//tableHtml = tableHtml.replace("?pagingUrl", baseUrl + "/" + table.getPagingUrl());
		tableHtml = tableHtml.replace("?pagingUrl", table.getPagingUrl());
		table.setHtml(tableHtml);
		table.setId(id);
		return table;
	}

	private StringBuilder initSQL(StringBuilder sql, String sqlQuery) {
		sql.setLength(0);
		sql.append(sqlQuery.toUpperCase());
		if (sql.indexOf("WHERE") < 0) {
			sql.append(" WHERE 1=1 ");
		}
		return sql;
	}

	private MapSqlParameterSource setWhereStatement(
			String sqlQuery, 
			MapSqlParameterSource sqlParams,
			Map<String,String> allParams, 
			Map<String,Input> inputFilters, 
			SqlRowSet results, 
			StringBuilder sql) {

		if (sqlQuery != null && results == null)
			sql = initSQL(sql, sqlQuery);

		if (allParams != null && inputFilters != null) {
			for (Entry<String, String> paramEntry: allParams.entrySet()) {
				String paramKey = paramEntry.getKey();
				if (paramKey != null){
					if (paramEntry.getValue() != null){
						if (paramEntry.getValue().trim() != ""){


							String paramKeyDate = paramKey;
							if (paramKeyDate.endsWith(UIInput.DATETIME_FROM_NAME_SUFFIX))
								paramKey = paramKey.replace(UIInput.DATETIME_FROM_NAME_SUFFIX, "");
							else
								paramKey = paramKey.replace(UIInput.DATETIME_TO_NAME_SUFFIX, "");

							Input i = inputFilters.get(paramKey);

							if (i.isDbPrivateKey()) {
								sql = initSQL(sql, sqlQuery);
								sqlParams = new MapSqlParameterSource();
							}

							if (i.getType().equals(UIInputType.DateTime)) {
								String paramName = "from" + i.getDbField();
								if (paramKeyDate.endsWith(UIInput.DATETIME_FROM_NAME_SUFFIX)) {
									sql.append(" AND " + i.getDbField() + " >= :" + paramName);
								}
								else {
									paramName = "to" + i.getDbField();
									sql.append(" AND " + i.getDbField() + " < :" + paramName);
								}
								String dbDate = DateUtil.convertUiDateToDbDateFormat(paramEntry.getValue().toString());
								sqlParams.addValue(paramName, dbDate);
							}
							else if (i.getType().equals(UIInputType.Text) && i.getDbFieldType() == DbFieldType.Text) {
								sql.append(" AND UPPER(" + i.getDbField() + ") LIKE UPPER(:"+ i.getDbField() +")");
								sqlParams.addValue(i.getDbField(), paramEntry.getValue().toString());
							}
							else {
								sql.append(" AND " + i.getDbField() + " = :"+ i.getDbField());		
								sqlParams.addValue(i.getDbField(), paramEntry.getValue().toString());
							}

							if (i.isDbPrivateKey())
								break;
						}
					}
				}
			}
		}

		return sqlParams;
	}

	private String getTableColumnsHtml(SqlRowSet resultSet, Map<String, ColumnDefinition> columnDefinitions) {
		StringBuilder tableRows = new StringBuilder();
		SqlRowSetMetaData m = resultSet.getMetaData();	
		resultSet.beforeFirst();
		int c = m.getColumnCount();
		for (int i = 1; i <= c; i++) {			
			String columnAlias = m.getColumnLabel(i);//get column label/alias not actual name
			String columnName = m.getColumnName(i);
			if (columnAlias != null) {
				columnName = columnAlias;
			}			
			//If column alias was specified in columnDefinitions override the columnName with this value
			String colLinkName = columnName.toUpperCase();
			if (columnDefinitions != null && columnDefinitions.get(colLinkName) != null) {
				ColumnDefinition columnDef = columnDefinitions.get(colLinkName);
				if (columnDef != null && columnDef.getColumnAlias() != null) {
					columnName = columnDef.getColumnAlias().toUpperCase();
				}
			}

			tableRows.append("<th>" + columnName + "</th>");
		}
		return tableRows.toString();
	}

	private synchronized String getTableHtml(SqlRowSet resultSet,  Map<String, String> pagingParams, Map<String, ColumnDefinition> columnDefinitions) throws Exception {
		StringBuilder tableRows = new StringBuilder();
		int rowCount = 0;
		int selectedPagingIndex = SUMMARY_DEFAULT_PAGING_INDEX;

		if (pagingParams != null)
			selectedPagingIndex = Integer.parseInt(
					pagingParams.entrySet().stream()
					.filter(k -> k.toString().startsWith("summarySelectedIndex"))
					.findFirst().get().getValue());

		tableRows.append("<div id='?id'>");
		tableRows.append("<div style='overflow-x:auto !important;height: 55vh'>");
		tableRows.append("<table class='table'>");
		tableRows.append("<thead>");
		tableRows.append(getTableColumnsHtml(resultSet, columnDefinitions));
		tableRows.append("</thead>");
		tableRows.append("<tbody>");

		if (resultSet == null) {
			tableRows.append("<tr>");
			tableRows.append("<td><span>Empty data</span></td>");
			tableRows.append("</tr>");
		}

		resultSet.beforeFirst();
		while (resultSet.next()) {
			rowCount++;
		}

		int recordIndex = selectedPagingIndex * PAGING_ROWS - PAGING_ROWS + 1;

		SqlRowSetMetaData m = resultSet.getMetaData();
		resultSet.beforeFirst();
		int currentRow = 0;
		int rowsToDisplay = 0;

		while (resultSet.next()) {
			currentRow++;

			if (selectedPagingIndex != SUMMARY_DEFAULT_PAGING_INDEX && currentRow < recordIndex)
			{
				continue;
			}

			rowsToDisplay++;

			tableRows.append("<tr class='accordion-toggle visiblerow'>");
			
			int count = m.getColumnCount();
			for(int i = 1; i<=count; i++) {
				String columnName = m.getColumnLabel(i);
				tableRows.append(setColumnValue(columnName, resultSet, columnDefinitions));
			}
//			for(String columnName : m.getColumnNames())
//			{		
//				tableRows.append(setColumnValue(columnName, resultSet, columnDefinitions));
//			}
			tableRows.append("</tr>");

			//always add extra hidden row for adding expandable row nested table
			tableRows.append("<tr class='hiddenRow collapse' style='backround-color:white !important;'>\r\n"
					+ "            <td colspan='12'>\r\n"
					+ "				<div class='divExpandableRow accordian-body'> \r\n"
					+ "              </div> \r\n"
					+ "          </td>\r\n"
					+ "        </tr>\r\n"
					+ "      ");

			if (rowsToDisplay == PAGING_ROWS)
				break;
		}

		if (rowCount == 0) {
			tableRows.append("<tr>");
			tableRows.append("<td><span>No data</span></td>");
			tableRows.append("</tr>");
		}

		tableRows.append("</tbody>");
		tableRows.append("</table>");
		tableRows.append("</div>");

		String pagination = getPagination(rowCount, selectedPagingIndex, pagingParams);

		int displayFromRecordIndex = rowCount > 0 ? recordIndex : 0;
		int displayToRecordIndex = recordIndex + PAGING_ROWS - 1;
		displayToRecordIndex = displayToRecordIndex > rowCount ? rowCount : displayToRecordIndex;
		String footerText = String.format("Displaying %d to %d of total %d record(s)", displayFromRecordIndex, displayToRecordIndex, rowCount);
		tableRows.append("<div><div style='float:left; padding-top: 10px'><span style='font-weight: normal'>" + footerText + "</span></div><div style='float:right'>" + pagination + "</div></div>");
		tableRows.append("</div>");
		return tableRows.toString();
	}

	private String setColumnValue(String columnName, SqlRowSet resultSet, Map<String, ColumnDefinition> columnDefinitions) {
		SqlRowSetMetaData m = resultSet.getMetaData();		
		int index = resultSet.findColumn(columnName);
		int columnType = m.getColumnType(index);

		Object value = Optional.ofNullable(resultSet.getObject(index)).orElse("");

		if (columnType == 93 && value != "") { //DateTime column type
			value = DateUtil.convertDbDateToUiDateFormat(value.toString());
		}

		String columnValue = "<td><span>" + value.toString() + "</span></td>";

		String colLinkName = columnName.toUpperCase();
		if (columnDefinitions != null && columnDefinitions.get(colLinkName) != null) {
			ColumnDefinition columnDef = columnDefinitions.get(colLinkName);
			columnValue = "<td>?expand<span></span>?rowScript</td>";

			if (columnDef.getType() == ColumnDefinitionType.EXPANDABLE_ROW) {
				String expandableButtonId = UUID.randomUUID().toString();
				columnValue = columnValue.replace("?rowScript", "<script>"
						+ "    "
						+ "  $('#"+expandableButtonId+"').click(function() {\r\n"
						+ "         console.log('calling post for expandable row');" 
						+ "        var thisButton = $(this); \n"         
						+ "        var divExpandable = thisButton.parents('tr')\r\n"
						+ "          .closest('tr')\r\n"
						+ "          .next('.hiddenRow')"
						+ "          .find('.divExpandableRow'); \n"
						+ "        if (!thisButton.parents('tr').closest('tr').next('.hiddenRow').is(':visible')){ \n"
						
						+ "        executeProcess('" + columnDef.getExpandableRowActionLink().replace("?id", value.toString()) + "', null, false, divExpandable);"
						
						//+ "        $.post('"+ columnDef.getExpandableRowActionLink().replace("?id", value.toString()) +"', '', function(data){\r\n"	                           
						//+ "         divExpandable.html(data); \n"
						//+ "        });\r\n"
						+ "} \n"
						+ " else \n"
						+ "{ \n"
						+ "          divExpandable.html('');"
						+ "};"
						+ ""    
						+ "          thisButton.parents('tr')\r\n"
						+ "          .closest('tr')\r\n"
						+ "          .next('.hiddenRow').toggle();"
						+ "});"
						+ ""
						+"</script>");
				columnValue = columnValue.replace("?expand", "<button id='" + expandableButtonId +"' class='btn btn-default btn-xs'><span>+</span></button>");
			}else if (columnDef.getType() == ColumnDefinitionType.CLICKABLE_LINK)
			{
				columnValue = "<td><span><a href='?link' class='link-primary' target='_blank'>"+ value.toString() +"</a></span></td>";
				columnValue = columnValue.replace("?link", columnDefinitions.get(colLinkName).getActionLink());
				columnValue = columnValue.replace("?id", value.toString());
			}
		}

		return columnValue;
	}

	private String getPagination(int rowCount, int selectedPagingIndex, Map<String, String> pagingParams) {

		int pagingSize = 0;

		if (rowCount % PAGING_ROWS > 0) {
			pagingSize = rowCount / PAGING_ROWS + 1;
		}
		else
		{
			pagingSize = rowCount / PAGING_ROWS;
		}

		int selectedPagingIndexIsLast = 0;
		int selectedStartPagingIndex = 0;
		int selectedEndPagingIndex = 0;
		if (pagingParams != null) {
			selectedPagingIndexIsLast = Integer.parseInt(
					pagingParams.entrySet().stream()
					.filter(k -> k.toString().startsWith("summarySelectedIndexLast"))
					.findFirst().get().getValue());

			selectedStartPagingIndex = Integer.parseInt(
					pagingParams.entrySet().stream()
					.filter(k -> k.toString().startsWith("summarySelectedIndexStart"))
					.findFirst().get().getValue());

			selectedEndPagingIndex = Integer.parseInt(
					pagingParams.entrySet().stream()
					.filter(k -> k.toString().startsWith("summarySelectedIndexEnd"))
					.findFirst().get().getValue());
		}


		StringBuilder sb = new StringBuilder();
		String pagingUniqueId = UUID.randomUUID().toString();
		String pagingMethodUniqueId = pagingUniqueId.substring(0, 10).replace("-", "");
		sb.append("<form id='summaryNavForm_?pagingUniqueId' method='POST' action='?pagingUrl'>");
		sb.append("<input type='hidden' id='summaryHash_?summaryHash' name='summaryHash_?summaryHash' val='?summaryHash'/>"); //used to save table resultset state in DataHolder object
		sb.append("<input type='hidden' id='summaryCurrentIndex_?pagingUniqueId' name='summaryCurrentIndex_?pagingUniqueId' val='?currentIndex'/>");
		sb.append("<input type='hidden' id='summarySelectedIndex_?pagingUniqueId' name='summarySelectedIndex_?pagingUniqueId'/>");
		sb.append("<input type='hidden' id='summarySelectedIndexLast_?pagingUniqueId' name='summarySelectedIndexLast_?pagingUniqueId' val='0'/>");
		sb.append("<input type='hidden' id='summarySelectedIndexStart_?pagingUniqueId' name='summarySelectedIndexStart_?pagingUniqueId' val='?startPagingIndex'/>");
		sb.append("<input type='hidden' id='summarySelectedIndexEnd_?pagingUniqueId' name='summarySelectedIndexEnd_?pagingUniqueId' val='?endPagingIndex'/>");

		// Previous Button
		sb.append("<nav aria-label=\"...\">\r\n" + 
				"  <ul class=\"pagination\">\r\n" + 
				"     <li class=\"page-item ?prvClass\"><button class=\"page-link\" tabindex='-1' onclick='sendPaging_?pagingMethodUniqueId(?previousIndex, 0)'>Previous</button></li>\r\n");

		
		int startPagingIndex = SUMMARY_DEFAULT_PAGING_INDEX;
		// Reformat paging selector (Previous, ..., 1,2,3,4,5, ..., Next) 
		// to start from the new start index, like (Previous, ..., 6,7,8,9, ..., Next)
		if (selectedPagingIndexIsLast > 0)
		{
			// Button (...) selected
			startPagingIndex = selectedPagingIndex;
		}	
		else if (selectedEndPagingIndex > 1 && selectedPagingIndex > selectedEndPagingIndex)
		{
			// Button Next clicked when Start index = 1 like (Previous, ..., 1, 2, 3, 4, ..., Next))
			startPagingIndex = selectedPagingIndex;
		}
		else if (selectedPagingIndex < selectedStartPagingIndex)
		{
			// Button Previous clicked when Start index > 1 like (Previous, ..., 9, 10, 11, 12, ..., Next))
			startPagingIndex = selectedStartPagingIndex - MAX_NUMBER_OF_PAGING_BUTTONS -1;
		}
		else if (selectedStartPagingIndex > 1)
		{
			// Button Next clicked when Start index > 1 like (Previous, ..., 9, 10, 11, 12, ..., Next)
			startPagingIndex = selectedStartPagingIndex;
		}
		
		// if start index > 1, that is (Previous, ..., 9, 10, 11, 12, ..., Next)
		// add a left (...) button, after Previous button
		if (startPagingIndex > 1)
		{
			int previousStartIndex = startPagingIndex - MAX_NUMBER_OF_PAGING_BUTTONS - 1;
			String paginationLink = "<li class=\"page-item ?active\"><button class=\"page-link\" onclick='sendPaging_?pagingMethodUniqueId(" + previousStartIndex + ", " + previousStartIndex + ")'>...</button></li>\r\n";
			paginationLink = paginationLink.replace("?active", "");
			sb.append(paginationLink);
		}
		
		int endPagingIndex = startPagingIndex - 1;

		int counter = 0;
		for (int pageNum = startPagingIndex; pageNum <= pagingSize; pageNum++) {
			String paginationLink = "<li class=\"page-item ?active\"><button class=\"page-link\" onclick='sendPaging_?pagingMethodUniqueId("+ pageNum +", 0)'>" + pageNum + "</button></li>\r\n";
			paginationLink = selectedPagingIndex == pageNum ? paginationLink.replace("?active", "active") : paginationLink.replace("?active", "");

			if (counter > MAX_NUMBER_OF_PAGING_BUTTONS)
			{
				paginationLink = "<li class=\"page-item ?active\"><button class=\"page-link\" onclick='sendPaging_?pagingMethodUniqueId(" + pageNum + ", " + pageNum + ")'>...</button></li>\r\n";
				paginationLink = selectedPagingIndex == pageNum ? paginationLink.replace("?active", "active") : paginationLink.replace("?active", "");
				sb.append(paginationLink);
				break;
			}

			counter++;
			sb.append(paginationLink);
			endPagingIndex++;
		}

		// Next button
		sb.append("      <li class=\"page-item ?nextClass\"><button class=\"page-link\" onclick='sendPaging_?pagingMethodUniqueId(?nextIndex, 0)'>Next</button></li>\r\n" +
				"  </ul>\r\n" + 
				"</nav>");

		sb.append("</form>"
				+ "<script>"
				+ "\n "
				+ "\n "
				+ "\n function sendPaging_?pagingMethodUniqueId(index, lastIndex){"
				+ "\n  $('input[name=\"summaryCurrentIndex_?pagingUniqueId\"]').val(index);"
				+ "\n  $('input[name=\"summarySelectedIndex_?pagingUniqueId\"]').val(index);"
				+ "\n  $('input[name=\"summaryHash_?summaryHash\"]').val('?summaryHash');"
				+ "\n  $('input[name=\"summarySelectedIndexLast_?pagingUniqueId\"]').val(lastIndex);"
				+ "\n  $('input[name=\"summarySelectedIndexStart_?pagingUniqueId\"]').val(?startPagingIndex);"
				+ "\n  $('input[name=\"summarySelectedIndexEnd_?pagingUniqueId\"]').val(?endPagingIndex);"
				+ "\n  $('summaryNavForm_?pagingUniqueId').submit();"
				+ "\n };"
				+ "\n"
				+ "\n $(document).ready(function(){\r\n"
				+ "    $('#summaryNavForm_?pagingUniqueId').on(\"submit\", function(event){\r\n"
				+ "        event.preventDefault();\r\n"
				+ "        var formValues= $('#summaryNavForm_?pagingUniqueId').serialize();\r\n"
				+ "        console.log('calling paging url: ?pagingUrl');"
				+ "        executeProcess('?pagingUrl', formValues, false, '#?id');"
				
				//+ "        $.post('?pagingUrl', formValues, function(data){\r\n"
				//+ "             $('#?id').html(data);\r\n"
				//+ "        })"
				+ "    });\r\n"
				+ "});\r\n"
				+ "\r\n"
				+ "</script>");

		String pagingHtml = sb.toString();
		if (pagingSize == 0) {
			pagingHtml = pagingHtml.replace("?prvClass", "disabled");
			pagingHtml = pagingHtml.replace("?nextClass", "disabled");
		}
		else
		{ 
			if (selectedPagingIndex > 1)
			{
				pagingHtml = pagingHtml.replace("?prvClass", "enabled");
			}
			else
			{
				pagingHtml = pagingHtml.replace("?prvClass", "disabled");
			}

			if (selectedPagingIndex >= pagingSize)
			{
				pagingHtml = pagingHtml.replace("?nextClass", "disabled");
			}
			else
			{
				pagingHtml = pagingHtml.replace("?nextClass", "enabled");
			}
		}

		int nextIndex = selectedPagingIndex;
		nextIndex++;
		int previousIndex = selectedPagingIndex;
		previousIndex--;
		pagingHtml = pagingHtml.replace("?startPagingIndex", Integer.toString(startPagingIndex));
		pagingHtml = pagingHtml.replace("?endPagingIndex", Integer.toString(endPagingIndex));
		pagingHtml = pagingHtml.replace("?currentIndex", Integer.toString(selectedPagingIndex));
		pagingHtml = pagingHtml.replace("?nextIndex", Integer.toString(nextIndex));
		pagingHtml = pagingHtml.replace("?previousIndex", Integer.toString(previousIndex));
		pagingHtml = pagingHtml.replace("?pagingUniqueId", pagingUniqueId);
		pagingHtml = pagingHtml.replace("?pagingMethodUniqueId", pagingMethodUniqueId);

		return pagingHtml;
	}

}
