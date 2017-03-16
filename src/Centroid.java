// Centroid Class used to keep track of the centroid of each community (modularity class)
public class Centroid
{
	private double x;
	private double y;
	private double avgDistFromCentroid;

	// Getter/Setter for x
	public double getX()
	{
		return x;
	}
	public void setX(double inX)
	{
		x = inX;
	}
	
	// Getter/Setter for y
	public double getY()
	{
		return y;
	}
	public void setY(double inY)
	{
		y = inY;
	}
	
	// Getter/Setter for avgDistFromCentroid
	public double getAvgDistFromCentroid()
	{
		return avgDistFromCentroid;
	}
	public void setAvgDistFromCentroid(double inAvgDistFromCentroid)
	{
		avgDistFromCentroid = inAvgDistFromCentroid;
	}
}