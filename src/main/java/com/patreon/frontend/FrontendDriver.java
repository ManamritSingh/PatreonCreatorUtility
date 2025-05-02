package com.patreon.frontend;

import com.patreon.frontend.models.EmailReward;
import com.patreon.frontend.models.EarningEntry;
import com.patreon.frontend.models.PostEntry;
import com.patreon.frontend.models.SurveyEntry;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FrontendDriver extends Application {

    private final ToolBar toolBar = new ToolBar();
    private final TabPane tabPane = new TabPane();
    private final VBox revenueChartBox = new VBox();
    private final VBox campaignChartBox = new VBox();

    private Stage window;

    private TableView<EarningEntry> earningTable = new TableView<>();
	private TableView<PostEntry> postTable = new TableView<>();
	private TableView<SurveyEntry> surveyTable = new TableView<>();
	private TableView<EmailReward> rewardsTable = new TableView<>();

	private ObservableList<EarningEntry> earningData = FXCollections.observableArrayList();
	private ObservableList<PostEntry> postData = FXCollections.observableArrayList(); 
	private ObservableList<SurveyEntry> surveyData = FXCollections.observableArrayList();
	private ObservableList<EmailReward> rewardList = FXCollections.observableArrayList();
	
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
        toolBar.setOrientation(Orientation.VERTICAL);
        BorderPane layout = new BorderPane();
        layout.setTop(createMenuBar());
        layout.setLeft(toolBar);
        layout.setCenter(tabPane);

        // Initialize tabs,tables, and toolbars
        initializeTabs();
        initializeTables();
        tabPane.getSelectionModel().select(0);
        updateToolBar(tabPane.getTabs().get(0).getText());

        // Tab change listener to update toolbar
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                updateToolBar(newTab.getText());
            }
        });

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

        
        viewRevenue.setOnAction(e -> openTab("Revenue"));
        viewRetention.setOnAction(e -> openTab("Retention"));
        viewDemographics.setOnAction(e -> openTab("Demographics"));
        viewCampaign.setOnAction(e -> openTab("Campaign Activity"));
        viewPostFile.setOnAction(e -> openTab("Posts File"));
        viewEarningsFile.setOnAction(e -> openTab("Earnings File"));
        viewSurveyFile.setOnAction(e -> openTab("Surveys File"));
        
        emailRewards.setOnAction(e -> openTab("Email Rewards"));



        //Open CSV Files
        menuOpen.setOnAction(e -> openFile());

        viewMenu.getItems().addAll(charts,dataFiles);
        charts.getItems().addAll(viewRevenue, viewRetention, viewDemographics, viewCampaign);
        dataFiles.getItems().addAll(viewPostFile, viewEarningsFile, viewSurveyFile);
        fileMenu.getItems().addAll(menuOpen, viewMenu, menuSave, emailRewards);
        menuBar.getMenus().add(fileMenu);

        return menuBar;
    }

    // ----------------------------
    // Tab Initialization
    // ----------------------------
    private void initializeTabs() {
        addTab("Revenue",new VBox());
        addTab("Retention",new VBox());
        addTab("Demographics",new VBox());
        addTab("Campaign Activity",new VBox());
    }

    private void addTab(String title, Node contents) {
        Tab tab = new Tab(title,contents);
        tabPane.getTabs().add(tab);
    }

    private void openTab(String section) {
        // First, check if the tab already exists
        for (Tab tab : tabPane.getTabs()) {
            if (tab.getText().equals(section)) {
                tabPane.getSelectionModel().select(tab);
                return; // Return if the tab already exists
            }
        }

        // If the tab doesn't exist, create a new one
        Tab newTab = new Tab(section);
        newTab.setClosable(true);

        VBox tabContent = new VBox(); // You can add your TableView and graphs here

        // Depending on the section (file type), we'll populate the content of the tab
        if (section.equals("Earnings File") && earningTable != null) {
            tabContent.getChildren().add(earningTable); // Add the Earnings table
            // You can also add any relevant graph for Earnings data here
        } else if (section.equals("Posts File") && postTable != null) {
            tabContent.getChildren().add(postTable); // Add the Posts table
            // You can also add any relevant graph for Posts data here
        } else if (section.equals("Surveys File") && surveyTable != null) {
            tabContent.getChildren().add(surveyTable); // Add the Surveys table
            // You can also add any relevant graph for Surveys data here
        } else if (section.equals("Email Rewards")) {
        	tabContent.getChildren().add(rewardsTable);
        }

        newTab.setContent(tabContent); // Set the content of the new tab
        tabPane.getTabs().add(newTab); // Add the new tab to the tab pane
        tabPane.getSelectionModel().select(newTab); // Select the new tab
    }
    
    private void initializeTables() {
    	//CHANGE THIS LATER TO GRAB PREVIOUS DATA FROM DATABASE
    	setupEarningTableColumns();
    	earningTable.setItems(earningData);
    	
    	setupPostTableColumns();
    	postTable.setItems(postData);
    	
    	setupSurveyTableColumns();
    	surveyTable.setItems(surveyData);
    	
    	setupRewardsTableColumns();
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


            EmailReward reward = new EmailReward(subjectText, messageText, triggerText, recipients);
            rewardList.add(reward);
            
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
    private void updateToolBar(String section) {
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
    }

    private Tab getTabByName(String name) {
        for (Tab tab : tabPane.getTabs()) {
            if (tab.getText().equals(name)) {
                return tab;
            }
        }
        return null;
    }
    
    private void openFile() {
    	try{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open CSV File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showOpenDialog(window);

            if (file != null) {
                List<String> options = Arrays.asList("Earnings", "Posts", "Surveys");
                ChoiceDialog<String> dialog = new ChoiceDialog<>("Earnings", options);
                dialog.setTitle("Select Data Type");
                dialog.setHeaderText("What type of data is this?");
                dialog.setContentText("Choose type:");

                Optional<String> result = dialog.showAndWait();

                if (result.isPresent()) {
                    String type = result.get();
                    Tab targetTab = null;
                    VBox chartBox = null;

                    switch (type) {
                        case "Earnings":
                            parseEarningsCSV(file);
                            targetTab = getTabByName("Revenue");
                            monthlyEarningsSeries = buildMonthlyEarningsSeries();
                            yearlyEarningsSeries = buildYearlyEarningsSeries();

                            monthlyEarningsChart = createLineChart("Monthly Earnings", monthlyEarningsSeries);
                            yearlyEarningsChart = createLineChart("Yearly Earnings", yearlyEarningsSeries);

                            revenueChartBox.getChildren().setAll(monthlyEarningsChart);
                            chartBox = revenueChartBox;
                            break;
                        case "Posts":
                            parsePostsCSV(file);
                            targetTab = getTabByName("Campaign Activity");

                            CategoryAxis x = new CategoryAxis();
                            x.setLabel("Post Titles");

                            NumberAxis y = new NumberAxis();
                            y.setLabel("Count");

                            postSBC = new StackedBarChart<>(x, y);
                            postSBC.setTitle("Post Activity");

                            campaignChartBox.getChildren().setAll(postSBC);
                            chartBox = campaignChartBox;
                            break;
                        case "Surveys":
                            parseSurveysCSV(file);
                            targetTab = getTabByName("Retention"); // or "Demographics" if you prefer
                            break;
                    }

                    if (targetTab != null && chartBox!=null) {
                        VBox tabContent = (VBox) targetTab.getContent();
                        tabContent.getChildren().clear();
                        tabContent.getChildren().add(chartBox); 
                    }
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
            String[] actualHeaders = header.toLowerCase().split("\t|,");

            for (String expected : expectedHeaders) {
                boolean found = Arrays.stream(actualHeaders)
                        .anyMatch(h -> h.trim().contains(expected.toLowerCase()));
                if (!found) {
                    showAlert("Invalid File", "This doesn't appear to be a surveys CSV.");
                }
            }


            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\t|,"); // handles tab- or comma-separated

                if (tokens.length < 7) continue; // Ensure the line is valid

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

}
