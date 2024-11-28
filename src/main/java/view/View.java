package view;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;



public class View extends Application {
    // measures
    private int terrainStageRows = 448;
    private int terrainStageColumns = 512;
    private int menuStageRows = 150;
    private int menuStageColumns = 300;
    private int tileRows = 32;
    private int tileColumns = 32;

    // menu
    private VBox menuVBox;
    private String boardname;
    private TextField boardnameText;
    private int rows;
    private TextField rowsText;
    private int columns;
    private TextField columnsText;
    private Button openButton;
    private Scene menuScene;

    // terrain
    private Group terrainGroup;
    private Image terrainImage;
    private ImageView terrainImageView;
    private String terrainMatrix[][];
    private Button terrainButtons[][];
    private boolean first = true;
    private int firstRow;
    private int firstColumn;
    private boolean second = false;
    private int secondRow;
    private int secondColumn;
    private Scene terrainScene;


    
    @Override
    public void start(Stage stage) {
        this.setMenuScene(stage);
        stage.setTitle("Mapper");
        stage.setResizable(true);
        stage.show();
    }

    private void setMenuScene(Stage stage) {
        // creating the text fields
        this.boardnameText = new TextField();
        this.boardnameText.setPromptText("Boardname");
        this.rowsText = new TextField();
        this.rowsText.setPromptText("Rows");
        this.columnsText = new TextField();
        this.columnsText.setPromptText("Columns");

        // creating the open button
        this.openButton = new Button("Open");
        this.openButton.setOnAction(event -> {
            this.boardname = this.boardnameText.getText();
            this.rows = Integer.parseInt(this.rowsText.getText());
            this.columns = Integer.parseInt(this.columnsText.getText());
            this.setTerrainScene(stage);
        });

        // creating the menu vbox
        this.menuVBox = new VBox(10); 
        this.menuVBox.getChildren().addAll(this.boardnameText, this.rowsText, this.columnsText, this.openButton);

        // creating the menu scene
        this.menuScene = new Scene(this.menuVBox, this.menuStageColumns, this.menuStageRows);

        // setting the menu scene to stage
        stage.setScene(this.menuScene);
    }

    private void setTerrainScene(Stage stage) {
        // creating the terrain group
        this.terrainGroup = new Group();

        // adding terrain image view
        this.terrainImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/main/resources/terrain_images/" + this.boardname + ".png")));
        this.terrainImageView = new ImageView(this.terrainImage);
        this.terrainGroup.getChildren().add(this.terrainImageView);

        // creating the matrix
        this.terrainMatrix = new String[this.rows][this.columns];
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                    this.terrainMatrix[i][j] = "w";
            }
        }

        // adding the buttons
        this.terrainButtons = new Button[this.rows][this.columns];
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                final int row = i;
                final int column = j;
                this.terrainButtons[row][column] = new Button();
                this.terrainButtons[row][column].setPrefSize(this.tileRows, this.tileColumns);
                this.terrainButtons[row][column].setLayoutX(this.tileColumns * column);
                this.terrainButtons[row][column].setLayoutY(this.tileRows * row);
                this.terrainButtons[row][column].setStyle("-fx-background-color: transparent;");
                this.terrainGroup.getChildren().add(this.terrainButtons[row][column]);
                this.terrainButtons[row][column].setOnAction(event -> {
                    if (this.terrainMatrix[row][column].equals("w")) {
                        this.terrainButtons[row][column].setStyle("-fx-background-color: red;");
                        this.terrainMatrix[row][column] = "e";
                        if (this.first) {
                            this.firstRow = row;
                            this.firstColumn = column;
                            this.first = false;
                            this.second = true;
                        }
                        else if (this.second) {
                            this.secondRow = row;
                            this.secondColumn = column;
                            this.second = false;
                            this.colorRectangle();
                            this.first = true;
                        }
                    }
                    else if (this.terrainMatrix[row][column].equals("e")) {
                        this.terrainButtons[row][column].setStyle("-fx-background-color: transparent;");
                        this.terrainMatrix[row][column] = "w";
                        this.first = true;
                        this.second = false;
                    }
                });
            }
        }

        // creating the terrain scene
        this.terrainScene = new Scene(this.terrainGroup, this.terrainStageColumns, this.terrainStageRows);
        this.terrainScene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            switch (code) {
                case KeyCode.S:
                    this.terrainGroup.setLayoutY(this.terrainGroup.getLayoutY() - this.tileColumns);
                    break;
                case KeyCode.W:
                    this.terrainGroup.setLayoutY(this.terrainGroup.getLayoutY() + this.tileColumns);
                    break;
                case KeyCode.A:
                    this.terrainGroup.setLayoutX(this.terrainGroup.getLayoutX() + this.tileRows);
                    break;
                case KeyCode.D:
                    this.terrainGroup.setLayoutX(this.terrainGroup.getLayoutX() - this.tileRows);
                    break;
                case KeyCode.X:
                    try (FileWriter writer = new FileWriter("src/main/resources/board_files/" + this.boardname + ".txt")) {
                        writer.write(this.rows + " " + this.columns + "\n");
                        for (int i = 0; i < this.rows; i++) {
                            for (int j = 0; j < this.columns; j++) {
                                    writer.write(this.terrainMatrix[i][j] + " ");
                            }
                            writer.write("\n");
                        }
                        System.out.println("Board file saved successfully");
                    } catch (IOException e) {
                        System.out.println("Error in saving board file");
                    }
                    break;
                default:
                    break;
            }
        });

        // setting the terrain scene to stage
        stage.setScene(this.terrainScene);
    }

    private void colorRectangle() {
        int firstRow = this.firstRow;
        int firstColumn = this.firstColumn;
        int secondRow = this.secondRow;
        int secondColumn = this.secondColumn;
        if (firstRow > secondRow) {
            int temp = firstRow;
            firstRow = secondRow;
            secondRow = temp;
        }
        if (firstColumn > secondColumn) {
            int temp = firstColumn;
            firstColumn = secondColumn;
            secondColumn = temp;
        }
        for (int i = firstRow; i <= secondRow; i++) {
            for (int j = firstColumn; j <= secondColumn; j++) {
                this.terrainButtons[i][j].setStyle("-fx-background-color: red;");
                this.terrainMatrix[i][j] = "e";
            }
        }
    }
}
