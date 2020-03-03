package org.scify.jedai.gui.utilities;

public class JedaiOptions {
    // Method configuration types
    public static final String DEFAULT_CONFIG = "Default";
    public static final String AUTOMATIC_CONFIG = "Automatic";
    public static final String MANUAL_CONFIG = "Manual";

    // Workflows
    public static final String WORKFLOW_BLOCKING_BASED = "Blocking-based";
    public static final String WORKFLOW_JOIN_BASED = "Join-based";
    public static final String WORKFLOW_PROGRESSIVE = "Progressive";


    // Entity resolution types
    public static final String DIRTY_ER = "Dirty Entity Resolution";
    public static final String CLEAN_CLEAN_ER = "Clean-Clean Entity Resolution";

    // File type options
    public static final String CSV = "CSV";
    public static final String DATABASE = "Database";
    public static final String RDF = "RDF";
    public static final String SERIALIZED = "Serialized";
    public static final String XML = "XML";

    // Schema Clustering Methods
    public static final String NO_SCHEMA_CLUSTERING = "No Schema Clustering";
    public static final String ATTRIBUTE_NAME_CLUSTERING = "Attribute Name Clustering";
    public static final String ATTRIBUTE_VALUE_CLUSTERING = "Attribute Value Clustering";
    public static final String HOLISTIC_ATTRIBUTE_CLUSTERING = "Holistic Attribute Clustering";

    // Block Building methods
    public static final String STANDARD_TOKEN_BUILDING = "Standard/Token Blocking";
    public static final String SORTED_NEIGHBORHOOD = "Sorted Neighborhood";
    public static final String SORTED_NEIGHBORHOOD_EXTENDED = "Extended Sorted Neighborhood";
    public static final String Q_GRAMS_BLOCKING = "Q-Grams Blocking";
    public static final String Q_GRAMS_BLOCKING_EXTENDED = "Extended Q-Grams Blocking";
    public static final String SUFFIX_ARRAYS_BLOCKING = "Suffix Arrays Blocking";
    public static final String SUFFIX_ARRAYS_BLOCKING_EXTENDED = "Extended Suffix Arrays Blocking";
    public static final String LSH_SUPERBIT_BLOCKING = "LSH SuperBit Blocking";
    public static final String LSH_MINHASH_BLOCKING = "LSH MinHash Blocking";

    // Block Cleaning methods
    public static final String BLOCK_FILTERING = "Block Filtering";
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
    public static final String CANOPY_CLUSTERING = "Canopy Clustering";
    public static final String CANOPY_CLUSTERING_EXTENDED = "Extended Canopy Clustering";

    // Entity Matching methods
    public static final String GROUP_LINKAGE = "Group Linkage";
    public static final String PROFILE_MATCHER = "Profile Matcher";

    // todo: These could be used for displaying names better in manual configuration (enum type)?
//    // Representation Model parameters
//    public static final String CHARACTER_BIGRAMS = "Character Bigrams";
//    public static final String CHARACTER_BIGRAM_GRAPHS = "Character Bigram Graphs";
//    public static final String CHARACTER_TRIGRAMS = "Character Trigrams";
//    public static final String CHARACTER_TRIGRAM_GRAPHS = "Character Trigram Graphs";
//    public static final String CHARACTER_FOURGRAMS = "Character Fourgrams";
//    public static final String CHARACTER_FOURGRAM_GRAPHS = "Character Fourgram Graphs";
//    public static final String TOKEN_UNIGRAMS = "Token Unigrams";
//    public static final String TOKEN_UNIGRAMS_TF_IDF = "Token Unigrams TF-IDF";
//    public static final String TOKEN_UNIGRAM_GRAPHS = "Token Unigram Graphs";
//    public static final String TOKEN_BIGRAMS = "Token Bigrams";
//    public static final String TOKEN_BIGRAMS_TF_IDF = "Token Bigrams TF-IDF";
//    public static final String TOKEN_BIGRAM_GRAPHS = "Token Bigram Graphs";
//    public static final String TOKEN_TRIGRAMS = "Token Trigrams";
//    public static final String TOKEN_TRIGRAMS_TF_IDF = "Token Trigrams TF-IDF";
//    public static final String TOKEN_TRIGRAM_GRAPHS = "Token Trigram Graphs";
//
//    // Similarity Method options
//    public static final String ARCS_SIMILARITY = "Arcs Similarity";
//    public static final String COSINE_SIMILARITY = "Cosine Similarity";
//    public static final String ENHANCED_JACCARD_SIMILARITY = "Enhanced Jaccard Similarity";
//    public static final String GENERALIZED_JACCARD_SIMILARITY = "Generalized Jaccard Similarity";
//    public static final String GRAPH_CONTAINMENT_SIMILARITY = "Graph Containment Similarity";
//    public static final String GRAPH_NORMALIZED_VALUE_SIMILARITY = "Graph Normalized Value Similarity";
//    public static final String GRAPH_VALUE_SIMILARITY = "Graph Value Similarity";
//    public static final String GRAPH_OVERALL_SIMILARITY = "Graph Overall Similarity";
//    public static final String JACCARD_SIMILARITY = "Jaccard Similarity";
//    public static final String SIGMA_SIMILARITY = "Sigma Similarity";
//    public static final String WEIGHTED_JACCARD_SIMILARITY = "Weighted Jaccard Similarity";

