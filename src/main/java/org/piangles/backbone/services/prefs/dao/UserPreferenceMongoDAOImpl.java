package org.piangles.backbone.services.prefs.dao;

import java.util.Map;

import org.bson.Document;
import org.piangles.backbone.services.prefs.UserPreference;
import org.piangles.core.dao.DAOException;
import org.piangles.core.dao.nosql.AbstractDAO;
import org.piangles.core.resources.ResourceManager;
import org.piangles.core.util.abstractions.ConfigProvider;

import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;

public class UserPreferenceMongoDAOImpl extends AbstractDAO<UserPreference> implements UserPreferenceDAO
{
	private static final String USER_ID = "userId";
	private static final String NV_PAIR = "nvPair";
	
	public UserPreferenceMongoDAOImpl(ConfigProvider cp) throws Exception
	{
		super.init(ResourceManager.getInstance().getMongoDataStore(cp));
	}

	public void persistUserPreference(UserPreference prefs) throws DAOException
	{
		Document doc = new Document();
		doc.put(USER_ID, prefs.getUserId());
		doc.put(NV_PAIR, new BasicDBObject(prefs.getNVPair()));
		super.getConnection().replaceOne(Filters.eq(USER_ID, prefs.getUserId()), doc, new ReplaceOptions().upsert(true));
	}

	public UserPreference retrieveUserPreference(String userId) throws DAOException
	{
		UserPreference prefs = null;
		Document doc = super.getConnection().find(Filters.eq(USER_ID, userId)).first();
		if (doc != null)
		{
			Map<String, Object> map = (Map<String, Object>)doc.get(NV_PAIR);
			prefs = new UserPreference((String)doc.get(USER_ID), map); 
		}
		return prefs;
	}
	
	@Override
	protected Class<UserPreference> getTClass()
	{
		return UserPreference.class;
	}
}
