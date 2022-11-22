/*****************************************************************************************************
 * Author:  Vaishali Anapayan
 * Date:  April 19, 2022
 * Description:  This is intended as a review of the concepts learned in the grade 11 course.  There
 *     will be loops, if structures, data types, and some object-oriented concepts.  This game is 
 *     not written with objects interacting with objects, but takes a less object-oriented approach 
 *     to focus on the basic concepts especially two-dimensional arrays.
 ****************************************************************************************************/
import javax.swing.*;
import java.awt.event.*;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.BasicStroke;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Image;
import java.util.Random;
import java.io.*; 
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.border.Border;
import java.lang.*; // files
import javax.swing.JOptionPane;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


/**************************************************************************
* AnapayanPacman class: Contains main method. Place where everything starts.
**************************************************************************/
public class AnapayanPacman
{
   static final long serialVersionUID = 2;
   
   
   /***********************************************************************
   * main Method: Instantiates the PacMan class and calls init method to 
   * start to game. Prints beginning and end game messages and stores 
   * same-game high score in highScore variable.
   ***********************************************************************/
   public static void main(String args[]) throws Exception 
   {  
      boolean startBoard = true;
      int highScore = 0;
      
      JOptionPane.showMessageDialog(null,"Welcome to Vaishali's Volumptious Pacman! The goal of the game is to eat as many dots as you can before the timer of 30 seconds runs out...", "WELCOME", JOptionPane.PLAIN_MESSAGE);
      JOptionPane.showMessageDialog(null, "If you land on the shooting star, you receive double to points for 5 seconds (while Pacman is red)...", "RULE 1", JOptionPane.PLAIN_MESSAGE);
      JOptionPane.showMessageDialog(null, "If you land on a heart, 5 seconds is added to your game time...", "RULE 2", JOptionPane.PLAIN_MESSAGE);
      
      if ((JOptionPane.showConfirmDialog(null, "Are you ready to play?", "WELCOME", JOptionPane.YES_NO_OPTION)) == 0)
      {
         while (startBoard)
         {
            PacMan gameBoard = new PacMan();
            gameBoard.setLocationRelativeTo(null);
            gameBoard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameBoard.init();
            gameBoard.dispose();
            if (highScore < gameBoard.highScore())
               highScore = gameBoard.highScore();
            if ((JOptionPane.showConfirmDialog(null, "You collected "+gameBoard.pointsEarned()+" points! Your high score is "+highScore+", would you like to play again?", "GAME OVER", JOptionPane.YES_NO_OPTION)) == 1)
               startBoard = false;
         }
      }
      System.exit(0);
   } // end main method
} // end class



/**************************************************************************
* PacMan class: Instantiates board array used for game board. Contains all 
* fields used in the contructors and methods in PacMan class. Instantiates 
* constants WIDTH and HEIGHT.
**************************************************************************/
class PacMan extends JFrame implements KeyListener
{
   static final long serialVersionUID = 2;
   BufferedImage buffer; // instance variable for double buffering
   
