package WordNet;

public class SemanticRelation extends Relation{

    private SemanticRelationType relationType;
    private int toIndex = 0;

    public static final String[] semanticDependency = {"ANTONYM", "HYPERNYM",
            "INSTANCE_HYPERNYM", "HYPONYM", "INSTANCE_HYPONYM", "MEMBER_HOLONYM", "SUBSTANCE_HOLONYM",
            "PART_HOLONYM", "MEMBER_MERONYM", "SUBSTANCE_MERONYM", "PART_MERONYM", "ATTRIBUTE",
            "DERIVATION_RELATED", "DOMAIN_TOPIC", "MEMBER_TOPIC", "DOMAIN_REGION", "MEMBER_REGION",
            "DOMAIN_USAGE", "MEMBER_USAGE", "ENTAILMENT", "CAUSE", "ALSO_SEE",
            "VERB_GROUP", "SIMILAR_TO", "PARTICIPLE_OF_VERB"};
    public static final SemanticRelationType[] semanticDependencyTags = {SemanticRelationType.ANTONYM, SemanticRelationType.HYPERNYM,
            SemanticRelationType.INSTANCE_HYPERNYM, SemanticRelationType.HYPONYM, SemanticRelationType.INSTANCE_HYPONYM, SemanticRelationType.MEMBER_HOLONYM, SemanticRelationType.SUBSTANCE_HOLONYM,
            SemanticRelationType.PART_HOLONYM, SemanticRelationType.MEMBER_MERONYM, SemanticRelationType.SUBSTANCE_MERONYM, SemanticRelationType.PART_MERONYM, SemanticRelationType.ATTRIBUTE,
            SemanticRelationType.DERIVATION_RELATED, SemanticRelationType.DOMAIN_TOPIC, SemanticRelationType.MEMBER_TOPIC, SemanticRelationType.DOMAIN_REGION, SemanticRelationType.MEMBER_REGION,
            SemanticRelationType.DOMAIN_USAGE, SemanticRelationType.MEMBER_USAGE, SemanticRelationType.ENTAILMENT, SemanticRelationType.CAUSE, SemanticRelationType.ALSO_SEE,
            SemanticRelationType.VERB_GROUP, SemanticRelationType.SIMILAR_TO, SemanticRelationType.PARTICIPLE_OF_VERB};

    public static SemanticRelationType getSemanticTag(String tag){
        for (int j = 0; j < semanticDependencyTags.length; j++) {
            if (tag.equalsIgnoreCase(semanticDependency[j])) {
                return semanticDependencyTags[j];
            }
        }
        return null;
    }

    public static SemanticRelationType reverse(SemanticRelationType semanticRelationType){
        switch (semanticRelationType){
            case HYPERNYM:
                return SemanticRelationType.HYPONYM;
            case HYPONYM:
                return SemanticRelationType.HYPERNYM;
            case ANTONYM:
                return SemanticRelationType.ANTONYM;
            case INSTANCE_HYPERNYM:
                return SemanticRelationType.INSTANCE_HYPONYM;
            case INSTANCE_HYPONYM:
                return SemanticRelationType.INSTANCE_HYPERNYM;
            case MEMBER_HOLONYM:
                return SemanticRelationType.MEMBER_MERONYM;
            case MEMBER_MERONYM:
                return SemanticRelationType.MEMBER_HOLONYM;
            case PART_MERONYM:
                return SemanticRelationType.PART_HOLONYM;
            case PART_HOLONYM:
                return SemanticRelationType.PART_MERONYM;
            case SUBSTANCE_MERONYM:
                return SemanticRelationType.SUBSTANCE_HOLONYM;
            case SUBSTANCE_HOLONYM:
                return SemanticRelationType.SUBSTANCE_MERONYM;
            case DOMAIN_TOPIC:
                return SemanticRelationType.MEMBER_TOPIC;
            case MEMBER_TOPIC:
                return SemanticRelationType.DOMAIN_TOPIC;
            case DOMAIN_REGION:
                return SemanticRelationType.MEMBER_REGION;
            case MEMBER_REGION:
                return SemanticRelationType.DOMAIN_REGION;
            case DOMAIN_USAGE:
                return SemanticRelationType.MEMBER_USAGE;
            case MEMBER_USAGE:
                return SemanticRelationType.DOMAIN_USAGE;
        }
        return null;
    }

    public SemanticRelation(String name, String relationType){
        super(name);
        this.relationType = getSemanticTag(relationType);
        if (this.relationType == null){
            System.out.println("Semantic relation tag " + relationType + " does not exist\n");
        }
    }

    public SemanticRelation(String name, String relationType, int toIndex){
        super(name);
        this.relationType = getSemanticTag(relationType);
        this.toIndex = toIndex;
        if (this.relationType == null){
            System.out.println("Semantic relation tag " + relationType + " does not exist\n");
        }
    }

    public SemanticRelation(String name, SemanticRelationType relationType){
        super(name);
        this.relationType = relationType;
    }

    public SemanticRelation(String name, SemanticRelationType relationType, int toIndex){
        super(name);
        this.relationType = relationType;
        this.toIndex = toIndex;
    }

    public boolean equals(Object second){
        if (!(second instanceof SemanticRelation)){
            return false;
        }
        SemanticRelation relation = (SemanticRelation) second;
        return name.equals(relation.name) && relationType.equals(relation.relationType) && toIndex == relation.toIndex;
    }

    public int toIndex(){
        return toIndex;
    }

    public SemanticRelationType getRelationType(){
        return relationType;
    }

    public void setRelationType(SemanticRelationType relationType){
        this.relationType = relationType;
    }

    public String getTypeAsString(){
        if (relationType != null){
            return semanticDependency[relationType.ordinal()];
        } else {
            return null;
        }
    }

    public String toString(){
        return getTypeAsString() + "->" + name;
    }

}
