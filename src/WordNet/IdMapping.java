package WordNet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class IdMapping {
    private HashMap<String, String> map;

    public IdMapping(String fileName){
        map = new HashMap<>();
        try {
            Scanner scanner = new Scanner(new File(fileName));
            while (scanner.hasNext()){
                String s = scanner.next();
                String[] mapInfo = s.split("->");
                map.put(mapInfo[0], mapInfo[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Set<String> keySet(){
        return map.keySet();
    }

    public String map(String id){
        if (map.get(id) == null){
            return null;
        }
        String mappedId = map.get(id);
        while (map.get(mappedId) != null){
            mappedId = map.get(mappedId);
        }
        return mappedId;
    }

    public String singleMap(String id){
        return map.get(id);
    }

    public void add(String key, String value){
        map.put(key, value);
    }

    public void remove(String key){
        map.remove(key);
    }

    public void save(String fileName){
        try {
            PrintWriter writer = new PrintWriter(new File(fileName));
            for (String key : map.keySet()){
                writer.println(key + "->" + map.get(key));
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
