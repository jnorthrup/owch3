package net.sourceforge.owch2.protocol.router;

import net.sourceforge.owch2.kernel.EventDescriptor;
import net.sourceforge.owch2.protocol.Receipt;
import net.sourceforge.owch2.protocol.Transport;

import java.util.concurrent.Future;

public class httpRouter extends AbstractRouterImpl {
    public httpRouter(Transport transport) {
        super(transport);
    }

    public Future<Receipt> route(EventDescriptor... async) throws Exception {
        return null;  //Todo: verify for a purpose
    }
}
