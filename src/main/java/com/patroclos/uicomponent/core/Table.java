package com.patroclos.uicomponent.core;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.patroclos.dto.BaseDTO;
import com.patroclos.exception.SystemException;
import com.patroclos.uicomponent.UILayoutForm.ComponentMode;

public class Table extends UIComponent {		

	private final String tableId;
	private String tableDescription;
	private boolean isDataTable;
	//private boolean isEditable;
	private final String sqlQuery;
	private final Map<String, Input> inputFilters;
	private String pagingUrl;
	private final Map<String, ColumnDefinition> columnDefinitions;	
	private SqlRowSet sqlRowSet;
	private List<? extends BaseDTO> datalist;
	private Class<? extends BaseDTO> datalistType;
	private Map<String,String> pagingParams;
    private TreeMap<Integer, Boolean> selectedRows; 
    private ComponentMode componentMode;
    private String parentTableId;
    private boolean containsDuplicates;
    private boolean isSingletonDatalist;
    
    private String addButtonGetUILayoutUrl;
	private String addButtonActionUrl;
	private String addButtonUILayoutTitle;
	private boolean disableAddRemoveButtons;
    
	public Table(Builder builder)
	{
		this.tableId = builder.tableId;
		if (builder.name == null)
			throw new SystemException("Table builder: Name is mandatory [Specify field name of corresponding List in the DTO!]");
		super.setName(builder.name);
		this.tableDescription = builder.tableDescription;	
		this.sqlQuery = builder.sqlQuery;
		this.sqlRowSet = builder.sqlRowSet;
		this.inputFilters = builder.inputFilters;
		this.pagingParams = builder.pagingParams;
		this.pagingUrl = builder.pagingUrl;
		this.columnDefinitions = builder.columnDefinitions;
		this.datalist = builder.datalist;
		this.datalistType = builder.datalistType;
		this.isSingletonDatalist = builder.isSingletonDatalist;
		this.containsDuplicates = builder.containsDuplicates;
		if (this.datalist != null) this.isDataTable = true;
		if (this.datalistType != null) this.isDataTable = true;
		if (this.pagingUrl == null || this.pagingUrl.trim().isBlank()) 
			this.pagingUrl = "tablePaging"; //use default
		this.componentMode = builder.componentMode;
		this.parentTableId = builder.parentTableId;
		super.isEditable = builder.isEditable;
		
		if (builder.editableBehaviorBuilder != null) {
			this.addButtonActionUrl = builder.editableBehaviorBuilder.addButtonActionUrl;
			this.addButtonGetUILayoutUrl = builder.editableBehaviorBuilder.addButtonGetUILayoutUrl;
			this.addButtonUILayoutTitle = builder.editableBehaviorBuilder.addButtonUILayoutTitle;
			this.disableAddRemoveButtons = builder.editableBehaviorBuilder.disableAddRemoveButtons;
		}
	}
	
	public void setSqlRowSet(SqlRowSet sqlRowSet) {
		this.sqlRowSet = sqlRowSet;
	}		
		
	public void setPagingParams(Map<String, String> pagingParams) {
		this.pagingParams = pagingParams;
	}

	/***
	 * This is the actual HTML element id of the <table> tag
	 * @return
	 */
	public String getTableId() {
		return tableId;
	}
	
	public String getTableDescription() {
		return this.tableDescription;
	}
	
	/***
	 * This is the corresponding Field name
	 * of the List to be displayed by the datatable
	 * For example, 
	 * 		public List<ArticleDTO> articles;
	 * The datatable's name must be set to articles;
	 * @return
	 */
	public String getName() {
		return super.name;
	}

	public String getSqlQuery() {
		return sqlQuery;
	}

	public Map<String, Input> getInputFilters() {
		return inputFilters;
	}

	public String getPagingUrl() {
		return pagingUrl;
	}
	
	public Map<String, String> getPagingParams() {
		return pagingParams;
	}

	public SqlRowSet getSqlRowSet() {
		return sqlRowSet;
	}

	public Map<String, ColumnDefinition> getColumnDefinitions() {
		return columnDefinitions;
	}
	
	public List<? extends BaseDTO> getDatalist() {
		return datalist;
	}
	
	public void setDatalist(List<? extends BaseDTO> datalist) {
		this.datalist = datalist;
	}	
	
	public Class<? extends BaseDTO> getDataListType() {
		return datalistType;
	}
	
	public boolean getIsDataTable() {
		return isDataTable;
	}

	public TreeMap<Integer, Boolean> getSelectedRows() {
		return selectedRows;
	}

	public void setSelectedRows(TreeMap<Integer, Boolean> selectedRows) {
		this.selectedRows = selectedRows;
	}

	public ComponentMode getComponentMode() {
		return componentMode;
	}

	public void setComponentMode(ComponentMode componentMode) {
		this.componentMode = componentMode;
	}
	
