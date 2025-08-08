package cy.jdkdigital.camol.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.state.BlockState;

public record CamoPosition(String camoType, BlockState state)
{
    public static final Codec<CamoPosition> CODEC = RecordCodecBuilder.create(
            builder -> builder
                    .group(
                            Codec.STRING.fieldOf("camoType").forGetter(CamoPosition::camoType),
                            BlockState.CODEC.fieldOf("state").forGetter(CamoPosition::state)
                    )
                    .apply(builder, CamoPosition::new));

    public static final StreamCodec<ByteBuf, CamoPosition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(Codec.STRING),
            CamoPosition::camoType,
            ByteBufCodecs.fromCodec(BlockState.CODEC),
            CamoPosition::state,
            CamoPosition::new
    );
}
