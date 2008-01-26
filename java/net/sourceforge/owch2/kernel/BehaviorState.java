package net.sourceforge.owch2.kernel;

public enum BehaviorState {
    hot,
    cold,
    frozen,
    dead;


    static final int lifespan = 12,
            mortality = lifespan ^ 2;

}


