package owch;
import java.io.*;

public interface Format{

    public void read(InputStream i,MetaProperties m)throws java.io.IOException;
    public void write(OutputStream i,MetaProperties m)throws java.io.IOException;
}
