package com.patreon.frontend;

import com.patreon.frontend.models.EmailReward;
import com.patreon.frontend.utils.*;
import com.patreon.frontend.models.EarningEntry;
import com.patreon.frontend.models.PostEntry;
import com.patreon.frontend.models.SurveyEntry;
import com.patreon.frontend.models.UserEntry;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javafx.stage.Stage;

import java.io.File;
import java.util.*;

public class FrontendDriver extends Application {

    //private final ToolBar toolBar = new ToolBar();
    private final TabPane tabPane = new TabPane();
    private final VBox revenueChartBox = new VBox();
    private final VBox campaignChartBox = new VBox();
    private final VBox retentionChartBox = new VBox();
    private final VBox demographicChartBox = new VBox();

    private Stage window;

    private TableView<EarningEntry> earningTable = new TableView<>();
	private TableView<PostEntry> postTable = new TableView<>();
	private TableView<SurveyEntry> surveyTable = new TableView<>();
	private TableView<EmailReward> rewardsTable = new TableView<>();
	private TableView<UserEntry> userTable = new TableView<>();

	private ObservableList<EarningEntry> earningData = FXCollections.observableArrayList();
	private ObservableList<PostEntry> postData = FXCollections.observableArrayList(); 
	private ObservableList<SurveyEntry> surveyData = FXCollections.observableArrayList();
	private ObservableList<EmailReward> rewardList = FXCollections.observableArrayList();
	private ObservableList<UserEntry> userData = FXCollections.observableArrayList();
    
    private RewardController rc = new RewardController();
    private TableCreator tc = new TableCreator();
    private CSVParser cp = new CSVParser();
    private ChartCreator cc = new ChartCreator();
    private DatabaseServices ds = new DatabaseServices();


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        window.setTitle("Patreon Creator Toolkit");

        // Layout setup
        //toolBar.setOrientation(Orientation.VERTICAL);
        BorderPane layout = new BorderPane();
        layout.setTop(createMenuBar());
        //layout.setLeft(toolBar);
        layout.setCenter(tabPane);

        // Initialize tabs,tables, and toolbars
        initializeTabs();
        initializeTables();
        tabPane.getSelectionModel().select(0);
        //updateToolBar(tabPane.getTabs().get(0).getText());

        // Tab change listener to update toolbar
        /*tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                updateToolBar(newTab.getText());
            }
        });*/

        // Show scene
        Scene scene = new Scene(layout, 960, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/chart-styles.css").toExternalForm());
        window.setScene(scene);
        window.show();
    }

    // ----------------------------
    // Menu Bar
    // ----------------------------
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem menuOpen = new MenuItem("Open");
        MenuItem menuSave = new MenuItem("Save");
        Menu viewMenu = new Menu("View");
        Menu charts = new Menu("Charts");
        Menu dataFiles = new Menu("Data File");
        MenuItem emailRewards = new MenuItem("Email Rewards");

        MenuItem viewRevenue = new MenuItem("Revenue");
        MenuItem viewRetention = new MenuItem("Retention");
        MenuItem viewDemographics = new MenuItem("Demographics");
        MenuItem viewCampaign = new MenuItem("Campaign Activity");
        MenuItem viewPostFile = new MenuItem("Posts File");
        MenuItem viewEarningsFile = new MenuItem("Earnings File");
        MenuItem viewSurveyFile = new MenuItem("Surveys File");
        MenuItem viewUserFile = new MenuItem("User File");

        
        viewRevenue.setOnAction(e -> openTab("Revenue",revenueChartBox));
        viewRetention.setOnAction(e -> openTab("Retention",retentionChartBox));
        viewDemographics.setOnAction(e -> openTab("Demographics",demographicChartBox));
        viewCampaign.setOnAction(e -> openTab("Campaign Activity",campaignChartBox));
        viewPostFile.setOnAction(e -> openDataTab("Posts File",postTable));
        viewEarningsFile.setOnAction(e -> openDataTab("Earnings File",earningTable));
        viewSurveyFile.setOnAction(e -> openDataTab("Surveys File",surveyTable));
        viewUserFile.setOnAction(e -> openDataTab("User File", userTable));
        
        emailRewards.setOnAction(e -> openRewardsTab());

        //Open CSV Files
        menuOpen.setOnAction(e -> openFile());

        viewMenu.getItems().addAll(charts,dataFiles);
        charts.getItems().addAll(viewRevenue, viewRetention, viewDemographics, viewCampaign);
        dataFiles.getItems().addAll(viewPostFile, viewEarningsFile, viewSurveyFile, viewUserFile);
        fileMenu.getItems().addAll(menuOpen, viewMenu, menuSave, emailRewards);
        menuBar.getMenus().add(fileMenu);

