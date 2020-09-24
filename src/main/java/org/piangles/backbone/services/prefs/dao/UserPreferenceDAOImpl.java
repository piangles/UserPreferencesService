package org.piangles.backbone.services.prefs.dao;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Properties;

import com.TBD.backbone.services.Locator;
import com.TBD.backbone.services.config.DefaultConfigProvider;
import com.TBD.backbone.services.logging.LoggingService;
import com.TBD.backbone.services.prefs.UserPreference;
import com.TBD.core.dao.DAOException;
import com.TBD.core.dao.rdbms.AbstractDAO;
import com.TBD.core.dao.rdbms.StatementPreparer;
import com.TBD.core.resources.ResourceManager;
import com.TBD.core.util.coding.JSON;

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
		UserPreference retUserPref = null;
		UserPreference userPref = new UserPreference();

		super.executeSPQueryProcessComplete(GET_USER_PREFS_SP, 1, (call) -> {
			call.setString(1, userId);
		}, (rs) -> {
			if (rs.next())
			{
				String propsAsString = rs.getString(PROPERTIES); 
				try
				{
					Properties props = JSON.getDecoder().decode(propsAsString.getBytes(), Properties.class);
					if (props != null)
					{
						userPref.setProperties(props);
					}
				}
				catch(Exception e)
				{
					logger.error("Unable to decode UserPreferences for userId : " + userId + " because of : " + e.getMessage(), e);
				}
			}
		});

		if (userPref != null && !userPref.isEmpty())
		{
			retUserPref = userPref;
		}
		return retUserPref;
	}
	
	public void persistUserPreference(String userId, UserPreference prefs) throws DAOException
	{
		super.executeSP(PUT_USER_PREFS_SP, 2, new StatementPreparer()
		{
			@Override
			public void prepare(CallableStatement statement) throws SQLException
			{
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
			}
		});
	}

}
