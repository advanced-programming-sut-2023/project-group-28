package Controller;

import Model.UserNetwork;
import Model.buildings.CastleBuilding;
import Model.buildings.OtherBuilding;
import Model.buildings.WeaponBuilding;
import Model.gameandbattle.Government;
import Model.gameandbattle.battle.Person;
import Model.gameandbattle.battle.Troop;
import Model.gameandbattle.battle.Weapon;
import Model.gameandbattle.map.*;
import Model.gameandbattle.shop.Request;
import Model.gameandbattle.stockpile.Resource;
import javafx.scene.layout.HBox;
import org.example.User;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBank {
    public static ArrayList<User> onlineUsers = new ArrayList<>();
    public static ArrayList<HBox> hBoxes=new ArrayList<>();
    public static ArrayList<UserNetwork> userNetworks=new ArrayList<>();
    public static ArrayList captcha  = new ArrayList<>(List.of(1181,1381,1491,1722,1959,2163,2177,2723,2785,3541,3847,3855,3876,3967,4185,4310,
            4487,4578,4602,4681,4924,5326,5463,5771,5849,6426,6553,6601,6733,6960,7415,7609
            ,7755,7905,8003,8070,8368,8455,8506,9061,9386));
    private static Stage stage;
    private static final ArrayList<Person> allUnits = new ArrayList<>();
    private static final HashMap<String, Building> buildingName = new HashMap<>(); // name to building
    private static final ArrayList<Wall> walls = new ArrayList<>();
    private static ArrayList<User> allUsers = new ArrayList<>();
    private static User currentUser;
    public static ArrayList<Map> allMaps = new ArrayList<>();
    private static ArrayList<Request> requests = new ArrayList<>();
    private static ArrayList<Government> governments;
    public static User userUnderConstruction;
    public static ArrayList<String> colors = new ArrayList<>(List.of("Blue", "Green", "Red", "Purple", "Orange", "Cyan", "Yellow", "Black"));
    private static final ArrayList<Texture> castleBuildingTextures = new ArrayList<>(List.of(Texture.GROUND, Texture.GRAVEL_GROUND
            , Texture.GRASS, Texture.HIGH_DENSITY_GRASSLAND, Texture.LOW_DENSITY_GRASSLAND, Texture.PLAIN));
    private static final ArrayList<Texture> foodAndIndustryTextures = new ArrayList<>(List.of(Texture.GROUND, Texture.GRAVEL_GROUND
            , Texture.GRASS, Texture.HIGH_DENSITY_GRASSLAND, Texture.LOW_DENSITY_GRASSLAND, Texture.PLAIN));
    private static final ArrayList<Texture> gardensAllowedTextures = new ArrayList<>(List.of(Texture.GRASS
            , Texture.HIGH_DENSITY_GRASSLAND, Texture.LOW_DENSITY_GRASSLAND, Texture.LOW_DENSITY_GRASSLAND, Texture.PLAIN));
    ///////////////////////////////////// getters and setters

    public static ArrayList<User> getAllUsers() {
        return allUsers;
    }

    public static void setAllUsers(ArrayList<User> allUsers) {
        DataBank.allUsers = allUsers;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        DataBank.currentUser = currentUser;
    }

    ///////////////////////////////////////////////////////
    public static User getUserByUsername(String username) {
        System.out.println(allUsers.size());
        for (User allUser : allUsers) {
            if (allUser.getUsername().equals(username)) return allUser;
        }
        return null;
    }

    public static Cell[][] getMapNumberOne() {
        Cell[][] mapCells = new Cell[200][200];
        //you can modify these variables according to your interest
        int stoneLength = 20;
        int stoneWidth = 10;
        int ironLength = 20;
        int ironWidth = 10;
        int grassLength = 20;
        int grassWidth = 100;
        int rockLength = 15;
        int rockWidth = 5;
        int waterLength = 25;
        int waterWidth = 10;
        for (int i = 0; i < 200; i++) {
            for (int j = 0; j < 200; j++) {
                mapCells[i][j] = new Cell(Texture.GROUND, null, null);
            }
        }
        // up of the map
        putTexture(mapCells, 0, stoneWidth, 0, stoneLength, Texture.ROCKY_GROUND);
        putTexture(mapCells, 0, stoneWidth, 200 - stoneLength, 200, Texture.ROCKY_GROUND);
        putTexture(mapCells, stoneWidth, stoneWidth + ironWidth, 0, ironLength, Texture.METAL);
        putTexture(mapCells, stoneWidth, stoneWidth + ironWidth, 200 - ironLength, 200, Texture.METAL);
        putTexture(mapCells, 49, 49 + grassWidth, 0, grassLength, Texture.GRASS);
        putTexture(mapCells, 49, 49 + grassWidth, 200 - grassLength, 200, Texture.GRASS);
        ///// down of the map
        putTexture(mapCells, 200 - stoneWidth, 200, 0, stoneLength, Texture.ROCKY_GROUND);
        putTexture(mapCells, 200 - stoneWidth, 200, 200 - stoneLength, 200, Texture.ROCKY_GROUND);
        putTexture(mapCells, 200 - stoneWidth - ironWidth, 200 - stoneWidth, 0, ironLength, Texture.METAL);
        putTexture(mapCells, 200 - stoneWidth - ironWidth, 200 - stoneWidth, 200 - ironLength, 200, Texture.METAL);
        return mapCells;
    }

    public static void putTexture(Cell[][] mapCells, int startX, int endX, int startY, int endY, Texture texture) {
        for (int i = startX; i < endX; i++) {
            for (int j = startY; j < endY; j++) {
                mapCells[i][j].setTexture(texture);
            }
        }
    }

    public static ArrayList<Government> getGovernments() {
        return governments;
    }

    public static void setGovernments(ArrayList<Government> governments) {
        DataBank.governments = governments;
    }

    public static ArrayList<Request> getRequests() {
        return requests;
    }

    public static void setRequests(ArrayList<Request> requests) {
        DataBank.requests = requests;
    }

    public static void initializeBuildingName() {
        //damage of Siege tent is not correct
        //rate of apple garden is not correct
        //rates are not correct

        // defend and attack range of towers
        // capacity of armoury
        // damages of towers and killing pit
        // rate of mill and Inn and Iron mine and Ox tether
        buildingName.put("Small stone gatehouse", new CastleBuilding(null, 0, "Small stone gatehouse",
                375, Resource.WOOD, 0, 8, castleBuildingTextures,
                null, null, null, null, null, null, null, null));
        buildingName.put("big stone gatehouse", new CastleBuilding(null, 0, "big stone gatehouse",
                75, Resource.STONE, 20, 10, castleBuildingTextures,
                null, null, null, null, null, null, null, null));
        buildingName.put("Drawbridge", new CastleBuilding(null, 0, "Drawbridge",
                75, Resource.WOOD, 10, 0, castleBuildingTextures,
                null, null, null, null, null, 5, null, null));
        buildingName.put("lookout tower", new CastleBuilding(null, 0, "lookout tower",
                189, Resource.STONE, 10, 0, castleBuildingTextures,
                null, null, 10, 10, null, null, 50, null));
        buildingName.put("perimeter tower", new CastleBuilding(null, 0, "lookout tower",
                189, Resource.STONE, 10, 0, castleBuildingTextures,
                null, null, 10, 10, null, null, 50, null));
        buildingName.put("defensive tower", new CastleBuilding(null, 0, "defensive tower",
                189, Resource.STONE, 15, 0, castleBuildingTextures,
                null, null, 10, 10, null, null, 50, null));
        buildingName.put("square tower", new CastleBuilding(null, 0, "square tower",
                189, Resource.STONE, 35, 0, castleBuildingTextures,
                null, null, 10, 10, null, null, 50, null));
        buildingName.put("circle tower", new CastleBuilding(null, 0, "circle tower",
                189, Resource.STONE, 40, 0, castleBuildingTextures,
                null, null, 10, 10, null, null, 50, null));
        buildingName.put("armoury", new CastleBuilding(null, 0, "armoury",
                75, Resource.WOOD, 5, 0, castleBuildingTextures,
                null, 20, 0, 0, null, null, null, null));
        buildingName.put("barrack", new CastleBuilding(null, 0, "barrack",
                75, Resource.STONE, 15, 0, castleBuildingTextures,
                null, 0, 0, 0, null, null, null, null));
        buildingName.put("Mercenary Post", new CastleBuilding(null, 0, "Mercenary Post",
                75, Resource.WOOD, 10, 0, castleBuildingTextures,
                null, 0, 0, 0, null, null, null, null));
        buildingName.put("engineer guild", new CastleBuilding(null, 100, "engineer guild",
                75, Resource.WOOD, 10, 0, castleBuildingTextures,
                null, 0, 0, 0, null, null, null, null));
        buildingName.put("killing pit", new CastleBuilding(null, 0, "killing pit",
                75, Resource.WOOD, 6, 0, castleBuildingTextures,
                null, 0, 0, 0, null, null, 150, null));
        buildingName.put("Inn", new OtherBuilding(null, 100, "Inn",
                75, Resource.WOOD, 20, 1, castleBuildingTextures,
                null, 2, 0));
        buildingName.put("Mill", new OtherBuilding(null, 0, "Mill",
                75, Resource.WOOD, 20, 3, foodAndIndustryTextures,
                null, 2, 0));
        buildingName.put("Iron mine", new OtherBuilding(null, 0, "Iron mine",
                75, Resource.WOOD, 20, 2, new ArrayList<Texture>(List.of(Texture.METAL)),
                null, 2, 0));
        buildingName.put("Market", new OtherBuilding(null, 0, "Market",
                75, Resource.WOOD, 5, 1, foodAndIndustryTextures,
                null, 0, 0));
        buildingName.put("Ox tether", new OtherBuilding(null, 0, "Ox tether",
                75, Resource.WOOD, 5, 1, foodAndIndustryTextures,
                null, 12, 0));
        buildingName.put("Pitch rig", new OtherBuilding(null, 0, "Pitch rig",
                75, Resource.WOOD, 20, 1, new ArrayList<Texture>(List.of(Texture.PLAIN)),
                null, 2, 0));
        buildingName.put("Quarry", new OtherBuilding(null, 0, "Quarry",
                75, Resource.WOOD, 20, 3, new ArrayList<Texture>(List.of(Texture.ROCKY_GROUND)),
                null, 2, 0));
        buildingName.put("Stockpile", new OtherBuilding(null, 0, "Stockpile",
                75, Resource.WOOD, 0, 0, foodAndIndustryTextures,
                null, 0, 1000));
        buildingName.put("Woodcutter", new OtherBuilding(null, 0, "Woodcutter",
                75, Resource.WOOD, 1, 0, foodAndIndustryTextures,
                null, 2, 0));
        buildingName.put("Hovel", new OtherBuilding(null, 0, "Hovel", 39, Resource.WOOD,
                6, 0, foodAndIndustryTextures, null, null, 8));
        buildingName.put("Church", new OtherBuilding(null, 250, "Church", 309,
                null, 0, 0,
                foodAndIndustryTextures, null, 2, null));
        buildingName.put("Cathedral", new OtherBuilding(null, 1000, "Cathedral", 459,
                null, 0, 0,
                foodAndIndustryTextures, null, 2, null));
        buildingName.put("armourer", new WeaponBuilding(null, 100, "armourer", 114, Resource.WOOD,
                20, 1, new ArrayList<Texture>(List.of(Texture.GROUND)),
                null, new ArrayList<>(List.of(Resource.METAL)), 10));// production rate???
        buildingName.put("blacksmith", new WeaponBuilding(null, 100, "blacksmith", 114, Resource.WOOD,
                20, 1, foodAndIndustryTextures,
                null, new ArrayList<>(List.of(Resource.METAL)), 10));
        buildingName.put("Fletcher", new WeaponBuilding(null, 100, "Fletcher", 114, Resource.WOOD,
                20, 1, foodAndIndustryTextures,
                null, new ArrayList<>(List.of(Resource.METAL)), 10));
        buildingName.put("Poleturner", new WeaponBuilding(null, 100, "Poleturner", 114, Resource.WOOD,
                10, 1, foodAndIndustryTextures,
                null, new ArrayList<>(List.of(Resource.METAL)), 10));
        buildingName.put("oil smelter", new CastleBuilding(null, 100, "oil smelter", 114, Resource.METAL,
                10, 1, castleBuildingTextures,
                null, null, null, null, null, null, null, 2));
        buildingName.put("pitch ditch", new CastleBuilding(null, 0, "pitch ditch", 1000000, Resource.PITCH,
                2, 0, castleBuildingTextures,
                null, null, null, null, null, null, 1000, null));
        buildingName.put("Caged War Dogs", new CastleBuilding(null, 100, "Caged War Dogs", 39, Resource.WOOD,
                10, 0, castleBuildingTextures,
                null, null, null, null, null, null, null, null));
        buildingName.put("Siege tent", new CastleBuilding(null, 0, "Siege tent", 1, null,
                0, 1, castleBuildingTextures,
                null, null, null, null, null, null, 50, null));
        buildingName.put("stable", new CastleBuilding(null, 400, "stable", 114, Resource.WOOD,
                20, 1, castleBuildingTextures,
                null, null, null, null, null, null, null, 4));
        buildingName.put("Apple garden", new OtherBuilding(null, 0, "Apple garden", 39, Resource.WOOD,
                5, 1, gardensAllowedTextures,
                null, 2, null));
        buildingName.put("Dairy products", new OtherBuilding(null, 0, "Dairy products", 39, Resource.WOOD,
                10, 1, gardensAllowedTextures,
                null, 2, null));
        buildingName.put("Hop garden", new OtherBuilding(null, 0, "Hop garden", 39, Resource.WOOD,
                15, 1, gardensAllowedTextures,
                null, 2, null));
        buildingName.put("hunting post", new OtherBuilding(null, 0, "hunting post", 39, Resource.WOOD,
                5, 1, new ArrayList<Texture>(List.of(Texture.GROUND)),
                null, 2, null));
        buildingName.put("Wheat garden", new OtherBuilding(null, 0, "Wheat garden", 39, Resource.WOOD,
                15, 1, gardensAllowedTextures,
                null, 2, null));
        buildingName.put("Bakery", new OtherBuilding(null, 0, "Bakery", 39, Resource.WOOD,
                10, 1, foodAndIndustryTextures,
                null, 2, null));
        buildingName.put("Brewery", new OtherBuilding(null, 0, "Brewery", 39, Resource.WOOD,
                10, 1, foodAndIndustryTextures,
                null, 2, null));
        buildingName.put("Food StockPile", new OtherBuilding(null, 0, "Food StockPile", 39, Resource.WOOD,
                5, 0, foodAndIndustryTextures,
                null, null, 1000));

    }

    public static HashMap<String, Building> getBuildingName() {
        return buildingName;
    }

    public static void initializeAllUnits() {
        //horse?
        //hitpoint of troops
        allUnits.add(new Troop("Archer", 100, null, true, null, 100, 300, 100, 5, 12, new ArrayList<Weapon>(List.of(Weapon.BOW))));
        allUnits.add(new Troop("Crossbowmen", 200, null, true, null, 100, 100, 200, 5, 20, new ArrayList<Weapon>(List.of(Weapon.CROSSBOW, Weapon.LEATHER_ARMOR))));
        allUnits.add(new Troop("Spearmen", 50, null, true, null, 200, 200, 50, 5, 8, new ArrayList<Weapon>(List.of(Weapon.SPEAR))));
        allUnits.add(new Troop("Pikemen", 300, null, true, null, 200, 100, 300, 5, 20, new ArrayList<Weapon>(List.of(Weapon.METAL_ARMOR,Weapon.PIKE))));
        allUnits.add(new Troop("Macemen", 200, null, true, null, 300, 200, 200, 5, 20, new ArrayList<Weapon>(List.of(Weapon.LEATHER_ARMOR, Weapon.MACE))));
        allUnits.add(new Troop("Swordsmen", 100, null, true, null, 300, 100, 100, 5, 40, new ArrayList<Weapon>(List.of(Weapon.SWORDS, Weapon.METAL_ARMOR))));
        allUnits.add(new Troop("Knight", 300, null, true, null, 600, 600, 300, 5, 40, new ArrayList<Weapon>(List.of(Weapon.METAL_ARMOR, Weapon.SWORDS))));
        allUnits.add(new Troop("Tunneler", 50, null, true, null, 200, 300, 50, 5, 30, new ArrayList<>()));
        allUnits.add(new Troop("Black Monk", 200, null, true, null, 200, 100, 200, 5, 10, new ArrayList<>()));
        allUnits.add(new Troop("Archer Bow", 100, null, true, null, 100, 300, 100, 5, 75, new ArrayList<>()));
        allUnits.add(new Troop("Slaves", 100, null, true, null, 50, 300, 100, 5, 5, new ArrayList<>()));
        allUnits.add(new Troop("Slingers", 50, null, true, null, 100, 300, 50, 5, 12, new ArrayList<>()));
        allUnits.add(new Troop("Assassins", 200, null, true, null, 200, 200, 200, 5, 60, new ArrayList<>()));
        allUnits.add(new Troop("Horse Archers", 200, null, true, null, 100, 600, 200, 5, 60, new ArrayList<>()));
        allUnits.add(new Troop("Arabian Swordsmen", 300, null, true, null, 300, 600, 300, 5, 80, new ArrayList<>()));
        allUnits.add(new Troop("Fire Throwers", 100, null, true, null, 300, 600, 100, 5, 100, new ArrayList<>()));
        allUnits.add(new Troop("Laddermen", 50, null, true, null, 0, 300, 50, 0, 3, new ArrayList<>()));
        allUnits.add(new Troop("Engineer", 50, null, true, null, 0, 200, 50, 0, 30, new ArrayList<>()));
        allUnits.add(new Troop("siege tower", 200, null, true, null, 0, 200, 0, 0, 10, new ArrayList<>()));
        allUnits.add(new Troop("portable shields", 200, null, true, null, 0, 200, 0, 0, 10, new ArrayList<>()));
        allUnits.add(new Troop("battering ram", 200, null, true, null, 600, 50, 0, 2, 10, new ArrayList<>()));
        allUnits.add(new Troop("dog", 50, null, true, null, 200, 300, 50, 2, 0, new ArrayList<>()));


    }

    public static Person getUnitByName(String name) {
        for (Person unit : allUnits) {
            if (unit.getName().equals(name))
                return unit;
        }
        return null;
    }

    public static void initializeWalls() {
        walls.add(new Wall("low wall",63,10,1));
        walls.add(new Wall("normal wall",72,15,2));
        walls.add(new Wall("crenelation",84,20,3));
        walls.add(new Wall("gate",63,10,1));
    }

    public static Wall getWallByName(String name) {
        for (Wall wall : walls) {
            if(wall.getName().equals(name))
                return wall;
        }
        return null;
    }

    public static ArrayList<Texture> getCastleBuildingTextures() {
        return castleBuildingTextures;
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        DataBank.stage = stage;
    }

    public static Government currentGovernment;

    public static Government getCurrentGovernment() {
        return currentGovernment;
    }

    public static void setCurrentGovernment(Government currentGovernment) {
        DataBank.currentGovernment = currentGovernment;
    }
    public static ArrayList<Cell> selectedCells = new ArrayList<>();

    public static ArrayList<Cell> getSelectedCells() {
        return selectedCells;
    }

    public static void setSelectedCells(int i, int j) {
        DataBank.selectedCells.add(Map.MAP_NUMBER_ONE.getACell(i,j));
    }
    public static UserNetwork getUserNetworkByUsername(String s){
        for (UserNetwork userNetwork:userNetworks){
            if (userNetwork.getUser().getUsername().equals(s)) return userNetwork;
        }
        return null;
    }

    public static String isThisUserOnline(String username) {
        for (User onlineUser : onlineUsers) {
            if(onlineUser.getUsername().equals(username))
                return " online";
        }
        return " offline";
    }
}
