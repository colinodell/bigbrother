package net.hcfactions.core.threading;

import net.hcfactions.core.log.EnhancedLogger;
import net.hcfactions.core.sql.action.ILogFailure;
import net.hcfactions.core.sql.action.ILogSuccess;
import net.hcfactions.core.sql.action.ISQLDebug;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class BackgroundQueue<Action extends IAction, Target> implements Runnable {

    protected ConcurrentLinkedQueue<Action> queue = new ConcurrentLinkedQueue<Action>();
    private static final int MAX_ITEMS_INFINITE = -1;
    private int maxItems = MAX_ITEMS_INFINITE;

    public void setMaxItems(int i)
    {
        maxItems = i;
    }

    /**
     * Adds a new action to the end of the queue
     * @param action The database action to queue for execution
     */
    public void enqueue(Action action)
    {
        queue.add(action);
    }


    private Target connection;
    private EnhancedLogger logger;

    /**
     * Creates a new queue using the given connection and logger
     * @param conn The database connection on which all queries will be run
     * @param log The logger to use for success and error logging
     */
    public BackgroundQueue(Target conn, EnhancedLogger log)
    {
        this.connection = conn;
        this.logger = log;
    }

    protected Logger getLogger()
    {
        return this.logger;
    }

    private void run(int limit)
    {
        // Check queue for things to execute
        for(int i = 0; !queue.isEmpty() && (limit == MAX_ITEMS_INFINITE || i < limit); i++)
        {
            // Pop the next item off the queue
            Action action = queue.poll();
            try
            {
                // Execute the 'run' code, passing it the active connection
                action.execute(connection);

                // Show a custom success message, if available
                if(action instanceof ILogSuccess)
                    logger.info(((ILogSuccess) action).getOnSuccessMessage());
            }
            catch(Exception e)
            {
                // Show a custom failure method, if available
                if(action instanceof ILogFailure)
                    logger.info(((ILogFailure) action).getOnFailureMessage());
                    // Otherwise show a generic error
                else
                    logger.severe("Queue action failed");

                // Additional error details for debugging purposes
                logger.logException(e);

                if(action instanceof ISQLDebug)
                {
                    try
                    {
                        ((ISQLDebug)action).debug(connection, logger);
                    }
                    catch(Exception ex)
                    {
                        logger.warning("Failed to get additional debug info: " + ex.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Executes all actions currently in the queue. This should be run on a background thread.
     * Any query that fails will NOT be retried.
     */
    @Override
    public void run() {
        this.run(maxItems);
    }

    // Only used when things get really backed up. I wouldn't recommend calling this yourself unless you know what you're doing.
    public void runAll() {
        this.run(MAX_ITEMS_INFINITE);
    }
}
