package net.hcfactions.core.sql.action;

/**
 * Represents an action that should display a custom message on failure, instead of a generic error
 */
public interface ILogFailure {
    public String getOnFailureMessage();
}
