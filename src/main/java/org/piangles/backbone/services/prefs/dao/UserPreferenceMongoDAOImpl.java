package org.piangles.backbone.services.prefs.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;

import org.piangles.backbone.services.prefs.UserPreference;
import org.piangles.core.dao.DAOException;
import org.piangles.core.dao.nosql.AbstractDAO;
import org.piangles.core.resources.ResourceManager;
import org.piangles.core.util.abstractions.ConfigProvider;

import com.mongodb.client.model.Filters;

public class UserPreferenceMongoDAOImpl extends AbstractDAO<UserPreference> implements UserPreferenceDAO
{
	private static final String ARRAY_DELIMITER = "|";

	public UserPreferenceMongoDAOImpl(ConfigProvider cp) throws Exception
	{
		super.init(ResourceManager.getInstance().getMongoDataStore(cp));
	}

	public void persistUserPreference(UserPreference prefs) throws DAOException
	{
		for (Entry<Object, Object> es: prefs.getProperties().entrySet())
		{
			if (es.getValue() instanceof Object[])
			{
				List<String> listAsStr = Arrays.asList((Object[])es.getValue()).stream().map(Object::toString).collect(Collectors.toList());
				es.setValue(String.join(ARRAY_DELIMITER, listAsStr));
			}
		}

		super.create(prefs);
	}

	public UserPreference retrieveUserPreference(String userId) throws DAOException
	{
		UserPreference prefs = readOne(Filters.eq("userId", userId));
		if (prefs != null)
		{
			Properties props = prefs.getProperties();
			if (props == null)
			{
				props = new Properties();
			}
			for (Entry<Object, Object> es: props.entrySet())
			{
				String valueAsStr = (String)es.getValue();
				es.setValue(valueAsStr.split("|"));
			}
			prefs = new UserPreference(userId, props);
		}
		else
		{
			prefs = new UserPreference(userId);
		}

		return prefs;
	}
	
	@Override
	protected Class<UserPreference> getTClass()
	{
		return UserPreference.class;
	}
}
