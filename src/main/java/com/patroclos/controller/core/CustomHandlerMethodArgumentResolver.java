package com.patroclos.controller.core;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import com.patroclos.utils.CustomModelMapper;

import com.patroclos.dto.BaseDTO;

public class CustomHandlerMethodArgumentResolver extends BaseController implements HandlerMethodArgumentResolver {

	@Autowired
	protected DataHolder DataHolder;

	@Autowired
	protected CustomModelMapper CustomModelMapper;

	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		return methodParameter.getParameterAnnotation(FormParam.class) != null;
	}

	@Override
	public Object resolveArgument(
			MethodParameter methodParameter, 
			ModelAndViewContainer modelAndViewContainer, 
			NativeWebRequest nativeWebRequest, 
			WebDataBinderFactory webDataBinderFactory) throws Exception {

		HttpServletRequest request 
		= (HttpServletRequest) nativeWebRequest.getNativeRequest();

		Map<String, String[]> params = request.getParameterMap();

		String formUniqueHash = params.get("hash")[0];
		var data = DataHolder.getDataFromMap(formUniqueHash);

		Form form = null;
		BeanWrapper beanWrapper = null;
		Class<? extends BaseDTO> dtoClass = null;
		if (data != null) {
			if (data instanceof Form) {
				form = (Form)data;
				dtoClass = form.getDto().getClass();
				beanWrapper = new BeanWrapperImpl(dtoClass.getConstructor().newInstance());
			}
		}

		StringToInstantPropertyEditor stringToInstantEditor = new StringToInstantPropertyEditor();
		beanWrapper.registerCustomEditor(Instant.class, stringToInstantEditor);
		for (Map.Entry<String, String[]> paramEntry : params.entrySet()) {
			if (paramEntry.getKey().toUpperCase().equals("HASH")) {
				//formUniqueHash = paramEntry.getValue()[0];
				continue;
			}
			if (beanWrapper.isWritableProperty(paramEntry.getKey()))
				beanWrapper.setPropertyValue(paramEntry.getKey(), paramEntry.getValue()[0]);
		}

		// Map the HTML form text fields to the corresponding DTO instance
		// Lists are omitted
		BaseDTO dirtyDTO = (BaseDTO) beanWrapper.getWrappedInstance();

		// Merge any transient DataTables add/remove element changes to the final dirtyDTO
		// to be injected to the Controller method
		// If dirtyDTO has not been changed already [is null], create a new one
		// by copying the values from the original dto on the form	
		Field[] fields = form.getDirtyDto() != null ? dirtyDTO.getClass().getDeclaredFields()
				: form.getDto().getClass().getDeclaredFields();
		for(var field : fields)
		{
			if (field.getType().equals(List.class))
			{
				field.setAccessible(true);

				if (form.getDirtyDto() != null)
					field.set(dirtyDTO, field.get(form.getDirtyDto()));
				else
					field.set(dirtyDTO, field.get(form.getDto()));
			}
		}
			
		var paramsTransform = params.entrySet().stream().collect(Collectors.toMap(
                entry -> entry.getKey(),
                entry -> entry.getValue() != null ? entry.getValue()[0] : null ));
		
		dirtyDTO = MapTableValuesToDTO(dirtyDTO, paramsTransform);

		form.setDirtyDto(dirtyDTO);

		return form;
	}
}