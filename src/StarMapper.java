// See Oracle tutorial on Java 2d graphics
import javax.swing.*;

import java.awt.geom.*;
import java.awt.event.*;
import java.awt.*;
// Deals with file io
// Modern java uses java.nio; see http://www.vogella.com/tutorials/JavaIO/article.html
import java.io.FileNotFoundException;
import java.io.IOException; 
import java.io.FileReader;
import java.io.BufferedReader; 
public class StarMapper extends JApplet{
	static int stars = 119610;

	static int starSize = 1;
	static int width = 1680;
	static int height = 840;
	static String filePath;   // Path for input file
	static String[] allLines; // String array derived from input file
//	int[][] coordinates = new int[3][2];
	static float[][] coordinates = new float[stars][3]; // for each star = [rAsc,Dec,app magnitude]
	
	// Optional syntax to indicate override
	// paint is actually a Component method
	@Override
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D) g; // Cast g to Graphics2D
		Color black = Color.black;
		Color white = Color.white;
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, 
											  RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
		g2.setPaint(black);
		g2.fill(new Rectangle2D.Double(0,0,width,height));
		g2.setPaint(white);
		
		for(int i = 0; i < stars; i++){  // Draw stars			
			if(coordinates[i][2] <= 1.5){
				g2.setPaint(new Color(100,100,100));
			}
			else{
				g2.setPaint(white);
			}
			g2.fill(new Ellipse2D.Double(coordinates[i][0], coordinates[i][1], coordinates[i][2], coordinates[i][2]));
		}		
	}
	
	public static void parseFile() throws IOException{
		// Returns an array of strings that contain
		// text from input file
		
		// Use both File- and BufferedReader to read from file
		// See http://stackoverflow.com/questions/9648811/specific-difference-between-bufferedreader-and-filereader
		FileReader fr = new FileReader(filePath);
		BufferedReader br = new BufferedReader(fr);
		int numOfLines = 2; // hard coded for now
		// Holds file's lines, each in a string
		String[] textLines = new String[numOfLines];
		/*for(int i = 0; i < numOfLines; i++){
			textLines[i] = textReader.readLine();
			System.out.println("reading line # " + i);
			String[] tempString = textLines[i].split(","); // Parse "2,1" into ["2","1"]
			int x = Integer.parseInt(tempString[0]);
			int y = Integer.parseInt(tempString[1]);
			coordinates[i][0] = x;
			coordinates[i][1] = y;
			System.out.println("xval = " + x);
			System.out.println("yval = " + y);
		}*/
		String someLine = br.readLine(); // Next 2 lines are to ignore 1st 2 lines in file
		someLine = br.readLine();	// First line is column description, 2nd line the sun
		for (int i = 0; i < stars; i++) {
			someLine = br.readLine();
			String[] columns = someLine.split(",");
			float rAscension = Float.parseFloat(columns[7]);  // Star coordinates (raw) 
			float declination = Float.parseFloat(columns[8]);
			float magnitude = Float.parseFloat(columns[13]);
			float factor; // size factor
			float mag4, mag3, mag2, mag1, mag0, magN;
			mag4 = (float)1;
			mag3 = (float)1.75;
			mag2 = (float)3;
			mag1 = (float)4.5;
			mag0 = (float)4.75;
			magN = (float)6;
			
			if(magnitude >= 4){ // Magnitude determines radius of point to draw
				coordinates[i][2] = mag4;
			}
			else if(magnitude >= 3){
				coordinates[i][2] = mag3;
				factor = (magnitude - mag3); // % between magnitude of 3 and 4
				factor = factor * (mag3-mag4) + mag4;
				
			}
			else if(magnitude >= 2){
				coordinates[i][2] = mag2;
				factor = (magnitude - mag2); 
				factor = factor * (mag2-mag3) + mag3;
			}
			else if(magnitude >= 1){
				coordinates[i][2] = mag1;
				factor = (magnitude - mag1); 
				factor = factor * (mag1-mag2) + mag2;
			}
			else if(magnitude >= 0){
				coordinates[i][2] = mag0;
				factor = (magnitude - mag0); 
				factor = factor * (mag0-mag1) + mag1;
			}
			else{
				coordinates[i][2] = magN;
			}
			/*
			System.out.println("rA and dec: ");
			System.out.println(rAscension);
			System.out.println(declination);
			System.out.println(magnitude);
			*/
			float raToX = (1-(rAscension/24))*width;		// Convert star coords to cartesian
			float decToY = ((90-declination)/180)*height;
			
			/*
			System.out.println();
			System.out.println("converted rA and dec: ");
			System.out.println(raToX);
			System.out.println(decToY);
			*/
			coordinates[i][0] = raToX;
			coordinates[i][1] = decToY;
//			System.out.println();

		}
		br.close();
	}
	
	public static void parseToStarCoords(String[] textCoord){
		// Input:  Array of strings specifying star coordinates
		// Method: Parse the text and 
	}
	
	
	
	public static void main(String[] args) throws IOException{
		long startTime = System.nanoTime();
		System.out.println("Running StarMapper");
		// Frame will be a place where applet resides
		JFrame f = new JFrame("StarMapper");
		// Handle exit scenario
		f.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
		JApplet a = new StarMapper(); // Note same class name when making applet, but the class must extend JApplet
		// Add applet to frame
		// getContentPane returns a Container object; add() adds the applet to the container
		f.getContentPane().add("Center", a);
		// init/load applet to system
		a.init();
		// Size window to fit its subcomponents
		f.pack();
		
		f.setSize(new Dimension(width, height));
		f.setVisible(true);
		
		// Read text file
		// Print where program's root directory is
//		System.out.println(System.getProperty("user.dir"));
		filePath = "src/hygdata_v3.csv"; // not /src/input.txt 
		// openFile() returns an array of strings from the file
		// it's a custom method made for this class
		parseFile(); // parseFile() is a class method; returns string of lines
		long endTime = System.nanoTime();
		System.out.println("Execution time = " + (endTime - startTime)/1000000000);
		
	}
	
	
}