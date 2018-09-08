package WordNet;

public class InterlingualRelation extends Relation{

    private InterlingualDependencyType dependencyType;

    public static final String[] ilrDependency = {"Hypernym", "Near_antonym", "Holo_member", "Holo_part", "Holo_portion",
            "Usage_domain", "Category_domain", "Be_in_state", "Subevent", "Verb_group",
            "Similar_to", "Also_see", "Causes", "SYNONYM"};
    public static final InterlingualDependencyType[] interlinguaDependencyTags = {InterlingualDependencyType.HYPERNYM, InterlingualDependencyType.NEAR_ANTONYM, InterlingualDependencyType.HOLO_MEMBER, InterlingualDependencyType.HOLO_PART, InterlingualDependencyType.HOLO_PORTION,
    InterlingualDependencyType.USAGE_DOMAIN, InterlingualDependencyType.CATEGORY_DOMAIN, InterlingualDependencyType.BE_IN_STATE, InterlingualDependencyType.SUBEVENT, InterlingualDependencyType.VERB_GROUP,
    InterlingualDependencyType.SIMILAR_TO, InterlingualDependencyType.ALSO_SEE, InterlingualDependencyType.CAUSES, InterlingualDependencyType.SYNONYM};

    public static InterlingualDependencyType getInterlinguaDependencyTag(String tag){
        for (int j = 0; j < ilrDependency.length; j++) {
            if (tag.equalsIgnoreCase(ilrDependency[j])) {
                return interlinguaDependencyTags[j];
            }
        }
        return null;
    }

    public InterlingualRelation(String name, String dependencyType){
        super(name);
        this.dependencyType = getInterlinguaDependencyTag(dependencyType);
        if (this.dependencyType == null){
            System.out.println("Interlingua dependency tag " + dependencyType + " does not exist\n");
        }
    }

    public InterlingualDependencyType getType(){
        return dependencyType;
    }

    public String getTypeAsString(){
        return ilrDependency[dependencyType.ordinal()];
    }

    public String toString(){
        return getTypeAsString() + "->" + name;
    }

}
