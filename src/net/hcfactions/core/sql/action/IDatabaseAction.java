package net.hcfactions.core.sql.action;

import net.hcfactions.core.threading.IAction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Represents an action that can be performed on a database
 */
public interface IDatabaseAction extends IAction<Connection> {
    /**
     * Execute the action on the given connection
     * @param conn The connection on which the action should execute
     * @throws SQLException
     */
    public void execute(Connection conn) throws SQLException;
}
