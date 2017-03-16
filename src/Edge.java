
/*********************************************************************
 *
 *      Project: Network Visualization
 *      Author:  Kevin Bartlett
 *      Date:    2015
 *
 *      This is a helper class for the Graph class.  Target and
 *      source hold the indices of the target and source Nodes in
 *      the Graph class's list of Nodes.
 *
 *********************************************************************/

import java.util.Comparator;

public class Edge
{
 protected float sourceRadius, targetRadius;
 protected float weight;
 protected String source;
 protected String target;
 protected Node sourceNode;
 protected Node targetNode;
 protected String id;
 protected GraphColor color;
 protected boolean noodle;

 public static final Comparator<Edge> comparator = new Comparator<Edge>()
 {
  public int compare(Edge e1, Edge e2)
  {
   return e1.getId().compareTo(e2.getId());
  }
 };

 public Edge()
 {
  weight = 0.0f;
  sourceRadius = 0.5f;
  targetRadius = 0.5f;
  source = "";
  target = "";
  color = new GraphColor(255, 255, 255);
  noodle=false;
 }
 public Edge(Edge edge)
 {
  this.sourceRadius = edge.sourceRadius;
  this.targetRadius = edge.targetRadius;
  this.weight = edge.weight;
  this.source = edge.source;
  this.target = edge.target;
  this.id = edge.id;
  this.color = edge.color;
  this.noodle = edge.noodle;
 }

 public String getId()
 {
  return id;
 }
 
 public void setNoodle(boolean isNoodle){
   noodle = isNoodle;
 }
 public Node getSourceNode(){  return sourceNode;  }
 public Node getTargetNode(){  return targetNode;  }
public void setSourceNode(Node node){  sourceNode=node;  }
public void setTargetNode(Node node){  targetNode=node;  }
public boolean isNoodle(){
  return noodle;
}
 public float getSourceRadius()
 {
  return sourceRadius;
 }
 public float getTargetRadius()
 {
  return targetRadius;
 }

 public float getWeight()
 {
  return weight;
 }

 public String getSource()
 {
  return source;
 }

 public String getTarget()
 {
  return target;
 }

 public GraphColor getColor()
 {
  return color;
 }

 public void setSource(String source)
 {
  this.source = source;
 }

 public void setTarget(String target)
 {
  this.target = target;
 }

 public void setTargetRadius(float radius)
 {
  this.targetRadius = radius;
 } 
 public void setSourceRadius(float radius)
 {
  this.sourceRadius = radius;
 }
 
 public void setRadius(float radius){
   this.sourceRadius = radius;
   this.targetRadius = radius;   
 }

 public void setWeight(float weight)
 {
  this.weight = weight;
 }

 public void setId(String id)
 {
  this.id = id;
 }

 public void setColor(GraphColor color)
 {
  this.color = color;
 }
 
 public Edge clone(){
   return new Edge(this);
 }
}
