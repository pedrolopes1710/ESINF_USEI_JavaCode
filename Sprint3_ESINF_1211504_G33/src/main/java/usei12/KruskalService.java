package usei12;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class KruskalService {

    public List<Edge> minimumSpanningTree(List<Edge> edges) {
        Set<String> vertices = new HashSet<>();
        for (Edge e : edges) {
            vertices.add(e.u);
            vertices.add(e.v);
        }

        UnionFind uf = new UnionFind();
        vertices.forEach(uf::makeSet);

        List<Edge> sortedEdges = new ArrayList<>(edges);
        Collections.sort(sortedEdges);

        List<Edge> mst = new ArrayList<>();

        for (Edge e : sortedEdges) {
            if (!uf.find(e.u).equals(uf.find(e.v))) {
                mst.add(e);
                uf.union(e.u, e.v);
            }
        }
        return mst;
    }

    public int totalCost(List<Edge> mst) {
        return mst.stream().mapToInt(e -> e.weight).sum();
    }
    public void generateFullReport(List<Edge> mst, Map<String, double[]> coords, String baseName) throws IOException {
        String dotPath = baseName + ".dot";
        String svgPath = baseName + ".svg";

        // Escrever o ficheiro DOT
        try (PrintWriter writer = new PrintWriter(new FileWriter(dotPath))) {
            writer.println("graph G {");
            writer.println("    layout=neato;");
            writer.println("    node [shape=circle, style=filled, fillcolor=lightblue];");
            
            // Escrever coordenadas
            for (String node : coords.keySet()) {
                double[] p = coords.get(node);
                writer.printf(Locale.US, "    \"%s\" [pos=\"%f,%f!\"];%n", node, p[0], p[1]);
            }

            // Escrever arestas da MST
            for (Edge e : mst) {
                writer.printf(Locale.US, "    \"%s\" -- \"%s\" [label=\"%d\"];%n", e.u, e.v, e.weight);
            }
            writer.println("}");
        }

        // Tentar converter para SVG usando o comando do sistema
        try {
            new ProcessBuilder("neato", "-Tsvg", dotPath, "-o", svgPath).start().waitFor();
            System.out.println("SVG gerado: " + svgPath);
        } catch (Exception e) {
            System.out.println("Ficheiro DOT criado, mas falhou a conversão para SVG (Graphviz não instalado?).");
        }
    }
}
