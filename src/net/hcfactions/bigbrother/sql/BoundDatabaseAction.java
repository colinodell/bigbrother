package net.hcfactions.bigbrother.sql;

public abstract class BoundDatabaseAction implements IDatabaseAction {

    private Object obj;

    public BoundDatabaseAction bind(Object o)
    {
        this.obj = o;
        return this;
    }

    protected Object getBoundObject()
    {
        return obj;
    }
}
