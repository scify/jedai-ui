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
import utils.JPair;

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
    public static List<EntityProfile> getEntities(String type, List<JPair<String, Object>> parameters) {
        List<EntityProfile> profiles = null;
        IEntityReader eReader = null;

        // If there are no parameters, we cannot initialize the reader
        if (parameters.isEmpty())
            return null;

        switch (type) {
            case JedaiOptions.CSV:
                // Get parameters
                String csvPath = parameters.get(0).getRight().toString();
                boolean attributeNamesInFirstRow = (boolean) parameters.get(1).getRight();
                char separator = (char) parameters.get(2).getRight();
                int idIndex = (int) parameters.get(3).getRight();
                Set<Integer> indicesToExcludeSet = (Set<Integer>) parameters.get(4).getRight();

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
                String url = parameters.get(0).getRight().toString();
                String table = parameters.get(1).getRight().toString();
                String username = parameters.get(2).getRight().toString();
                String password = parameters.get(3).getRight().toString();
                Set<String> excludedAttrs = (Set<String>) parameters.get(4).getRight();
                boolean ssl = (boolean) parameters.get(5).getRight();

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
                // Get parameters
                String rdfPath = parameters.get(0).getRight().toString();
                Set<String> excludedPredicates = (Set<String>) parameters.get(1).getRight();

                // Initialize the Entity reader
                EntityRDFReader rdfReader = new EntityRDFReader(rdfPath);
                rdfReader.setAttributesToExclude(excludedPredicates.toArray(new String[0]));
                eReader = rdfReader;
                break;
            case JedaiOptions.SERIALIZED:
                // Get parameters
                String jsoPath = parameters.get(0).getRight().toString();

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
    public static AbstractDuplicatePropagation getGroundTruth(String type, List<JPair<String, Object>> parameters, String erType, List<EntityProfile> profilesD1, List<EntityProfile> profilesD2) {
        AbstractDuplicatePropagation dp = null;
        IGroundTruthReader gtReader = null;

        // If there are no parameters, we cannot initialize the reader
        if (parameters.isEmpty())
            return null;

        switch (type) {
            case JedaiOptions.CSV:
                // Get parameters
                String csvPath = parameters.get(0).getRight().toString();
                boolean ignoreFirstRow = (boolean) parameters.get(1).getRight();
                char separator = (char) parameters.get(2).getRight();

                // Initialize the reader
                GtCSVReader csvReader = new GtCSVReader(csvPath);
                csvReader.setIgnoreFirstRow(ignoreFirstRow);
                csvReader.setSeparator(separator);

                gtReader = csvReader;
                break;
            case JedaiOptions.RDF:
                // Get parameters
                String rdfPath = parameters.get(0).getRight().toString();

                // Initialize the reader
                gtReader = new GtRDFReader(rdfPath);
                break;
            case JedaiOptions.SERIALIZED:
                // Get parameters
                String jsoPath = parameters.get(0).getRight().toString();

                // Initialize the reader
                gtReader = new GtSerializationReader(jsoPath);
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
