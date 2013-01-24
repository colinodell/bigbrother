package net.hcfactions.core.commands;

public class InvalidArgumentsException extends Exception {
    public InvalidArgumentsException() {}
    public InvalidArgumentsException(String s) {
        super(s);
    }
}
