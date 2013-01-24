package net.hcfactions.core.threading;

import net.hcfactions.core.log.EnhancedLogger;

import java.util.logging.Logger;

public abstract class MonitoredBackgroundQueue<Action extends IAction, Target> extends BackgroundQueue<Action, Target> {

    private String queueIdentifier;
    protected MonitoredBackgroundQueue(Target conn, EnhancedLogger log) {
        super(conn, log);
    }
    public MonitoredBackgroundQueue(Target conn, EnhancedLogger log, String identifier) {
        super(conn, log);
        queueIdentifier = identifier;
    }

    protected static final int WARNING = 0;
    protected static final int ERROR = 1;

    protected int warnLevel = 9999;
    private boolean warned = false;
    protected int errorLevel = 9999;

    private boolean allowProcessing = true;

    public void setWarnLevel(int i)
    {
        warnLevel = i;
        if(queue.size() < warnLevel)
        {
            warned = false;
        }
        else
        {
            onMonitorAlarm(WARNING);
            warned = true;
        }
    }

    public void setErrorLevel(int i)
    {
        errorLevel = i;
        if(queue.size() >= errorLevel)
        {
            onMonitorAlarm(ERROR);
        }

    }

    @Override
    public void run()
        {
        int size = queue.size();

        if(!warned && size >= warnLevel)
        {
            onMonitorAlarm(WARNING);
            warned = true;
        }
        else if(warned && size < warnLevel)
        {
            onMonitorAlarmReset(WARNING);
            warned = false;
        }

        if(size >= errorLevel)
        {
            stopProcessing();
            onMonitorAlarm(ERROR);
            runAll();
            resumeProcessing();
            return;
        }



        if(allowProcessing)
            super.run();
    }

    public void stopProcessing()
    {
        this.allowProcessing = false;
    }
    public void resumeProcessing()
    {
        resumeProcessing(true);
    }
    public void resumeProcessing(boolean b)
    {
        this.allowProcessing = b;
    }

    public abstract boolean onMonitorAlarm(int level);
    public abstract void onMonitorAlarmReset(int level);

    public String getQueueIdentifier() {
        return queueIdentifier;
    }
}
