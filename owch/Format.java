package owch;
import java.io.*;
import java.util.*;

/**
 * @version $Id: Format.java,v 1.2 2001/09/23 10:20:10 grrrrr Exp $
 * @author James Northrup 
 */
public interface Format{

    public void read(InputStream i,Map m)throws java.io.IOException;
    void write(OutputStream o, Map m)throws java.io.IOException;
}
