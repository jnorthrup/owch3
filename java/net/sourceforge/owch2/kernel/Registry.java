package net.sourceforge.owch2.kernel;

import java.lang.ref.*;
import java.util.*;

/**
 * gatekeeper registers a prefix of an Item such as "/cgi-bin/foo.cgi" The algorithm to locate the Item works in 2 phases;<OL>
 * <LI> The weakHashMap is checked for an exact match. <LI> The arraycache is then checked from top to bottom to see if
 * Item startswith (element <n>) </OL> The when an Item is located -- registering the Item "/" is a sure
 * bet, the owch agent registered in the WeakHashMap is notified of a waiting pipeline
 * @version $Id: Registry.java,v 1.1 2002/12/08 16:05:51 grrrrr Exp $
 * @author James Northrup
 */
public abstract class Registry {
    protected Reference refGet(Object key) {
        return (Reference) getWeakMap().get(key);
    };

    protected Object weakGet(Object key) {
        if (key == null) {
            return null;
        }
        Reference r = refGet(key);
        if (r == null) {
            return null;
        }
        Object o = r.get();
        return o;
    };

    abstract public String displayKey(Comparable key);

    abstract public String displayValue(Reference o);

    abstract public Reference referenceValue(Object o);

    /** URLSet is exported frequently after a change. */
    private Object[] cache;
    private ReferenceQueue refQ = new ReferenceQueue();
    private Comparator comparator;

    /** when URLSet next used will rewrite cacheArray */
    private boolean cacheInvalid = true;
    private Map weakMap;

    /** this is used to store the URLS in order of length */
    private SortedSet set;

    public void registerItem(Comparable key, Object val) {
        registerItem(key, referenceValue(val));
    }

    protected void registerItem(Comparable key, Reference val) {
        synchronized (getSet()) {
            getSet().add(key);
            setCacheInvalid(true);
        }
        ;
        getWeakMap().put(key, val);
        if (Env.logDebug)
            Env.log(15, getClass().getName() + ":::Item Registration:" + "@" + displayKey(key) +
                    " -- " + displayValue(val));
    };

    /** unregister the tree item */
    public void unregisterItem(Comparable key) {
        synchronized (getSet()) {
            getSet().remove(key);
            setCacheInvalid(true);
        }
        ;
        if (Env.logDebug) Env.log(15, getClass().getName() + ":::Item DeRegistration:" + displayKey(key));
    };

    /** this renews our cache for specific custom ordered results. */
    protected void reCache() {
        if (Env.logDebug) Env.log(150, getClass().getName() + " recache starting..");
        synchronized (getSet()) {
            setCache(getSet().toArray());
            setCacheInvalid(false);
            if (Env.logDebug) Env.log(150, getClass().getName() + " recache done..");
        }
        ;
        if (Env.logDebug) Env.log(150, getClass().getName() + " - recache fin..");
    };

    public Object[] getCache() {
        return cache;
    }

    public void setCache(Object[] cache) {
        this.cache = cache;
    }

    public Object getCache(int index) {
        return cache[index];
    }

    public void setCache(int i, Object cache) {
        this.cache[i] = cache;
    }

    public ReferenceQueue getRefQ() {
        return refQ;
    }

    public void setRefQ(ReferenceQueue refQ) {
        this.refQ = refQ;
    }

    public Comparator getComparator() {
        return comparator;
    }

    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    public boolean isCacheInvalid() {
        return cacheInvalid;
    }

    public void setCacheInvalid(boolean cacheInvalid) {
        this.cacheInvalid = cacheInvalid;
    }

    public Map getWeakMap() {
        return weakMap;
    }

    public void setWeakMap(Map weakMap) {
        this.weakMap = weakMap;
    }

    public SortedSet getSet() {
        return set;
    }

    public void setSet(SortedSet set) {
        this.set = set;
    }
}

;


