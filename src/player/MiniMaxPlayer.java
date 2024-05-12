package player;
import game.BoardHelper;
import java.awt.*;
import java.util.ArrayList;

public class MiniMaxPlayer extends GamePlayer {
    private Point jugada;
    private final int oponente;
    public MiniMaxPlayer(int mark, int depth) {
        super(mark, depth);
        oponente = (mark == 1 ? 2 : 1);
    }

    @Override
    public boolean isUserPlayer() { return false; }

    @Override
    public String playerName() { return "MiniMax Ficha "+ ( myMark == 1 ? "Negra" : "Blanca" ); }

    /**
     * @brief Expande el árbol de búsqueda Minimax para determinar la mejor jugada posible.
     * @param tableroActual La representación del tablero actual.
     * @param nivel El nivel actual en nuestro arbol de busqueda.
     * @param mark El id del jugador actual.
     * @return La puntuación de la mejor jugada posible.
     */
    private int expandTreeMiniMax(int[][] tableroActual, int nivel, int mark){
        //Compruebo que estoy dentro del nivel permitido
        if(nivel <= this.depth){
            if(BoardHelper.hasAnyMoves(tableroActual,mark)){//Hay movimientos posibles
                //Array de mmovimientos posibles
                ArrayList<Point> movimientos = BoardHelper.getAllPossibleMoves(tableroActual,mark);
                //Array de matrices con las posibles tableros
                ArrayList<int[][]> hijos = new ArrayList<>();
                //Array con las puntuaciones
                ArrayList<Integer> puntuaciones = new ArrayList<>();
                //Creo todos los posibles tableros despues de los posibles movimientos que hemos obtenido
                for(Point m : movimientos){
                    hijos.add(BoardHelper.getNewBoardAfterMove(tableroActual,m,mark));
                }
                //Si estamos en nuestro turno expandimos max
                if(mark==myMark){
                    return max(hijos,movimientos,puntuaciones,nivel);
                }else{ //Sino expandimos min
                    return min(hijos,puntuaciones,nivel);
                }
            }else{
                //Estoy en un nivel permitido pero ya no tengo movimientos posibles
                return vencedor(tableroActual);
            }
        }else{
            //Si he llegado al nivel permitido calculo el vencedor
           return vencedor(tableroActual);
        }
    }

    /**
     * @brief Realiza la funcion MAX en el árbol de búsqueda.
     * @param hijos Lista de tableros hijos a evaluar.
     * @param movimientos Lista de movimientos posibles.
     * @param puntuaciones Lista de puntuaciones de los movimientos posibles.
     * @param nivel El nivel actual en el árbol de búsqueda.
     * @return La puntuación máxima encontrada.
     */
    int max(ArrayList<int[][]> hijos, ArrayList<Point> movimientos, ArrayList<Integer> puntuaciones, int nivel){
        //Recorro todos los hijos expandiendo hasta que me pase de nivel
        for(int[][] h : hijos){
            puntuaciones.add(expandTreeMiniMax(h,nivel+1, oponente));
        }
        int mayorPuntuacion=-101;
        int indiceMayorPuntuacion=-1;

        //Me quedo con la puntuacion mas alta y actualizo la jugada con el movimiento de la maxima puntuacion
        for(int i=0; i<puntuaciones.size(); i++){
            if(puntuaciones.get(i) > mayorPuntuacion){
                mayorPuntuacion=puntuaciones.get(i);
                indiceMayorPuntuacion=i;
            }
        }
        if(nivel==1) {
            jugada = movimientos.get(indiceMayorPuntuacion);
        }
        return mayorPuntuacion;
    }

    /**
     * @brief Realiza la funcion MIN en el árbol de búsqueda.
     * @param hijos Lista de tableros hijos a evaluar.
     * @param puntuaciones Lista de puntuaciones de los movimientos.
     * @param nivel El nivel actual en el árbol de búsqueda.
     * @return La puntuación mínima encontrada.
     */
    int min(ArrayList<int[][]> hijos, ArrayList<Integer> puntuaciones, int nivel){
        for(int[][] h : hijos){
            puntuaciones.add(expandTreeMiniMax(h,nivel+1,myMark));
        }
        //Me quedo con la puntuacion mas baja y actualizo la jugada con el movimiento de la minima puntuacion
        int menorPuntuacion=101;
        for(int i: puntuaciones){
            if(i < menorPuntuacion){ menorPuntuacion=i; }
        }
        return menorPuntuacion;
    }

    /**
     * @brief Determina el resultado del juego en el tablero actual.
     * @param tableroActual La representación actual del tablero.
     * @return La puntuación del juego en el tablero actual.
     */
    int vencedor(int[][] tableroActual) {
        if (BoardHelper.getWinner(tableroActual) == myMark) {
            return 100;
        } else if (BoardHelper.getWinner(tableroActual) == oponente) {
            return -100;
        } else if (BoardHelper.getWinner(tableroActual) == 0) {
            return 0;
        } else {
            return BoardHelper.getPlayerStoneCount(tableroActual, myMark) - BoardHelper.getPlayerStoneCount(tableroActual, oponente);
        }
    }

    @Override
    public Point play(int[][] board) {
        jugada=null;
        if(BoardHelper.hasAnyMoves(board,myMark)) {
            //Comienzo expansión
            int mejorPuntuacion = expandTreeMiniMax(board, 1,myMark);
            System.out.println("La mejor puntuacion expandida es " + mejorPuntuacion);
        }
        return jugada;
    }
}