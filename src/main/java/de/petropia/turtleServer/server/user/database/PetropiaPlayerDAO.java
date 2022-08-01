package de.petropia.turtleServer.server.user.database;

import de.petropia.turtleServer.server.user.PetropiaPlayer;
import dev.morphia.Datastore;

public class PetropiaPlayerDAO extends Dao{

    /**
     * A simple Data Acess Object for the {@link PetropiaPlayer}
     * @param entityClass Class of {@link PetropiaPlayer}
     * @param datastore The datastore
     */
    public PetropiaPlayerDAO(Class<PetropiaPlayer> entityClass, Datastore datastore){
        super(entityClass, datastore);
    }
}
