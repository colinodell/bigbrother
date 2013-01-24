package net.hcfactions.bigbrother.blocklogging;

import net.hcfactions.bigbrother.blocklogging.model.*;
import net.hcfactions.core.sql.DbHelper;
import net.hcfactions.core.sql.action.IDatabaseAction;
import net.hcfactions.core.threading.BackgroundQueue;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles logic for creating block-related tables and queueing queries
 * This class sits between the event listener and the tables/queries/queue
 */
public class BlockDbHelper extends DbHelper {

    /**
     * Creates a new instance, bound to the given queue, and creates db tables
     * @param q The queue to bind to
     */
    public BlockDbHelper(BackgroundQueue q)
    {
        super(q);
        getDb().enqueue(new IDatabaseAction() {
            @Override
            public void execute(Connection conn) throws SQLException {
                Statement stmt = conn.createStatement();
                stmt.execute(TableChest.CREATE_TABLE);
                stmt.execute(TableSign.CREATE_TABLE);
                stmt.execute(TableChestAccess.CREATE_TABLE);
                stmt.execute(TableOres.CREATE_TABLE);
            }
        });
    }

    public void savePlacedBlock(BlockChange record)
    {
        if(record instanceof ChestBlockChange)
            savePlacedChestBlock((ChestBlockChange) record);
        else if(record instanceof SignBlockChange)
            savePlacedSignBlock((SignBlockChange) record);
    }

    private void savePlacedChestBlock(ChestBlockChange record)
    {
        getDb().enqueue(new TableChest.LogChestPlacedAction(record));
    }

    private void savePlacedSignBlock(SignBlockChange record)
    {
        getDb().enqueue(new TableSign.LogSignPlacedAction(record));
    }

    public void saveDestroyedBlock(BlockChange record)
    {
        if(record instanceof ChestBlockChange)
            saveDestroyedChestBlock((ChestBlockChange) record);
        else if(record instanceof SignBlockChange)
            saveDestroyedSignBlock((SignBlockChange) record);
    }

    private void saveDestroyedChestBlock(ChestBlockChange record)
    {
        getDb().enqueue(new TableChest.LogChestDestroyedAction(record));
    }

    private void saveDestroyedSignBlock(SignBlockChange record)
    {
        getDb().enqueue(new TableSign.LogSignDestroyedAction(record));
    }

    public void saveChestAccess(ChestInventoryOpen record)
    {
        getDb().enqueue(new TableChestAccess.LogChestAccessedAction(record));
    }

    public void updateOreCount(PlayerItemStack record) {
        getDb().enqueue(new TableOres.LogOres(record));
    }
}
