import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GexfReader implements GraphReader
{
	private Scanner file;
	private String path;
	GraphV2 graph = new GraphV2();
	int idCounter = 0;
	int nodeCounter = 0;
        public int greatestZ =0;
        
	ArrayList<Integer> dates = new ArrayList<>();

	GexfReader()
	{
		
	}
	
	public GexfReader(String path)
	{
		setPath(path);
	}
	
	public void setPath(String p)
	{
		path = p;
	}

	public String getPath()
	{
		return path;
	}
        public int getGreatestZFromDates()
        {
            for(int i=0; i<=dates.size()-1;i++)
            {
                if(greatestZ < dates.get(i))
                {
                    greatestZ=dates.get(i);
                }
            }
            System.out.println("Greatest Z from Dates equals "+ greatestZ);
            return greatestZ;
        }
        public void setGreatestZ(int greatestZIn){
            greatestZ=greatestZIn;
        }

	public GraphV2 getGraph()
	{
		String token;

		try
		{
			file = new Scanner(new File(getPath()));
			while (file.hasNext())
			{
				token = file.next();

				if (token.equalsIgnoreCase("<edge"))
				{
					graph.addBaseEdge(loadEdge(file));
				}
				else if (token.equalsIgnoreCase("<node"))
				{
					loadNode(file);
				}
				// No indication of directed or not directed in .gexf
				/*else if (token.equalsIgnoreCase("directed"))
				{
					graph.setDirected(file.next().equals("1"));
				}*/
				else
				{
					// keep looping and get next token;
				}
			}
		}
		catch(IOException e)
		{
			System.err.println(e.getMessage());
		}
		System.out.println("beginning sort");
		graph.finalize();
		System.out.println("sort sucessful");
		return graph;
	}
	private void loadNode(Scanner file) throws IOException
	{
		String token = "";
		Node node = new Node();
		ArrayList<Integer> start = new ArrayList<>();
		ArrayList<Integer> end = new ArrayList<>();
		boolean loop = true;

		do
		{
			token = file.next();

			if (token.startsWith("id="))
			{
				node.setId(token.substring(4, findSecondQuote(4, token)));
				nodeCounter++;
			}
			else if (token.startsWith("label="))
			{
				node.setLabelNQ(token.substring(7, findSecondQuote(7, token)));
			}
			else if (token.equalsIgnoreCase("for=\"modularity_class\""))
			{
				token = file.next();
				node.setModClass(Float.parseFloat(token.substring(7, findSecondQuote(7, token))));
			}
			else if (token.equalsIgnoreCase("<viz:size"))
			{
				token = file.next();
				node.setRadius(Float.parseFloat(token.substring(7, findSecondQuote(7, token))));
			}
			else if (token.equalsIgnoreCase("<spell"))
			{
				spellTimes(file, start, end);
			}
			
			// Creates the time intervals from the start and endopen values
                        
                        //Needs to be fixed to set end time to GreatestZ if there is no matching end time for a start time
			else if (token.startsWith("start="))
			{
				start.add(convertDateToInt(7, token));
                                if(token.endsWith(">"))
				{
                                        //end.add(convertDateToInt(7, token));
                                        System.out.println("Greatest Z: "+greatestZ);
					end.add(greatestZ);
                                       // break;
				}
                                else
                                {
                                    token = file.next();
                                }
                                
                                if (token.startsWith("endopen="))
                                {
                                    end.add(convertDateToInt(9, token));
                                }
//                                
			}
//			else if (token.startsWith("endopen="))
//			{
//				end.add(convertDateToInt(9, token));
//			}
			
			// Creates a single time instance at the start value
			//else if (token.startsWith("start="))
			//{
			//	start.add(convertDateToDouble(7, token));
			//	end.add(convertDateToDouble(7, token));
			//}
			
			else if (token.startsWith("x="))
			{
				node.setX(Float.parseFloat(token.substring(3, findSecondQuote(3, token))));
			}
			else if (token.startsWith("y="))
			{
				node.setY(Float.parseFloat(token.substring(3, findSecondQuote(3, token))));
			}
			else if (token.startsWith("z="))
			{
				// This shouldn't be found in the file because the z will come from the time interval
				//node.setZ(Integer.parseInt(token.substring(3, findSecondQuote(3, token))));
			}

			else if (token.equalsIgnoreCase("<viz:color"))
			{
				int r, g, b;
				token = file.next();
				r = Integer.parseInt(token.substring(3, findSecondQuote(3, token)));
				token = file.next();
				g = Integer.parseInt(token.substring(3, findSecondQuote(3, token)));
				token = file.next();
				b = Integer.parseInt(token.substring(3, findSecondQuote(3, token)));

				node.setColor(new GraphColor(String.format("#%02X%02X%02X", r, g, b)));
			}
			else if(token.equalsIgnoreCase("</node>"))
			{
				loop = false;
			}
		}while(loop);
		
		for(int i = 0; i < start.size(); i++ )
		{
			for(int j = start.get(i); j < end.get(i) + 1; j++)
			{
				if(dates.contains(j))
				{
					Node tempNode = new Node();
					tempNode.setColor(node.getColor());
					tempNode.setDegree(node.getDegree());
					tempNode.setId(node.getId());
					tempNode.setLabel(node.getLabel());
					tempNode.setLabelNQ(node.getLabel());
					tempNode.setModClass(node.getModClass());
					tempNode.setRadius(node.getDegree());
					tempNode.setX(node.getX());
					tempNode.setY(node.getY());
					tempNode.setZ(node.getZ());
					tempNode.setZ((float)j);
					graph.addBaseNode(tempNode);
					graph.addNode(tempNode);
				}
			}
		}
	}

	private EdgeV2 loadEdge(Scanner file) throws IOException
	{
		String token;
		EdgeV2 edge = new EdgeV2();

		do
		{
			token = file.next();
			/*if (token.startsWith("id="))
			{
				edge.setId(token.substring(4, findSecondQuote(4,token)));
			}*/
			
			edge.setId(Integer.toString(idCounter));
			idCounter++;
			
			if (token.startsWith("source="))
			{
				edge.setSource(token.substring(8, findSecondQuote(8,token)));
			}
			else if (token.startsWith("target="))
			{
				edge.setTarget(token.substring(8, findSecondQuote(8,token)));
			}
			else if (token.startsWith("weight="))
			{
				edge.setWeight(Float.parseFloat(token.substring(8, findSecondQuote(8,token))));
			}
			else if (token.startsWith("time="))
			{
				edge.setTime(token.substring(6, findSecondQuote(6,token)));
			}
			else if (token.startsWith("duration="))
			{
				edge.setEndTime(token.substring(10, findSecondQuote(10,token)));
			}
		}while (!token.endsWith("</edge>"));

		return edge;
	}
	
	// Finds the second double quote
	private int findSecondQuote(int i, String s)
	{
		int secondQuoteLocation = s.length();
		for(int j = i; j < s.length(); j++)
		{
			if(s.charAt(j)== '"')
			{
				secondQuoteLocation = j;
			}
		}
		return secondQuoteLocation;
	}

	// Adds multiple start and end times from the spell attribute
	public void spellTimes(Scanner file, ArrayList<Integer> start, ArrayList<Integer> end)
	{
		String token;
		do
		{
			token = file.next();
			
			//----- for Time Interval -----//
			if(token.startsWith("start="))
			{
				start.add(convertDateToInt(7, token));
				if(token.endsWith("</spell>"))
				{
					end.add(convertDateToInt(7, token));
				}
			}
			else if(token.startsWith("endopen="))
			{
				end.add(convertDateToInt(9, token));
			}
			
			//----- for Single Instance -----//
			/*if(token.startsWith("start="))
			{
				start.add(convertDateToDouble(7, token));
				end.add(convertDateToDouble(7, token));
			}*/
			
		}while(!token.endsWith("</spell>"));
	}

	public String fileFormatDescription()
	{
		return "GEXF (*.gexf)";
	}

	public String fileFormatExtension()
	{
		return "gexf";
	}
	
	public void relevantDates(String s)
	{
		Pattern p = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
		String token;
		try
		{
			file = new Scanner(new File(s));
			while (file.hasNextLine())
			{
				token = file.nextLine();
				if(p.matcher(token).matches())
				{
					dates.add(convertDateToInt(0, token));
				}
			}
		}
		catch(IOException e)
		{
			System.err.println(e.getMessage());
		}
	}
	
	public int convertDateToInt(int i, String token)
	{
		int date;
		String unparsedDate;
		int year;
		int month;
		int day;
		
		unparsedDate = token.substring(i, findSecondQuote(i, token));
		year = Integer.parseInt(unparsedDate.substring(0,4));
		month = Integer.parseInt(unparsedDate.substring(5, 7));
		day = Integer.parseInt(unparsedDate.substring(8, 10));
		
		// Not adjusted for Leap year.
		year = year * 365;
		switch((int)month){
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
		date = day + month + year;
		
		return date;
	}
}