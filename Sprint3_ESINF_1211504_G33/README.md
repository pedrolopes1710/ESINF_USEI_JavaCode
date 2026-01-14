🧩 USEI11 — Directed Line Upgrade Plan
📖 Descrição

Esta user story tem como objetivo determinar uma ordem válida para a atualização das linhas ferroviárias, respeitando dependências direcionadas entre estações. Caso existam ciclos de dependências, estes devem ser identificados.

🧠 Abordagem Algorítmica

O problema é modelado como um grafo dirigido, onde:

Vértices representam estações

Arestas representam dependências

É aplicada uma ordenação topológica baseada em DFS, utilizando marcação de estados (WHITE, GRAY, BLACK) para deteção de ciclos.

✅ Resultado Esperado

Se o grafo for acíclico: lista ordenada de estações (plano de upgrade)

Se existirem ciclos: indicação das estações envolvidas

⏱️ Análise de Complexidade

Tempo: O(V + E)

Espaço: O(V)

Onde:

V é o número de estações

E é o número de dependências

🧩 USEI12 — Minimal Backbone Network
📖 Descrição

Esta user story pretende determinar o custo mínimo necessário para manter toda a rede ferroviária conectada, eliminando redundâncias.

🧠 Abordagem Algorítmica

O problema é resolvido através da construção de uma Árvore Geradora Mínima (MST) usando o algoritmo de Kruskal:

O grafo é considerado não dirigido e ponderado

As arestas são ordenadas por custo crescente

É utilizada uma estrutura Union-Find para evitar ciclos

✅ Resultado Esperado

Conjunto mínimo de ligações que mantém a rede conectada

Custo total mínimo

⏱️ Análise de Complexidade

Tempo: O(E log E)

Espaço: O(V)

Onde:

E é o número de ligações

V é o número de estações

🧪 Testes Unitários

Foram desenvolvidos testes unitários com JUnit 5 para garantir:

Correção da ordenação topológica

Deteção de ciclos na USEI11

Cálculo correto do custo mínimo da MST na USEI12

Os testes encontram-se em src/test/java e podem ser executados com:

mvn test
📌 Conclusão

As soluções apresentadas seguem boas práticas de engenharia de software, utilizam algoritmos eficientes e estão devidamente testadas, cumprindo integralmente os requisitos das user stories USEI11 e USEI12 do Sprint 3.