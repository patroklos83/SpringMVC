package com.patroclos.uicomponent;

import org.springframework.stereotype.Component;

import com.patroclos.uicomponent.core.DataTableFunction;
import com.patroclos.uicomponent.core.UIComponent;

@Component
public class UIModal extends UIComponent {
	
	public static final String MODAL_TABLE_REFERENCE_PARENT_PLACEHOLDER_PREFIX_NAME = "modal_tableRefid_";
	public static final String MODAL_TABLE_REFERENCE_PARENT_PLACEHOLDER_ID = "?refId";
	public static final String MODAL_TABLE_REFERENCE_PARENT_PLACEHOLDER_NAME = MODAL_TABLE_REFERENCE_PARENT_PLACEHOLDER_PREFIX_NAME + MODAL_TABLE_REFERENCE_PARENT_PLACEHOLDER_ID;
	
	public static final String MODAL_BODY_DIV_ID = "#modal-body";
	public static final String MODAL_ID = "#dialog-form";
	public static final String MODAL_TITLE_ID = "#modal-title";
	public static final String MODAL_CONFIRM_BUTTON_ID = "#modalConfirmBtn";
	
	public static final String MODAL_CONFIRM_BUTTON_PLACEHOLDER_UNIQUE_ID = "?uniqueId";
	public static final String MODAL_CONFIRM_BUTTON_JAVASCRIPT_FUNCTION = "executeTableAddConfirm_";
	
	public String getModalConfirmButtonScript() {
		return getModalConfirmButtonScript(null);
	}
	
	public String getModalConfirmButtonScript(String uniqueId) {
		
		String javascript = ""
				+ "<script>"
				+ "\n"
				+ "\n" // Set Modal confirm button with the proper confirm function
				+ "\n    $('" + MODAL_CONFIRM_BUTTON_ID + "').removeAttr('onclick');"
			    + "\n    $('" + MODAL_CONFIRM_BUTTON_ID + "').attr('onClick', '" + MODAL_CONFIRM_BUTTON_JAVASCRIPT_FUNCTION + MODAL_CONFIRM_BUTTON_PLACEHOLDER_UNIQUE_ID + "();');"
				+ "\n"
				+ "\n	function " + MODAL_CONFIRM_BUTTON_JAVASCRIPT_FUNCTION + MODAL_CONFIRM_BUTTON_PLACEHOLDER_UNIQUE_ID + "(){"
				+ "\n"
				+ "\n		$('input[name=\"summaryHash_?summaryHash\"]').val('?summaryHash');\r\n"
				+ "\n		$('input[name=\""+MODAL_TABLE_REFERENCE_PARENT_PLACEHOLDER_NAME+"\"]').val('"+MODAL_TABLE_REFERENCE_PARENT_PLACEHOLDER_ID+"');\r\n"
				+ "\n       $('input[name=\"dataFunction_?summaryHash\"]').val('" + DataTableFunction.Add + "');"
				+ "\n		var formValues= $('input[id^=selected_?id], "
				+ "input[name*=\""+MODAL_TABLE_REFERENCE_PARENT_PLACEHOLDER_NAME+"\"], "
				+ "input[id=\"hash\"],"
				+ "input[name*=\"summaryHash_?summaryHash\"], "
				+ "input[name=\"dataFunction_?summaryHash\"],"
				+ "" + MODAL_BODY_DIV_ID + " input[type=text]')"
				+ ".serialize();\r\n"
			    + "\n"
				+ "\n	    executeProcess('?dataFunctionUrl', formValues, false, '#"+MODAL_TABLE_REFERENCE_PARENT_PLACEHOLDER_ID+"');\r\n"
				+ "\n"
				+ "\n       $('" + MODAL_BODY_DIV_ID + "').empty();"
				+ "\n	    $('" + MODAL_ID + "').modal('hide');"
				+ "\n	}"
				+ "\n"
				+ "\n" 
				+ "</script>";
		
		if (uniqueId != null && uniqueId.length() > 0) {
			javascript = javascript.replace(MODAL_CONFIRM_BUTTON_PLACEHOLDER_UNIQUE_ID, uniqueId);
		}
		
		return javascript;
	}

}
