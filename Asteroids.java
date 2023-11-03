/* AsteroidsGame
 * Ivan Korneychuk
 * Asteroids Game in Java
 */
import java.util.Random;
import java.awt.*;
import java.io.File;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;


public class Asteroids extends JFrame{
  AsteroidsPane game = new AsteroidsPane();
  
 public Asteroids(){
   super("Asteroids");
   setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   Image logo = new ImageIcon("logo.png").getImage();
   setIconImage(logo);
   add(game);
   pack();  
   setVisible(true);
   setResizable(false); 
 }
    
 public static void main(String[] arguments){
   Asteroids frame = new Asteroids();  
 }
}

class Ship{
  private double x, y, vx, vy, angle;
  Polygon ship = new Polygon();
  
  public Ship(){
    x = 400;
    y = 300;
    vx = 0;
    vy = 0;
    angle = 270; //Angle of 270 means the ship is pointing up.
  }
  //Getter and Setter methods.
  public double getAngle(){
    return angle;
  }
  
  public void setAng(int newAngle){
    angle = newAngle;
  }
  
  public void setVX(int newVX){
    vx = newVX;
  }
  
  public void setVY(int newVY){
    vy = newVY;
  }
  
  public void setAngle(int newAngle){
    angle += newAngle;
  }
    
  public double getX(){
    return x;
  }
  
  public void setX(int newX){
    x = newX;
  }
  
  public double getY(){
    return y;
  }
  
  public void setY(int newY){
    y = newY;
  }
  //Moves the ship.
  public void move(){
    x+=vx;
    y+=vy;
  }
  
  public void acc(){ //Acceleration of ship.
    double []thrust = thrus(0.1,angle); //Calls method "thrus".
    vx += thrust[0]; //Adds acceleration to ship.
    vy += thrust[1];
    double []tot = vec(vx,vy);
    if(tot[0] > 5){  //Adds a max acceleration. (If accel is too high.)
      double []newThrus = thrus(5,tot[1]); //Figures out accel of the max accel the ship can go.
      vx = newThrus[0];
      vy = newThrus[1];
    }
  }
//Figures out the accel of the ship (x and y coord) with given angle and length.
  public double[] thrus(double len, double angle){
    double[] xy = new double[2];
    xy[0] = Math.cos(Math.toRadians(angle))*len;
    xy[1] = Math.sin(Math.toRadians(angle))*len;
    return xy;
  }

  public double[] vec(double x, double y){ //Inverse of the thrus function.
    double[] newXY = new double[2]; 
    newXY[0] = Math.sqrt(Math.pow(x,2) + Math.pow(y,2)); //This finds the accel.
    newXY[1] = Math.toDegrees(Math.atan2(y,x)); //This finds the angle.
    return newXY;
  }
  
  public void dec(){ //Decelerates the ship when it is not moving.
    vx *= 0.97;
    vy *= 0.97;
    
  }
  public void draw(Graphics g){ //Draws the ship.
    int []px = new int[8];
    int []py = new int[8];
    int intx = (int)x;
    int inty = (int)y;

    px [0] = intx + (int)(thrus(16,angle)[0]);
    py [0] = inty + (int)(thrus(16,angle)[1]); 
    px [1] = intx + (int)(thrus(6,angle-260)[0]);
    py [1] = inty + (int)(thrus(6,angle-260)[1]);
    px [2] = intx + (int)(thrus(12,angle+30)[0]);
    py [2] = inty + (int)(thrus(12,angle+30)[1]); 
    px [3] = intx + (int)(thrus(20,angle-220)[0]);
    py [3] = inty + (int)(thrus(20,angle-220)[1]); 
    px [4] = intx + (int)(thrus(10,angle-180)[0]);
    py [4] = inty + (int)(thrus(10,angle-180)[1]); 
    px [5] = intx + (int)(thrus(20,angle-140)[0]);
    py [5] = inty + (int)(thrus(20,angle-140)[1]); 
    px [6] = intx + (int)(thrus(12,angle-30)[0]);
    py [6] = inty + (int)(thrus(12,angle-30)[1]); 
    px [7] = intx + (int)(thrus(6,angle-100)[0]);
    py [7] = inty + (int)(thrus(6,angle-100)[1]);

    g.drawPolygon(px, py, 8);
 }