    // Entity Clustering methods
    public static final String CENTER_CLUSTERING = "Center Clustering";
    public static final String CONNECTED_COMPONENTS_CLUSTERING = "Connected Components Clustering";
    public static final String CUT_CLUSTERING = "Cut Clustering";
    public static final String MARKOV_CLUSTERING = "Markov Clustering";
    public static final String MERGE_CENTER_CLUSTERING = "Merge-Center Clustering";
    public static final String RICOCHET_SR_CLUSTERING = "Ricochet SR Clustering";
    public static final String UNIQUE_MAPPING_CLUSTERING = "Unique Mapping Clustering";

    // Automatic Configuration
    public static final String AUTOCONFIG_HOLISTIC = "Holistic";
    public static final String AUTOCONFIG_STEPBYSTEP = "Step-by-step";
    public static final String AUTOCONFIG_RANDOMSEARCH = "Random Search";
    public static final String AUTOCONFIG_GRIDSEARCH = "Grid Search";

    // JedAI workflow step names & descriptions
    public static final String STEP_LABEL_WELCOME = "Welcome";
    public static final String STEP_DESCRIPTION_WELCOME = "Welcome to JedAI, an open source, high scalability toolkit that offers out-of-the-box solutions for Entity Resolution over structured (relational) and semi-structured (RDF) data.";
    public static final String STEP_LABEL_WORKFLOW_SELECTION = "Workflow Selection";
    public static final String STEP_DESCRIPTION_WORKFLOW_SELECTION = "Select one of the three available workflows.";
    public static final String STEP_LABEL_DATA_READING = "Data Reading";
    public static final String STEP_DESCRIPTION_DATA_READING = "Data Reading transforms the input data into a list of entity profiles.";
    public static final String STEP_LABEL_SCHEMA_CLUSTERING = "Schema Clustering";
    public static final String STEP_DESCRIPTION_SCHEMA_CLUSTERING = "Schema Clustering groups together syntactically (not semantically) similar attributes. This can improve the performance of all workflow steps.";
    public static final String STEP_LABEL_BLOCK_BUILDING = "Block Building";
    public static final String STEP_DESCRIPTION_BLOCK_BUILDING = "Block Building clusters entities into overlapping blocks in a lazy manner that relies on unsupervised blocking keys: every token in an attribute value forms a key. Blocks are then extracted, based on its equality or on its similarity with other keys.";
    public static final String STEP_LABEL_BLOCK_CLEANING = "Block Cleaning";
    public static final String STEP_DESCRIPTION_BLOCK_CLEANING = "Block Cleaning aims to clean a set of overlapping blocks from unnecessary comparisons, which can be either redundant (i.e., repeated) or superfluous (i.e., between non-matching entities). Its methods operate on the coarse level of individual blocks or entities.";
    public static final String STEP_LABEL_COMPARISON_CLEANING = "Comparison Cleaning";
    public static final String STEP_DESCRIPTION_COMPARISON_CLEANING = "Similar to Block Cleaning, Comparison Cleaning aims to clean a set of blocks from both redundant and superfluous comparisons. Unlike Block Cleaning, its methods operate on the finer granularity of individual comparisons.";
    public static final String STEP_LABEL_ENTITY_MATCHING = "Entity Matching";
    public static final String STEP_DESCRIPTION_ENTITY_MATCHING = "Entity Matching compares pairs of entity profiles, associating every pair with a similarity in [0,1]. Its output comprises the similarity graph, i.e., an undirected, weighted graph where the nodes correspond to entities and the edges connect pairs of compared entities.";
    public static final String STEP_LABEL_ENTITY_CLUSTERING = "Entity Clustering";
    public static final String STEP_DESCRIPTION_ENTITY_CLUSTERING = "Entity Clustering takes as input the similarity graph produced by Entity Matching and partitions it into a set of equivalence clusters, with every cluster corresponding to a distinct real-world object.";
    public static final String STEP_LABEL_SELECTION_CONFIRMATION = "Selection Confirmation";
    public static final String STEP_DESCRIPTION_SELECTION_CONFIRMATION = "Confirm the selected values and press the \"Next\" button to go to the results page.";
    public static final String STEP_LABEL_WORKFLOW_EXECUTION = "Workflow Execution";
    public static final String STEP_DESCRIPTION_WORKFLOW_EXECUTION = "Press \"Run algorithm\" to run the algorithm. You can export the results to a CSV file with the \"Export CSV\" button.";
    public static final String STEP_LABEL_SIMILARITY_JOIN = "Similarity Join";
    public static final String STEP_DESCRIPTION_SIMILARITY_JOIN = "??";
    public static final String STEP_LABEL_PRIORITIZATION = "Prioritization";
    public static final String STEP_DESCRIPTION_PRIORITIZATION = "??";
}
