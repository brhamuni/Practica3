package game;

import player.*;

import javax.swing.*;

public class GameWindow extends JFrame {

    public GameWindow(GamePlayer player1, GamePlayer player2){
        GamePanel gp = new GamePanel(player1, player2);
        this.add(gp);
        this.setTitle("Othello - Inteligencia Artificial curso 2023-24");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Set menu
        // Creación de la barra de menú
        JMenuBar menuBar = new JMenuBar();

        // Menú Archivo
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> System.exit(0));
        menuArchivo.add(itemSalir);
        
        // Menú Opciones
        JMenu menuOpciones = new JMenu("Opciones");
        JMenuItem itemTipoJugadores = new JMenuItem("Seleccionar tipo de jugadores");
        
        itemTipoJugadores.addActionListener(e -> gp.selectPlayers());
        
        menuOpciones.add(itemTipoJugadores);
        
        // Agregar menús a la barra de menú
        menuBar.add(menuArchivo);
        menuBar.add(menuOpciones);
        
        // Establecer la barra de menú en el JFrame
        setJMenuBar(menuBar);

        this.pack();
        this.setVisible(true);
    }

    public static void main(String[] args) {
        // Select the players
        GamePlayer player1 = new HumanPlayer(1, 0); 
        GamePlayer player2 = new HumanPlayer(2, 0); 

        // Starts the game with the selected players.
        new GameWindow(player1, player2);
    }

}