        return menuBar;
    }

    // ----------------------------
    // Tab Initialization
    // ----------------------------
    private void initializeTabs() {

    	openTab("Revenue", revenueChartBox);
        openTab("Retention", retentionChartBox);
        openTab("Demographics", demographicChartBox);
        openTab("Campaign Activity", campaignChartBox);
    }

    private void openTab(String section, VBox chartBox) {
    	for (Tab tab : tabPane.getTabs()) {
            if (tab.getText().equals(section)) {
                tabPane.getSelectionModel().select(tab);
                return;
            }
        }

        Tab newTab = new Tab(section);
        newTab.setClosable(true);

        ScrollPane scrollPane = new ScrollPane(chartBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(10));

        newTab.setContent(scrollPane);
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);
    }
    
    private void openDataTab(String section, TableView<?> table) {
        for (Tab tab : tabPane.getTabs()) {
            if (tab.getText().equals(section)) {
                tabPane.getSelectionModel().select(tab);
                return;
            }
        }

        VBox tabContent = new VBox(10);
        tabContent.setPadding(new Insets(10));
        tabContent.getChildren().add(table);

        ScrollPane scrollPane = new ScrollPane(tabContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(10));

        Tab newTab = new Tab(section, scrollPane);
        newTab.setClosable(true);
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);
    }
    
	private void openRewardsTab() {
        for (Tab tab : tabPane.getTabs()) {
            if (tab.getText().equals("Email Rewards")) {
                tabPane.getSelectionModel().select(tab);
                return;
            }
        }

        // Left VBox with buttons
        VBox buttonPanel = new VBox(10);
        buttonPanel.setPadding(new Insets(10));
        buttonPanel.setAlignment(Pos.TOP_LEFT);
        buttonPanel.setMinWidth(100); // optional: fixed width

        Button newButton = new Button("New");
        Button deleteButton = new Button("Delete");

        newButton.setOnAction(e -> rc.newReward(rewardList));
        deleteButton.setOnAction(e -> rc.deleteSelectedReward(rewardsTable, rewardList));

        buttonPanel.getChildren().addAll(newButton, deleteButton);

        // Right VBox with rewardsTable, set to grow
        VBox tableContainer = new VBox(rewardsTable);
        tableContainer.setPadding(new Insets(10));
        tableContainer.setAlignment(Pos.TOP_LEFT);

        // Make rewardsTable grow to fill width
        rewardsTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(rewardsTable, Priority.ALWAYS);

        // HBox with buttons and table, make tableContainer grow
        HBox contentBox = new HBox(20, buttonPanel, tableContainer);
        contentBox.setPadding(new Insets(10));
        HBox.setHgrow(tableContainer, Priority.ALWAYS); // allow right side to grow

        // ScrollPane to wrap everything
        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPadding(new Insets(10));
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        Tab rewardsTab = new Tab("Email Rewards", scrollPane);
        rewardsTab.setClosable(true);
        tabPane.getTabs().add(rewardsTab);
        tabPane.getSelectionModel().select(rewardsTab);
    }
	
	private void buildCharts(String section) {
		
		switch(section) {
			case "Revenue":
				HBox monthlyYearlyEarnings = cc.createMonthlyYearlyEarnings(earningTable);
                revenueChartBox.getChildren().setAll(monthlyYearlyEarnings);
				break;
			case "Retention":
				break;
			case "Demographics":
				HBox genderDist = cc.createGenderDistributionChart(userData);
                HBox behavior = cc.createIncomeVsPledgeScatterChart(userData);
                HBox educationPie = cc.createEducationPieChart(userData);
                demographicChartBox.getChildren().setAll(genderDist, behavior, educationPie); 
				break;
			case "Campaign Activity":
				HBox postActivity = cc.createPostActivity(postData);
				HBox surveyPie = cc.createSurveyPieChart(surveyData);
                campaignChartBox.getChildren().setAll(postActivity, surveyPie);
				break;
		}
	}
 
    private void initializeTables() {
    	//CHANGE THIS LATER TO GRAB PREVIOUS DATA FROM DATABASE
    	tc.setupEarningTableColumns(earningTable);
    	ds.loadEarningsFromDB(earningTable, earningData);
    	earningTable.setItems(earningData);
    	
    	tc.setupPostTableColumns(postTable);
    	ds.loadPostFromDB(postTable, postData);
    	postTable.setItems(postData);
    	
    	tc.setupSurveyTableColumns(surveyTable);
    	ds.loadSurveyFromDB(surveyTable, surveyData);
    	surveyTable.setItems(surveyData);
    	
    	tc.setupUserTableColumns(userTable);
    	ds.loadUserFromDB(userTable, userData);
    	userTable.setItems(userData);
    	
    	tc.setupRewardsTableColumns(rewardsTable);
    	ds.loadRewardsFromDB(rewardsTable, rewardList);
    	rewardsTable.setItems(rewardList);
    	
    	buildCharts("Revenue");
    	buildCharts("Retention");
    	buildCharts("Demographics");
    	buildCharts("Campaign Activity");
    }
    
    private void openFile() {
    	try{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open CSV File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showOpenDialog(window);

            if (file != null) {
                List<String> options = Arrays.asList("Earnings", "Posts", "Surveys","User");
                ChoiceDialog<String> dialog = new ChoiceDialog<>("Earnings", options);
                dialog.setTitle("Select Data Type");
                dialog.setHeaderText("What type of data is this?");
                dialog.setContentText("Choose type:");

                Optional<String> result = dialog.showAndWait();

                
                if (result.isPresent()) {
                    String type = result.get();
                    Platform.runLater(() -> { 

                        switch (type) {
                            case "Earnings":
                                cp.parseEarningsCSV(file, earningTable, earningData);
                                buildCharts("Revenue");
                                break;

                            case "Posts":
                                cp.parsePostsCSV(file, postTable, postData);
                                buildCharts("Campaign Activity");
                                break;

                            case "Surveys":
                                cp.parseSurveysCSV(file, surveyTable, surveyData);
                                buildCharts("Campaign Activity");
                                break;

                            case "User":
                                cp.parseUserCSV(file, userTable, userData);
                                buildCharts("Demographics");
                                break;
                        }
                    });
                }

            }
        }catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error opening file: " + ex.getMessage());}
    }
    
    

}
