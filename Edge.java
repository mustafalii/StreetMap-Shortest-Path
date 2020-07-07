
public class Edge {
	public final String ID;
	public double weight;
	Vertex v;
	public Vertex w;
	
	public Edge(String ID, Vertex v, Vertex w) {
		this.ID = ID;
		this.v = v;
		this.w = w;
		double vlong = Math.toRadians(v.longitude);
		double wlong = Math.toRadians(w.longitude);
		double vlat = Math.toRadians(v.latitude);
		double wlat = Math.toRadians(w.latitude);
		double ang = (vlat - wlat) / 2;
		double ang2 = (vlong - wlong) / 2;
		double temp = Math.sqrt((Math.pow(Math.sin(ang2), 2)) + Math.cos(vlong) * Math.cos(wlong) * Math.pow(Math.sin(ang), 2));
		double dist = 2 * 3959 * Math.asin(temp);
		this.weight = dist;
		if (!w.adjacents.contains(v)) {
			w.addtoAdjacent(v);
		}
		if (!v.adjacents.contains(w)) {
			v.addtoAdjacent(w);
		}
		v.addE(this);
		w.addE(this);
	}

}