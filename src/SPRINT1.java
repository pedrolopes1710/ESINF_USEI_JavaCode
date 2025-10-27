import java.io.*;
import java.util.*;
import java.util.*;

public class SPRINT1 {
    static class Box {
        String boxId, sku, expiryDate;
        int qty;
        Box(String boxId, String sku, int qty, String expiryDate) {
            this.boxId = boxId; this.sku = sku; this.qty = qty; this.expiryDate = expiryDate;
        }
    }

    static class Bay {
        String warehouseId, aisle;
        int bay, capacityBoxes;
        List<Box> boxes = new ArrayList<>();
        Bay(String warehouseId, String aisle, int bay, int capacityBoxes) {
            this.warehouseId = warehouseId; this.aisle = aisle;
            this.bay = bay; this.capacityBoxes = capacityBoxes;
        }
    }

    static Map<String, Map<String, String>> items = new HashMap<>();
    static List<Bay> bays = new ArrayList<>();
    static List<Box> boxes = new ArrayList<>();
    static Map<String, String> orders = new HashMap<>();
    static List<Map<String, String>> orderLines = new ArrayList<>();

    public static void main(String[] args) {
        loadItems("train_station_dataset/items.csv");
        loadBays("train_station_dataset/bays.csv");
        loadBoxesFromWagons("train_station_dataset/wagons.csv");

        Scanner sc = new Scanner(System.in);
        int op;
        do {
            System.out.println("\n=== MENU USEI01 / USEI02 ===");
            System.out.println("1 - Listar Bays");
            System.out.println("2 - Mostrar Boxes por SKU");
            System.out.println("3 - Relocalizar Box");
            System.out.println("4 - Despachar SKU (manual)");
            System.out.println("5 - Mostrar Todas as Boxes");
            System.out.println("6 - Carregar Orders");
            System.out.println("7 - Processar Orders (USE02)");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");
            op = Integer.parseInt(sc.nextLine());

            switch (op) {
                case 1:
                    listarBays();
                    break;
                case 2:
                    mostrarBoxesSKU(sc);
                    break;
                case 3:
                    relocalizarBox(sc);
                    break;
                case 4:
                    despacharManual(sc);
                    break;
                case 5:
                    mostrarTodasBoxes();
                    break;
                case 6:
                    loadOrders("train_station_dataset/orders.csv");
                    loadOrderLines("train_station_dataset/order_lines.csv");
                    break;
                case 7:
                    processOrders();
                    break;
            }
        } while (op != 0);
    }

