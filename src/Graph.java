
/*********************************************************************
 *
 *      Project: Network Visualization
 *      Author:  Kevin Bartlett
 *      Date:    2015
 *
 *      This class represents information of a 3D graph.  An object
 *      of this class cannot be exported on its own, but it can be
 *      given to a GraphWriter class to be exported into a supported
 *      file format.
 *
 *********************************************************************/

import java.util.ArrayList;
import java.util.Collections;

public class Graph
{
 private boolean directed;
 private ArrayList<Edge> edgeList;
 private ArrayList<Node> nodeList;
 private ArrayList<GraphColor> colorList;
 private GraphColor[][][] colorDB;
 private float minX,minY,minZ,maxX,maxY,maxZ;
 public Graph()
 {
  directed = false;
  nodeList = new ArrayList<Node>();
  edgeList = new ArrayList<Edge>();
  colorList = new ArrayList<GraphColor>();
  colorDB = new GraphColor[256][256][256];
  minX=0;
  minY=0;
  minZ=0;
  maxX=0;
  maxY=0;
  maxZ=0;
 }
 
 public Graph(Graph g){
   this(g.getDirected(), g.getNodeList(), g.getEdgeList(), g.getColorList(), g.getColorDB());
  minX=g.getMinX();
  minY=g.getMinY();
  minZ=g.getMinZ();
  maxX=g.getMaxX();
  maxY=g.getMaxY();
  maxZ=g.getMaxZ();
 }
  public Graph(boolean _directed, ArrayList<Node> _nodeList, ArrayList<Edge> _edgeList, ArrayList<GraphColor> _colorList, GraphColor[][][] _colorDB)
 {
  directed = _directed;
  colorDB = _colorDB;
  nodeList = new ArrayList<Node>();
  edgeList = new ArrayList<Edge>();
  
    for(Node n : _nodeList){
      nodeList.add(n.clone());
    }
    for(Edge e : _edgeList){
      edgeList.add(e.clone());
    }
    
  colorList = _colorList;
 }  
  public float getMinX(){
    return minX;
  }
  public float getMinY(){
    return minY;
  }
  public float getMinZ(){
    return minZ;
  }
  
  public float getMaxX(){
    return maxX;
  }  
  public float getMaxY(){
    return maxY;
  }  
  public float getMaxZ(){
    return maxZ;
  }
  
 public boolean getDirected()
 {
  return directed;
 }

 public ArrayList<Edge> getEdgeList()
 {
  return edgeList;
 }

 public ArrayList<Node> getNodeList()
 {
  return nodeList;
 }

 public ArrayList<GraphColor> getColorList()
 {
  return colorList;
 }

 public GraphColor[][][] getColorDB()
 {
  return colorDB;
 }

 public Edge getEdge(String id)
 {
  Edge edge = new Edge();
  edge.setId(id);

  int index = Collections.binarySearch(edgeList, edge, Edge.comparator);

  if (index >= 0)
   return edgeList.get(index);
  else
   return null;
 }

 public Node getNode(String id)
 {
  Node node = new Node();
  node.setId(id);

  int index = Collections.binarySearch(nodeList, node, Node.comparator);

  if (index >= 0)
   return nodeList.get(index);
  else
   return null;
 }    
 
 public int getNodeListSize(){
   return nodeList.size();
 }
 
 public void addNode(Node node){
   int index=Collections.binarySearch(nodeList, node, Node.comparator);
   if(index<0){
     nodeList.add(node);
     Collections.sort(nodeList, Node.comparator);
   }
   else
     nodeList.get(index).incrementDegree();
   
   GraphColor c = node.getColor();
   
   // Ensures that the argument node's color is not already
   // in the colorDatabase.  If it is, then it will reuse
   // that object.
   if (colorDB[c.getR()][c.getG()][c.getB()] == null)
   {
    colorDB[c.getR()][c.getG()][c.getB()] = c;
    colorList.add(c);
   }
 }
  
 public void addEdge(Edge edge){
   edgeList.add(edge);
   
   GraphColor c = edge.getColor();

   // Ensures that the argument edge's color is not already
   // in the colorDatabase.  If it is, then it will reuse
   // that object.
   if (colorDB[c.getR()][c.getG()][c.getB()] == null)
   {
    colorDB[c.getR()][c.getG()][c.getB()] = c;
    colorList.add(c);
   }
 }
 
 public void finalize(){
   Collections.sort(edgeList, Edge.comparator);
   //Collections.sort(nodeList, Node.comparator);
   removeDuplicates();
 }
 
 private void removeDuplicates(){
   GraphCollections.removeDuplicates(edgeList,Edge.comparator);
   //GraphCollections.removeDuplicates(nodeList,Node.comparator);
 }

 public void setDirected(boolean directed)
 {
  this.directed = directed;
 }
 
 public void resetLists(){
   edgeList.clear();
   nodeList.clear();
 }
 
 public void resetEdgeList(){
   edgeList.clear();
 }
 
 public void addEdgePreSorted(Edge edge){
   edgeList.add(edge);
 }
 
 public void addColorPreSorted(GraphColor c){
   if (colorDB[c.getR()][c.getG()][c.getB()] == null)
   {
     colorDB[c.getR()][c.getG()][c.getB()] = c;
     colorList.add(c);
   }
 }
 
 public Graph clone(){
  Graph clone =new Graph(directed, nodeList, edgeList, colorList, colorDB);
  return clone;
 }
 
 public Graph getGraph(){
   return this;
 }
}
