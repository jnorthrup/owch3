  
package owch;
import java.io.*;
import java.util.*;


public class  RFC822Format implements Format
{
   
    public RFC822Format(){
    };
    public void read(InputStream is,Map m)throws IOException
    {
	String line,key,val;
	int col; 
  
	BufferedReader ins
	    = new  BufferedReader(new InputStreamReader(is));
	do{
	    line=ins.readLine();
	    if(line==null)
		return; 
	    col=line.indexOf(':');
	    if(col<1)
		return; 
	    key=line.substring(0,col).trim();
	    val=line.substring(col+1).trim();
	    m.put(key,val);
	}while(true); 
        
    }

    public void write(OutputStream o,Map m)  throws IOException
    {
	String line,key;
	for(Iterator i=m.keySet().iterator();i.hasNext();) {
	    
	    key=(String)i.next();
	    line=key.toString()+": "+m.get(key).toString();
	    o.write((line+'\n').getBytes());
	    Env.debug(200,"RFC822Format line saved:"+line);
	}
	o.write('\n');
	o.flush();
    }
}
