package com.landshop;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * LandShop Mod — Server-side Fabric mod for buying Flan claim blocks with items.
 */
public class LandShopMod implements DedicatedServerModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("land-shop");

    // ========== CONFIGURATION ==========
    private static final String CLAIM_BLOCK_TYPE = "bonus";

    private static class Trade {
        public final Item item;
        public final int requiredAmount;
        public final int rewardBlocks;
        public final String displayName;

        public Trade(Item item, int requiredAmount, int rewardBlocks, String displayName) {
            this.item = item;
            this.requiredAmount = requiredAmount;
            this.rewardBlocks = rewardBlocks;
            this.displayName = displayName;
        }
    }

    private static final Trade TRADE_COAL = new Trade(Items.DEEPSLATE_COAL_ORE, 15, 256, "Deepslate Coal Ore");
    private static final Trade TRADE_DIAMOND = new Trade(Items.DIAMOND_ORE, 1, 256, "Diamond Ore");
    private static final Trade TRADE_EMERALD = new Trade(Items.DEEPSLATE_EMERALD_ORE, 1, 6400, "Deepslate Emerald Ore");
    // ====================================

    @Override
    public void onInitializeServer() {
        LOGGER.info("[LandShop] Initializing...");

        CommandRegistrationCallback.EVENT.register(
            (CommandDispatcher<CommandSourceStack> dispatcher,
             CommandBuildContext registryAccess,
             Commands.CommandSelection environment) -> {

                dispatcher.register(
                    Commands.literal("buycoal")
                        .executes(context -> executeBuy(context, TRADE_COAL))
                );

                dispatcher.register(
                    Commands.literal("buydiamond")
                        .executes(context -> executeBuy(context, TRADE_DIAMOND))
                );

                dispatcher.register(
                    Commands.literal("buyemerald")
                        .executes(context -> executeBuy(context, TRADE_EMERALD))
                );

                LOGGER.info("[LandShop] Commands registered: /buycoal, /buydiamond, /buyemerald");
            }
        );
    }

    private static int executeBuy(CommandContext<CommandSourceStack> context, Trade trade) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String playerName = player.getGameProfile().name();
        MinecraftServer server = context.getSource().getServer();

        int totalCount = countItemInInventory(player, trade.item);
        int possibleTrades = totalCount / trade.requiredAmount;

        if (possibleTrades > 0) {
            int itemsToRemove = possibleTrades * trade.requiredAmount;
            int reward = possibleTrades * trade.rewardBlocks;

            int remaining = removeItemsFromInventory(player, trade.item, itemsToRemove);

            if (remaining > 0) {
                LOGGER.error("[LandShop] Failed to remove all {} from {}'s inventory! {} remaining",
                    trade.displayName, playerName, remaining);
                player.sendSystemMessage(Component.literal("[LandShop] Lỗi trừ đồ rồi má ơi! Báo admin gấp!")
                    .withStyle(ChatFormatting.RED));
                return 0;
            }

            String flanCommand = "flan giveClaimBlocks " + playerName + " " + reward;

            LOGGER.info("[LandShop] Executing: /{} (for player {})", flanCommand, playerName);

            try {
                server.getCommands().performPrefixedCommand(
                    server.createCommandSourceStack().withSuppressedOutput(),
                    flanCommand
                );

                MutableComponent successMsg = Component.literal("[LandShop] ")
                    .withStyle(ChatFormatting.GOLD)
                    .append(Component.literal("Ngon từ thịt ngọt từ xương! Húp được +" + reward + " Bonus Claim Blocks! ")
                        .withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(
                        "(Vừa bay màu " + itemsToRemove + " " + trade.displayName + ")")
                        .withStyle(ChatFormatting.GRAY));
                player.sendSystemMessage(successMsg);
                
                return 1;
            } catch (Exception e) {
                LOGGER.error("[LandShop] Flan command failed for {}! Refunding items. Error: {}",
                    playerName, e.getMessage(), e);
                player.getInventory().add(new ItemStack(trade.item, itemsToRemove));
                player.sendSystemMessage(
                    Component.literal("[LandShop] Úi dồi ôi lỗi hệ thống! Quặng của bạn đã được trả lại. Kêu admin lẹ lên!")
                        .withStyle(ChatFormatting.RED));
                return 0;
            }
        }

        player.sendSystemMessage(Component.literal("[LandShop] Nghèo rớt mồng tơi! Bạn không có đủ " + trade.displayName + " để đổi đâu nha!")
            .withStyle(ChatFormatting.RED));
        return 0;
    }

    private static int countItemInInventory(ServerPlayer player, Item item) {
        int count = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(item)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static int removeItemsFromInventory(ServerPlayer player, Item item, int amount) {
        int remaining = amount;
        for (int i = 0; i < player.getInventory().getContainerSize() && remaining > 0; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(item)) {
                int removeCount = Math.min(remaining, stack.getCount());
                stack.shrink(removeCount);
                remaining -= removeCount;
                if (stack.isEmpty()) {
                    player.getInventory().setItem(i, ItemStack.EMPTY);
                }
            }
        }
        return remaining;
    }
}
