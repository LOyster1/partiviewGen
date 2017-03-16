import java.util.ArrayList;
import java.util.Collections;

public class Layers
{
	private ArrayList<Node> timeSlice = new ArrayList<>();
        public String date; 
        public int discreteZ;
	
	public Layers()
	{
		
	}
	/*
	public Layers(float x, float y, float z)
	{
		timeSlice.add(new Node(x,y,z));
	}
	
	public Layers(int min)
	{
		for(int i = 0; i < SpeckModeler.getPoints().size(); i++)
		{
			if((SpeckModeler.getPoints().get(i).getZ() < min + 9999) && (SpeckModeler.getPoints().get(i).getZ() >= min))
			{
				timeSlice.add(SpeckModeler.getPoints().get(i));
			}
		}
	}*/
	
	public void setTimeSliceNodes(ArrayList<Node> layer)
	{
		timeSlice = layer;
	}
	
	public ArrayList<Node> getTimeSliceNodes()
	{
		return timeSlice;
	}
	
	public void addNodeToTimeSlice(Node node)
	{
		timeSlice.add(node);
	}
	
	public void removeNodeFromTimeSlice(int index)
	{
		timeSlice.remove(index);
	}
	
	public void clearTimeSlice()
	{
		timeSlice.clear();
	}
	
	public int getSize()
	{
		return timeSlice.size();
	}
	
	public void sortByTheta()
	{
		for(int i = 0; i < timeSlice.size(); i++)
		{
			for(int j = 0; j < i; j++)
			{
				if(timeSlice.get(i).getX() >= 0)
				{
					if(timeSlice.get(j).getX() >= 0)
					{
						if(getTheta(timeSlice.get(i).getX(),timeSlice.get(i).getY()) < getTheta(timeSlice.get(j).getX(),timeSlice.get(j).getY()))
						{
							Collections.swap(timeSlice, i, j);
						}
					}
					else
					{
						Collections.swap(timeSlice, i, j);
					}
				}
			}
		}
		for(int i = 0; i < timeSlice.size(); i++)
		{
			for(int j = 0; j < i; j++)
			{
				if(timeSlice.get(i).getX() < 0)
				{
					if(timeSlice.get(j).getX() >= 0)
					{
						//Collections.swap(timeSlice, i, j);
					}
					else
					{
						if(getTheta(timeSlice.get(i).getX(),timeSlice.get(i).getY()) < getTheta(timeSlice.get(j).getX(),timeSlice.get(j).getY()))
						{
							Collections.swap(timeSlice, i, j);
						}
					}
				}
			}
		}
	}
	
	public double getTheta(double x, double y)
	{
		return Math.atan(y/x);
	}
        public void setDate(String dateIn)
        {
            date=dateIn;
        }
        public String getDate()
        {
            return date;
        }
        public void setDiscreteZ(int zIn)
        {
            discreteZ=zIn;
        }
        public int getDiscreteZ(){
            return discreteZ;
        }
        
}