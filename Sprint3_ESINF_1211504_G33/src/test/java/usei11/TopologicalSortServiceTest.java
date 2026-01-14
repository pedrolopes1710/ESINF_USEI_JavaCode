package usei11;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class TopologicalSortServiceTest {

    @Test
    void validSort() {
        DirectedGraph g = new DirectedGraph();
        g.addEdge("A", "B");
        g.addEdge("B", "C");

        List<String> result = new TopologicalSortService().sort(g);
        assertEquals(List.of("A", "B", "C"), result);
    }

    @Test
    void detectsCycle() {
        DirectedGraph g = new DirectedGraph();
        g.addEdge("A", "B");
        g.addEdge("B", "A");

        assertThrows(IllegalStateException.class,
                () -> new TopologicalSortService().sort(g));
    }
}