   char[][] board = {{'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X'},
                     {'X', 'P', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', 'X'},
                     {'X', '*', 'X', '*', 'X', '*', '*', '*', 'X', 'X', 'X', 'X', 'X', '*', 'X', 'X', 'X', 'X', '*', 'X'},
                     {'X', '*', 'X', '*', 'X', 'X', 'X', '*', '*', '*', '*', '*', 'X', '*', 'X', '*', 'T', 'X', '*', 'X'},
                     {'X', '*', 'X', '*', '*', '*', '*', '*', '*', '*', 'X', '*', 'X', '*', 'X', '*', 'X', 'X', '*', 'X'},
                     {'X', '*', 'X', 'X', 'X', '*', 'X', 'X', 'X', '*', 'X', '*', 'X', '*', '*', '*', '*', '*', '*', 'X'},
                     {'X', '*', '*', '*', 'X', '*', 'X', '*', 'X', '*', '*', '*', '*', '*', 'X', 'X', '-', '-', 'X', 'X'},
                     {'X', '*', 'X', '*', 'X', '*', 'X', '*', 'X', '*', 'X', 'X', 'X', 'X', 'X', '1', '2', '3', '4', 'X'},
                     {'X', '*', 'X', '*', 'X', '*', 'X', 'D', '*', '*', '*', '*', 'X', '*', 'X', 'X', 'X', 'X', 'X', 'X'},
                     {'X', 'T', 'X', '*', '*', '*', 'X', '*', '*', '*', 'X', '*', '*', '*', '*', '*', '*', '*', '*', 'X'},
                     {'X', '*', '*', '*', 'X', 'X', 'X', 'X', '*', '*', 'X', '*', 'X', '*', 'X', 'X', 'X', 'X', '*', 'X'},
                     {'X', '*', 'X', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', 'T', '*', 'X'},
                     {'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X'}};
                     
   char pacmanShape = 'R';
   char direction = 'R';
   int pacmanR = 1;
   int pacmanC = 1;
   int points = 0;
   boolean play = true;
   long gameStart = System.currentTimeMillis();
   int gameTime = 30000;
   int highestPoints = 0;
   String pacmanColor = "yellow";
   long startOfDoublePoints = 0;
   
   final int WIDTH = board[0].length*30, HEIGHT = board.length*30;
   JPanel boardPanel;
   
   
   /********************************************************************************
   * PacMan constructor: Sets up game board and calls KeyListener.
   ********************************************************************************/
   public PacMan()
   {
      //super ("Vaishali's Volumptious Pacman Game");
      buffer = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
      
      boardPanel = new JPanel();
      boardPanel.setDoubleBuffered(true);
      boardPanel.setPreferredSize(new Dimension(WIDTH,HEIGHT));
      boardPanel.setMinimumSize(new Dimension(WIDTH,HEIGHT));
      
      JPanel pane = (JPanel) getContentPane();
      addKeyListener(this);
      pane.add(boardPanel);
      pane.setDoubleBuffered(true);
      setSize(WIDTH+40, HEIGHT+40);
      pack();
      setLocationRelativeTo(null);
      setVisible(true);
   }
   
   
   /********************************************************************************
   * keyTyped method: KeyListener needs this, used when a key is pressed.
   ********************************************************************************/
   public void keyTyped(KeyEvent e)
   {
   }


   /********************************************************************************
   * keyPressed method: Reading and storing key directions pressed by user.
   ********************************************************************************/
   public void keyPressed(KeyEvent e)
   {
      int key = e.getKeyCode();
      if (key == KeyEvent.VK_LEFT)
         direction = 'L';
      if (key == KeyEvent.VK_RIGHT)
         direction = 'R';
      if (key == KeyEvent.VK_UP)
         direction = 'U';
      if (key == KeyEvent.VK_DOWN)
         direction = 'D';
   }


   /*********************************************************************************
   * keyReleased Method: KeyListener needs this, used when a key is realesed.
   *********************************************************************************/
   public void keyReleased(KeyEvent e)
   {
   } // End methods needed for keyListener.


   /*********************************************************************************
   * brawBoard method: Draws walls, food, ghosts, and pacman as found in board array.
   * Calls drawScreen method.
   *********************************************************************************/
   public void drawBoard()
   {
      Graphics2D b = buffer.createGraphics();
      BufferedImage boardImage = null;
      b.setColor(Color.black);
      b.fillRect(0,25,WIDTH,HEIGHT);
      for (int row=0; row<board.length ; row++)
         for(int column=0; column<board[row].length; column++)
            if(board[row][column] == 'X') // walls
            {
               b.setColor(new Color(28, 25, 224));
               b.fillRect((column)*30, (row)*30, 30, 30);
            }
            else if (board[row][column] == '*') // food
            {
               b.setColor(new Color(245, 243, 184));
               b.fillOval((column)*30+9, (row)*30+9, 10, 10);
            }
            else if (board[row][column] == '-') // gate blocking ghosts
            {
               b.setColor(new Color(52, 174, 235));
               b.setStroke(new BasicStroke(6)); // strokesize of 6 increases 3 pixels on all sides
               b.drawLine(column*30+3, row*30+12, column*30+27, row*30+12); 
            }
            else if (board[row][column] >= '1' && board[row][column] <= '6') // ghosts
            {
               addGhost(b, board[row][column], column*30, row*30);
            }
            else if (board[row][column] == 'D')
            {
               powerUpDoublePoints(b, column*30, row*30);
            }
            else if (board[row][column] == 'T')
            {
               additionalTime(b, column*30, row*30);
            }
            
            else if (board[row][column] == 'P') // draws pacman on board depending on its direction
            {
               pacmanR = row; pacmanC = column;
               if (pacmanColor.equals("yellow"))
                  b.setColor(Color.yellow);
               else
                  b.setColor(Color.red);
               if (pacmanShape == 'O')
                  b.fillArc((column)*30+3, (row)*30+3, 24, 24, 0, 365);
               else if (pacmanShape == 'R')
                  b.fillArc((column)*30+3, (row)*30+3, 24, 24, 45, 270);
               if (pacmanShape == 'L')
                  b.fillArc((column)*30+3, (row)*30+3, 24, 24, 180+45, 270);
               else if (pacmanShape == 'U')
                  b.fillArc((column)*30+3, (row)*30+3, 24, 24, 90+45, 270);
               else if (pacmanShape == 'D')
                  b.fillArc((column)*30+3, (row)*30+3, 24, 24, 270+45, 270);
            }
      b.drawImage(boardImage, 0, 15, this);
      b.dispose();
      drawScreen();
   }
   
   
   /*********************************************************************************
   * drawScreen Method: Place picture from buffer (memory) onto the screen.
   **********************************************************************************/
   public void drawScreen()
   {
      Graphics2D g = (Graphics2D)this.getGraphics();
      g.drawImage(buffer,6,30,this);
      Toolkit.getDefaultToolkit().sync();
      g.dispose();
   }
   
   
   /********************************************************************************
   * init Method: Gets called in the main method. Calls drawBoard, drawScreen, and 
   * gameLoop methods to run the pacman game.
   ********************************************************************************/
   public void init()
   {
      Graphics2D b = buffer.createGraphics();
      
      drawBoard();
   
      b.dispose();
      drawScreen();
      gameLoop();
      return;
   }
   
   
   /********************************************************************************
   * Gameloop Method: In a while loop, calls the move and drawBoard methods, allowing 
   * the game to continiously run. The while loop runs while the run time is less than 
   * the total time allowed for one game, otherwise when the run time exceeds the game 
   * time, the loop is exited and the game stops running.
   *********************************************************************************/
   public void gameLoop()
   {
      while(play)
      {
         try
         {
            move();
            drawBoard();
            Thread.sleep(100);
            if ((System.currentTimeMillis()-gameStart) <= gameTime)
               play = true;
            else 
               play = false;
         }
         catch(Exception e)
         {
            e.printStackTrace();
         }
      }
   }
   
   
   /*********************************************************************************
   * addGhost Method: Adds the four ghosts images onto game board.
   *********************************************************************************/
   public void addGhost(Graphics2D b, char ghost, int x, int y)
   {
      BufferedImage img = null;
      Image newImage = null;
      try
      {
         if (ghost == '1')
            img = ImageIO.read(new File("blueGhost.png"));
         else if (ghost == '2')
            img = ImageIO.read(new File("orangeGhost.png")); 
         else if (ghost == '3')
            img = ImageIO.read(new File("pinkGhost.png"));
         else if (ghost == '4')
            img = ImageIO.read(new File("redGhost.png"));
      
         newImage = img.getScaledInstance(24, 30, Image.SCALE_DEFAULT);
         b.drawImage(newImage, x+3, y, this);
      }
      catch(Exception e)
      {
         e.printStackTrace();
         System.exit(0);
      }
   }
   
   
   /********************************************************************************
   * powerUpDoublePoints Method: To add star image onto game board for double-points 
   * power up.
   ********************************************************************************/
   public void powerUpDoublePoints(Graphics2D b, int x, int y)
   {
      try
      {
         BufferedImage image = ImageIO.read(new File("star.png"));
         Image newImage = image.getScaledInstance(24, 30, Image.SCALE_DEFAULT);
         b.drawImage(newImage, x+3, y, this);
      }
      catch(Exception e)
      {
         e.printStackTrace();
         System.exit(0);
      }
   }
   
   
   /********************************************************************************
   * additionalTime Method: Adds heart image onto gameboard for additional-time power
   * up.
   *********************************************************************************/
   public void additionalTime(Graphics2D b, int x, int y)
   {
      try
      {
         BufferedImage im = ImageIO.read(new File("heart.jpeg"));
         Image newImage = im.getScaledInstance(24, 30, Image.SCALE_DEFAULT);
         b.drawImage(newImage, x+3, y, this);
      }
      catch(Exception e)
      {
         e.printStackTrace();
         System.exit(0);
      }
   }
   
   
   /********************************************************************************
   * move Method: Moves pacman in accordance to keys pressed and adds a point, or 
   * double points in accordance to double-points power up, when food is eaten. Adds 
   * five seconds to game time when additional-time power up is eaten. 
   *********************************************************************************/
   public void move()
   {
      if (pacmanShape == 'O')
         pacmanShape = direction;
      else
         pacmanShape = 'O';
      
      int row = 0, column = 0;
      if (direction == 'R')
      {
         row = pacmanR;
         column = pacmanC+1;
      }
      else if (direction == 'L')
      {
         row = pacmanR;
         column = pacmanC-1;
      }
      else if (direction == 'U')
      {
         row = pacmanR-1;
         column = pacmanC;
      }
      else if (direction == 'D')
      {
         row = pacmanR+1;
         column = pacmanC;
      }
      
      if (board[row][column] == 'D') // double-points power up
         startOfDoublePoints = System.currentTimeMillis();
      if (board[row][column] == 'T') // additional-time power up
         gameTime+=5000;
         
      if (board[row][column] != 'X') // wall
      {  
         if (board[row][column] == '*') // food
            if ((System.currentTimeMillis()-startOfDoublePoints) <= 5000)
            {
               pacmanColor = "red"; // turns pacman red during double-points power up
               points+=2;
            }
            else
            {
               pacmanColor = "yellow";
               points++;
            }
         board[pacmanR][pacmanC] = ' ';
         board[row][column] = 'P';
         if ((System.currentTimeMillis()-gameStart) <= 3000)
            play = true;
         else 
            play = false;
      }
   }
   
   
   /********************************************************************************
   * pointsEarned Method: Returns points collected in one game cycle.
   *********************************************************************************/
   public int pointsEarned()
   {
      return points;
   }
   
   
   /********************************************************************************
   * highScore Method: Returns highest points collected in one overall run of the
   * program.
   *********************************************************************************/
   public int highScore()
   {
      if (highestPoints < points)
      {
         highestPoints = points;
         return highestPoints;
      }
      return highestPoints;
   }
}

