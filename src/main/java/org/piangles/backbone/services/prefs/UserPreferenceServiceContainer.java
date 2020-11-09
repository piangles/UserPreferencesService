package org.piangles.backbone.services.prefs;

import org.piangles.core.email.EmailSupport;
import org.piangles.core.services.remoting.AbstractContainer;
import org.piangles.core.services.remoting.ContainerException;

public class UserPreferenceServiceContainer extends AbstractContainer
{
	public static void main(String[] args)
	{
		UserPreferenceServiceContainer container = new UserPreferenceServiceContainer();
		try
		{
			container.performSteps(args);
		}
		catch (ContainerException e)
		{
			EmailSupport.notify(e, e.getMessage());
			System.exit(-1);
		}
	}

	public UserPreferenceServiceContainer()
	{
		super(UserPreferenceService.NAME);
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
