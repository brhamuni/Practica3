/**
 * @author  Ángel Miguel García Vico (agvico@ujaen.es)
 * @version 1.0
 * @since JDK 1.8
 */

package game;

import player.*;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
//import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase representa el panel del juego, incluyendo el tablero y la lógica
 * de juego.
 * Implementa la interfaz GameEngine para proporcionar una estructura básica de
 * juego.
 */
public class GamePanel extends JPanel implements GameEngine {

    // Tablero del juego Reversi
    private int[][] board;

    // Turno del jugador, donde 1 representa a las negras y juegan primero.
    private int turn = 1;

    // Elementos de Swing para la interfaz gráfica
    private BoardCell[][] cells;
    private JLabel score1;
    private JLabel score2;
    private int totalscore1 = 0;
    private int totalscore2 = 0;
    private JLabel tscore1;
    private JLabel tscore2;

    // Definición de los jugadores y sus Timers
    private GamePlayer player1;
    private GamePlayer player2;
    private GamePlayer[] players;
    private Timer player1HandlerTimer;
    private Timer player2HandlerTimer;
    private Timer[] playerTimers;
    private boolean selectNewPlayer = false;

    // Centinela para esperar a click en caso de jugador humano
    private boolean awaitForClick = false;

    /**
     * Obtiene el valor de una celda específica en el tablero.
     *
     * @param i la fila de la celda.
     * @param j la columna de la celda.
     * @return el valor de la celda en la posición especificada.
     */
    @Override
    public int getBoardValue(int i, int j) {
        return board[i][j];
    }

    /**
     * Establece el valor de una celda específica en el tablero.
     *
     * @param i     la fila de la celda.
     * @param j     la columna de la celda.
     * @param value el valor a establecer en la celda.
     */
    @Override
    public void setBoardValue(int i, int j, int value) {
        board[i][j] = value;
    }

    /**
     * Constructor de GamePanel. Inicializa el juego, el tablero, y los jugadores.
     *
     * @param player1 el primer jugador del juego.
     * @param player2 el segundo jugador del juego.
     */
    public GamePanel(GamePlayer player1, GamePlayer player2) {

        this.player1 = player1;
        this.player2 = player2;
        players = new GamePlayer[] { player1, player2 };

        this.setBackground(Color.WHITE);
        this.setLayout(new BorderLayout());

        // Crea el layout del juego
        JPanel reversiBoard = new JPanel();
        reversiBoard.setLayout(new GridLayout(8, 8));
        reversiBoard.setPreferredSize(new Dimension(500, 500));
        reversiBoard.setBackground(new Color(41, 100, 59));

        // init board
        resetBoard();

        cells = new BoardCell[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                cells[i][j] = new BoardCell(this, reversiBoard, i, j);
                reversiBoard.add(cells[i][j]);
            }
        }

