package com.patroclos.envers;

import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.event.spi.EnversPreCollectionUpdateEventListenerImpl;
import org.hibernate.event.spi.PreCollectionUpdateEvent;

public class MyEnversPreCollectionUpdateEventListener extends EnversPreCollectionUpdateEventListenerImpl {

	public MyEnversPreCollectionUpdateEventListener(EnversService enversService) {
		super(enversService);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onPreUpdateCollection(PreCollectionUpdateEvent event) {
		super.onPreUpdateCollection(event);
	}

}
