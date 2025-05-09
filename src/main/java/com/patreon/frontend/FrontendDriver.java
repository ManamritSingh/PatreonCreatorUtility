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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javafx.stage.Stage;
//import org.springframework.http.HttpRequest;

import java.io.File;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
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
	private TableView<EmailReward> rewardsTable = new TableView<>();
	private TableView<UserEntry> userTable = new TableView<>();

	private ObservableList<EarningEntry> earningData = FXCollections.observableArrayList();
	private ObservableList<PostEntry> postData = FXCollections.observableArrayList(); ;
	private ObservableList<EmailReward> rewardList = FXCollections.observableArrayList();
	private ObservableList<UserEntry> userData = FXCollections.observableArrayList();
    
    private RewardController rc;
    private TableCreator tc = new TableCreator();
    private CSVParser cp;
    private ChartCreator cc = new ChartCreator();
    private DatabaseServices ds = new DatabaseServices();
    


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        //start spring in a new parallel thread
    	Thread springThread = new Thread(() -> {
            context = SpringApplication.run(com.patreon.backend.DemoApplication.class);
            // Once Spring is ready, get the RewardController bean
            rc = context.getBean(RewardController.class);
            cp = context.getBean(CSVParser.class);
        });
        springThread.setDaemon(true);
        springThread.start();

        window = primaryStage;
        window.setTitle("Patreon Creator Toolkit");

        // Layout setup
        BorderPane layout = new BorderPane();
        layout.setTop(createMenuBar());
        layout.setCenter(tabPane);

        // Initialize tabs,tables, and charts
        openChatbotTab();
        initializeTabs();
        initializeTables();
        tabPane.getSelectionModel().select(0);
        buildTabContent("Revenue");
    	buildTabContent("Retention");
    	buildTabContent("Demographics");
    	buildTabContent("Campaign Activity");
    	
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
        viewUserFile.setOnAction(e -> openDataTab("User File", userTable));
        
        emailRewards.setOnAction(e -> openRewardsTab());

        //Open CSV Files
        menuOpen.setOnAction(e -> openFile());

        viewMenu.getItems().addAll(charts,dataFiles);
        charts.getItems().addAll(viewRevenue, viewRetention, viewDemographics, viewCampaign);
        dataFiles.getItems().addAll(viewPostFile, viewEarningsFile, viewSurveyFile, viewUserFile);
        fileMenu.getItems().addAll(menuOpen, viewMenu, emailRewards);
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
        HBox mockPanel = new HBox(5);
        mockPanel.setPadding(new Insets(5));
	    mockPanel.setStyle("-fx-background-color: #f0f0f0;");
	    mockPanel.setAlignment(Pos.CENTER_LEFT);
	    mockPanel.setMaxWidth(Double.MAX_VALUE);
        
	    Label mockLabel = new Label("For demo, click buttom to mock API member calls:");
	    Button mockButton = new Button("Mock API call");
	    Region spacer = new Region();
	    HBox.setHgrow(spacer, Priority.ALWAYS);
	    HBox statusPanel = new HBox(5);
	    Label statusLabel = new Label();
	    statusLabel.setAlignment(Pos.CENTER_RIGHT);
	    statusPanel.getChildren().setAll(statusLabel);
	    statusPanel.setAlignment(Pos.CENTER_RIGHT);
	    
	    mockButton.setOnAction(e -> generateMockMember(statusLabel));
	    mockPanel.getChildren().setAll(mockLabel,spacer, mockButton);

        // Left VBox with buttons
        HBox buttonPanel = new HBox(10);
        buttonPanel.setPadding(new Insets(10));
        buttonPanel.setAlignment(Pos.TOP_LEFT);
        buttonPanel.setMinWidth(100); // optional: fixed width

        Button newButton = new Button("New");
        Button deleteButton = new Button("Delete");

        newButton.setOnAction(e -> rc.newReward(rewardList));
        deleteButton.setOnAction(e -> rc.deleteSelectedReward(rewardsTable, rewardList));

        buttonPanel.getChildren().addAll(newButton, deleteButton);

        // Right VBox with rewardsTable, set to grow
        VBox tableContainer = new VBox();
        tableContainer.getChildren().addAll(buttonPanel, rewardsTable);
        tableContainer.setPadding(new Insets(10));
        tableContainer.setAlignment(Pos.TOP_LEFT);
        rewardsTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(rewardsTable, Priority.ALWAYS);

        // ScrollPane to wrap everything
        VBox scrollPane = new VBox();
        scrollPane.getChildren().addAll(mockPanel, tableContainer, statusPanel);

        Tab rewardsTab = new Tab("Email Rewards", scrollPane);
        rewardsTab.setClosable(true);
        tabPane.getTabs().add(rewardsTab);
        tabPane.getSelectionModel().select(rewardsTab);
    }
	
	private void buildTabContent(String section) {
		List<String> allTiers = ds.getAllTiers(true);
		switch(section) {
			case "Revenue":
				HBox monthlyYearlyEarnings = cc.createMonthlyYearlyEarnings(earningTable);
				HBox netGrossChart = cc.createGrossVsNetChart(earningData);
                revenueChartBox.getChildren().setAll(monthlyYearlyEarnings, new Separator(), netGrossChart);
				break;
			case "Demographics":
				System.out.println("Entering Demographics");
				HBox genderDist = cc.createGenderDistributionChart(userData);
                HBox behavior = cc.createIncomeVsPledgeScatterChart(userData);
                HBox educationPie = cc.createEducationPieChart(userData);
                demographicChartBox.getChildren().setAll(genderDist, new Separator(), behavior, new Separator(), educationPie); 
				break;
			case "Campaign Activity":
				HBox postActivity = cc.createPostActivity(postData);
				//HBox surveyPie = cc.createSurveyPieChart(surveyData);
                campaignChartBox.getChildren().setAll(postActivity);
				break;
			case "Retention":
			    HBox dataGenBanner = new HBox(5);
			    dataGenBanner.setPadding(new Insets(5));
			    dataGenBanner.setStyle("-fx-background-color: #f0f0f0;");
			    dataGenBanner.setAlignment(Pos.CENTER_LEFT);
			    dataGenBanner.setMaxWidth(Double.MAX_VALUE);
			    
			    Region spacer = new Region();
			    HBox.setHgrow(spacer, Priority.ALWAYS);
			    
			    Label header = new Label("Due to lack of real data, please use generated fake data for chart demo:");
			    
			    Button realDataButton = new Button("Show Real Data");
			    Button fakeDataButton = new Button("Show Fake Data");
			    
			    // Track the data type (real or fake)
			    AtomicBoolean isMock = new AtomicBoolean(true);  // Default to fake data

			    // Chart containers
			    HBox chart1 = new HBox();
			    HBox chart2 = new HBox();
			    HBox chart3 = new HBox();
			    
			    sendGenerateYearlyRequest(); // Generate fake data
			    sendGenerateRequest(false); // Use real data

			    // Method to update all charts based on data type
			    Runnable updateCharts = () -> {
			        chart1.getChildren().setAll(cc.createRetentionLineChart("monthly", allTiers, isMock.get()));
			        chart2.getChildren().setAll(cc.createAvgChurnChart("monthly", allTiers, isMock.get()));
			        chart3.getChildren().setAll(cc.createWeeklyChurnChart(allTiers, isMock.get()));
			    };

			    // Button actions
			    realDataButton.setOnAction(e -> {
			        isMock.set(false);
			        updateCharts.run();
			        realDataButton.setStyle("-fx-font-weight: bold;");
			        fakeDataButton.setStyle("");
			    });

			    fakeDataButton.setOnAction(e -> {
			        isMock.set(true);
			        updateCharts.run();
			        fakeDataButton.setStyle("-fx-font-weight: bold;");
			        realDataButton.setStyle("");
			    });

			    // Initialize with fake data
			    updateCharts.run();
			    fakeDataButton.setStyle("-fx-font-weight: bold;"); // Start with fake data highlighted

			    dataGenBanner.getChildren().addAll(header, spacer, realDataButton, fakeDataButton);
			    retentionChartBox.getChildren().setAll(dataGenBanner, chart1, new Separator(), chart2, new Separator(), chart3);
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
    	
    	tc.setupUserTableColumns(userTable);
    	ds.loadUserFromDB(userTable, userData);
    	userTable.setItems(userData);
    	
    	tc.setupRewardsTableColumns(rewardsTable);
    	ds.loadRewardsFromDB(rewardsTable, rewardList);
    	rewardsTable.setItems(rewardList);
    }
    
    private void openFile() {
    	try{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open CSV File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showOpenDialog(window);

            if (file != null) {
                List<String> options = Arrays.asList("Earnings", "Posts","User");
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
                                buildTabContent("Revenue");
                                break;

                            case "Posts":
                                cp.parsePostsCSV(file, postTable, postData);
                                buildTabContent("Campaign Activity");
                                break;

                            case "User":
                                cp.parseUserCSV(file, userTable, userData);
                                buildTabContent("Demographics");
                                break;
                        }
                    });
                }

            }
        }catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error opening file: " + ex.getMessage());}
    }
    
    private void openChatbotTab() {
        for (Tab tab : tabPane.getTabs()) {
            if (tab.getText().equals("Chatbot")) {
                tabPane.getSelectionModel().select(tab);
                return;
            }
        }

        String sessionId = UUID.randomUUID().toString();
        VBox chatbotBox = new VBox(10);
        chatbotBox.setStyle("-fx-background-color: lightgray; -fx-padding: 10;");
        chatbotBox.setPrefWidth(400);

        // Chat history scroll area
        ScrollPane scrollPane = new ScrollPane();
        VBox chatHistory = new VBox(5);
        chatHistory.setFillWidth(true);
        scrollPane.setContent(chatHistory);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        VBox.setVgrow(scrollPane, Priority.ALWAYS); // expand to fill available space

        // User input + send
        TextField userInput = new TextField();
        userInput.setPromptText("Type your message...");

        Button sendButton = new Button("Send");
        sendButton.setDefaultButton(true); // Triggers on Enter

        sendButton.setOnAction(event -> sendMessage(userInput, chatHistory, scrollPane, sessionId));
        userInput.setOnAction(event -> sendMessage(userInput, chatHistory, scrollPane, sessionId)); // Enter key

        // Input row (grow text, button on right)
        HBox inputBox = new HBox(10);
        inputBox.setStyle("-fx-padding: 10;");
        inputBox.getChildren().addAll(userInput, sendButton);
        HBox.setHgrow(userInput, Priority.ALWAYS);

        chatbotBox.getChildren().addAll(scrollPane, inputBox);

        Tab chatbotTab = new Tab("Chatbot");
        chatbotTab.setContent(chatbotBox);
        chatbotTab.setClosable(false);
        tabPane.getTabs().add(chatbotTab);
        tabPane.getSelectionModel().select(chatbotTab);
    }
    private void sendMessage(TextField userInput, VBox chatHistory, ScrollPane scrollPane, String sessionId) {
        String userMessage = userInput.getText().trim();
        if (!userMessage.isEmpty()) {
            chatHistory.getChildren().add(createUserMessage(userMessage));

            // Add loading spinner and remember its reference
            HBox loading = createLoadingBubble();
            Platform.runLater(() -> chatHistory.getChildren().add(loading));

            userInput.clear();

            new Thread(() -> {
                try {
                    String safeInput = userMessage.replace("\"", "\\\"").replace("\n", "\\n");
                    String json = """
                {
                    "sessionId": "%s",
                    "userInput": "%s"
                }
                """.formatted(sessionId, safeInput);

                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/api/chat"))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(json))
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    String botReply = response.body();

                    Platform.runLater(() -> {
                        chatHistory.getChildren().remove(loading); // Remove spinner
                        chatHistory.getChildren().add(createChatbotResponse(botReply));

                        scrollPane.layout(); // ensures scrollPane is updated
                        scrollPane.setVvalue(1.0); // scroll to bottom
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    chatHistory.getChildren().remove(loading);
                    Platform.runLater(() -> chatHistory.getChildren().add(createChatbotResponse("⚠️ Error contacting AI")));
                }
            }).start();
        }
    }
    private HBox createLoadingBubble() {
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(18, 18);

        Label loadingLabel = new Label("Thinking...");
        loadingLabel.setStyle("-fx-text-fill: #555555; -fx-font-style: italic;");

        HBox loadingBubble = new HBox(5, spinner, loadingLabel);
        loadingBubble.setPadding(new Insets(10));
        loadingBubble.setStyle("-fx-background-color: #eeeeee; -fx-background-radius: 20px;");
        loadingBubble.setMaxWidth(200);
        loadingBubble.setAlignment(Pos.CENTER_LEFT);

        HBox container = new HBox(loadingBubble);
        container.setAlignment(Pos.BASELINE_LEFT);
        return container;
    }




    // Helper method to create the user message bubble
    private HBox createUserMessage(String message) {
        Label userMessage = new Label(message);
        userMessage.setStyle("-fx-background-color: #5dadec; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 20px;");
        userMessage.setMaxWidth(200);
        userMessage.setWrapText(true);
        HBox userBubble = new HBox(userMessage);
        userBubble.setAlignment(Pos.BASELINE_RIGHT);
        return userBubble;
    }

    // Helper method to create the chatbot response bubble
    private HBox createChatbotResponse(String response) {
        Label chatbotMessage = new Label(response);
        chatbotMessage.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: black; -fx-padding: 10; -fx-background-radius: 20px;");
        chatbotMessage.setMaxWidth(200);
        chatbotMessage.setWrapText(true);
        HBox chatbotBubble = new HBox(chatbotMessage);
        chatbotBubble.setAlignment(Pos.BASELINE_LEFT);
        return chatbotBubble;
    }

    // Helper method to get the name of the selected tab
    private String getSelectedTabName() {
        // In a real application, you'd check which tab is selected
        return "Revenue";  // Return a dummy value for now
    }
    
    private void generateMockMember(Label statusLabel) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/generate-mock-member"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> {
                    statusLabel.setText("Mock member generated successfully!");
                    statusLabel.setStyle("-fx-text-fill: green;");
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        statusLabel.setText("Failed to generate mock member.");
                        statusLabel.setStyle("-fx-text-fill: red;");
                    });
                    ex.printStackTrace();
                    return null;
                });
        } catch (Exception ex) {
            ex.printStackTrace();
            Platform.runLater(() -> {
                statusLabel.setText("Failed to trigger mock member API.");
                statusLabel.setStyle("-fx-text-fill: red;");
            });
        }
    }


}
