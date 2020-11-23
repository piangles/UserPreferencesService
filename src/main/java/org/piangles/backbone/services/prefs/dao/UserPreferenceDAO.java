package org.piangles.backbone.services.prefs.dao;

import org.piangles.backbone.services.prefs.UserPreference;
import org.piangles.core.dao.DAOException;

public interface UserPreferenceDAO
{
	public UserPreference retrieveUserPreference(String userId) throws DAOException;
	
	public void persistUserPreference(UserPreference prefs) throws DAOException;

}
