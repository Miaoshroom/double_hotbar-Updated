package com.sidezbros.double_hotbar.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.sidezbros.double_hotbar.DHModConfig;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Mixin(Gui.class)
public abstract class InGameHudMixin{

	@Shadow @Final private static Identifier HOTBAR_SPRITE;
	@Shadow private Player getCameraPlayer() { throw new AssertionError(); }
	@Shadow private void extractSlot(GuiGraphicsExtractor graphics, int x, int y, DeltaTracker tickCounter, Player player, ItemStack stack, int seed) { throw new AssertionError(); }
	
	private boolean reverseShifted = false;
	
	@Inject(method = "extractItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 0))
	private void renderHotbarFrame(GuiGraphicsExtractor graphics, DeltaTracker tickCounter, CallbackInfo info) {
		if(DHModConfig.INSTANCE.displayDoubleHotbar && !DHModConfig.INSTANCE.disableMod) {
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SPRITE, 182, 22, 0, 0, graphics.guiWidth() / 2 - 91, graphics.guiHeight() - 22 - DHModConfig.INSTANCE.shift, 182, 22-DHModConfig.INSTANCE.renderCrop);
		}
		
	}
	
	@Inject(method = "extractItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 1))
	private void shiftHotbarSelector(GuiGraphicsExtractor graphics, DeltaTracker tickCounter, CallbackInfo info) {
		if(DHModConfig.INSTANCE.displayDoubleHotbar && DHModConfig.INSTANCE.reverseBars && !DHModConfig.INSTANCE.disableMod) {
			graphics.pose().translate(0, -DHModConfig.INSTANCE.shift);
		}
		
	}
	
	@Inject(method = "extractItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 0))
	private void returnHotbarSelector(GuiGraphicsExtractor graphics, DeltaTracker tickCounter, CallbackInfo info) {
		if(DHModConfig.INSTANCE.displayDoubleHotbar && DHModConfig.INSTANCE.reverseBars && !DHModConfig.INSTANCE.disableMod) {
			graphics.pose().translate(0, DHModConfig.INSTANCE.shift);
		}
	}
	
	@Inject(method = "extractItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;extractSlot(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IILnet/minecraft/client/DeltaTracker;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;I)V", ordinal = 0))
	private void shiftHotbarItems(GuiGraphicsExtractor graphics, DeltaTracker tickCounter, CallbackInfo info) {
		if(DHModConfig.INSTANCE.displayDoubleHotbar && DHModConfig.INSTANCE.reverseBars && !DHModConfig.INSTANCE.disableMod && !reverseShifted) {
			graphics.pose().translate(0, -DHModConfig.INSTANCE.shift);
			reverseShifted = true;
		}
	}
	
	@Inject(method = "extractItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 1))
	private void renderHotbarItems(GuiGraphicsExtractor graphics, DeltaTracker tickCounter, CallbackInfo info) {
		if(DHModConfig.INSTANCE.displayDoubleHotbar && !DHModConfig.INSTANCE.disableMod) {
			if(DHModConfig.INSTANCE.reverseBars) {
				graphics.pose().translate(0, DHModConfig.INSTANCE.shift);
				reverseShifted = false;
			}
			int m = 1;
			for (int n2 = 0; n2 < 9; ++n2) {
		            int o = graphics.guiWidth() / 2 - 90 + n2 * 20 + 2;
		            int p = graphics.guiHeight() - 16 - 3 - (DHModConfig.INSTANCE.reverseBars ? 0 : DHModConfig.INSTANCE.shift);
		            if(getCameraPlayer() != null) {
		            	this.extractSlot(graphics, o, p, tickCounter, getCameraPlayer(), getCameraPlayer().getInventory().getItem(n2+DHModConfig.INSTANCE.inventoryRow*9), m++);
		            }
		            
		    }
		}
	}
	
	@Inject(method = "extractHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;extractPlayerHealth(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"))
	public void shiftStatusBars(GuiGraphicsExtractor graphics, DeltaTracker tickCounter, CallbackInfo info) {
		if(DHModConfig.INSTANCE.displayDoubleHotbar && !DHModConfig.INSTANCE.disableMod && getCameraPlayer() != null && !getCameraPlayer().isSpectator()) {
			graphics.pose().translate(0, -DHModConfig.INSTANCE.shift);
		}
		
	}
	
	@Inject(method = "extractHotbarAndDecorations", at = @At(value = "TAIL"))
	public void returnStatusBars(GuiGraphicsExtractor graphics, DeltaTracker tickCounter, CallbackInfo info) {
		if(DHModConfig.INSTANCE.displayDoubleHotbar && !DHModConfig.INSTANCE.disableMod && getCameraPlayer() != null && !getCameraPlayer().isSpectator()) {
			graphics.pose().translate(0, DHModConfig.INSTANCE.shift);
		}
	}

}
