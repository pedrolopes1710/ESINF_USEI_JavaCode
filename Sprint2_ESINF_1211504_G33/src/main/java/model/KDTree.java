package model;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * KDTree 2D (latitude, longitude) com bulk build usando selecção linear (median-of-medians).
 * Mantém buckets por coordenada exacta e ordena buckets por StationRecord.getName().
 */
public class KDTree {

    //Representa um ponto único (lat, lon) e o bucket correspondente.
    private static class Point {
        double lat;
        double lon;
        List<StationRecord> bucket;

        Point(double lat, double lon, List<StationRecord> b) {
            this.lat = lat;
            this.lon = lon;
            this.bucket = b;
        }
    }

    public static class KDNode {
        public final double lat;
        public final double lon;
        public final List<StationRecord> bucket; //Bucket ordenado por nome asc
        public KDNode left;
        public KDNode right;
        public final int axis; //0 = latitude, 1 = longitude

        public KDNode(double lat, double lon, List<StationRecord> bucket, int axis) {
            this.lat = lat;
            this.lon = lon;
            this.bucket = bucket;
            this.axis = axis;
            this.left = null;
            this.right = null;
        }

        public Point2D.Double point() {
            return new Point2D.Double(lat, lon);
        }
    }

    private KDNode root;
    private int nodeCount; //Número de nós
    private int totalStations; //Soma dos buckets (número de estações)

    private KDTree() {
        this.root = null;
        this.nodeCount = 0;
        this.totalStations = 0;
    }

    public static KDTree buildFromStations(List<StationRecord> stations) {
        KDTree tree = new KDTree();

        Map<String, List<StationRecord>> buckets = new HashMap<>();
        Map<String, double[]> coords = new HashMap<>();

        for (StationRecord s : stations) {
            double lat = s.getLatitude();
            double lon = s.getLongitude();

            String key = lat + "|" + lon;

            buckets.computeIfAbsent(key, k -> new ArrayList<>()).add(s);

            coords.putIfAbsent(key, new double[]{lat, lon});
        }

        List<Point> points = new ArrayList<>(buckets.size());

        for (Map.Entry<String, List<StationRecord>> e : buckets.entrySet()) {
            double[] c = coords.get(e.getKey());
            List<StationRecord> bucket = e.getValue();

            //Ordenar bucket por station name ascendente
            bucket.sort(Comparator.comparing(StationRecord::getName, Comparator.nullsFirst(String::compareTo)));

            points.add(new Point(c[0], c[1], bucket));
        }

        tree.nodeCount = points.size();
        int tot = 0;

        for (Point p : points){
            tot += p.bucket.size();
        }

        tree.totalStations = tot;

        tree.root = tree.buildRecursive(points, 0);

        return tree;
    }


    private KDNode buildRecursive(List<Point> pts, int axis) {
        int n = pts.size();

        if (n == 0) return null;

        int left = 0;
        int right = n - 1;
        int mid = left + (n / 2);

        //Seleccionar mediana na posição mid (0-based) usando selecção linear
        linearSelect(pts, left, right, mid - left, axis);

        Point m = pts.get(mid);
        KDNode node = new KDNode(m.lat, m.lon, m.bucket, axis);

        //Criar sublistas esquerda e direita
        List<Point> leftList = new ArrayList<>();
        for (int i = 0; i < mid; i++){
            leftList.add(pts.get(i));
        }

        List<Point> rightList = new ArrayList<>();

        for (int i = mid + 1; i < n; i++){
            rightList.add(pts.get(i));
        }

        node.left = buildRecursive(leftList, 1 - axis);
        node.right = buildRecursive(rightList, 1 - axis);

        return node;
    }

    private void linearSelect(List<Point> pts, int left, int right, int k, int axis) {

        while (true) {
            int n = right - left + 1;

            if (n <= 5) {
                pts.subList(left, right + 1).sort((a, b) -> Double.compare(coord(a, axis), coord(b, axis)));

                return;
            }

            int numMedians = 0;

            for (int i = left; i <= right; i += 5) {
                int subRight = Math.min(i + 4, right);

                pts.subList(i, subRight + 1).sort((a, b) -> Double.compare(coord(a, axis), coord(b, axis)));

                int medianIndex = i + (subRight - i) / 2;

                Collections.swap(pts, left + numMedians, medianIndex);
                numMedians++;
            }

            //Seleccionar mediana das medianas recursivamente
            linearSelect(pts, left, left + numMedians - 1, (numMedians - 1) / 2, axis);

            double pivotVal = coord(pts.get(left + (numMedians - 1) / 2), axis);

            int pivotFinal = partition(pts, left, right, pivotVal, axis);
            int leftSize = pivotFinal - left;

            if (k == leftSize) {
                return;
            } else if (k < leftSize) {
                right = pivotFinal - 1;
            } else {
                k = k - (leftSize + 1);
                left = pivotFinal + 1;
            }
        }
    }

