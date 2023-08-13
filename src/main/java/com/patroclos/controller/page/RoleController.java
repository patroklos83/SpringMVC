package com.patroclos.controller.page;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.patroclos.controller.core.Form;
import com.patroclos.controller.core.FormParam;
import com.patroclos.controller.core.PageController;
import com.patroclos.controller.core.PageState;
import com.patroclos.dto.PrivilegeDTO;
import com.patroclos.dto.RoleDTO;
import com.patroclos.dto.RoleEntityAccessDTO;
import com.patroclos.uicomponent.core.ColumnDefinition;
import com.patroclos.uicomponent.core.ColumnDefinitionType;
import com.patroclos.uicomponent.core.Input;
import com.patroclos.uicomponent.core.Table;
import com.patroclos.uicomponent.core.UIComponent;
import com.patroclos.uicomponent.UIInputType;

@Controller
@RequestMapping("role")
public class RoleController extends PageController {

	@RequestMapping(value="/{id}", method=RequestMethod.POST)
	@Override
	protected ModelAndView postPage(ModelMap model, @PathVariable("id") Long id) throws Exception {       	
		return pageLoad(id, PageState.Read, model);
	}

	@RequestMapping(value="/{id}", method=RequestMethod.GET) 
	@Override
	protected ModelAndView getPage(ModelMap model, @PathVariable("id") Long id) throws Exception {       
		return pageLoad(id, PageState.Read, model);
	}

	@RequestMapping(value="/edit/{id}", method=RequestMethod.POST)
	@Override
	protected ModelAndView postPageEdit(ModelMap model, @PathVariable("id") Long id) throws Exception {       	
		return pageLoad(id, PageState.Edit, model);
	}

	@RequestMapping(value="/edit/{id}", method=RequestMethod.GET)    
	@Override
	protected ModelAndView getPageEdit(ModelMap model, @PathVariable("id") Long id) throws Exception {       
		return pageLoad(id, PageState.Edit, model);
	}

	@RequestMapping(value="/new", method=RequestMethod.POST)
	@Override
	protected ModelAndView postPageNew(ModelMap model) throws Exception {       	
		return pageLoad(0L, PageState.New, model);
	}

	@RequestMapping(value="/new", method=RequestMethod.GET)
	@Override
	protected ModelAndView getPageNew(ModelMap model) throws Exception {       	
		return pageLoad(0L, PageState.New, model);
	}

	public ModelAndView pageLoad(Long id, PageState state, ModelMap model) throws Exception {
		return super.pageLoad(id, RoleDTO.class, state, model, "/pages/role");
	}