  public void drawFlame(Graphics g){ //Draw flame coming out of ship.
    int [] fpx = new int [3];
    int [] fpy = new int [3];
    int intx = (int)x;
    int inty = (int)y;

    fpx [0] = intx + (int)(thrus(18,angle-200)[0]);
    fpy [0] = inty + (int)(thrus(18,angle-200)[1]);
    fpx [1] = intx + (int)(thrus(10,angle-180)[0]);
    fpy [1] = inty + (int)(thrus(10,angle-180)[1]);
    fpx [2] = intx + (int)(thrus(18,angle-160)[0]);
    fpy [2] = inty + (int)(thrus(18,angle-160)[1]); 

    g.drawPolygon(fpx, fpy, 3);
 }
   //Border for ship, makes it so if you go one way to far, you'll appear from opposite side of screen.
   public void border(){
    if(x > 800){
      x = 0;
    }
    else if(x < 0){
      x = 800;
    }
    if(y > 600){
      y = 0;
      
    }
    if(y < 0){
      y = 600;
    }
   }
  public boolean collide(Polygon rock){
        return rock.contains(x,y);
   }

  public String toString(){ //Sinple toString method.
    return String.format("%.1f,%.1f,%.1f,%.1f ",x,y,vx,vy);
  }
}    

class Bullet{
  private double x, y, vx, vy, angle, speed;
  private int size;
  private int dist;
  private final int max = 500; //Bullet's max range.
 
  
  public Bullet(int x, int y, int angle){
    this.x = x;
    this.y = y;
    this.angle = angle;
    speed = 5;
    size = 2;
    vx = Math.cos(Math.toRadians(angle))*speed;
    vy = Math.sin(Math.toRadians(angle))*speed;
  }
  //Getter and Setter methods.
   public double getX(){
    return x;
  }
  
  public void setX(int newX){
    x = newX;
  }
  
  public double getY(){
    return y;
  }
  
  public void setY(int newY){
    y = newY;
  }
  
  public void move(){ 
    x += vx;
    y += vy;
  }
  public boolean max(){ //If Max range hit, it removes bullet.
    dist += 5;
    if(dist>max){
      return true;
    }
    return false;
  }
   //Border for bullet, makes it so if you go one way to far, you'll appear from opposite side of screen.
   public void border(){
    if(x > 800){
      x = 0;
    }
    else if(x < 0){
      x = 800;
    }
    if(y > 600){
      y = 0;
      
    }
    if(y < 0){
      y = 600;
    }
   }
   public boolean collide(Polygon rock){
    return rock.contains(x,y);
   }
   
   public void draw(Graphics g){
     g.setColor(Color.WHITE);
     g.fillRect((int)x-size,(int)y-size,2*size,2*size); 
   }
}

   

class Asteroid{
  private double x, y, angle, speed, vx, vy, px, py;
  ArrayList<Integer> pAngles = new ArrayList<Integer>();
  private int pAngle;
  private static final int TOP = 0, RIGHT = 1, BOTTOM = 2, LEFT = 3; //Possible spawn location (Top of screen, right of screen, etc.)
  Random random = new Random();
  Polygon rock = new Polygon();
  private int size = random.nextInt(21)+30; //Size of asteroid (random size).
  
  public Asteroid(double x, double y){
    this.x = x;
    this.y = y; 
    size = random.nextInt(15)+15;
    angle = random.nextInt(361);
    pAngle = 0;
    speed = random.nextDouble()+0.5; //Determines speed of rock.
    vx = Math.cos(Math.toRadians(angle))*speed;
    vy = Math.sin(Math.toRadians(angle))*speed;
    
    //This gives the random points of the asteroid, so it means no 2 asteroids will be the same (or highly unlikely).
    while(pAngle<360){ //Makes sure the asteroid doesn't overlap eachother.
      px = Math.cos(Math.toRadians(pAngle))*size + x;
      py = Math.sin(Math.toRadians(pAngle))*size + y;
      pAngles.add(pAngle);
      rock.addPoint((int)px,(int)py);
      int newP = random.nextInt(46)+15; //Adds a random point around the rock (need set value so it wouldn't look weird).
      pAngle += newP;
    } 
  }
    
