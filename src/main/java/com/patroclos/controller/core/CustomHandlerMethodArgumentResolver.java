package com.patroclos.controller.core;

import java.time.Instant;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.patroclos.dto.BaseDTO;

public class CustomHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

	@Autowired
    protected DataHolder DataHolder;

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
	    Class<? extends BaseDTO> dtoClass;
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
	    
    	BaseDTO dirtyDTO = (BaseDTO) beanWrapper.getWrappedInstance();
    	form.setDirtyDto(dirtyDTO);
	    
		return form;
	}
}