package net.hcfactions.bigbrother.sql;

import net.hcfactions.bigbrother.util.LogUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class QueuedDatabaseRunnable implements Runnable {
    private ConcurrentLinkedQueue<IDatabaseAction> queue = new ConcurrentLinkedQueue<IDatabaseAction>();

    private Connection connection;
    private Logger logger;

    public QueuedDatabaseRunnable(Connection conn, Logger log)
    {
        this.connection = conn;
        this.logger = log;
    }

    protected Logger getLogger()
    {
        return this.logger;
    }

    public void enqueue(IDatabaseAction action)
    {
        queue.add(action);
    }

    @Override
    public void run() {
        // Check queue for things to execute
        while(!queue.isEmpty())
        {
            IDatabaseAction action = queue.peek();
            try
            {
                action.execute(connection);
                if(action instanceof ILogSuccess)
                    logger.info(((ILogSuccess) action).getOnSuccessMessage());
            }
            catch(SQLException e)
            {
                logger.severe("Queue action failed");
                logger.severe(e.getMessage());
                logger.severe(LogUtils.getStackTrace(e));
            }
            finally
            {
                queue.poll();
            }
        }

    }
}
