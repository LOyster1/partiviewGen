
/*********************************************************************
 *
 *      Project: Network Visualization
 *      Author:  Kevin Bartlett
 *      Date:    2015
 *
 *      A simple interface to allow a reference to any arbitrary
 *      writer of any graph file format.
 *
 *********************************************************************/

public interface GraphWriter
{
 public void setFileName(String fileName);
 public void write(Graph graph);
 public String fileFormatDescription();
 public String fileFormatExtension();
}
