# 🧾 ESINF — Sprint 1  
## USE01 + USE02 — Gestão de Armazém e Processamento de Encomendas  

---

## 🧩 Objetivo Geral

Estas duas User Stories (USE01 e USE02) têm como objetivo:
- **USE01**: carregar e gerir os dados do armazém (itens, bays e boxes), permitindo listar, relocalizar e despachar produtos segundo o critério **FEFO** (First Expired, First Out);
- **USE02**: carregar e processar encomendas, associando cada SKU aos pedidos e retirando quantidades do stock existente.

A implementação foi feita em **Java**, de forma **simplificada e direta**, sem dependências externas, priorizando legibilidade e funcionalidade.

---

## ⚙️ Funcionalidades Implementadas

| Nº | Funcionalidade | Descrição |
|----|----------------|-----------|
| 1 | **Leitura de dados base** | Carrega ficheiros CSV: `items.csv`, `bays.csv`, `wagons.csv`. |
| 2 | **Gestão de boxes** | Lista boxes por SKU, mostra todas, e permite relocalizar boxes entre bays. |
| 3 | **Despacho manual** | Permite remover quantidades de um SKU do stock, aplicando FEFO. |
| 4 | **Leitura de encomendas (USE02)** | Lê `orders.csv` e `order_lines.csv`, incluindo prioridade e data de entrega. |
| 5 | **Processamento automático de encomendas (USE02)** | Para cada order line, despacha o SKU necessário automaticamente, respeitando FEFO. |

---

## 🧱 Estrutura dos Ficheiros CSV

### `items.csv`
```csv
sku,name,category,unit,volume,unitWeight
SKU0001,Item_01,Cleaning,pack,1.9,9.38
SKU0002,Item_02,Electronics,pack,8.99,8.71
...
```

### `bays.csv`
```csv
warehouseId;aisle;bay;capacityBoxes
W1;1;1;12
W1;1;2;10
W1;1;3;12
...
```

### `wagons.csv`
```csv
wagonId,boxId,sku,qty,expiryDate,receivedAt
WGN001,BOX00001,SKU0022,7,,2025-07-05T15:42:00
WGN001,BOX00002,SKU0008,26,2026-01-05,2025-07-21T22:02:00
...
```

### `orders.csv`
```csv
orderId,dueDate,priority
ORD00001,2025-09-29T09:00:00,2
ORD00002,2025-10-03T14:00:00,3
...
```

### `order_lines.csv`
```csv
orderId,lineNo,sku,qty
ORD00001,1,SKU0007,36
ORD00002,1,SKU0004,15
ORD00002,2,SKU0025,49
...
```

---

## 🧮 Análise de Complexidade

| Método | Descrição | Complexidade Temporal | Complexidade Espacial |
|---------|------------|------------------------|------------------------|
| `loadItems()` | Leitura sequencial de itens do ficheiro CSV | **O(n)** | **O(n)** |
| `loadBays()` | Leitura sequencial das bays | **O(b)** | **O(b)** |
| `loadBoxesFromWagons()` | Criação de boxes e atribuição aleatória a bays | **O(n)** | **O(n)** |
| `listarBays()` | Percorre todas as bays e mostra número de boxes | **O(b)** | **O(1)** |
| `mostrarBoxesSKU()` | Filtra e ordena boxes por SKU e data (FEFO) | **O(n log n)** | **O(n)** |
| `relocalizarBox()` | Procura e move uma box | **O(n)** | **O(1)** |
| `despacharSKU()` | Retira quantidades de boxes por data de expiração | **O(n log n)** | **O(n)** |
| `loadOrders() / loadOrderLines()` | Lê os ficheiros de encomendas | **O(n)** | **O(n)** |
| `processOrders()` | Itera sobre todas as linhas de encomenda e aplica FEFO | **O(m × n log n)** | **O(n)** |

> **Conclusão:** Todos os métodos principais apresentam complexidade linear ou quase linear.  
> O desempenho é adequado para volumes típicos de dados em contexto académico.

---

## 🧪 Testes Unitários (Resumo)

| Teste | Objetivo | Resultado Esperado |
|--------|-----------|--------------------|
| `testLoadItems()` | Verifica se o ficheiro `items.csv` é lido corretamente | Número de itens > 0 |
| `testLoadBays()` | Confirma carregamento e número de bays | Número de bays = linhas válidas |
| `testLoadBoxesFromWagons()` | Testa criação de boxes e atribuição a bays | Boxes > 0 |
| `testDespacharSKU()` | Verifica que quantidades são removidas corretamente por FEFO | Quantidades atualizadas |
| `testProcessOrders()` | Testa processamento completo das encomendas | Despacho correto de SKUs e quantidades |

---

## ⚙️ Execução

### Compilação
```bash
javac USEI01.java
```

### Execução
```bash
java USEI01
```

### Menu Interativo
```
=== MENU USEI01 / USEI02 ===
1 - Listar Bays
2 - Mostrar Boxes por SKU
3 - Relocalizar Box
4 - Despachar SKU (manual)
5 - Mostrar Todas as Boxes
6 - Carregar Orders
7 - Processar Orders (USE02)
0 - Sair
```

---

## 📊 Observações Finais

- Código simplificado e direto, sem dependências externas.  
- Utiliza estruturas básicas (`ArrayList`, `HashMap`, `Map`).  
- O critério **FEFO** é aplicado através da ordenação por `expiryDate`.  
- Não há verificações complexas para manter o foco nos objetivos principais da Sprint.  
- A arquitetura é facilmente extensível para futuras sprints (ex: USE03 – otimização de despacho).

---

📌 **Contribuição**  
Trabalho desenvolvido no âmbito da disciplina de **ESINF**.  
Sprint 1: Implementação + Testes + Análise de Complexidade.
