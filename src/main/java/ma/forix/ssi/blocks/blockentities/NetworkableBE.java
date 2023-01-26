package ma.forix.ssi.blocks.blockentities;

import ma.forix.ssi.network.StateNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public abstract class NetworkableBE extends BlockEntity {
    protected StateNetwork network;

    public NetworkableBE(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public StateNetwork getNetwork(){
        return network;
    }

    public void updateNetwork(StateNetwork network){
        this.network = network;
    }

    public void tickServer(Level level){
        if (network == null) {
            System.out.println("no network");
            List<NetworkableBE> nearbyNetworks = new ArrayList<>();
            for (final var direction : Direction.values()) {
                BlockEntity be = level.getBlockEntity(this.worldPosition.relative(direction));
                if (be instanceof NetworkableBE el && el.network != null) {
                    nearbyNetworks.add(el);
//                    this.network = el.getNetwork();
                }
            }

            if (nearbyNetworks.size() > 1) {
                if (nearbyNetworks.get(0).getNetwork() != null){
                    for (int i = 1; i < nearbyNetworks.size(); i++) {
                        nearbyNetworks.get(0).getNetwork().mergeNetworks(nearbyNetworks.get(i).getNetwork());
                    }
                }
                this.network = nearbyNetworks.get(0).getNetwork();
            }
            if (nearbyNetworks.size() >= 1){
                this.network = nearbyNetworks.get(0).getNetwork();
                if (this.network != null)
                    this.network.AddElement(this);
            } else {
                this.network = new StateNetwork(this);
            }
        }
    }

    @Override
    public void setRemoved() {
        if (network != null) {
            this.network.BeingRemoved(this);
        }
        super.setRemoved();
    }
}
