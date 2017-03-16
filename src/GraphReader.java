
/*********************************************************************
 *
 *      Project: Network Visualization
 *      Author:  Kevin Bartlett
 *      Date:    2015
 *
 *      A simple interface to allow a reference to any arbitrary
 *      reader of any graph file format.
 *
 *********************************************************************/


public interface GraphReader
{
 public void setPath(String path);
 public Graph getGraph();
 public String fileFormatDescription();
 public String fileFormatExtension();
}
