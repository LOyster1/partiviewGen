/*********************************************************************
*
*      Project: Network Visualization
*      Author:  Ben Gurganious
*      Date:    2015
*
*      A graph class that has base lists for nodes/edges. These base lists
*      should not be used for output. Instead, populate the lists of the super class
*      for the nodes/edges desired.
*      Currently this class assists in creating graphs from edges with time values.   
* 
*      Note: This class does not check for node overlapping when generating 2D time slices.
*
**********************************************************************/
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphV2 extends Graph{  
 private ArrayList<EdgeV2> baseEdgeList;
 private ArrayList<Node> baseNodeList;
 private float start_time;
 private float end_time, t_end_time;
 private float current_time_slice;
 private float lowWeight;
 private float highWeight;
 private boolean is3D;
 private boolean radiusFromDegree;
 private float zRadius;
 private int lowestZTimeOffset;
 private int greatestZTimeOffset;
 private ArrayList<Float> validTimes;
 private String graphName;
 public int individualFileDate;
 
  public GraphV2()
  {
    super();
    baseEdgeList = new ArrayList<EdgeV2>();
    baseNodeList = new ArrayList<Node>();
    start_time=-1;
    end_time=0;
    current_time_slice=-1;
    is3D=true;
    radiusFromDegree=false;
    zRadius=0;
    lowWeight=-1;
    highWeight=-1;
    validTimes=new ArrayList<Float>();
  }
  
  public GraphV2(GraphV2 g)
  {
    super(g);
    ArrayList<Node> tempNList=g.getBaseNodeList();
    ArrayList<EdgeV2> tempEList=g.getBaseEdgeList();
      Node[] tempNodes= new Node[tempNList.size()];
      EdgeV2[] tempEdges= new EdgeV2[tempEList.size()];
      tempNodes=tempNList.toArray(tempNodes);
      tempEdges=tempEList.toArray(tempEdges);
      baseNodeList=new ArrayList<Node>();
      baseEdgeList = new ArrayList<EdgeV2>();
    validTimes=new ArrayList<Float>();
  
    for(Float f : g.getTimesList()){
     validTimes.add(f); 
    }
    for(Node n : tempNodes){
      baseNodeList.add(n);
    }
    for(EdgeV2 e : tempEdges){
      baseEdgeList.add(e);
    }
    
    start_time=g.getStartTime();
    end_time=g.getEndTime();
    lowWeight=g.getLowWeight();
    highWeight=g.getHighWeight();
    current_time_slice=-1;
    Collections.sort(baseEdgeList, Edge.comparator);
    Collections.sort(baseNodeList, Node.comparator);
  }
  
  public ArrayList<Float> getTimesList(){ return validTimes;  }
  
  public float getLowWeight(){
    return lowWeight;
  }
  
  public void set3D(boolean _is3D){
    is3D=_is3D;
  }
  
  public void setRadiusFromDegree(boolean _radiusFromDegree){
    radiusFromDegree=_radiusFromDegree;
  }
  
  public float getHighWeight(){
    return highWeight;
  }
  
  public float getStartTime(){
    return start_time;
  }
  
  public float getEndTime(){
    return end_time;
  }
  public void setGraphName(String nameIn){
      graphName=nameIn;
  }
  public String getGraphName(){
      return graphName;
  }
  public void setDateFromFileName(String fileDate)
  {                
        int year;
        int month;
        int day;

        year = Integer.parseInt(fileDate.substring(0,4));

        month = Integer.parseInt(fileDate.substring(5, 7));

        day = Integer.parseInt(fileDate.substring(8, 10));
        System.out.println("Year: "+year+" Month: "+ month+" Day: " +day);

        // Not adjusted for Leap year.
        year = year * 365;
        switch((int)month)
        {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                    month = month * 31;
                    break;
            case 4:
            case 6:
            case 9:
            case 11:
                    month = month *30;
                    break;
            case 2:
                    month = month *28;
                    break;
        }
        individualFileDate = day + month + year;

    }
    public void offsetFileDate(int offSetIn)
    {
        individualFileDate=individualFileDate-offSetIn;
    }
    public int getIndividualFileDate(){
        return individualFileDate;
    }
  /*
  public boolean addBaseEdge(EdgeV2 edge){
   
  if (edge != null)
  {
   /*int index = Collections
    .binarySearch(baseEdgeList, edge, Edge.comparator);
   if (index < 0)
   {
    baseEdgeList.add(edge);
    
    
    GraphCollections.insertSorted(edge, baseEdgeList, EdgeV2.comparator);
    if(edge.getTime()<start_time || start_time==-1)
      start_time=edge.getTime();
    
    if(edge.getEndTime()>end_time)
      end_time=edge.getEndTime();
    
    if(edge.getWeight() < lowWeight || lowWeight == -1)
      lowWeight=edge.getWeight();
    if(edge.getWeight() > highWeight || highWeight == -1)
      highWeight=edge.getWeight();
   /* 
    Collections.sort(baseEdgeList, Edge.comparator);
   }
   else
   {
    return false;
   }

   return true;
  }
   return false;
  }
  
 public boolean addBaseNode(Node node)
 {
  if (node != null)
  {/*
   int index = Collections
    .binarySearch(baseNodeList, node, Node.comparator);
   if (index < 0)
   {
    baseNodeList.add(node);
    Collections.sort(baseNodeList, Node.comparator);
    
    GraphCollections.insertSorted(node, baseNodeList, Node.comparator);
    if(node.getZ()>zRadius){
      zRadius=node.getZ()+500;
    }

    return true;
   }
   return false;
  }
 */
 public void resetSuperGraph(){
   super.resetLists();
 }
 
 public Graph generateAllTimeSlices(){
   System.out.println("time slice count=" + validTimes.size());
   return generateTimeSlicesRange(start_time,end_time);
 }
 
 public void filterByWeight(float min, float max){
   Edge tEdge;
   for(int i=baseEdgeList.size()-1; i>=0; i--){    
     tEdge=baseEdgeList.get(i);
     if(tEdge.getWeight()<min || tEdge.getWeight()>max){
       baseEdgeList.remove(i);
     }
   }
 }
 
 public Graph generate(){
   for(Edge edge : baseEdgeList){
     addBranch(edge);
   }
   return getGraph();
 }
 
 public Graph generateTimeSlicesRange(float start, float end){
  t_end_time=end;
  current_time_slice=start;
  while(runNextTimeSlice()){}
  baseEdgeList.addAll(tempList);
  tempList.clear();
  Collections.sort(baseEdgeList,EdgeV2.comparator);
  super.finalize();
  return super.getGraph();
 }
 
   ArrayList<EdgeV2> tempList = new ArrayList<EdgeV2>();
   int timeIndex=0;
 //returns false when time slice is out of range for the current graph
 public boolean runNextTimeSlice(){
   if(validTimes.size()==timeIndex){
     timeIndex=0;
     return false;
   }/*
   if(current_time_slice>t_end_time)
     return false;
   
   if(current_time_slice==-1)
     current_time_slice=start_time;
   */
   current_time_slice=validTimes.get(timeIndex).floatValue();
   
   EdgeV2 tempEdge;
   for(int i=baseEdgeList.size()-1;i>=0;i--){
     tempEdge=baseEdgeList.get(i);
     if(tempEdge.isInTimeSlot(current_time_slice)){
       addBranch(tempEdge);
       //tempList.add(tempEdge);
       //baseEdgeList.remove(i);
     }
   }
   timeIndex++;
   //current_time_slice++;
   return true;
 }
 
 private void addBranch(Edge edge){  
       Node temp_node=new Node(getBaseNode(edge.getSource()));
       Node temp_node1=new Node(getBaseNode(edge.getTarget()));
       Edge temp_edge=new Edge(edge);
       temp_node.setId(temp_node.getId()+"_t="+timeIndex);
       temp_node1.setId(temp_node1.getId()+"_t="+timeIndex);
       addNodeForSlice(temp_node);
       addNodeForSlice(temp_node1);
       temp_edge.setSource(temp_node.getId());
       temp_edge.setTarget(temp_node1.getId());
       temp_edge.setSourceNode(temp_node);
       temp_edge.setTargetNode(temp_node1);
       temp_edge.setId(edge.getId()+current_time_slice);
       addEdge(temp_edge);
 }
 
 private void addNodeForSlice(Node node){
   if(is3D){
     node.setZ(node.getZ()+current_time_slice * zRadius);
   }
   else{
     node.setZ(timeIndex*500);
   }
   addNode(node);
 }
 
 public void addBaseNode(Node node){
   baseNodeList.add(node);   
   
   GraphColor c = node.getColor();

   // Ensures that the argument node's color is not already
   // in the colorDatabase.  If it is, then it will reuse
   // that object.
       if(node.getZ()>zRadius){
      zRadius=node.getZ()+500;
    }
 }
  
 public void addBaseEdge(EdgeV2 edge){
   baseEdgeList.add(edge);
   /*
    if(edge.getTime()<start_time || start_time==-1)
      start_time=edge.getTime();
    
    if(edge.getEndTime()>end_time)
      end_time=edge.getEndTime();
    */
   validTimes.add(new Float(edge.getTime()));
    if(edge.getWeight() < lowWeight || lowWeight == -1)
      lowWeight=edge.getWeight();
    if(edge.getWeight() > highWeight || highWeight == -1)
      highWeight=edge.getWeight();
 }
 
 public void finalize(){
   Collections.sort(baseEdgeList, Edge.comparator);
   Collections.sort(baseNodeList, Node.comparator);
   Collections.sort(validTimes);
   GraphCollections.removeDuplicates(validTimes, new Comparator<Float>()
 {
  public int compare(Float e1, Float e2)
  {
   return e1.compareTo(e2);
  }
 });
   super.finalize();
 }
 public Edge getBaseEdge(String id)
 {
  Edge edge = new Edge();
  edge.setId(id);

  int index = Collections.binarySearch(baseEdgeList, edge, Edge.comparator);

  if (index >= 0)
   return baseEdgeList.get(index);
  else
   return null;
 }

 public Node getBaseNode(String id)
 {
  Node node = new Node();
  node.setId(id);

  int index = Collections.binarySearch(baseNodeList, node, Node.comparator);

  if (index >= 0)
   return baseNodeList.get(index);
  else
   return null;
 } 
 
 // Finds the largest z 
public int greatestZ()
{
        greatestZTimeOffset = 0;
        for(int i = 0; i < baseNodeList.size(); i++)
        {
                if(greatestZTimeOffset < baseNodeList.get(i).getZ())
                {
                        greatestZTimeOffset = (int)baseNodeList.get(i).getZ();
                }
        }
        return greatestZTimeOffset;
}

// Finds the smallest z
public int lowestZ()
{
        lowestZTimeOffset = (int)baseNodeList.get(0).getZ();
        for(int i = 0; i < baseNodeList.size(); i++)
        {
                if(lowestZTimeOffset > baseNodeList.get(i).getZ())
                {
                        lowestZTimeOffset = (int)baseNodeList.get(i).getZ();
                }
        }
        return lowestZTimeOffset;
}
 
 public int getBaseNodeListSize(){
   return baseNodeList.size();
 }
 
 public ArrayList<Node> getBaseNodeList(){
   return baseNodeList;
 }
 
 public ArrayList<EdgeV2> getBaseEdgeList(){
   return baseEdgeList;
 }
 
 public GraphV2 clone(){
   return new GraphV2(this);
 }
}