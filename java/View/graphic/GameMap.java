package View.graphic;

import Controller.*;
import Model.HoverRectangle;
import Model.buildings.BuildingImage;
import Model.gameandbattle.Government;
import Model.gameandbattle.ShopImages;
import Model.gameandbattle.battle.Person;
import Model.gameandbattle.battle.Troop;
import Model.gameandbattle.battle.Weapon;
import Model.gameandbattle.map.Map;
import Model.gameandbattle.shop.Shop;
import Model.gameandbattle.stockpile.Food;
import Model.gameandbattle.stockpile.Resource;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//todo 1.dade tajamoii 2.bug of zoom 3.shortcuts 4.clipboard 5.fire 6.attack
public class GameMap extends Application {


    private VBox copyAndNext;
    private String selectedBuildingName;
    private ArrayList<String> clipBoard;
    private int clipBoardClicks=0;
    private Text clipBoardText;
    private Cell selectedCell;
    private final GameMenuController gameMenuController = new GameMenuController(DataBank.getGovernments(), Map.MAP_NUMBER_ONE);
    private Button sell;
    private Button buy;
    private Text buyPrice;
    private Text sellPrice;
    private HBox shopBox;
    private ShopImages selectedItem;
    private int shopStartRow = 0;
    private final ArrayList<ShopImages> shopImages;

    private boolean sick = false;
    private final Text popularity = new Text();
    private int zoomLevel = 0;
    private int stageWidth = 0;
    private int stageHeight = 0;
    private int imageWidth = 120;
    private int imageHeight = 75;
    private int startRow = 49;
    private int startCol = 8;
    private int size = 10;
    private HBox popularityHBox;
    private ImageView[][] images;
    private Scene scene;
    private final ArrayList<Rectangle> borders = new ArrayList<>();
    private final ArrayList<FadeTransition> transitions = new ArrayList<>();

    private final ArrayList<HoverRectangle> hoverRectangles;
    private final ArrayList<String> buildingNames = new ArrayList<>(List.of("Small stone gatehouse", "big stone gatehouse",
            "Mercenary Post", "barrack", "engineer guild", "Quarry", "Iron mine", "Pitch rig", "Market",
            "Woodcutter", "Wheat garden", "Apple garden", "Hop garden", "hunting post", "Dairy products",
            "lookout tower", "perimeter tower", "defensive tower", "square tower", "circle tower", "Hovel"
            , "StockPile", "stable", "Church"));

    private final ArrayList<String> arabSoldiers = new ArrayList<>(List.of("Arabian Swordsmen", "Archer Bow", "Assassins",
            "Fire Throwers", "Horse Archers", "Slaves", "Slingers"));
    private final ArrayList<BuildingImage> buildingImages = new ArrayList<>();
    private final ArrayList<ImageView> unitImages = new ArrayList<>();
    private BuildingImage temp;
    private HBox hBox; //hBox for buildings
    private VBox foodRateVBox;
    private VBox taxRateVBox;
    private VBox fearRateVBox;
    private HBox unitsHbox;
    private int startIndexForBuilding = 0;
    private boolean isBuildingDragged = false;
    private BuildingImage selectedBuilding;
    private Pane pane;
    private boolean isSelecting = false;
    private ArrayList<Person> selectedTroop = new ArrayList<>();
    private int x1, y1;
    private int selectX = -1, selectY = -1;

    {
        clipBoard=new ArrayList<>();
        shopImages = new ArrayList<>();
        images = new ImageView[size][size];
        images = new ImageView[size][size];
        stageHeight = imageHeight * size;
        stageWidth = imageWidth * size;
        hoverRectangles = new ArrayList<>();
        initializeBuildingImages();
        initializeUnitImages();
    }

