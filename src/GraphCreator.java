import java.util.ArrayList;
/*GraphCreator class
 * This class is used as an adapter to hold algorithms for graph creation.
 * Most algorithms reside in this class providing a higher level view of subgraph creation
 * from imported graph data.
 */
public class GraphCreator{
  private GraphV2 baseGraph;
  private GraphV2 graph;
  private boolean radiusFromDegree;
  private boolean edgesThroughTime;
  private boolean showEdges;
  private float M = 1.0f;
  private int maxDegree;

  
  public GraphCreator(){
    radiusFromDegree=true;
    edgesThroughTime=false;
    showEdges=true;
  }
  
  public GraphCreator(GraphV2 _baseGraph){
    baseGraph=_baseGraph;
    radiusFromDegree=true;
    edgesThroughTime=false;
    showEdges=true;
    resetGraph();
  }
  public int getMaxDegree(){ return maxDegree;}
  
  public void resetGraph(){
    graph=baseGraph.clone();
    graph.resetLists();
  }
  
  public void setM(float M){
    this.M = M;
  }
  
  public void setShowEdges(boolean in){
    showEdges=in;
  }
  
  public void setGraph(GraphV2 _graph){
    baseGraph=_graph;
    graph=baseGraph.clone();
  }
  
  public void setRadiusFromDegree(boolean _radiusFromDegree){
    radiusFromDegree=_radiusFromDegree;
  }  
  public void setEdgesThroughTime(boolean _edgesThroughTime){
    edgesThroughTime=_edgesThroughTime;
  }
  
  public Graph getGraph(){
    manipulateFinalGraph();
    return graph;
  }  
  public GraphV2 getBaseGraph(){
    return baseGraph;
  }
  
  public void filterByEdge(float min, float max){
    graph.filterByWeight(min, max);
  }
  
  public void filterByTime(float time_slot, boolean is3D){    
    graph.set3D(is3D);
    graph.resetLists();
    graph.generateTimeSlicesRange(time_slot, time_slot);
  }
  
  public void generateFull3D(){
    graph.set3D(true);
    graph.resetLists();
    graph.generate();
  }
  
  public void generate3DSlices(){
    graph.set3D(true);
    graph.resetLists();
    graph.generateAllTimeSlices();    
  }
  
  public void generate2DSlices(){
    edgesThroughTime=true;
    graph.set3D(false);
    graph.resetLists();
  System.out.println("running time slices...");
    graph.generateAllTimeSlices();
  System.out.println("final manipulation...");
  }
  
  private void manipulateFinalGraph(){
    ArrayList<Node> nodes = graph.getNodeList();
      if(radiusFromDegree){
        for(Node n : nodes){
          if(n.getDegree()>maxDegree){
            maxDegree=n.getDegree();
          }
        }
        M=(M/(float)maxDegree);
        for(Node n : nodes){
          n.makeRadiusDependentOnDegree(M);
        }
      }
      if(!showEdges){
        graph.resetEdgeList();
      }
      if(edgesThroughTime){
        System.out.println("creating noodles");
        createEdgesThroughTime(nodes);
      }
  }
  
  private void createEdgesThroughTime(ArrayList<Node> nodes){
    ArrayList<Float> times = graph.getTimesList();
    int max = nodes.size()-1;
    int i = 0;
    int indexOf=-1;
    String match;
    Node source,target;
    for(i=0; i<times.size(); i++){
      System.out.println(times.get(i).floatValue());
    }
    i=0;
    while(i<max){
      source = nodes.get(i);
      match=source.getId();
      indexOf = match.indexOf("_t=");
      if(indexOf>-1){
        target = nodes.get(i+1);
        match = match.substring(0,indexOf);
        if(target.getId().contains(match)){
          int stemp = Integer.parseInt(source.getId().substring(indexOf+3,source.getId().length()));
          int ttemp = Integer.parseInt(target.getId().substring(indexOf+3,target.getId().length()))-stemp;
          if(ttemp==1){
            Edge newEdge = new Edge();
            newEdge.setSource(source.getId());
            newEdge.setTarget(target.getId());
            newEdge.setSourceRadius(source.getRadius());
            newEdge.setTargetRadius(target.getRadius());
            newEdge.setSourceNode(source);
            newEdge.setTargetNode(target);
            newEdge.setId(source.getId() + target.getId());
            newEdge.setNoodle(true);
            graph.addEdge(newEdge);
          }
        }
      }
      i++;
    }
  }
}