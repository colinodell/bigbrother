package net.hcfactions.core.sql;

import net.hcfactions.core.threading.BackgroundQueue;

/**
 * A helper class that sits between application logic and the database logic
 * It binds itself to the given queue and should execute all actions through that queue
 */
public abstract class DbHelper {
    private BackgroundQueue db;

    /**
     * Creates a new helper bound to the given queue
     * @param queue The queue which all actions should run on
     */
    public DbHelper(BackgroundQueue queue)
    {
        db = queue;
    }

    /**
     * Get the current queue this helper is bound to
     * @return
     */
    protected BackgroundQueue getDb()
    {
        return db;
    }
}