        // Define la barra lateral que muestra la puntuación actual
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));

        sidebar.add(new JLabel("PARTIDA ACTUAL:"));
        score1 = new JLabel("Score 1");
        score2 = new JLabel("Score 2");

        tscore1 = new JLabel("Total Score 1");
        tscore2 = new JLabel("Total Score 2");

        sidebar.add(score1);
        sidebar.add(score2);

        sidebar.add(new JLabel("-----------"));
        sidebar.add(new JLabel("PARTIDAS GANADAS TOTALES:"));
        sidebar.add(tscore1);
        sidebar.add(tscore2);

        this.add(sidebar, BorderLayout.WEST);
        this.add(reversiBoard);

        // Repintar el panel  para mostrar todo.
        updateBoardInfo();
        updateTotalScore();
        
        //Tiempo limite de ejecución en ms. Si nos colamos, nos avisará
        long timelimit = 1000;

        // AI Handler Timer
        player1HandlerTimer = new Timer(1000, (ActionEvent e) -> {
            long t = System.currentTimeMillis();
            handleAI(players[0]);
            player1HandlerTimer.stop();
            long t_ejec = System.currentTimeMillis() - t;
            System.out.println("Tiempo de ejecución: " +t_ejec + " ms.");
            if(t_ejec > timelimit) System.out.println("JUGADOR 1 SE HA PASADO EN SU TIEMPO DE EJECUCIÓN.");
            manageTurn();
        });

        player2HandlerTimer = new Timer(1000, (ActionEvent e) -> {
            long t = System.currentTimeMillis();
            handleAI(players[1]);
            player2HandlerTimer.stop();
            long t_ejec = System.currentTimeMillis() - t;
            System.out.println("Tiempo de ejecución: " +t_ejec + " ms.");
            if(t_ejec > timelimit) System.out.println("JUGADOR 2 SE HA PASADO EN SU TIEMPO DE EJECUCIÓN.");
            manageTurn();
        });

        playerTimers = new Timer[] { player1HandlerTimer, player2HandlerTimer };

        manageTurn();
    }



    /**
     * Método para gestionar los turnos del juego.
     */
    public void manageTurn() {
        updateBoardInfo();

        if(selectNewPlayer){    // New Players selected, restart the game!!
            resetBoard();
            updateBoardInfo();
            turn=1;
            selectNewPlayer = false;
        }
        
        // Start the turn
        if (!BoardHelper.isGameFinished(board)) { // Determinar si el tablaro se ha llenado (nadie puede mover)
            if (BoardHelper.hasAnyMoves(board, turn)) { // si el jugador tiene movimientos
                if (players[turn - 1].isUserPlayer()) {
                    // Jugador humano
                    awaitForClick = true;
                } else {
                    // Jugador IA
                    playerTimers[turn - 1].start();
                }
            } else { // si no tiene movimientos, pasar turno.
                System.out.println("\033[1;30;34m Jugador " + turn + " no tiene movimientos!\033[0m\n");
                turn = (turn % 2) + 1;
                manageTurn();
            }
        } else {
            // game finished
            System.out.println("Fin del Juego !");
            int winner = BoardHelper.getWinner(board);
            if (winner == 1)
                totalscore1++;
            else if (winner == 2)
                totalscore2++;
            updateTotalScore();

            // restart
            String message = "";
            if (winner > 0) {
                message += "PARTIDA TERMINADA\n\n GANA JUGADOR " + winner + ".\n\n ¿Quieres empezar otra partida?";
            } else if (winner == 0) {
                message += "PARTIDA TERMINADA\n\n  ¡EMPATE!\n\n ¿Quieres empezar otra partida?";
            } else {
                // Error
                throw new RuntimeException(
                        "winner ha devuelto partida no terminada y el juego ha determinado que se ha terminado... ¿Qué ha pasado?");
            }
            int option = JOptionPane.showConfirmDialog(this, message, "Reversi IA", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                resetBoard();
                turn = 1;
                manageTurn();
            }
        }
    }

    /**
     * Reinicia el tablero a su estado inicial.
     */
    public void resetBoard() {
        board = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = 0;
            }
        }
        // initial board state
        setBoardValue(3, 3, 2);
        setBoardValue(3, 4, 1);
        setBoardValue(4, 3, 1);
        setBoardValue(4, 4, 2);
    }

    /**
     * Actualiza la información del tablero, incluyendo los posibles movimientos y
     * las puntuaciones.
     */
    public void updateBoardInfo() {

        int p1score = 0;
        int p2score = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 1)
                    p1score++;
                if (board[i][j] == 2)
                    p2score++;

                if (BoardHelper.canPlay(board, turn, i, j)) {
                    cells[i][j].highlight = 1;
                } else {
                    cells[i][j].highlight = 0;
                }
            }
        }

        score1.setText(players[0].playerName() + " : " + p1score);
        score2.setText(players[1].playerName() + " : " + p2score);
    }

    /**
     * Actualiza el marcador total de cada jugador.
     */
    public void updateTotalScore() {
        tscore1.setText(players[0].playerName() + " : " + totalscore1);
        tscore2.setText(players[1].playerName() + " : " + totalscore2);
    }

    /**
     * Maneja las acciones de clic en el tablero por parte del usuario.
     *
     * @param i la fila donde el usuario ha hecho clic.
     * @param j la columna donde el usuario ha hecho clic.
     */
    @Override
    public void handleClick(int i, int j) {
        if (awaitForClick && BoardHelper.canPlay(board, turn, i, j)) {
            System.out.println("User Played in : (" + i + ", " + j + ")");

            // update board
            board = BoardHelper.getNewBoardAfterMove(board, new Point(i, j), turn);

            // advance turn
            turn = (turn % 2) + 1;

            repaint();

            awaitForClick = false;

            // callback
            manageTurn();
        }
    }

    /**
     * Procesa la jugada de un jugador de Inteligencia Artificial.
     *
     * @param ai el jugador IA que está realizando la jugada.
     */
    public void handleAI(GamePlayer ai) throws RuntimeException {
        Point aiPlayPoint = ai.play(board);
        int i = aiPlayPoint.x;
        int j = aiPlayPoint.y;

        if (!BoardHelper.canPlay(board, ai.getMyMark(), i, j)) { // Si la IA elige una posición no válida, lanzamos
                                                                 // excepción y terminamos el juego
            throw new RuntimeException("FATAL : Movimiento inválido de la IA !");
        }

        System.out.println(ai.playerName() + " Jugó en : (" + i + ", " + j + ")");

        // update board
        board = BoardHelper.getNewBoardAfterMove(board, aiPlayPoint, turn);

        // advance turn
        turn = (turn % 2) + 1; // (turn == 1) ? 2 : 1;

        repaint();
    }


    /**
     * Muestra el desplegable para seleccionar nuevos jugadores y reinicia el juego tras confirmar.
     */
    public void selectPlayers() {
        JDialog selectionDialog = new JDialog((JFrame) SwingUtilities.windowForComponent(this),
                "Seleccionar Tipo de Jugadores", true);
        selectionDialog.setLayout(new GridLayout(5, 2)); // Actualizado para acomodar dos filas adicionales

        // Etiquetas y combos para selección de jugadores
        JLabel labelPlayer1 = new JLabel("Jugador 1:");
        JComboBox<Class<?>> comboBoxPlayer1 = new JComboBox<>();

        JLabel labelPlayer2 = new JLabel("Jugador 2:");
        JComboBox<Class<?>> comboBoxPlayer2 = new JComboBox<>();

        // Campos para establecer la profundidad de exploración de la IA
        JLabel labelDepthPlayer1 = new JLabel("Profundidad IA Jugador 1:");
        JSpinner spinnerDepthPlayer1 = new JSpinner(new SpinnerNumberModel(3, 1, 20, 1)); // Ejemplo: valor inicial 3,
                                                                                          // min 1, max 10, paso 1
        JLabel labelDepthPlayer2 = new JLabel("Profundidad IA Jugador 2:");
        JSpinner spinnerDepthPlayer2 = new JSpinner(new SpinnerNumberModel(3, 1, 20, 1)); // Similar para el jugador 2

        // Botón de confirmación
        JButton confirmButton = new JButton("Confirmar");

        // Cargar clases dinámicamente y añadirlas a los JComboBox
        List<Class<?>> playerClasses = PlayerLoader.load();
        for (Class<?> playerClass : playerClasses) {
            comboBoxPlayer1.addItem(playerClass);
            comboBoxPlayer2.addItem(playerClass);
        }

        // Establecer selecciones por defecto si es posible
        comboBoxPlayer1.setSelectedItem(players[0].getClass());
        comboBoxPlayer2.setSelectedItem(players[1].getClass());

        // Cuando se pulsa el boton de confirmar, se realiza la accion
        confirmButton.addActionListener(e -> {
            // Adquirir datos de la ventana
            Class<?> selectedClass1 = (Class<?>) comboBoxPlayer1.getSelectedItem();
            Class<?> selectedClass2 = (Class<?>) comboBoxPlayer2.getSelectedItem();
            int depth1 = (Integer) spinnerDepthPlayer1.getValue();
            int depth2 = (Integer) spinnerDepthPlayer2.getValue();
            
            // Parar las jugadas de las IAs (si las hay)
            playerTimers[0].stop();
            playerTimers[1].stop();

            // Instanciar las nuevas clases elegidas y reiniciar
            try {
                // La clase GamePlayer tiene 2 parametros enteros: mark y depth. Depth en
                // HumanPlayer se ignorara
                Class[] intArgsClass = new Class[] { int.class, int.class };

                Constructor<?> constructor = selectedClass1.getConstructor(intArgsClass);
                players[0] = (GamePlayer) constructor.newInstance(1, depth1);
                Constructor<?> constructor2 = selectedClass2.getConstructor(intArgsClass);
                players[1] = (GamePlayer) constructor2.newInstance(2, depth2);
                selectionDialog.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            // Resetear el juego una vez se han seleccionado los jugadores.
            this.selectNewPlayer = true;
            repaint();
            awaitForClick = false;
            // callback
            turn = 1;
            manageTurn();
      
        });

        // Añadir componentes al diálogo
        selectionDialog.add(labelPlayer1);
        selectionDialog.add(comboBoxPlayer1);
        selectionDialog.add(labelPlayer2);
        selectionDialog.add(comboBoxPlayer2);
        selectionDialog.add(labelDepthPlayer1);
        selectionDialog.add(spinnerDepthPlayer1);
        selectionDialog.add(labelDepthPlayer2);
        selectionDialog.add(spinnerDepthPlayer2);
        selectionDialog.add(new JLabel()); // Placeholder para alinear el botón de confirmación
        selectionDialog.add(confirmButton);

        selectionDialog.pack();
        selectionDialog.setLocationRelativeTo(this);
        selectionDialog.setVisible(true);
    }
}
