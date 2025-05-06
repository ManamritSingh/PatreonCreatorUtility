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
//import org.springframework.http.HttpRequest;

import java.io.File;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.SpringApplication;



public class FrontendDriver extends Application {

    //private final ToolBar toolBar = new ToolBar();
    private final TabPane tabPane = new TabPane();
    private final VBox revenueChartBox = new VBox();
    private final VBox campaignChartBox = new VBox();
    private final VBox retentionChartBox = new VBox();
    private final VBox demographicChartBox = new VBox();

    private Stage window;
    private ConfigurableApplicationContext context;


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
        //start spring in a new parallel thread
        Thread springThread = new Thread(() -> context = SpringApplication.run(com.patreon.backend.DemoApplication.class));
        springThread.setDaemon(true);
        springThread.start();

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

        window.setOnCloseRequest(e -> {
            if (context != null) {
                SpringApplication.exit(context, () -> 0);
            }
            Platform.exit();
            System.exit(0);
        });

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

        Menu devMenu = new Menu("API Data Options");

        MenuItem generateReal = new MenuItem("Fetch Real Data");
        MenuItem generateFake = new MenuItem("Generate Fake Data");
        MenuItem generateYearlyFake = new MenuItem("Generate Yearly Fake Data");

        generateReal.setOnAction(e -> sendGenerateRequest(false));
        generateFake.setOnAction(e -> sendGenerateRequest(true));
        generateYearlyFake.setOnAction(e -> sendGenerateYearlyRequest());

        devMenu.getItems().add(generateYearlyFake);
        devMenu.getItems().addAll(generateReal, generateFake);
        menuBar.getMenus().add(devMenu);



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

    // helper for switch

    private void sendGenerateRequest(boolean useMock) {
        try {
            String url = "http://localhost:8080/api/data/generate?mock=" + useMock;
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> System.out.println("✔ Backend: " + response.body()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendGenerateYearlyRequest() {
        try {
            String url = "http://localhost:8080/api/data/generate/yearly-fake";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> System.out.println("✔ Yearly Data: " + response.body()));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            case "Retention":
                VBox container = new VBox(10);
                container.setPadding(new Insets(10));

                Label intervalLabel = new Label("Select Interval:");
                ComboBox<String> intervalBox = new ComboBox<>();
                intervalBox.getItems().addAll("daily", "weekly", "monthly");
                intervalBox.setValue("monthly");

                Label tierLabel = new Label("Select Tiers:");
                HBox tierCheckboxes = new HBox(10);
                List<String> allTiers = ds.getAllTiers(true);
                List<CheckBox> checkBoxes = new ArrayList<>();
                for (String tier : allTiers) {
                    CheckBox cb = new CheckBox(tier);
                    cb.setSelected(true);
                    checkBoxes.add(cb);
                    tierCheckboxes.getChildren().add(cb);
                }

                Button updateButton = new Button("Update Retention/Avg Churn Charts");

                Label churnTierLabel = new Label("Select Tiers for Weekly Churn:");
                HBox churnTierCheckboxes = new HBox(10);
                List<CheckBox> churnCheckBoxes = new ArrayList<>();
                for (String tier : allTiers) {
                    CheckBox cb = new CheckBox(tier);
                    cb.setSelected(true);
                    churnCheckBoxes.add(cb);
                    churnTierCheckboxes.getChildren().add(cb);
                }
                Button churnUpdateButton = new Button("Update Weekly Churn Chart");

                // Default charts
                HBox chart1 = new HBox(cc.createRetentionLineChart("monthly", allTiers, true));
                HBox chart2 = new HBox(cc.createAvgChurnChart("monthly", allTiers, true));
                HBox chart3 = new HBox(cc.createWeeklyChurnChart(allTiers, true));

                // Controls for Avg Churn Chart (#2)
                Label avgChurnLabel = new Label("Select Tiers for Avg Churn:");
                HBox avgChurnCheckboxes = new HBox(10);
                List<CheckBox> avgChurnCheckBoxes = new ArrayList<>();
                for (String tier : allTiers) {
                    CheckBox cb = new CheckBox(tier);
                    cb.setSelected(true);
                    avgChurnCheckBoxes.add(cb);
                    avgChurnCheckboxes.getChildren().add(cb);
                }
                Button avgChurnUpdateButton = new Button("Update Avg Churn Chart");

                // Handler for Chart #2
                avgChurnUpdateButton.setOnAction(e -> {
                    List<String> selectedAvgChurnTiers = avgChurnCheckBoxes.stream()
                            .filter(CheckBox::isSelected)
                            .map(CheckBox::getText)
                            .toList();
                    chart2.getChildren().setAll(cc.createAvgChurnChart(intervalBox.getValue(), selectedAvgChurnTiers, true));
                });

                VBox chartsArea = new VBox(20,
                        chart1,
                        avgChurnLabel,
                        avgChurnCheckboxes,
                        avgChurnUpdateButton,
                        chart2
                );

                VBox bottomCharts = new VBox(20, chart3);

                // Button handlers
                updateButton.setOnAction(e -> {
                    String interval = intervalBox.getValue();
                    List<String> selectedTiers = checkBoxes.stream().filter(CheckBox::isSelected).map(CheckBox::getText).toList();
                    chart1.getChildren().setAll(cc.createRetentionLineChart(interval, selectedTiers, true));
                });


                churnUpdateButton.setOnAction(e -> {
                    List<String> selectedChurnTiers = churnCheckBoxes.stream().filter(CheckBox::isSelected).map(CheckBox::getText).toList();
                    chart3.getChildren().setAll(cc.createWeeklyChurnChart(selectedChurnTiers, true));
                });

                // Layout
                container.getChildren().addAll(
                        intervalLabel, intervalBox,
                        tierLabel, tierCheckboxes,
                        updateButton,
                        chartsArea,
                        churnTierLabel, churnTierCheckboxes,
                        churnUpdateButton,
                        bottomCharts
                );

                retentionChartBox.getChildren().setAll(container);
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
