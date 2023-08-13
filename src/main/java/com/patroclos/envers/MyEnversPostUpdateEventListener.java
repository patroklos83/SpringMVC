package com.patroclos.envers;

import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.event.spi.EnversPostUpdateEventListenerImpl;
import org.hibernate.event.spi.PostUpdateEvent;

import com.patroclos.model.BaseO;

public class MyEnversPostUpdateEventListener extends EnversPostUpdateEventListenerImpl {

	public MyEnversPostUpdateEventListener(EnversService enversService) {
		super(enversService);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onPostUpdate(PostUpdateEvent event) {
		super.onPostUpdate(event);
	}

}
