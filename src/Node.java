
/*********************************************************************
 *
 *      Project: Network Visualization
 *      Author:  Kevin Bartlett
 *      Date:    2015
 *
 *      This is a helper class for the Graph class.
 *
 *********************************************************************/

import java.util.Comparator;

public class Node extends V
{
	protected float radius;
	protected String id;
	protected String label;
	protected GraphColor color;
	protected int degree;
	protected float modClass; // Modularity Class (community)

	public static final Comparator<Node> comparator = new Comparator<Node>()
	{
		public int compare(Node n1, Node n2)
		{
			return n1.getId().compareTo(n2.getId());
		}
	};

	public Node()
	{
		id = "";
		radius = 0.0f;
		x = 0.0f;
		y = 0.0f;
		z = 0.0f;
		label = "untitled";
		degree = 1;
	}
	public Node(Node node)
	{
		copy(node);
		degree = 1;
	}
	public Node(float x, float y, float z)
	{
		super.setX(x);
		super.setY(y);
		super.setZ(z);
	}

	public float getRadius()
	{
		return radius;
	}

	public String getId()
	{
		return id;
	}

	public String getLabel()
	{
		return label;
	}

	public GraphColor getColor()
	{
		return color;
	}

	public void copy(Node node)
	{
		id = node.id;
		radius = node.radius;
		color = node.color;
		x = node.x;
		y = node.y;
		z = node.z;
		label = node.label;
	}

	public void setDegree(int d){
		degree=d;
	}

	public void setRadius(float radius)
	{
		this.radius = radius;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	// Removes outer quotes if they exist.
	public void setLabel(String label)
	{
		int indexFirstQuote = label.indexOf("\"");
		if (indexFirstQuote > 0)
		{
			this.label = label.substring(indexFirstQuote + 1,
					label.lastIndexOf("\""));
		}
	}
	
	// Set the label without quotes
	public void setLabelNQ(String label)
	{
		this.label = label;
	}

	public void setColor(GraphColor color)
	{
		this.color = color;
	}

	public int getDegree(){return degree;}

	public Node incrementDegree(){
		degree++;
		return this;
	}

	public void makeRadiusDependentOnDegree(float m){
		radius = degree * m;
	}

	public Node clone(){
		return new Node(this);
	}

	public float getModClass()
	{
		return modClass;
	}
	public void setModClass(float i)
	{
		modClass = i;
	}
}