package net.hcfactions.core.sql.action;

import java.sql.SQLException;
import java.util.logging.Logger;

public interface ISQLDebug<Target> {
    public void debug(Target conn, Logger logger) throws SQLException;
}
