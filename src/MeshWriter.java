

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MeshWriter{
	private String path; // String that holds the path of the file
	private String fileName; // String to hold the name of the file to be read
	private ArrayList<Node> baseNodeList; // Holds the unaltered NodeList
	private static ArrayList<Node> nodesOfCompleteGraph = new ArrayList<>(); // Holds the Nodes in an array
	private static ArrayList<Layers> layers = new ArrayList<>(); // Holds the different TimeSlices in an array
        private static ArrayList<Layers> discreteTimeslices = new ArrayList<>();
	private static ArrayList<Centroid> centroids = new ArrayList<>(); // Holds the centroid of the layers
	private GraphV2 completeGraph; // Graph object
	private int range; // Interval size
        public static ArrayList<GraphV2> individualGraphs;
	
	// Constructor that sets the path, filename, and completeGraph
	MeshWriter(String path, String fileName, Graph graph2)
	{
		this.path = path;
		this.fileName = fileName;
		this.completeGraph = (GraphV2) graph2;
	}

	// Constructor that sets the path, filename, completeGraph, and interval
	MeshWriter(String path, String fileName, Graph graph2, int range)
	{
		this.path = path;
		this.fileName = fileName;
		this.completeGraph = (GraphV2) graph2;
		this.range = range;
	}
	
	public static ArrayList<Node> getNodesOfCompleteGraph()
	{
		return nodesOfCompleteGraph;
	}
	public static void setNodesOfCompleteGraph(ArrayList<Node> points)
	{
		MeshWriter.nodesOfCompleteGraph = points;
	}
	
	// Fill the nodesOfCompleteGraph array with the nodes from the baseNodeList of the completeGraph object
	public void setBaseNodeList()
	{
		baseNodeList = completeGraph.getBaseNodeList();
	}
        public void setIndividualGraphs(ArrayList<GraphV2> individualGraphsIn){
            individualGraphs=individualGraphsIn;
        }
        public ArrayList<GraphV2> getIndividualGraphs(){
            return  individualGraphs;
        }
        public ArrayList<Layers> getDiscreteLayers(){
            return discreteTimeslices;
        }
	
	// Sorts the nodesOfCompleteGraph array by z
	public static void sortByZ()
	{
		for(int i = 0; i < nodesOfCompleteGraph.size(); i++)
		{
			for(int j = 0; j < i; j++)
			{
				if(nodesOfCompleteGraph.get(i).getZ() < nodesOfCompleteGraph.get(j).getZ())
				{
					Collections.swap(nodesOfCompleteGraph, i, j);
				}
			}
		}
	}
	
        //This method iterates through all layers of CompleteGraph to update the modularity classes 
        //with that of individual graphs representing each layer
        public void updateModClassFromIndiv()
	{
                System.out.println("Update Mod Class ");
                for(int i = completeGraph.lowestZ(); i <= completeGraph.greatestZ(); i++)//Iterate through all layers of Complete Graph
		{  
                   // System.out.println("Current Z= " + i);
			for(int j=1; j<=individualGraphs.size()-1; j++)//Iterate through the Individual graphs
                        {
                            //If the Z value of the Complete completeGraph is the time stamp of an individual completeGraph process further
                            if(i==individualGraphs.get(j).getIndividualFileDate())
                            {
                                
                               // System.out.println("\n\n i value: "+i+" File Name: " + individualGraphs.get(j).getGraphName() + " File Date: " + individualGraphs.get(j).getIndividualFileDate()+"\n\n");
                                for(int k=0; k<=individualGraphs.get(j).getNodeListSize()-1;k++)
                                {
                                    //For each node in the individual completeGraph, update the mod class of the node in the complete completeGraph at that Z layer
                                    for(int x=0; x<=completeGraph.getNodeListSize()-1;x++)
                                    {
                                        if(completeGraph.getNodeList().get(x).getZ()==individualGraphs.get(j).getIndividualFileDate() &&
                                                completeGraph.getNodeList().get(x).getLabel().equals(individualGraphs.get(j).getNodeList().get(k).getLabel())){
                                           // System.out.println("Raw Data Label: "+ completeGraph.getNodeList().get(x).getLabel()+ " Mod Class: "+ completeGraph.getNodeList().get(x).getModClass()+
                                           // " Individual Raw Data: "+ individualGraphs.get(j).getNodeList().get(k).getLabel() + " Mod Clss: "+ individualGraphs.get(j).getNodeList().get(k).getModClass());
                                            completeGraph.getNodeList().get(x).setModClass(individualGraphs.get(j).getNodeList().get(k).getModClass());
                                    }
                                    }
                                    
                                }
                            }
                        }
		}
	}
        
        public void printNodeLabelAndMods()
        {
            String tempLabel;
            for(int i =0; i<completeGraph.getNodeListSize()-1;i++)
            {
                tempLabel=completeGraph.getNodeList().get(i).getLabel();
                
                for(int j = completeGraph.lowestZ(); j <= completeGraph.greatestZ()-1; j++)//Iterate through all layers of Complete Graph
		{  
                    if(completeGraph.getNodeList().get(i).getZ()==j)
                    {
                        System.out.println(tempLabel+" Z value "+completeGraph.getNodeList().get(i).z+" Mod Class "+completeGraph.getNodeList().get(j).getModClass());
                    }
//                    if(completeGraph.getNodeList().get(j).getLabel().equals(tempLabel))
//                    {
//                        System.out.println(tempLabel+" Z value "+completeGraph.getNodeList().get(j).z+" Mod Class "+completeGraph.getNodeList().get(j).getModClass());
//                    }
                    
                }
                System.out.println("\n");
            }
        }
        
        //Keep track of layers in a discrete fashion as opposed to continuous within a time range
        public void createDiscreteTimeSlice()
        {
            for(int i = completeGraph.lowestZ(); i <= completeGraph.greatestZ(); i++)
            {

                    if(!createNodeLayer(i, 1).getTimeSliceNodes().isEmpty())
                    {
                            discreteTimeslices.add(createNodeLayer(i, 1));
                            discreteTimeslices.get(i-completeGraph.lowestZ()).setDiscreteZ(i);
                    }
            }
        }
        
	// Create continuous time slices with the width of the float that gets entered into the method
	public void createTimeSlice(int range)
	{
		//System.out.println("in Create timeslice");
		//System.out.println("greatest z " + greatestZ());
                for(int i = completeGraph.lowestZ(); i <= completeGraph.greatestZ(); i+= range)
		//for(int i = lowestZ(); i <= greatestZ(); i+= range)
		{
			//System.out.println("lowestz: " + lowestZ());
			//System.out.println("first i: " + i);
			if(!createNodeLayer(i, range).getTimeSliceNodes().isEmpty())
			{
				//System.out.println("in: " + i);
				layers.add(createNodeLayer(i, range));
			}
		}
	}
        
	
	// Create an array of nodes for a timeSlice specified by the input of the method
	public static Layers createNodeLayer(int min, int range)
	{
		//System.out.println("minrange: " + (min+range));
		ArrayList<Node> temp = new ArrayList<>();
		Layers tempLayer = new Layers();
		for(int i = 0; i < getNodesOfCompleteGraph().size(); i++)
		{
			
			//System.out.println("i: " + i);
			//System.out.println("here: " + getNodesOfCompleteGraph().get(i).getZ());
			//System.out.println("if: " + min + range);
			if((getNodesOfCompleteGraph().get(i).getZ() < min + range) && (getNodesOfCompleteGraph().get(i).getZ() >= min))
			{
				temp.add(getNodesOfCompleteGraph().get(i));
			}
		}
		if(!temp.isEmpty())
		{
			tempLayer.setTimeSliceNodes(temp);
		}
		return tempLayer;
	}
	
	// Finds the largest z 
//	public int greatestZ()
//	{
//		int temp = 0;
//		for(int i = 0; i < baseNodeList.size(); i++)
//		{
//			if(temp < baseNodeList.get(i).getZ())
//			{
//				temp = (int)baseNodeList.get(i).getZ();
//			}
//		}
//		return temp;
//	}
//	
//	// Finds the smallest z
//	public int lowestZ()
//	{
//		int temp = (int)baseNodeList.get(0).getZ();
//		for(int i = 0; i < baseNodeList.size(); i++)
//		{
//			if(temp > baseNodeList.get(i).getZ())
//			{
//				temp = (int)baseNodeList.get(i).getZ();
//			}
//		}
//		return temp;
//	}
//	
        public int greatestZ(){
            return completeGraph.greatestZ();
        }
        public int lowestZ(){
            return completeGraph.lowestZ();
        }
        
        
	// Fills the gaps of the layers to make all the time slice layers be the same size
	public static void fillGapsInMesh()
	{
		int layerWMN = layerWithMostNodes();
		//System.out.println("LayerWMN size: " + layers.get(layerWMN).getSize());

		// Fills the gaps of the layers in front of the layer with the most nodes
		for(int i = layerWMN; i < layers.size() - 1; i++)
		{
			float shortestTheta;
			int shortestThetaPositionInArray;
			ArrayList<Float> initialUseChecker = new ArrayList<Float>();
			boolean hasBeenUsed = false;
			
			for(int k = 0; k < layers.get(i).getSize(); k++)
			{
				shortestTheta = (float) Math.abs(layers.get(i).getTheta(layers.get(i).getTimeSliceNodes().get(k).getX(), layers.get(i).getTimeSliceNodes().get(k).getY()) - layers.get(i+1).getTheta(layers.get(i+1).getTimeSliceNodes().get(0).getX(), layers.get(i+1).getTimeSliceNodes().get(0).getY()));
				shortestThetaPositionInArray = 0;
				for(int j = 0; j < layers.get(i+1).getSize(); j++)
				{
					if(shortestTheta > Math.abs(layers.get(i).getTheta(layers.get(i).getTimeSliceNodes().get(k).getX(), layers.get(i).getTimeSliceNodes().get(k).getY()) - layers.get(i+1).getTheta(layers.get(i+1).getTimeSliceNodes().get(j).getX(), layers.get(i+1).getTimeSliceNodes().get(j).getY())))
					{
						shortestTheta = (float) Math.abs(layers.get(i).getTheta(layers.get(i).getTimeSliceNodes().get(k).getX(), layers.get(i).getTimeSliceNodes().get(k).getY()) - layers.get(i+1).getTheta(layers.get(i+1).getTimeSliceNodes().get(j).getX(), layers.get(i+1).getTimeSliceNodes().get(j).getY()));
						shortestThetaPositionInArray = j;
					}
				}
				for(int l = 0; l < initialUseChecker.size(); l++)
				{
					if(shortestTheta == initialUseChecker.get(l))
					{
						hasBeenUsed = true;
					}
				}
				
				if(layers.get(i+1).getSize() >= layers.get(layerWMN).getSize())
				{
					break;
				}
				
				//if(hasBeenUsed == false)
				//{
					initialUseChecker.add(shortestTheta);
				//}
				//else
				//{
					layers.get(i+1).getTimeSliceNodes().add(shortestThetaPositionInArray, layers.get(i+1).getTimeSliceNodes().get(shortestThetaPositionInArray));
				//}
			}
			//printLayers();
		}

		// Fills the gaps of the layers behind the layer with the most nodes
		for(int i = layerWMN; i > 0; i--)
		{
			float shortestTheta;
			int shortestThetaPositionInArray;
			ArrayList<Float> initialUseChecker = new ArrayList<Float>();
			boolean hasBeenUsed = false;
			
			for(int k = 0; k < layers.get(i).getSize(); k++)
			{
				shortestTheta = (float) Math.abs(layers.get(i).getTheta(layers.get(i).getTimeSliceNodes().get(k).getX(), layers.get(i).getTimeSliceNodes().get(k).getY()) - layers.get(i-1).getTheta(layers.get(i-1).getTimeSliceNodes().get(0).getX(), layers.get(i-1).getTimeSliceNodes().get(0).getY()));
				shortestThetaPositionInArray = 0;
				for(int j = 0; j < layers.get(i-1).getSize(); j++)
				{
					if(shortestTheta > Math.abs(layers.get(i).getTheta(layers.get(i).getTimeSliceNodes().get(k).getX(), layers.get(i).getTimeSliceNodes().get(k).getY()) - layers.get(i-1).getTheta(layers.get(i-1).getTimeSliceNodes().get(j).getX(), layers.get(i-1).getTimeSliceNodes().get(j).getY())))
					{
						shortestTheta = (float) Math.abs(layers.get(i).getTheta(layers.get(i).getTimeSliceNodes().get(k).getX(), layers.get(i).getTimeSliceNodes().get(k).getY()) - layers.get(i-1).getTheta(layers.get(i-1).getTimeSliceNodes().get(j).getX(), layers.get(i-1).getTimeSliceNodes().get(j).getY()));
						shortestThetaPositionInArray = j;
					}
				}
				for(int l = 0; l < initialUseChecker.size(); l++)
				{
					if(shortestTheta == initialUseChecker.get(l))
					{
						hasBeenUsed = true;
					}
				}
				
				if(layers.get(i-1).getSize() >= layers.get(layerWMN).getSize())
				{
					//System.out.println("break2: " + layers.get(i-1).getSize());
					break;
				}
				
				//if(hasBeenUsed == false)
				//{
					initialUseChecker.add(shortestTheta);
				//}
				//else
				//{
					layers.get(i-1).getTimeSliceNodes().add(shortestThetaPositionInArray, layers.get(i-1).getTimeSliceNodes().get(shortestThetaPositionInArray));
				//}
			}
		}
		//System.out.println("layerwmn after: " + layers.get(layerWithMostNodes()).getSize());
	}
	
	// Compares two nodes from a layer and the next layer in the array. Returns the theta between the two nodes
	public double thetaBetweenTwoNodes(int layerPosition, int firstNodePosition, int secondNodePosition)
	{
		double firstTheta = layers.get(layerPosition).getTheta(layers.get(layerPosition).getTimeSliceNodes().get(firstNodePosition).getX(), layers.get(layerPosition).getTimeSliceNodes().get(firstNodePosition).getY());
		double secondTheta =  layers.get(layerPosition + 1).getTheta(layers.get(layerPosition + 1).getTimeSliceNodes().get(secondNodePosition).getX(), layers.get(layerPosition + 1).getTimeSliceNodes().get(secondNodePosition).getY());
		return firstTheta - secondTheta;
	}
	
	// compares two layers and makes them even in size. This is necessary for partiview's mesh to work
	public void fillGapsInMesh(int layerPosition, ArrayList<Layers> tempLayers)
	{
		double shortestTheta; // The value of the shortest theta between two nodes
		int shortestThetaPosition; // The position in the array of the shortest theta
		//ArrayList<Double> initialUseChecker = new ArrayList<Double>(); // Array of the numbers that have been used
		//ArrayList<Node> initialNodeUseChecker = new ArrayList<Node>(); // Array of the nodes that have been used
		//boolean hasBeenUsed = false;
		
		//System.out.println("layer1 size:" + tempLayers.get(layerPosition).getTimeSliceNodes().size() + " layer2 size: " + tempLayers.get(layerPosition + 1).getTimeSliceNodes().size());
		
		//System.out.println("layer Size: " + tempLayers.get(layerPosition).getTimeSliceNodes().size());
		//System.out.println("layer+1 Size: " + tempLayers.get(layerPosition + 1).getTimeSliceNodes().size());
		
		// If the first layer is larger than the second layer
		if(tempLayers.get(layerPosition).getTimeSliceNodes().size() > tempLayers.get(layerPosition + 1).getTimeSliceNodes().size())
		{
			//System.out.println("---- in If ----");
			for(int firstNodePosition = 0; firstNodePosition < tempLayers.get(layerPosition).getTimeSliceNodes().size(); firstNodePosition++)
			{
				//hasBeenUsed = false;
				
				// Compares the firstNode to the 0 position secondNode and sets it as the current shortest theta to have an initial value to compare to
				shortestTheta = Math.abs(thetaBetweenTwoNodes(layerPosition, firstNodePosition, 0));
				shortestThetaPosition = 0;
				
				for(int secondNodePosition = 0; secondNodePosition < tempLayers.get(layerPosition + 1).getTimeSliceNodes().size(); secondNodePosition++)
				{
					if(shortestTheta > Math.abs(thetaBetweenTwoNodes(layerPosition, firstNodePosition, secondNodePosition)))
					{
						shortestTheta = Math.abs(thetaBetweenTwoNodes(layerPosition, firstNodePosition, secondNodePosition));
						shortestThetaPosition = secondNodePosition;
					}
				}
				/*for(Node n: initialNodeUseChecker)
				{
					if(tempLayers.get(layerPosition + 1).getTimeSliceNodes().get(shortestThetaPosition).id == n.id && 
						tempLayers.get(layerPosition + 1).getTimeSliceNodes().get(shortestThetaPosition).getX() == n.getX() && 
						tempLayers.get(layerPosition + 1).getTimeSliceNodes().get(shortestThetaPosition).getY() == n.getY() &&
						tempLayers.get(layerPosition + 1).getTimeSliceNodes().get(shortestThetaPosition).getZ() == n.getZ())
					{
						hasBeenUsed = true;
					}
				}*/
				

				if(tempLayers.get(layerPosition).getTimeSliceNodes().size() == tempLayers.get(layerPosition + 1).getTimeSliceNodes().size())
				{
					break;
				}
				
				/*if(hasBeenUsed == false)
				{
					System.out.println("False id: " + tempLayers.get(layerPosition + 1).getTimeSliceNodes().get(shortestThetaPosition).id);
					initialNodeUseChecker.add(tempLayers.get(layerPosition + 1).getTimeSliceNodes().get(shortestThetaPosition));
				}*/
				//else
				//{
					//System.out.println("True id: " + tempLayers.get(layerPosition + 1).getTimeSliceNodes().get(shortestThetaPosition).id);
					tempLayers.get(layerPosition + 1).getTimeSliceNodes().add(shortestThetaPosition, tempLayers.get(layerPosition + 1).getTimeSliceNodes().get(shortestThetaPosition));
				//}
			}
		}
		
		// If the second layer is larger than the first layer
		else if(tempLayers.get(layerPosition).getTimeSliceNodes().size() < tempLayers.get(layerPosition + 1).getTimeSliceNodes().size())
		{
			//System.out.println("---- in Else If ----");
			for(int secondNodePosition = 0; secondNodePosition < tempLayers.get(layerPosition + 1).getTimeSliceNodes().size(); secondNodePosition++)
			{
				shortestTheta = Math.abs(thetaBetweenTwoNodes(layerPosition, 0, secondNodePosition));
				shortestThetaPosition = 0;
				for(int firstNodePosition = 0; firstNodePosition < tempLayers.get(layerPosition).getTimeSliceNodes().size(); firstNodePosition++)
				{
					if(shortestTheta > Math.abs(thetaBetweenTwoNodes(layerPosition, firstNodePosition, secondNodePosition)))
					{
						shortestTheta = Math.abs(thetaBetweenTwoNodes(layerPosition, firstNodePosition, secondNodePosition));
						shortestThetaPosition = firstNodePosition;
					}
				}
				/*for(Node n: initialNodeUseChecker)
				{
					if(tempLayers.get(layerPosition + 1).getTimeSliceNodes().get(shortestThetaPosition).id == n.id && 
						tempLayers.get(layerPosition + 1).getTimeSliceNodes().get(shortestThetaPosition).getX() == n.getX() && 
						tempLayers.get(layerPosition + 1).getTimeSliceNodes().get(shortestThetaPosition).getY() == n.getY() &&
						tempLayers.get(layerPosition + 1).getTimeSliceNodes().get(shortestThetaPosition).getZ() == n.getZ())
					{
						hasBeenUsed = true;
					}
				}*/
				if(tempLayers.get(layerPosition).getTimeSliceNodes().size() == tempLayers.get(layerPosition + 1).getTimeSliceNodes().size())
				{
					break;
				}
//				if(hasBeenUsed == false)
//				{
//					System.out.println("False id: " + tempLayers.get(layerPosition + 1).getTimeSliceNodes().get(shortestThetaPosition).id);
//					initialNodeUseChecker.add(tempLayers.get(layerPosition).getTimeSliceNodes().get(shortestThetaPosition));
//				}
//				else
//				{
					//System.out.println("True id: " + tempLayers.get(layerPosition + 1).getTimeSliceNodes().get(shortestThetaPosition).id);
					tempLayers.get(layerPosition).getTimeSliceNodes().add(shortestThetaPosition, tempLayers.get(layerPosition).getTimeSliceNodes().get(shortestThetaPosition));
				//}
			}
		}
		
		// If the layer sizes are equal
		else
		{
			//System.out.println("---- in Else ----");
			// Layer size are the same so no action needed
		}
	}
	
	// Returns the layer that has the most nodes
	public static int layerWithMostNodes()
	{
		int largestLayer = 0;
		int layerWithMostNodes = 0;
		for(int i = 0; i < layers.size(); i++)
		{
			if(layers.get(i).getSize() > largestLayer)
			{
				layerWithMostNodes = i;
				largestLayer = layers.get(i).getSize();
			}
		}
		return layerWithMostNodes;
	}
	
	// calls the sortByTheta method in the layers class
	public static void sortLayersByTheta()
	{
		for(int i = 0; i < layers.size(); i++)
		{
			layers.get(i).sortByTheta();
		}
	}
	
	// Finds the centroid of a layer (timeSlice)
	public static void findCentroid(ArrayList<Node> timeSlice)
	{
		Centroid tempCentroid = new Centroid();
		double tempX = 0;
		double tempY = 0;
		
		// Getting x coordinate
		for(int i = 0; i < timeSlice.size(); i++)
		{
			tempX += timeSlice.get(i).getX();
		}
		tempX = tempX / timeSlice.size();
		
		// Getting y coordinate
		for(int i = 0; i < timeSlice.size(); i++)
		{
			tempY += timeSlice.get(i).getY();
		}
		tempY = tempY / timeSlice.size();
		
		tempCentroid.setX(tempX);
		tempCentroid.setY(tempY);
		
		centroids.add(tempCentroid);
		//System.out.println("centroid x: " + tempCentroid.getX());
		//System.out.println("centroid y: " + tempCentroid.getY());
	}
	
	public static void createCentroidArray()
	{
		//System.out.println("Createing Centroid Array...");
		for(int i = 0; i < layers.size(); i++)
		{
			findCentroid(layers.get(i).getTimeSliceNodes());
			//System.out.println("timeSlice: " + layers.get(i).getTimeSliceNodes().get(0).getX());
		}
		//System.out.println(centroids.size() + " Centroid Array Created");
	}
	
	public static double distFromCentroid(double inX, double inY, Centroid c)
	{
		double dist;
		double xDist;
		double yDist;
		dist = 0;
		
		xDist = Math.abs(inX - c.getX());
		yDist = Math.abs(inY - c.getY());
		dist = Math.sqrt((xDist * xDist) + (yDist * yDist));
		
		return dist;
	}
	
	public static double avgDistFromCentroid(Layers l, Centroid c)
	{
		double avgDistFC = 0;
		ArrayList<Node> tempTimeSlice = l.getTimeSliceNodes();
		for(int i = 0; i < l.getSize(); i++)
		{
			avgDistFC += distFromCentroid(tempTimeSlice.get(i).getX(), tempTimeSlice.get(i).getY(), c);
		}
		return avgDistFC/l.getSize();
	}
	
	public static void setAvgDistFromCentroid()
	{
		//System.out.println("Setting Average Distance from the Centroid...");
		for(int i = 0; i < layers.size(); i++)
		{
			centroids.get(i).setAvgDistFromCentroid(avgDistFromCentroid(layers.get(i), centroids.get(i)));
			//System.out.println(centroids.get(i).getAvgDistFromCentroid());
		}
		//System.out.println("Average Distance from the Centroid Set");
	}
	
	public static void printNodes()
	{
		for(int i = 0; i < getNodesOfCompleteGraph().size(); i++)
		{
			System.out.println("X:" + getNodesOfCompleteGraph().get(i).getX() + " Y: " +  getNodesOfCompleteGraph().get(i).getY() + " Z: " + getNodesOfCompleteGraph().get(i).getZ());
		}
	}
	
	public static void printLayers()
	{
		System.out.println("Number of layers: " + layers.size());
		for(int i = 0; i < layers.size(); i++)
		{
			for(int j = 0; j < layers.get(i).getTimeSliceNodes().size(); j++)
			{
				System.out.println("X: " + layers.get(i).getTimeSliceNodes().get(j).getX() + " Y: " + layers.get(i).getTimeSliceNodes().get(j).getY() + " Z: " + layers.get(i).getTimeSliceNodes().get(j).getZ());
				//System.out.println("Y: " + layers.get(i).getTimeSliceNodes().get(j).getY());
			}
			System.out.println("--------------New Time Slice-------------");
		}
	}
	
	// Finds the largest layer and returns the size of it
	public static int largestNumberOfNodes()
	{
		int max = 0;
		for(int i = 0; i < layers.size(); i++)
		{
			if(max < layers.get(i).getSize())
			{
				max = layers.get(i).getSize();
			}
		}
		return max + 1;
	}
	
	// Creates the _mesh.speck after the information has been finalized. *should be the last method called.
	public void createSpeck(int meshNumber)
	{
		// Sets the path and file name
		String pathName = path + fileName + "_mesh.speck";
		try
		{
			BufferedWriter writer;
			if(meshNumber == 0)
			{
				writer = new BufferedWriter(new FileWriter(pathName));
			}
			else
			{
				writer = new BufferedWriter(new FileWriter(pathName,true));
			}
		    writer.write("eval cmap " + fileName +".cmap\n");
			writer.write("mesh -s wire -c " + meshNumber + " {\n");
			writer.write(largestNumberOfNodes() + " " + layers.size() + "\n");
			for(int i = 0; i < layers.size(); i++)
			{
				System.out.println();
				for(int j = 0; j < layers.get(i).getSize(); j++)
				{
					String x = Double.toString(layers.get(i).getTimeSliceNodes().get(j).getX());
					String y = Double.toString(layers.get(i).getTimeSliceNodes().get(j).getY());
                                        String z = Double.toString(layers.get(i).getTimeSliceNodes().get(j).getZ()-completeGraph.lowestZ());
					//String z = Double.toString(layers.get(i).getTimeSliceNodes().get(j).getZ()-lowestZ());
					writer.write(x + " " + y + " " + z + "\n");
					//System.out.println(x + " " + y + " " + z);
				}

				String x = Double.toString(layers.get(i).getTimeSliceNodes().get(0).getX());
				String y = Double.toString(layers.get(i).getTimeSliceNodes().get(0).getY());
				String z = Double.toString(layers.get(i).getTimeSliceNodes().get(0).getZ()-completeGraph.lowestZ());
				//String z = Double.toString(layers.get(i).getTimeSliceNodes().get(0).getZ()-lowestZ());
				writer.write(x + " " + y + " " + z + "\n");
			}
			writer.write("}\n");
			writer.close();
		}
		catch(IOException e)
		{
			
		}
	}
	
	public void createSpeck(int community, int position, ArrayList<Layers> tempLayers)
	{
		// Sets the path and file name
		String pathName = path + fileName + "_mesh.speck";
		try
		{
			BufferedWriter writer;
			if(community == 0 && position == 0)
			{
				writer = new BufferedWriter(new FileWriter(pathName));
			}
			else
			{
				writer = new BufferedWriter(new FileWriter(pathName,true));
			}
		    writer.write("eval cmap " + fileName + ".cmap\n");
			writer.write("mesh -s wire -c " + community + " {\n");
			
			//System.out.println("layer1 size:" + tempLayers.get(position).getTimeSliceNodes().size() + " layer2 size: " + tempLayers.get(position + 1).getTimeSliceNodes().size());
			
			writer.write((tempLayers.get(position).getSize() + 1) + " " + 2 + "\n");
			for(int i = position; i < position + 2; i++)
			{
				//System.out.println("layer: " + i);
				for(int j = 0; j < layers.get(i).getSize(); j++)
				{
					String x = Double.toString(tempLayers.get(i).getTimeSliceNodes().get(j).getX());
					String y = Double.toString(tempLayers.get(i).getTimeSliceNodes().get(j).getY());
                                        String z = Double.toString(tempLayers.get(i).getTimeSliceNodes().get(j).getZ()-completeGraph.lowestZ());
					//String z = Double.toString(tempLayers.get(i).getTimeSliceNodes().get(j).getZ()-lowestZ());
					writer.write(x + " " + y + " " + z + "\n");
					//System.out.println(x + " " + y + " " + z);
				}

				String x = Double.toString(tempLayers.get(i).getTimeSliceNodes().get(0).getX());
				String y = Double.toString(tempLayers.get(i).getTimeSliceNodes().get(0).getY());
                                String z = Double.toString(tempLayers.get(i).getTimeSliceNodes().get(0).getZ()-completeGraph.lowestZ());
				//String z = Double.toString(tempLayers.get(i).getTimeSliceNodes().get(0).getZ()-lowestZ());
				writer.write(x + " " + y + " " + z + "\n");
			}
			writer.write("}\n");
			writer.close();
		}
		catch(IOException e)
		{
			
		}
	}
	
	public int maxCommunity()
	{
		float maxCommunity = 0;
		for(int i = 0; i < baseNodeList.size(); i++)
		{
			if(baseNodeList.get(i).getModClass() > maxCommunity)
			{
				maxCommunity = baseNodeList.get(i).getModClass();
			}
		}
		return (int)maxCommunity + 1;
	}
	
	public void filterByCommunity(int i)
	{
		for(int j = 0; j < nodesOfCompleteGraph.size(); j++)
		{
			if(nodesOfCompleteGraph.get(j).getModClass() != (float)i)
			{
				nodesOfCompleteGraph.remove(j);
				j--;
			}
		}
	}
	
	public void resetPoints()
	{
		nodesOfCompleteGraph.clear();
		for(int i = 0; i < baseNodeList.size(); i++)
		{
			nodesOfCompleteGraph.add(baseNodeList.get(i));
		}
	}
	
	public void resetVariables()
	{
		layers.clear();
		centroids.clear();
		resetPoints();
	}
	
	// Method used to initiate the creation of the _mesh.speck *This should be run AFTER the path and filename has been set with the constructor.
	public void MeshWriter()
	{
		System.out.println("set Base Node List");
		setBaseNodeList();
		int max = maxCommunity();
		for(int community = 0; community < max; community++)
		{
			System.out.println("Community number " + (community+1) + " of " + max);
			//System.out.println("reset variable");
			resetVariables();
			//System.out.println("filter by community");
			filterByCommunity(community);
			//System.out.println("sort by z");
			sortByZ();
			// printNodes();
			// Number entered into Create Time Slice determines the size of the time slice.
			//System.out.println("create timeslice");
			createTimeSlice(1);
                        
                        //Attempt to create discrete Timeslices to iterate through
                      //  createDiscreteTimeSlice();
                        
			//System.out.println("create centroid array");
			createCentroidArray();
			//System.out.println("set average distance from centroid");
			setAvgDistFromCentroid();
			//System.out.println("sort layers by theta");
			sortLayersByTheta();
			//printLayers();
			
			for(int position = 0; position < layers.size() - 1; position++)
			{
				ArrayList<Layers> tempLayers = new ArrayList<Layers>(layers);
				//System.out.println("fill gaps");
				/*for(Layers L: tempLayers)
				{
					for(int j = 0; j < L.getTimeSliceNodes().size(); j++)
					{
						System.out.println(L.getTimeSliceNodes().get(j).getId());
					}
				}*/
				
				// Checks to see if the next two layers are within (variable range) days of each other [91 days by default]
				if(tempLayers.get(position + 1).getTimeSliceNodes().get(0).getZ() - tempLayers.get(position).getTimeSliceNodes().get(0).getZ() <= range)
				{
					fillGapsInMesh(position, tempLayers);
					createSpeck(community, position, tempLayers);
				}
				else
				{
					// Do nothing because the distance between the nodes are too far
				}
			}
		}
	}
}