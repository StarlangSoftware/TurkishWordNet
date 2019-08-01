package WordNet;

public class Relation {

    protected String name;

    /**
     * A constructor that sets the name of the relation.
     *
     * @param name String relation name
     */
    public Relation(String name) {
        this.name = name;
    }

    /**
     * Accessor method for the relation name.
     *
     * @return String relation name
     */
    public String getName() {
        return name;
    }

    /**
     * Mutator for the relation name.
     *
     * @param name String relation name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * An overridden equals method to compare two {@code Object}s.
     *
     * @param second the reference object with which to compare
     * @return {@code true} if this object is the same as the obj
     * argument; {@code false} otherwise.
     */
    public boolean equals(Object second) {
        if (!(second instanceof Relation)) {
            return false;
        }
        Relation relation = (Relation) second;
        return name.equals(relation.name);
    }
}