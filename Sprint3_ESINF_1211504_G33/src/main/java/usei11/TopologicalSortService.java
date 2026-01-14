package usei11;
import java.util.*;


public class TopologicalSortService {

    private enum State { WHITE, GRAY, BLACK }
    private final Map<String, State> state = new HashMap<>();
    private final Deque<String> stack = new ArrayDeque<>();

    public List<String> sort(DirectedGraph graph) {
        for (String v : graph.getVertices())
            state.put(v, State.WHITE);

        for (String v : graph.getVertices())
            if (state.get(v) == State.WHITE && !dfs(v, graph))
                throw new IllegalStateException("Cycle detected");

        return new ArrayList<>(stack);
    }

    private boolean dfs(String v, DirectedGraph g) {
        state.put(v, State.GRAY);

        for (String adj : g.getAdj(v)) {
            if (state.get(adj) == State.GRAY) return false;
            if (state.get(adj) == State.WHITE && !dfs(adj, g)) return false;
        }

        state.put(v, State.BLACK);
        stack.push(v);
        return true;
    }
}
