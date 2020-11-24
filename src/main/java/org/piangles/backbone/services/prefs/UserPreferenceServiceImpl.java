package org.piangles.backbone.services.prefs;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.config.DefaultConfigProvider;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.prefs.dao.UserPreferenceDAO;
import org.piangles.backbone.services.prefs.dao.UserPreferenceMongoDAOImpl;
import org.piangles.backbone.services.prefs.dao.UserPreferenceRDBMSDAOImpl;
import org.piangles.core.dao.DAOException;
import org.piangles.core.util.abstractions.ConfigProvider;

public final class UserPreferenceServiceImpl implements UserPreferenceService
{
	private static final String COMPONENT_ID = "131a693b-a821-4e58-a44e-ddef529ca634";
	private static final String DEFAULT_DAO_TYPE = "NoSql";
	private static final String DAO_TYPE = "DAOType";

	private LoggingService logger = Locator.getInstance().getLoggingService();

	private UserPreferenceDAO userPreferenceDAO = null;
	
	public UserPreferenceServiceImpl() throws Exception
	{
		ConfigProvider cp = new DefaultConfigProvider(UserPreferenceService.NAME, COMPONENT_ID);
		Properties props = cp.getProperties();
		if (DEFAULT_DAO_TYPE.equals(props.getProperty(DAO_TYPE)))
		{
			userPreferenceDAO = new UserPreferenceMongoDAOImpl(cp); 
		}
		else
		{
			userPreferenceDAO = new UserPreferenceRDBMSDAOImpl(cp); 
		}
		logger.info("Starting UserPreferenceService with DAO: " + userPreferenceDAO.getClass());
	}
	
	@Override
	public void persistUserPreference(String userId, UserPreference prefs) throws UserPreferenceException
	{
		try
		{
			logger.info("Persisting UserPreferences for : " + userId);
			if (prefs == null || prefs.getNVPair() == null)
			{
				prefs = new UserPreference(userId);
			}
			for (Entry<String, Object> es: prefs.getNVPair().entrySet())
			{
				if (es.getValue() instanceof Object[])
				{
					List<String> listAsStr = Arrays.asList((Object[])es.getValue()).stream().map(Object::toString).collect(Collectors.toList());
					es.setValue(listAsStr);
				}
			}

			userPreferenceDAO.persistUserPreference(prefs);
		}
		catch (DAOException e)
		{
			String message = "Faied persisting UserPreferences for : " + userId + " because of : " + e.getMessage();
			logger.error(message, e);
			throw new UserPreferenceException(message);
		}
	}

	@Override
	public UserPreference retrieveUserPreference(String userId) throws UserPreferenceException
	{
		UserPreference userPreference = null;
		try
		{
			logger.info("Retriving UserPreferences for : " + userId);
			userPreference = userPreferenceDAO.retrieveUserPreference(userId);
			if (userPreference == null || userPreference.getNVPair() == null)
			{
				userPreference = new UserPreference(userId);
				/**
				 * Without the below, the object when encoded to JSON 
				 * will return a null NVPair instead of creating an
				 * Map with no values.
				 */
				userPreference.setValue("PiAngles.Seeding", "SampleValue");
			}
		}
		catch (DAOException e)
		{
			String message = "Faied retriving UserPreferences for : " + userId + " because of : " + e.getMessage();
			logger.error(message, e);
			throw new UserPreferenceException(message);
		}
		return userPreference;
	}
}
