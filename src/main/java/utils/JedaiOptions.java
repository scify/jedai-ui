package utils;

public class JedaiOptions {
    // Entity resolution types
    public static final String DIRTY_ER = "Dirty Entity Resolution";
    public static final String CLEAN_CLEAN_ER = "Clean-Clean Entity Resolution";

    // Block Building methods
    public static final String STANDARD_TOKEN_BUILDING = "Standard/Token Blocking";
    public static final String ATTRIBUTE_CLUSTERING = "Attribute Clustering";
    public static final String SORTED_NEIGHBORHOOD = "Sorted Neighborhood";
    public static final String SORTED_NEIGHBORHOOD_EXTENDED = "Extended Sorted Neighborhood";
    public static final String Q_GRAMS_BLOCKING = "Q-Grams Blocking";
    public static final String Q_GRAMS_BLOCKING_EXTENDED = "Extended Q-Grams Blocking";
    public static final String SUFFIX_ARRAYS_BLOCKING = "Suffix Arrays Blocking";
    public static final String SUFFIX_ARRAYS_BLOCKING_EXTENDED = "Extended Suffix Arrays Blocking";

    // Block Cleaning methods
    public static final String BLOCK_FILTERING = "Block Filtering";
    public static final String BLOCK_SCHEDULING = "Block Scheduling";
    public static final String SIZE_BASED_BLOCK_PURGING = "Size-based Block Purging";
    public static final String COMPARISON_BASED_BLOCK_PURGING = "Comparison-based Block Purging";

    // Comparison Cleaning methods
    public static final String NO_CLEANING = "No cleaning";
    public static final String COMPARISON_PROPAGATION = "Comparison Propagation";
    public static final String CARDINALITY_EDGE_PRUNING = "Cardinality Edge Pruning (CEP)";
    public static final String CARDINALITY_NODE_PRUNING = "Cardinality Node Pruning (CNP)";
    public static final String WEIGHED_EDGE_PRUNING = "Weighed Edge Pruning (WEP)";
    public static final String WEIGHED_NODE_PRUNING = "Weighed Node Pruning (WNP)";
    public static final String RECIPROCAL_CARDINALITY_NODE_PRUNING = "Reciprocal Cardinality Node Pruning (ReCNP)";
    public static final String RECIPROCAL_WEIGHED_NODE_PRUNING = "Reciprocal Weighed Node Pruning (ReWNP)";

    // Entity Matching methods
    public static final String GROUP_LINKAGE = "Group Linkage";
    public static final String PROFILE_MATCHER = "Profile Matcher";

    // Profile Matcher parameters
    public static final String REPRESENTATION = "Representation";
    public static final String SIMILARITY = "Similarity";

    // Entity Clustering methods
    public static final String CENTER_CLUSTERING = "Center Clustering";
    public static final String CONNECTED_COMPONENTS_CLUSTERING = "Connected Components Clustering";
    public static final String CUT_CLUSTERING = "Cut Clustering";
    public static final String MARKOV_CLUSTERING = "Markov Clustering";
    public static final String MERGE_CENTER_CLUSTERING = "Merge-Center Clustering";
    public static final String RICOCHET_SR_CLUSTERING = "Ricochet SR Clustering";
}
