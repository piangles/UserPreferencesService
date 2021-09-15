/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
 
 
package org.piangles.backbone.services.prefs.dao;

import java.util.Map;

import org.bson.Document;
import org.piangles.backbone.services.prefs.UserPreferences;
import org.piangles.core.dao.DAOException;
import org.piangles.core.dao.nosql.AbstractDAO;
import org.piangles.core.resources.ResourceManager;
import org.piangles.core.util.abstractions.ConfigProvider;

import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;

public class UserPreferencesMongoDAOImpl extends AbstractDAO<UserPreferences> implements UserPreferencesDAO
{
	private static final String USER_ID = "userId";
	private static final String NV_PAIR = "nvPair";
	
	public UserPreferencesMongoDAOImpl(ConfigProvider cp) throws Exception
	{
		super.init(ResourceManager.getInstance().getMongoDataStore(cp));
	}

	public void persistUserPreferences(UserPreferences prefs) throws DAOException
	{
		Document doc = new Document();
		doc.put(USER_ID, prefs.getUserId());
		doc.put(NV_PAIR, new BasicDBObject(prefs.getNVPair()));
		super.getConnection().replaceOne(Filters.eq(USER_ID, prefs.getUserId()), doc, new ReplaceOptions().upsert(true));
	}

	public UserPreferences retrieveUserPreferences(String userId) throws DAOException
	{
		UserPreferences prefs = null;
		Document doc = super.getConnection().find(Filters.eq(USER_ID, userId)).first();
		if (doc != null)
		{
			Map<String, Object> map = (Map<String, Object>)doc.get(NV_PAIR);
			prefs = new UserPreferences((String)doc.get(USER_ID), map); 
		}
		return prefs;
	}
	
	@Override
	protected Class<UserPreferences> getTClass()
	{
		return UserPreferences.class;
	}
}
