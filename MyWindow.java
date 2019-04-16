/*

https://www.gamedev.net/articles/programming/general-and-gameplay-programming/java-games-active-rendering-r2418

*/

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JButton;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.WritableRaster;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.LinkedList;
import java.util.TreeSet;
import java.util.NoSuchElementException;
import javax.imageio.ImageIO;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;



public class MyWindow implements ActionListener, MouseListener, MouseMotionListener, KeyListener  {
	
	//***************** Private Variables
	private String title = "";
	private boolean borderless = false;
	private final int DEFAULT_SIZE = 512;
	private int width = DEFAULT_SIZE; 
	private int height = DEFAULT_SIZE;
	
	
	// boundary of drawing canvas, 0% border
    // private static final double BORDER = 0.05;
    private final double BORDER = 0.00;
    private final double DEFAULT_XMIN = 0.0;
    private final double DEFAULT_XMAX = 1.0;
    private final double DEFAULT_YMIN = 0.0;
    private final double DEFAULT_YMAX = 1.0;
	private double xmin, ymin, xmax, ymax;
	// default pen radius
    private final double DEFAULT_PEN_RADIUS = 0.002;
    // current pen radius
    private double penRadius;
	// default font
    private Font DEFAULT_FONT = new Font("Consolas", Font.PLAIN, 12);
    // current font
    private Font font = DEFAULT_FONT;
	// default colors
    private final Color DEFAULT_PEN_COLOR   = addAlpha(Color.WHITE, 200);
    private final Color DEFAULT_CLEAR_COLOR = Color.BLACK;
    // current pen color
    private Color penColor;
	
	
	// for synchronization
    private Object mouseLock = new Object();
    private Object keyLock = new Object();
	
	// mouse state
    private boolean mousePressed = false;
    private double mouseX = 0;
    private double mouseY = 0;
	
	// queue of typed key characters
    private LinkedList<Character> keysTyped = new LinkedList<Character>();
    // set of key codes currently pressed down
    private TreeSet<Integer> keysDown = new TreeSet<Integer>();
	
	
	private String cursor = "";
	
	
	private JFrame app;
	private Canvas canvas;
	private BufferStrategy buffer;
	private GraphicsEnvironment ge;
	private GraphicsDevice gd;
	private GraphicsConfiguration gc;
	private BufferedImage bi;
	
	private Graphics graphics = null;
	private Graphics2D g2d = null;
	private Color background = DEFAULT_CLEAR_COLOR;
	
	
	// Variables for counting frames per seconds
	private int fps = 0;
	private int frames = 0;
	private long totalTime = 0;
	private long curTime = System.currentTimeMillis();
	private long lastTime = curTime;
	
	
	
	
	
	
	//***************** Constructor
	public MyWindow(String _title){
		title = _title;
		init();
	}
	
	
	//***************** Private Static functions
	//From StdDraw
	// get an image from the given filename
    private static Image getImage(String filename) {
        if (filename == null) throw new IllegalArgumentException();

        // to read from file
        ImageIcon icon = new ImageIcon(filename);

        // try to read from URL
        if ((icon == null) || (icon.getImageLoadStatus() != MediaTracker.COMPLETE)) {
            try {
                URL url = new URL(filename);
                icon = new ImageIcon(url);
            }
            catch (MalformedURLException e) {
                /* not a url */
            }
        }

        // in case file is inside a .jar (classpath relative to FritzStdDraw)
        if ((icon == null) || (icon.getImageLoadStatus() != MediaTracker.COMPLETE)) {
            URL url = MyWindow.class.getResource(filename);
            if (url != null)
                icon = new ImageIcon(url);
        }
        
        // in case file is inside a .jar (classpath relative to root of jar)
        if ((icon == null) || (icon.getImageLoadStatus() != MediaTracker.COMPLETE)) {
            URL url = MyWindow.class.getResource("/" + filename);
            if (url == null) throw new IllegalArgumentException("image " + filename + " not found");
            icon = new ImageIcon(url);
        }

        return icon.getImage();
    }
	
	private static Color addAlpha(Color _in, int _alpha){
		return new Color(_in.getRed(), _in.getGreen(), _in.getBlue(), _alpha);
	}
	
