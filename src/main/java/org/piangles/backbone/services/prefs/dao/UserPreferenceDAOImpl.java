package org.piangles.backbone.services.prefs.dao;

import java.util.Properties;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.config.DefaultConfigProvider;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.prefs.UserPreference;
import org.piangles.core.dao.DAOException;
import org.piangles.core.dao.rdbms.AbstractDAO;
import org.piangles.core.resources.ResourceManager;
import org.piangles.core.util.coding.JSON;

public class UserPreferenceDAOImpl extends AbstractDAO implements UserPreferenceDAO
{
	private static final String COMPONENT_ID = "131a693b-a821-4e58-a44e-ddef529ca634";
	private static final String GET_USER_PREFS_SP = "Backbone.GetUserPreference";
	private static final String PUT_USER_PREFS_SP = "Backbone.PutUserPreference";

	private LoggingService logger = Locator.getInstance().getLoggingService();
	
	private static final String PROPERTIES = "Properties";
	
	public UserPreferenceDAOImpl() throws Exception
	{
		super.init(ResourceManager.getInstance().getRDBMSDataStore(new DefaultConfigProvider("UserPreferences", COMPONENT_ID)));
	}

	public UserPreference retrieveUserPreference(String userId) throws DAOException
	{
		UserPreference retUserPref = super.executeSPQuery(GET_USER_PREFS_SP, 1, (call) -> {
			call.setString(1, userId);
		}, (rs) -> {
			UserPreference userPref = null;
			String propsAsString = rs.getString(PROPERTIES); 
			try
			{
				Properties props = JSON.getDecoder().decode(propsAsString.getBytes(), Properties.class);
				if (props != null)
				{
					userPref = new UserPreference(props);
				}
			}
			catch(Exception e)
			{
				logger.error("Unable to decode UserPreferences for userId : " + userId + " because of : " + e.getMessage(), e);
			}
			return userPref;
		});

		return retUserPref;
	}
	
	public void persistUserPreference(String userId, UserPreference prefs) throws DAOException
	{
		super.executeSP(PUT_USER_PREFS_SP, 2, (statement) -> {
			statement.setString(1, userId);
			try
			{
				byte[] propertiesAsJson = JSON.getEncoder().encode(prefs.getProperties());
				statement.setString(2, new String(propertiesAsJson));
			}
			catch (Exception e)
			{
				logger.error("Unable to encode UserPreferences for userId : " + userId + " because of : " + e.getMessage(), e);
			}
		});
	}

}
