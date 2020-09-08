import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MultMap
{
    HashMap<String, ArrayList<String>> hashMap=new HashMap<>();

    public void put(String key,String value){
        if(hashMap.containsKey(key)){
            ArrayList temp=hashMap.get(key);
            temp.add(value);
            hashMap.remove(key);
            hashMap.put(key,temp);
            System.out.println(value);
        }
        else{
            ArrayList temp=new ArrayList<String>();
            temp.add(value);
            hashMap.put(key,temp);
            System.out.println(value);
        }

    }
    public boolean contains(String key){
        return hashMap.containsKey(key);

    }
    public String toString(){
        Collection<ArrayList<String>> col=hashMap.values();
        String string="";
        for(ArrayList<String> list : col){
            for(String val : list){
                string+=val+"\n";
            }
            string+="\n";
        }
        return string;
    }

}
