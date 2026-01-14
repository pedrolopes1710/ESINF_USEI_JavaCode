# Análise de Complexidade — Sprint 2 (ESINF)

Este documento apresenta a **análise de complexidade temporal** das User Stories USEI06 a USEI10, de acordo com os critérios de avaliação do Sprint 2.

---

## USEI06 — Time-Zone Index and Windowed Queries

### Operações Principais
- Validação de dados de entrada
- Inserção em BST/AVL indexada por *time zone group*
- Pesquisa por *time zone group* e janelas de grupos

### Complexidade Temporal
- **Inserção na AVL**:  
  \[ O(\log n) \] por estação

- **Pesquisa por time zone group**:  
  \[ O(\log n + k) \]  
  onde *k* é o número de estações devolvidas

- **Pesquisa por janela de time zones**:  
  \[ O(\log n + k) \]

### Justificação
A utilização de árvores AVL garante equilíbrio, permitindo operações eficientes sem necessidade de percorrer todo o conjunto de ~64k estações.

---

## USEI07 — Construção de uma 2D-Tree Balanceada

### Operações Principais
- Construção por *bulk build*
- Seleção da mediana
- Alternância de eixo (latitude / longitude)

### Complexidade Temporal
- **Construção da 2D-tree**:  
  \[ O(n \log n) \]

### Justificação
Cada nível da árvore requer a ordenação ou acesso ordenado dos dados (provenientes das AVL), repetindo-se para \( \log n \) níveis.

---

## USEI08 — Pesquisa por Área Geográfica (Range Search)

### Operações Principais
- Pesquisa em intervalo [latMin, latMax] × [lonMin, lonMax]
- *Pruning* de ramos irrelevantes
- Filtros opcionais (country, is_city, is_main_station)

### Complexidade Temporal
- **Melhor caso**:  
  \[ O(\log n) \]

- **Caso médio**:  
  \[ O(\sqrt{n} + k) \]

- **Pior caso**:  
  \[ O(n) \]

### Justificação
A 2D-tree permite eliminar grandes partes da árvore através de *pruning*, reduzindo significativamente o número de nós visitados em cenários reais.

---

## USEI09 — Proximity Search (Nearest-N)

### Operações Principais
- Cálculo de distância (Haversine)
- Pesquisa recursiva na 2D-tree
- Poda baseada na distância ao plano de separação

### Complexidade Temporal
- **Caso médio**:  
  \[ O(\log n + N \log N) \]

- **Pior caso**:  
  \[ O(n) \]

### Justificação
A maioria dos nós é descartada através de pruning espacial. O custo adicional \( N \log N \) corresponde à manutenção da lista ordenada dos vizinhos mais próximos.

---
