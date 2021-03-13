package WordNet;

import Util.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class IdMapping {
    private HashMap<String, String> map;

    /**
     * Constructor to load ID mappings from specific file "mapping.txt" to a {@link HashMap}.
     */
    public IdMapping() {
        map = new HashMap<>();
        Scanner scanner = new Scanner(FileUtils.getInputStream("mapping.txt"));
        while (scanner.hasNext()) {
            String s = scanner.next();
            String[] mapInfo = s.split("->");
            map.put(mapInfo[0], mapInfo[1]);
        }
    }

    /**
     * Constructor to load ID mappings from given file to a {@link HashMap}.
     *
     * @param fileName String file name input that will be read
     */
    public IdMapping(String fileName) {
        map = new HashMap<>();
        Scanner scanner = new Scanner(FileUtils.getInputStream(fileName));
        while (scanner.hasNext()) {
            String s = scanner.next();
            String[] mapInfo = s.split("->");
            map.put(mapInfo[0], mapInfo[1]);
        }
    }

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     *
     * @return a set view of the keys contained in this map
     */
    public Set<String> keySet() {
        return map.keySet();
    }


    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * @param id String id of a key
     * @return value of the specified key
     */
    public String map(String id) {
        if (map.get(id) == null) {
            return null;
        }
        String mappedId = map.get(id);
        while (map.get(mappedId) != null) {
            mappedId = map.get(mappedId);
        }
        return mappedId;
    }


    /**
     * Returns the value to which the specified key is mapped.
     *
     * @param id String id of a key
     * @return value of the specified key
     */
    public String singleMap(String id) {
        return map.get(id);
    }

    /**
     * Associates the specified value with the specified key in this map.
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     */
    public void add(String key, String value) {
        map.put(key, value);
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     *
     * @param key key whose mapping is to be removed from the map
     */
    public void remove(String key) {
        map.remove(key);
    }

    /**
     * Saves map to the specified file.
     *
     * @param fileName String file to write map
     */
    public void save(String fileName) {
        try {
            PrintWriter writer = new PrintWriter(new File(fileName));
            for (String key : map.keySet()) {
                writer.println(key + "->" + map.get(key));
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
