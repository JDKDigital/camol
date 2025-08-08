package cy.jdkdigital.camol.network;

import com.mojang.serialization.Codec;
import cy.jdkdigital.camol.Camol;
import cy.jdkdigital.camol.utils.CamoHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClearCamoData(String source) implements CustomPacketPayload
{
    public static final Type<ClearCamoData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Camol.MODID, "camo_clear_data"));

    public static final StreamCodec<ByteBuf, ClearCamoData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(Codec.STRING),
            ClearCamoData::source,
            ClearCamoData::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void clientHandle(final ClearCamoData data, final IPayloadContext context) {
        CamoHelper.CLIENT_CAMO_MAP.clear();
    }

    public static void serverHandle(final ClearCamoData data, final IPayloadContext context) {
    }
}
