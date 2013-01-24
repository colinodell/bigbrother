package net.hcfactions.bigbrother.blocklogging.model;

import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerItemStack extends BaseModel {

    public String player;
    public int qty;
    public Material type;

    public PlayerItemStack(InventoryClickEvent event)
    {
        ItemStack smelted = event.getCurrentItem();
        qty = smelted.getAmount();

        if(!event.isShiftClick())
        {
            // Right-clicking with air causes only half to be removed (rounded up)
            if(event.isRightClick() && event.getCursor().getType() == Material.AIR)
            {
                qty = (int)(qty / 2) + (qty % 2);
            }
        }

        player = event.getWhoClicked().getName();
        type = smelted.getType();
    }

    public PlayerItemStack(BlockBreakEvent e)
    {
        player = e.getPlayer().getName();
        type = e.getBlock().getType();
        qty = 1;
    }

    public PlayerItemStack(BlockChange bi, ItemStack stack) {
        player = bi.destroyedBy;
        type = stack.getType();
        qty = stack.getAmount();
    }
}
