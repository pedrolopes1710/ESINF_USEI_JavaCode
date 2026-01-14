package usei12;

public class Edge implements Comparable<Edge> {
    public String u;
    public String v;
    public int weight;

    public Edge(String u, String v, int weight) {
        this.u = u;
        this.v = v;
        this.weight = weight;
    }

    @Override
    public int compareTo(Edge other) {
        return Integer.compare(this.weight, other.weight); 
    }
}
