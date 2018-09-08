package WordNet;

public class Relation {

    protected String name;

    public Relation(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public boolean equals(Object second){
        if (!(second instanceof Relation)){
            return false;
        }
        Relation relation = (Relation) second;
        return name.equals(relation.name);
    }
}
