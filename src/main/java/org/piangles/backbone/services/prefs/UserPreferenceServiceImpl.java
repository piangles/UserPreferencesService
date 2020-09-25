package org.piangles.backbone.services.prefs;

import org.piangles.backbone.services.prefs.dao.UserPreferenceDAO;
import org.piangles.backbone.services.prefs.dao.UserPreferenceDAOImpl;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.prefs.UserPreference;
import org.piangles.backbone.services.prefs.UserPreferenceException;
import org.piangles.backbone.services.prefs.UserPreferenceService;
import org.piangles.core.dao.DAOException;

public final class UserPreferenceServiceImpl implements UserPreferenceService
{
	private LoggingService logger = Locator.getInstance().getLoggingService();

	private UserPreferenceDAO userPreferenceDAO = null;
	
	public UserPreferenceServiceImpl() throws Exception
	{
		userPreferenceDAO = new UserPreferenceDAOImpl(); 
	}
	
	@Override
	public UserPreference retrieveUserPreference(String userId) throws UserPreferenceException
	{
		UserPreference userPreference = null;
		try
		{
			logger.info("Retriving UserPreferences for : " + userId);
			userPreference = userPreferenceDAO.retrieveUserPreference(userId);
		}
		catch (DAOException e)
		{
			String message = "Faied retriving UserPreferences for : " + userId + " because of : " + e.getMessage();
			logger.error(message, e);
			throw new UserPreferenceException(message);
		}
		return userPreference;
	}

	@Override
	public void persistUserPreference(String userId, UserPreference prefs) throws UserPreferenceException
	{
		try
		{
			logger.info("Persisting UserPreferences for : " + userId);
			userPreferenceDAO.persistUserPreference(userId, prefs);
		}
		catch (DAOException e)
		{
			String message = "Faied persisting UserPreferences for : " + userId + " because of : " + e.getMessage();
			logger.error(message, e);
			throw new UserPreferenceException(message);
		}
	}
}
