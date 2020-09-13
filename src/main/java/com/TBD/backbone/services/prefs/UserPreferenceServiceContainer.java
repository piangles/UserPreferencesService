package com.TBD.backbone.services.prefs;

import com.TBD.core.email.EmailSupport;
import com.TBD.core.services.remoting.AbstractContainer;
import com.TBD.core.services.remoting.ContainerException;

public class UserPreferenceServiceContainer extends AbstractContainer
{
	public static void main(String[] args)
	{
		UserPreferenceServiceContainer container = new UserPreferenceServiceContainer();
		try
		{
			container.performSteps();
		}
		catch (ContainerException e)
		{
			EmailSupport.notify(e, e.getMessage());
			System.exit(-1);
		}
	}

	public UserPreferenceServiceContainer()
	{
		super("UserPreferenceService");
	}
	
	@Override
	protected Object createServiceImpl() throws ContainerException
	{
		Object service = null;
		try
		{
			service = new UserPreferenceServiceImpl();
		}
		catch (Exception e)
		{
			throw new ContainerException(e);
		}
		return service;
	}
}