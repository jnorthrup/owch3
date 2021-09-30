package net.sourceforge.owch2.kernel;

import java.util.Map;

public interface Notification extends Map<CharSequence, Object>,
        HasOrigin,
        HasProperties {

}
