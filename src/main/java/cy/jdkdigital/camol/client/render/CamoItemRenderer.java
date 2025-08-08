package cy.jdkdigital.camol.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import cy.jdkdigital.camol.Camol;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class CamoItemRenderer extends BlockEntityWithoutLevelRenderer
{
    public CamoItemRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        BlockState state = stack.get(Camol.BLOCK_COMPONENT);
        if (state != null) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.scale(2, 2, 2);
            boolean leftHand = Minecraft.getInstance().player != null && Minecraft.getInstance().player.getUsedItemHand().equals(InteractionHand.OFF_HAND);
            Minecraft.getInstance().getItemRenderer().renderStatic(Minecraft.getInstance().player, state.getBlock().asItem().getDefaultInstance(), ItemDisplayContext.FIXED, leftHand, poseStack, buffer, Minecraft.getInstance().level, combinedLight, combinedOverlay, 0);
            poseStack.popPose();
        }
    }
}
