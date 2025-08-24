# Documentação Arquitetural do Jogo Sudoku

Este documento detalha a arquitetura do jogo Sudoku, abrangendo desde o modelo de dados principal até a construção da interface gráfica e os padrões de projeto utilizados para a comunicação entre os componentes.

---

## 1. Visão Geral da Arquitetura

O projeto é estruturado em três camadas principais:

- Camada de Modelo (Model): Contém as classes que representam o estado e as regras do jogo (`Board`, `Space`, `GameStatusEnum`). É o núcleo da lógica de negócio.
- Camada de Serviço (Service): Funciona como uma ponte entre o Modelo e a Interface do Usuário (UI). Ela simplifica o acesso à lógica do jogo (`BoardService`) e gerencia a comunicação desacoplada entre componentes (`NotifierService`).
- Camada de Interface (UI): Responsável por toda a apresentação visual e interação com o usuário, construída com Java Swing.

A seguir, um diagrama de classes completo que ilustra a relação entre todos esses componentes.

---

## 2. Diagrama de Classes Completo
``` mermaid
classDiagram
direction TD

%% Camada de Modelo (Model)
namespace "Camada de Modelo (Model)" {
  class Board {
    - spaces: List~List~Space~~
    + getStatus(): GameStatusEnum
    + hasErrors(): boolean
    + reset(): void
  }

  class Space {
    - actual: Integer
    - fixed: boolean
    - expected: int
    + setActual(value: Integer): void
    + clearSpace(): void
  }

  class GameStatusEnum
  GameStatusEnum : <<enumeration>>
  GameStatusEnum : NON_STARTED
  GameStatusEnum : INCOMPLETE
  GameStatusEnum : COMPLETE
}

%% Camada de Serviço (Service)
namespace "Camada de Serviço (Service)" {
  class BoardService {
    - board: Board
    + getSpaces(): List~List~Space~~
    + reset(): void
    + hasErrors(): boolean
  }

  class NotifierService {
    - listeners: Map
    + subscribe(listener: EventListener): void
    + notify(event: EventEnum): void
  }

  class EventListener
  EventListener : <<interface>>
  EventListener : + update(event: EventEnum): void

  class EventEnum
  EventEnum : <<enumeration>>
  EventEnum : CLEAR_SPACE
}

%% Camada de Interface (UI)
namespace "Camada de Interface (UI)" {
  class UIMain {
    + main(args: String[])
  }

  class MainScreen {
    + buildMainScreen(): void
  }

  class MainFrame
  class MainPanel
  class SudokuSector

  class NumberText {
    - space: Space
    + update(event: EventEnum): void
  }

  class ResetButton

  %% (Opcional) classes Swing para evitar arestas apontarem para tipos não declarados
  class JTextField
  class JButton
  class JFrame
  class JPanel
}

%% Relações entre camadas
BoardService ..> Board : Delega para
MainScreen o-- BoardService : Usa
MainScreen o-- NotifierService : Usa

%% Relações na UI
UIMain ..> MainScreen : Cria
MainScreen ..> MainFrame : Cria
MainFrame o-- MainPanel : Contém
MainPanel o-- SudokuSector : Contém
SudokuSector o-- NumberText : Contém
MainScreen ..> ResetButton : Cria e configura

%% Relação do Padrão Observer
NotifierService o-- EventListener : Notifica
NumberText --|> EventListener : Implementa

%% Relação entre UI e Modelo
NumberText o-- Space : Associa-se a

%% Heranças do Swing
NumberText --|> JTextField
ResetButton --|> JButton
MainFrame --|> JFrame
MainPanel --|> JPanel
SudokuSector --|> JPanel

```


---

## 3. Descrição das Camadas e Classes

### 3.1. Camada de Modelo (Model)

Esta camada é o coração do jogo, contendo a lógica pura, sem qualquer conhecimento sobre a interface gráfica.

#### Space (`Space.java`)
- Descrição: Classe que representa uma única célula no tabuleiro de Sudoku.
- Atributos:
    - `actual: Integer` — Valor atual da célula, inserido pelo jogador. Pode ser `null`.
    - `fixed: boolean` — Indica se a célula é um número inicial do jogo e, portanto, imutável.
    - `expected: int` — Valor correto que a célula deve ter para que o jogo esteja resolvido.
- Métodos-chave:
    - `setActual(Integer value)`: Define o valor atual, apenas se a célula não for fixa.
    - `clearSpace()`: Limpa o valor da célula (define como `null`).

#### GameStatusEnum (`GameStatusEnum.java`)
- Descrição: Enumeração que define os possíveis estados do jogo, auxiliando no controle do fluxo da partida.
- Valores: `NON_STARTED`, `INCOMPLETE`, `COMPLETE`.

#### Board (`Board.java`)
- Descrição: Classe que representa o tabuleiro 9x9 e gerencia a lógica principal, como o estado do jogo e a interação com as células.
- Atributos:
    - `spaces: List<List<Space>>` — Matriz 9x9 contendo os objetos `Space`.
