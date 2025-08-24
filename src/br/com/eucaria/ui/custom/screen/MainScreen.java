package br.com.eucaria.ui.custom.screen;

import br.com.eucaria.model.Space;
import br.com.eucaria.service.BoardService;
import br.com.eucaria.ui.custom.button.CheckGameStatusButton;
import br.com.eucaria.ui.custom.button.FinishGameButton;
import br.com.eucaria.ui.custom.button.ResetButton;
import br.com.eucaria.ui.custom.frame.MainFrame;
import br.com.eucaria.ui.custom.input.NumberText;
import br.com.eucaria.ui.custom.panel.MainPanel;
import br.com.eucaria.ui.custom.panel.SudokuSector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;

public class MainScreen {

    private final static Dimension dimension = new Dimension(600, 600);

    private final BoardService boardService;

    private JButton checkGameStatusButton;
    private JButton finishGameButton;
    private JButton resetButton;

    public MainScreen(final Map<String, String> gameConfig) {
        this.boardService = new BoardService(gameConfig);
    }

    public void buildMainScreen(){
        JPanel mainPanel = new MainPanel(dimension);
        JFrame mainFrame = new MainFrame(dimension, mainPanel);
        for (int r = 0; r < 9; r+=3) {
            var endRow = r + 2;
            for (int c = 0; c < 9; c+=3) {
                var endCol = c+2;
                var spaces = getSpacesFromSector(boardService.getSpaces(), c, endCol, r, endRow);
                JPanel sector = generateSection(spaces);
                mainPanel.add(sector);

            }
        }
        addResetButton(mainPanel);
        addCheckGameStatusButton(mainPanel);
        addFinishGameButton(mainPanel);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private List<Space> getSpacesFromSector(final List<List<Space>> spaces,
                                            final int initCol, final int endCol,
                                            final int initRow, final int endRow){
        List<Space> spaceSector = new ArrayList<>();
        for (int r = initRow; r <= endRow; r++) {
            for (int c = initCol; c <= endCol; c++) {
                spaceSector.add(spaces.get(c).get(r));
            }
        }
        return spaceSector;
    }

    private JPanel generateSection(final List<Space> spaces){
        List<NumberText> fields = new ArrayList<>(spaces.stream().map(NumberText::new).toList());
        // fields.forEach(f -> f.notifierService.subscribe(CLEAR_SPACE, t));
        return new SudokuSector(fields);
    }

    private void addResetButton(final JPanel mainPanel) {
        resetButton = new ResetButton(e ->{
            var dialogResult = JOptionPane.showConfirmDialog(
                    null,
                    "Deseja realmente reiniciar o jogo?",
                    "Limpar o jogo",
                    YES_NO_OPTION,
                    QUESTION_MESSAGE
            );
            if (dialogResult == 0){ // 0 -> sim
                boardService.reset();
            }
        });
        mainPanel.add(resetButton);
    }

    private void addCheckGameStatusButton(final JPanel mainPanel) {
        checkGameStatusButton = new CheckGameStatusButton(e -> {
            var hasErrors = boardService.hasErrors();
            var gameStatus = boardService.getStatus();
            var message = switch (gameStatus){
                case NON_STARTED -> "O jogo não foi iniciado";
                case INCOMPLETE -> "O jogo está imcompleto";
                case COMPLETE -> "O jogo está completo";
            };
            message += hasErrors ? " e contém erros" : " e não contém erros";
            JOptionPane.showMessageDialog(null, message);
        });
        mainPanel.add(MainScreen.this.checkGameStatusButton);
    }

    private void addFinishGameButton(final JPanel mainPanel) {
        finishGameButton = new FinishGameButton(e -> {
            if (boardService.gameIsFinished()){
                JOptionPane.showMessageDialog(null, "Parabéns, você concluiu o jogo!");
                resetButton.setEnabled(false);
                checkGameStatusButton.setEnabled(false);
                finishGameButton.setEnabled(false);
            } else {
                var message = "Seu jogo tem alguma inconsistência, ajuste e tente novamente";
                JOptionPane.showMessageDialog(null, message);
            }
        });
        mainPanel.add(finishGameButton);
    }

}