    private int partition(List<Point> pts, int left, int right, double pivotVal, int axis) {

        int pivotIndex = left;
        boolean found = false;

        for (int i = left; i <= right; i++) {

            if (Double.compare(coord(pts.get(i), axis), pivotVal) == 0) {
                pivotIndex = i;
                found = true;
                break;
            }
        }

        if (!found){
            pivotIndex = right;
        }

        Collections.swap(pts, pivotIndex, right);

        int store = left;

        for (int i = left; i < right; i++) {
            if (Double.compare(coord(pts.get(i), axis), pivotVal) < 0) {
                Collections.swap(pts, store, i);
                store++;
            }
        }

        Collections.swap(pts, store, right);

        return store;
    }

    private double coord(Point p, int axis) {
        return (axis == 0) ? p.lat : p.lon;
    }

    public int size() {
        return totalStations;
    }

    public int nodeCount() {
        return nodeCount;
    }

    public int height() {
        return nodeHeight(root);
    }

    private int nodeHeight(KDNode node) {

        if (node == null) return -1;

        return Math.max(nodeHeight(node.left), nodeHeight(node.right)) + 1;
    }

    public Map<Integer, Integer> bucketHistogram() {

        Map<Integer, Integer> hist = new HashMap<>();
        bucketHistRec(root, hist);

        return hist;
    }

    private void bucketHistRec(KDNode node, Map<Integer,Integer> hist) {

        if (node == null) return;

        int b = node.bucket.size();

        hist.put(b, hist.getOrDefault(b, 0) + 1);

        bucketHistRec(node.left, hist);
        bucketHistRec(node.right, hist);
    }

    public void printStats() {
        System.out.println("KDTree: nodeCount=" + nodeCount() + ", totalStations=" + size() + ", height=" + height());
        System.out.println("Bucket histogram: " + bucketHistogram());
    }

    //Retorna todas as estações
    public List<StationRecord> rangeQuery(double latMin, double latMax, double lonMin, double lonMax) {

        List<StationRecord> out = new ArrayList<>();
        rangeRec(root, latMin, latMax, lonMin, lonMax, out);

        return out;
    }

    private void rangeRec(KDNode node, double latMin, double latMax, double lonMin, double lonMax, List<StationRecord> acc) {

        if (node == null) return;

        double lat = node.lat;
        double lon = node.lon;

        if (lat >= latMin && lat <= latMax && lon >= lonMin && lon <= lonMax) acc.addAll(node.bucket);

        if (node.axis == 0) {
            if (latMin <= lat) rangeRec(node.left, latMin, latMax, lonMin, lonMax, acc);
            if (latMax >= lat) rangeRec(node.right, latMin, latMax, lonMin, lonMax, acc);
        } else {
            if (lonMin <= lon) rangeRec(node.left, latMin, latMax, lonMin, lonMax, acc);
            if (lonMax >= lon) rangeRec(node.right, latMin, latMax, lonMin, lonMax, acc);
        }
    }

    //k-Nearest Neighbours (kNN) — Devolve lista ordenada por distância
    public List<StationRecord> kNearest(double qLat, double qLon, int k) {

        if (k <= 0) return new ArrayList<>();

        PriorityQueue<Map.Entry<Double, StationRecord>> heap = new PriorityQueue<>(
                Comparator.<Map.Entry<Double, StationRecord>>comparingDouble(Map.Entry::getKey).reversed()
        );

        kNNRec(root, qLat, qLon, k, heap);

        List<Map.Entry<Double, StationRecord>> list = new ArrayList<>(heap);
        list.sort(Comparator.comparingDouble(Map.Entry::getKey));
        List<StationRecord> out = new ArrayList<>();

        for (Map.Entry<Double, StationRecord> e : list){
            out.add(e.getValue());
        }

        return out;
    }

    private void kNNRec(KDNode node, double qLat, double qLon, int k, PriorityQueue<Map.Entry<Double, StationRecord>> heap) {

        if (node == null) return;

        for (StationRecord s : node.bucket) {
            double d = dist2(qLat, qLon, s.getLatitude(), s.getLongitude());
            addToHeap(heap, d, s, k);
        }

        double delta = (node.axis == 0) ? qLat - node.lat : qLon - node.lon;
        KDNode first = delta < 0 ? node.left : node.right;
        KDNode second = delta < 0 ? node.right : node.left;

        kNNRec(first, qLat, qLon, k, heap);

        double best = heap.isEmpty() ? Double.POSITIVE_INFINITY : heap.peek().getKey();
        double delta2 = delta * delta;

        if (heap.size() < k || delta2 <= best) {
            kNNRec(second, qLat, qLon, k, heap);
        }
    }

    private void addToHeap(PriorityQueue<Map.Entry<Double, StationRecord>> heap, double dist, StationRecord s, int k) {

        Map.Entry<Double, StationRecord> entry = new AbstractMap.SimpleEntry<>(dist, s);

        if (heap.size() < k){
            heap.add(entry);
        } else {
            Map.Entry<Double, StationRecord> top = heap.peek();

            if (top.getKey() > dist) {
                heap.poll();
                heap.add(entry);
            }
        }   
    }

    private double dist2(double aLat, double aLon, double bLat, double bLon) {
        double dx = aLat - bLat;
        double dy = aLon - bLon;

        return dx*dx + dy*dy;
    }
}