  public Asteroid(){
    int spawn = random.nextInt(4); //Indicates where the asteroid will spawn.
    //No matter the spawn, it makes sure it spawns out of screen (Spawning on screen will look weird).
    if(spawn == TOP){ 
      y = 0 - size;
      x = random.nextInt(801);
      angle = random.nextInt(180); //This angle is random (so all asteroids won't move on screen at same angle).
                                       //But has set number, to make sure it "floats" toward screen.
    }
    if(spawn == RIGHT){
      y = random.nextInt(601);
      x = 800 + size;
      angle = random.nextInt(180)+90;
    }
    if(spawn == BOTTOM){
      y = 600 + size;
      x = random.nextInt(801);
      angle = random.nextInt(180)+180;
    }
    if(spawn == LEFT){
      y = random.nextInt(600);
      x = 0 - size;
      angle = random.nextInt(180)+270;
    }
    
    pAngle = 0;
    speed = random.nextDouble()+0.6; //Determines speed of rock.
    vx = Math.cos(Math.toRadians(angle))*speed;
    vy = Math.sin(Math.toRadians(angle))*speed;
    
    //This gives the random points of the asteroid, so it means no 2 asteroids will be the same (or highly unlikely).
    while(pAngle<360){ //Makes sure the asteroid doesn't overlap eachother.
      px = Math.cos(Math.toRadians(pAngle))*size + x;
      py = Math.sin(Math.toRadians(pAngle))*size + y;
      pAngles.add(pAngle);
      rock.addPoint((int)px,(int)py);
      int newP = random.nextInt(46)+15; //Adds a random point around the rock (need set value so it wouldn't look weird).
      pAngle += newP;
    } 
  }
  //Getter and Setter methods.
  public int getSize(){
    return size;
  }
  
  public double getX(){
    return x;
  }
  
  public void setX(int newX){
    x = newX;
  }
  
  public double getY(){
    return y;
  }
  
  public void setY(int newY){
    y = newY;
  }
  public double[] thrusA(double len, double angle){
    double[] xy = new double[2];
    xy[0] = Math.cos(Math.toRadians(angle))*len;
    xy[1] = Math.sin(Math.toRadians(angle))*len;
    return xy;
  }
  public Polygon getPoly(){
    int []xp = new int[rock.npoints]; 
    int []yp = new int[rock.npoints];
    //Moves each point.
    for(int i = 0; i < rock.npoints; i++){
      xp[i] = (int)(thrusA(size,pAngles.get(i)+angle)[0] + x);
      yp[i] = (int)(thrusA(size,pAngles.get(i)+angle)[1] + y);
    }
    
    //Recreates the rock with new points.
    return new Polygon(xp,yp,rock.npoints);
  }

  //Moves the rock.
  public void move(){
    x += vx;
    y += vy;
    angle += 1; //Rotates rock.
  }
  //Border for rock, makes it so if you go one way to far, you'll appear from opposite side of screen.
   public void border(){
    if(x > 800){
      x = 0;
    }
    else if(x < 0){
      x = 800;
    }
    if(y > 600){
      y = 0;
      
    }
    if(y < 0){
      y = 600;
    }
   }
  
  //Draws rock.
  public void draw(Graphics g){
    g.setColor(Color.WHITE);
    g.drawPolygon(getPoly());
  }
}

