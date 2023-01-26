package ma.forix.ssi.network;

import ma.forix.ssi.blocks.blockentities.NetworkableBE;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class StateNetwork {

    private List<NetworkableBE> elements;
    public static int ID_COUNTER = 0;
    public int ID;

    public StateNetwork(NetworkableBE be){
        this.elements = new ArrayList<>();
        this.elements.add(be);
        ID = ID_COUNTER++;
        for (NetworkableBE e : elements){
            System.out.println(e+" at "+ e.getBlockPos());
        }
    }

    public StateNetwork(List<NetworkableBE> bes){
        this.elements = new ArrayList<>();
        this.elements.addAll(bes);
        ID = ID_COUNTER++;
        for (NetworkableBE e : elements){
            System.out.println(e+" at "+ e.getBlockPos());
        }
    }

    public <T extends NetworkableBE> List<T> getType(Class<T> element){
        List<T> toReturn = new ArrayList<>();
        for (NetworkableBE networkable : elements) {
//            System.out.println("element: "+networkable);
            if (element.isInstance(networkable)){
                toReturn.add((T) networkable);
            }
        }
        return toReturn;
    }

    public void AddElement(NetworkableBE element){
//        if (!this.elements.contains(element)) {
            this.elements.add(element);
            for (NetworkableBE e : elements) {
                System.out.println(e + " at " + e.getBlockPos());
            }
//        }
    }

    public List<NetworkableBE> getElements(){
        return elements;
    }

    public void BeingRemoved(NetworkableBE element){
        elements.remove(element);
        if (elements.size() == 0){
            ID_COUNTER--;
        } else {
            List<NetworkableBE> section = extractNearbyElements(elements.get(0));
            while (elements.size() > 0){
                List<NetworkableBE> subSection = extractNearbyElements(elements.get(0));
                StateNetwork subNetwork = new StateNetwork(subSection);
                subSection.forEach((e) -> {
                    e.updateNetwork(subNetwork);
                });
            }
            this.elements.addAll(section);
        }
    }

    private List<NetworkableBE> extractNearbyElements(NetworkableBE element){
        List<NetworkableBE> extracted = new ArrayList<>();
        if (element != null && elements.contains(element)) {
            extracted.add(element);
            elements.remove(element);
            Level level = element.getLevel();
            for (Direction direction : Direction.values()) {
                List<NetworkableBE> subExtracted = extractNearbyElements((NetworkableBE) level.getBlockEntity(element.getBlockPos().relative(direction)));
                extracted.addAll(subExtracted);
            }
        }
        return extracted;
    }

    public void mergeNetworks(StateNetwork network){
        if (network != null) {
            for (NetworkableBE element : network.getElements()) {
                element.updateNetwork(this);
                elements.add(element);
            }
        }
    }
}
