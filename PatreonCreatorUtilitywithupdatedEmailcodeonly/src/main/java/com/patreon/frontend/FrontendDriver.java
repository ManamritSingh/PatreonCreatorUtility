package com.patreon.frontend;

import com.patreon.frontend.models.EmailReward;
import com.patreon.frontend.models.EarningEntry;
import com.patreon.frontend.models.PostEntry;
import com.patreon.frontend.models.SurveyEntry;
import com.patreon.frontend.models.UserEntry;
import com.patreon.utils.DatabaseConnection;
import com.patreon.utils.DatabaseUtils;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
	
    //Earnings Graphs
    private XYChart.Series<String, Number> monthlyEarningsSeries = new XYChart.Series<>();
    private XYChart.Series<String, Number> yearlyEarningsSeries = new XYChart.Series<>();
    private LineChart<String, Number> monthlyEarningsChart;
    private LineChart<String, Number> yearlyEarningsChart;

    //Posts Graphs
    private StackedBarChart<String, Number> postSBC;


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

        newButton.setOnAction(e -> newReward());
        deleteButton.setOnAction(e -> deleteSelectedReward());

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
 
    private void initializeTables() {
    	//CHANGE THIS LATER TO GRAB PREVIOUS DATA FROM DATABASE
    	setupEarningTableColumns();
    	loadEarningsFromDB();
    	earningTable.setItems(earningData);
    	
    	setupPostTableColumns();
    	loadPostFromDB();
    	postTable.setItems(postData);
    	
    	setupSurveyTableColumns();
    	loadSurveyFromDB();
    	surveyTable.setItems(surveyData);
    	
    	setupUserTableColumns();
    	loadUserFromDB();
    	userTable.setItems(userData);
    	
    	setupRewardsTableColumns();
    	loadRewardsFromDB();
    	rewardsTable.setItems(rewardList);
    }
    
    private void newReward() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("New Email Reward");

        // Trigger dropdown
        Label triggerLabel = new Label("Trigger:");
        ComboBox<String> triggerCombo = new ComboBox<>();
        triggerCombo.getItems().addAll(
                "Send Now", "New Subscriber", "Upgraded Tier", "Survey Completion", "Yearly Anniversary", "Unsubscribed"
        );
        triggerCombo.setValue("Send Now");

        // Recipient checkboxes
        Label recipientLabel = new Label("Recipient:");
        CheckBox everyone = new CheckBox("Everyone");
        CheckBox tier1 = new CheckBox("Tier 1");
        CheckBox tier2 = new CheckBox("Tier 2");
        CheckBox tier3 = new CheckBox("Tier 3");

        HBox recipientBox = new HBox(10, everyone, tier1, tier2, tier3);
        recipientBox.setAlignment(Pos.CENTER_LEFT);

        // Subject input
        Label subjectLabel = new Label("Subject:");
        TextField subjectField = new TextField();
        subjectField.setPromptText("Enter subject");

        // Email Message input
        Label messageLabel = new Label("Email Message:");
        TextArea messageArea = new TextArea();
        messageArea.setPrefRowCount(6);
        messageArea.setWrapText(true);
        messageArea.setPromptText("Enter reward email message");
        
        Tooltip tooltip = new Tooltip("Use {FIRST_NAME} and {LAST_NAME} to personalize emails.");
        Tooltip.install(messageArea, tooltip);


        // Done button
        Button doneButton = new Button("Done");
        doneButton.setOnAction(e -> {
            String trigger = triggerCombo.getValue();
            List<String> recipients = new ArrayList<>();
            if (everyone.isSelected()) recipients.add("Everyone");
            if (tier1.isSelected()) recipients.add("Tier 1");
            if (tier2.isSelected()) recipients.add("Tier 2");
            if (tier3.isSelected()) recipients.add("Tier 3");
            

            String subject = subjectField.getText();
            SimpleStringProperty subjectText = new SimpleStringProperty(subject);
            String message = messageArea.getText();
            SimpleStringProperty messageText = new SimpleStringProperty(message);
            SimpleStringProperty triggerText = new SimpleStringProperty(trigger);
            
            if (subject.isBlank() || recipients.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a subject and select at least one recipient.", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            EmailReward reward = new EmailReward(messageText, subjectText, triggerText, recipients);
            rewardList.add(reward);
            try (Connection conn = DatabaseConnection.getConnection()) {
                DatabaseUtils.saveRewardToDatabase(conn, reward);
                System.out.println("Reward saved successfully.");
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            
            // Simulate saving logic
            System.out.println("=== Email Trigger Saved ===");
            System.out.println("Trigger: " + trigger);
            System.out.println("Recipients: " + recipients);
            System.out.println("Subject: " + subject);
            System.out.println("Message: " + message);
            System.out.println("===========================");

            popupStage.close();
        });
        
        //Cancel button
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> popupStage.close());
   
        VBox layout = new VBox(10,
                new HBox(10, triggerLabel, triggerCombo),
                new VBox(5, recipientLabel, recipientBox),
                new VBox(5, subjectLabel, subjectField),
                new VBox(5, messageLabel, messageArea),
                new HBox(5,cancelButton, doneButton)
        );
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.TOP_LEFT);

        popupStage.setScene(new Scene(layout, 450, 400));
        popupStage.showAndWait();
    }
    
    @SuppressWarnings("unchecked")
	private void setupRewardsTableColumns() {
    	TableColumn<EmailReward, String> triggerCol = new TableColumn<>("Trigger");
    	triggerCol.setCellValueFactory(cellData -> cellData.getValue().getTriggerOpt());
    	
    	TableColumn<EmailReward, String> recipientsCol = new TableColumn<>("Recipients");
    	recipientsCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.join(", ", cellData.getValue().getRecepients())));
    	
    	TableColumn<EmailReward, String> subjectCol = new TableColumn<>("Email Subject");
    	subjectCol.setCellValueFactory(cellData -> cellData.getValue().getSubject());
    	
    	TableColumn<EmailReward, String> messageCol = new TableColumn<>("Email Message");
    	messageCol.setCellValueFactory(cellData -> cellData.getValue().getMessage());
    	
    	rewardsTable.getColumns().addAll(triggerCol, recipientsCol, subjectCol, messageCol);
    }
    
    private void deleteSelectedReward() {
        EmailReward selected = rewardsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
                                      "Are you sure you want to delete this reward?", 
                                      ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText("Confirm Delete");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                rewardList.remove(selected);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, 
                                    "Please select a reward to delete.", 
                                    ButtonType.OK);
            alert.setHeaderText("No Selection");
            alert.showAndWait();
        }
    }

    // ----------------------------
    // Toolbar Logic
    // ----------------------------
    /*private void updateToolBar(String section) {
        toolBar.getItems().clear();

        switch (section) {
            case "Revenue":
                RadioButton monthlyButton = new RadioButton("Monthly");
                RadioButton yearlyButton = new RadioButton("Yearly");
                ToggleGroup viewToggle = new ToggleGroup();
                monthlyButton.setToggleGroup(viewToggle);
                yearlyButton.setToggleGroup(viewToggle);
                monthlyButton.setSelected(true); // Default view is monthly

                HBox toggleBox = new HBox(10, monthlyButton, yearlyButton);

                // Add listener to the toggle group
                viewToggle.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
                    if (newToggle == monthlyButton) {
                        revenueChartBox.getChildren().setAll(monthlyEarningsChart);
                    } else if (newToggle == yearlyButton) {
                        revenueChartBox.getChildren().setAll(yearlyEarningsChart);
                    }
                });

                toolBar.getItems().add(toggleBox);
                break;

            case "Retention":
                toolBar.getItems().addAll(
                        new Button("Monthly"),
                        new Button("Yearly"),
                        new Button("All"),
                        new Button("By Tier")
                );
                break;
            case "Demographics":
                toolBar.getItems().addAll(
                        new Button("Country"),
                        new Button("State")
                );
                break;
            case "Campaign Activity":
                CheckBox showImpressions = new CheckBox("Impressions");
                CheckBox showLikes = new CheckBox("Likes");
                CheckBox showComments = new CheckBox("Comments");
                CheckBox showFreeUsers = new CheckBox("New Free Users");
                CheckBox showPaidUsers = new CheckBox("New Paid Users");

                VBox checkBoxPanel = new VBox(10, showImpressions, showLikes, showComments, showFreeUsers, showPaidUsers);

                showImpressions.setOnAction(e -> updatePostChart(showImpressions, showLikes, showComments,showFreeUsers, showPaidUsers));
                showLikes.setOnAction(e -> updatePostChart(showImpressions, showLikes, showComments,showFreeUsers, showPaidUsers));
                showComments.setOnAction(e -> updatePostChart(showImpressions, showLikes, showComments,showFreeUsers, showPaidUsers));
                showFreeUsers.setOnAction(e -> updatePostChart(showImpressions, showLikes, showComments,showFreeUsers, showPaidUsers));
                showPaidUsers.setOnAction(e -> updatePostChart(showImpressions, showLikes, showComments,showFreeUsers, showPaidUsers));

                toolBar.getItems().add(checkBoxPanel);
                break;
            case "Email Rewards":
            	Button newRewardBtn = new Button("New");
            	Button deleteRewardBtn = new Button("Delete");
            	
            	newRewardBtn.setOnAction(e -> newReward());
            	deleteRewardBtn.setOnAction(e -> deleteSelectedReward());
            	
            	toolBar.getItems().addAll(newRewardBtn, deleteRewardBtn);
            	break;
        }
    }*/
    
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
                        VBox updatedCampaignBox = new VBox(20);  // For stacking post and survey charts
                        updatedCampaignBox.setPadding(new Insets(10));

                        switch (type) {
                            case "Earnings":
                                parseEarningsCSV(file);
                                HBox monthlyYearlyEarnings = createMonthlyYearlyEarnings();
                                revenueChartBox.getChildren().setAll(monthlyYearlyEarnings); // Add to revenue chart box
                                break;

                            case "Posts":
                                parsePostsCSV(file);
                                HBox postActivity = createPostActivity();
                                postActivity.setId("postChart");

                                // Remove the old post chart if it exists and add the new one
                                updatedCampaignBox.getChildren().clear(); // Clear out the old contents
                                
                                // Add post chart to the top of the updatedCampaignBox
                                updatedCampaignBox.getChildren().add(postActivity);

                                // Re-add all existing charts (Survey and others) that are not the post chart
                                List<Node> existingNodes = new ArrayList<>(campaignChartBox.getChildren());
                                for (Node node : existingNodes) {
                                    if (!(node instanceof HBox) || !"postChart".equals(node.getId())) {
                                        updatedCampaignBox.getChildren().add(node);
                                    }
                                }

                                // Replace campaignChartBox contents with the updated stack (post + existing charts)
                                campaignChartBox.getChildren().setAll(updatedCampaignBox.getChildren());
                                break;

                            case "Surveys":
                                parseSurveysCSV(file);
                                HBox surveyPie = createSurveyPieChart();
                                surveyPie.setId("surveyChart"); // Tag this chart

                                // Ensure survey chart goes to the bottom of the updated stack
                                updatedCampaignBox.getChildren().add(surveyPie);

                                // Re-add all existing charts (Post and others) that are not the survey chart
                                List<Node> existingNodesSur = new ArrayList<>(campaignChartBox.getChildren());
                                for (Node node : existingNodesSur) {
                                    if (!(node instanceof HBox) || !"surveyChart".equals(node.getId())) {
                                        updatedCampaignBox.getChildren().add(node);
                                    }
                                }

                                // Replace campaignChartBox contents with the updated stack (post + survey + others)
                                campaignChartBox.getChildren().setAll(updatedCampaignBox.getChildren());
                                break;

                            case "User":
                                parseUserCSV(file);
                                HBox genderDist = createGenderDistributionChart();
                                HBox behavior = createIncomeVsPledgeScatterChart();
                                HBox educationPie = createEducationPieChart();
                                demographicChartBox.getChildren().setAll(genderDist, behavior, educationPie); 
                                break;
                        }
                    });
                }

            }
        }catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error opening file: " + ex.getMessage());}
    }

    // ----------------------------
    // Parsing Files
    // ----------------------------

    private void parseEarningsCSV(File file){
    	earningData.clear();
        
    	try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String header = reader.readLine();

            if (header == null) {
                showAlert("Error", "The file is empty.");
            }

            // Expected column headers (first few are enough to identify it as an earnings file)
            String[] expectedHeaders = {
                    "Month","Total","Membership charges - web","Membership charges - iOS app",
                    "Gift charges - web","Gift charges - iOS app","Your total earnings","Processing fee",
                    "Patreon fee","iOS App Store fee","Merch items and shipping","Declines","Your earnings - membership(%)",
                    "Processing fees - membership(%)","Patreon fee - membership(%)","Your earnings - gift(%)",
                    "Processing fees - gift(%)","Patreon fee - gift(%)","Currency conversion fee",
                    "Currency conversion fee (%)","Currency"
            };

            // Normalize and split header (handle comma or tab)
            String[] actualHeaders = header.toLowerCase().split("\t|,");

            for (String expected : expectedHeaders) {
                boolean found = Arrays.stream(actualHeaders)
                        .anyMatch(h -> h.trim().contains(expected.toLowerCase()));
                if (!found) {
                    showAlert("Invalid File", "This doesn't appear to be an earnings CSV.");
                }
            }

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\t|,"); // handles tab- or comma-separated

                String monthYear = tokens[0].trim();
                EarningEntry entry = new EarningEntry(
                        monthYear,
                        new SimpleDoubleProperty(Double.parseDouble(tokens[1])),
                        new SimpleDoubleProperty(Double.parseDouble(tokens[2])),
                        new SimpleDoubleProperty(Double.parseDouble(tokens[3])),
                        new SimpleDoubleProperty(Double.parseDouble(tokens[4])),
                        new SimpleDoubleProperty(Double.parseDouble(tokens[5])),
                        new SimpleDoubleProperty(Double.parseDouble(tokens[6])),
                        new SimpleDoubleProperty(Double.parseDouble(tokens[7])),
                        new SimpleDoubleProperty(Double.parseDouble(tokens[8])),
                        new SimpleDoubleProperty(Double.parseDouble(tokens[9])),
                        new SimpleDoubleProperty(Double.parseDouble(tokens[10])),
                        new SimpleDoubleProperty(Double.parseDouble(tokens[11])),
                        new SimpleDoubleProperty(parsePercent(tokens[12])),
                        new SimpleDoubleProperty(parsePercent(tokens[13])),
                        new SimpleDoubleProperty(parsePercent(tokens[14])),
                        new SimpleDoubleProperty(parsePercent(tokens[15])),
                        new SimpleDoubleProperty(parsePercent(tokens[16])),
                        new SimpleDoubleProperty(parsePercent(tokens[17])),
                        new SimpleDoubleProperty(Double.parseDouble(tokens[18])),
                        new SimpleDoubleProperty(parsePercent(tokens[19])),
                        new SimpleStringProperty(tokens[20].trim())
                );

                earningData.add(entry);
            }
            earningTable.setItems(earningData);
            try (Connection conn = DatabaseConnection.getConnection()) {
                DatabaseUtils.saveEarningsToDatabase(conn, earningData);
                System.out.println("Earnings data saved successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("File Error", "Could not read the file.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Parsing Error", "There was an error while parsing the CSV.");
        }
    }

    private double parsePercent(String percentString) {
        return Double.parseDouble(percentString.replace("%", "").trim());
    }

    @SuppressWarnings("unchecked")
    private void setupEarningTableColumns() {
        TableColumn<EarningEntry, String> monthCol = new TableColumn<>("Month");
        monthCol.setCellValueFactory(cellData -> cellData.getValue().getMonth());

        TableColumn<EarningEntry, Number> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(cellData -> cellData.getValue().getYear());

        TableColumn<EarningEntry, Number> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(cellData -> cellData.getValue().getTotal());

        TableColumn<EarningEntry, Number> webMemCharCol = new TableColumn<>("Membership Charges - Web");
        webMemCharCol.setCellValueFactory(cellData -> cellData.getValue().getWebMembershipCharges());

        TableColumn<EarningEntry, Number> iosMemCharCol = new TableColumn<>("Membership Charges - iOS");
        iosMemCharCol.setCellValueFactory(cellData -> cellData.getValue().getiOSMembershipCharges());

        TableColumn<EarningEntry, Number> webGiftCharCol = new TableColumn<>("Gift Charges - Web");
        webGiftCharCol.setCellValueFactory(cellData -> cellData.getValue().getWebGiftCharges());

        TableColumn<EarningEntry, Number> earningsCol = new TableColumn<>("Earnings");
        earningsCol.setCellValueFactory(cellData -> cellData.getValue().getEarnings());

        TableColumn<EarningEntry, Number> processFeeCol = new TableColumn<>("Processing Fees");
        processFeeCol.setCellValueFactory(cellData -> cellData.getValue().getProcessingFee());

        TableColumn<EarningEntry, Number> patreonFeeCol = new TableColumn<>("Patreon Fees");
        patreonFeeCol.setCellValueFactory(cellData -> cellData.getValue().getPatreonFee());

        TableColumn<EarningEntry, Number> iosFeeCol = new TableColumn<>("iOS Fees");
        iosFeeCol.setCellValueFactory(cellData -> cellData.getValue().getiOSFee());

        TableColumn<EarningEntry, Number> merchShipCol = new TableColumn<>("Merch Shipping Fees");
        merchShipCol.setCellValueFactory(cellData -> cellData.getValue().getMerchShipping());

        TableColumn<EarningEntry, Number> declinesCol = new TableColumn<>("Declines");
        declinesCol.setCellValueFactory(cellData -> cellData.getValue().getDeclines());

        TableColumn<EarningEntry, Number> perMemEarnCol = new TableColumn<>("Membership Earnings(%)");
        perMemEarnCol.setCellValueFactory(cellData -> cellData.getValue().getPercentMembershipEarnings());

        TableColumn<EarningEntry, Number> perMemProcFeeCol = new TableColumn<>("Membership Processing Fees(%)");
        perMemProcFeeCol.setCellValueFactory(cellData -> cellData.getValue().getPercentMembershipProcessingFees());

        TableColumn<EarningEntry, Number> perMemPatFeeCol = new TableColumn<>("Membership Patreon Fees(%)");
        perMemPatFeeCol.setCellValueFactory(cellData -> cellData.getValue().getPercentMembershipPatreonFees());

        TableColumn<EarningEntry, Number> perGiftEarnCol = new TableColumn<>("Gift Earnings(%)");
        perGiftEarnCol.setCellValueFactory(cellData -> cellData.getValue().getPercentGiftEarnings());

        TableColumn<EarningEntry, Number> perGiftProcFeeCol = new TableColumn<>("Gift Processing Fees(%)");
        perGiftProcFeeCol.setCellValueFactory(cellData -> cellData.getValue().getPercentGiftProcessingFees());

        TableColumn<EarningEntry, Number> perGiftPatFeeCol = new TableColumn<>("Gift Patreon Fees(%)");
        perGiftPatFeeCol.setCellValueFactory(cellData -> cellData.getValue().getPercentGiftPatreonFees());

        TableColumn<EarningEntry, Number> currConvFeeCol = new TableColumn<>("Currency Conversion Fees");
        currConvFeeCol.setCellValueFactory(cellData -> cellData.getValue().getCurrencyConversionFee());

        TableColumn<EarningEntry, Number> currConcFeePerCol = new TableColumn<>("Currency Conversion Fees(%)");
        currConcFeePerCol.setCellValueFactory(cellData -> cellData.getValue().getCurrencyConversionFeePercent());

        TableColumn<EarningEntry, String> currencyCol = new TableColumn<>("Currency");
        currencyCol.setCellValueFactory(cellData -> cellData.getValue().getCurrency());

        ObservableList<TableColumn<EarningEntry, ?>> columns = FXCollections.observableArrayList();
        columns.addAll(monthCol, yearCol, totalCol, webMemCharCol, iosMemCharCol, webGiftCharCol,
                earningsCol, processFeeCol, patreonFeeCol, iosFeeCol, merchShipCol, declinesCol,
                perMemEarnCol, perMemProcFeeCol, perMemPatFeeCol, perGiftEarnCol, perGiftProcFeeCol,
                perGiftPatFeeCol, currConvFeeCol, currConcFeePerCol, currencyCol);

        earningTable.getColumns().addAll(columns);

    }

    private void parsePostsCSV(File file){
    	try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String header = reader.readLine();

            if (header == null) {
                showAlert("Error", "The file is empty.");
            }

            // Expected column headers (first few are enough to identify it as an earnings file)
            String[] expectedHeaders = {
                    "Post Title","Total Impressions","Likes","Comments","New Free Members",
                    "New Paid Members","Publish Date (UTC)","Link To Post"
            };

            // Normalize and split header (handle comma or tab)
            String[] actualHeaders = header.split("\t|,");

            for (String expected : expectedHeaders) {
                boolean found = Arrays.stream(actualHeaders)
                        .anyMatch(h -> h.trim().equalsIgnoreCase(expected));
                if (!found) {
                    showAlert("Invalid File", "This doesn't appear to be a posts CSV.");
                }
            }


            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\t|,"); // handles tab- or comma-separated

                PostEntry entry = new PostEntry(
                        new SimpleStringProperty(tokens[0].trim()),
                        new SimpleIntegerProperty(Integer.parseInt(tokens[1])),
                        new SimpleIntegerProperty(Integer.parseInt(tokens[2])),
                        new SimpleIntegerProperty(Integer.parseInt(tokens[3])),
                        new SimpleIntegerProperty(Integer.parseInt(tokens[4])),
                        new SimpleIntegerProperty(Integer.parseInt(tokens[5])),
                        new SimpleStringProperty(tokens[6].trim()),
                        new SimpleStringProperty(tokens[7].trim())
                );
                postData.add(entry);
            }

            postTable.setItems(postData);
            try (Connection conn = DatabaseConnection.getConnection()) {
                DatabaseUtils.savePostToDatabase(conn, postData);
                System.out.println("Post data saved successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("File Error", "Could not read the file.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Parsing Error", "There was an error while parsing the CSV.");
        }
    }

    @SuppressWarnings("unchecked")
    private void setupPostTableColumns() {
        TableColumn<PostEntry, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(cellData -> cellData.getValue().getTitle());

        TableColumn<PostEntry, Number> impressionsCol = new TableColumn<>("Total Impressions");
        impressionsCol.setCellValueFactory(cellData -> cellData.getValue().getTotalImpressions());

        TableColumn<PostEntry, Number> likesCol = new TableColumn<>("Likes");
        likesCol.setCellValueFactory(cellData -> cellData.getValue().getLikes());

        TableColumn<PostEntry, Number> commentsCol = new TableColumn<>("Comments");
        commentsCol.setCellValueFactory(cellData -> cellData.getValue().getComments());

        TableColumn<PostEntry, Number> newFreeMemCol = new TableColumn<>("New Free Members");
        newFreeMemCol.setCellValueFactory(cellData -> cellData.getValue().getNewFreeMembers());

        TableColumn<PostEntry, Number> newPaidMemCol = new TableColumn<>("New Paid Members");
        newPaidMemCol.setCellValueFactory(cellData -> cellData.getValue().getNewPaidMembers());

        TableColumn<PostEntry, String> pubDateTimeCol = new TableColumn<>("Published Date");
        pubDateTimeCol.setCellValueFactory(cellData -> cellData.getValue().getPublishedDateTime());

        TableColumn<PostEntry, String> linkCol = new TableColumn<>("Link");
        linkCol.setCellValueFactory(cellData -> cellData.getValue().getLink());

        postTable.getColumns().addAll(titleCol, impressionsCol, likesCol, commentsCol, newFreeMemCol, newPaidMemCol,
                pubDateTimeCol, linkCol);
    }

    private void parseSurveysCSV(File file){
    	try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String header = reader.readLine();

            if (header == null) {
                showAlert("Error", "The file is empty.");
            }

            // Expected column headers (first few are enough to identify it as an earnings file)
            String[] expectedHeaders = {
                    "Submitted At (UTC)","Name","Email","Tier","Survey Choice","Additional Comment"
            };

            // Normalize and split header (handle comma or tab)
            String[] actualHeaders = header.toLowerCase().split(",",-1);

            for (String expected : expectedHeaders) {
                boolean found = Arrays.stream(actualHeaders)
                        .anyMatch(h -> h.trim().contains(expected.toLowerCase()));
                if (!found) {
                    showAlert("Invalid File", "This doesn't appear to be a surveys CSV.");
                }
            }


            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",",-1); 
                SurveyEntry entry = new SurveyEntry(
                        new SimpleStringProperty(tokens[0].trim()),
                        new SimpleStringProperty(tokens[1].trim()),
                        new SimpleStringProperty(tokens[2].trim()),
                        new SimpleStringProperty(tokens[3].trim()),
                        new SimpleStringProperty(tokens[4].trim()),
                        new SimpleStringProperty(tokens[5].trim())
                );
                surveyData.add(entry);
            }

            surveyTable.setItems(surveyData);
            try (Connection conn = DatabaseConnection.getConnection()) {
                DatabaseUtils.saveSurveyToDatabase(conn, surveyData);
                System.out.println("Survey data saved successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("File Error", "Could not read the file.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Parsing Error", "There was an error while parsing the CSV.");
        }
    }

    @SuppressWarnings("unchecked")
    private void setupSurveyTableColumns() {
        TableColumn<SurveyEntry, String> submittedDateTimeCol = new TableColumn<>("Submitted Date");
        submittedDateTimeCol.setCellValueFactory(cellData -> cellData.getValue().getSubmittedDateTime());

        TableColumn<SurveyEntry, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().getName());

        TableColumn<SurveyEntry, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> cellData.getValue().getEmail());

        TableColumn<SurveyEntry, String> tierCol = new TableColumn<>("Tier");
        tierCol.setCellValueFactory(cellData -> cellData.getValue().getTier());

        TableColumn<SurveyEntry, String> surveyCol = new TableColumn<>("Survey Type");
        surveyCol.setCellValueFactory(cellData -> cellData.getValue().getSurvey());

        TableColumn<SurveyEntry, String> commentsCol = new TableColumn<>("Comments");
        commentsCol.setCellValueFactory(cellData -> cellData.getValue().getComments());

        surveyTable.getColumns().addAll(submittedDateTimeCol, nameCol, emailCol, tierCol, surveyCol, commentsCol);
    }
    
    private void parseUserCSV(File file) {
    	userData.clear();
        
    	try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String header = reader.readLine();

            if (header == null) {
                showAlert("Error", "The file is empty.");
            }

            // Expected column headers (first few are enough to identify it as an earnings file)
            String[] expectedHeaders = {
            		"User ID","First Name","Last Name","Email","Active?","Tier",
            		"Pledge ($)","Address Name","Address Line 1","Address Line 2",
            		"City","State","ZIP Code","Country","Gender","Age Range","Education Level",
            		"Income Range","Raffle Eligible" 																			
            };

            // Normalize and split header (handle comma or tab)
            String[] actualHeaders = header.toLowerCase().split("\t|,");

            for (String expected : expectedHeaders) {
                boolean found = Arrays.stream(actualHeaders)
                        .anyMatch(h -> h.trim().contains(expected.toLowerCase()));
                if (!found) {
                    showAlert("Invalid File", "This doesn't appear to be a User CSV.");
                }
            }

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\t|,"); // handles tab- or comma-separated

               
                UserEntry entry = new UserEntry(
                        new SimpleIntegerProperty(),
                        new SimpleStringProperty(tokens[1].trim()),
                        new SimpleStringProperty(tokens[2].trim()),
                        new SimpleStringProperty(tokens[3].trim()),
                        new SimpleStringProperty(tokens[4].trim()),
                        new SimpleStringProperty(tokens[5].trim()),
                        new SimpleStringProperty(tokens[6].trim()),
                        new SimpleStringProperty(tokens[7].trim()),
                        new SimpleStringProperty(tokens[8].trim()),
                        new SimpleStringProperty(tokens[9].trim()),
                        new SimpleStringProperty(tokens[10].trim()),
                        new SimpleStringProperty(tokens[11].trim()),
                        new SimpleStringProperty(tokens[12].trim()),
                        new SimpleStringProperty(tokens[13].trim()),
                        new SimpleStringProperty(tokens[14].trim()),
                        new SimpleStringProperty(tokens[15].trim()),
                        new SimpleStringProperty(tokens[16].trim()),
                        new SimpleStringProperty(tokens[17].trim()),
                        new SimpleStringProperty(tokens[18].trim())
                );

                userData.add(entry);
            }
            userTable.setItems(userData);
            try (Connection conn = DatabaseConnection.getConnection()) {
                DatabaseUtils.saveUserToDatabase(conn, userData);
                System.out.println("User data saved successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("File Error", "Could not read the file.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Parsing Error", "There was an error while parsing the CSV.");
        }
    }
    
    @SuppressWarnings("unchecked")
	private void setupUserTableColumns() {
    	TableColumn<UserEntry, Integer> userIDCol = new TableColumn<>("User ID");
        userIDCol.setCellValueFactory(cellData -> cellData.getValue().getUserID().asObject());
        
        TableColumn<UserEntry, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(cellData -> cellData.getValue().getFirstName());
        
        TableColumn<UserEntry, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(cellData -> cellData.getValue().getLastName());
        
        TableColumn<UserEntry, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> cellData.getValue().getEmail());
        
        TableColumn<UserEntry, String> activeCol = new TableColumn<>("Active?");
        activeCol.setCellValueFactory(cellData -> cellData.getValue().getActive());
        
        TableColumn<UserEntry, String> tierCol = new TableColumn<>("Tier");
        tierCol.setCellValueFactory(cellData -> cellData.getValue().getTier());
        
        TableColumn<UserEntry, String> pledgeCol = new TableColumn<>("Pledge");
        pledgeCol.setCellValueFactory(cellData -> cellData.getValue().getPledge());
        
        TableColumn<UserEntry, String> addressNameCol = new TableColumn<>("Address Name");
        addressNameCol.setCellValueFactory(cellData -> cellData.getValue().getAddressName());
        
        TableColumn<UserEntry, String> addressLine1Col = new TableColumn<>("Address Line 1");
        addressLine1Col.setCellValueFactory(cellData -> cellData.getValue().getAddressLine1());
        
        TableColumn<UserEntry, String> addressLine2Col = new TableColumn<>("Address Line 2");
        addressLine2Col.setCellValueFactory(cellData -> cellData.getValue().getAddressLine2());
        
        TableColumn<UserEntry, String> cityCol = new TableColumn<>("City");
        cityCol.setCellValueFactory(cellData -> cellData.getValue().getCity());
        
        TableColumn<UserEntry, String> stateCol = new TableColumn<>("State");
        stateCol.setCellValueFactory(cellData -> cellData.getValue().getState());
        
        TableColumn<UserEntry, String> zipCodeCol = new TableColumn<>("Zip Code");
        zipCodeCol.setCellValueFactory(cellData -> cellData.getValue().getZipCode());
        
        TableColumn<UserEntry, String> countryCol = new TableColumn<>("Country");
        countryCol.setCellValueFactory(cellData -> cellData.getValue().getCountry());
        
        TableColumn<UserEntry, String> genderCol = new TableColumn<>("Gender");
        genderCol.setCellValueFactory(cellData -> cellData.getValue().getGender());
        
        TableColumn<UserEntry, String> ageRangeCol = new TableColumn<>("Age Range");
        ageRangeCol.setCellValueFactory(cellData -> cellData.getValue().getAgeRange());
        
        TableColumn<UserEntry, String> educationCol = new TableColumn<>("Education Level");
        educationCol.setCellValueFactory(cellData -> cellData.getValue().getEducationLevel());
        
        TableColumn<UserEntry, String> incomeRangeCol = new TableColumn<>("Income Range");
        incomeRangeCol.setCellValueFactory(cellData -> cellData.getValue().getIncomeRange());
        
        TableColumn<UserEntry, String> raffleEligibleCol = new TableColumn<>("Raffle Eligible");
        raffleEligibleCol.setCellValueFactory(cellData -> cellData.getValue().getRaffleEligible());
        
        ObservableList<TableColumn<UserEntry, ?>> columns = FXCollections.observableArrayList();
        columns.addAll(userIDCol,firstNameCol, lastNameCol, emailCol, activeCol, tierCol, pledgeCol,
        		addressNameCol, addressLine1Col, addressLine2Col, cityCol, stateCol, zipCodeCol,
        		countryCol,genderCol, ageRangeCol, educationCol, incomeRangeCol, raffleEligibleCol);

        userTable.getColumns().addAll(columns);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private LineChart<String, Number> createLineChart(String title, XYChart.Series<String, Number> series) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);

        chart.setTitle(title);
        chart.setLegendVisible(false);
        chart.setAnimated(false); // prevents jitter when switching
        chart.setCreateSymbols(true);

        chart.getData().add(series);

        // Optional styling
        chart.setStyle("-fx-background-color: white;");
        return chart;
    }

    private XYChart.Series<String, Number> buildMonthlyEarningsSeries() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (EarningEntry entry : earningTable.getItems()) {
            String label = entry.getMonthValue().substring(0, 3) + " " + entry.getYearValue();
            double value = entry.getEarnings().get();
            series.getData().add(new XYChart.Data<>(label, value));
        }
        return series;
    }

    private XYChart.Series<String, Number> buildYearlyEarningsSeries() {
        Map<Integer, Double> yearlyEarnings = new HashMap<>();
        for (EarningEntry entry : earningTable.getItems()) {
            int year = entry.getYearValue();
            double value = entry.getEarnings().get();
            yearlyEarnings.put(year, yearlyEarnings.getOrDefault(year, 0.0) + value);
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<Integer, Double> entry : yearlyEarnings.entrySet()) {
            series.getData().add(new XYChart.Data<>(String.valueOf(entry.getKey()), entry.getValue()));
        }
        return series;
    }

    private void updatePostChart(CheckBox impressionsCB, CheckBox likesCB, CheckBox commentsCB, CheckBox freeCB, CheckBox paidCB) {
        postSBC.getData().clear();

        if (postData == null) return;

        if (impressionsCB.isSelected()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Impressions");
            for (PostEntry entry : postData) {
                series.getData().add(new XYChart.Data<>(entry.getTitle().get(), entry.getTotalImpressions().get()));
            }
            postSBC.getData().add(series);
        }

        if (likesCB.isSelected()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Likes");
            for (PostEntry entry : postData) {
                series.getData().add(new XYChart.Data<>(entry.getTitle().get(), entry.getLikes().get()));
            }
            postSBC.getData().add(series);
        }

        if (commentsCB.isSelected()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Comments");
            for (PostEntry entry : postData) {
                series.getData().add(new XYChart.Data<>(entry.getTitle().get(), entry.getComments().get()));
            }
            postSBC.getData().add(series);
        }

        if (freeCB.isSelected()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("New Free Users");
            for (PostEntry entry : postData) {
                series.getData().add(new XYChart.Data<>(entry.getTitle().get(), entry.getNewFreeMembers().get()));
            }
            postSBC.getData().add(series);
        }

        if (paidCB.isSelected()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("New Paid Users");
            for (PostEntry entry : postData) {
                series.getData().add(new XYChart.Data<>(entry.getTitle().get(), entry.getNewPaidMembers().get()));
            }
            postSBC.getData().add(series);
        }
    }
    
    private PieChart createPieChart(Map<String, Integer> data) {
    	ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

    	for (Map.Entry<String, Integer> entry : data.entrySet()) {
    		String labelWithCount = entry.getKey() + " (" + entry.getValue() + ")";
    	    pieChartData.add(new PieChart.Data(labelWithCount, entry.getValue()));
    	}
    	
    	PieChart pieChart = new PieChart(pieChartData);
    	return pieChart;

    }
    
    private HBox createMonthlyYearlyEarnings() {
    	HBox window = new HBox(10); // Add spacing between toggle and chart

        // Create toggle buttons
        RadioButton monthlyButton = new RadioButton("Monthly");
        RadioButton yearlyButton = new RadioButton("Yearly");
        ToggleGroup viewToggle = new ToggleGroup();
        monthlyButton.setToggleGroup(viewToggle);
        yearlyButton.setToggleGroup(viewToggle);
        monthlyButton.setSelected(true); // Default view is monthly

        VBox toggleBox = new VBox(10, monthlyButton, yearlyButton);
        toggleBox.setAlignment(Pos.TOP_LEFT);
        toggleBox.setPadding(new Insets(10));

        // Build data series and charts
        monthlyEarningsSeries = buildMonthlyEarningsSeries();
        yearlyEarningsSeries = buildYearlyEarningsSeries();

        monthlyEarningsChart = createLineChart("Monthly Earnings", monthlyEarningsSeries);
        yearlyEarningsChart = createLineChart("Yearly Earnings", yearlyEarningsSeries);

        // Wrap the chart in a Region to allow resizing
        StackPane chartPane = new StackPane(monthlyEarningsChart);
        chartPane.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(chartPane, Priority.ALWAYS);

        // Toggle between charts
        viewToggle.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == monthlyButton) {
                chartPane.getChildren().setAll(monthlyEarningsChart);
            } else if (newToggle == yearlyButton) {
                chartPane.getChildren().setAll(yearlyEarningsChart);
            }
        });

        window.getChildren().setAll(toggleBox, chartPane);
        window.setPadding(new Insets(10));
        HBox.setHgrow(window, Priority.ALWAYS);

        return window;
    }

    private HBox createPostActivity() {
        HBox window = new HBox(10); // Adds spacing between checkbox panel and chart

        // Create CheckBoxes
        CheckBox showImpressions = new CheckBox("Impressions");
        CheckBox showLikes = new CheckBox("Likes");
        CheckBox showComments = new CheckBox("Comments");
        CheckBox showFreeUsers = new CheckBox("New Free Users");
        CheckBox showPaidUsers = new CheckBox("New Paid Users");

        // Add them to VBox
        VBox checkBoxPanel = new VBox(10, showImpressions, showLikes, showComments, showFreeUsers, showPaidUsers);
        checkBoxPanel.setAlignment(Pos.TOP_LEFT);
        checkBoxPanel.setPadding(new Insets(10));

        // Add listeners
        showImpressions.setOnAction(e -> updatePostChart(showImpressions, showLikes, showComments, showFreeUsers, showPaidUsers));
        showLikes.setOnAction(e -> updatePostChart(showImpressions, showLikes, showComments, showFreeUsers, showPaidUsers));
        showComments.setOnAction(e -> updatePostChart(showImpressions, showLikes, showComments, showFreeUsers, showPaidUsers));
        showFreeUsers.setOnAction(e -> updatePostChart(showImpressions, showLikes, showComments, showFreeUsers, showPaidUsers));
        showPaidUsers.setOnAction(e -> updatePostChart(showImpressions, showLikes, showComments, showFreeUsers, showPaidUsers));

        // Setup chart
        CategoryAxis x = new CategoryAxis();
        x.setLabel("Post Titles");
        NumberAxis y = new NumberAxis();
        y.setLabel("Count");

        postSBC = new StackedBarChart<>(x, y);
        postSBC.setTitle("Post Activity");

        // Wrap the chart in a resizable container
        StackPane chartPane = new StackPane(postSBC);
        chartPane.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(chartPane, Priority.ALWAYS);

        window.getChildren().setAll(checkBoxPanel, chartPane);
        window.setPadding(new Insets(10));
        HBox.setHgrow(window, Priority.ALWAYS);

        return window;
    }
    
    private HBox createSurveyPieChart() {
        HBox window = new HBox(10); // spacing between controls and chart
        window.setPadding(new Insets(10));

        Map<String, Integer> choiceCounts = new HashMap<>();
        for (SurveyEntry entry : surveyData) {
            String choice = entry.getSurvey().get();
            choiceCounts.put(choice, choiceCounts.getOrDefault(choice, 0) + 1);
        }

        PieChart surveyPieChart = createPieChart(choiceCounts);
        surveyPieChart.setTitle("Surveys Completed");

        // Create button to toggle "Show Percentages"
        CheckBox showPercentages = new CheckBox("Show Percentages");
        showPercentages.setSelected(false);

        showPercentages.setOnAction(e -> {
            for (PieChart.Data data : surveyPieChart.getData()) {
                if (showPercentages.isSelected()) {
                    double total = choiceCounts.values().stream().mapToInt(Integer::intValue).sum();
                    double percent = (data.getPieValue() / total) * 100;
                    data.setName(String.format("%s (%.1f%%)", data.getName().split(" \\(")[0], percent));
                } else {
                    data.setName(data.getName().split(" \\(")[0]); // reset name
                }
            }
        });

        VBox controlBox = new VBox(10, showPercentages);
        controlBox.setAlignment(Pos.TOP_LEFT);
        controlBox.setPadding(new Insets(5));

        // Allow chart to expand with window
        StackPane chartPane = new StackPane(surveyPieChart);
        chartPane.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(chartPane, Priority.ALWAYS);

        window.getChildren().addAll(controlBox, chartPane);
        return window;
    }
    
    public HBox createGenderDistributionChart() {
        // Axes
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Gender");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Patron Count");

        // Chart
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Gender Distribution");

        // Button Bar
        VBox buttonBar = new VBox(10);
        buttonBar.setPadding(new Insets(10));
        buttonBar.setAlignment(Pos.CENTER);

        Button allButton = new Button("All");
        Button tier1Button = new Button("Tier 1");
        Button tier2Button = new Button("Tier 2");
        Button tier3Button = new Button("Tier 3");

        buttonBar.getChildren().addAll(allButton, tier1Button, tier2Button, tier3Button);

        // Set chart width behavior
        chart.setAnimated(false);
        chart.setCategoryGap(20);
        chart.setBarGap(5);
        
        Map<String, Map<String, Integer>> dataByTier = new HashMap<>();

        for (UserEntry user : userData) {
        	String tier = "Tier " + user.getTier().get().trim();
            String gender = user.getGender().get(); 

            dataByTier.putIfAbsent(tier, new HashMap<>());
            Map<String, Integer> genderMap = dataByTier.get(tier);

            genderMap.put(gender, genderMap.getOrDefault(gender, 0) + 1);
        }

        // Update chart data based on selection
        Runnable updateChart = () -> updateGenderChart(chart, dataByTier, "All");  // Default view
        allButton.setOnAction(e -> updateGenderChart(chart, dataByTier, "All"));
        tier1Button.setOnAction(e -> updateGenderChart(chart, dataByTier, "Tier 1"));
        tier2Button.setOnAction(e -> updateGenderChart(chart, dataByTier, "Tier 2"));
        tier3Button.setOnAction(e -> updateGenderChart(chart, dataByTier, "Tier 3"));

        updateChart.run(); // Initial chart

        HBox.setHgrow(chart, Priority.ALWAYS);
        chart.setMaxWidth(Double.MAX_VALUE);

        HBox layout = new HBox(10, buttonBar, chart);
        layout.setPadding(new Insets(10));
        return layout;
    }

    private void updateGenderChart(BarChart<String, Number> chart, Map<String, Map<String, Integer>> data, String tier) {
        chart.getData().clear();

        Map<String, Integer> counts = new HashMap<>();
        counts.put("Male", 0);
        counts.put("Female", 0);
        counts.put("Non-Binary", 0);

        if (tier.equals("All")) {
            for (Map<String, Integer> genderMap : data.values()) {
                for (String gender : counts.keySet()) {
                    counts.put(gender, counts.get(gender) + genderMap.getOrDefault(gender, 0));
                }
            }
        } else if (data.containsKey(tier)) {
            counts = new HashMap<>(data.get(tier)); // Only that tier
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(tier);

        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        chart.getData().add(series);
    }
    
    public HBox createIncomeVsPledgeScatterChart() {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Income ($)");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Pledge ($)");

        ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.setTitle("Income vs Pledge");

        // Map to group users by tier
        Map<String, XYChart.Series<Number, Number>> tierSeriesMap = new HashMap<>();

        // Create a random generator for jitter
        Random random = new Random();
        double jitterAmount = 1.5; // adjust this to control spacing

        for (UserEntry user : userData) {
            double incomeMid = getMidpoint(user.getIncomeRange().get());
            double pledge = Double.parseDouble(user.getPledge().get());
            String tier = user.getTier().get(); // Assuming this returns "1", "2", "3", etc.

            // Apply jitter to income and pledge
            incomeMid += (random.nextDouble() - 0.5) * jitterAmount;
            pledge += (random.nextDouble() - 0.5) * jitterAmount;

            // Create or get the appropriate series
            XYChart.Series<Number, Number> series = tierSeriesMap.computeIfAbsent(tier, k -> {
                XYChart.Series<Number, Number> s = new XYChart.Series<>();
                s.setName("Tier " + k);
                return s;
            });

            XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(incomeMid, pledge);

            // Tooltip with relevant info
            Tooltip tooltip = new Tooltip(
                "Income: " + user.getIncomeRange().get() + "\n" +
                "Pledge: $" + user.getPledge().get()
            );

            // Add the data point and install tooltip after layout
            series.getData().add(dataPoint);
            dataPoint.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    Tooltip.install(newNode, tooltip);
                }
            });
        }

        scatterChart.getData().addAll(tierSeriesMap.values());

        HBox.setHgrow(scatterChart, Priority.ALWAYS);
        scatterChart.setMaxWidth(Double.MAX_VALUE);

        HBox layout = new HBox(scatterChart);
        layout.setPadding(new Insets(10));
        return layout;
    }

    private double getMidpoint(String range) {
        try {
            range = range.replaceAll("[$kK,]", "") // Remove symbols
                         .replaceAll("\\s+", ""); // Remove spaces

            String[] parts = range.split("-");
            if (parts.length == 2) {
                double low = Double.parseDouble(parts[0]);
                double high = Double.parseDouble(parts[1]);
                return (low + high) / 2;
            } else {
                return Double.parseDouble(range); // If only one number
            }
        } catch (Exception e) {
            return 0; // Default fallback
        }
    }

    public HBox createEducationPieChart() {
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Education Level Breakdown");

        VBox buttonBar = new VBox(10);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setPadding(new Insets(10));

        Button allButton = new Button("All");
        Button tier1Button = new Button("Tier 1");
        Button tier2Button = new Button("Tier 2");
        Button tier3Button = new Button("Tier 3");

        buttonBar.getChildren().addAll(allButton, tier1Button, tier2Button, tier3Button);

        // Action logic
        allButton.setOnAction(e -> updateEducationPieChart(pieChart, "All"));
        tier1Button.setOnAction(e -> updateEducationPieChart(pieChart, "1"));
        tier2Button.setOnAction(e -> updateEducationPieChart(pieChart, "2"));
        tier3Button.setOnAction(e -> updateEducationPieChart(pieChart, "3"));

        updateEducationPieChart(pieChart, "All"); // Initial state

        HBox layout = new HBox(10, buttonBar, pieChart);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);
        return layout;
    }

    private void updateEducationPieChart(PieChart chart, String tierFilter) {
        Map<String, Integer> educationCounts = new HashMap<>();

        for (UserEntry user : userData) {
            String tier = user.getTier().get().trim();
            String education = user.getEducationLevel().get().trim();

            if (!tierFilter.equals("All") && !tier.equals(tierFilter)) continue;

            educationCounts.put(education, educationCounts.getOrDefault(education, 0) + 1);
        }

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        for (Map.Entry<String, Integer> entry : educationCounts.entrySet()) {
            pieData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        chart.setData(pieData);
    }

    public void loadEarningsFromDB() {
        String query = "SELECT month, year, total, webMembershipCharges, iOSMembershipCharges, " +
                       "webGiftCharges, iOSGiftCharges, earnings, processingFee, patreonFee, iOSFee, " +
                       "merchShipping, declines, percentMembershipEarnings, percentMembershipProcessingFees, " +
                       "percentMembershipPatreonFees, percentGiftEarnings, percentGiftProcessingFees, " +
                       "percentGiftPatreonFees, currencyConversionFee, currencyConversionFeePercent, currency " +
                       "FROM earnings";  // match your actual DB table and columns

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            earningData.clear();

            while (rs.next()) {
                EarningEntry entry = new EarningEntry(
                    rs.getString("month"),
                    rs.getInt("year"),
                    rs.getDouble("total"),
                    rs.getDouble("webMembershipCharges"),
                    rs.getDouble("iOSMembershipCharges"),
                    rs.getDouble("webGiftCharges"),
                    rs.getDouble("iOSGiftCharges"),
                    rs.getDouble("earnings"),
                    rs.getDouble("processingFee"),
                    rs.getDouble("patreonFee"),
                    rs.getDouble("iOSFee"),
                    rs.getDouble("merchShipping"),
                    rs.getDouble("declines"),
                    rs.getDouble("percentMembershipEarnings"),
                    rs.getDouble("percentMembershipProcessingFees"),
                    rs.getDouble("percentMembershipPatreonFees"),
                    rs.getDouble("percentGiftEarnings"),
                    rs.getDouble("percentGiftProcessingFees"),
                    rs.getDouble("percentGiftPatreonFees"),
                    rs.getDouble("currencyConversionFee"),
                    rs.getDouble("currencyConversionFeePercent"),
                    rs.getString("currency")
                );

                earningData.add(entry);
            }

            earningTable.setItems(earningData);

            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            if (e.getMessage().contains("no such table")) {
                System.out.println("Earnings table does not exist. Creating it...");
                if (conn != null) {
                    createEarningsTable(conn);
                }
            } else {
                e.printStackTrace();
            }
        }
    }

    public static void createEarningsTable(Connection conn) {
        String sql = """
            CREATE TABLE IF NOT EXISTS earnings (
                month TEXT,
                year INTEGER,
                total REAL,
                webMembershipCharges REAL,
                iOSMembershipCharges REAL,
                webGiftCharges REAL,
                iOSGiftCharges REAL,
                earnings REAL,
                processingFee REAL,
                patreonFee REAL,
                iOSFee REAL,
                merchShipping REAL,
                declines REAL,
                percentMembershipEarnings REAL,
                percentMembershipProcessingFees REAL,
                percentMembershipPatreonFees REAL,
                percentGiftEarnings REAL,
                percentGiftProcessingFees REAL,
                percentGiftPatreonFees REAL,
                currencyConversionFee REAL,
                currencyConversionFeePercent REAL,
                currency TEXT
            )
            """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Earnings table created.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void loadPostFromDB() {
    	String query = "SELECT title, totalImpressions, likes, comments, newFreeMembers, " +
    			"newPaidMembers, publishedDateTime, link FROM posts";  // match your actual DB table and columns

    	Connection conn = null;

    	try {conn = DatabaseConnection.getConnection();
    		PreparedStatement stmt = conn.prepareStatement(query);
    		ResultSet rs = stmt.executeQuery();

    		postData.clear();

    		while (rs.next()) {
    			PostEntry entry = new PostEntry(
    					rs.getString("title"),
    					rs.getInt("totalImpressions"),
    					rs.getInt("likes"),
    					rs.getInt("comments"),
    					rs.getInt("newFreeMembers"),
    					rs.getInt("newPaidMembers"),
    					rs.getString("publishedDateTime"),
    					rs.getString("link")
    			);

    			postData.add(entry);
    		}

    		postTable.setItems(postData);

    		rs.close();
    		stmt.close();
    		conn.close();

    	} catch (SQLException e) {
    		if (e.getMessage().contains("no such table")) {
    			System.out.println("Post table does not exist. Creating it...");
    			if (conn != null) {
    				createPostTable(conn);
    			}
    		} else {
    			e.printStackTrace();
    		}
    	}
    }
    
    public static void createPostTable(Connection conn) {
    	String sql = """
                CREATE TABLE IF NOT EXISTS posts (
                    title TEXT, 
                    totalImpressions INTEGER, 
                    likes INTEGER, 
                    comments INTEGER, 
                    newFreeMembers INTEGER,
    				newPaidMembers INTEGER, 
    				publishedDateTime TEXT, 
    				link TEXT
                )
                """;

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("Posts table created.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
    
    public void loadSurveyFromDB() {
    	String query = "SELECT submittedDateTime, name, email, tier, survey, comments FROM surveys";  // match your actual DB table and columns

    	Connection conn = null;

    	try {conn = DatabaseConnection.getConnection();
    		PreparedStatement stmt = conn.prepareStatement(query);
    		ResultSet rs = stmt.executeQuery();

    		surveyData.clear();

    		while (rs.next()) {
    			SurveyEntry entry = new SurveyEntry(
    					rs.getString("submittedDateTime"),
    					rs.getString("name"),
    					rs.getString("email"),
    					rs.getString("tier"),
    					rs.getString("survey"),
    					rs.getString("comments")
    			);

    			surveyData.add(entry);
    		}

    		surveyTable.setItems(surveyData);

    		rs.close();
    		stmt.close();
    		conn.close();

    	} catch (SQLException e) {
    		if (e.getMessage().contains("no such table")) {
    			System.out.println("Survey table does not exist. Creating it...");
    			if (conn != null) {
    				createSurveyTable(conn);
    			}
    		} else {
    			e.printStackTrace();
    		}
    	}
    }
    
    public static void createSurveyTable(Connection conn) {
    	String sql = """
                CREATE TABLE IF NOT EXISTS surveys (
                    submittedDateTime TEXT, 
                    name TEXT, 
                    email TEXT, 
                    tier TEXT, 
                    survey TEXT, 
                    comments TEXT
                )
                """;

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("Surveys table created.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
    
    public void loadUserFromDB() {
    	String query = "SELECT id, address_line1, address_line2, address_name, age_range, city,"
    			+ "country, education_level, email, first_name, gender, income_range, is_active,"
    			+ "last_name, pledge_amount_cents, raffle_eligible, state, tier_id, zip_code "
    			+ "FROM member";  // match your actual DB table and columns

    	Connection conn = null;

    	try {
    		conn = DatabaseConnection.getConnection();
    		PreparedStatement stmt = conn.prepareStatement(query);
    		ResultSet rs = stmt.executeQuery();

    		userData.clear();

    		while (rs.next()) {
    			UserEntry entry = new UserEntry(
    					rs.getInt("id"),
    					rs.getString("first_name"),
    					rs.getString("last_name"),
    					rs.getString("email"),
    					rs.getString("is_active"),	
    					rs.getString("tier_id"),
    					rs.getString("pledge_amount_cents"),
    					rs.getString("address_name"),	
    					rs.getString("address_line1"),
    					rs.getString("address_line2"),
    					rs.getString("city"),
    					rs.getString("state"),		
    					rs.getString("zip_code"),
    					rs.getString("country"),
    					rs.getString("gender"),
    					rs.getString("age_range"),
    					rs.getString("education_level"),
    					rs.getString("income_range"),
    					rs.getString("raffle_eligible")
    			);

         userData.add(entry);
     }

     userTable.setItems(userData);

     rs.close();
     stmt.close();
     conn.close();

 } catch (SQLException e) {
     if (e.getMessage().contains("no such table")) {
         System.out.println("Member table does not exist. Creating it...");
         if (conn != null) {
             createUserTable(conn);
         }
     } else {
         e.printStackTrace();
     }
 }
    }
    
    public static void createUserTable(Connection conn) {
    	String sql = """
                CREATE TABLE IF NOT EXISTS member (
                    id INTEGER, 
                    address_line1 TEXT, 
                    address_line2 TEXT, 
                    address_name TEXT, 
                    age_range TEXT, 
                    city TEXT,
    				country TEXT, 
    				education_level TEXT, 
    				email TEXT, 
    				first_name TEXT, 
    				gender TEXT, 
    				income_range TEXT, 
    				is_active TEXT, 
    				last_name TEXT, 
    				pledge_amount_cents INTEGER, 
    				raffle_eligible TEXT, 
    				state TEXT, 
    				tier_id TEXT, 
    				zip_code TEXT
                )
                """;

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("Member table created.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
    
    public void loadRewardsFromDB() {
    	String query = "SELECT message, subject, trigger, recipients FROM rewards";  // match your actual DB table and columns

    	Connection conn = null;

    	try {conn = DatabaseConnection.getConnection();
    		PreparedStatement stmt = conn.prepareStatement(query);
    		ResultSet rs = stmt.executeQuery();

    		rewardList.clear();

    		while (rs.next()) {
    			EmailReward entry = new EmailReward(
    					rs.getString("message"),
    					rs.getString("subject"),
    					rs.getString("trigger"),
    					rs.getString("recipients")
    			);

    			rewardList.add(entry);
    		}

    		rewardsTable.setItems(rewardList);

    		rs.close();
    		stmt.close();
    		conn.close();

    	} catch (SQLException e) {
    		if (e.getMessage().contains("no such table")) {
    			System.out.println("Rewards table does not exist. Creating it...");
    			if (conn != null) {
    				createRewardsTable(conn);
    			}
    		} else {
    			e.printStackTrace();
    		}
    	}
    }
    
    public static void createRewardsTable(Connection conn) {
    	String sql = """
                CREATE TABLE IF NOT EXISTS rewards (
                    message TEXT, 
                    subject TEXT, 
                    trigger TEXT, 
                    recipients TEXT
                )
                """;

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("Rewards table created.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
}
