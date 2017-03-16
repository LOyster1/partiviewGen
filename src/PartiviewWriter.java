import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.lang.Math;

/* Partiview writer class exports the final graph in a format that can be read by Partiview based applications*/
public class PartiviewWriter implements GraphWriter {
	private String path;
	private Graph graph;
	private String filename;
	private MeshWriter speckWriter;
	private int range = 91;
        public static ArrayList<GraphV2> individualGraphs;

	public PartiviewWriter() {
		path = "out";
	}

	public PartiviewWriter(String path) {
		this.path = path;
	}
	
	public PartiviewWriter(String path, int range) {
		this.path = path;
		this.range = range;
	}

	public void setFileName(String fileName) {
		this.filename = fileName;
	}

	public void setPath(String path) {
		this.path = path;
	}
        public void setIndividualGraphs(ArrayList<GraphV2> individualGraphsIn){
            individualGraphs=individualGraphsIn;
        }
        public ArrayList<GraphV2> getIndividualGraphs(){
            return  individualGraphs;
        }
        public MeshWriter getMeshWriter()
        {
            return speckWriter;
        }
        public void setMeshWriter(MeshWriter meshIn){
            speckWriter=meshIn;
        }

	public void write(Graph graph) {
		this.graph = graph;
		speckWriter = new MeshWriter(path, filename, graph, range);

		System.out.println("writing mesh.speck...");
		speckWriter.MeshWriter();
		System.out.println("writing nodes.speck...");
		writeNodes(graph.getNodeList(), graph.getColorList());
		System.out.println("writing edges.speck...");
		writeEdges(graph.getEdgeList());
		System.out.println("writing cmap...");
		System.out.println("master file...");
		writeDS2();
		writeCF();
		writeCmap(graph.getColorList());
	}

	public String fileFormatDescription() {
		return "Partiview Data (*.cf)";
	}

	public String fileFormatExtension() {
		return "cf";
	}

