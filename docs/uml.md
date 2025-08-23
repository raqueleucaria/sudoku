# Diagrama UML

``` mermaid
classDiagram
    direction LR
    class Board {
        + spaces: Space[][]
    }
    class Space {
        + fixed: boolean
        + actual: Integer
        + expected: int
    }

    Board "1" --o "*" Space : contains


```