	@Override
	public Map<String, UIComponent> getFields(Object o) throws Exception {
		RoleDTO role = (RoleDTO)o;
		Input id = UIInput.draw("Id", role.getId(), UIInputType.Text);
		Input name = UIInput.draw("name", role.getName(), UIInputType.Text);
		Input hierarchyOrder = UIInput.draw("hierarchyOrder", role.getHierarchyOrder(), UIInputType.Text);
		Input roleHierarchyDiagram = UIInput.draw("roleHierarchy", role.getRoleHierarchy(), UIInputType.TextArea);
		roleHierarchyDiagram.setIsEditable(false);
				
		Table processes = Table.Builder.newInstance()
				.setTableDescription("Process Access")
				.setName("privileges")
				.setIsEditable(true)
				.setDatalist(role.getPrivileges())
				.setDatalistType(PrivilegeDTO.class)
				.setTableId("privileges")
				.build();	
		processes = UIDataTable.draw(processes);
		
		Map<String, ColumnDefinition> columnDefinitions = new HashMap<String, ColumnDefinition>();
		ColumnDefinition colDefId = new ColumnDefinition();
		colDefId.setType(ColumnDefinitionType.NORMAL_TEXT);
		colDefId.setColumnDbName("id");
		colDefId.setColumnAlias("Id");
		columnDefinitions.put(colDefId.getColumnDbName(), colDefId);
		ColumnDefinition colDefEntity = new ColumnDefinition();
		colDefEntity.setType(ColumnDefinitionType.NORMAL_TEXT);
		colDefEntity.setColumnDbName("entityAccess");
		colDefEntity.setColumnAlias("Entity");
		columnDefinitions.put(colDefEntity.getColumnDbName(), colDefEntity);
		ColumnDefinition colDefReadAccess = new ColumnDefinition();
		colDefReadAccess.setType(ColumnDefinitionType.EDITABLE);
		colDefReadAccess.setColumnDbName("readAccess");
		colDefReadAccess.setColumnAlias("Read");
		columnDefinitions.put(colDefReadAccess.getColumnDbName(), colDefReadAccess);
		ColumnDefinition colDefCreateAccess = new ColumnDefinition();
		colDefCreateAccess.setType(ColumnDefinitionType.EDITABLE);
		colDefCreateAccess.setColumnDbName("createAccess");
		colDefCreateAccess.setColumnAlias("Create");
		columnDefinitions.put(colDefCreateAccess.getColumnDbName(), colDefCreateAccess);
		ColumnDefinition colDefUpdateAccess = new ColumnDefinition();
		colDefUpdateAccess.setType(ColumnDefinitionType.EDITABLE);
		colDefUpdateAccess.setColumnDbName("updateAccess");
		colDefUpdateAccess.setColumnAlias("Update");
		columnDefinitions.put(colDefUpdateAccess.getColumnDbName(), colDefUpdateAccess);
		ColumnDefinition colDefDeleteAccess = new ColumnDefinition();
		colDefDeleteAccess.setType(ColumnDefinitionType.EDITABLE);
		colDefDeleteAccess.setColumnDbName("deleteAccess");
		colDefDeleteAccess.setColumnAlias("Delete");
		columnDefinitions.put(colDefDeleteAccess.getColumnDbName(), colDefDeleteAccess);
		
		Table entityAccess = Table.Builder.newInstance()
				.setTableDescription("Entity Level Access")
				.setName("roleEntityAccess")
				.setIsEditable(true)
				.addEditBehavior()
					.setDisableAddRemoveButtons(true)
					.done()
				.setDatalist(role.getRoleEntityAccess())
				.setDatalistType(RoleEntityAccessDTO.class)
				.setTableId("roleEntityAccess")
				.setColumnDefinitions(columnDefinitions)
				.build();
		entityAccess = UIDataTable.draw(entityAccess);
	
		Map<String, UIComponent> fields = new LinkedHashMap<String, UIComponent>();
		fields.put(id.getName(), id);
		fields.put(name.getName(), name);
		fields.put(processes.getTableDescription(), processes);
		fields.put(entityAccess.getTableDescription(), entityAccess);
		fields.put(hierarchyOrder.getName(), hierarchyOrder);
		fields.put(roleHierarchyDiagram.getName(), roleHierarchyDiagram);		
		
		return fields;
	}

	@RequestMapping(value="/update", method=RequestMethod.POST)
	protected ModelAndView saveUpdate(@FormParam Form form, ModelMap model) throws Exception { 
		RoleDTO roleDTO = (RoleDTO) form.getDto();
		RoleDTO dirtyRole = (RoleDTO) form.getDirtyDto();	
		
		roleDTO.setName(dirtyRole.getName());		
		roleDTO.setHierarchyOrder(dirtyRole.getHierarchyOrder());		
		
		return super.saveUpdateEntity(roleDTO, model, "role");
	}

	@RequestMapping(value="/create", method=RequestMethod.POST)
	protected ModelAndView saveNew(@FormParam Form form, ModelMap model) throws Exception { 
		RoleDTO roleDTO = (RoleDTO) form.getDto();
		RoleDTO dirtyRole = (RoleDTO) form.getDirtyDto();			
		
		roleDTO.setName(dirtyRole.getName());
		roleDTO.setHierarchyOrder(dirtyRole.getHierarchyOrder());
		
		return super.saveNewEntity(roleDTO, model, "role");
	}

	@RequestMapping(value="/delete", method=RequestMethod.POST)
	protected ModelAndView delete(@FormParam Form form, ModelMap model) throws Exception {   	
		RoleDTO roleDTO = (RoleDTO) form.getDto();
		return super.deleteEntity(roleDTO, model, "role");
	}

}