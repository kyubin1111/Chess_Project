package com.kyubin.chess.functions;

import com.kyubin.chess.Main;
import com.kyubin.chess.functions.move.*;
import com.kyubin.chess.functions.move.analysis.Move;
import com.kyubin.chess.functions.move.king.King;
import com.kyubin.chess.functions.move.pawn.Pawn;
import com.kyubin.chess.functions.move.pawn.promotion.Promotion;
import com.kyubin.chess.functions.move.pawn.promotion.PromotionType;
import com.kyubin.chess.functions.move.pawn.xyplus.XYPlus;
import com.kyubin.chess.object.ChessGame;
import com.kyubin.chess.object.Piece;
import com.kyubin.chess.object.xy.XY;

import javax.swing.*;
import java.awt.*;

import static com.kyubin.chess.functions.BasicFunctions.sortObject;
import static com.kyubin.chess.functions.BasicFunctions.sortXY;
import static com.kyubin.chess.object.ChessGame.*;

public class PieceFunctions {
    // 기물을 잡고있는지 보는 변수
    public static boolean grab_object=false;

    public static boolean is_promotion=false;

    static XY xy=new XY(0,0);

    // 잡기(Grab) 함수
    public static void grab(JLabel object, String object_type, int chess_object_size){
        boolean is_white=object_type.contains("white");

        if(is_promotion) return;

        // 만약 내 턴이 아니라면 함수 종료
        if(ChessGame.white_turn&&!is_white) return;
        if(!ChessGame.white_turn&&is_white) return;
        grab_object=!grab_object;
        if(!grab_object) return;
        else xy=sortXY(chess_object_size,object.getX(),object.getY());

        Main.frame.getContentPane().setComponentZOrder(object,0);

        new Thread(()->{
            while(grab_object){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                object.setLocation((int) (MouseInfo.getPointerInfo().getLocation().x-chess_object_size/1.8 - Main.frame.getX())
                        , MouseInfo.getPointerInfo().getLocation().y - chess_object_size - Main.frame.getY());
            }

            // 정렬
            XY xy_now=sortObject(chess_object_size,MouseInfo.getPointerInfo().getLocation().x - Main.frame.getX(),MouseInfo.getPointerInfo().getLocation().y - Main.frame.getY());

            // 갈수 있는 칸인지 체크
            boolean is_en_passant=false;

            boolean isItPossible=false;

            boolean is_king_side_castling=false;
            boolean is_queen_side_castling=false;

            if(object_type.contains("rook")){
                for(XY moves: Rook.rookMoves(xy,is_white,false)){
                    if(moves.x==xy_now.x&&moves.y==xy_now.y){
                        isItPossible=true;
                        break;
                    }
                }
            }
            if(object_type.contains("knight")){
                for(XY moves: Knight.knightMoves(xy,is_white,false)){
                    if(moves.x==xy_now.x&&moves.y==xy_now.y){
                        isItPossible=true;
                        break;
                    }
                }
            }
            if(object_type.contains("bishop")){
                for(XY moves: Bishop.bishopMoves(xy,is_white,false)){
                    if(moves.x==xy_now.x&&moves.y==xy_now.y){
                        isItPossible=true;
                        break;
                    }
                }
            }
            if(object_type.contains("queen")){
                for(XY moves: Queen.queenMoves(xy,is_white,false)){
                    if(moves.x==xy_now.x&&moves.y==xy_now.y){
                        isItPossible=true;
                        break;
                    }
                }
            }
            if(object_type.contains("pawn")){
                for(XYPlus moves: Pawn.pawnMoves(xy,is_white,false)){
                    if(moves.xy.x==xy_now.x&&moves.xy.y==xy_now.y){
                        isItPossible=true;

                        if(xy_now.y==(is_white?1:8)){
                            is_promotion=true;
                        }
                        if(moves.is_en_passant){
                            is_en_passant=true;
                        }

                        break;
                    }
                }
            }
            if(object_type.contains("king")){
                for(XY moves: King.kingMoves(xy,is_white,false)){
                    if(moves.x==xy_now.x&&moves.y==xy_now.y){
                        if(xy.x+2==xy_now.x) is_king_side_castling=true;
                        if(xy.x-2==xy_now.x) is_queen_side_castling=true;

                        isItPossible=true;
                        break;
                    }
                }
            }

            if(is_promotion){
                promotion(xy_now,is_white,null);

                object.setLocation((xy_now.x-1)*chess_object_size,(xy_now.y-1)*chess_object_size);

                new Thread(()->{
                    do {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                    } while (get_promotion_type == null);

                    if(get_promotion_type!=PromotionType.NONE){
                        Move.MovePromotionPiece(is_white,xy,xy_now,get_promotion_type,object);
                    } else {
                        object.setLocation((xy.x-1)*chess_object_size,(xy.y-1)*chess_object_size);
                        JLabel a1 = new JLabel();
                        a1.setSize(1, 1);
                        a1.setLocation(1000, 100);
                    }

                    get_promotion_type=null;
                }).start();
            } else {
                if(isItPossible){
                    Move.MovePiece(is_white,xy,xy_now,is_king_side_castling,is_queen_side_castling,is_en_passant,object_type,object);
                } else {
                    object.setLocation((xy.x-1)*chess_object_size,(xy.y-1)*chess_object_size);
                    JLabel a1 = new JLabel();
                    a1.setSize(1, 1);
                    a1.setLocation(1000, 100);
                }
            }

            is_promotion=false;
        }).start();
    }

