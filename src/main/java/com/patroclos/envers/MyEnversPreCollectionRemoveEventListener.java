package com.patroclos.envers;

import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.event.spi.EnversPreCollectionRemoveEventListenerImpl;
import org.hibernate.event.spi.PreCollectionRemoveEvent;

import com.patroclos.model.BaseO;

public class MyEnversPreCollectionRemoveEventListener extends EnversPreCollectionRemoveEventListenerImpl {

	public MyEnversPreCollectionRemoveEventListener(EnversService enversService) {
		super(enversService);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onPreRemoveCollection(PreCollectionRemoveEvent event) {
		super.onPreRemoveCollection(event);
	}
}
