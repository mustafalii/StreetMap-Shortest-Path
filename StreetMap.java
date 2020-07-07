/**
 * @author Mustafa
 * Date: 03/18
 * Main Class:
 * Reads arguments from the command line
 * Displays shortest path between the specified points
 *
 */


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.StringTokenizer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


@SuppressWarnings("serial")
public class StreetMap extends JComponent{
	
	static ArrayList<Vertex> vertices = new ArrayList<Vertex> ();
	static ArrayList<Edge> edges = new ArrayList<Edge>();
	static ArrayList<Vertex> visited = new ArrayList<Vertex> ();
	static Vertex minX;
	static Vertex minY;
	static Vertex maxX;
	static Vertex maxY;
	static Object color="Red";
	static Object background="Black";
	static boolean draw=false;
	static boolean path=false; 

	
	public static void main(String[] args) throws IOException {

		ArrayList<String> argsCopy = new ArrayList<String>();
		String mapName = args[0];
		String start=" ", end=" ";
		createGraph(mapName);
		
		for(String params: args) {
			argsCopy.add(params);
		}
		
		if(argsCopy.contains("--show")) {
			draw = true;
		}
		
		if(argsCopy.contains("--directions")) {
			path = true;
			int index = argsCopy.indexOf("--directions");
			start = argsCopy.get(index+1);
			end = argsCopy.get(index+2);
		}
		
		if(path == true) {
			dijkstra(getVertex(start), getVertex(end));
		}
		
		if(draw == true) {
			new Map().setVisible(true);
		}
		
	}
	
	
	// Reads the specified file and creates a graph
	public static void createGraph(String filename) throws IOException {
		File file = new File(filename);
		FileReader fileReader = new FileReader(file);
		Scanner scan = new Scanner(fileReader);
		boolean sort = false;
		
		while (scan.hasNextLine()) {
			StringTokenizer st = new StringTokenizer(scan.nextLine());
			while (st.hasMoreTokens()) {
				
				if (st.nextToken().equals("i")) {
					Vertex v = new Vertex(st.nextToken(), Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken())); // Creates a Vertex
					vertices.add(v);
				} 
				
				else {
					
					if(sort==false) {
						Collections.sort(vertices, compareID);
						sort=true;
					}
					
					Edge edge = new Edge(st.nextToken(), getVertex(st.nextToken()), getVertex(st.nextToken())); // Creates Edge between two vertices
					edges.add(edge);
					
				}
			}
		}
		scan.close();
	}

	
	//Djikstra's Algorithm to find shortest path between two vertices
	
	public static void dijkstra(Vertex start, Vertex fin) {
		visited.clear();
		start.distance = 0; //SETS DISTANCE OF THE STARTING VERTEX TO 0
		PriorityQueue<Vertex> queue = new PriorityQueue<Vertex>(new Compare()); //PRIORITYQUEUE TO RETURN THE SMALLEST VERTEX BASED ON DISTANCE
		queue.add(start);
		while (queue.isEmpty()!=true) {
			start = queue.poll();
			start.known = true;
			for (Vertex adj : start.adjacents) {
				if (adj.known == false) {
					double distToAdj = weight(start, adj);
					if (start.distance + distToAdj < adj.distance) {
						adj.distance = start.distance + distToAdj;
						adj.previous = start;
						queue.add(adj);
					} 
				}
			}
		}
		
		
		// Adding all vertices in the path to the ArrayList: visited
		visited.add(fin);
		for (int i = 0; i <= vertices.size(); i++) {
			fin = fin.previous;
			if (fin == null) {
				break;
			} else {
				visited.add(0, fin);
			}
		}
		
		// If vertices are connected, then print the path and distance.
		if(visited.get(visited.size()-1).distance != Integer.MAX_VALUE) {
			System.out.println("\n" + "PATH:");
			for(Vertex n:visited) {
				System.out.print(n.ID+"  ");
			}
			System.out.println("\n"+"DISTANCE (MILES): "+ String.format("%.5g%n", visited.get(visited.size()-1).distance));
		}
		
		else {
			System.out.println("Vertices not Connected");
			System.out.println("\n"+"*PROGRAM TERMINATED*");
			System.exit(1);
		}
	}
	
	
	//COMPARATOR COMPARES THE ID'S OF THE VERTICES TO PERFORM BINARYSEARCH 
	static Comparator<Vertex> compareID = new Comparator<Vertex>() {
		@Override
		public int compare(Vertex o1, Vertex o2) {
			return o1.getID().compareTo(o2.getID());
		}
	};
	
	//RETURNS THE REQUIRED VERTEX (SEARHCES USING JAVA BINARYSEARCH)
	public static Vertex getVertex(String id) {
		Vertex n=new Vertex(id,0,0);
	    int ind = Collections.binarySearch(vertices, n, compareID);
  		return vertices.get(ind);
	}
	
	
	public static double yInterval() {
		maxY = vertices.get(0);
		minY = vertices.get(0);

		for (Vertex a : vertices) {
			if (a.latitude > maxY.latitude) {
				maxY = a;
			} else if (a.latitude < minY.latitude) {
				minY = a;
			}
		}
		double ranegy = maxY.latitude - minY.latitude;
		return ranegy;
	}

	public static double xInterval() {
		maxX = vertices.get(0);
		minX = vertices.get(0);
		for (Vertex a : vertices) {
			if (a.longitude > maxX.longitude) {
				maxX = a;
			} else if (a.longitude < minX.longitude) {
				minX = a;
			}
		}
		double rangex = maxX.longitude - minX.longitude;
		return rangex;
	}
	
	//RESETS THE PROPERTIES OF VERTICES
	public static void reset() {
		for(Vertex n: vertices) {
			n.known=false;
			n.distance=Integer.MAX_VALUE;
			n.previous=null;
		}
	}
	
	//paintComponent METHOD TO DRAW ON CANVAS
	public void paintComponent(Graphics g) {
		
		double rangex = xInterval();
		double rangey = yInterval();
		
		//DRAWS THE GRAPH IF --show ARGUMENT IS PASSED
		if(draw==true) {
		if(background.equals("Black")) {
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.WHITE);
		}else {
			g.setColor(Color.white);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(Color.BLACK);
		}
		for (Edge r : edges) {
			double lat1 = this.getHeight() - ((this.getHeight() * (r.v.latitude - minY.latitude)) / rangey);
			double lon1 = (this.getWidth() * (r.v.longitude - (minX.longitude))) / rangex;
			double lat2 = this.getHeight() - ((this.getHeight() * (r.w.latitude - minY.latitude)) / rangey);
			double lon2 = (this.getWidth() * (r.w.longitude - (minX.longitude))) / rangex;
			g.drawLine((int) lon1, (int) lat1, (int) lon2, (int) lat2);
		   }
		}
		
		//DRAWS THE PATH IF --directions ARGUMENT IS PASSED
		if(path==true) {
			
		//PATH COLOR OPTION
		if(color.equals("Red")) {
		g.setColor(Color.RED);
		}else if(color.equals("Blue")) {
			g.setColor(Color.BLUE);
		}else if(color.equals("Yellow")) {
			g.setColor(Color.YELLOW);
		}else if(color.equals("Magenta")) {
			g.setColor(Color.MAGENTA);
		}else if(color.equals("Pink")) {
			g.setColor(Color.PINK);
		}else if(color.equals("Green")) {
			g.setColor(Color.GREEN);
		}
		for (int i = visited.size() - 1; i > 0; i--) {
			double lat1 = this.getHeight()
					- ((this.getHeight() * (visited.get(i).latitude - minY.latitude)) / rangey);
			double lon1 = (this.getWidth() * (visited.get(i).longitude - (minX.longitude))) / rangex;
			double lat2 = this.getHeight()
					- ((this.getHeight() * (visited.get(i - 1).latitude - minY.latitude)) / rangey);
			double lon2 = (this.getWidth() * (visited.get(i - 1).longitude - (minX.longitude))) / rangex;
			Graphics2D t= (Graphics2D)g;
			t.setStroke(new BasicStroke(3));
			t.drawLine((int) lon1, (int) lat1, (int) lon2, (int) lat2);
		}
		double lat1 = this.getHeight()- ((this.getHeight() * (visited.get(visited.size()-1).latitude - minY.latitude)) / rangey);
		double lon1 = (this.getWidth() * (visited.get(visited.size()-1).longitude - (minX.longitude))) / rangex;
		g.setColor(Color.CYAN);
		g.fillOval((int)lon1-6, (int)lat1, 15, 15);
		double lat2 =this.getHeight()- ((this.getHeight() * (visited.get(0).latitude - minY.latitude)) / rangey);
		double lon2 = (this.getWidth() * (visited.get(0).longitude - (minX.longitude))) / rangex;
		g.fillOval((int)lon2-6, (int)lat2, 15, 15);
		}
		
	}
	
  public static double weight(Vertex v, Vertex w) {
	  double vlong = Math.toRadians(v.longitude);
	  double wlong = Math.toRadians(w.longitude);
	  double vlat = Math.toRadians(v.latitude);
	  double wlat = Math.toRadians(w.latitude);
	  double ang = (vlat - wlat) / 2;
	  double ang2 = (vlong - wlong) / 2;
	  double a = Math.sqrt((Math.pow(Math.sin(ang2), 2)) + Math.cos(vlong) * Math.cos(wlong) * Math.pow(Math.sin(ang), 2));
	  double dist = 2 * 3959 * Math.asin(a);
	  return dist;	
  }

}

