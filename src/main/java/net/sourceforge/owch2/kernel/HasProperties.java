package net.sourceforge.owch2.kernel;

import java.util.*;

public interface HasProperties extends Iterable<Map.Entry<CharSequence, Object>> {
    CharSequence DESTINATION_KEY = "To";
}
