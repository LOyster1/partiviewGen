import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

// Contains the main method
public class TerminalReader 
{
        public static ArrayList<GraphV2> individualGraphs;
        public static int overallGreatestZ;
	// Method used to create speck files from a .gexf and date.txt
	public static void terminal(String arg, String dates)
	{
		GexfReader graphReader;
		GraphV2 graph;
		GraphCreator creator = new GraphCreator();

		// Parse out the extension so that the output files can be named the same as the input file
		String[] tokens = arg.split("\\.(?=[^\\.]+$)");
		String output=tokens[0];
		
		graphReader = new GexfReader();
		graphReader.relevantDates(dates);
		graphReader.setPath(arg);
		graph = graphReader.getGraph();
		creator.setGraph(graph);
		
		
		PartiviewWriter writer = new PartiviewWriter("");
		writer.setFileName(output);
		writer.write(graph);
		System.out.println("export successful");
	}
	
	// Method used to create speck files from a .gexf, date.txt, and interval(int)
	public static void terminal(String path, String dates, String range)
	{
		GexfReader graphReader;
		GraphV2 graph;
		GraphCreator creator = new GraphCreator();
		
		// Parse out the extension so that the output files can be named the same as the input file
		String[] tokens = path.split("\\.(?=[^\\.]+$)");
		String output=tokens[0];
		
		graphReader = new GexfReader(path);
		graphReader.relevantDates(dates);
		graph = graphReader.getGraph();
		System.out.println("graph: " + graph.getNodeList().get(0).getRadius());
		creator.setGraph(graph);
		
		int rangeToInt = Integer.valueOf(range);
		
		PartiviewWriter writer = new PartiviewWriter("", rangeToInt);
		writer.setFileName(output);
		writer.write(graph);
		System.out.println("export successful");
	}
	
//	public static void terminal(String path, String dates, String folder, String range) throws IOException
//	{
//		GexfReader graphReader;
//		GraphV2 graph;
//		GraphCreator creator = new GraphCreator();
//		
//		// Parse out the extension so that the output files can be named the same as the input file
//		String[] tokens = path.split("\\.(?=[^\\.]+$)");
//		String output=tokens[0];
//		
//		graphReader = new GexfReader(path);
//		graphReader.relevantDates(dates);
//		graph = graphReader.getGraph();
//		System.out.println("graph: " + graph.getNodeList().get(0).getRadius());
//		creator.setGraph(graph);
//		
//		int rangeToInt = Integer.valueOf(range);
//		
//		PartiviewWriter writer = new PartiviewWriter("", rangeToInt);
//		writer.setFileName(output);
//		writer.write(graph);
//		System.out.println("export successful");
//		
//		/*
//		for(int i = 0; i < Files.list(Paths.get("./"+folder)).count(); i++)
//		{	
//			
//			graphReader = new GexfReader(path);
//			graphReader.relevantDates(dates);
//			graph = graphReader.getGraph();
//			System.out.println("graph: " + graph.getNodeList().get(0).getRadius());
//			creator.setGraph(graph);
//			
//			writer.setFileName(output);
//			writer.write(graph);
//			System.out.println("export successful");
//		}*/
//		
//                //importDirectoryOfGEXFs(folder);
//                
//		//try(Stream<Path> paths = Files.walk(Paths.get("./"+folder))) 
//                try(Stream<Path> paths = Files.walk(Paths.get(folder))) 
//                {
//		    paths.forEach(filePath -> {
//		        if (Files.isRegularFile(filePath)) {
//		            System.out.println(filePath);
//					GexfReader graphRead = new GexfReader(filePath.toString());
//					graphRead.relevantDates(dates);
//					GraphV2 graphtemp = graphRead.getGraph();
//					System.out.println("graph: " + graph.getNodeList().get(0).getRadius());
//					creator.setGraph(graphtemp);
//					individualGraphs.add(graphtemp);
//					//writer.setFileName(output);
//					//writer.write(graphtemp);
//					System.out.println("export successful");
//		        }
//		    });
//		} 
//		
//	}
        public static void terminal(String path, String dates, String folder, String range) throws IOException
	{
		GexfReader graphReader;
		GraphV2 graph;
		GraphCreator creator = new GraphCreator();
                individualGraphs=new ArrayList<GraphV2>();
		
		// Parse out the extension so that the output files can be named the same as the input file
		String[] tokens = path.split("\\.(?=[^\\.]+$)");
		String output=tokens[0];
		
		graphReader = new GexfReader(path);
		graphReader.relevantDates(dates);
                
                graphReader.getGreatestZFromDates();
                overallGreatestZ= graphReader.getGreatestZFromDates();
                
		graph = graphReader.getGraph();
		System.out.println("graph: " + graph.getNodeList().get(0).getRadius());
		creator.setGraph(graph);
		
		int rangeToInt = Integer.valueOf(range);
		
		PartiviewWriter writer = new PartiviewWriter("", rangeToInt);
		writer.setFileName(output);
		writer.write(graph);
		System.out.println("export successful");
		
		/*
		for(int i = 0; i < Files.list(Paths.get("./"+folder)).count(); i++)
		{	
			
			graphReader = new GexfReader(path);
			graphReader.relevantDates(dates);
			graph = graphReader.getGraph();
			System.out.println("graph: " + graph.getNodeList().get(0).getRadius());
			creator.setGraph(graph);
			
			writer.setFileName(output);
			writer.write(graph);
			System.out.println("export successful");
		}*/
		
		try(Stream<Path> paths = Files.walk(Paths.get("./"+folder))) 
                {
		    paths.forEach(filePath -> 
                    {
		        if (Files.isRegularFile(filePath)) {
		            System.out.println(filePath);
                            GexfReader graphRead = new GexfReader(filePath.toString());
                            graphRead.relevantDates(dates);
                            graphRead.setGreatestZ(overallGreatestZ);
                            GraphV2 graphtemp = graphRead.getGraph();
                            System.out.println("graph: " + graph.getNodeList().get(0).getRadius());
                            graphtemp.setGraphName(filePath.toString());
                            System.out.println("Graph Name: " + graphtemp.getGraphName());
                            String myRegex="\\d{4}-\\d{2}-\\d{2}";
                            Pattern p=Pattern.compile(myRegex);
                            Matcher m=p.matcher(graphtemp.getGraphName());
                            String fileDate;    
                            if (m.find()) 
                            {
                                //System.out.println("M find 0: "+ m.find(0));
                                fileDate=m.group(0);   
                                System.out.println("Graph Date "+fileDate);
                                graphtemp.setDateFromFileName(fileDate);
                            }
                           // graphtemp.offsetFileDate(graph.lowestZ());
                            


                            creator.setGraph(graphtemp);
                            //Attempt to add to list of graphs
                            individualGraphs.add(graphtemp);
                            //writer.setFileName(output);
                            //writer.write(graphtemp);
                            System.out.println("export successful");
		        }
		    });
		}
                writer.getMeshWriter().setIndividualGraphs(individualGraphs);
                writer.getMeshWriter().updateModClassFromIndiv();
               // writer.getMeshWriter().printNodeLabelAndMods();
                System.out.println("Size of Discrete Layers: "+ writer.getMeshWriter().getDiscreteLayers().size());
                
                for(int i=0; i<=writer.getMeshWriter().getDiscreteLayers().size()-1;i++)
                {
                    System.out.println("Discrete Layer Z: " +writer.getMeshWriter().getDiscreteLayers().get(i).getDiscreteZ() + " num of nodes in layer " + writer.getMeshWriter().getDiscreteLayers().get(i).getTimeSliceNodes().size());
                }
                
                
                

// System.out.println("Individual Graphs size: "+individualGraphs.size());
//		for(int i=0; i<individualGraphs.size();i++){
//                    System.out.println("File Name "+individualGraphs.get(i).getGraphName());
//                    System.out.println("File Date "+individualGraphs.get(i).getIndividualFileDate());
//                     
//                }
//                System.out.println("Lowest Z = " +graph.lowestZ());
	}
        
	
	public static void main(String[] args) throws IOException{
		// Args are for [.gexf, DateFile.txt]
		if(args.length == 2)
		{
			terminal(args[0], args[1]);
		}
		// Args are for [.gexf, DateFile.txt, IntervalSize(int)]
		else if(args.length == 3)
		{
			terminal(args[0], args[1], args[2]);
		}
		// Args are for [.gexf, DateFile.txt, individualGEXFFolder(Path), IntervalSize(int)]
		else if(args.length == 4)
		{
			terminal(args[0], args[1], args[2], args[3]);
		}
	}
}
