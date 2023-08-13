package com.patroclos.uicomponent;

import java.util.Map;
import java.util.UUID;

import com.patroclos.uicomponent.core.Table;

public abstract class UITableBase extends UIComponentTemplate {
	
	public final int SUMMARY_DEFAULT_PAGING_INDEX = 1;
	public final String SUMMARY_PAGING_MAPPING = "summaryPaging";
	
	protected final int PAGING_ROWS = 8;
	protected final int MAX_NUMBER_OF_PAGING_BUTTONS = 7;

	protected String getTableUniqueHash() {
		return "\n<input type='hidden' id='summaryHash_?summaryHash' name='summaryHash_?summaryHash' val='?summaryHash'/>"; //used to save table resultset state in DataHolder object
	}
	
	protected String getPagination(int rowCount, int selectedPagingIndex, Table table) {

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
		Map<String, String> pagingParams = table.getPagingParams();
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
		//sb.append("\n<form id='summaryNavForm_?pagingUniqueId' method='POST' action='?pagingUrl'>");
		
		// for cases where table is opened in a modal, hold the tableid of the parent table which called this modal			
		if (table.getParentTableId() != null) {		
			String parentTableRef = "\n<input type='hidden' id='"+ UIModal.MODAL_TABLE_REFERENCE_PARENT_PLACEHOLDER_NAME +"' name='" + UIModal.MODAL_TABLE_REFERENCE_PARENT_PLACEHOLDER_NAME+
			"' val='"+ UIModal.MODAL_TABLE_REFERENCE_PARENT_PLACEHOLDER_ID+"'/>";;
			//parentTableRef = parentTableRef.replace(MODAL_TABLE_REFERENCE_PARENT_PLACEHOLDER_ID, table.getParentTableId());
			sb.append(parentTableRef); 
		}
		
		sb.append("\n<input type='hidden' id='summaryCurrentIndex_?pagingUniqueId' name='summaryCurrentIndex_?pagingUniqueId' val='?currentIndex'/>");
		sb.append("\n<input type='hidden' id='summarySelectedIndex_?pagingUniqueId' name='summarySelectedIndex_?pagingUniqueId'/>");
		sb.append("\n<input type='hidden' id='summarySelectedIndexLast_?pagingUniqueId' name='summarySelectedIndexLast_?pagingUniqueId' val='0'/>");
		sb.append("\n<input type='hidden' id='summarySelectedIndexStart_?pagingUniqueId' name='summarySelectedIndexStart_?pagingUniqueId' val='?startPagingIndex'/>");
		sb.append("\n<input type='hidden' id='summarySelectedIndexEnd_?pagingUniqueId' name='summarySelectedIndexEnd_?pagingUniqueId' val='?endPagingIndex'/>");

		// Previous Button
		sb.append("\n<nav aria-label=\"...\">" + 
				"  \n<ul class=\"pagination\">" + 
				"  \n<li class=\"page-item ?prvClass\"><button type=\"button\" class=\"page-link\" tabindex='-1' onclick='sendPaging_?pagingMethodUniqueId(?previousIndex, 0);'>Previous</button></li>\r\n");


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
			String paginationLink = "<li class=\"page-item ?active\"><button class=\"page-link\" onclick='sendPaging_?pagingMethodUniqueId(" + previousStartIndex + ", " + previousStartIndex + ");'>...</button></li>\r\n";
			paginationLink = paginationLink.replace("?active", "");
			sb.append(paginationLink);
		}

		int endPagingIndex = startPagingIndex - 1;

		int counter = 0;
		for (int pageNum = startPagingIndex; pageNum <= pagingSize; pageNum++) {
			String paginationLink = "\n<li class=\"page-item ?active\"><button type=\"button\" class=\"page-link\" onclick='sendPaging_?pagingMethodUniqueId("+ pageNum +", 0);'>" + pageNum + "</button></li>\r\n";
			paginationLink = selectedPagingIndex == pageNum ? paginationLink.replace("?active", "active") : paginationLink.replace("?active", "");

			if (counter > MAX_NUMBER_OF_PAGING_BUTTONS)
			{
				paginationLink = "\n<li class=\"page-item ?active\"><button type=\"button\" class=\"page-link\" onclick='sendPaging_?pagingMethodUniqueId(" + pageNum + ", " + pageNum + ");'>...</button></li>\r\n";
				paginationLink = selectedPagingIndex == pageNum ? paginationLink.replace("?active", "active") : paginationLink.replace("?active", "");
				sb.append(paginationLink);
				break;
			}

			counter++;
			sb.append(paginationLink);
			endPagingIndex++;
		}

		// Next button
		sb.append("\n<li class=\"page-item ?nextClass\"><button type=\"button\" class=\"page-link\" onclick='sendPaging_?pagingMethodUniqueId(?nextIndex, 0);'>Next</button></li>\r\n" +
				  "\n</ul>" + 
				  "\n</nav>");

		sb.append(""
				+ "\n<script>"
				+ "\n "
				+ "\n "
				+ "\n function sendPaging_?pagingMethodUniqueId(index, lastIndex){"
				+ "\n  $('input[name=\"summaryCurrentIndex_?pagingUniqueId\"]').val(index);"
				+ "\n  $('input[name=\"summarySelectedIndex_?pagingUniqueId\"]').val(index);"
				+ "\n  $('input[name=\"summaryHash_?summaryHash\"]').val('?summaryHash');"
				+ "\n  $('input[name=\"summarySelectedIndexLast_?pagingUniqueId\"]').val(lastIndex);"
				+ "\n  $('input[name=\"summarySelectedIndexStart_?pagingUniqueId\"]').val(?startPagingIndex);"
				+ "\n  $('input[name=\"summarySelectedIndexEnd_?pagingUniqueId\"]').val(?endPagingIndex);"
				+ "\n  executeTablePaging_?pagingMethodUniqueId();"
				+ "\n }"
				+ "\n"
				+ "\n function executeTablePaging_?pagingMethodUniqueId(){"
				+ "\n    "
				+ "\n	 var formValues= $('input[name*=\"?pagingUniqueId\"], input[name*=\"summaryHash_?summaryHash\"], input[name*=\"selected_?id\"], input[id=\"hash\"]').serialize();"
				
				//Inject the table's row data
				+ "\n    var formVals = formValues;\r\n"
				+ "\n      $('#table_?id > tbody').find('tr').each(function(){\r\n"
				+ "\n      var tablerow = $(this);\r\n"
				+ "\n      var i=0;"
				+ "\n      $(\"td input\", tablerow).each(function () {\r\n"
				+ "\n          var input = $(this);\r\n"
				+ "\n	       var c = input.attr('class');\r\n"
				+ "\n	       if (c != null && c == 'valuedatafield')\r\n"
				+ "\n		   {\r\n"
				+ "\n             var checkboxValue = $(input).prop('checked'); "
				+ "\n             var value = checkboxValue != null ? checkboxValue : input.val();"
				+ "\n			  formVals += '&' + input.attr('name') + '=' + encodeURIComponent(checkboxValue);\r\n"
				+ "\n		   }\r\n"
				+ "\n  });\r\n"
				+ "\n });"

				+ "\n"
				+ "\n	 executeProcess('?pagingUrl', formVals, false, '#?id');"
				+ "\n }"
				+ "\n"
				+ "\n</script>");

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
