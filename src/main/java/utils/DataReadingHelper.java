package utils;

import DataModel.EntityProfile;
import DataReader.EntityReader.*;
import DataReader.GroundTruthReader.GtCSVReader;
import DataReader.GroundTruthReader.GtRDFReader;
import DataReader.GroundTruthReader.GtSerializationReader;
import DataReader.GroundTruthReader.IGroundTruthReader;
import Utilities.DataStructures.AbstractDuplicatePropagation;
import Utilities.DataStructures.BilateralDuplicatePropagation;
import Utilities.DataStructures.UnilateralDuplicatePropagation;

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

    /**
     * Read ground truth using the specified reader.
     *
     * @param path   Path of ground truth file
     * @param type   Type of ground truth file (see JedaiOptions)
     * @param erType Clean-Clean or Dirty ER
     * @return Ground truth (duplicate propagation)
     */
    public static AbstractDuplicatePropagation getGroundTruth(String path, String type, String erType, List<EntityProfile> profilesD1, List<EntityProfile> profilesD2) {
        AbstractDuplicatePropagation dp = null;
        IGroundTruthReader gtReader = null;

        switch (type) {
            case JedaiOptions.CSV:
                gtReader = new GtCSVReader(path);
                break;
            case JedaiOptions.DATABASE:
                gtReader = new GtSerializationReader(path);
                //todo: where is database ground truth reader?
                break;
            case JedaiOptions.RDF:
                gtReader = new GtRDFReader(path);
                break;
            case JedaiOptions.SERIALIZED:
                gtReader = new GtSerializationReader(path);
                break;
        }

        if (gtReader != null) {
            if (erType.equals(JedaiOptions.DIRTY_ER)) {
                dp = new UnilateralDuplicatePropagation(gtReader.getDuplicatePairs(profilesD1));
            } else {
                dp = new BilateralDuplicatePropagation(gtReader.getDuplicatePairs(profilesD1, profilesD2));
            }
        }

        return dp;
    }
}