    @Override
    public void start(Stage stage) throws Exception {
        pane = new Pane();
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(255, 0, 0, 0.5), null, null)));
        stage.setHeight(stageHeight); //change whenever you want
        stage.setWidth(stageWidth);
        initialize();
        scene = new Scene(pane);
        stage.setScene(scene);
        drag();
        scene.getStylesheets().add(String.valueOf(GameMap.class.getResource("/CSS/style1.css")));
        stage.show();
    }

    private void initializeBuildingImages() {
        int counter = 0;
        for (String buildingName : buildingNames) {
            counter++;
            if (counter == 6)
                counter = 1;
            BuildingImage buildingImage = new BuildingImage(getBuildingImageAddress(buildingName));
            int finalCounter = counter;
            buildingImage.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    Point2D sceneCoordinates = buildingImage.localToScreen(mouseEvent.getX(), mouseEvent.getSceneY());
                    temp = new BuildingImage(getBuildingImageAddress(buildingName));
                    temp.setLayoutX(91 + (finalCounter % 6) * 42.5);
                    temp.setLayoutY(300);
                    temp.setUserData(new Point2D(mouseEvent.getSceneX() -
                            temp.getLayoutX(), mouseEvent.getSceneY() - temp.getLayoutY()));
                    addBuildingFeature(temp, buildingName);
                }
            });
            buildingImage.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    isBuildingDragged = true;
                    if (!pane.getChildren().contains(temp)) {
                        pane.getChildren().add(temp);
                    }
                    Point2D mousePosition = new Point2D(event.getSceneX() - temp.getLayoutX(),
                            event.getSceneY() - temp.getLayoutY());
                    Point2D imagePosition = (Point2D) temp.getUserData();
                    double newX = temp.getLayoutX() + mousePosition.getX() - imagePosition.getX();
                    double newY = temp.getLayoutY() + mousePosition.getY() - imagePosition.getY();
                    temp.setX(newX);
                    temp.setY(newY);
                }
            });
            buildingImage.setOnMouseReleased(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (isBuildingDragged) {
                        Pair<Integer, Integer> coordinates = getBuildingDestination(temp, buildingName);

                    }
                    isBuildingDragged = false;
                }
            });
            buildingImages.add(buildingImage);
        }
    }

    private void initializeUnitImages() {
        for (String arabSoldier : arabSoldiers) {
            ImageView unitImage = new ImageView(new Image(getUnitImageAddress(arabSoldier)));
            unitImage.setScaleX(0.75);
            unitImage.setScaleY(0.75);
            unitImages.add(unitImage);
            unitImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    createArabUnit(arabSoldier);
                }
            });
        }
    }

    private void createNextTurnButton() {
        copyAndNext=new VBox();
        Button button = new Button("next");
        button.getStyleClass().add("button2");
        button.setLayoutY(595);
        button.setLayoutX(0);
        button.setOnMouseClicked(me -> {
            try {
                nextTurn();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Button copy=new Button("Copy"); Button paste=new Button("Paste"); Button clipBoard=new Button("Clipboard");
        copy.getStyleClass().add("button2");  paste.getStyleClass().add("button2");  clipBoard.getStyleClass().add("button2");
        copyAndNext.getChildren().addAll(button,copy,paste,clipBoard); copyAndNext.setSpacing(2); copyAndNext.setLayoutY(595);
        copy.setOnMouseClicked(e->copyClicked());
        clipBoard.setOnMouseClicked(e->clipBoardClicked());
        paste.setOnMouseClicked(e-> {
            try {
                pasteClicked();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });
        Button chat=new Button("chat room");
        chat.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    new ChatRoom().start(DataBank.getStage());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        copyAndNext.getChildren().add(chat);
        pane.getChildren().add(copyAndNext);
    }

    private void pasteClicked() throws InterruptedException {
        BuildingMenuController buildingMenuController = new BuildingMenuController(Map.MAP_NUMBER_ONE, DataBank.getCurrentGovernment());
        String text = buildingMenuController.dropBuilding(selectX, selectY, clipBoard.get(clipBoard.size()-1));
        if (!text.equals("success")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Drop building error");
            alert.setContentText(text);
            alert.showAndWait();
        } else {
            BuildingImage buildingImage = new BuildingImage(getBuildingImageAddress(clipBoard.get(clipBoard.size()-1)));
            Map.MAP_NUMBER_ONE.getACell(selectX,selectY).setBuildingImage(buildingImage);
            update();
        }
        selectX = -1;
        selectY = -1;
    }

    private void clipBoardClicked() {
        if (clipBoardClicks%2==0) {
            String string = "";
            int i = 1;
            for (String name : clipBoard) {
                string = string + i + ": " + name + "\n";
                i++;
            }
            clipBoardText = new Text(string); clipBoardText.setX(110); clipBoardText.setY(620); clipBoardText.setFont(Font.font(15));
            clipBoardText.setFill(Color.BLACK);
            clipBoardClicks++;
            pane.getChildren().add(clipBoardText);
        }
        else {
            pane.getChildren().remove(clipBoardText);
            clipBoardClicks++;
        }

    }

    private void copyClicked() {
        clipBoard.add(selectedBuildingName);
        if (clipBoard.size()>5) clipBoard.remove(0);
    }

    private void nextTurn() throws InterruptedException {
        String text = gameMenuController.nextTurn();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Turn Finished");
        alert.setHeaderText("This month has ended");
        alert.setContentText(text);
        alert.showAndWait();
        pane.getChildren().clear();
        initialize();
    }

    private void createArabUnit(String name) {
        ImageView unit = new ImageView(new Image(getUnitImageAddress(name)));
        SelectBuildingController selectBuildingController = new SelectBuildingController();
        String text = selectBuildingController.createUnit(unit,DataBank.getCurrentGovernment(), name,
                Map.MAP_NUMBER_ONE.getACell(selectedBuilding.getI(), selectedBuilding.getJ()).getBuilding());
        if (text.equals("successful")) {
            dropUnit(unit);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Drop building error");
            alert.setContentText(text);
            alert.showAndWait();
        }

    }

    private void dropUnit(ImageView unit) {
        unit.setScaleX(0.3);
        unit.setScaleY(0.3);
        HBox queues = Map.MAP_NUMBER_ONE.getACell(selectedBuilding.getI(),
                selectedBuilding.getJ()).getQueues();
        queues.setSpacing(-15);
        queues.setAlignment(Pos.CENTER);
        VBox leftQueue;
        VBox rightQueue;
        if (queues.getChildren().size() == 0) {
            leftQueue = new VBox();
            rightQueue = new VBox();
        } else {
            leftQueue = (VBox) queues.getChildren().get(0);
            rightQueue = (VBox) queues.getChildren().get(1);
        }
        rightQueue.setAlignment(Pos.CENTER);
        rightQueue.setSpacing(-60);
        leftQueue.setAlignment(Pos.CENTER);
        leftQueue.setSpacing(-60);
        if (!pane.getChildren().contains(queues)) {
            queues.getChildren().clear();
            queues.getChildren().add(leftQueue);
            queues.getChildren().add(rightQueue);
            pane.getChildren().add(queues);
        }
        if (leftQueue.getChildren().size() < rightQueue.getChildren().size()) {
            setTroopQueues(unit, leftQueue);
        } else {
            setTroopQueues(unit, rightQueue);
        }
        double deltaX = selectedBuilding.getBoundsInParent().getMinX() -
                queues.getBoundsInParent().getMaxX() + 5;
        queues.setLayoutX(queues.getLayoutX() + deltaX);
        double deltaY = selectedBuilding.getBoundsInParent().getCenterY() -
                queues.getBoundsInParent().getCenterY();
        queues.setLayoutY(queues.getLayoutY() + deltaY);

    }

    private void setTroopQueues(ImageView unit, VBox queue) {
        queue.getChildren().add(unit);
    }

    private void initialize() throws InterruptedException {
        for (int i = startRow; i < Math.min(startRow + size, 200); i++) {
            for (int j = startCol; j < Math.min(size + startCol, 200); j++) {
                images[i - startRow][j - startCol] = new ImageView(new Image(Map.MAP_NUMBER_ONE.getACell(i, j).getTexture().getImageAddress()));
                images[i - startRow][j - startCol].setFitWidth(imageWidth);
                images[i - startRow][j - startCol].setFitHeight(imageHeight);
                images[i - startRow][j - startCol].setX((i - startRow) * imageWidth);
                images[i - startRow][j - startCol].setY((j - startCol) * imageHeight);
                pane.getChildren().add(images[i - startRow][j - startCol]);
                setOnHover(images[i - startRow][j - startCol], i, j);
            }
        }
        createBuildingMenuBar();
        update();
    }

    private void setOnHover(ImageView imageView, int i, int j) {
        imageView.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                HoverRectangle hoverRectangle = new HoverRectangle(new Text(MapMenuController.showDetails(Map.MAP_NUMBER_ONE, i, j)));
                hoverRectangle.setX(imageView.getX());
                hoverRectangle.setY(imageView.getY());
                hoverRectangle.setFill(Color.TRANSPARENT);
                hoverRectangle.setStrokeWidth(2);
                hoverRectangle.setStroke(Color.LIGHTBLUE);
                hoverRectangle.setHeight(imageHeight);
                hoverRectangle.setWidth(imageWidth);
                hoverRectangle.setAllTextProperties();
                pane.getChildren().add(hoverRectangle);
                pane.getChildren().add(hoverRectangle.getText());
                hoverRectangles.add(hoverRectangle);
                hoverRectangle.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        for (Rectangle border : borders) {
                            pane.getChildren().remove(border);
                        }
                        borders.clear();
                        DataBank.selectedCells.clear();
                        if (isSelecting && selectX != -1 && selectY != -1) {
                            int minX = Math.min(selectX, i);
                            int minY = Math.min(selectY, j);
                            int maxX = Math.max(selectX, i);
                            int maxY = Math.max(selectY, j);
                            for (int z = minX; z <= maxX; z++) {
                                for (int k = minY; k <= maxY; k++) {
                                    Rectangle border = new Rectangle(imageWidth, imageHeight);
                                    border.setX(images[z - startRow][k - startCol].getX());
                                    border.setY(images[z - startRow][k - startCol].getY());
                                    border.setStroke(Color.BLUE);
                                    border.setFill(Color.TRANSPARENT);
                                    border.setStrokeWidth(2);
                                    pane.getChildren().add(border);
                                    borders.add(border);
                                    DataBank.setSelectedCells(i, j);
                                }
                            }
                            selectY = -1;
                            selectX = -1;
                            isSelecting = false;
                        } else {
                            hoverRectangle.setStroke(Color.RED);
                            Rectangle border = new Rectangle(imageWidth, imageHeight);
                            border.setX(imageView.getX());
                            border.setY(imageView.getY());
                            border.setStroke(Color.RED);
                            border.setFill(Color.TRANSPARENT);
                            border.setStrokeWidth(2);
                            border.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent mouseEvent) {
                                    isSelecting = true;
                                    selectX = i;
                                    selectY = j;
                                }
                            });
                            pane.getChildren().add(border);
                            borders.add(border);
                            DataBank.setSelectedCells(i, j);
                        }
                    }
                });
                hoverRectangle.setOnMouseExited(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        clearAllRectangles();
                    }
                });
            }
        });
    }

    private void clearAllRectangles() {
        try {
            for (HoverRectangle rectangle : hoverRectangles) {
                pane.getChildren().remove(rectangle.getText());
                pane.getChildren().remove(rectangle);
            }
        } catch (Exception e) {

        }
        hoverRectangles.clear();
    }

    private void update() throws InterruptedException {
        clearAllRectangles();
        //images should be loaded before this
        pane.getChildren().clear();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                images[i][j].setFitWidth(imageWidth);
                images[i][j].setFitHeight(imageHeight);
                images[i][j].setX(i * imageWidth);
                images[i][j].setY(j * imageHeight);
                setOnHover(images[i][j], i + startRow, j + startCol);
                pane.getChildren().add(images[i][j]);
                renderDetails(startRow + i, startCol + j);
            }
        }
        createBuildingMenuBar();
        createNextTurnButton();
    }

    private void renderDetails(int i, int j) {
        BuildingImage buildingImage = Map.MAP_NUMBER_ONE.getACell(i, j).getBuildingImage();
        HBox queues = Map.MAP_NUMBER_ONE.getACell(i, j).getQueues();
        if (Map.MAP_NUMBER_ONE.getACell(i, j).getBuilding() != null) {
            addBuildingFeature(buildingImage,Map.MAP_NUMBER_ONE.getACell(i,j).getBuilding().getName());
            if (!pane.getChildren().contains(buildingImage)) {
                pane.getChildren().add(buildingImage);
                double deltaX = images[i - startRow][j - startCol].getBoundsInParent().getCenterX() -
                        buildingImage.getBoundsInParent().getCenterX();
                double deltaY = images[i - startRow][j - startCol].getBoundsInParent().getCenterY() -
                        buildingImage.getBoundsInParent().getCenterY();
                buildingImage.setX(buildingImage.getX() + deltaX);
                buildingImage.setY(buildingImage.getY() + deltaY);
            }
        }
        if (Map.MAP_NUMBER_ONE.getACell(i, j).getPeople().size() != 0) {
            double firstX = queues.getLayoutX();
            double firstY = queues.getLayoutY();
            double deltaX;
            double deltaY;
            if (!pane.getChildren().contains(queues)) {
                if (buildingImage != null) {
                    deltaX = buildingImage.getBoundsInParent().getMinX() -
                            queues.getBoundsInParent().getMaxX() + 5;
                    queues.setLayoutX(firstX + deltaX);
                    deltaY = buildingImage.getBoundsInParent().getCenterY() -
                            queues.getBoundsInParent().getCenterY();
                    queues.setLayoutY(firstY + deltaY);
                } else {
                    deltaX = images[i - startRow][j - startCol].getBoundsInParent().getCenterX() -
                            queues.getBoundsInParent().getCenterX();
                    deltaY = images[i - startRow][j - startCol].getBoundsInParent().getCenterY() -
                            queues.getBoundsInParent().getCenterY();
                    queues.setLayoutX(firstX + deltaX);
                    queues.setLayoutY(firstY + deltaY);
                }
                pane.getChildren().add(queues);
            }

        }
    }

    private void addBuildingFeature(BuildingImage buildingImage, String buildingName) {
        switch (buildingName) {
            case "Small stone gatehouse", "big stone gatehouse" ->
                    buildingImage.setOnMouseClicked(me -> {
                        selectedBuilding = buildingImage;
                        selectedBuildingName=buildingName;
                        goToGovernmentMenu();
                    });
            case "Mercenary Post" -> buildingImage.setOnMouseClicked(me -> {
                selectedBuilding = buildingImage;
                selectedBuildingName=buildingName;
                goToUnitMenu();
            });
            case "Market" -> buildingImage.setOnMouseClicked(me -> {
                selectedBuilding = buildingImage;
                selectedBuildingName=buildingName;
                goToShopMenu();
            });
        }
    }

    private void goToShopMenu() {
        removeEverything();
        shopBox = new HBox();
        shopBox.setLayoutX(350);
        shopBox.setLayoutY(610);
        pane.getChildren().add(shopBox);
        createShopImages();
        pane.getChildren().remove(hBox);
        createShopButtonsAndText();
        updateShopImages(0);
    }

    private void createShopButtonsAndText() {
        sell = new Button("Sell");
        sell.setLayoutX(460);
        sell.setLayoutY(655);
        buy = new Button("Buy");
        buy.setLayoutX(460);
        buy.setLayoutY(680);
        sellPrice = new Text("-");
        sellPrice.setLayoutX(510);
        sellPrice.setLayoutY(675);
        sellPrice.setFill(Color.DARKORANGE);
        sellPrice.setFont(Font.font(15));
        buyPrice = new Text("-");
        buyPrice.setLayoutX(510);
        buyPrice.setLayoutY(702);
        buyPrice.setFill(Color.DARKORANGE);
        buyPrice.setFont(Font.font(15));
        pane.getChildren().addAll(buy, buyPrice, sell, sellPrice);
        Button back = new Button("Back");
        pane.getChildren().add(back);
        back.setLayoutY(610);
        back.setLayoutX(275);
        back.setOnMouseClicked(me -> backToBuildingMenuFromShop());
        sell.setOnMouseClicked(e -> sellClicked());
        buy.setOnMouseClicked(e -> buyClicked());
        Button tradeMenu = new Button("trade menu");
        pane.getChildren().add(tradeMenu);
        tradeMenu.setLayoutX(620);
        tradeMenu.setLayoutY(612.5);
        tradeMenu.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    new Trade().start(DataBank.getStage());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void sellClicked() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        if (selectedItem == null) {
            alert.setContentText("you did not choose an item");
            alert.showAndWait();
        } else {
            String name = getSelectedItemName();
            if (ShopMenuController.sell(name, DataBank.getCurrentGovernment())) {
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("successful");
                alert.showAndWait();
            } else {
                alert.setContentText("dont have enough items");
                alert.showAndWait();
            }
        }
    }

    private String getSelectedItemName() {
        if (selectedItem.getFood() != null) return selectedItem.getFood().getName();
        else if (selectedItem.getWeapon() != null) return selectedItem.getWeapon().getName();
        else return selectedItem.getResource().getName();
    }

    private void backToBuildingMenuFromShop() {
        pane.getChildren().removeAll(shopBox, sell, buy, sellPrice, buyPrice);
        createBuildingMenuBar();
    }

    private void buyClicked() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        if (selectedItem == null) {
            alert.setContentText("you did not choose an item");
            alert.showAndWait();
        } else {
            String name = getSelectedItemName();
            if (ShopMenuController.buy(name, DataBank.getCurrentGovernment())) {
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("successful");
                alert.showAndWait();
            } else {
                alert.setContentText("dont have enough golds");
                alert.showAndWait();
            }
        }
    }

    private void updateShopImages(int start) {
        shopBox.getChildren().clear();
        if (start <= 0) {
            shopStartRow = shopStartRow + shopImages.size();
            start = start + shopImages.size();
        }
        for (int i = start; i < start + 6; i++) {
            ShopImages shopImages1 = shopImages.get(i % (shopImages.size()));
            shopBox.getChildren().add(shopImages.get(i % (shopImages.size())));
            shopImages.get(i % shopImages.size()).setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (shopImages1.getFood() != null) {
                        sellPrice.setText(shopImages1.getFood().getSellPrice() + "");
                        buyPrice.setText(shopImages1.getFood().getPrice() + "");
                    } else if (shopImages1.getWeapon() != null) {
                        sellPrice.setText(shopImages1.getWeapon().getSellPrice() + "");
                        buyPrice.setText(shopImages1.getWeapon().getPrice() + "");
                    } else {
                        sellPrice.setText(shopImages1.getResource().getSellPrice() + "");
                        buyPrice.setText(shopImages1.getResource().getPrice() + "");
                    }
                    selectedItem = shopImages1;
                }
            });
        }
    }

    private void createShopImages() {
        Shop shop = Shop.getShop();
        for (Food food : shop.getFoods())
            shopImages.add(new ShopImages(getClass().getResource("/IMAGE/Shop/" + food.getName() + ".png").toExternalForm(), food, null, null));
        for (Resource food : shop.getResources())
            shopImages.add(new ShopImages(getClass().getResource("/IMAGE/Shop/" + food.getName() + ".png").toExternalForm(), null, null, food));
        for (Weapon weapon : shop.getWeapons())
            shopImages.add(new ShopImages(getClass().getResource("/IMAGE/Shop/" + weapon.getName() + ".png").toExternalForm(), null, weapon, null));
    }

    private void goToUnitMenu() {
        createBackButton();
        removeEverything();
        if (!pane.getChildren().contains(unitsHbox)) {
            unitsHbox = new HBox();
            pane.getChildren().add(unitsHbox);
            showUnits();
        }
    }

    private void removeEverything() {
        pane.getChildren().remove(unitsHbox);
        pane.getChildren().remove(hBox);
        pane.getChildren().remove(foodRateVBox);
        pane.getChildren().remove(taxRateVBox);
        pane.getChildren().remove(popularityHBox);
    }

    private void goToGovernmentMenu() {
        removeEverything();
        createBackButton();
        pane.getChildren().remove(hBox);
        if (!pane.getChildren().contains(taxRateVBox)) {
            foodRateVBox = new VBox();
            taxRateVBox = new VBox();
            fearRateVBox = new VBox();
            popularityHBox = new HBox();
            pane.getChildren().add(taxRateVBox);
            showTaxRate();
        }
    }

    private void drag() {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                String keyName = keyEvent.getCode().getName();
                if (keyName.equals("D")) {
                    try {
                        right();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else if (keyName.equals("A")) {
                    try {
                        left();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else if (keyName.equals("W")) {
                    try {
                        up();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else if (keyName.equals("S")) {
                    try {
                        down();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else if (keyName.equals("Z")) {
                    try {
                        zoomIn();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                else if (keyName.equals("X")) {
                    try {
                        zoomOut();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                else if (keyName.equals("B"))
                    setSick(!sick);
                else if (keyName.equals("M")) {
                    if (selectX != -1 && selectY != -1) {
                        moveUnit();
                    }
                } else if(keyName.equals("E")) {
                    if (selectX != -1 && selectY != -1) {
                        try {
                            airAttack();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else if (keyName.equals("R")) {
                    if(selectX != -1 && selectY != -1) {
                        attack();
                    }
                }
            }
        });
    }

    private void moveUnit() {
        showDialog("MOVE");
        HBox queues = new HBox();
        VBox leftQueue = new VBox();
        VBox rightQueue = new VBox();
        prepareTempHBox(queues, rightQueue, leftQueue);
        pane.getChildren().remove(Map.MAP_NUMBER_ONE.getACell(selectX, selectY).getQueues());// this statement is not necessary
        Map.MAP_NUMBER_ONE.getACell(selectX, selectY).getQueues().getChildren().clear();
        pane.getChildren().add(queues);
        String text = selectUnit(selectX, selectY).moveUnit(x1, y1, Map.MAP_NUMBER_ONE,
                queues, images, startRow, startCol);
        if(!text.contains("success")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Move Unit Error");
            alert.setContentText(text);
            alert.showAndWait();
        }
        selectX = -1;
        selectY = -1;
    }

    private void airAttack() throws InterruptedException {
        showDialog("AIR ATTACK");
        String text = selectUnit(selectX, selectY).skyAttack(x1,y1);
        setAttackBanner();
        if(!text.contains("success")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Air Attack Error");
            alert.setContentText(text);
            alert.showAndWait();
        }
        if(Map.MAP_NUMBER_ONE.getACell(x1,y1).getBuilding() != null) {
            if (Map.MAP_NUMBER_ONE.getACell(x1,y1).getBuilding().isFiery()) {
                BuildingImage fire = new BuildingImage(getBuildingImageAddress("fire"));
                Map.MAP_NUMBER_ONE.getACell(x1,y1).setBuildingImage(fire);
                update();
            }
        }
        selectX = -1;
        selectY = -1;
    }

    private void attack() {
        showDialog("ATTACK");
        String text = selectUnit(selectX, selectY).attack(x1,y1);
        setAttackBanner();
        if(!text.contains("success")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Attack Error");
            alert.setContentText(text);
            alert.showAndWait();
        }
        HBox queues = new HBox();
        VBox leftQueue = new VBox();
        VBox rightQueue = new VBox();
        prepareTempHBox(queues, rightQueue, leftQueue);
        pane.getChildren().remove(Map.MAP_NUMBER_ONE.getACell(selectX, selectY).getQueues());// this statement is not necessary
        Map.MAP_NUMBER_ONE.getACell(selectX, selectY).getQueues().getChildren().clear();
        pane.getChildren().add(queues);
        new MoveAnimation(queues,images[x1 - startRow][y1 -startCol]).play();
        Map.MAP_NUMBER_ONE.getACell(x1,y1).setQueues(queues);
        selectX = -1;
        selectY = -1;
    }

    private void showDialog(String type) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("select x,y");
        dialog.setHeaderText("Enter your destination X and Y");
        ButtonType changeButtonType = new ButtonType(type, ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);
        TextField textFieldX = new TextField();
        TextField textFieldY = new TextField();
        textFieldX.setPromptText("X");
        textFieldY.setPromptText("Y");
        dialog.getDialogPane().lookupButton(changeButtonType).setDisable(true);

        textFieldX.textProperty().addListener((observable, oldValue, newValue) -> {
            dialog.getDialogPane().lookupButton(changeButtonType).setDisable(newValue.trim().isEmpty());
        });
        textFieldY.textProperty().addListener((observable, oldValue, newValue) -> {
            dialog.getDialogPane().lookupButton(changeButtonType).setDisable(newValue.trim().isEmpty());
        });
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.addRow(0, new Label("X :"), textFieldX);
        gridPane.addRow(1, new Label("Y :"), textFieldY);
        dialog.getDialogPane().setPrefWidth(600);
        dialog.getDialogPane().setPrefHeight(400);
        dialog.getDialogPane().setContent(gridPane);
        Platform.runLater(textFieldX::requestFocus);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == changeButtonType) {
                return new Pair<>(textFieldX.getText(), textFieldY.getText());
            }
            return null;
        });
        dialog.showAndWait().ifPresent(result -> {
            x1 = Integer.parseInt(textFieldX.getText());
            y1 = Integer.parseInt(textFieldY.getText());
        });
    }

    private void prepareTempHBox(HBox queues, VBox rightQueue, VBox leftQueue) {
        queues.setSpacing(-15);
        queues.setAlignment(Pos.CENTER);
        rightQueue.setAlignment(Pos.CENTER);
        rightQueue.setSpacing(-60);
        leftQueue.setAlignment(Pos.CENTER);
        leftQueue.setSpacing(-60);
        queues.getChildren().addAll(leftQueue, rightQueue);
        VBox rightTemp = (VBox) Map.MAP_NUMBER_ONE.getACell(selectX, selectY).getQueues().getChildren().get(1);
        VBox leftTemp = (VBox) Map.MAP_NUMBER_ONE.getACell(selectX, selectY).getQueues().getChildren().get(0);
        ArrayList<Node> leftNodes = new ArrayList<>(leftTemp.getChildren());
        ArrayList<Node> rightNodes = new ArrayList<>(rightTemp.getChildren());
        rightQueue.getChildren().addAll(rightNodes);
        leftQueue.getChildren().addAll(leftNodes);
        queues.setLayoutX(Map.MAP_NUMBER_ONE.getACell(selectX, selectY).getQueues().getLayoutX());
        queues.setLayoutY(Map.MAP_NUMBER_ONE.getACell(selectX, selectY).getQueues().getLayoutY());
    }

    private void zoomOut() throws InterruptedException {
        size++;
        zoomLevel--;
        imageWidth = stageWidth / size;
        imageHeight = stageHeight / size;
        images = new ImageView[size][size];
        pane.getChildren().clear();
        initialize();
    }

    private void zoomIn() throws InterruptedException {
        size--;
        zoomLevel++;
        imageWidth = stageWidth / size;
        imageHeight = stageHeight / size;
        images = new ImageView[size][size];
        pane.getChildren().clear();
        initialize();
    }

    private void up() throws InterruptedException {
        startCol--;
        for (int i = size - 1; i > 0; i--) {
            for (int j = 0; j < size; j++) {
                images[j][i] = images[j][i - 1];
            }
        }
        for (int j = 0; j < size; j++) {
            images[j][0] = new ImageView(new Image(Map.MAP_NUMBER_ONE.getACell(startRow + j,
                    startCol).getTexture().getImageAddress()));
        }
        update();

    }

    private void down() throws InterruptedException {
        startCol++;
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size; j++) {
                images[j][i] = images[j][i + 1];
            }
        }
        for (int j = 0; j < size; j++) {
            images[j][size - 1] = new ImageView(new Image(Map.MAP_NUMBER_ONE.getACell(startRow + j,
                    startCol + size - 1).getTexture().getImageAddress()));
        }
        update();
    }

    private void left() throws InterruptedException {
        startRow--;
        for (int i = size - 1; i > 0; i--) {
            for (int j = 0; j < size; j++) {
                images[i][j] = images[i - 1][j];
            }
        }
        for (int j = 0; j < size; j++) {
            images[0][j] = new ImageView(new Image(Map.MAP_NUMBER_ONE.getACell(startRow,
                    j + startCol).getTexture().getImageAddress()));
        }
        update();
    }

    private void right() throws InterruptedException {
        startRow++;
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size; j++) {
                images[i][j] = images[i + 1][j];
            }
        }
        for (int j = 0; j < size; j++) {
            images[size - 1][j] = new ImageView(new Image(Map.MAP_NUMBER_ONE.getACell(size - 1 + startRow,
                    j + startCol).getTexture().getImageAddress()));
        }
        update();
    }

    private Pair<Integer, Integer> getBuildingDestination(BuildingImage buildingImage, String buildingName) {
        double maxIntersectionArea = 0;
        int finalI = 0;
        int finalJ = 0;
        for (int i = 0; i < images.length; i++) {
            for (int j = 0; j < images[i].length; j++) {
                Bounds buildingBounds = buildingImage.getBoundsInParent();
                Bounds imageBounds = images[i][j].getBoundsInParent();
                if (buildingBounds.intersects(imageBounds)) {
                    double startX = Math.max(buildingBounds.getMinX(), imageBounds.getMinX());
                    double endX = Math.min(buildingBounds.getMaxX(), imageBounds.getMaxX());
                    double startY = Math.max(buildingBounds.getMinY(), imageBounds.getMinY());
                    double endY = Math.min(buildingBounds.getMaxY(), imageBounds.getMaxY());
                    double width = endX - startX;
                    double height = endY - startY;
                    double intersectionArea = width * height;
                    if (maxIntersectionArea < intersectionArea) {
                        maxIntersectionArea = intersectionArea;
                        finalI = i;
                        finalJ = j;
                    }
                }
            }
        }
        double deltaX = images[finalI][finalI].getBoundsInParent().getCenterX() -
                buildingImage.getBoundsInParent().getCenterX();
        double deltaY = images[finalI][finalJ].getBoundsInParent().getCenterY() -
                buildingImage.getBoundsInParent().getCenterY();
        buildingImage.setX(buildingImage.getX() + deltaX);
        buildingImage.setY(buildingImage.getY() + deltaY);
        BuildingMenuController buildingMenuController = new BuildingMenuController(Map.MAP_NUMBER_ONE, DataBank.getCurrentGovernment());
        String text = buildingMenuController.dropBuilding(finalI + startRow, finalJ + startCol, buildingName);
        if (!text.equals("success")) {
            pane.getChildren().remove(buildingImage);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Drop building error");
            alert.setContentText(text);
            alert.showAndWait();
            return null;
        }
        temp.setCoordinates(finalI + startRow, finalJ + startCol);
        Map.MAP_NUMBER_ONE.getACell(finalI + startRow, finalJ + startCol).setBuildingImage(buildingImage);
        return new Pair<>(finalI, finalJ);
    }

    public void createBuildingMenuBar() {
        javafx.scene.image.ImageView menu = new ImageView(new Image(
                Objects.requireNonNull(ProfileMenu.class.getResource("/IMAGE/Buildings/building menu.png")).toString(), 1200, 182, false, false));
        javafx.scene.image.ImageView front = new ImageView(new Image(
                Objects.requireNonNull(ProfileMenu.class.getResource("/IMAGE/ICONS/front.png")).toString(), 22.5, 22.5, false, false));
        javafx.scene.image.ImageView back = new ImageView(new Image(
                Objects.requireNonNull(ProfileMenu.class.getResource("/IMAGE/ICONS/back.png")).toString(), 22.5, 22.5, false, false));
        javafx.scene.image.ImageView minimap = new ImageView(new Image(
                Objects.requireNonNull(ProfileMenu.class.getResource("/IMAGE/minimap.png")).toString(), 125, 125, false, false));
        minimap.setX(990);
        minimap.setY(590);
        popularity.setText(DataBank.getCurrentGovernment().getPopularity() + "");
        Text gold = new Text();
        gold.setText(DataBank.getCurrentGovernment().getCoin() + "");
        Rotate rotate = new Rotate(15);
        popularity.getTransforms().add(rotate);
        gold.getTransforms().add(rotate);
        gold.setX(850);
        gold.setY(462.5);
        popularity.setX(855);
        popularity.setY(437.5);
        popularity.setFont(new Font("Callibri", 20));
        gold.setFill(Color.GREEN);
        popularity.setFill(Color.GREEN);
        hBox = new HBox(); //hBox for buildings
        hBox.setAlignment(Pos.CENTER);
        hBox.setLayoutX(267.5);
        hBox.setLayoutY(612.5);
        hBox.setSpacing(10);
        front.setOnMouseClicked(mouseEvent -> moveMenuToRight(hBox));
        back.setOnMouseClicked(mouseEvent -> moveMenuToLeft(hBox));
        menu.setX(0);
        menu.setY(532.5);
        front.setX(635.5);
        front.setY(688.5);
        back.setY(688.5);
        back.setX(265.5);
        pane.getChildren().addAll(menu, front, back, minimap);
        setBuildingImages(hBox);
        pane.getChildren().add(hBox);
        createNextTurnButton();
        // pane.getChildren().addAll(popularity,gold); todo : please fix this bug mini
    }

    private void showFoodRate() {
        foodRateVBox.setSpacing(10);
        foodRateVBox.setAlignment(Pos.CENTER);
        foodRateVBox.setLayoutX(400);
        foodRateVBox.setLayoutY(620);
        Slider slider = new Slider(-2, 2, 5);
        setSlider(slider);
        slider.setValue(DataBank.currentGovernment.getFoodRate());
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int roundedValue = (int) Math.round(newValue.doubleValue());
            slider.setValue(roundedValue);
            GovernmentMenuController.changeFoodRate(roundedValue, DataBank.getCurrentGovernment());
        });
        Text text = new Text();
        text.setText("Food Rate");
        text.setFont(new Font("Callibri", 20));
        foodRateVBox.getChildren().addAll(text, slider);
    }

    private void showFearRate() {
        fearRateVBox.setSpacing(10);
        fearRateVBox.setAlignment(Pos.CENTER);
        fearRateVBox.setLayoutX(400);
        fearRateVBox.setLayoutY(620);
        Slider slider = new Slider(-5, 5, 11);
        setSlider(slider);
        slider.setValue(DataBank.currentGovernment.getFearRate());
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int roundedValue = (int) Math.round(newValue.doubleValue());
            slider.setValue(roundedValue);
            GovernmentMenuController.changeFearRate(roundedValue, DataBank.getCurrentGovernment());
        });
        Text text = new Text();
        text.setText("Fear Rate");
        text.setFont(new Font("Callibri", 20));
        fearRateVBox.getChildren().addAll(text, slider);
    }

    private void createBackButton() {
        Button back = new Button("back");
        back.getStyleClass().add("button2");
        pane.getChildren().add(back);
        back.setLayoutY(605);
        back.setLayoutX(275);
        back.setOnMouseClicked(me -> backToBuildingMenu(back));
    }

    private void showPopularityFactors() {
        Text text = new Text();
        text.setText("\nPopularity");
        text.setFont(new Font("Callibri", 20));
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);
        VBox vBox1 = new VBox();
        vBox1.setSpacing(10);
        vBox1.setAlignment(Pos.CENTER);
        VBox vBox2 = new VBox();
        vBox1.setSpacing(10);
        vBox1.setAlignment(Pos.CENTER);
        HBox hBox1 = getFactorHbox(4 * DataBank.getCurrentGovernment().getFoodRate(), "Food");
        int taxRate;
        if (DataBank.getCurrentGovernment().getTaxRate() <= 0)
            taxRate = (-2) * DataBank.getCurrentGovernment().getTaxRate() + 1;
        else
            taxRate = (-2) * DataBank.getCurrentGovernment().getTaxRate();
        HBox hBox2 = getFactorHbox(taxRate, "Tax");
        int populationRate = DataBank.getCurrentGovernment().getPopulation() -
                DataBank.getCurrentGovernment().getMaxPopulation() * DataBank.getCurrentGovernment().getPopularity() / 100;
        HBox hBox3 = getFactorHbox(populationRate, "Crowding");
        HBox hBox4 = getFactorHbox(DataBank.getCurrentGovernment().getFearRate(), "Fear Factor");
        int religionRate = 0;
        if (DataBank.getCurrentGovernment().getBuildingByName("Church") != null)
            religionRate = 1;
        HBox hBox5 = getFactorHbox(religionRate, "Religion");
        int Ale = 0; //todo correct this for inn
        HBox hBox6 = getFactorHbox(Ale, "Ale coverage");

        int sum = 4 * DataBank.getCurrentGovernment().getFoodRate() + taxRate + populationRate +
                DataBank.getCurrentGovernment().getFearRate() + religionRate + Ale;
        HBox hBox7 = getFactorHbox(sum, "Sum");
        vBox.getChildren().addAll(hBox1, hBox2, hBox3);
        vBox1.getChildren().addAll(hBox4, hBox5, hBox6);
        vBox2.getChildren().addAll(text, hBox7);
        popularityHBox.setAlignment(Pos.CENTER);
        popularityHBox.setSpacing(10);
        popularityHBox.setLayoutX(300);
        popularityHBox.setLayoutY(620);
        popularityHBox.getChildren().addAll(vBox2, vBox, vBox1);
    }

    public HBox getFactorHbox(int rate, String factor) {
        Text Factor = new Text(factor);
        String rateString = rate + "";
        Text Rate = new Text();
        if (rate > 0) {
            rateString = "+" + rate;
            Rate.setFill(Color.GREEN);
        } else if (rate < 0)
            Rate.setFill(Color.RED);
        else
            Rate.setFill(Color.YELLOW);
        Rate.setText(rateString);
        Rate.setStyle("-fx-font-weight: bold");
        HBox hBox = new HBox();
        hBox.getChildren().addAll(Rate, getFaceMask(rate), Factor);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        return hBox;
    }

    private ImageView getFaceMask(int rate) {
        if (rate > 0) {
            return new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/IMAGE/face1.png")).toExternalForm()));
        } else if (rate == 0) {
            return new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/IMAGE/face2.png")).toExternalForm()));
        } else {
            return new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/IMAGE/face3.png")).toExternalForm()));
        }
    }


    private void showTaxRate() {
        taxRateVBox.setAlignment(Pos.CENTER);
        taxRateVBox.setSpacing(10);
        taxRateVBox.setLayoutX(400);
        taxRateVBox.setLayoutY(620);
        Slider slider = new Slider(-3, 8, 12);
        setSlider(slider);
        slider.setValue(DataBank.currentGovernment.getTaxRate());
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int roundedValue = (int) Math.round(newValue.doubleValue());
            slider.setValue(roundedValue);
            GovernmentMenuController.changeTaxRate(roundedValue, DataBank.getCurrentGovernment());
        });
        Text text = new Text();
        text.setText("Tax Rate");
        text.setFont(new Font("Callibri", 20));
        taxRateVBox.getChildren().addAll(text, slider);
    }

    private void showUnits() {
        unitsHbox.setAlignment(Pos.CENTER);
        unitsHbox.setSpacing(8);
        unitsHbox.setLayoutY(627.5);
        unitsHbox.setLayoutX(285);
        setUnitImages();
    }

    private void backToBuildingMenu(Button button) {
        pane.getChildren().remove(foodRateVBox);
        pane.getChildren().remove(taxRateVBox);
        pane.getChildren().remove(unitsHbox);
        pane.getChildren().remove(popularityHBox);
        pane.getChildren().remove(button);
        createBuildingMenuBar();
    }

    private void setSlider(Slider slider) {
        slider.setPrefWidth(175);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setBlockIncrement(1);
    }

    private String getBuildingImageAddress(String name) {
        return Objects.requireNonNull(getClass().getResource("/IMAGE/Buildings/Other Buildings/" + name + ".png")).toExternalForm();
    }

    private String getUnitImageAddress(String name) {
        return Objects.requireNonNull(getClass().getResource("/IMAGE/Units/" + name + ".png")).toExternalForm();
    }

    private void moveMenuToRight(HBox hBox) {
        if (pane.getChildren().contains(hBox)) {
            if (startIndexForBuilding != 20) {
                startIndexForBuilding += 5;
                hBox.getChildren().clear();
                setBuildingImages(hBox);
            }
        } else if (pane.getChildren().contains(taxRateVBox)) {
            clearEverything();
            pane.getChildren().remove(taxRateVBox);
            pane.getChildren().add(foodRateVBox);
            showFoodRate();
        } else if (pane.getChildren().contains(foodRateVBox)) {
            clearEverything();
            pane.getChildren().remove(foodRateVBox);
            pane.getChildren().add(fearRateVBox);
            showFearRate();
        } else if (pane.getChildren().contains(fearRateVBox)) {
            clearEverything();
            pane.getChildren().remove(fearRateVBox);
            pane.getChildren().add(popularityHBox);
            showPopularityFactors();
        } else if (pane.getChildren().contains(sell)) {
            updateShopImages(++shopStartRow);
        }
    }

    private void clearEverything() {
        fearRateVBox.getChildren().clear();
        foodRateVBox.getChildren().clear();
        taxRateVBox.getChildren().clear();
        popularityHBox.getChildren().clear();
    }

    private void moveMenuToLeft(HBox hBox) {
        if (pane.getChildren().contains(hBox)) {
            if (startIndexForBuilding != 0) {
                startIndexForBuilding -= 5;
                hBox.getChildren().clear();
                setBuildingImages(hBox);
            }
        } else if (pane.getChildren().contains(foodRateVBox)) {
            clearEverything();
            pane.getChildren().remove(foodRateVBox);
            pane.getChildren().add(taxRateVBox);
            showTaxRate();
        } else if (pane.getChildren().contains(fearRateVBox)) {
            clearEverything();
            pane.getChildren().remove(fearRateVBox);
            pane.getChildren().add(foodRateVBox);
            showFoodRate();
        } else if (pane.getChildren().contains(popularityHBox)) {
            clearEverything();
            pane.getChildren().remove(popularityHBox);
            pane.getChildren().add(fearRateVBox);
            showFearRate();
        } else if (pane.getChildren().contains(sell)) {
            updateShopImages(--shopStartRow);
        }
    }

    public void setBuildingImages(HBox hBox) {
        for (int i = startIndexForBuilding; i < Math.min(startIndexForBuilding + 5, buildingImages.size()); i++) {
            hBox.getChildren().add(buildingImages.get(i));
        }
    }

    public void setUnitImages() {
        for (ImageView unitImage : unitImages) {
            unitsHbox.getChildren().add(unitImage);
        }
    }

    public UnitMenuController selectUnit(int x, int y) {
        UnitMenuController unitMenuController;
        for (Person person : Map.MAP_NUMBER_ONE.getACell(x, y).getPeople()) {
            if (person.getGovernment().equals(DataBank.getCurrentGovernment()) && person instanceof Troop)
                selectedTroop.add(person);
        }
        unitMenuController = new UnitMenuController(selectedTroop, x, y, DataBank.getCurrentGovernment());
        unitMenuController.setMap(Map.MAP_NUMBER_ONE);
        return unitMenuController;
    }

    public void setSick(boolean sick) {
        this.sick = sick;
        if (sick) {
            DataBank.getCurrentGovernment().setPopularity(DataBank.getCurrentGovernment().getPopularity() - 10);
            popularity.setText(DataBank.getCurrentGovernment().getPopularity() + "");
            for (int i = 0; i < 7; i += 2) {
                for (int j = (i / 2) % 2; j < 10; j += 2) {
                    FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), images[j][i]);
                    fadeTransition.setFromValue(1);
                    fadeTransition.setToValue(0.5);
                    fadeTransition.setAutoReverse(true);
                    fadeTransition.setCycleCount(-1);
                    fadeTransition.play();
                    transitions.add(fadeTransition);
                }
            }
        } else {
            for (int i = 0; i < 7; i += 2) {
                for (int j = (i / 2) % 2; j < 10; j += 2) {
                    images[j][i].setOpacity(1);
                }
            }
            for (FadeTransition transition : transitions) {
                transition.stop();
            }
        }
    }

    private Government winner() {
        for (Government government : gameMenuController.getGovernments()) {
            if (government.isAlive()) return government;
        }
        return null;
    }

    public void setAttackBanner()
    {
        ImageView attackBanner = new ImageView(new Image(getClass().getResource("/IMAGE/Buildings/Other Buildings/attack.png").toExternalForm()));
        attackBanner.setX(0);
        attackBanner.setY(0);
        attackBanner.setFitHeight(100);
        attackBanner.setFitWidth(100);
        pane.getChildren().add(attackBanner);
        Timeline attackLogo = new Timeline(new KeyFrame(Duration.millis(3000), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                pane.getChildren().remove(attackBanner);
            }
        }));
        attackLogo.play();
        attackLogo.setCycleCount(1);
    }
}