/*class UFO{
  private double x, y, angle, speed, vx, vy, px, py;
  ArrayList<Integer> pAngles = new ArrayList<Integer>();
  private int pAngle;
  private static final int TOP = 0, RIGHT = 1, BOTTOM = 2, LEFT = 3; //Possible spawn location (Top of screen, right of screen, etc.)
  Random random = new Random();
  Polygon rock = new Polygon();
  int size = 30;
  
  public UFO(){
    int spawn = random.nextInt(4); //Indicates where the ufo will spawn.
    //No matter the spawn, it makes sure it spawns out of screen (Spawning on screen will look weird).
    if(spawn == TOP){ 
      y = 0 - size;
      x = random.nextInt(801);
      angle = random.nextInt(180); //This angle is random (so all ufo won't move on screen at same angle).
                                       //But has set number, to make sure it "floats" toward screen.
    }
    if(spawn == RIGHT){
      y = random.nextInt(601);
      x = 800 + size;
      angle = random.nextInt(180)+90;
    }
    if(spawn == BOTTOM){
      y = 600 + size;
      x = random.nextInt(801);
      angle = random.nextInt(180)+180;
    }
    if(spawn == LEFT){
      y = random.nextInt(600);
      x = 0 - size;
      angle = random.nextInt(180)+270;
    }
    
    pAngle = 0;
    speed = random.nextDouble()+0.7; //Determines speed of UFO.
    vx = Math.cos(Math.toRadians(angle))*speed;
    vy = Math.sin(Math.toRadians(angle))*speed;
    
  
    int []px = new int[8];
    int []py = new int[8];
    int intx = (int)x;
    int inty = (int)y;

    px [0] = intx + (int)(thrusA(16,230)[1]); 
    px [1] = intx + (int)(thrusA(16,310)[0]);
    py [1] = inty + (int)(thrusA(16,310)[1]);
    px [2] = intx + (int)(thrusA(12,angle+30)[0]);
    py [2] = inty + (int)(thrusA(12,angle+30)[1]); 
    px [3] = intx + (int)(thrusA(20,angle-220)[0]);
    py [3] = inty + (int)(thrusA(20,angle-220)[1]); 
    px [4] = intx + (int)(thrusA(10,angle-180)[0]);
    py [4] = inty + (int)(thrusA(10,angle-180)[1]); 
    px [5] = intx + (int)(thrusA(20,angle-140)[0]);
    py [5] = inty + (int)(thrusA(20,angle-140)[1]); 
    px [6] = intx + (int)(thrusA(12,angle-30)[0]);
    py [6] = inty + (int)(thrusA(12,angle-30)[1]); 
    px [7] = intx + (int)(thrusA(6,angle-100)[0]);
    py [7] = inty + (int)(thrusA(6,angle-100)[1]);

  }

  //Getter and Setter methods. 
  public double getX(){
    return x;
  }
  
  public void setX(int newX){
    x = newX;
  }
  
  public double getY(){
    return y;
  }
  
  public void setY(int newY){
    y = newY;
  }
  public double[] thrusA(double len, double angle){
    double[] xy = new double[2];
    xy[0] = Math.cos(Math.toRadians(angle))*len;
    xy[1] = Math.sin(Math.toRadians(angle))*len;
    return xy;
  }
  public Polygon getPoly(){
    int []xp = new int[ufo.npoints]; 
    int []yp = new int[ufo.npoints];
    //Moves each point.
    for(int i = 0; i < rock.npoints; i++){
      xp[i] = (int)(thrusA(size,pAngles.get(i)+angle)[0] + x);
      yp[i] = (int)(thrusA(size,pAngles.get(i)+angle)[1] + y);
    }
    
    //Recreates the rock with new points.
    return new Polygon(xp,yp,ufo.npoints);
  }

  //Moves the ufo.
  public void move(){
    x += vx;
    y += vy;
  }
  public void turn(){
    angle += random.nextInt(360);
  }
    
  //Border for ufo, makes it so if you go one way to far, you'll appear from opposite side of screen.
   public void border(){
    if(x > 800){
      x = 0;
    }
    else if(x < 0){
      x = 800;
    }
    if(y > 600){
      y = 0;
      
    }
    if(y < 0){
      y = 600;
    }
   }
  
  //Draws ufo.
  public void draw(Graphics g){
    g.setColor(Color.WHITE);
    g.drawPolygon(getPoly());
  }
}*/


