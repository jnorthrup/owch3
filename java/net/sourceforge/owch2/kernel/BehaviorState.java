package net.sourceforge.owch2.kernel;

public interface BehaviorState {
    final int hot = 0;
    final int cold = 1;
    final int frozen = 2;
    final byte dead = 3;
    final int lifespan = 12;
    final int mortality = lifespan * lifespan;
    final public String[] age =
            {
                "hot",
                "cold",
                "frozen",
                "dead"
            };
}


