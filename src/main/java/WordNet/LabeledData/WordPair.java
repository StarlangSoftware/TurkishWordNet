package WordNet.LabeledData;

public class WordPair {
    protected String leftWord = null;
    protected String rightWord = null;

    public WordPair(){
    }

    public WordPair(String leftWord, String rightWord){
        this.leftWord = leftWord;
        this.rightWord = rightWord;
    }

    public boolean equals(Object secondObject){
        if (!(secondObject instanceof WordPair)){
            return false;
        }
        WordPair second = (WordPair) secondObject;
        String result1, result2;
        if (leftWord == null){
            return second.leftWord == null;
        }
        result1 = leftWord + rightWord;
        result2 = second.leftWord + second.rightWord;
        return result1.equals(result2);
    }

    public int hashCode(){
        if (leftWord == null){
            return 0;
        }
        String result = leftWord + rightWord;
        return result.hashCode();
    }

}