class Delay{ //Delay's an action (help from Michael).
  private long time;
    
  public Delay(){
    time = System.currentTimeMillis();
  }
    
  public boolean delay(int d){
    long newTime = System.currentTimeMillis();
    if(newTime - d > time){
      time = newTime;
        return true;
      }
      else{
        return false;      
      }
   }
}

class Music{
    private Clip c;
    
    public Music(String filename){
        setClip(filename);
    }
    public void setClip(String filename){
        try{
            File f = new File(filename);
            c = AudioSystem.getClip();
            c.open(AudioSystem.getAudioInputStream(f));
        } catch(Exception e){
         e.printStackTrace();
        }
    }
    public void play(){
        c.setFramePosition(0);
        c.start();
    }
    public void stop(){
        c.stop();
    }
}

class AsteroidsPane extends JPanel implements KeyListener, ActionListener, MouseListener{
  
  //All the Images used in program.
   Image logo = new ImageIcon("logo.png").getImage();
   Image back = new ImageIcon("back.gif").getImage();
   Image goback = new ImageIcon("GoBack.jpeg").getImage();
   Image gameback = new ImageIcon("GameBack.jpeg").getImage();
   
   //All sound effects used in program.
   Music bgm;
   Music shoot;
   
   //All fonts used in program.
   Font font = new Font("DialogInput", Font.BOLD, 20);
   Font menuFont = new Font("DialogInput", Font.BOLD, 50);
   Font introFont = new Font("DialogInput", Font.BOLD, 65);
   Font gameOFont = new Font("DialogInput", Font.BOLD, 75);
   
   private boolean[] keys;
   private int score = 0; //score.
   private int lives; //lives.
   //All screens, start with Intro.
   public static final int INTRO = 0, GAME = 1, GAME2 = 2, GAMEOVER = 3, WIN = 4;
   private int screen = INTRO;
   Timer timer;
   
   Ship ship;  
   UFO ufo;
   Delay cooldown = new Delay();
   
