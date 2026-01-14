package usei12;

import java.util.HashMap;
import java.util.Map;

public class UnionFind {

    private final Map<String, String> parent = new HashMap<>();

    public void makeSet(String x) {
        parent.put(x, x);
    }

    public String find(String x) {
        if (!parent.get(x).equals(x)) {
            parent.put(x, find(parent.get(x))); 
        }
        return parent.get(x);
    }

    public void union(String a, String b) {
        String rootA = find(a);
        String rootB = find(b);

        if (!rootA.equals(rootB)) {
            parent.put(rootA, rootB);
        }
    }
}
