package com.patroclos.envers;

import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.event.spi.EnversPreUpdateEventListenerImpl;
import org.hibernate.event.spi.PreUpdateEvent;

import com.patroclos.model.BaseO;

public class MyEnversPreUpdateEventListener extends EnversPreUpdateEventListenerImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyEnversPreUpdateEventListener(EnversService enversService) {
		super(enversService);
	}

	@Override
	public boolean onPreUpdate(PreUpdateEvent event) {
		BaseO o = (BaseO)event.getEntity();
		return super.onPreUpdate(event);
	}

}