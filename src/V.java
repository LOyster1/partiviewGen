
/*********************************************************************
 *
 *      Project: Network Visualization
 *      Author:  Kevin Bartlett
 *      Date:    2015
 *
 *      This is a simple class to represent a vertex.  It can be
 *      easily extended and can be used to contain 3D point with
 *      accessible coordinates.
 *
 *********************************************************************/

public class V
{
    protected float x;
    protected float y;
    protected float z;

    public V()
    {
        setX(0.0f);
        setY(0.0f);
        setZ(0.0f);
    }
    public V(float x, float y, float z)
    {
        setX(x);
        setY(y);
        setZ(z);
    }

    public float getX()
    {
        return x;
    }
    public float getY()
    {
        return y;
    }
    public float getZ()
    {
        return z;
    }

    public void setX(float x)
    {
        this.x = x;
    }
    public void setY(float y)
    {
        this.y = y;
    }
    public void setZ(float z)
    {
        this.z = z;
    }

    public V clone()
    {
        return new V(x, y, z);
    }
}
