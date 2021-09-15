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
 
 
 
package org.piangles.backbone.services.prefs;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.config.DefaultConfigProvider;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.prefs.dao.UserPreferencesDAO;
import org.piangles.backbone.services.prefs.dao.UserPreferencesMongoDAOImpl;
import org.piangles.backbone.services.prefs.dao.UserPreferencesRDBMSDAOImpl;
import org.piangles.core.dao.DAOException;
import org.piangles.core.util.abstractions.ConfigProvider;

public final class UserPreferencesServiceImpl implements UserPreferencesService
{
	private static final String COMPONENT_ID = "131a693b-a821-4e58-a44e-ddef529ca634";
	private static final String DEFAULT_DAO_TYPE = "NoSql";
	private static final String DAO_TYPE = "DAOType";

	private LoggingService logger = Locator.getInstance().getLoggingService();

	private UserPreferencesDAO userPreferencesDAO = null;
	
	public UserPreferencesServiceImpl() throws Exception
	{
		ConfigProvider cp = new DefaultConfigProvider(UserPreferencesService.NAME, COMPONENT_ID);
		Properties props = cp.getProperties();
		if (DEFAULT_DAO_TYPE.equals(props.getProperty(DAO_TYPE)))
		{
			userPreferencesDAO = new UserPreferencesMongoDAOImpl(cp); 
		}
		else
		{
			userPreferencesDAO = new UserPreferencesRDBMSDAOImpl(cp); 
		}
		logger.info("Starting UserPreferenceService with DAO: " + userPreferencesDAO.getClass());
	}
	
	@Override
	public void persistUserPreference(String userId, UserPreferences prefs) throws UserPreferencesException
	{
		try
		{
			logger.info("Persisting UserPreferences for : " + userId);
			if (prefs == null || prefs.getNVPair() == null)
			{
				prefs = new UserPreferences(userId);
			}
			for (Entry<String, Object> es: prefs.getNVPair().entrySet())
			{
				if (es.getValue() instanceof Object[])
				{
					List<String> listAsStr = Arrays.asList((Object[])es.getValue()).stream().map(Object::toString).collect(Collectors.toList());
					es.setValue(listAsStr);
				}
			}

			userPreferencesDAO.persistUserPreferences(prefs);
		}
		catch (DAOException e)
		{
			String message = "Faied persisting UserPreferences for : " + userId + " because of : " + e.getMessage();
			logger.error(message, e);
			throw new UserPreferencesException(message);
		}
	}

	@Override
	public UserPreferences retrieveUserPreference(String userId) throws UserPreferencesException
	{
		UserPreferences userPreference = null;
		try
		{
			logger.info("Retriving UserPreferences for : " + userId);
			userPreference = userPreferencesDAO.retrieveUserPreferences(userId);
			if (userPreference == null || userPreference.getNVPair() == null)
			{
				userPreference = new UserPreferences(userId);
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
			throw new UserPreferencesException(message);
		}
		return userPreference;
	}
}
