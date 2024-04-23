package player;
import game.BoardHelper;
import java.awt.*;
import java.util.ArrayList;

public class MiniMaxPlayer extends GamePlayer {
    private Point jugada;
    private final int oponente;
    public MiniMaxPlayer(int mark, int depth) {
        super(mark, depth);
        if(mark==1)
            oponente =2;
        else
            oponente = 1;
    }

    @Override
    public boolean isUserPlayer() {
        return false;
    }

    @Override
    public String playerName() {
        return "MiniMax Player";
    }

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

    int max(ArrayList<int[][]> hijos, ArrayList<Point> movimientos, ArrayList<Integer> puntuaciones, int nivel){
        //Recorro todos los hijos expandiendo hasta que me pase de nivel
        for(int[][] h : hijos){
            puntuaciones.add(expandTreeMiniMax(h,nivel+1, oponente));
        }
        int dev=-101;
        int indDev=-1;

        //Me quedo con la puntuacion mas alta y actualizo la jugada con el movimiento de la maxima puntuacion
        for(int i=0; i<puntuaciones.size(); i++){
            if(puntuaciones.get(i) > dev){
                dev=puntuaciones.get(i);
                indDev=i;
            }
        }
        if(nivel==1)
            jugada = movimientos.get(indDev);
        return dev;
    }

    int min(ArrayList<int[][]> hijos, ArrayList<Integer> puntuaciones, int nivel){
        for(int[][] h : hijos){
            puntuaciones.add(expandTreeMiniMax(h,nivel+1,myMark));
        }
        //Me quedo con la puntuacion mas baja y actualizo la jugada con el movimiento de la minima puntuacion
        int dev=101;
        for(int i: puntuaciones){
            if(i < dev){
                dev=i;
            }
        }
        return dev;
    }

    int vencedor(int[][] tableroActual){
        if(BoardHelper.getWinner(tableroActual)==myMark)
            return 100;//Compruebo si es tablero final
        else
            if(BoardHelper.getWinner(tableroActual)== oponente)
                return -100;
            else
                if(BoardHelper.getWinner(tableroActual) == 0)
                    return 0;
                else
                    return BoardHelper.getPlayerStoneCount(tableroActual,myMark)- BoardHelper.getPlayerStoneCount(tableroActual, oponente);
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
