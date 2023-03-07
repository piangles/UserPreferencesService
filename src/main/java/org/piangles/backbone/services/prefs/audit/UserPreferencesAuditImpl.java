package org.piangles.backbone.services.prefs.audit;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.mongo.MongoRepository;
import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.prefs.UserPreferences;

import com.mongodb.client.MongoDatabase;

public class UserPreferencesAuditImpl implements UserPreferencesAudit
{
	private static final String AUTHOR = "PiAngles"; //needs to change ?
	
	private static final int CACHE_SIZE = 1;
	
	private LoggingService logger = Locator.getInstance().getLoggingService();
	
	private Javers javers = null;
	
	private MongoRepository javersRepository = null;
	
	public UserPreferencesAuditImpl(MongoDatabase mongoDb)
	{
		this.javersRepository = MongoRepository.mongoRepositoryWithDocumentDBCompatibility(mongoDb, CACHE_SIZE);
		
		this.javers = JaversBuilder.javers()
							  .registerEntities(UserPreferences.class)
							  .registerJaversRepository(javersRepository)
							  .build();
	}
	
	@Override
	public void performAudit(UserPreferences userPreferences)
	{
		logger.info("Performing UsePreferences object audit for userId: " + userPreferences.getUserId());
		try 
		{
			AuditableUserPreferences auditRecord = new AuditableUserPreferences(userPreferences.getUserId(), userPreferences);
			javers.commit(AUTHOR, auditRecord);
		} 
		catch (Exception e) 
		{
			logger.error("Unable to perform userPreferences audit. Reason: " + e.getMessage(), e);
		}
	}

	@Override
	public String retrieveLatestAuditRecord(UserPreferences userPreferences) 
	{
		CdoSnapshot snapshot = null;
		try 
		{
			snapshot = javers.getLatestSnapshot(userPreferences.getUserId(), UserPreferences.class).get();
		} 
		catch (Exception e) 
		{
			logger.error("Unable to retrieveLatestAuditRecord for userPreferences. Reason: " + e.getMessage(), e);
		}
		
		return javers.getJsonConverter().toJson(snapshot);
	}
}
