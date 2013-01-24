package net.hcfactions.core.sql.action;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A database action that should be executed as a transaction
 * This class automatically handles the transaction start and committing, plus auto-rollback if it fails
 */
public abstract class TransactionalDatabaseAction implements IDatabaseAction {

    public TransactionalDatabaseAction() {}

    /**
     * Contains the actions to execute on the given connection
     * Anything in this method body is called immediately after the transaction starts and before it's committed - you should NOT handle this yourself
     * @param conn The connection to use
     * @throws SQLException
     */
    protected abstract void _execute(Connection conn) throws SQLException;

    /**
     * Handles all transaction-related logic and calls the custom _execute method
     * @param conn The connection on which the action should execute
     * @throws SQLException
     */
    public void execute(Connection conn) throws SQLException
    {
        try
        {
            conn.setAutoCommit(false);
            this._execute(conn);
            conn.commit();
        } catch(SQLException ex) {
            conn.rollback();
            throw(ex);
        }
        finally {
            conn.setAutoCommit(true);
        }
    }
}
