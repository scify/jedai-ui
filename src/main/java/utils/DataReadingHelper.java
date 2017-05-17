package utils;

import DataModel.EntityProfile;
import DataReader.EntityReader.*;

import java.util.List;

/**
 * Class to help with reading the required data for the JedAI toolkit using the available UI options
 */
public class DataReadingHelper {
    /**
     * Get a list of Entity Profiles, using the specified reader type (CSV, Database, RDF or Serialized)
     *
     * @param path Path of entities file, for reader
     * @param type Type of reader. Available readers are specified in JedaiOptions helper class
     * @return List of read entities
     */
    public static List<EntityProfile> getEntities(String path, String type) {
        List<EntityProfile> profiles = null;
        IEntityReader eReader = null;

        switch (type) {
            case JedaiOptions.CSV:
                eReader = new EntityCSVReader(path);
                break;
            case JedaiOptions.DATABASE:
                eReader = new EntityDBReader(path);
                break;
            case JedaiOptions.RDF:
                eReader = new EntityRDFReader(path);
                break;
            case JedaiOptions.SERIALIZED:
                eReader = new EntitySerializationReader(path);
                break;
        }

        if (eReader != null) {
            profiles = eReader.getEntityProfiles();
        }

        return profiles;
    }
}
