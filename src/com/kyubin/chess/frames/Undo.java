package com.kyubin.chess.frames;

import com.kyubin.chess.Main;
import com.kyubin.chess.functions.move.analysis.Move;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.kyubin.chess.object.ChessGame.game;

public class Undo extends JFrame {
    public Undo(int size){
        setSize(size,size);
        setLocation(1000, Main.stockfish_y_size+50);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JButton undo_button = new JButton("undo");
        undo_button.setSize(size,size);
        undo_button.setLocation(0,0);
        undo_button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(game.size()!=0){
                    Move.UndoPiece();
                }
            }
        });
        getContentPane().add(undo_button);
        setVisible(true);
    }
}