	/***
	 * This is the table server side unique Id
	 * @return
	 */
	public String getParentTableId() {
		return this.parentTableId;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public String getAddButtonGetUILayoutUrl() {
		return addButtonGetUILayoutUrl;
	}

	public String getAddButtonActionUrl() {
		return addButtonActionUrl;
	}

	public String getAddButtonUILayoutTitle() {
		return addButtonUILayoutTitle;
	}

	public boolean getIsContainsDuplicates() {
		return containsDuplicates;
	}

	public boolean getIsSingletonDatalist() {
		return isSingletonDatalist;
	}

	public boolean isDisableAddRemoveButtons() {
		return disableAddRemoveButtons;
	}

	public static class Builder {	
		
		private String tableId;
		private String tableDescription;
		private String name;
		private String sqlQuery;
		private SqlRowSet sqlRowSet;
		private Map<String, Input> inputFilters;
		private String pagingUrl;
		private Map<String,String> pagingParams;
		private Map<String, ColumnDefinition> columnDefinitions;
		private List<? extends BaseDTO> datalist;
		private Class<? extends BaseDTO> datalistType;
		private boolean isEditable;
		private ComponentMode componentMode;
		private String parentTableId;
		private boolean containsDuplicates;
		private boolean isSingletonDatalist;
		
		private EditableBehaviorBuilder editableBehaviorBuilder;

		public static Builder newInstance()
		{
			return new Builder();
		}

		private Builder() {}

		/***
		 * This is the corresponding Field name
		 * of the List to be displayed by the datatable
		 * For example, 
		 * 		public List<ArticleDTO> articles;
		 * The datatable's name must be set to articles;
		 * @return
		 */
		public Builder setName(String name) {
			this.name = name;
			return this;
		}
		
		public Builder setTableDescription(String tableDescription) {
			this.tableDescription = tableDescription;
			return this;
		}
		
		public Builder setTableId(String tableId) {
			this.tableId = tableId;
			return this;
		}

		public Builder setSqlQuery(String sqlQuery) {
			this.sqlQuery = sqlQuery;
			return this;
		}
		
		public Builder setInputFilters(Map<String, Input> inputFilters) {
			this.inputFilters = inputFilters;
			return this;
		}

		public Builder setPagingUrl(String pagingUrl) {
			this.pagingUrl = pagingUrl;
			return this;
		}

		public Builder setPagingParams(Map<String, String> pagingParams) {
			this.pagingParams = pagingParams;
			return this;
		}

		public Builder setSqlRowSet(SqlRowSet sqlRowSet) {
			this.sqlRowSet = sqlRowSet;
			return this;
		}

		public Builder setColumnDefinitions(Map<String, ColumnDefinition> columnDefinitions) {
			var t = columnDefinitions;
			if (t != null && t.size() > 0) {
				t = columnDefinitions
						.entrySet()
						.stream()
						.collect(Collectors.toMap(entry -> entry.getKey().toUpperCase(), entry -> entry.getValue()));	
			}
			this.columnDefinitions = t;
			return this;
		} 
		
		public Builder setDatalist(List<? extends BaseDTO> list) {
			this.datalist = list;
			return this;
		}
		
		public Builder setDatalistType(Class<? extends BaseDTO> datalistType) {
			this.datalistType = datalistType;
			return this;
		}

		public Builder setComponentMode(ComponentMode componentMode) {
			this.componentMode = componentMode;
			return this;
		}
		
		public Builder setParentTableId(String parentTableId) {
			this.parentTableId = parentTableId;
			return this;
		}
		
		public Builder setIsContainsDuplicates(boolean containsDuplicates) {
			this.containsDuplicates = containsDuplicates;
			return this;
		}
		
		public Builder setIsSingletonDatalist(boolean isSingletonDatalist) {
			this.isSingletonDatalist = isSingletonDatalist;
			return this;
		}
		
		public EditableBehaviorBuilder addEditBehavior() {
			return EditableBehaviorBuilder.editableBehavior(this);
		}
		
		/***
		 * Render this table but only in READ mode
		 * Do not display Edit buttons
		 * @param isEditable
		 * @return
		 */
		public Builder setIsEditable(boolean isEditable) {
			this.isEditable = isEditable;
			return this;
		}
		
		public static class EditableBehaviorBuilder {	
			
			private String addButtonGetUILayoutUrl;
			private String addButtonUILayoutTitle;
			private String addButtonActionUrl;
			private boolean disableAddRemoveButtons;

			private Builder builder;

			public static EditableBehaviorBuilder editableBehavior(Builder builder)
			{
				return new EditableBehaviorBuilder(builder);
			}

			private EditableBehaviorBuilder(Builder builder) 
			{
				this.builder = builder;
			}

			public EditableBehaviorBuilder setAddButtonGetUILayoutUrl(String addButtonGetUILayoutUrl) {
				this.addButtonGetUILayoutUrl = addButtonGetUILayoutUrl;
				return this;
			}

			public EditableBehaviorBuilder setAddButtonActionUrl(String addButtonActionUrl) {
				this.addButtonActionUrl = addButtonActionUrl;
				return this;
			}
			
			public EditableBehaviorBuilder setAddButtonUILayoutTitle(String addButtonUILayoutTitle) {
				this.addButtonUILayoutTitle = addButtonUILayoutTitle;
				return this;
			}
			
			public EditableBehaviorBuilder setDisableAddRemoveButtons(boolean disableAddRemoveButtons) {
				this.disableAddRemoveButtons = disableAddRemoveButtons;
				return this;
			}
			
			public Builder done() {
				this.builder.editableBehaviorBuilder = this;
				return this.builder;
			}
			
		}

		public Table build() {
			Table t = new Table(this);
			return t;		
		}
	}
}