package de.petropia.turtleServer.server.user.database;

import de.petropia.turtleServer.server.user.PetropiaPlayer;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

public class PetropiaPlayerDAO extends BasicDAO<PetropiaPlayer, String> {

    /**
     * A simple Data Acess Object for the {@link PetropiaPlayer}
     * @param entityClass Class of {@link PetropiaPlayer}
     * @param datastore The datastore
     */
    public PetropiaPlayerDAO(Class<PetropiaPlayer> entityClass, Datastore datastore){
        super(entityClass, datastore);
    }
}
