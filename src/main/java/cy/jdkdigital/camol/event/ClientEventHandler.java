package cy.jdkdigital.camol.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import cy.jdkdigital.camol.Camol;
import cy.jdkdigital.camol.utils.CamoHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = Camol.MODID, value = Dist.CLIENT)
public class ClientEventHandler
{
    public static boolean shouldBeTransparent = false;
    public static boolean isTransparent = false;

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        // Change transparency of camo blocks when switching items in hand
        if (Minecraft.getInstance().level != null && shouldBeTransparent != isTransparent) {
            isTransparent = shouldBeTransparent;
            Set<SectionPos> sections = new HashSet<>();

            CamoHelper.CLIENT_CAMO_MAP.entrySet().stream().filter(entry -> !entry.getValue().isAir()).forEach(entry -> {
                sections.add(SectionPos.of(BlockPos.of((Long.parseLong(entry.getKey())))));
            });

            for (SectionPos section : sections) {
                Minecraft.getInstance().levelRenderer.setSectionDirty(section.x(), section.y(), section.z());
            }
        }
    }

    @SubscribeEvent
    public static void geometryEvent(AddSectionGeometryEvent event) {
        SectionPos section = SectionPos.of(event.getSectionOrigin());

        Map<BlockPos, BlockState> camoBlocks = CamoHelper.CLIENT_CAMO_MAP.entrySet().stream()
                .filter(entry -> !entry.getValue().isAir())
                .filter(p -> SectionPos.of(BlockPos.of(Long.parseLong(p.getKey()))).equals(section))
                .collect(Collectors.toMap(e -> BlockPos.of(Long.parseLong(e.getKey())), Map.Entry::getValue));

        if (!camoBlocks.isEmpty() && Minecraft.getInstance().player != null) {
            event.addRenderer(sectionRenderingContext -> {
                BlockAndTintGetter level = sectionRenderingContext.getRegion();
                var random = RandomSource.create();

                for (Map.Entry<BlockPos, BlockState> entry : camoBlocks.entrySet()) {
                    BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
                    BlockState camoState = entry.getValue();
                    BlockPos pos = entry.getKey();
                    PoseStack poseStack = sectionRenderingContext.getPoseStack();

                    BakedModel model = blockRenderer.getBlockModel(camoState);
                    ModelData modelData = model.getModelData(level, pos, camoState, ModelData.EMPTY);

                    poseStack.pushPose();
                    poseStack.translate(SectionPos.sectionRelative(pos.getX()), SectionPos.sectionRelative(pos.getY()), SectionPos.sectionRelative(pos.getZ()));

                    poseStack.translate(0.5, 0.5, 0.5);
                    poseStack.scale(1.005F, 1.005F, 1.005F);
                    poseStack.translate(-0.5, -0.5, -0.5);

                    boolean shouldRenderTransparentCamo = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).is(Camol.CAMO_ITEM);
                    for (RenderType renderType : model.getRenderTypes(camoState, random, ModelData.EMPTY)) {
                        VertexConsumer buffer = sectionRenderingContext.getOrCreateChunkBuffer(shouldRenderTransparentCamo ? RenderType.translucent() : renderType);
                        blockRenderer.renderBatched(camoState, pos, level, poseStack, shouldRenderTransparentCamo ? new CamoHelper.SemiTransparentVertexConsumer(buffer) : buffer, true, random, modelData, renderType);
                    }
                    poseStack.popPose();
                }
            });
        }
    }

    @SubscribeEvent
    public static void playerLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        CamoHelper.CLIENT_CAMO_MAP.clear();
    }
}
