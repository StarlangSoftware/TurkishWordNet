package WordNet;

public class InterlingualRelation extends Relation {

    private InterlingualDependencyType dependencyType;

    public static final String[] ilrDependency = {"Hypernym", "Near_antonym", "Holo_member", "Holo_part", "Holo_portion",
            "Usage_domain", "Category_domain", "Be_in_state", "Subevent", "Verb_group",
            "Similar_to", "Also_see", "Causes", "SYNONYM"};

    public static final InterlingualDependencyType[] interlingualDependencyTags = {InterlingualDependencyType.HYPERNYM,
            InterlingualDependencyType.NEAR_ANTONYM, InterlingualDependencyType.HOLO_MEMBER, InterlingualDependencyType.HOLO_PART,
            InterlingualDependencyType.HOLO_PORTION, InterlingualDependencyType.USAGE_DOMAIN, InterlingualDependencyType.CATEGORY_DOMAIN,
            InterlingualDependencyType.BE_IN_STATE, InterlingualDependencyType.SUBEVENT, InterlingualDependencyType.VERB_GROUP,
            InterlingualDependencyType.SIMILAR_TO, InterlingualDependencyType.ALSO_SEE, InterlingualDependencyType.CAUSES,
            InterlingualDependencyType.SYNONYM};

    /**
     * Compares specified {@code String} tag with the tags in InterlingualDependencyType {@code Array}, ignoring case
     * considerations.
     *
     * @param tag String to compare
     * @return interlingual dependency type according to specified tag
     */
    public static InterlingualDependencyType getInterlingualDependencyTag(String tag) {
        for (int j = 0; j < ilrDependency.length; j++) {
            if (tag.equalsIgnoreCase(ilrDependency[j])) {
                return interlingualDependencyTags[j];
            }
        }
        return null;
    }

    /**
     * InterlingualRelation method sets its relation with the specified String name, then gets the InterlingualDependencyType
     * according to specified String dependencyType.
     *
     * @param name           relation name
     * @param dependencyType interlingual dependency type
     */
    public InterlingualRelation(String name, String dependencyType) {
        super(name);
        this.dependencyType = getInterlingualDependencyTag(dependencyType);
        if (this.dependencyType == null) {
            System.out.println("Interlingua dependency tag " + dependencyType + " does not exist\n");
        }
    }

    /**
     * Accessor method to get the private InterlingualDependencyType.
     *
     * @return interlingual dependency type
     */
    public InterlingualDependencyType getType() {
        return dependencyType;
    }

    /**
     * Method to retrieve interlingual dependency type as {@code String}.
     *
     * @return String interlingual dependency type
     */
    public String getTypeAsString() {
        return ilrDependency[dependencyType.ordinal()];
    }

    /**
     * toString method to print interlingual dependency type.
     *
     * @return String of relation name
     */
    public String toString() {
        return getTypeAsString() + "->" + name;
    }

}
