package usei11;
import java.util.*;

public class DirectedGraph {
    private final Map<String, List<String>> adjList = new HashMap<>();

    public void addVertex(String v) {
        adjList.putIfAbsent(v, new ArrayList<>());
    }

    public void addEdge(String from, String to) {
        addVertex(from);
        addVertex(to);
        adjList.get(from).add(to);
    }

    public Set<String> getVertices() {
        return adjList.keySet();
    }

    public List<String> getAdj(String v) {
        return adjList.getOrDefault(v, Collections.emptyList());
    }
}