	// helper functions that scale from user coordinates to screen coordinates and back
    private double  scaleX(double x) { return width  * ((x - xmin) / (xmax - xmin)); }
    private double  scaleY(double y) { return height * ((ymax - y) / (ymax - ymin)); }
    private double factorX(double w) { return width * (w / Math.abs(xmax - xmin));  }
    private double factorY(double h) { return height * (h/ Math.abs(ymax - ymin));  }
    private double   userX(double x) { return xmin + x * (xmax - xmin) / width;    }
    private double   userY(double y) { return ymax - y * (ymax - ymin) / height;   }
	
	//***************** Public Static functions
	//Fritz
	public static void message(String message){
		// create a jframe
		JFrame frame = new JFrame("JOptionPane showMessageDialog example");

		// show a joptionpane dialog using showMessageDialog
		JOptionPane.showMessageDialog(frame, message, "Message", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void messagePlain(String message){
		// create a jframe
		JFrame frame = new JFrame("JOptionPane showMessageDialog example");

		// show a joptionpane dialog using showMessageDialog
		JOptionPane.showMessageDialog(frame, message, "Message", JOptionPane.PLAIN_MESSAGE);
	}
	
	public static void warning(String message){
		// create a jframe
		JFrame frame = new JFrame("JOptionPane showMessageDialog example");

		// show a joptionpane dialog using showMessageDialog
		JOptionPane.showMessageDialog(frame, message, "Warning!", JOptionPane.WARNING_MESSAGE);
	}
	
	public static void error(String message){
		// create a jframe
		JFrame frame = new JFrame("JOptionPane showMessageDialog example");

		// show a joptionpane dialog using showMessageDialog
		JOptionPane.showMessageDialog(frame, message, "Error!", JOptionPane.ERROR_MESSAGE);
	}
	
	
	//***************** Private functions
	private void init(){
		// Create game window...
		app = new JFrame();
		app.setIgnoreRepaint( true );
		app.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		//JFrame.EXIT_ON_CLOSE — Exit the application.
		//JFrame.HIDE_ON_CLOSE — Hide the frame, but keep the application running.
		//JFrame.DISPOSE_ON_CLOSE — Dispose of the frame object, but keep the application running.
		//JFrame.DO_NOTHING_ON_CLOSE — Ignore the click.
		
		
		app.setTitle(title);
		app.setResizable(false);
		
		// Create canvas for painting...
		canvas = new Canvas();
		canvas.setIgnoreRepaint( true );
		canvas.setSize( width, height );
		
		app.setUndecorated(borderless);
		
		
		
		// Add canvas to game window...
		app.add( canvas );
		app.pack();
		app.requestFocusInWindow();
		app.setVisible( true );
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		app.setLocation(dim.width/2-app.getSize().width/2, dim.height/2-app.getSize().height/2);
		
		// Create BackBuffer...
		canvas.createBufferStrategy( 2 );
		buffer = canvas.getBufferStrategy();
		
		
		// Get graphics configuration...
		ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		gd = ge.getDefaultScreenDevice();
		gc = gd.getDefaultConfiguration();
		
		// Create off-screen drawing surface
		bi = gc.createCompatibleImage( width, height );
		
		// Objects needed for rendering...
		graphics = null;
		g2d = null;
		
		// Variables for counting frames per seconds
		fps = 0;
		frames = 0;
		totalTime = 0;
		curTime = System.currentTimeMillis();
		lastTime = curTime;
		setTitle(title);
		
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addKeyListener(this);
		canvas.requestFocusInWindow();
		
		
		
		clear();
		//reset all drawing thangs
		setPenColor();
        setPenRadius();
        setFont();
		setXscale(0,1);
		setYscale(0,1);
		
		setCursor("");
	}
	
	public void close(){
		app.setVisible(false);
		app.dispose();
	}
	
	public void setXscale(double min, double max) {
        double size = max - min;
        if (size == 0.0) throw new IllegalArgumentException("the min and max are the same");
        synchronized (mouseLock) {
            xmin = min - (BORDER * size);
            xmax = max + (BORDER * size);
        }
    }
	
	public void setYscale(double min, double max) {
        double size = max - min;
        if (size == 0.0) throw new IllegalArgumentException("the min and max are the same");
        synchronized (mouseLock) {
            ymin = min - BORDER * size;
            ymax = max + BORDER * size;
        }
    }
	
	//***************** Public functions
	//WINDOW THINGS
	public void setTitle(String _title){
		title = _title;
		app.setTitle(_title);
	}
	
	public void setIcon(String filename){
		app.setIconImage(getImage(filename));
	}
	
	public void setResolution(int _w, int _h){
		close();
		width = _w;
		height = _h;
		init();
	}
	
	public void setBorderless(boolean _borderless){
		close();
		borderless = _borderless;
		init();
	}
	
	//CLEAR AND SHOW
	public void clear(){
		try{
			// count Frames per second...
			lastTime = curTime;
			curTime = System.currentTimeMillis();
			totalTime += curTime - lastTime;
			if( totalTime > 1000 ) {
				totalTime -= 1000;
				fps = frames;
				frames = 0;
			} 
			++frames;

			// clear back buffer...
			g2d = bi.createGraphics();
			g2d.setColor( background );
			g2d.fillRect( 0, 0, width, height);
		}
		catch(Exception e){
			error("MyWindow: clear()");
		}
	}
	
	public void clear(Color _col){
		try{
			// count Frames per second...
			lastTime = curTime;
			curTime = System.currentTimeMillis();
			totalTime += curTime - lastTime;
			if( totalTime > 1000 ) {
				totalTime -= 1000;
				fps = frames;
				frames = 0;
			} 
			++frames;

			// clear back buffer...
			g2d = bi.createGraphics();
			g2d.setColor( _col );
			g2d.fillRect( 0, 0, width, height);
		}
		catch(Exception e){
			error("MyWindow: clear()");
		}
	}
	
	public void show(){
		try {
			// display frames per second...
			//g2d.setFont( new Font( "Courier New", Font.PLAIN, 12 ) );
			
			
			consoleText("FPS: "+String.valueOf(fps), 0);
			
			//setPenColor(addAlpha(Color.WHITE, 150));
			
			if (cursor != ""){
				picture(mouseX, mouseY, cursor);
			}
			
			

			// Blit image and flip...
			graphics = buffer.getDrawGraphics();
			graphics.drawImage( bi, 0, 0, null );
			if( !buffer.contentsLost() )
			buffer.show();

			// Let the OS have a little time...
			Thread.yield();
		}
		catch(Exception e){
			error("MyWindow: show()");
		}
		finally {
			// release resources
			if( graphics != null ) 
			graphics.dispose();
			if( g2d != null ) 
			g2d.dispose();
		}
		
	}
	
	//PENRADIUS
	public void setPenRadius() {
        setPenRadius(DEFAULT_PEN_RADIUS);
    }
	
	public void setPenRadius(double radius) {
        if (!(radius >= 0)) throw new IllegalArgumentException("pen radius must be nonnegative");
        penRadius = radius;
        float scaledPenRadius = (float) (radius * DEFAULT_SIZE);
        BasicStroke stroke = new BasicStroke(scaledPenRadius, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        // BasicStroke stroke = new BasicStroke(scaledPenRadius);
        g2d.setStroke(stroke);
    }
	
	//PENCOLOR
	public void setPenColor() {
        setPenColor(DEFAULT_PEN_COLOR);
    }
	
	public void setPenColor(Color color) {
        if (color == null) throw new IllegalArgumentException();
        penColor = color;
        g2d.setColor(penColor);
    }
	
	//FONT
	public void setFont() {
        setFont(DEFAULT_FONT);
    }
	
	public void setFont(Font _font) {
        if (font == null) throw new IllegalArgumentException();
        font = _font;
    }
	
	public void setDefaultFont(Font _font) {
        if (font == null) throw new IllegalArgumentException();
		DEFAULT_FONT = _font;
        font = _font;
    }
	
	//DRAWING
	public void line(double x0, double y0, double x1, double y1) {
        g2d.draw(new Line2D.Double(scaleX(x0), scaleY(y0), scaleX(x1), scaleY(y1)));
    }
	
	private void pixel(double x, double y) {
        g2d.fillRect((int) Math.round(scaleX(x)), (int) Math.round(scaleY(y)), 1, 1);
    }
	
	public void point(double x, double y) {
        double xs = scaleX(x);
        double ys = scaleY(y);
        double r = penRadius;
        float scaledPenRadius = (float) (r * DEFAULT_SIZE);

        // double ws = factorX(2*r);
        // double hs = factorY(2*r);
        // if (ws <= 1 && hs <= 1) pixel(x, y);
        if (scaledPenRadius <= 1) pixel(x, y);
        else g2d.fill(new Ellipse2D.Double(xs - scaledPenRadius/2, ys - scaledPenRadius/2,
                                                 scaledPenRadius, scaledPenRadius));
    }
	
	public void circle(double x, double y, double radius) {
        if (!(radius >= 0)) throw new IllegalArgumentException("radius must be nonnegative");
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*radius);
        double hs = factorY(2*radius);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else g2d.draw(new Ellipse2D.Double(xs - ws/2, ys - hs/2, ws, hs));
    }
	
	public void filledCircle(double x, double y, double radius) {
        if (!(radius >= 0)) throw new IllegalArgumentException("radius must be nonnegative");
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*radius);
        double hs = factorY(2*radius);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else g2d.fill(new Ellipse2D.Double(xs - ws/2, ys - hs/2, ws, hs));
    }
	
	public void ellipse(double x, double y, double semiMajorAxis, double semiMinorAxis) {
        if (!(semiMajorAxis >= 0)) throw new IllegalArgumentException("ellipse semimajor axis must be nonnegative");
        if (!(semiMinorAxis >= 0)) throw new IllegalArgumentException("ellipse semiminor axis must be nonnegative");
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*semiMajorAxis);
        double hs = factorY(2*semiMinorAxis);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else g2d.draw(new Ellipse2D.Double(xs - ws/2, ys - hs/2, ws, hs));
    }
	
