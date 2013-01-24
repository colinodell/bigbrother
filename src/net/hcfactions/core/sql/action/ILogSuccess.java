package net.hcfactions.core.sql.action;

/**
 * Represents an action that should display a custom message on success, instead of being silent
 */
public interface ILogSuccess {
    public String getOnSuccessMessage();
}