- Métodos-chave:
    - `getStatus()`: Retorna o estado atual do jogo (`GameStatusEnum`).
    - `hasErrors()`: Verifica se algum dos valores inseridos pelo jogador está incorreto.
    - `reset()`: Reinicia o tabuleiro para o estado inicial, limpando todas as células não fixas.
    - `gameIsFinished()`: Indica se o jogo foi concluído com sucesso (completo e sem erros).

### 3.2. Camada de Serviço (Service)

Esta camada desacopla a UI da lógica de negócio, facilitando a manutenção e a comunicação entre componentes.

#### BoardService (`BoardService.java`)
- Descrição: Atua como uma fachada (Facade) para o modelo. Ela simplifica a interação da UI com o `Board`, expondo apenas os métodos necessários para a manipulação do jogo.
- Interações: É instanciada pela `MainScreen` e delega todas as chamadas de lógica de jogo para o objeto `Board` que ela encapsula.

#### NotifierService (`NotifierService.java`)
- Descrição: Implementa o "Subject" do padrão de projeto Observer. Sua função é permitir que componentes se inscrevam para "ouvir" eventos e notificar todos eles quando um evento é disparado.
- Interações: É usada pela `MainScreen` para notificar eventos (como `CLEAR_SPACE`) e pelos `NumberText` que se inscrevem como ouvintes.

#### EventListener (`EventListener.java`) e EventEnum (`EventEnum.java`)
- Descrição: `EventListener` é a interface que define o contrato para os "Observers". `EventEnum` define os tipos de eventos que podem ser transmitidos, como `CLEAR_SPACE`.

### 3.3. Camada de Interface (UI)

Construída com Java Swing, esta camada é responsável por toda a parte visual e pela captura das ações do usuário.

#### UIMain (`UIMain.java`)
- Descrição: Ponto de entrada da aplicação gráfica. Inicia a `MainScreen`.

#### MainScreen (`MainScreen.java`)
- Descrição: Classe orquestradora da UI. Monta a janela, os painéis, os campos de texto e os botões, conectando as ações do usuário (cliques, digitação) com a camada de serviço.
- Interações: Utiliza o `BoardService` para executar ações de jogo e o `NotifierService` para disparar eventos de UI.

#### NumberText (`NumberText.java`)
- Descrição: Componente de texto customizado que representa uma célula do tabuleiro. Ele está diretamente associado a um objeto `Space` do modelo e também atua como um "Observer", implementando `EventListener`.
- Interações:
    - Quando o usuário digita, um `DocumentListener` interno atualiza o valor no objeto `Space` correspondente.
    - Quando o `NotifierService` dispara o evento `CLEAR_SPACE`, seu método `update()` é chamado, fazendo com que o campo de texto se limpe.

#### Outros Componentes (`MainFrame`, `MainPanel`, `SudokuSector`, `ResetButton`)
- Descrição: São os componentes Swing que formam a estrutura visual da aplicação, como a janela principal, painéis de organização e botões de ação.

---

## 4. Interações Dinâmicas: O Padrão Observer em Ação

Para entender como os componentes interagem dinamicamente, o melhor exemplo é o fluxo de reinicialização do jogo.

### Diagrama de Sequência: Reiniciar Jogo

``` mermaid
sequenceDiagram
    participant User
    participant ResetButton
    participant MainScreen
    participant BoardService
    participant NotifierService
    participant NumberText_1 as "NumberText (Ouvinte 1)"
    participant NumberText_2 as "NumberText (Ouvinte N)"

    User->>ResetButton: Clica no botão
    activate ResetButton
    ResetButton->>MainScreen: Dispara ActionEvent
    activate MainScreen
    MainScreen->>BoardService: reset()
    activate BoardService
    BoardService-->>MainScreen: Modelo de dados resetado
    deactivate BoardService

    MainScreen->>NotifierService: notify(CLEAR_SPACE)
    activate NotifierService
    NotifierService->>NumberText_1: update(CLEAR_SPACE)
    activate NumberText_1
    NumberText_1->>NumberText_1: this.setText("")
    deactivate NumberText_1

    NotifierService->>NumberText_2: update(CLEAR_SPACE)
    activate NumberText_2
    NumberText_2->>NumberText_2: this.setText("")
    deactivate NumberText_2

    NotifierService-->>MainScreen: Notificações enviadas
    deactivate NotifierService
    deactivate MainScreen
    deactivate ResetButton
```


### Fluxo de Execução

1. O usuário clica no `ResetButton`.
2. O `ActionListener` do botão, configurado na `MainScreen`, é acionado.
3. A `MainScreen` instrui o `BoardService` a resetar o modelo de dados (`boardService.reset()`).
4. Em seguida, a `MainScreen` pede ao `NotifierService` para notificar a todos sobre o evento `CLEAR_SPACE`.
5. O `NotifierService` percorre sua lista de ouvintes (todos os `NumberText` que se inscreveram) e chama o método `update()` em cada um.
6. Cada `NumberText` recebe a notificação e executa sua lógica interna: limpar seu próprio campo de texto.

Este mecanismo garante que a `MainScreen` não precise conhecer cada um dos 81 campos de texto individualmente, criando um sistema de baixo acoplamento e alta coesão.