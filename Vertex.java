import java.util.ArrayList;
import java.util.LinkedList;


public class Vertex   {
	
	public final String ID;
	public double distance, longitude, latitude;
	public boolean known;
	public Vertex previous;
	public ArrayList<Vertex> adjacents = new ArrayList<Vertex>();
	public LinkedList<Edge> edgelist = new LinkedList<Edge>();

	public Vertex(String ID, double latitude, double longitude) {
		this.ID=ID;
		this.longitude=longitude;
		this.latitude=latitude;
		this.distance=Integer.MAX_VALUE;
		this.known=false;
		this.previous=null;
	}

	public void addtoAdjacent(Vertex v) {
		this.adjacents.add(v);
	}
	public void addE(Edge r){
		this.edgelist.add(r);
	}
	public LinkedList<Edge> getRoads(){
		return this.edgelist;
	}
	public String getID() {
		return this.ID;
	}
}