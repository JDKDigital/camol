package cy.jdkdigital.camol.network;

import com.mojang.serialization.Codec;
import cy.jdkdigital.camol.Camol;
import cy.jdkdigital.camol.utils.CamoHelper;
import cy.jdkdigital.camol.utils.CamoPosition;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Map;

public record SyncChunkCamoData(Map<String, CamoPosition> data, BlockPos chunkPos) implements CustomPacketPayload
{
    public static final Type<SyncChunkCamoData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Camol.MODID, "sync_chunk_camo_data"));

    public static final StreamCodec<ByteBuf, SyncChunkCamoData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(Codec.unboundedMap(Codec.STRING, CamoPosition.CODEC)),
            SyncChunkCamoData::data,
            ByteBufCodecs.fromCodec(BlockPos.CODEC),
            SyncChunkCamoData::chunkPos,
            SyncChunkCamoData::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void clientHandle(final SyncChunkCamoData data, final IPayloadContext context) {
        // Add camos to global client map
        CamoHelper.CLIENT_CAMO_MAP.putAll(data.data());

        // Update changed position
        var state = context.player().level().getBlockState(data.chunkPos());
        context.player().level().setBlocksDirty(data.chunkPos(), state.isAir() ? Blocks.DIRT.defaultBlockState() : Blocks.AIR.defaultBlockState(), state);
    }

    public static void serverHandle(final SyncChunkCamoData data, final IPayloadContext context) {
    }
}