   ArrayList<Asteroid> rocks = new ArrayList<>();//List of asteroid objects.
   ArrayList<Bullet> bullets = new ArrayList<>();//List of bullet objects.

  
   public AsteroidsPane(){
     keys = new boolean[KeyEvent.KEY_LAST+1];
     setPreferredSize(new Dimension(800, 600));
     lives = 3;
     setFocusable(true);
     requestFocus();
     addKeyListener(this);
     addMouseListener(this);
     for(int i = 0; i < 15; i++){
       rocks.add(new Asteroid());
     }
     bgm = new Music("BGM.wav");
     shoot = new Music("blaster.wav");
     
     ship = new Ship();
    
     timer = new Timer(10, this);
     timer.start();
   }
public void update(){
  //If bullet hits asteroid, splits into 2. If smaller ones get hit, they dissapear. Smaller rocks are worth more points.
       if(screen == GAME){
         for(int i = 0; i < bullets.size(); i++){
           for(int j = 0; j < rocks.size(); j++){
             if(bullets.get(i).collide(rocks.get(j).getPoly())){
               if(rocks.get(j).getSize() > 30){
               for(int k = 0; k < 2; k++){
                   rocks.add(new Asteroid(bullets.get(i).getX(),bullets.get(i).getY()));
                }
               }
               if(rocks.get(j).getSize() > 30){
                 score += 20;
               }
               else{
                 score += 50;
               }
               bullets.remove(i);
               rocks.remove(j);
               i--;
               j--;
               break;
             }
           }
         }
       }
       //If ship hits a rock.
       for(int i = 0; i < rocks.size(); i++){
         if(ship.collide(rocks.get(i).getPoly())){
           rocks.remove(i);
           lives--;
           try{
             TimeUnit.SECONDS.sleep(1);
           }catch(InterruptedException e){
             e.printStackTrace();
           }
           break; 
         }
       }
       //If you die.
       if(lives <= 0) {
         lives = 3;
         ship.setX(400);
         ship.setY(300);
         ship.setAng(270);
         ship.setVX(0);
         ship.setVY(0);
         bgm.stop();
         screen = GAMEOVER;
       }
       //If you win.
       if(rocks.isEmpty()){
         try{
             TimeUnit.SECONDS.sleep(1);
           }catch(InterruptedException e){
             e.printStackTrace();
           }
           lives = 3;
           ship.setX(400);
           ship.setY(300);
           ship.setAng(270);
           ship.setVX(0);
           ship.setVY(0);
           bgm.stop();
           screen = WIN;
       }
       //Game 2 has more asteroids, and a ufo.
        if(screen == GAME2){
         for(int i = 0; i < bullets.size(); i++){
           for(int j = 0; j < rocks.size(); j++){
             if(bullets.get(i).collide(rocks.get(j).getPoly())){
               if(rocks.get(j).getSize() > 30){
               for(int k = 0; k < 2; k++){
                   rocks.add(new Asteroid(bullets.get(i).getX(),bullets.get(i).getY()));
                }
               }
               if(rocks.get(j).getSize() > 30){
                 score += 20;
               }
               else{
                 score += 50;
               }
               bullets.remove(i);
               rocks.remove(j);
               i--;
               j--;
               break;
             }
           }
         }
       }
       for(int i = 0; i < rocks.size(); i++){
         if(ship.collide(rocks.get(i).getPoly())){
           rocks.remove(i);
           lives--;
           try{
             TimeUnit.SECONDS.sleep(1);
           }catch(InterruptedException e){
             e.printStackTrace();
           }
           break; 
         }
       }
       if(lives <= 0) {
         lives = 3;
         ship.setX(400);
         ship.setY(300);
         ship.setAng(270);
         ship.setVX(0);
         ship.setVY(0);
         bgm.stop();
         screen = GAMEOVER;
       }
       if(rocks.isEmpty()){
         try{
             TimeUnit.SECONDS.sleep(1);
           }catch(InterruptedException e){
             e.printStackTrace();
           }
           lives = 3;
           score = 0;
           ship.setX(400);
           ship.setY(300);
           ship.setAng(270);
           ship.setVX(0);
           ship.setVY(0);
           bgm.stop();
           screen = WIN;
       }

}
        

   
     
   public void move(){
     //Moves the asteroid and ship.
     ship.move();
     for(Bullet bullet:bullets){
       bullet.move();
     }
     
     ArrayList<Bullet> graveyard = new ArrayList<>();
     for(Bullet bullet:bullets){
       if(bullet.max()){
         graveyard.add(bullet);
       }
     }
     bullets.removeAll(graveyard);
     
     for(Asteroid rock:rocks){
       rock.move();
     }
     //Changes angle of ship when we press left and right.
       if(keys[KeyEvent.VK_LEFT]){
         ship.setAngle(-5);
       }  
       
       if(keys[KeyEvent.VK_RIGHT]){
         ship.setAngle(5);
       }  
       
       if(keys[KeyEvent.VK_UP]){
         ship.acc(); //Accelerate when we press up.
         ship.border(); //Ship's border.
       }
       else{
         ship.dec(); //Decelerate when we aren't.
         ship.border(); //Ship's border.
       }
       if(keys[KeyEvent.VK_SPACE] && cooldown.delay(300)){   
         bullets.add(new Bullet((int)ship.getX(),(int)ship.getY(),(int)ship.getAngle())); //Shoots bullet.
         shoot.play();
       }
     }
   @Override
   public void actionPerformed(ActionEvent e){
     repaint();
     if(screen == GAME || screen == GAME2){
       update();
       move();
       for(Bullet bullet:bullets){
         bullet.border(); //Adds border for all bullets.
       }
       for(Asteroid rock:rocks){
         rock.border(); //Adds border for all rocks.
       }
     }
   }
   @Override
   public void keyReleased(KeyEvent ke){
     int key = ke.getKeyCode();
     keys[key] = false;
   } 
   
