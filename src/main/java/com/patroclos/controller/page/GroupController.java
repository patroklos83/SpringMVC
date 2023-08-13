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
import com.patroclos.dto.GroupDTO;
import com.patroclos.dto.RoleDTO;
import com.patroclos.dto.RoleEntityAccessDTO;
import com.patroclos.dto.UserDTO;
import com.patroclos.uicomponent.core.ColumnDefinition;
import com.patroclos.uicomponent.core.ColumnDefinitionType;
import com.patroclos.uicomponent.core.Input;
import com.patroclos.uicomponent.core.Table;
import com.patroclos.uicomponent.core.UIComponent;
import com.patroclos.uicomponent.UIInputType;

@Controller
@RequestMapping("group")
public class GroupController extends PageController {

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
		return super.pageLoad(id, GroupDTO.class, state, model, "/pages/group");
	}

	@Override
	public Map<String, UIComponent> getFields(Object o) throws Exception {
		GroupDTO group = (GroupDTO)o;
		Input id = UIInput.draw("Id", group.getId(), UIInputType.Text);
		Input name = UIInput.draw("name", group.getName(), UIInputType.Text);
		
		Map<String, ColumnDefinition> columnDefinitions = new HashMap<String, ColumnDefinition>();
		ColumnDefinition colDefId = new ColumnDefinition();
		colDefId.setType(ColumnDefinitionType.NORMAL_TEXT);
		colDefId.setColumnDbName("id");
		colDefId.setColumnAlias("Id");
		columnDefinitions.put(colDefId.getColumnDbName(), colDefId);
		ColumnDefinition colDefUserName = new ColumnDefinition();
		colDefUserName.setType(ColumnDefinitionType.NORMAL_TEXT);
		colDefUserName.setColumnDbName("username");
		colDefUserName.setColumnAlias("username");
		columnDefinitions.put(colDefUserName.getColumnDbName(), colDefUserName);
		
		Table users = Table.Builder.newInstance()
				.setTableDescription("Group Users")
				.setName("users")
				.setIsEditable(false)
				.setDatalist(group.getUsers())
				.setDatalistType(UserDTO.class)
				.setColumnDefinitions(columnDefinitions)
				.setTableId("users")
				.build();
		users = UIDataTable.draw(users);
	
		Map<String, UIComponent> fields = new LinkedHashMap<String, UIComponent>();
		fields.put(id.getName(), id);
		fields.put(name.getName(), name);
		fields.put(users.getName(), users);

		return fields;
	}

	@RequestMapping(value="/update", method=RequestMethod.POST)
	protected ModelAndView saveUpdate(@FormParam Form form, ModelMap model) throws Exception { 
		GroupDTO groupDTO = (GroupDTO) form.getDto();
		GroupDTO dirtyGroupDTO = (GroupDTO) form.getDirtyDto();		
		
		groupDTO.setName(dirtyGroupDTO.getName());
		
		return super.saveUpdateEntity(groupDTO, model, "group");
	}

	@RequestMapping(value="/create", method=RequestMethod.POST)
	protected ModelAndView saveNew(@FormParam Form form, ModelMap model) throws Exception { 
		GroupDTO groupDTO = (GroupDTO) form.getDto();
		GroupDTO dirtyGroupDTO = (GroupDTO) form.getDirtyDto();		
		
		groupDTO.setName(dirtyGroupDTO.getName());

		return super.saveNewEntity(groupDTO, model, "group");
	}

	@RequestMapping(value="/delete", method=RequestMethod.POST)
	protected ModelAndView delete(@FormParam Form form, ModelMap model) throws Exception {   	
		GroupDTO roleDTO = (GroupDTO) form.getDto();
		return super.deleteEntity(roleDTO, model, "group");
	}

}