	public void filledEllipse(double x, double y, double semiMajorAxis, double semiMinorAxis) {
        if (!(semiMajorAxis >= 0)) throw new IllegalArgumentException("ellipse semimajor axis must be nonnegative");
        if (!(semiMinorAxis >= 0)) throw new IllegalArgumentException("ellipse semiminor axis must be nonnegative");
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*semiMajorAxis);
        double hs = factorY(2*semiMinorAxis);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else g2d.fill(new Ellipse2D.Double(xs - ws/2, ys - hs/2, ws, hs));
    }
	
	public void arc(double x, double y, double radius, double angle1, double angle2) {
        if (radius < 0) throw new IllegalArgumentException("arc radius must be nonnegative");
        while (angle2 < angle1) angle2 += 360;
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*radius);
        double hs = factorY(2*radius);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else g2d.draw(new Arc2D.Double(xs - ws/2, ys - hs/2, ws, hs, angle1, angle2 - angle1, Arc2D.OPEN));
    }
	
	public void square(double x, double y, double halfLength) {
        if (!(halfLength >= 0)) throw new IllegalArgumentException("half length must be nonnegative");
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*halfLength);
        double hs = factorY(2*halfLength);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else g2d.draw(new Rectangle2D.Double(xs - ws/2, ys - hs/2, ws, hs));
    }
	
	public void filledSquare(double x, double y, double halfLength) {
        if (!(halfLength >= 0)) throw new IllegalArgumentException("half length must be nonnegative");
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*halfLength);
        double hs = factorY(2*halfLength);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else g2d.fill(new Rectangle2D.Double(xs - ws/2, ys - hs/2, ws, hs));
    }
	
	public void rectangle(double x, double y, double halfWidth, double halfHeight) {
        if (!(halfWidth  >= 0)) throw new IllegalArgumentException("half width must be nonnegative");
        if (!(halfHeight >= 0)) throw new IllegalArgumentException("half height must be nonnegative");
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*halfWidth);
        double hs = factorY(2*halfHeight);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else g2d.draw(new Rectangle2D.Double(xs - ws/2, ys - hs/2, ws, hs));
    }
	
	public void filledRectangle(double x, double y, double halfWidth, double halfHeight) {
        if (!(halfWidth  >= 0)) throw new IllegalArgumentException("half width must be nonnegative");
        if (!(halfHeight >= 0)) throw new IllegalArgumentException("half height must be nonnegative");
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*halfWidth);
        double hs = factorY(2*halfHeight);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else g2d.fill(new Rectangle2D.Double(xs - ws/2, ys - hs/2, ws, hs));
    }
	
	public void polygon(double[] x, double[] y) {
        if (x == null) throw new IllegalArgumentException();
        if (y == null) throw new IllegalArgumentException();
        int n1 = x.length;
        int n2 = y.length;
        if (n1 != n2) throw new IllegalArgumentException("arrays must be of the same length");
        int n = n1;
        GeneralPath path = new GeneralPath();
        path.moveTo((float) scaleX(x[0]), (float) scaleY(y[0]));
        for (int i = 0; i < n; i++)
            path.lineTo((float) scaleX(x[i]), (float) scaleY(y[i]));
        path.closePath();
        g2d.draw(path);
    }
	
	public void filledPolygon(double[] x, double[] y) {
        if (x == null) throw new IllegalArgumentException();
        if (y == null) throw new IllegalArgumentException();
        int n1 = x.length;
        int n2 = y.length;
        if (n1 != n2) throw new IllegalArgumentException("arrays must be of the same length");
        int n = n1;
        GeneralPath path = new GeneralPath();
        path.moveTo((float) scaleX(x[0]), (float) scaleY(y[0]));
        for (int i = 0; i < n; i++)
            path.lineTo((float) scaleX(x[i]), (float) scaleY(y[i]));
        path.closePath();
        g2d.fill(path);
    }
	
	//PICTURE STUFF
	public void picture(double x, double y, String filename) {
        // BufferedImage image = getImage(filename);
        Image image = getImage(filename);
        double xs = scaleX(x);
        double ys = scaleY(y);
        // int ws = image.getWidth();    // can call only if image is a BufferedImage
        // int hs = image.getHeight();
        int ws = image.getWidth(null);
        int hs = image.getHeight(null);
        if (ws < 0 || hs < 0) throw new IllegalArgumentException("image " + filename + " is corrupt");

        g2d.drawImage(image, (int) Math.round(xs - ws/2.0), (int) Math.round(ys - hs/2.0), null);
    }
	
	public void picture(double x, double y, String filename, double degrees) {
        // BufferedImage image = getImage(filename);
        Image image = getImage(filename);
        double xs = scaleX(x);
        double ys = scaleY(y);
        // int ws = image.getWidth();    // can call only if image is a BufferedImage
        // int hs = image.getHeight();
        int ws = image.getWidth(null);
        int hs = image.getHeight(null);
        if (ws < 0 || hs < 0) throw new IllegalArgumentException("image " + filename + " is corrupt");

        g2d.rotate(Math.toRadians(-degrees), xs, ys);
        g2d.drawImage(image, (int) Math.round(xs - ws/2.0), (int) Math.round(ys - hs/2.0), null);
        g2d.rotate(Math.toRadians(+degrees), xs, ys);
    }
	
	public void picture(double x, double y, String filename, double scaledWidth, double scaledHeight) {
        Image image = getImage(filename);
        if (scaledWidth  < 0) throw new IllegalArgumentException("width  is negative: " + scaledWidth);
        if (scaledHeight < 0) throw new IllegalArgumentException("height is negative: " + scaledHeight);
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(scaledWidth);
        double hs = factorY(scaledHeight);
        if (ws < 0 || hs < 0) throw new IllegalArgumentException("image " + filename + " is corrupt");
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else {
            g2d.drawImage(image, (int) Math.round(xs - ws/2.0),
                                       (int) Math.round(ys - hs/2.0),
                                       (int) Math.round(ws),
                                       (int) Math.round(hs), null);
        }
    }
	
	public void picture(double x, double y, String filename, double scaledWidth, double scaledHeight, double degrees) {
        if (scaledWidth < 0) throw new IllegalArgumentException("width is negative: " + scaledWidth);
        if (scaledHeight < 0) throw new IllegalArgumentException("height is negative: " + scaledHeight);
        Image image = getImage(filename);
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(scaledWidth);
        double hs = factorY(scaledHeight);
        if (ws < 0 || hs < 0) throw new IllegalArgumentException("image " + filename + " is corrupt");
        if (ws <= 1 && hs <= 1) pixel(x, y);

        g2d.rotate(Math.toRadians(-degrees), xs, ys);
        g2d.drawImage(image, (int) Math.round(xs - ws/2.0),
                                   (int) Math.round(ys - hs/2.0),
                                   (int) Math.round(ws),
                                   (int) Math.round(hs), null);
        g2d.rotate(Math.toRadians(+degrees), xs, ys);
    }
	
	//TEXT STUFF
	public void text(double x, double y, String text) {
        if (text == null) throw new IllegalArgumentException();
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics();
        double xs = scaleX(x);
        double ys = scaleY(y);
        int ws = metrics.stringWidth(text);
        int hs = metrics.getDescent();
        g2d.drawString(text, (float) (xs - ws/2.0), (float) (ys + hs));
    }
	
	public void text(double x, double y, String text, double degrees) {
        if (text == null) throw new IllegalArgumentException();
        double xs = scaleX(x);
        double ys = scaleY(y);
        g2d.rotate(Math.toRadians(-degrees), xs, ys);
        text(x, y, text);
        g2d.rotate(Math.toRadians(+degrees), xs, ys);
    }
	
	public void textLeft(double x, double y, String text) {
        if (text == null) throw new IllegalArgumentException();
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics();
        double xs = scaleX(x);
        double ys = scaleY(y);
        int hs = metrics.getDescent();
        g2d.drawString(text, (float) xs, (float) (ys + hs));
    }
	
	public void textRight(double x, double y, String text) {
        if (text == null) throw new IllegalArgumentException();
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics();
        double xs = scaleX(x);
        double ys = scaleY(y);
        int ws = metrics.stringWidth(text);
        int hs = metrics.getDescent();
        g2d.drawString(text, (float) (xs - ws), (float) (ys + hs));
    }
	
	public void consoleText(String _in, int _num){
		setPenColor(addAlpha(Color.GREEN, 150));
		setFont();
		setXscale(0, 1);
		setYscale(0, 1);
		textRight( 0.98, 0.98 -0.03*(double)_num, _in);
	}
	
	//SAVING
	public void save(String filename) {
        if (filename == null) throw new IllegalArgumentException();
        File file = new File(filename);
        String suffix = filename.substring(filename.lastIndexOf('.') + 1);

        // png files
        if ("png".equalsIgnoreCase(suffix)) {
            try {
                ImageIO.write(bi, suffix, file);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        // need to change from ARGB to RGB for JPEG
        // reference: http://archives.java.sun.com/cgi-bin/wa?A2=ind0404&L=java2d-interest&D=0&P=2727
        else if ("jpg".equalsIgnoreCase(suffix)) {
            WritableRaster raster = bi.getRaster();
            WritableRaster newRaster;
            newRaster = raster.createWritableChild(0, 0, width, height, 0, 0, new int[] {0, 1, 2});
            DirectColorModel cm = (DirectColorModel) bi.getColorModel();
            DirectColorModel newCM = new DirectColorModel(cm.getPixelSize(),
                                                          cm.getRedMask(),
                                                          cm.getGreenMask(),
                                                          cm.getBlueMask());
            BufferedImage rgbBuffer = new BufferedImage(newCM, newRaster, false,  null);
            try {
                ImageIO.write(rgbBuffer, suffix, file);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        else {
            System.out.println("Invalid image file type: " + suffix);
        }
    }
	
	//MOUSE
	private Point mousePos(){
		return MouseInfo.getPointerInfo().getLocation();
	}
	
	public double mouseX(){
		//Point m = mousePos();
		//int x = m.x - app.getLocation().x - 3;
		//double r = (double)x/width;
		//return r*(xmax-xmin) + xmin;
		
		synchronized (mouseLock) {
            return mouseX;
        }
	}
	
	public double mouseY(){
		//Point m = mousePos();
		//int y = m.y - app.getLocation().y - 26;
		//double r = (double)y/height;
		//return -(r*(ymax-ymin) + ymin);
		
		synchronized (mouseLock) {
            return mouseY;
        }
	}
	
	public boolean mousePressed() {
        synchronized (mouseLock) {
            return mousePressed;
        }
    }
	
	//KEYBOARD
	public boolean hasNextKeyTyped() {
        synchronized (keyLock) {
            return !keysTyped.isEmpty();
        }
    }
	
	public char nextKeyTyped() {
        synchronized (keyLock) {
            if (keysTyped.isEmpty()) {
                throw new NoSuchElementException("your program has already processed all keystrokes");
            }
            return keysTyped.removeLast();
        }
    }
	
	public boolean isKeyPressed(int keycode) {
        synchronized (keyLock) {
            return keysDown.contains(keycode);
        }
    }
	
	//CURSOR
	public void setCursor(String filename){
		cursor = filename;
		if (filename == ""){
			app.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}
	}
	
	
	
	//***************** Interface functions
	@Override
    public void actionPerformed(ActionEvent e) {
        FileDialog chooser = new FileDialog(app, "Use a .png or .jpg extension", FileDialog.SAVE);
        chooser.setVisible(true);
        String filename = chooser.getFile();
        if (filename != null) {
            save(chooser.getDirectory() + File.separator + chooser.getFile());
        }
    }
	
	@Override
    public void mouseClicked(MouseEvent e) {
        // this body is intentionally left empty
    }
	
	@Override
    public void mouseEntered(MouseEvent e) {
        // this body is intentionally left empty
    }
	
	@Override
    public void mouseExited(MouseEvent e) {
        // this body is intentionally left empty
    }
	
	@Override
    public void mousePressed(MouseEvent e) {
		synchronized (mouseLock) {
            mouseX = userX(e.getX());
            mouseY = userY(e.getY());
            mousePressed = true;
        }
    }
	
	@Override
    public void mouseReleased(MouseEvent e) {
        synchronized (mouseLock) {
            mousePressed = false;
        }
    }
	
	@Override
    public void mouseDragged(MouseEvent e)  {
        synchronized (mouseLock) {
            mouseX = userX(e.getX());
            mouseY = userY(e.getY());
        }
    }
	
	@Override
    public void mouseMoved(MouseEvent e) {
        synchronized (mouseLock) {
            mouseX = userX(e.getX());
            mouseY = userY(e.getY());
        }
    }
	
	@Override
    public void keyTyped(KeyEvent e) {
        synchronized (keyLock) {
            keysTyped.addFirst(e.getKeyChar());
        }
    }
	
	@Override
    public void keyPressed(KeyEvent e) {
        synchronized (keyLock) {
            keysDown.add(e.getKeyCode());
        }
    }
	
	@Override
    public void keyReleased(KeyEvent e) {
        synchronized (keyLock) {
            keysDown.remove(e.getKeyCode());
        }
    }
	
	
	//***************** Main
	public static void main( String[] args ) {
		
		MyWindow bleh = new MyWindow("my window");
		
		//bleh.setIcon("1.png");
		
		bleh.setCursor("");

		
		//bleh.setResolution(1920, 1080);
		//bleh.setBorderless(true);
		bleh.setXscale(-1, 1);
		bleh.setYscale(-1, 1);
		
		//error("eyyy");
		
		Random rand = new Random();
		
		while( true ) {
			bleh.clear();
			if (bleh.mouseY() > 0 && bleh.mouseX() > 0){
				try{
					// draw some rectangles...
					for( int i = 0; i < 20; ++i ) {
						int r = rand.nextInt(256);
						int g = rand.nextInt(256);
						int b = rand.nextInt(256);
						bleh.setPenColor( new Color(r,g,b) );
						double x = (rand.nextDouble()-0.5)*1;
						double y = (rand.nextDouble()-0.5)*1;
						double w = rand.nextDouble()*0.5;
						double h = rand.nextDouble()*0.5;
						//bleh.filledRectangle( x, y, w, h );
					}
				}
				catch(Exception e){
					error("MyWindow: sommin()");
				}
			}
			else {
				bleh.setPenColor(Color.WHITE);
			}
			
			//bleh.filledCircle(bleh.mouseX(), bleh.mouseY(), 0.1);
			
			bleh.show();
		}
	}
}