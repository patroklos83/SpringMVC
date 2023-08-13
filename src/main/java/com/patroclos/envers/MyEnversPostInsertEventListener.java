package com.patroclos.envers;

import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.event.spi.EnversPostInsertEventListenerImpl;
import org.hibernate.event.spi.PostInsertEvent;

import com.patroclos.model.BaseO;

public class MyEnversPostInsertEventListener extends EnversPostInsertEventListenerImpl {

	public MyEnversPostInsertEventListener(EnversService enversService) {
		super(enversService);
	}

	@Override
	public void onPostInsert(PostInsertEvent event) {
		super.onPostInsert(event);
	}
}