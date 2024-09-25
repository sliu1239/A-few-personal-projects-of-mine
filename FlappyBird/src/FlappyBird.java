// OUR JPANEL, what we will be using to draw our game
import java.awt.*; 
import java.awt.event.*;
import java.util.ArrayList; // this will store all the pipes in our game
import java.util.Random; // this will be used to place our pipes at random positions
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener { // FlappyBird inherit JPanel class, this will allow us to define a new class with all the functionalities of JPanel
// this way we can keep the JPanel features and add variables and functions that we need for our Flappy Bird game   
// ActionListener and KeyListener are interfaces 
    int boardWidth = 360;
    int boardHeight = 640;

    // Images (adding some variables for the images)
    // these four variables will store our image objects
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    // adding variables for our bird
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img; // image field

        // define constructor and pass in the image
        Bird(Image img) {
            this.img = img;
        }
    }

    // Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;       // scaled by 1/6  --> actual image is 384 pixels
    int pipeHeight = 512; // the actual dimensions for the image is 6 times bigger

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img; // whenever we create a Pipe object we need to pass in the image, especially because we have two pipes (top pipe & bottom pipe)
        boolean passed = false; // this will check whether our bird has passed the pipe yet (used for keeping track of score)

        // lets define the constructor
        Pipe(Image img) {
            this.img = img;
        }
    }

    // game logic
    // add field for bird
    Bird bird;
    int velocityX = -4; // move pipes to the left speed (simulates bird moving right); change the x position by negative 4 pixels every frame
    int velocityY = 0; // want to make the bird move, in pixels per frame
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;
    double score = 0;

    // create constructor
    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        // setBackground(Color.blue);
        setFocusable(true); // make sure that our JPanel (FlappyBird) is the one that takes in our key events
        addKeyListener(this); // make sure that we check the 3 functions (at end) when we have a key pressed

        // load images onto variables
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage(); // getClass refers to FlappyBird.class; getResource is the location of the FlappyBird.class (in the src folder)
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        // Bird
        // within constructor, create bird object
        bird = new Bird(birdImg);

        pipes = new ArrayList<Pipe>();

        // within constructor,
        // place pipes timer
        placePipesTimer = new Timer(1500, new ActionListener() { // rmb 1000 = 1 second, so 1500 = 1.5 seconds, so every 1.5 s we are going to call an action
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        }); 
        placePipesTimer.start();

        // game timer, we need the game loop so we can continuously draw frames for our game
        gameLoop = new Timer(1000/60, this); // first input is in milliseconds (1000 milliseconds = 1 second); we want 60 frames per second
        // action is this, and this will refer to the FlappyBird class and we need to implement ActionListener to this class
        gameLoop.start();
    }

    public void placePipes() { // a function that will create the pipes and add them to the array list
        // math.random give value between 0-1 then * pipeHeight/2 (which gives 256) -> (0-256)
        // 512/4 = 128
        // total = 0 - 128 - (0-256) --> if math.random gives 0 then pipeHeight/4, if gives 1 then substract pipeHeight/2
        // range = 1/4 pipeHeight -> 3/4 pipeHeight   this is how much we're shifting upwards for the y position 
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;
        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe); // add bottom pipes to the array list
    }
    // draw the image onto the background
    // create function of the JPanel
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // this is just gonna invoke the function from JPanel (bc we inherited the JPanel, super refers to the parent class which is JPanel)
        draw(g);
    }

    public void draw(Graphics g) {
        // System.out.println("draw");   (just to test)
        // background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        // bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null); // so we created a class to easily access these values with these simplified names

        // pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("GAME OVER: " + String.valueOf((int) score), 10, 35);
        }
        else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        // bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        // pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;
            
            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5; // 0.5 because there are 2 pipes, so 0.5*2 = 1, 1 pt for each set of pipes
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }
        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b) {
        // formula for detecting collisions
        return a.x < b.x + b.width && // a's top left corner doesn't reach b's top right corner
            a.x + a.width > b.x &&    // a's top right corner doesn't reach b's top left corner
            a.y < b.y + b.height &&   // a's top left corner doesn't reach b's bottom left corner
            a.y + a.height > b.y;     // a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) { // the action performed every 16 milliseconds or 60 times a second, it's going to be the paintComponent function
        move();
        repaint(); // with the JPanel, this will call the paintComponent
        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop(); // stops repainting and updating the frames of our game
        }
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}


// so we move the pipes, we draw the pipes, and then we place new pipes, and then we have a timer that
// will place the new pipes every 1.5 seconds