    // === LOAD METHODS ===
    static void loadItems(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length < 2) continue;
                Map<String, String> item = new HashMap<>();
                item.put("name", p[1]);
                items.put(p[0].trim(), item);
            }
            System.out.println("Items carregados: " + items.size());
        } catch (Exception e) {
            System.out.println("Erro ao carregar items: " + e.getMessage());
        }
    }

    static void loadBays(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(";");
                if (p.length < 4) continue;
                bays.add(new Bay(p[0], p[1], Integer.parseInt(p[2]), Integer.parseInt(p[3])));
            }
            System.out.println("Bays carregadas: " + bays.size());
        } catch (Exception e) {
            System.out.println("Erro ao carregar bays: " + e.getMessage());
        }
    }

    static void loadBoxesFromWagons(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine();
            String line;
            Random rand = new Random();
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length < 6) continue;
                String boxId = p[1].trim();
                String sku = p[2].trim();
                int qty = Integer.parseInt(p[3].trim());
                String expiry = p[4].trim().isEmpty() ? "9999-12-31" : p[4].trim();

                if (!items.containsKey(sku)) continue;
                Box box = new Box(boxId, sku, qty, expiry);
                boxes.add(box);
                if (!bays.isEmpty()) bays.get(rand.nextInt(bays.size())).boxes.add(box);
            }
            System.out.println("Boxes carregadas: " + boxes.size());
        } catch (Exception e) {
            System.out.println("Erro ao carregar boxes: " + e.getMessage());
        }
    }

    static void loadOrders(String path) {
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
        String header = br.readLine(); // lê o cabeçalho
        if (header == null) {
            System.out.println("Ficheiro orders.csv vazio!");
            return;
        }

        String line;
        while ((line = br.readLine()) != null) {
            String[] p = line.split(",");
            if (p.length < 3) continue;

            String orderId = p[0].trim();
            String dueDate = p[1].trim();
            String priority = p[2].trim();

            // guardamos tudo no mesmo Map
            orders.put(orderId, dueDate + ";" + priority);
        }
        System.out.println("Orders carregadas: " + orders.size());
        } catch (Exception e) {
            System.out.println("Erro ao carregar orders: " + e.getMessage());
        }
    }


    static void loadOrderLines(String path) {
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
        String header = br.readLine(); // lê o cabeçalho
        if (header == null) {
            System.out.println("Ficheiro order_lines.csv vazio!");
            return;
        }

        String line;
        while ((line = br.readLine()) != null) {
            String[] p = line.split(",");
            if (p.length < 4) continue;

            Map<String, String> ol = new HashMap<>();
            ol.put("orderId", p[0].trim());
            ol.put("sku", p[2].trim());
            ol.put("qty", p[3].trim());
            orderLines.add(ol);
        }
        System.out.println("OrderLines carregadas: " + orderLines.size());
        } catch (Exception e) {
            System.out.println("Erro ao carregar order lines: " + e.getMessage());
        }
    }


    // === MENU FUNCTIONS ===
    static void listarBays() {
        for (Bay b : bays)
            System.out.println("Bay " + b.bay + " -> " + b.boxes.size() + " boxes");
    }

    static void mostrarBoxesSKU(Scanner sc) {
        System.out.print("SKU: ");
        String sku = sc.nextLine();
        boxes.stream().filter(b -> b.sku.equals(sku))
                .sorted(Comparator.comparing(b -> b.expiryDate))
                .forEach(b -> System.out.println(b.boxId + " qty:" + b.qty + " exp:" + b.expiryDate));
    }

    static void relocalizarBox(Scanner sc) {
        System.out.print("Box ID: ");
        String id = sc.nextLine();
        Box bx = boxes.stream().filter(b -> b.boxId.equals(id)).findFirst().orElse(null);
        if (bx == null) return;
        Bay oldBay = bays.stream().filter(b -> b.boxes.contains(bx)).findFirst().orElse(null);
        Bay newBay = bays.get(new Random().nextInt(bays.size()));
        if (oldBay != null) oldBay.boxes.remove(bx);
        newBay.boxes.add(bx);
        System.out.println("Box " + id + " movida para bay " + newBay.bay);
    }

    static void despacharManual(Scanner sc) {
        System.out.print("SKU: ");
        String sku = sc.nextLine();
        System.out.print("Quantidade: ");
        int q = Integer.parseInt(sc.nextLine());
        despacharSKU(sku, q);
    }

    static void mostrarTodasBoxes() {
        for (Box b : boxes)
            System.out.println(b.boxId + " " + b.sku + " qty:" + b.qty + " exp:" + b.expiryDate);
    }

    static void despacharSKU(String sku, int qty) {
        List<Box> skuBoxes = boxes.stream()
                .filter(b -> b.sku.equals(sku))
                .sorted(Comparator.comparing(b -> b.expiryDate))
                .toList();

        for (Box b : skuBoxes) {
            if (qty <= 0) break;
            int used = Math.min(qty, b.qty);
            b.qty -= used;
            qty -= used;
            System.out.println("Usada box " + b.boxId + " -> " + used + " unidades");
        }
        if (qty > 0) System.out.println("Faltam " + qty + " unidades do SKU " + sku);
    }

    // === USE02 MAIN METHOD ===
    static void processOrders() {
        for (Map<String, String> ol : orderLines) {
            String orderId = ol.get("orderId");
            String sku = ol.get("sku");
            int qty = Integer.parseInt(ol.get("qty"));
            System.out.println("\n[ORDER " + orderId + "] SKU " + sku + " x" + qty);
            despacharSKU(sku, qty);
        }
    }
}