class Compare implements Comparator<Vertex>{
	@Override
	public int compare(Vertex o1, Vertex o2) {
		// TODO Auto-generated method stub
		return (Double.compare(o1.distance, o2.distance));
	}
}

 @SuppressWarnings("serial")
class Map extends JFrame implements ActionListener {
	
	protected JLabel label1, label2, label3, label4, label5;
	protected JTextField field1, field2, field3;
	protected JButton button1;
	static double speed;
	static double angle;
	static double time;
	static int x1 = 0;
	static int y1;
	static JComboBox<?> box2;
	static JComboBox<?> box1;
	StreetMap p;
	static Object m;

	// CONSTRUCTOR
	public Map() {
		
		// Sets frame Properties
		setTitle("Map");
		setLayout(new BorderLayout()); // BorderLayout used
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel j = new JPanel(); // JPanel j to contain the buttons and canvas
		setSize(1000, 1000);

		y1 = this.getHeight();
		label1 = new JLabel("Start VertexID");
		j.add(label1);

		field1 = new JTextField(5);
		field1.addActionListener(this);
		j.add(field1);

		label2 = new JLabel("End VertexID");
		j.add(label2);

		field2 = new JTextField(5);
		field2.addActionListener(this);
		j.add(field2);
		
		JLabel u= new JLabel("Path Color:");
		j.add(u);
		  String colors[]= {"Red","Yellow","Green","Magenta","Blue","Pink"};
		   box2=new JComboBox<Object>(colors);
		    box2.addActionListener(this);
		    j.add(box2);
		    
			JLabel b= new JLabel("Background:");
			j.add(b);
			  String back[]= {"Black","White"};
		    box1=new JComboBox<Object>(back);
		    box1.addActionListener(this);
		    j.add(box1);
		    
		    add(j, BorderLayout.NORTH);
			JLabel label6= new JLabel("Distance (Miles):");
			j.add(label6);


			label5=new JLabel();
			j.add(label5);
			if(StreetMap.path==true) {
				label5.setText(String.format("%.5g%n",StreetMap.visited.get(StreetMap.visited.size()-1).distance));
			}
			
		// Button to show path
		button1 = new JButton("SHOW PATH");
		button1.addActionListener(this);
		j.add(button1);

		// Adds canvas to the frame
		p = new StreetMap();
		p.setPreferredSize(new Dimension(getWidth(), getHeight()));
		add(p, BorderLayout.CENTER);  
		pack();
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		String v = e.getActionCommand();
		if (v.equals("SHOW PATH")) {
			StreetMap.reset();
		    StreetMap.dijkstra(StreetMap.getVertex(field1.getText()), StreetMap.getVertex(field2.getText()));
		    StreetMap.path=true;
		    label5.setText(String.format("%.5g%n",StreetMap.visited.get(StreetMap.visited.size()-1).distance));
			repaint();
		}
		StreetMap.color=box2.getSelectedItem();
		StreetMap.background=box1.getSelectedItem();
		repaint();
	}
}
