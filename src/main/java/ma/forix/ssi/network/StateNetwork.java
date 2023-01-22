package ma.forix.ssi.network;

import ma.forix.ssi.blocks.Networkable;
import ma.forix.ssi.blocks.blockentities.RackBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class StateNetwork {

    private List<Networkable> elements;
    public static int ID_COUNTER = 0;
    public int ID;

    public StateNetwork(Networkable be){
        this.elements = new ArrayList<>();
        this.elements.add(be);
        ID = ID_COUNTER++;
        System.out.println("(constructor) Elements in network "+ID+":");
        for (Networkable e : elements){
            System.out.println(e+" at "+ e.getBlockPos());
        }
    }

    public StateNetwork(List<Networkable> bes){
        this.elements = new ArrayList<>();
        this.elements.addAll(bes);
        ID = ID_COUNTER++;
        System.out.println("(constructor) Elements in network "+ID+":");
        for (Networkable e : elements){
            System.out.println(e+" at "+ e.getBlockPos());
        }
    }

    public RackBlockEntity getType(Class<RackBlockEntity> element){
        for (Networkable networkable : elements) {
            if (element.isInstance(networkable)){
                return (RackBlockEntity) networkable;
            }
        }
        return null;
    }

    public void AddElement(Networkable element){
//        if (!this.elements.contains(element)) {
            this.elements.add(element);
            System.out.println("(add) Elements in network " + ID + ":");
            for (Networkable e : elements) {
                System.out.println(e + " at " + e.getBlockPos());
            }
//        }
    }

    public List<Networkable> getElements(){
        return elements;
    }

    public void BeingRemoved(Networkable element){
        elements.remove(element);
        if (elements.size() == 0){
            ID_COUNTER--;
        } else {
            List<Networkable> section = extractNearbyElements(elements.get(0));
            while (elements.size() > 0){
                List<Networkable> subSection = extractNearbyElements(elements.get(0));
                StateNetwork subNetwork = new StateNetwork(subSection);
                subSection.forEach((e) -> {
                    e.updateNetwork(subNetwork);
                });
            }
            this.elements.addAll(section);
        }
    }

    private List<Networkable> extractNearbyElements(Networkable element){
        List<Networkable> extracted = new ArrayList<>();
        if (element != null && elements.contains(element)) {
            extracted.add(element);
            elements.remove(element);
            Level level = element.getLevel();
            for (Direction direction : Direction.values()) {
                List<Networkable> subExtracted = extractNearbyElements((Networkable) level.getBlockEntity(element.getBlockPos().relative(direction)));
                extracted.addAll(subExtracted);
            }
        }
        return extracted;
    }

    public void mergeNetworks(StateNetwork network){
        if (network != null) {
            for (Networkable element : network.getElements()) {
                element.updateNetwork(this);
                elements.add(element);
            }
        }
    }
}
