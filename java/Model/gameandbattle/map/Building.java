package Model.gameandbattle.map;

import Model.buildings.BuildingImage;
import Model.buildings.CastleBuilding;
import Model.buildings.OtherBuildingsMethods;
import Model.gameandbattle.Government;
import Model.gameandbattle.battle.Troop;
import Model.gameandbattle.stockpile.Resource;
import Model.gameandbattle.battle.Person;
import View.menus.SelectBuildingMenu;

import java.util.ArrayList;
import java.util.Scanner;

public class Building {
    private final SelectBuildingMenu selectBuildingMenu;
    private Government government;
    private double gold;
    private String name;
    private int hitpoint;
    private Resource resourceRequired;
    private final int amountOfResource;
    private ArrayList<Person> workers;
    private ArrayList<Texture> allowedTextures;
    private int popularityIncreaseRate;
    private final Cell occupiedCell;
    private final int amountOfWorkers;
    private boolean isFiery;

    public Building(Government government, double gold, String name, int hitpoint, Resource resourceRequired,int amountOfResource, int amountOfWorkers, ArrayList<Texture> textures, Cell occupiedCell) {
        this.government = government;
        this.gold = gold;
        this.name = name;
        this.hitpoint = hitpoint;
        this.resourceRequired = resourceRequired;
        workers=new ArrayList<>();
        allowedTextures=textures;
        this.occupiedCell=occupiedCell;
        this.amountOfResource=amountOfResource;
        selectBuildingMenu= new SelectBuildingMenu();
        this.amountOfWorkers = amountOfWorkers;
        isFiery=false;
    }

    public Building getBuildingByName(String name){
        return null;
    }
    public void makeAffect(int x, int y, Map map){
        increasePopularity();
    }
    public void whenBuildingIsSelected(int x, int y, Map map,Scanner scanner){
        selectBuildingMenu.run(scanner,this,x,y,map);
        //population
    }
    ////////setters and getters

    public Government getGovernment() {
        return government;
    }

    public void setGovernment(Government government) {
        this.government = government;
    }

    public double getGold() {
        return gold;
    }

    public void setGold(double gold) {
        this.gold = gold;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHitpoint() {
        return hitpoint;
    }

    public void setHitpoint(int hitpoint) {
        this.hitpoint = hitpoint;
    }

    public Resource getResourcesRequired() {
        return resourceRequired;
    }

    public void setResourcesRequired(Resource resourceRequired) {
        this.resourceRequired = resourceRequired;
    }

    public ArrayList<Person> getWorkers() {
        return workers;
    }

    public void setWorkers(ArrayList<Person> workers) {
        this.workers = workers;
    }

    public Resource getResourceRequired() {
        return resourceRequired;
    }

    public void setResourceRequired(Resource resourceRequired) {
        this.resourceRequired = resourceRequired;
    }

    public ArrayList<Texture> getAllowedTextures() {
        return allowedTextures;
    }

    public void setAllowedTextures(ArrayList<Texture> allowedTextures) {
        this.allowedTextures = allowedTextures;
    }

    public Cell getOccupiedCell() {
        return occupiedCell;
    }


    public void increasePopularity()
    {
        government.setPopularity(government.getPopularity()+popularityIncreaseRate);
    }

    public int getAmountOfResource() {
        return amountOfResource;
    }

    public int getAmountOfWorkers() {
        return amountOfWorkers;
    }

    public void addUnit(Troop troop) {
        troop.setBuilding(this);
        workers.add(troop);
    }

    public boolean isFiery() {
        return isFiery;
    }

    public void setFiery(boolean fiery) {
        isFiery = fiery;
    }
}
