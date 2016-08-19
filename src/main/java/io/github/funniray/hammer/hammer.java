package io.github.funniray.hammer;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class hammer extends JavaPlugin implements Listener {

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this,this);
    }
    private void reduceDurability(Player player,int dmg){
        ItemStack pick = player.getInventory().getItemInMainHand();
        int durability = pick.getDurability()+dmg;
        short max = pick.getType().getMaxDurability();
        if (max >= durability)
            pick.setDurability((short) durability);
        else {
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 10, 1);
        }
    }
    private void breakBlock(Block block, Player player, String type) {
         if(block != null)
             if (block.getType() != Material.BEDROCK)
                 if (!block.isLiquid())
                     if (type == "stone")
                         if (block.getType() == Material.STONE || block.getType() == Material.COBBLESTONE || block.getType().name().contains("ORE") || block.getType() == Material.STAINED_CLAY)
                             if (player.getGameMode().name() == "SURVIVAL") {
                                 block.breakNaturally(player.getInventory().getItemInMainHand());
                                 reduceDurability(player,1);
                             }else if (player.getGameMode().name() == "CREATIVE")
                                 block.setType(Material.AIR);
                     if (type == "wood")
                         if (block.getType().isFlammable() && block.getType() != Material.LEAVES && block.getType() != Material.LEAVES_2)
                             if (player.getGameMode().name() == "SURVIVAL") {
                                 block.breakNaturally(player.getInventory().getItemInMainHand());
                                 reduceDurability(player,1);
                             }else if (player.getGameMode().name() == "CREATIVE")
                                 block.setType(Material.AIR);
                     if (type == "dirt")
                         if (block.getType().hasGravity() || block.getType() == Material.DIRT || block.getType() == Material.GRASS || block.getType() == Material.GRASS_PATH)
                             if (player.getGameMode().name() == "SURVIVAL") {
                                 block.breakNaturally(player.getInventory().getItemInMainHand());
                                 reduceDurability(player,1);
                             }else if (player.getGameMode().name() == "CREATIVE")
                                 block.setType(Material.AIR);

    }
    private void hammer(String facing,Block block,Player player,int x, int y, int z, String type){
        int nx = (int) Math.floor(x/2);
        int ny = (int) Math.floor(y/2);
        for (int i = -nx; i <= nx; i++) {
            for (int v = -ny; v <= ny; v++) {
                for (int m = 0; m <= z-1; m++) {
                    if (facing == "SOUTH")
                        breakBlock(block.getLocation().add(i, v, -m).getBlock(), player,type);
                    if (facing == "NORTH")
                        breakBlock(block.getLocation().add(i, v, m).getBlock(), player,type);
                    if (facing == "WEST")
                        breakBlock(block.getLocation().add(m, i, v).getBlock(), player,type);
                    if (facing == "EAST")
                        breakBlock(block.getLocation().add(-m, i, v).getBlock(), player,type);
                    if (facing == "UP")
                        breakBlock(block.getLocation().add(i, -m, v).getBlock(), player,type);
                    if (facing == "DOWN")
                        breakBlock(block.getLocation().add(i, m, v).getBlock(), player,type);
                }
            }
        }
    }
    public boolean loreContains(List<String> list,String match){
        boolean contain = false;
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (ChatColor.stripColor(list.get(i)).contains(match)) {
                    contain = true;
                }
            }
        }
        return contain;
    }
    @EventHandler
    public void PlayerInteract(PlayerInteractEvent test) {
        if (test.getAction().toString() == "LEFT_CLICK_BLOCK") {
            if (test.getItem() != null) {
                if (test.getItem().hasItemMeta()) {
                    if (loreContains(test.getItem().getItemMeta().getLore()," Mining")) {
                        ItemStack item = test.getItem();
                        ItemMeta itemMeta = item.getItemMeta();
                        List<String> lore = itemMeta.getLore();
                        if (loreContains(lore,"WEST"))
                            lore.remove("WEST");
                        if (loreContains(lore,"SOUTH"))
                            lore.remove("SOUTH");
                        if (loreContains(lore,"EAST"))
                            lore.remove("EAST");
                        if (loreContains(lore,"NORTH"))
                            lore.remove("NORTH");
                        if (loreContains(lore,"UP"))
                            lore.remove("UP");
                        if (loreContains(lore,"DOWN"))
                            lore.remove("DOWN");
                        lore.add(test.getBlockFace().toString());
                        test.getItem().getItemMeta().setLore(lore);
                        itemMeta.setLore(lore);
                        item.setItemMeta(itemMeta);
                        test.getPlayer().getInventory().setItemInMainHand(item);
                    }
                }
            }
        }
    }
    @EventHandler
    private void playerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        String name = player.getDisplayName();
        for (int i = 0; i<this.getDescription().getAuthors().size();i++){
            if (name.equalsIgnoreCase(this.getDescription().getAuthors().get(i))) {
                player.setPlayerListName(String.format("[%sPlugin Dev%s]%s " + name + "%s", ChatColor.GOLD, ChatColor.WHITE, ChatColor.AQUA, ChatColor.WHITE));
                player.setDisplayName(String.format("[%sPlugin Dev%s]%s " + name + "%s", ChatColor.GOLD, ChatColor.WHITE, ChatColor.AQUA, ChatColor.WHITE));
                player.setCustomNameVisible(true);
            }
        }
    }
    @EventHandler
    public void onBreakBlock(BlockBreakEvent event){
        if (event.getPlayer() != null) {
            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
            if (item.hasItemMeta()) {
                if (item.getItemMeta().hasLore()) {
                    List<String> lore = item.getItemMeta().getLore();
                    String facing = lore.get(lore.size()-1);
                    lore.remove(lore.size()-1);
                    ItemMeta itemMeta = item.getItemMeta();
                    itemMeta.setLore(lore);
                    item.setItemMeta(itemMeta);
                    event.getPlayer().getInventory().setItemInMainHand(item);
                    if (lore != null) {
                        //if (lore.contains(" Mining")) {
                        for (int i = 0; i < lore.size(); i++) {
                            if (ChatColor.stripColor(lore.get(i)).contains(" Mining")) {
                                String lores = ChatColor.stripColor(lore.get(i)).replace(" Mining", "");
                                String[] size = lores.split("x");
                                if (size.length == 2) {
                                    if (item.getType().name().contains("PICKAXE"))
                                        hammer(facing,event.getBlock(),event.getPlayer(), Integer.parseInt(size[0]), Integer.parseInt(size[1]), 1, "stone");
                                    if (item.getType().name().contains("_AXE"))
                                        hammer(facing,event.getBlock(),event.getPlayer(), Integer.parseInt(size[0]), Integer.parseInt(size[1]), 1, "wood");
                                    if (item.getType().name().contains("SHOVEL"))
                                        hammer(facing,event.getBlock(),event.getPlayer(), Integer.parseInt(size[0]), Integer.parseInt(size[1]), 1, "dirt");
                                } else {
                                    if (item.getType().name().contains("PICKAXE"))
                                        hammer(facing,event.getBlock(),event.getPlayer(), Integer.parseInt(size[0]), Integer.parseInt(size[1]), Integer.parseInt(size[2]), "stone");
                                    if (item.getType().name().contains("_AXE"))
                                        hammer(facing,event.getBlock(),event.getPlayer(), Integer.parseInt(size[0]), Integer.parseInt(size[1]), Integer.parseInt(size[2]), "wood");
                                    if (item.getType().name().contains("SHOVEL"))
                                        hammer(facing,event.getBlock(),event.getPlayer(), Integer.parseInt(size[0]), Integer.parseInt(size[1]), Integer.parseInt(size[2]), "dirt");
                                }
                                break;
                            }
                        }
                        //}
                    }
                }
            }
        }
    }
}