package player;

import game.BoardHelper;

import java.awt.*;
import java.util.ArrayList;

public class AlphaBetaPlayer extends GamePlayer {
    private Point jugada;
    private int oponente;
    private double Peso_Puntuacion, Peso_Movimiento;

    public AlphaBetaPlayer(int mark, int depth) {
        super(mark, depth);
        oponente = (mark == 1 ? 2 : 1);
        Peso_Puntuacion =0.5;
        Peso_Movimiento =0.5;
    }

    @Override
    public boolean isUserPlayer() {
        return false;
    }

    @Override
    public String playerName() { return "AlphaBetaPlayer Ficha "+ ( myMark == 1 ? "Negra" : "Blanca" ); }

    private Double expandTreeMiniMax(int[][] tableroActual, int nivel, int mark, Double alpha, Double beta) {
        if (nivel <= this.depth) {//Estoy dentro del nivel permitido
            if (BoardHelper.hasAnyMoves(tableroActual, mark)) {//Hay movimientos posibles
                ArrayList<Point> movimientos = BoardHelper.getAllPossibleMoves(tableroActual, mark);
                ArrayList<int[][]> hijos = new ArrayList<>();
                ArrayList<Double> puntuaciones = new ArrayList<>();
                for (Point m : movimientos) {
                    hijos.add(BoardHelper.getNewBoardAfterMove(tableroActual, m, mark));
                }
                if (mark == myMark) { // CASO MAX
                    return max(hijos,movimientos,puntuaciones,nivel,alpha,beta);
                } else { //PODA MIN
                    return min(hijos,puntuaciones,nivel,alpha,beta);
                }
            } else {//Estoy dentro de nivel permitido pero no hay movimientos
                return vencedor(tableroActual);
            }
        } else {//Me he colado de profundidad
            return vencedor(tableroActual);
        }
    }


    /**
     * Método que encuentra el valor máximo de una lista de movimientos de un jugador.
     *
     * @param hijos        Lista de matrices bidimensionales que representan los posibles estados del juego.
     * @param movimientos  Lista de objetos Point que representan los movimientos posibles.
     * @param puntuaciones Lista de enteros que almacena las puntuaciones calculadas para cada movimiento.
     * @param nivel        Entero que indica el nivel actual en el árbol de decisiones.
     * @param alpha        Valor alfa para la poda alfa-beta.
     * @param beta         Valor beta para la poda alfa-beta.
     * @return La mayor puntuación encontrada para el jugador maximizador.
     */
    Double max(ArrayList<int[][]> hijos, ArrayList<Point> movimientos, ArrayList<Double> puntuaciones, int nivel, Double alpha, Double beta) {
        double V = -101; // Valor inicial muy bajo para comparar máximos.

        // Itera sobre todos los hijos (posibles estados del juego).
        for (int[][] h : hijos) {
            puntuaciones.add(expandTreeMiniMax(h, nivel + 1, oponente, alpha, beta)); // Expande el árbol y calcula la puntuación.

            // Actualiza V si se encuentra una puntuación mayor.
            if (puntuaciones.get(puntuaciones.size() - 1) > V)
                V = puntuaciones.get(puntuaciones.size() - 1);

            // Actualiza alpha si la nueva V es mayor.
            if (V >= alpha) {
                alpha = V;
            }

            // Si alpha es mayor o igual a beta y el nivel es mayor a 1, poda el resto del árbol.
            if (alpha >= beta && nivel > 1) return V;
        }

        // Encuentra la mayor puntuación entre las calculadas.
        double mayorPuntuacion = -101;
        int indiceMayorPuntuacion = -1;
        for (int i = 0; i < puntuaciones.size(); i++) {
            if (puntuaciones.get(i) > mayorPuntuacion) {
                mayorPuntuacion = puntuaciones.get(i);
                indiceMayorPuntuacion = i;
            }
        }

        // Si es el primer nivel, guarda el movimiento asociado a la mayor puntuación.
        if (nivel == 1) jugada = movimientos.get(indiceMayorPuntuacion);

        return mayorPuntuacion;
    }

    /**
     * Método que encuentra el valor mínimo de una lista de movimientos de un jugador.
     *
     * @param hijos        Lista de matrices bidimensionales que representan los posibles estados del juego.
     * @param puntuaciones Lista de enteros que almacena las puntuaciones calculadas para cada movimiento.
     * @param nivel        Entero que indica el nivel actual en el árbol de decisiones.
     * @param alpha        Valor alfa para la poda alfa-beta.
     * @param beta         Valor beta para la poda alfa-beta.
     * @return La menor puntuación encontrada para el jugador minimizador.
     */
    Double min(ArrayList<int[][]> hijos, ArrayList<Double> puntuaciones, int nivel, Double alpha, Double beta) {
        double V = 101; // Valor inicial muy alto para comparar mínimos.

        // Itera sobre todos los hijos (posibles estados del juego).
        for (int[][] h : hijos) {
            puntuaciones.add(expandTreeMiniMax(h, nivel + 1, myMark, alpha, beta)); // Expande el árbol y calcula la puntuación.

            // Actualiza V si se encuentra una puntuación menor.
            if (puntuaciones.get(puntuaciones.size() - 1) < V)
                V = puntuaciones.get(puntuaciones.size() - 1);

            // Actualiza beta si la nueva V es menor.
            if (V <= beta) {
                beta = V;
            }

            // Si alpha es mayor o igual a beta y el nivel es mayor a 1, poda el resto del árbol.
            if (alpha >= beta && nivel > 1) return V;
        }

        // Encuentra la menor puntuación entre las calculadas.
        double menorPuntuacion = 101;
        for (int i = 0; i < puntuaciones.size(); i++) {
            if (puntuaciones.get(i) < menorPuntuacion) {
                menorPuntuacion = puntuaciones.get(i);
            }
        }

        return menorPuntuacion;
    }

    double vencedor(int[][] tableroActual) {
        if (BoardHelper.getWinner(tableroActual) == myMark) {
            return 100;
        } else if (BoardHelper.getWinner(tableroActual) == oponente) {
            return -100;
        } else if (BoardHelper.getWinner(tableroActual) == 0) {
            return 0;
        } else {
            return heuristica(tableroActual);
        }
    }

    private double heuristica(int [][] tableroActual){
        double Porcentaje_Puntuacion=BoardHelper.getPlayerStoneCount(tableroActual,myMark)/BoardHelper.getTotalStoneCount(tableroActual);
        ArrayList<Point> Movimientos_Mios=BoardHelper.getAllPossibleMoves(tableroActual,myMark);
        ArrayList<Point> Movimientos_Oponente=BoardHelper.getAllPossibleMoves(tableroActual,oponente);
        double Porcentaje_Movimientos=(double)Movimientos_Mios.size()/((double)Movimientos_Mios.size()+Movimientos_Oponente.size());
        return Peso_Puntuacion *Porcentaje_Puntuacion+ Peso_Movimiento *Porcentaje_Movimientos;
    }

    @Override
    public Point play(int[][] board) {
        jugada = null;
        if (BoardHelper.hasAnyMoves(board, myMark)) {
            //Comienzo expansión
            Double mejorPuntuacion = expandTreeMiniMax(board, 1, myMark,-101.0,101.0);
            System.out.println("La mejor puntuacion expandida es " + mejorPuntuacion);
        }
        return jugada;
    }
}
