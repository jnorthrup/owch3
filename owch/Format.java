package owch;
import java.io.*;
import java.util.*;

public interface Format{

    public void read(InputStream i,Map m)throws java.io.IOException;
    void write(OutputStream o, Map m)throws java.io.IOException;
}
