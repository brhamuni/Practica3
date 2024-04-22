package game;

import java.awt.*;
import java.util.ArrayList;

/**
 * Clase que contiene utilidades para proporcionar funcionalidades comunes necesarias para el juego, como verificar el estado del juego,
 * calcular movimientos válidos, y más.
 */
public class BoardHelper {

    /**
     * Verifica si el juego ha terminado.
     *
     * @param board El tablero de juego actual.
     * @return true si el juego ha terminado, false en caso contrario.
     */
    public static boolean isGameFinished(int[][] board){
        return !(hasAnyMoves(board,1) || hasAnyMoves(board,2));
    }


    /**
     * Genera el tablero de inicio con la configuración inicial de las piezas.
     *
     * @return Un tablero de juego inicializado.
     */
    public static int[][] getStartBoard(){
        int[][] b = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                b[i][j] = 0;
            }
        }
        b[3][3] = 2;
        b[3][4] = 1;
        b[4][3] = 1;
        b[4][4] = 2;
        return b;
    }

    /**
     * Identifica el movimiento realizado entre dos estados del tablero.
     *
     * @param before El tablero antes del movimiento.
     * @param after El tablero después del movimiento.
     * @return La posición del movimiento realizado.
     */
    public static Point getMove(int[][] before , int[][] after){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(before[i][j]==0 && after[i][j]!=0){
                    return new Point(i,j);
                }
            }
        }
        return null;
    }

    /**
     * Determina el ganador del juego basado en el conteo de piezas.
     *
     * @param board El tablero de juego actual.
     * @return 1 si gana el jugador 1, 2 si gana el jugador 2, 0 en caso de empate, -1 si el juego no ha terminado.
     */
    public static int getWinner(int[][] board){
        if(!isGameFinished(board))
            //game not finished
            return -1;
        else{
            //count stones
            int p1s = getPlayerStoneCount(board,1);
            int p2s = getPlayerStoneCount(board,2);

            if(p1s == p2s){
                //tie
                return 0;
            }else if(p1s > p2s){
                //p1 wins
                return 1;
            }else{
                //p2 wins
                return 2;
            }
        }
    }

    /**
     * Calcula el total de piezas en el tablero.
     *
     * @param board El tablero de juego actual.
     * @return El número total de piezas.
     */
    public static int getTotalStoneCount(int[][] board){
        int c = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(board[i][j] != 0) c++;
            }
        }
        return c;
    }

    /**
     * Cuenta el número de piezas de un jugador específico.
     *
     * @param board El tablero de juego actual.
     * @param player El jugador del cual contar las piezas.
     * @return El conteo de piezas del jugador.
     */
    public static int getPlayerStoneCount(int[][] board, int player){
        int score = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(board[i][j] == player) score++;
            }
        }
        return score;
    }

    /**
     * Verifica si un jugador tiene movimientos válidos disponibles.
     *
     * @param board El tablero de juego actual.
     * @param player El jugador a verificar.
     * @return true si hay al menos un movimiento válido, false en caso contrario.
     */
    public static boolean hasAnyMoves(int[][] board, int player){
        return getAllPossibleMoves(board,player).size() > 0;
    }


    /**
     * Obtiene una lista de todos los movimientos válidos para un jugador.
     *
     * @param board El tablero de juego actual.
     * @param player El jugador a verificar.
     * @return Una lista de puntos representando movimientos válidos.
     */
    public static ArrayList<Point> getAllPossibleMoves(int[][] board, int player){
        ArrayList<Point> result = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(canPlay(board,player,i,j)){
                    result.add(new Point(i,j));
                }
            }
        }
        return result;
    }

    /**
     * Calcula los puntos que serían volteados si se realiza un movimiento en una posición específica.
     *
     * @param board El tablero de juego actual.
     * @param player El jugador que realiza el movimiento.
     * @param i La fila del movimiento.
     * @param j La columna del movimiento.
     * @return Una lista de puntos que serían volteados.
     */
    public static ArrayList<Point> getReversePoints(int[][] board,int player,int i,int j){

        ArrayList<Point> allReversePoints = new ArrayList<>();

        int mi , mj , c;
        int oplayer = ((player == 1) ? 2 : 1);

        //move up
        ArrayList<Point> mupts = new ArrayList<>();
        mi = i - 1;
        mj = j;
        while(mi>0 && board[mi][mj] == oplayer){
            mupts.add(new Point(mi,mj));
            mi--;
        }
        if(mi>=0 && board[mi][mj] == player && mupts.size()>0){
            allReversePoints.addAll(mupts);
        }


        //move down
        ArrayList<Point> mdpts = new ArrayList<>();
        mi = i + 1;
        mj = j;
        while(mi<7 && board[mi][mj] == oplayer){
            mdpts.add(new Point(mi,mj));
            mi++;
        }
        if(mi<=7 && board[mi][mj] == player && mdpts.size()>0){
            allReversePoints.addAll(mdpts);
        }

        //move left
        ArrayList<Point> mlpts = new ArrayList<>();
        mi = i;
        mj = j - 1;
        while(mj>0 && board[mi][mj] == oplayer){
            mlpts.add(new Point(mi,mj));
            mj--;
        }
        if(mj>=0 && board[mi][mj] == player && mlpts.size()>0){
            allReversePoints.addAll(mlpts);
        }

        //move right
        ArrayList<Point> mrpts = new ArrayList<>();
        mi = i;
        mj = j + 1;
        while(mj<7 && board[mi][mj] == oplayer){
            mrpts.add(new Point(mi,mj));
            mj++;
        }
        if(mj<=7 && board[mi][mj] == player && mrpts.size()>0){
            allReversePoints.addAll(mrpts);
        }

        //move up left
        ArrayList<Point> mulpts = new ArrayList<>();
        mi = i - 1;
        mj = j - 1;
        while(mi>0 && mj>0 && board[mi][mj] == oplayer){
            mulpts.add(new Point(mi,mj));
            mi--;
            mj--;
        }
        if(mi>=0 && mj>=0 && board[mi][mj] == player && mulpts.size()>0){
            allReversePoints.addAll(mulpts);
        }

        //move up right
        ArrayList<Point> murpts = new ArrayList<>();
        mi = i - 1;
        mj = j + 1;
        while(mi>0 && mj<7 && board[mi][mj] == oplayer){
            murpts.add(new Point(mi,mj));
            mi--;
            mj++;
        }
        if(mi>=0 && mj<=7 && board[mi][mj] == player && murpts.size()>0){
            allReversePoints.addAll(murpts);
        }

        //move down left
        ArrayList<Point> mdlpts = new ArrayList<>();
        mi = i + 1;
        mj = j - 1;
        while(mi<7 && mj>0 && board[mi][mj] == oplayer){
            mdlpts.add(new Point(mi,mj));
            mi++;
            mj--;
        }
        if(mi<=7 && mj>=0 && board[mi][mj] == player && mdlpts.size()>0){
            allReversePoints.addAll(mdlpts);
        }

        //move down right
        ArrayList<Point> mdrpts = new ArrayList<>();
        mi = i + 1;
        mj = j + 1;
        while(mi<7 && mj<7 && board[mi][mj] == oplayer){
            mdrpts.add(new Point(mi,mj));
            mi++;
            mj++;
        }
        if(mi<=7 && mj<=7 && board[mi][mj] == player && mdrpts.size()>0){
            allReversePoints.addAll(mdrpts);
        }

        return allReversePoints;
    }


    /**
     * Verifica si un movimiento es válido para un jugador en una posición específica.
     *
     * @param board El tablero de juego actual.
     * @param player El jugador a verificar.
     * @param i La fila del movimiento propuesto.
     * @param j La columna del movimiento propuesto.
     * @return true si el movimiento es válido, false en caso contrario.
     */
    public static boolean canPlay(int[][] board,int player,int i,int j){

        if(board[i][j] != 0) return false;

        int mi , mj , c;
        int oplayer = ((player == 1) ? 2 : 1);

        //move up
        mi = i - 1;
        mj = j;
        c = 0;
        while(mi>0 && board[mi][mj] == oplayer){
            mi--;
            c++;
        }
        if(mi>=0 && board[mi][mj] == player && c>0) return true;


        //move down
        mi = i + 1;
        mj = j;
        c = 0;
        while(mi<7 && board[mi][mj] == oplayer){
            mi++;
            c++;
        }
        if(mi<=7 && board[mi][mj] == player && c>0) return true;

        //move left
        mi = i;
        mj = j - 1;
        c = 0;
        while(mj>0 && board[mi][mj] == oplayer){
            mj--;
            c++;
        }
        if(mj>=0 && board[mi][mj] == player && c>0) return true;

        //move right
        mi = i;
        mj = j + 1;
        c = 0;
        while(mj<7 && board[mi][mj] == oplayer){
            mj++;
            c++;
        }
        if(mj<=7 && board[mi][mj] == player && c>0) return true;

        //move up left
        mi = i - 1;
        mj = j - 1;
        c = 0;
        while(mi>0 && mj>0 && board[mi][mj] == oplayer){
            mi--;
            mj--;
            c++;
        }
        if(mi>=0 && mj>=0 && board[mi][mj] == player && c>0) return true;

        //move up right
        mi = i - 1;
        mj = j + 1;
        c = 0;
        while(mi>0 && mj<7 && board[mi][mj] == oplayer){
            mi--;
            mj++;
            c++;
        }
        if(mi>=0 && mj<=7 && board[mi][mj] == player && c>0) return true;

        //move down left
        mi = i + 1;
        mj = j - 1;
        c = 0;
        while(mi<7 && mj>0 && board[mi][mj] == oplayer){
            mi++;
            mj--;
            c++;
        }
        if(mi<=7 && mj>=0 && board[mi][mj] == player && c>0) return true;

        //move down right
        mi = i + 1;
        mj = j + 1;
        c = 0;
        while(mi<7 && mj<7 && board[mi][mj] == oplayer){
            mi++;
            mj++;
            c++;
        }
        if(mi<=7 && mj<=7 && board[mi][mj] == player && c>0) return true;

        //when all hopes fade away
        return false;
    }

    /**
     * Actualiza el tablero realizando un movimiento específico para un jugador.
     *
     * @param board El tablero de juego actual.
     * @param move La posición del movimiento.
     * @param player El jugador que realiza el movimiento.
     * @return Un nuevo tablero reflejando el movimiento realizado.
     */
    public static int[][] getNewBoardAfterMove(int[][] board, Point move , int player){
        //get clone of old board
        int[][] newboard = new int[8][8];
        for (int k = 0; k < 8; k++) {
            for (int l = 0; l < 8; l++) {
                newboard[k][l] = board[k][l];
            }
        }

        //place piece
        newboard[move.x][move.y] = player;
        //reverse pieces
        ArrayList<Point> rev = BoardHelper.getReversePoints(newboard,player,move.x,move.y);
        for(Point pt : rev){
            newboard[pt.x][pt.y] = player;
        }

        return newboard;
    }


   
}
