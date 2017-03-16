import java.util.Comparator;
import java.util.ArrayList;
//Collections class for manipulating data
public class GraphCollections{
  /* removeDuplicates : removes duplicate items from a sorted list
   * preconditions    : list must be sorted  
   * note             : the duplicate item with the highest index is preserved, removing the identical previous items */
  public static <T> void removeDuplicates(ArrayList<T> list, Comparator<T> comp){
   int i,j;
   int max=list.size();
   for(i=0,j=1;j<max;i++,j++){
     if(comp.compare(list.get(i),list.get(j))==0){
       list.remove(j);
       i--;
       j--;
       max--;
     }
   }
  }
   
  /* insertSorted : inserts an item into the correct location of a sorted list
   * preconditions: list must be sorted, comparator must check values in same sort order as list  */
  public static <T> int insertSorted(T object, ArrayList<T> list, Comparator<T> comp){
    if(list.size()==0){
      list.add(object);
      return -1;
    }
    int nearest=insertSorted(object, list, comp, 0, list.size()-1);
    int value=comp.compare(object,list.get(nearest));
    if(value==0)
      return nearest;
    if(value>0)
      nearest++;
    list.add(nearest,object);
    return -1;
  }
  
   private static <T> int insertSorted(T object, ArrayList<T> list, Comparator<T> comp, int left, int right){
     int mid=(left+right)/2;
     if(left>=right)
       return mid;
     int value=comp.compare(object,list.get(mid));
     if(value==0)
       return mid;
     if(value>0)
       return insertSorted(object, list, comp, mid+1, right);
     return insertSorted(object, list, comp, left, mid-1);
   }
}