   @Override
   public void keyPressed(KeyEvent ke){
     int key = ke.getKeyCode();
     keys[key] = true;
   }
   
   @Override
   public void keyTyped(KeyEvent ke){
   }
   
   public void mouseClicked(MouseEvent e){
   }
   
   public void mouseEntered(MouseEvent e){
   }
   
   public void mouseExited(MouseEvent e){ 
   }
   
   public void mouseReleased(MouseEvent e){
   }
   
   public void mousePressed(MouseEvent e){
     //Switches screens when you press desired location.
     if(screen == INTRO){
       if(e.getX() > 260 && e.getX() < 520 &&
          e.getY() > 365 && e.getY() < 400){
         screen = GAME;
         bgm.play();
       }
     }
 
    if(screen == GAMEOVER){
      if(e.getX() > 200 && e.getX() < 585 &&
         e.getY() > 360 && e.getY() < 400){
        score = 0;
        rocks.clear();
        screen = INTRO;
        for(int i = 0; i < 15; i++){
          rocks.add(new Asteroid());
        }
      }         
    }
    
    if(screen == WIN){
      if(e.getX() > 197 && e.getX() < 545 &&
         e.getY() > 360 && e.getY() < 400){
        screen = INTRO;
        for(int i = 0; i < 15; i++){
          rocks.add(new Asteroid());
        }
      }
      
      if(e.getX() > 280 && e.getX() < 480 &&
         e.getY() > 270 && e.getY() < 300){
        screen = GAME2;
        bgm.play();
        for(int i = 0; i < 22; i++){
          rocks.add(new Asteroid());
        }
      }
    }
   }
 //Graphics for all screens.
 public void drawIntro(Graphics g){
   g.drawImage(back,0,0,this);
   g.setFont(introFont);
   g.setColor(new Color(1,1,3));
   g.fillRect(170,250,450,90);
   g.setColor(new Color(255,255,255));
   g.drawString("Asteroids",220,310);  
   g.setFont(menuFont);
   g.drawString("Play Now!",260,400);
 }
 
 public void drawGame(Graphics g){
   g.drawImage(gameback,0,0,this);
   g.setFont(font);
   g.setColor(Color.WHITE);
   g.drawString("Score: " + String.valueOf(score),25,550);   //Shows current sore and lives.
   g.drawString("Lives: " + String.valueOf(lives),650,550);
   ship.draw(g);
   for(Bullet bullet:bullets){ //Draws bullets.
     bullet.draw(g);
   }
   for(Asteroid rock:rocks){ //Draws rocks.
     rock.draw(g);
   }
   if(keys[KeyEvent.VK_UP]){ //If you're accelerating, it adds flame to ship/
     ship.drawFlame(g);
   }
 }
 
 public void drawGameOver(Graphics g){
   g.drawImage(goback,0,0,this);
   g.setFont(gameOFont);
   g.setColor(Color.WHITE);
   g.drawString("Game Over!",180,200);
   g.setFont(menuFont);
   g.drawString("Your Score: " + String.valueOf(score),200,300); 
   g.setFont(introFont);
   g.drawString("Play Again",200,400); 
 }
 
 public void drawWin(Graphics g){
   g.drawImage(goback,0,0,this);
   g.setFont(gameOFont);
   g.setColor(Color.WHITE);
   g.drawString("You Win!",220,200);
   g.setFont(menuFont);
   g.drawString("Level 2",280,300); 
   g.setFont(introFont);
   g.drawString("Main Menu",200,400); 
 }
   
 public void paint(Graphics g){
   //If screen is a certain screen, it'll draw it's corresponding graphics.
   if(screen == INTRO){
      drawIntro(g);
     }
   else if(screen == GAME){
      drawGame(g);
     }
   else if(screen == GAME2){
     drawGame(g);
     ufo.draw(g);
   }
   else if(screen == GAMEOVER){
     drawGameOver(g);
   }
   else if(screen == WIN){
     drawWin(g);
   }
 }
}