package net.hcfactions.core.threading;

public interface IAction<Target> {
    /**
     * Execute the action on the given connection
     * @param conn The connection on which the action should execute
     * @throws java.sql.SQLException
     */
    public void execute(Target conn) throws Exception;
}
