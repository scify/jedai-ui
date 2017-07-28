package utils;

import BlockBuilding.*;
import Utilities.Enumerations.BlockBuildingMethod;
import Utilities.Enumerations.RepresentationModel;
import Utilities.Enumerations.SimilarityMetric;

import java.util.List;

public class CustomMethodConfiguration {
    /**
     * Given a Block Building method and a list of parameters, initialize and return that method with the given
     * parameters. Assumes the parameters are of the correct type (they are cast) and correct number.
     *
     * @param method     Method to initialize
     * @param parameters Parameter values
     * @return
     */
    public static IBlockBuilding configureBlockBuildingMethod(BlockBuildingMethod method, List<Object> parameters) {
        switch (method) {
            case STANDARD_BLOCKING:
                return new StandardBlocking();
            case ATTRIBUTE_CLUSTERING:
                return new AttributeClusteringBlocking(
                        (RepresentationModel) parameters.get(0),
                        (SimilarityMetric) parameters.get(1)
                );
            case SUFFIX_ARRAYS:
                return new SuffixArraysBlocking(
                        (int) parameters.get(0),
                        (int) parameters.get(1)
                );
            case Q_GRAMS_BLOCKING:
                return new QGramsBlocking(
                        (int) parameters.get(0)
                );
            case SORTED_NEIGHBORHOOD:
                return new SortedNeighborhoodBlocking(
                        (int) parameters.get(0)
                );
            case EXTENDED_SUFFIX_ARRAYS:
                return new ExtendedSuffixArraysBlocking(
                        (int) parameters.get(0),
                        (int) parameters.get(1)
                );
            case EXTENDED_Q_GRAMS_BLOCKING:
                return new ExtendedQGramsBlocking(
                        (double) parameters.get(0),
                        (int) parameters.get(1)
                );
            case EXTENDED_SORTED_NEIGHBORHOOD:
                return new ExtendedSortedNeighborhoodBlocking(
                        (int) parameters.get(0)
                );
            default:
                return null;
        }
    }
}
