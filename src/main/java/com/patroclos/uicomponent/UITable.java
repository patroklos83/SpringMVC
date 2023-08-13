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
import com.patroclos.facade.Facade;
import com.patroclos.repository.IRepository;
import com.patroclos.service.IAuthenticationService;
import com.patroclos.service.UserService;
import com.patroclos.uicomponent.core.ColumnDefinition;
import com.patroclos.uicomponent.core.ColumnDefinitionType;
import com.patroclos.uicomponent.core.DbFieldType;
import com.patroclos.uicomponent.core.Input;
import com.patroclos.uicomponent.core.Table;
import com.patroclos.utils.DateUtil;

@Component
public class UITable extends UITableBase {

	@Autowired
	protected IRepository Repository;
	@Autowired
	protected Facade Facade;
	@Autowired
	private UserService UserService;
	@Autowired
	private IAuthenticationService AuthenticationService;

	private final String DATA_FILTERING_COLUMN_USERID = "USERID";

	public Table draw(Table table) throws Exception {
		return drawTable(table, null);
	}

	public Table draw(Table table, Map<String,String> searchParams) throws Exception {
		return drawTable(table, searchParams);
	}

	private Table drawTable(Table table, Map<String,String> searchParams) throws Exception {

		SqlRowSet resultSet = null;
		boolean isFromPagingAction = false;

		if (table == null) {
			throw new SystemException("No Table object provided!");
		}

		if (table.getSqlQuery() == null && table.getSqlRowSet() == null) {
			throw new SystemException("No sql or resultSet to populate table!");
		}

		if (table.getTableId() == null) {
			throw new SystemException("No tableId provided!");
		}

		if (table.getSqlRowSet() != null) {
			resultSet = table.getSqlRowSet();
			isFromPagingAction = true;
		}
		else
		{	
			MapSqlParameterSource sqlParams = new MapSqlParameterSource();
			if (table.getColumnDefinitions() != null && table.getColumnDefinitions().size() > 0) {					
				var rowsDefinitionValues = table.getColumnDefinitions().values().stream().filter(c -> c.getValue() != null).collect(Collectors.toList());
				if (rowsDefinitionValues != null)
					for(var colDef : rowsDefinitionValues) {
						sqlParams.addValue(colDef.getColumnDbName().toUpperCase(), colDef.getValue().toString());
					}
			}

			StringBuilder sql = new StringBuilder();
			String orderBy = "ORDER BY";
			String sqlWithoutOrderBy = table.getSqlQuery();
			String sqlOrderByStatement = "";
			
			if (table.getSqlQuery().toUpperCase().indexOf(orderBy) > 0) {
				String[] sqlParts = table.getSqlQuery().toUpperCase().split(orderBy);
				sqlWithoutOrderBy = sqlParts[0];
				sqlOrderByStatement = sqlParts.length > 0 ? " " + orderBy + sqlParts[1] : "";
			}
			
			sqlParams = setWhereStatement(sqlWithoutOrderBy, sqlParams, searchParams, table.getInputFilters(), table.getSqlRowSet(), sql);
			String finalSql = sql.toString().toUpperCase() + sqlOrderByStatement;
			
			resultSet = Repository.customNativeQuery(finalSql, sqlParams);
			table.setSqlRowSet(resultSet);
		}

		String tableHtml = getTableHtml(resultSet, table);
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
			Map<String,Input> inputFiltersMap, 
			SqlRowSet results, 
			StringBuilder sql) {

		if (sqlQuery != null && results == null)
			sql = initSQL(sql, sqlQuery);

		if (allParams != null && inputFiltersMap != null) {
			
			Map<String,Input> inputFilters = inputFiltersMap.entrySet().stream()
			.collect(Collectors.toMap(entry -> entry.getKey().replace(" ", ""),
					entry -> entry.getValue())); // trim spaces from keys
			
			for (Entry<String, String> paramEntry: allParams.entrySet()) {
				String paramKey = paramEntry.getKey().replace(" ", ""); // trim spaces
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
								sqlParams.addValue(paramName.toUpperCase(), dbDate);
							}
							else if (i.getType().equals(UIInputType.Text) && i.getDbFieldType() == DbFieldType.Text) {
								sql.append(" AND UPPER(" + i.getDbField() + ") LIKE UPPER(:"+ i.getDbField() +")");
								sqlParams.addValue(i.getDbField().toUpperCase(), paramEntry.getValue().toString());
							}
							else {
								sql.append(" AND " + i.getDbField() + " = :"+ i.getDbField());		
								sqlParams.addValue(i.getDbField().toUpperCase(), paramEntry.getValue().toString());
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

			if (columnAlias.toUpperCase().equals(DATA_FILTERING_COLUMN_USERID))
				continue; // skip USERID Column, it used for system internal filtering of data

			//If column alias was specified in columnDefinitions override the columnName with this value
			String name = columnName.toUpperCase();
			if (columnDefinitions != null && columnDefinitions.get(name) != null) {
				ColumnDefinition columnDef = columnDefinitions.get(name);
				if (columnDef != null && columnDef.getColumnAlias() != null) {
					columnName = columnDef.getColumnAlias().toUpperCase();
				}
			}

			tableRows.append("<th>" + columnName + "</th>");
		}
		return tableRows.toString();
	}

	private synchronized String getTableHtml(SqlRowSet resultSet, Table table) throws Exception {
		StringBuilder tableRows = new StringBuilder();

		int rowCount = 0;
		int selectedPagingIndex = SUMMARY_DEFAULT_PAGING_INDEX;

		Map<String, String> pagingParams = table.getPagingParams();
		if (pagingParams != null)
			selectedPagingIndex = Integer.parseInt(
					pagingParams.entrySet().stream()
					.filter(k -> k.toString().startsWith("summarySelectedIndex"))
					.findFirst().get().getValue());

		tableRows.append("<div id='?id'>");
		tableRows.append("<div style='overflow-x:auto !important;height: 55vh'>");
		tableRows.append(getTableUniqueHash());
		tableRows.append("<table id='table_?id' class='table'>");
		tableRows.append("<thead>");
		Map<String, ColumnDefinition> columnDefinitions = table.getColumnDefinitions();
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

		var users = UserService.getAllUsersBelongingToUserGroups(AuthenticationService.getAuthentication().getName());

		while (resultSet.next()) {

			boolean skipRow = false;
			int c = m.getColumnCount();
			for (int i = 1; i <= c; i++) {			
				String columnAlias = m.getColumnLabel(i);//get column label/alias not actual name		
				String name = columnAlias.toUpperCase();
				if (name.equals(DATA_FILTERING_COLUMN_USERID)) {
					Long userId = resultSet.getLong(i);
					// skip do not display row on UI, since user does not have the access rights
					// to view other user's data from other groups
					long count = users.stream().filter(u -> u.getId() == userId).count();
					if (count == 0l) {
						skipRow = true;
						rowCount--;
						break;
					}
				}
			}

			if (skipRow) continue;

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
				if (columnName.toUpperCase().equals(DATA_FILTERING_COLUMN_USERID))
					continue; // skip USERID Column, it used for system internal filtering of data
				tableRows.append(setColumnValue(columnName, resultSet, columnDefinitions));
			}

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

		String pagination = getPagination(rowCount, selectedPagingIndex, table);

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

}
