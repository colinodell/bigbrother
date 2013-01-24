package net.hcfactions.bigbrother.sql;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class TransactionalDatabaseAction implements IDatabaseAction {

    public TransactionalDatabaseAction() {}

    protected abstract void _execute(Connection conn) throws SQLException;

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
