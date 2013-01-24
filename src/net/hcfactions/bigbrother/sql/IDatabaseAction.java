package net.hcfactions.bigbrother.sql;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDatabaseAction {
    public void execute(Connection conn) throws SQLException;
}
