package net.sourceforge.owch2.kernel;

import net.sourceforge.idyuts.IOLayer.*;

public interface MetaPropertiesSource extends Source {
    void attach(MetaPropertiesFilter filter);

    void detach(MetaPropertiesFilter filter);
}

;


