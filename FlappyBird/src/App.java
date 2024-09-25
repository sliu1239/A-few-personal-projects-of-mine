import javax.swing.*;

public class App {                 
    public static void main(String[] args) throws Exception {
        // dimensions of our window (in pixels)
        int boardWidth = 360; // 360 pixels (dimensions of our bg image)
        int boardHeight = 640; // 640 pixels (dimensions of our bg image)

        JFrame frame = new JFrame("Flappy Bird"); // title for our window
        // frame.setVisible(true); // make frame visible (want to set visible after adding settings)
        frame.setSize(boardWidth, boardHeight); // set size of window
        frame.setLocationRelativeTo(null); // place the window at the center of our screen
        frame.setResizable(false);// user cannot resize the window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // when user clicks x button on the window, it will terminate the program

        // Add JPanel onto our frame
        FlappyBird flappyBird = new FlappyBird(); // create an instance of flappy bird
        frame.add(flappyBird);
        frame.pack(); // excludes the title bar in the dimensions of the blue screen (we want the blue screen to be of the given dimensions)
        flappyBird.requestFocus();
        frame.setVisible(true); // set visible after adding settings
    
    }  
}