    public static String xyToStockFish(XY xy){
        int x=xy.x;
        int y=xy.y;

        String stringX="";
        if(x==1) stringX="a";
        if(x==2) stringX="b";
        if(x==3) stringX="c";
        if(x==4) stringX="d";
        if(x==5) stringX="e";
        if(x==6) stringX="f";
        if(x==7) stringX="g";
        if(x==8) stringX="h";

        return stringX+(9-y);
    }

    static PromotionType get_promotion_type=null;

    public static void promotion(XY xy,boolean is_white, PromotionType promotionType){
        new Thread(() -> {
            if(promotionType==null){
                Promotion.getPromotionType(xy,is_white);
            } else {
                Promotion.promotionType=promotionType;
            }

            while (true) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (Promotion.promotionType != null) {
                    if(Promotion.promotionType!=PromotionType.NONE){
                        removeEnemy(xy,!is_white);
                    }
                    if(Promotion.promotionType==PromotionType.QUEEN){
                        Move.addPiece(is_white ? "white_queen":"black_queen",xy);
                    }
                    if(Promotion.promotionType==PromotionType.KNIGHT){
                        Move.addPiece(is_white ? "white_knight":"black_knight",xy);
                    }
                    if(Promotion.promotionType==PromotionType.ROOK){
                        Move.addPiece(is_white ? "white_rook":"black_rook",xy);
                    }
                    if(Promotion.promotionType==PromotionType.BISHOP){
                        Move.addPiece(is_white ? "white_bishop":"black_bishop",xy);
                    }

                    get_promotion_type=Promotion.promotionType;

                    Promotion.promotionType=null;
                    break;
                }
            }
        }).start();
    }

    public static void removePiece(XY xy, String object_type){
        if(object_type.equals("white_rook")){
            for(Piece piece: ChessGame.white_rook){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    ChessGame.white_rook.remove(piece);
                    piece.label.setLocation(-100,-100);
                    break;
                }
            }
        }
        if(object_type.equals("black_rook")){
            for(Piece piece: ChessGame.black_rook){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    ChessGame.black_rook.remove(piece);
                    piece.label.setLocation(-100,-100);
                    break;
                }
            }
        }
        if(object_type.equals("white_knight")){
            for(Piece piece: ChessGame.white_knight){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    ChessGame.white_knight.remove(piece);
                    piece.label.setLocation(-100,-100);
                    break;
                }
            }
        }
        if(object_type.equals("black_knight")){
            for(Piece piece: ChessGame.black_knight){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    ChessGame.black_knight.remove(piece);
                    piece.label.setLocation(-100,-100);
                    break;
                }
            }
        }
        if(object_type.equals("white_bishop")){
            for(Piece piece: ChessGame.white_bishop){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    ChessGame.white_bishop.remove(piece);
                    piece.label.setLocation(-100,-100);
                    break;
                }
            }
        }
        if(object_type.equals("black_bishop")){
            for(Piece piece: ChessGame.black_bishop){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    ChessGame.black_bishop.remove(piece);
                    piece.label.setLocation(-100,-100);
                    break;
                }
            }
        }
        if(object_type.equals("white_queen")){
            for(Piece piece: white_queen){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    white_queen.remove(piece);
                    piece.label.setLocation(-100,-100);
                    break;
                }
            }
        }
        if(object_type.equals("black_queen")){
            for(Piece piece: black_queen){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    black_queen.remove(piece);
                    piece.label.setLocation(-100,-100);
                    break;
                }
            }
        }
        if(object_type.equals("white_pawn")){
            for(Piece piece: ChessGame.white_pawn){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    ChessGame.white_pawn.remove(piece);
                    piece.label.setLocation(-100,-100);
                    break;
                }
            }
        }
        if(object_type.equals("black_pawn")){
            for(Piece piece: ChessGame.black_pawn){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    ChessGame.black_pawn.remove(piece);
                    piece.label.setLocation(-100,-100);
                    break;
                }
            }
        }
    }

    public static String removeEnemy(XY xy, boolean is_white){
        if(is_white){
            for(Piece piece: ChessGame.black_rook){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    ChessGame.black_rook.remove(piece);
                    piece.label.setLocation(-100,-100);
                    return "black_rook";
                }
            }
            for(Piece piece: ChessGame.black_knight){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    ChessGame.black_knight.remove(piece);
                    piece.label.setLocation(-100,-100);
                    return "black_knight";
                }
            }
            for(Piece piece: ChessGame.black_bishop){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    ChessGame.black_bishop.remove(piece);
                    piece.label.setLocation(-100,-100);
                    return "black_bishop";
                }
            }
            for(Piece piece: black_queen){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    black_queen.remove(piece);
                    piece.label.setLocation(-100,-100);
                    return "black_queen";
                }
            }
            for(Piece piece: ChessGame.black_pawn){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    ChessGame.black_pawn.remove(piece);
                    piece.label.setLocation(-100,-100);
                    return "black_pawn";
                }
            }
        } else {
            for(Piece piece: ChessGame.white_rook){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    ChessGame.white_rook.remove(piece);
                    piece.label.setLocation(-100,-100);
                    return "white_rook";
                }
            }
            for(Piece piece: ChessGame.white_knight){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    ChessGame.white_knight.remove(piece);
                    piece.label.setLocation(-100,-100);
                    return "white_knight";
                }
            }
            for(Piece piece: ChessGame.white_bishop){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    ChessGame.white_bishop.remove(piece);
                    piece.label.setLocation(-100,-100);
                    return "white_bishop";
                }
            }
            for(Piece piece: white_queen){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    white_queen.remove(piece);
                    piece.label.setLocation(-100,-100);
                    return "white_queen";
                }
            }
            for(Piece piece: ChessGame.white_pawn){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    ChessGame.white_pawn.remove(piece);
                    piece.label.setLocation(-100,-100);
                    return "white_pawn";
                }
            }
        }

        return "";
    }


    public static Piece removeEnemyWithPiece(XY xy, boolean is_white){
        if(is_white){
            for(Piece piece: ChessGame.black_rook){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    ChessGame.black_rook.remove(piece);
                    return piece;
                }
            }
            for(Piece piece: ChessGame.black_knight){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    ChessGame.black_knight.remove(piece);
                    return piece;
                }
            }
            for(Piece piece: ChessGame.black_bishop){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    ChessGame.black_bishop.remove(piece);
                    return piece;
                }
            }
            for(Piece piece: black_queen){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    black_queen.remove(piece);
                    return piece;
                }
            }
            for(Piece piece: ChessGame.black_pawn){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    ChessGame.black_pawn.remove(piece);
                    return piece;
                }
            }
        } else {
            for(Piece piece: ChessGame.white_rook){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    ChessGame.white_rook.remove(piece);
                    return piece;
                }
            }
            for(Piece piece: ChessGame.white_knight){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    ChessGame.white_knight.remove(piece);
                    return piece;
                }
            }
            for(Piece piece: ChessGame.white_bishop){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    ChessGame.white_bishop.remove(piece);
                    return piece;
                }
            }
            for(Piece piece: white_queen){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    white_queen.remove(piece);
                    return piece;
                }
            }
            for(Piece piece: ChessGame.white_pawn){
                if(piece.xy.x==xy.x&&piece.xy.y==xy.y){
                    ChessGame.white_pawn.remove(piece);
                    return piece;
                }
            }
        }

        return null;
    }

    public static boolean isPieceInSquare(XY xy){
        for(Piece piece:ChessGame.white_rook){
            if(piece.xy.equals(xy)){
                return true;
            }
        }
        for(Piece piece:ChessGame.black_rook){
            if(piece.xy.equals(xy)){
                return true;
            }
        }
        for(Piece piece:ChessGame.white_knight){
            if(piece.xy.equals(xy)){
                return true;
            }
        }
        for(Piece piece:ChessGame.black_knight){
            if(piece.xy.equals(xy)){
                return true;
            }
        }
        for(Piece piece:ChessGame.white_bishop){
            if(piece.xy.equals(xy)){
                return true;
            }
        }
        for(Piece piece:ChessGame.black_bishop){
            if(piece.xy.equals(xy)){
                return true;
            }
        }
        for(Piece piece: white_queen){
            if(piece.xy.equals(xy)){
                return true;
            }
        }
        for(Piece piece: black_queen){
            if(piece.xy.equals(xy)){
                return true;
            }
        }
        for(Piece piece:ChessGame.white_pawn){
            if(piece.xy.equals(xy)){
                return true;
            }
        }
        for(Piece piece:ChessGame.black_pawn){
            if(piece.xy.equals(xy)){
                return true;
            }
        }
        if(ChessGame.white_king.xy.equals(xy)){
            return true;
        }
        return ChessGame.black_king.xy.equals(xy);
    }
}
