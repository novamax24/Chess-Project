import java.awt.Image;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.List;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.*;


public class Board extends JPanel implements MouseListener, MouseMotionListener {
    
    private ChessWindow g;
    private String whiteBishop = "wbishop.png";
	private String blackBishop = "bbishop.png";
	private String whiteKnight = "wknight.png";
	private String blackKnight = "bknight.png";
	private String whiteRook = "wrook.png";
	
    private String blackRook = "brook.png";
	private String whiteKing = "wking.png";
	private String blackKing = "bking.png";
	private String blackQueen = "bqueen.png";
	private String whiteQueen = "wqueen.png";
	private String whitePawn = "wpawn.png";
	private String blackPawn = "bpawn.png";
	
	private Square[][] board;
    
    
    public LinkedList<Piece> pBlack;
    public LinkedList<Piece> pWhite;
    public List<Square> movable;
    
    private boolean whiteTurn;

    private Piece currentPiece;
    private int currX;
    private int currY;
    
    private CheckmateCheck cmc;
    

    //creates new 8 by 8 2D array of squares to represent a board
    // 
    public Board(ChessWindow game) {
        g = game;
        
        board = new Square[8][8];
        //List of all of black and white's pieces
        pBlack = new LinkedList<Piece>();
        pWhite = new LinkedList<Piece>();
        setLayout(new GridLayout(8, 8, 0, 0));

        //allows mouse to interact with the board
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        //create the new squares with their respective colors
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {


                if (((x % 2) == 0 && (y % 2) == 0) || ((x % 2) == 1 && (y % 2) == 1)) {
                    board[x][y] = new Square(this, true, y, x);
                    this.add(board[x][y]);
                } 
                
                else {
                    board[x][y] = new Square(this, false, y, x);
                    this.add(board[x][y]);
                }
            }
        }
        
        this.setMinimumSize(this.getPreferredSize());
        this.setSize(new Dimension(1040, 1040));
        createPieces();

        this.setPreferredSize(new Dimension(1040, 1040));
        this.setMaximumSize(new Dimension(1040, 1040));
        

        whiteTurn = true;

    }

    //returns the chesswindow
    public ChessWindow getChessWindow() {
        return g;
    }

    private void createPieces() {
    	//runs through the board adding pieces in specific areas
        for (int x = 0; x < 8; x++) {
            board[1][x].put(new Pawn(0, board[1][x], blackPawn));
            board[6][x].put(new Pawn(1, board[6][x], whitePawn));
        }
        
        board[7][3].put(new Queen(1, board[7][3], whiteQueen));
        board[0][3].put(new Queen(0, board[0][3], blackQueen));
        
        King bk = new King(0, board[0][4], blackKing);
        King wk = new King(1, board[7][4], whiteKing);
        board[0][4].put(bk);
        board[7][4].put(wk);

        board[0][0].put(new Rook(0, board[0][0], blackRook));
        board[0][7].put(new Rook(0, board[0][7], blackRook));
        board[7][0].put(new Rook(1, board[7][0], whiteRook));
        board[7][7].put(new Rook(1, board[7][7], whiteRook));

        board[0][1].put(new Knight(0, board[0][1], blackKnight));
        board[0][6].put(new Knight(0, board[0][6], blackKnight));
        board[7][1].put(new Knight(1, board[7][1], whiteKnight));
        board[7][6].put(new Knight(1, board[7][6], whiteKnight));

        board[0][2].put(new Bishop(0, board[0][2], blackBishop));
        board[0][5].put(new Bishop(0, board[0][5], blackBishop));
        board[7][2].put(new Bishop(1, board[7][2], whiteBishop));
        board[7][5].put(new Bishop(1, board[7][5], whiteBishop));
        
        //fills up pBlack and pWhite with current pieces
        for(int y = 0; y < 2; y++) {
            for (int x = 0; x < 8; x++) {
                pBlack.add(board[y][x].getOccupyingPiece());
                pWhite.add(board[7-y][x].getOccupyingPiece());
            }
        }
        
        //initializes new CheckmateCheck object passing 
        cmc = new CheckmateCheck(this, pWhite, pBlack, wk, bk);
    }

    
    //setters and getters of instance variables
    public boolean getTurn() {
        return whiteTurn;
    }

    public void setCurrPiece(Piece p) {
        this.currentPiece = p;
    }

    public Piece getCurrPiece() {
        return this.currentPiece;
    }

    public Square[][] getBoardArray() {
        return this.board;
    }

    //draw the board
    public void paintComponent(Graphics g) {
        
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Square sq = board[y][x];
                sq.paintComponent(g);
            }
        }

        if (currentPiece != null) {
            if ((currentPiece.getColor() == 1 && whiteTurn) || (currentPiece.getColor() == 0 && !whiteTurn)) {
                final Image i = currentPiece.getImage();
                g.drawImage(i, currX - 13, currY - 13, 100, 100, null);
            }
        }
    }


    public void mousePressed(MouseEvent e) {
        currX = e.getX();
        currY = e.getY();

        Square sq = (Square) this.getComponentAt(new Point(e.getX(), e.getY()));

        if (sq.isOccupied()) {
            currentPiece = sq.getOccupyingPiece();
            if (currentPiece.getColor() == 1 && !whiteTurn) {
                return;
            }
            if (currentPiece.getColor() == 0 && whiteTurn) {
                return;
            }
            
            sq.setDisplay(false);
        }

        repaint();
    }

    //when mouse is released check if the piecee can move there
    public void mouseReleased(MouseEvent e) {
        Square sq = (Square) this.getComponentAt(new Point(e.getX(), e.getY()));

        if (currentPiece != null) {
            if (currentPiece.getColor() == 0 && whiteTurn) {
                return;
            }
            if (currentPiece.getColor() == 1 && !whiteTurn) {
                return;
            }
            
            //get possible moves for the side
            List<Square> possMoves = currentPiece.getMoves(this);
            //get the legal moves if in check
            movable = cmc.getAllowableSquares(whiteTurn);
            
            //if the move is possible, legal, and will not result in check
            if (possMoves.contains(sq) && movable.contains(sq) && cmc.checkMove(currentPiece, sq)) {
                sq.setDisplay(true);
                currentPiece.move(sq);
                cmc.update();

                //if black is checkmated then stop recording mouse movement
                if (cmc.blackCheckMated()) {
                    currentPiece = null;
                    repaint();
                    this.removeMouseListener(this);
                    this.removeMouseMotionListener(this);
                    g.checkmateOccurred(0);
                } 
                
                //same with white
                else if (cmc.whiteCheckMated()) {
                    currentPiece = null;
                    repaint();
                    this.removeMouseListener(this);
                    this.removeMouseMotionListener(this);
                    g.checkmateOccurred(1);
                } 
                
                //switch to next players turn
                else {
                    currentPiece = null;
                    whiteTurn = !whiteTurn;
                    movable = cmc.getAllowableSquares(whiteTurn);
                }

            } 
            
            else {
                currentPiece.getPosition().setDisplay(true);
                currentPiece = null;
            }
        }

        repaint();
    }

    //drags the pieces with the mouse
    public void mouseDragged(MouseEvent e) {
        
        currX = e.getX() - 24;
        currY = e.getY() - 24;

        repaint();
        
    }
    

 
    //these methods are necessary but do nothing in the game
    public void mouseMoved(MouseEvent e) {
    }


    public void mouseClicked(MouseEvent e) {
    }


    public void mouseEntered(MouseEvent e) {
    }

 
    public void mouseExited(MouseEvent e) {
    }
    

}