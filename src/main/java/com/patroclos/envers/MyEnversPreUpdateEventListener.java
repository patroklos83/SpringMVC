package com.patroclos.envers;

import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.event.spi.EnversPreUpdateEventListenerImpl;
import org.hibernate.event.spi.PreUpdateEvent;

public class MyEnversPreUpdateEventListener extends EnversPreUpdateEventListenerImpl {

	public MyEnversPreUpdateEventListener(EnversService enversService) {
		super(enversService);
	}

	@Override
	public boolean onPreUpdate(PreUpdateEvent event) {
		return super.onPreUpdate(event);
	}

}