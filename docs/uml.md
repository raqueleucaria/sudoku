# Diagrama UML

``` mermaid
classDiagram
    direction LR
    class Board {
    - spaces: List<List<Space>>
    + getSpaces(): List<List<Space>>
    + getStatus(): GameStatusEnum
    + hasErrors(): boolean
    + changeValue(int row, int col, Integer value): boolean
    + clearValue(int row, int col): boolean
    + reset(): void
    + gameIsFinished(): boolean
    }
    
    class Space {
        - actual: Integer
        + fixed: boolean
        + expected: int
        + getActual(): Integer
        + setActual(Integer value): void
        + clearSpace(): void
        + getExpected(): int
        + isFixed(): boolean
    }
    
    class GameStatusEnum {
        <<enumeration>>
        NON_STARTED
        INCOMPLETE
        COMPLETE
    }
    
    Board "1" o-- "*" Space : contains
    Board ..> GameStatusEnum : getStatus()

```


## Classes

### Space
Classe que representa uma única célula no tabuleiro de Sudoku.

- Atributos:
    - `actual: Integer` — Valor atual da célula, pode ser `null`.
    - `fixed: boolean` — Indica se a célula é um número inicial do jogo (imutável após a criação).
    - `expected: int` — Valor correto que a célula deve ter para que o jogo esteja correto (imutável).

- Métodos:
    - `getActual(): Integer` — Retorna o valor atual da célula.
    - `setActual(Integer value): void` — Define o valor atual da célula, apenas se ela não for fixa.
    - `clearSpace(): void` — Limpa o valor da célula (define como `null`).
    - `getExpected(): int` — Retorna o valor esperado da célula.
    - `isFixed(): boolean` — Retorna `true` se a célula for fixa.

---

### GameStatusEnum
Enumeração que define os possíveis estados do jogo.

- Valores:
    - `NON_STARTED` — O jogo ainda não começou (nenhum número foi inserido).
    - `INCOMPLETE` — O jogo está em andamento, mas ainda não está completo.
    - `COMPLETE` — O jogo está completo (todas as células preenchidas).

---

### Board
Classe que representa o tabuleiro do jogo e gerencia a lógica principal (estado do jogo e interação com as células).

- Atributos:
    - `spaces: List<List<Space>>` — Matriz 9x9 contendo os objetos `Space`.

- Métodos:
    - `getSpaces(): List<List<Space>>` — Retorna a estrutura de células do tabuleiro.
    - `getStatus(): GameStatusEnum` — Retorna o estado atual do jogo.
    - `hasErrors(): boolean` — Indica se há inconsistências no tabuleiro.
    - `changeValue(int row, int col, Integer value): boolean` — Altera o valor de uma célula (se permitido).
    - `clearValue(int row, int col): boolean` — Limpa o valor de uma célula.
    - `reset(): void` — Reinicia o tabuleiro para o estado inicial.
    - `gameIsFinished(): boolean` — Indica se o jogo foi concluído.