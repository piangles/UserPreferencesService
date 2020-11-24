package org.piangles.backbone.services.prefs.dao;

import java.util.Map;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.prefs.UserPreference;
import org.piangles.core.dao.DAOException;
import org.piangles.core.dao.rdbms.AbstractDAO;
import org.piangles.core.resources.ResourceManager;
import org.piangles.core.util.abstractions.ConfigProvider;
import org.piangles.core.util.coding.JSON;

public class UserPreferenceRDBMSDAOImpl extends AbstractDAO implements UserPreferenceDAO
{
	private static final String GET_USER_PREFS_SP = "Backbone.GetUserPreference";
	private static final String PUT_USER_PREFS_SP = "Backbone.PutUserPreference";

	private LoggingService logger = Locator.getInstance().getLoggingService();
	
	private static final String PROPERTIES = "Properties";
	
	public UserPreferenceRDBMSDAOImpl(ConfigProvider cp) throws Exception
	{
		super.init(ResourceManager.getInstance().getRDBMSDataStore(cp));
	}

	public void persistUserPreference(UserPreference prefs) throws DAOException
	{
		super.executeSP(PUT_USER_PREFS_SP, 2, (statement) -> {
			statement.setString(1, prefs.getUserId());
			try
			{
				byte[] propertiesAsJson = JSON.getEncoder().encode(prefs.getNVPair());
				statement.setString(2, new String(propertiesAsJson));
			}
			catch (Exception e)
			{
				logger.error("Unable to encode UserPreferences for userId : " + prefs.getUserId() + " because of : " + e.getMessage(), e);
			}
		});
	}

	public UserPreference retrieveUserPreference(String userId) throws DAOException
	{
		UserPreference retUserPref = super.executeSPQuery(GET_USER_PREFS_SP, 1, (call) -> {
			call.setString(1, userId);
		}, (rs, call) -> {
			UserPreference userPref = null;
			String nvPairAsString = rs.getString(PROPERTIES); 
			try
			{
				Map<String, Object> nvPair = null;
				if (nvPairAsString != null)
				{
					nvPair = JSON.getDecoder().decode(nvPairAsString.getBytes(), Map.class);
				}
				userPref = new UserPreference(userId, nvPair);
			}
			catch(Exception e)
			{
				logger.error("Unable to decode UserPreferences for userId : " + userId + " because of : " + e.getMessage(), e);
			}
			return userPref;
		});

		//TODO Incorporate AddendumPreferences table
		return retUserPref;
	}
}
