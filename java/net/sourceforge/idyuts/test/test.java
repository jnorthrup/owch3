package net.sourceforge.idyuts.test;

import net.sourceforge.idyuts.IOConversion.*;
import net.sourceforge.idyuts.IOLayer.*;
import net.sourceforge.idyuts.IOPipes.*;
import net.sourceforge.idyuts.IOUtil.*;

class test {
    public static void main(String[] args) {
        intSource isrc; //test RangeCounter
        intFilter iflt; //test IntPrinter
        //should print 5 ints to screen
        isrc = (intSource) new RangeCounter(25, 30);
        iflt = (intFilter) new IntPrinter();
        //tests manual attach
        //isrc.attach(iflt);
        Auto.attach(isrc, iflt);
        isrc.xmit();
        isrc = (intSource) new RangeCounter(0, 5);
        //we just make a new one...
        intFilter icnv = new IntStringConverter();
        //test IntStringConvertor,
        StringFilter sflt = new StringAssembler(4);
        //StringAssembler,
        //this object has two input channels, first int sets
        //index, then String sets data.
        ArrayFilter acnv = new ArrayCollectionConverter();
        //ArrayCollectionConverter,
        CollectionFilter cflt = new CollectionPrinter();
        //CollectionFilter  CollectionPrinter
        //test out our Auto attach
        //isrc.attach((IntFilter)sflt);
        Auto.attach(isrc, sflt);
        //must first talk to *Assembler.index
        Auto.attach(isrc, icnv);
        //then we can add Strings via the StringFilter Channel
        Auto.attach(icnv, sflt);
        Auto.attach(sflt, acnv);
        Auto.attach(acnv, cflt);
        isrc.xmit();
    }
}


