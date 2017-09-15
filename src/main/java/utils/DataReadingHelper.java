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
import com.google.common.primitives.Ints;

import java.util.List;
import java.util.Set;

/**
 * Class to help with reading the required data for the JedAI toolkit using the available UI options
 */
public class DataReadingHelper {
    /**
     * Get a list of Entity Profiles, using the specified reader type (CSV, Database, RDF or Serialized)
     *
     * @param type       Type of reader. Available readers are specified in JedaiOptions helper class
     * @param parameters Parameters for Entity Reader
     * @return List of read entities
     */
    public static List<EntityProfile> getEntities(String type, List<Object> parameters) {
        List<EntityProfile> profiles = null;
        IEntityReader eReader = null;

        // If there are no parameters, we cannot initialize the reader
        if (parameters.isEmpty())
            return null;

        switch (type) {
            case JedaiOptions.CSV:
                // Get parameters
                String csvPath = parameters.get(0).toString();
                boolean attributeNamesInFirstRow = (boolean) parameters.get(1);
                char separator = (char) parameters.get(2);
                int idIndex = (int) parameters.get(3);
                Set<Integer> indicesToExcludeSet = (Set<Integer>) parameters.get(4);

                // Initialize the Entity reader
                EntityCSVReader csvReader = new EntityCSVReader(csvPath);
                csvReader.setAttributeNamesInFirstRow(attributeNamesInFirstRow);
                csvReader.setSeparator(separator);
                csvReader.setIdIndex(idIndex);
                csvReader.setAttributesToExclude(Ints.toArray(indicesToExcludeSet));

                eReader = csvReader;
                break;
            case JedaiOptions.DATABASE:
                // Get parameters
                String url = parameters.get(0).toString();
                String table = parameters.get(1).toString();
                String username = parameters.get(2).toString();
                String password = parameters.get(3).toString();
                Set<String> excludedAttrs = (Set<String>) parameters.get(4);
                boolean ssl = (boolean) parameters.get(5);

                // Initialize the Entity reader
                EntityDBReader dbReader = new EntityDBReader(url);
                dbReader.setTable(table);
                dbReader.setUser(username);
                dbReader.setPassword(password);
                dbReader.setAttributesToExclude(excludedAttrs.toArray(new String[0]));
                dbReader.setSSL(ssl);

                eReader = dbReader;
                break;
            case JedaiOptions.RDF:
                //todo: Get parameters

                // Initialize the Entity reader
                eReader = new EntityRDFReader("");
                break;
            case JedaiOptions.SERIALIZED:
                // Get parameters
                String jsoPath = parameters.get(0).toString();

                // Initialize the Entity reader
                eReader = new EntitySerializationReader(jsoPath);
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
     * @param type       Type of ground truth file (see JedaiOptions)
     * @param parameters Parameters for reader
     * @param erType     Clean-Clean or Dirty ER
     * @param profilesD1 Entity Profiles for Dataset 1
     * @param profilesD2 Entity Profiles for Dataset 2
     * @return Ground truth (duplicate propagation)
     */
    public static AbstractDuplicatePropagation getGroundTruth(String type, List<Object> parameters, String erType, List<EntityProfile> profilesD1, List<EntityProfile> profilesD2) {
        AbstractDuplicatePropagation dp = null;
        IGroundTruthReader gtReader = null;

        // If there are no parameters, we cannot initialize the reader
        if (parameters.isEmpty())
            return null;

        // Get the path from the parameters (todo: do this only for Serialized...)
        String path = parameters.get(0).toString();

        switch (type) {
            case JedaiOptions.CSV:
                gtReader = new GtCSVReader(path);
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
