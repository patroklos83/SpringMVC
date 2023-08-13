package com.patroclos.envers;

import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.event.spi.EnversPostCollectionRecreateEventListenerImpl;
import org.hibernate.event.spi.PostCollectionRecreateEvent;

public class MyEnversPostCollectionRecreateEventListener extends EnversPostCollectionRecreateEventListenerImpl {

	public MyEnversPostCollectionRecreateEventListener(EnversService enversService) {
		super(enversService);
	}
	
	@Override
	public void onPostRecreateCollection(PostCollectionRecreateEvent event) {
		super.onPostRecreateCollection(event);
	}

}
