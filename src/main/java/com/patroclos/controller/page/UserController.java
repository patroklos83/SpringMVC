package com.patroclos.controller.page;

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
import com.patroclos.dto.UserDTO;
import com.patroclos.uicomponent.core.Input;
import com.patroclos.uicomponent.core.Table;
import com.patroclos.uicomponent.core.UIComponent;
import com.patroclos.uicomponent.UIInputType;

@Controller
@RequestMapping("user")
public class UserController extends PageController {

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
		return super.pageLoad(id, UserDTO.class, state, model, "/pages/user");
	}

	@Override
	public Map<String, UIComponent> getFields(Object o) throws Exception {
		UserDTO user = (UserDTO)o;
		Input id = UIInput.draw("Id", user.getId(), UIInputType.Text);
		Input username = UIInput.draw("username", user.getUsername(), UIInputType.Text);
		username.setIsEditable(false);
		Input password = UIInput.draw("password", "", UIInputType.Text);
		Input isEnabled = UICheckbox.draw("enabled", user.getEnabled() == 1 ? true : false);

		Table roles = Table.Builder.newInstance()
				.setTableDescription("roles")
				.setName("roles")
				.setIsEditable(true)
				.addEditBehavior()
				.setAddButtonUILayoutTitle("Select Roles")
				.done()
				.setDatalist(user.getRoles())
				.setDatalistType(RoleDTO.class)
				.setIsContainsDuplicates(false)
				.setTableId("roles")
				.build();
		roles = UIDataTable.draw(roles);
		
		Table groups = Table.Builder.newInstance()
				.setTableDescription("groups")
				.setName("groups")
				.setIsEditable(true)
				.addEditBehavior()
				.setAddButtonUILayoutTitle("Select Groups")
				.done()
				.setDatalist(user.getGroups())
				.setDatalistType(GroupDTO.class)
				.setIsSingletonDatalist(true)
				.setTableId("groups")
				.build();
		groups = UIDataTable.draw(groups);

		Map<String, UIComponent> fields = new LinkedHashMap<String, UIComponent>();
		fields.put(id.getName(), id);
		fields.put(username.getName(), username);
		fields.put(password.getName(), password);
		fields.put(isEnabled.getName(), isEnabled);
		fields.put(roles.getName(), roles);
		fields.put(groups.getName(), groups);

		return fields;
	}

	@RequestMapping(value="/update", method=RequestMethod.POST)
	protected ModelAndView saveUpdate(@FormParam Form form, ModelMap model) throws Exception { 
		UserDTO userDTO = (UserDTO) form.getDto();
		UserDTO dirtyUser = (UserDTO) form.getDirtyDto();		

		if (dirtyUser.getPassword() != null && !dirtyUser.getPassword().isEmpty()) {
			userDTO.setPassword(dirtyUser.getPassword());
			userDTO.setPasswordConfirm(dirtyUser.getPassword());
			userDTO.setPasswordChange(true);
		}
		userDTO.setEnabled(dirtyUser.getEnabled());
		userDTO.setRoles(dirtyUser.getRoles());

		return super.saveUpdateEntity(userDTO, model, "user");
	}

	@RequestMapping(value="/create", method=RequestMethod.POST)
	protected ModelAndView saveNew(@FormParam Form form, ModelMap model) throws Exception { 
		UserDTO userDTO = (UserDTO) form.getDto();
		UserDTO dirtyUser = (UserDTO) form.getDirtyDto();

		userDTO.setUsername(dirtyUser.getUsername());
		userDTO.setPassword(dirtyUser.getPassword());
		userDTO.setPasswordConfirm(dirtyUser.getPassword());
		userDTO.setEnabled(dirtyUser.getEnabled());
		userDTO.setRoles(dirtyUser.getRoles());

		return super.saveNewEntity(userDTO, model, "user");
	}

	@RequestMapping(value="/delete", method=RequestMethod.POST)
	protected ModelAndView delete(@FormParam Form form, ModelMap model) throws Exception {   	
		UserDTO userDTO = (UserDTO) form.getDto();	
		return super.deleteEntity(userDTO, model, "user");
	}

}