	private void writeDS2() {
		String path = this.path + filename + ".sct";
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			writer.write("\tButtonText \"plugin loading...\"\n");
			writer.write("+1\tPlugin Load \"DU\"\n");
			writer.write("+1\tPlugin Show \"DU\"\n+1\n");
			writer.write("\tButtonText \"dataset loading...\"\n");
			writer.write("\tScene Add Object \"graph\" \"World\" \"<ShowPath>\\" + filename + ".cf\"\n");
			writer.write("\tScene View \"graph\" 0 100\n");
			writer.write("\tNavigator SweetSpot [0:0:0] 180 90\n");
			writer.write("\tScene Camera Center World\n");
			writer.write("\tButtonText \"remove data\"\n");
			writer.write("\tstop\n");
			writer.write("\tScene Remove \"graph\"\n");
			writer.close();
		} catch (IOException e) {
		}
	}

	private void writeCmap(ArrayList<GraphColor> colorList) {
		String path = this.path + filename + ".cmap";
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			writer.write(colorList.size() + "\n");
			for (GraphColor c : colorList) {
				writer.write((double) (c.getR() / 255.0) + " " + (double) (c.getG() / 255.0) + " "
						+ (double) (c.getB() / 255.0) + " 1\n");
			}
			writer.close();
		} catch (IOException e) {
		}
	}

	boolean hasNoodles = true;

	private void writeCF() {
		String path = this.path + filename + ".cf";
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			writer.write("eval clip 1 1000000\n");
			writer.write("object g1=nodes\n");
			writer.write("textcment 1 1 0 0\n");
			writer.write("include " + filename + "_nodes.speck\n");
			writer.write("eval alpha 1\n");
			writer.write("eval lsize 25\n");
			writer.write("eval laxes off\n");
			writer.write("eval label on\n");
			writer.write("object g2=edges\n");
			writer.write("cment 0 1 1 1\n");
			writer.write("include " + filename + "_edges.speck\n");
			writer.write("eval alpha .1\n");
			if (hasNoodles) {
				writer.write("object g3=noodles\n");
				writer.write("cment 0 1 1 1\n");
				writer.write("include " + filename + "_noodles.speck\n");
				writer.write("eval alpha .1\n");
				writer.write("include " + filename + "_mesh.speck\n");
			}
			writer.close();
		} catch (IOException e) {
		}
	}

	private int indexOf(ArrayList<GraphColor> clist, GraphColor c) {
		GraphColor temp;
		for (int i = 0; i < clist.size(); i++) {
			temp = clist.get(i);
			if (temp.getR() == c.getR() && temp.getG() == c.getG() && temp.getB() == c.getB()) {
				return i;
			}
		}
		return -1;
	}

	private void writeNodes(ArrayList<Node> nodeList, ArrayList<GraphColor> colorList) {
		String path = this.path + filename + "_nodes.speck";
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			float temp;
			float maxRadius = 0;
			writer.write("textcolor 1\n");
			writer.write("eval cmap " + filename + ".cmap\n");
			for (Node n : nodeList) {
				temp = n.getRadius();
				if (temp > maxRadius)
					maxRadius = temp;
				writer.write(n.getX() + " " + n.getY() + " " + (n.getZ()-speckWriter.lowestZ()) + " ellipsoid -r " + temp + "," + temp + ","
						+ temp + " -c " + indexOf(colorList, n.getColor()) + "\n");
			}
			for (Node n : nodeList) {
				writer.write(n.getX() + " " + n.getY() + " " + (n.getZ()-speckWriter.lowestZ()) + " text -size " + (n.getRadius() / maxRadius)
						+ " " + n.getLabel() + '\n');
			}
			writer.close();
		} catch (IOException e) {
		}
	}

	private void writeEdges(ArrayList<Edge> edgeList) {
		ArrayList<Edge> noodleList;
		String path = this.path + filename + "_edges.speck";
		String mesh = "mesh -s wire -c 0 {\n1 2\n";
		noodleList = new ArrayList<Edge>();
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			for (Edge e : edgeList) {
				if (e.isNoodle()) {
					noodleList.add(e);
				} else {
					Node s = e.getSourceNode();
					Node t = e.getTargetNode();
					writer.write(mesh);
					writer.write(s.getX() + " " + s.getY() + " " + (s.getZ()-speckWriter.lowestZ()) + "\n");
					writer.write(t.getX() + " " + t.getY() + " " + (t.getZ()-speckWriter.lowestZ()) + "\n}\n");
				}
			}
			writer.close();
			if (noodleList.size() > 0) {
				hasNoodles = true;
				writer = new BufferedWriter(new FileWriter(this.path + filename + "_noodles.speck"));
				for (Edge e : noodleList)
					writeEdgeFaces(writer, getVerticesForFaces(e));
				writer.close();
			}
		} catch (IOException e) {
		}
	}

	private V[] getVerticesForFaces(Edge edge) {
		V[] v = new V[8];
		Node source = edge.getSourceNode();
		Node target = edge.getTargetNode();
		v[0] = new V(source.getX() + edge.getSourceRadius(), source.getY(), source.getZ());
		v[1] = new V(source.getX(), source.getY() + edge.getSourceRadius(), source.getZ());
		v[2] = new V(source.getX() - edge.getSourceRadius(), source.getY(), source.getZ());
		v[3] = new V(source.getX(), source.getY() - edge.getSourceRadius(), source.getZ());
		v[4] = new V(target.getX() + edge.getTargetRadius(), target.getY(), target.getZ());
		v[5] = new V(target.getX(), target.getY() + edge.getTargetRadius(), target.getZ());
		v[6] = new V(target.getX() - edge.getTargetRadius(), target.getY(), target.getZ());
		v[7] = new V(target.getX(), target.getY() - edge.getTargetRadius(), target.getZ());
		return v;
	}

	private void writeEdgeFaces(BufferedWriter writer, V[] vs) throws IOException {
		String facePrefix = "mesh -s solid -c 0 {\n2 2\n";
		writer.write(facePrefix);
		writer.write(vs[1].getX() + " " + vs[1].getY() + " " + (vs[1].getZ()-speckWriter.lowestZ()) + "\n");
		writer.write(vs[0].getX() + " " + vs[0].getY() + " " + (vs[0].getZ()-speckWriter.lowestZ()) + "\n");
		writer.write(vs[5].getX() + " " + vs[5].getY() + " " + (vs[5].getZ()-speckWriter.lowestZ()) + "\n");
		writer.write(vs[4].getX() + " " + vs[4].getY() + " " + (vs[4].getZ()-speckWriter.lowestZ()) + "\n}\n");
		writer.write(facePrefix);
		writer.write(vs[3].getX() + " " + vs[3].getY() + " " + (vs[3].getZ()-speckWriter.lowestZ()) + "\n");
		writer.write(vs[0].getX() + " " + vs[0].getY() + " " + (vs[0].getZ()-speckWriter.lowestZ()) + "\n");
		writer.write(vs[7].getX() + " " + vs[7].getY() + " " + (vs[7].getZ()-speckWriter.lowestZ()) + "\n");
		writer.write(vs[4].getX() + " " + vs[4].getY() + " " + (vs[4].getZ()-speckWriter.lowestZ()) + "\n}\n");
		writer.write(facePrefix);
		writer.write(vs[2].getX() + " " + vs[2].getY() + " " + (vs[2].getZ()-speckWriter.lowestZ()) + "\n");
		writer.write(vs[3].getX() + " " + vs[3].getY() + " " + (vs[3].getZ()-speckWriter.lowestZ()) + "\n");
		writer.write(vs[6].getX() + " " + vs[6].getY() + " " + (vs[6].getZ()-speckWriter.lowestZ()) + "\n");
		writer.write(vs[7].getX() + " " + vs[7].getY() + " " + (vs[7].getZ()-speckWriter.lowestZ()) + "\n}\n");
		writer.write(facePrefix);
		writer.write(vs[1].getX() + " " + vs[1].getY() + " " + (vs[1].getZ()-speckWriter.lowestZ()) + "\n");
		writer.write(vs[2].getX() + " " + vs[2].getY() + " " + (vs[2].getZ()-speckWriter.lowestZ()) + "\n");
		writer.write(vs[5].getX() + " " + vs[5].getY() + " " + (vs[5].getZ()-speckWriter.lowestZ()) + "\n");
		writer.write(vs[6].getX() + " " + vs[6].getY() + " " + (vs[6].getZ()-speckWriter.lowestZ()) + "\n}\n");
	}

	private V[] getEdgeVertices(Edge edge) {
		V[] v = new V[8];
		V s = new V(edge.getSourceNode().getX(), edge.getSourceNode().getY(), edge.getSourceNode().getZ());
		V t = new V(edge.getTargetNode().getX(), edge.getTargetNode().getY(), edge.getTargetNode().getZ());
		float sourceRadius = edge.getSourceNode().getRadius();// edge.getSourceRadius();
		float targetRadius = edge.getTargetNode().getRadius();// edge.getTargetRadius();

		v[0] = new V(s.getX() + sourceRadius, s.getY(), s.getZ());
		v[1] = new V(s.getX(), s.getY() + sourceRadius, s.getZ());
		v[2] = new V(s.getX() - sourceRadius, s.getY(), s.getZ());
		v[3] = new V(s.getX(), s.getY() - sourceRadius, s.getZ());

		v[4] = new V(t.getX() + targetRadius, t.getY(), t.getZ());
		v[5] = new V(t.getX(), t.getY() + targetRadius, t.getZ());
		v[6] = new V(t.getX() - targetRadius, t.getY(), t.getZ());
		v[7] = new V(t.getX(), t.getY() - targetRadius, t.getZ());

		for (int i = 0; i < 4; i++) {
			v[0] = new V(v[0].getX() - getDiff(sourceRadius, t.getX(), s.getX(), v[0].getX()),
					v[0].getY() - getDiff(sourceRadius, t.getY(), s.getY(), v[0].getY()),
					v[0].getZ() - getDiff(sourceRadius, t.getZ(), s.getZ(), v[0].getZ()));

			v[i + 4] = new V(v[i + 4].getX() - getDiff(targetRadius, s.getX(), t.getX(), v[i + 4].getX()),
					v[i + 4].getY() - getDiff(targetRadius, s.getY(), t.getY(), v[i + 4].getY()),
					v[i + 4].getZ() - getDiff(targetRadius, s.getZ(), t.getZ(), v[i + 4].getZ()));
		}
		return v;
	}

	private float getDiff(float radius, float d1, float d0, float v) {
		float diff = d1 - v;
		diff -= (d1 - d0);
		return diff;
	}
	/*
	 * private V[] calculateVerts(Edge e){ V[] v = new V[8]; V s = new
	 * V(e.getSourceNode().getX(),e.getSourceNode().getY(),e.getSourceNode().
	 * getZ()); V t = new
	 * V(e.getTargetNode().getX(),e.getTargetNode().getY(),e.getTargetNode().
	 * getZ());
	 * 
	 * float sourceRadius=e.getSourceNode().getRadius();//e.getSourceRadius();
	 * float targetRadius=e.getTargetNode().getRadius();//e.getTargetRadius();
	 * float distance =
	 * (float)Math.sqrt(square(t.getX()-s.getX())+square(t.getY()-s.getY())+
	 * square(t.getZ()-s.getZ()));
	 * 
	 * float distancePoint =
	 * (float)Math.sqrt(square(distance)+square(sourceRadius)); float
	 * changeAngle = getAzimuth(distance, 0, distancePoint); float azimuth =
	 * getAzimuth(s.getZ(),t.getZ(),distance); float elevation =
	 * getElevation(s.getY(),t.getY(),distance,azimuth);
	 * 
	 * v[0] = calculateCoords(t, distancePoint, elevation+changeAngle, azimuth);
	 * v[1] = calculateCoords(t, distancePoint, elevation, azimuth+changeAngle);
	 * v[2] = calculateCoords(t, distancePoint, elevation-changeAngle, azimuth);
	 * v[3] = calculateCoords(t, distancePoint, elevation, azimuth-changeAngle);
	 * 
	 * distancePoint=(float)Math.sqrt(square(distance)+square(targetRadius));
	 * azimuth = getAzimuth(t.getZ(),s.getZ(),distance); elevation =
	 * getElevation(t.getY(), s.getY(),distance, azimuth);
	 * changeAngle=getAzimuth(distance, 0, distancePoint);
	 * 
	 * v[4] = calculateCoords(s, distancePoint, elevation+changeAngle, azimuth);
	 * v[5] = calculateCoords(s, distancePoint, elevation, azimuth+changeAngle);
	 * v[6] = calculateCoords(s, distancePoint, elevation-changeAngle, azimuth);
	 * v[7] = calculateCoords(s, distancePoint, elevation, azimuth-changeAngle);
	 * 
	 * return v; } private float square(float v){ return v*v; }
	 * 
	 * private float getElevation(float y0, float y1, float distance, float
	 * azimuth){ return (float)Math.asin((y0-y1)/(distance*Math.sin(azimuth)));
	 * }
	 * 
	 * private float getAzimuth(float z0, float z1, float distance){ return
	 * (float)Math.acos(Math.abs((z0-z1))/distance); }
	 * 
	 * private V calculateCoords(V v, float dist, float elevation, float
	 * azimuth){ float sinAz = (float)Math.sin(azimuth); return new V(v.getX() +
	 * dist * (float)Math.cos(elevation) * sinAz, v.getY() + dist *
	 * (float)Math.sin(elevation) * sinAz, v.getZ() + dist *
	 * (float)Math.cos(azimuth) ); }
	 */

	/*
	 * below method utilizes a formula based upon a script found here:
	 * http://astro.uchicago.edu/cosmus/PV/pvutils.pm this method generates 8
	 * vertices that are the vertices of a rectangular prism; to be used for
	 * drawing faces of edges
	 *
	 * private V[] getEdgeVertices(Edge edge){ final V s =
	 * (V)edge.getSourceNode().clone(); final V t = (V)edge.getTargetNode(); V
	 * sa,sb,sc,sd,ta,tb,tc,td; V tref1 = new V(); V tref2 = new V(); float
	 * sourceRadius, targetRadius,dst,dst2;
	 * sourceRadius=edge.getSourceNode().getRadius();
	 * targetRadius=edge.getTargetNode().getRadius();
	 * 
	 * sa=new V(); sb=new V(); sc=new V(); sd=new V(); ta=new V(); tb=new V();
	 * tc=new V(); td=new V();
	 * 
	 * sa.setX(s.getX()+(sourceRadius/2)); sa.setY(s.getY()+(sourceRadius/2));
	 * sa.setZ(s.getZ() +
	 * sourceRadius*(t.getX()-s.getX()+t.getY()-s.getY())/(2*(s.getZ()-t.getZ())
	 * ));
	 * dst=(float)Math.sqrt((s.getZ()-sa.getZ())*(s.getZ()-sa.getZ())+(s.getY()-
	 * sa.getY())*(s.getY()-sa.getY())+(s.getX()-sa.getX())*(s.getX()-sa.getX())
	 * );
	 * 
	 * sa.setX(s.getX()+(sourceRadius/dst)*(sa.getX()-s.getX()));
	 * sa.setY(s.getY()+(sourceRadius/dst)*(sa.getY()-s.getY()));
	 * sa.setX(s.getX()+(sourceRadius/dst)*(sa.getX()-s.getX()));
	 * 
	 * sc.setX(s.getX()-(sa.getX()-s.getX()));
	 * sc.setY(s.getY()-(sa.getY()-s.getY()));
	 * sc.setZ(s.getZ()-(sa.getZ()-s.getZ()));
	 * 
	 * tref1.setX(s.getX()+(targetRadius/dst)*((s.getX()+(targetRadius))-s.getX(
	 * )));
	 * tref1.setY(s.getY()+(targetRadius/dst)*((s.getY()+(targetRadius))-s.getY(
	 * )));
	 * tref1.setZ(s.getZ()+(targetRadius/dst)*((s.getZ()+targetRadius*(t.getX()-
	 * s.getX()+t.getY()-s.getY())/(2*(s.getZ()-t.getZ())))-s.getZ()));
	 * 
	 * sb.setX(s.getX()+((t.getY()-s.getY())*(sa.getZ()-s.getZ())-(t.getZ()-s.
	 * getZ())*(sa.getY()-s.getY())));
	 * sb.setY(s.getY()+((t.getZ()-s.getZ())*(sa.getX()-s.getX())-(t.getX()-s.
	 * getX())*(sa.getZ()-s.getZ())));
	 * sb.setZ(s.getZ()+((t.getX()-s.getX())*(sa.getY()-s.getY())-(t.getY()-s.
	 * getY())*(sa.getX()-s.getX())));
	 * 
	 * dst2=(float)Math.sqrt((s.getZ()-sb.getZ())*(s.getZ()-sb.getZ())+(s.getY()
	 * -sb.getY())*(s.getY()-sb.getY())+(s.getX()-sb.getX())*(s.getX()-sb.getX()
	 * )); //
	 * tref2.setX(s.getX()+((t.getY()-s.getY())*(tref1.getZ()-s.getZ())-(t.getZ(
	 * )-s.getZ())*(tref1.getY()-s.getY())));
	 * tref2.setY(s.getY()+((t.getZ()-s.getZ())*(tref1.getX()-s.getX())-(t.getX(
	 * )-s.getX())*(tref1.getZ()-s.getZ())));
	 * tref2.setZ(s.getZ()+((t.getX()-s.getX())*(tref1.getY()-s.getY())-(t.getY(
	 * )-s.getY())*(tref1.getX()-s.getX()))); //
	 * tref2.setX(s.getX()+(targetRadius/dst2)*(tref2.getX()-s.getX()));
	 * tref2.setY(s.getY()+(targetRadius/dst2)*(tref2.getY()-s.getY()));
	 * tref2.setZ(s.getZ()+(targetRadius/dst2)*(tref2.getZ()-s.getZ()));
	 * 
	 * sb.setX(s.getX()+(sourceRadius/dst2)*(sb.getX()-s.getX()));
	 * sb.setY(s.getY()+(sourceRadius/dst2)*(sb.getY()-s.getY()));
	 * sb.setZ(s.getZ()+(sourceRadius/dst2)*(sb.getZ()-s.getZ()));
	 * 
	 * sd.setX(s.getX()-(sb.getX()-s.getX()));
	 * sd.setY(s.getY()-(sb.getY()-s.getY()));
	 * sd.setZ(s.getZ()-(sb.getZ()-s.getZ()));
	 * 
	 * ta.setX((s.getX()-(tref1.getX()-s.getX()))+(t.getX()-s.getX()));
	 * ta.setY((s.getY()-(tref1.getY()-s.getY()))+(t.getY()-s.getY()));
	 * ta.setZ((s.getZ()-(tref1.getZ()-s.getZ()))+(t.getZ()-s.getZ()));
	 * 
	 * tc.setX(tref1.getX()+(t.getX()-s.getX()));
	 * tc.setY(tref1.getY()+(t.getY()-s.getY()));
	 * tc.setZ(tref1.getZ()+(t.getZ()-s.getZ()));
	 * 
	 * tb.setX((s.getX()-(tref2.getX()-s.getX()))+(t.getX()-s.getX()));
	 * tb.setY((s.getY()-(tref2.getY()-s.getY()))+(t.getY()-s.getY()));
	 * tb.setZ((s.getZ()-(tref2.getZ()-s.getZ()))+(t.getZ()-s.getZ()));
	 * 
	 * td.setX(tref2.getX()+(t.getX()-s.getX()));
	 * td.setY(tref2.getY()+(t.getY()-s.getY()));
	 * td.setZ(tref2.getZ()+(t.getZ()-s.getZ()));
	 * 
	 * return new V[]{sa,sb,sc,sd,ta,tb,tc,td}; }
	 */
	/*
	 * private V[] getEdgeVertices(Edge edge){ double
	 * distance=calculateDistance(edge.getSourceNode(),edge.getTargetNode());
	 * double ang1=getAng1(edge.getSourceNode(),edge.getTargetNode(),distance);
	 * double
	 * ang2=getAng2(edge.getSourceNode(),edge.getTargetNode(),distance,ang1);
	 * return calcVerts(edge, edge.getSourceNode(),edge.getTargetNode(),ang1,
	 * ang2); }
	 * 
	 * private V[] calcVerts(Edge edge, Node one, Node two, double ang1, double
	 * ang2){ V[] v = new V[8]; double cos1 = Math.cos(ang1); double sin1 =
	 * Math.sin(ang1); double cos2 = Math.cos(ang2); double sin2 =
	 * Math.sin(ang2); double right = Math.PI/2.0; double sr =
	 * edge.getSourceRadius(); double tr = edge.getTargetRadius();
	 * 
	 * v[0]= new V(one.getX() + sr*cos2*sin1, one.getY() + sr*sin2*sin1,
	 * one.getZ() + sr*cos1); v[1]= new V(one.getX() - sr*cos2*sin1, one.getY()
	 * + sr*sin2*sin1, one.getZ() + sr*cos1); v[2]= new V(one.getX() +
	 * sr*cos2*sin1, one.getY() - sr*sin2*sin1, one.getZ() + sr*cos1); v[3]= new
	 * V(one.getX() - sr*cos2*sin1, one.getY() - sr*sin2*sin1, one.getZ() +
	 * sr*cos1); v[4]= new V(two.getX() + tr*cos2*sin1, two.getY() +
	 * tr*sin2*sin1, two.getZ() + tr*cos1); v[5]= new V(two.getX() -
	 * tr*cos2*sin1, two.getY() + tr*sin2*sin1, two.getZ() + tr*cos1); v[6]= new
	 * V(two.getX() + tr*cos2*sin1, two.getY() - tr*sin2*sin1, two.getZ() +
	 * tr*cos1); v[7]= new V(two.getX() - tr*cos2*sin1, two.getY() -
	 * tr*sin2*sin1, two.getZ() + tr*cos1);
	 * 
	 * return v; }
	 * 
	 * private double getAng1(Node one, Node two, double distance){ double angle
	 * = (one.getZ()-two.getZ())/distance; angle=Math.acos(angle); return angle;
	 * }
	 * 
	 * private double getAng2(Node one, Node two, double distance, double ang1){
	 * double angle = (one.getY()-two.getY())/distance*Math.sin(ang1);
	 * angle=Math.asin(angle); return angle; }
	 * 
	 * private double calculateDistance(Node one, Node two){ double dX =
	 * one.getX()-two.getX(); double dY = one.getY()-two.getY(); double dZ =
	 * one.getZ()-two.getZ(); return Math.sqrt(dX*dX + dY*dY + dZ*dZ); }
	 */
}