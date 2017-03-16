/*********************************************************************
*
*      Project: Network Visualization
*      Author:  Ben Gurganious
*      Date:    2015
*
*      An Edge class that contains additional fields for time values.
*
**********************************************************************/
import java.util.Comparator;
public class EdgeV2 extends Edge{
  protected float time;
  protected float end_time;
  
  public EdgeV2(){
    super();
    time=0;
    end_time=0;
  }
  
  public EdgeV2(EdgeV2 edge){
    super(edge);
    time=edge.getTime();
    end_time=edge.getEndTime();
  }
 public static final Comparator<EdgeV2> comparator = new Comparator<EdgeV2>()
 {
  public int compare(EdgeV2 e1, EdgeV2 e2)
  {
   return e1.getId().compareTo(e2.getId());
  }
 };
  public float getTime(){
    return time;
  }
  
  public float getEndTime(){
    return end_time;
  }
  
  public void setTime(String _time_string){
    int index = _time_string.indexOf("\"");
    if(index!=-1){
      _time_string=_time_string.substring(index+1,_time_string.lastIndexOf("\""));
    }try{
    time=Float.parseFloat(_time_string);
    end_time=time+1;
    }
    catch(Exception e){}
  }
  
  //method accepts a duration and adds to current time value to set the end time
  public void setEndTime(String _duration_string){
    int index = _duration_string.indexOf("\"");
    if(index!=-1){
      _duration_string=_duration_string.substring(index+1,_duration_string.lastIndexOf("\""));
    }
    end_time = time + Float.parseFloat(_duration_string);
  }

  public boolean isInTimeSlot(float time_slot){
    if(time<=time_slot && end_time>=time_slot)
      return true;
    return false;
  }
  
  public EdgeV2 clone(){
    return new EdgeV2(this);
  }
}