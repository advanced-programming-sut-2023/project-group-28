package Model.buildings;

import Model.gameandbattle.*;
import Model.gameandbattle.battle.Weapon;
import Model.gameandbattle.map.Building;
import Model.gameandbattle.map.Cell;
import Model.gameandbattle.map.Map;
import Model.gameandbattle.map.Texture;
import Model.gameandbattle.stockpile.Resource;

import java.util.ArrayList;
import java.util.Scanner;

public class WeaponBuilding extends Building {
    private ArrayList<Resource> consumableResources;
    private int productionRate;
    private ArrayList<Weapon> weapons;

    public WeaponBuilding(Government government, double gold, String name, int hitpoint, Resource resourceRequired,int amountOfResource, int amountOfWorkers, ArrayList<Texture> textures, ArrayList<Cell> occupiedCells, ArrayList<Resource> consumableResources, int productionRate) {
        super(government, gold, name, hitpoint, resourceRequired,amountOfResource, amountOfWorkers,textures,occupiedCells);
        this.consumableResources = consumableResources;
        this.productionRate = productionRate;
        weapons=new ArrayList<>();
    }

    @Override
    public void whenBuildingIsSelected(int x, int y, Map map, Scanner scanner) {
        super.whenBuildingIsSelected(x, y, map,scanner);
    }

    @Override
    public void makeAffect(int x, int y, Map map) {
        super.makeAffect(x, y, map);
    }
    public void makeWeapon(Government government){

    }

    public ArrayList<Resource> getConsumableResources() {
        return consumableResources;
    }

    public void setConsumableResources(ArrayList<Resource> consumableResources) {
        this.consumableResources = consumableResources;
    }

    public int getProductionRate() {
        return productionRate;
    }

    public void setProductionRate(int productionRate) {
        this.productionRate = productionRate;
    }

    public ArrayList<Weapon> getWeapons() {
        return weapons;
    }

    public void setWeapons(ArrayList<Weapon> weapons) {
        this.weapons = weapons;
    }
}
