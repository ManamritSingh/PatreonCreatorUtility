package com.patreon.frontend.utils;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import com.patreon.frontend.models.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;

public class ChartCreator {
    
    private StackedBarChart<String, Number> postSBC;
    
	private LineChart<String, Number> createLineChart(String title, XYChart.Series<String, Number> series) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);

        chart.setTitle(title);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setCreateSymbols(true);

        chart.getData().add(series);

        // Optional styling
        chart.setStyle("-fx-background-color: white;");
        return chart;
    }

    private XYChart.Series<String, Number> buildMonthlyEarningsSeries(TableView<EarningEntry> earningTable) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (EarningEntry entry : earningTable.getItems()) {
            String label = entry.getMonthValue().substring(0, 3) + " " + entry.getYearValue();
            double value = entry.getEarnings().get();
            series.getData().add(new XYChart.Data<>(label, value));
        }
        return series;
    }
    
    private XYChart.Series<String, Number> buildMonthlySeriesForYear(List<EarningEntry> entries) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        entries.stream()
        .sorted(Comparator.comparingInt(EarningEntry::getMonthNumber))
        .forEach(entry -> {
            String label = entry.getMonthValue().substring(0, 3); // Still use short label
            series.getData().add(new XYChart.Data<>(label, entry.getEarnings().get()));
        });

        return series;
    }

    private XYChart.Series<String, Number> buildYearlyEarningsSeries(TableView<EarningEntry> earningTable) {
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

    public void updatePostChart(StackedBarChart<String, Number> postSBC, ObservableList<PostEntry> postData, CheckBox impressionsCB, CheckBox likesCB, CheckBox commentsCB, CheckBox freeCB, CheckBox paidCB) {
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
    
    public HBox createMonthlyYearlyEarnings(TableView<EarningEntry> earningTable) {
        HBox window = new HBox(10);
        VBox controlBox = new VBox(10);
        controlBox.setAlignment(Pos.TOP_LEFT);
        controlBox.setPadding(new Insets(10));

        // Group data by year
        Map<Integer, List<EarningEntry>> earningsByYear = earningTable.getItems()
                .stream()
                .collect(Collectors.groupingBy(EarningEntry::getYearValue));

        // Year selection ComboBox with "All" first, then years in descending order
        ComboBox<String> yearSelector = new ComboBox<>();
        List<String> years = earningsByYear.keySet().stream()
                .sorted(Comparator.reverseOrder()) // Sort descending: most recent first
                .map(String::valueOf)
                .collect(Collectors.toList());

        years.add(0, "All"); // Add "All" at the top
        yearSelector.getItems().addAll(years);
        yearSelector.getSelectionModel().selectFirst(); // Default to "All"


        // Create toggle buttons
        RadioButton monthlyButton = new RadioButton("Monthly");
        RadioButton yearlyButton = new RadioButton("Yearly");
        ToggleGroup viewToggle = new ToggleGroup();
        monthlyButton.setToggleGroup(viewToggle);
        yearlyButton.setToggleGroup(viewToggle);
        monthlyButton.setSelected(true);

        controlBox.getChildren().addAll(new Label("Select Year:"), yearSelector, monthlyButton, yearlyButton);

        // Initial series and charts
        XYChart.Series<String, Number> monthlySeries = buildMonthlyEarningsSeries(earningTable); // "All" by default
        XYChart.Series<String, Number> yearlySeries = buildYearlyEarningsSeries(earningTable);

        LineChart<String, Number> monthlyChart = createLineChart("Monthly Earnings (All Years)", monthlySeries);
        LineChart<String, Number> yearlyChart = createLineChart("Yearly Earnings", yearlySeries);

        StackPane chartPane = new StackPane(monthlyChart);
        HBox.setHgrow(chartPane, Priority.ALWAYS);

        // Year selector listener
        yearSelector.setOnAction(e -> {
            String selected = yearSelector.getValue();
            if ("All".equals(selected)) {
                XYChart.Series<String, Number> allSeries = buildMonthlyEarningsSeries(earningTable);
                monthlyChart.getData().setAll(allSeries);
                monthlyChart.setTitle("Monthly Earnings (All Years)");
            } else {
                int selectedYear = Integer.parseInt(selected);
                XYChart.Series<String, Number> newSeries = buildMonthlySeriesForYear(earningsByYear.get(selectedYear));
                monthlyChart.getData().setAll(newSeries);
                monthlyChart.setTitle("Monthly Earnings (" + selected + ")");
            }
        });

        // Chart toggle listener
        viewToggle.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == monthlyButton) {
                chartPane.getChildren().setAll(monthlyChart);
            } else if (newToggle == yearlyButton) {
                chartPane.getChildren().setAll(yearlyChart);
            }
        });

        window.getChildren().addAll(controlBox, chartPane);
        window.setPadding(new Insets(10));
        return window;
    }

    public HBox createPostActivity(ObservableList<PostEntry> postData) {
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
        showImpressions.setOnAction(e -> updatePostChart(postSBC, postData, showImpressions, showLikes, showComments, showFreeUsers, showPaidUsers));
        showLikes.setOnAction(e -> updatePostChart(postSBC, postData, showImpressions, showLikes, showComments, showFreeUsers, showPaidUsers));
        showComments.setOnAction(e -> updatePostChart(postSBC, postData, showImpressions, showLikes, showComments, showFreeUsers, showPaidUsers));
        showFreeUsers.setOnAction(e -> updatePostChart(postSBC, postData, showImpressions, showLikes, showComments, showFreeUsers, showPaidUsers));
        showPaidUsers.setOnAction(e -> updatePostChart(postSBC, postData, showImpressions, showLikes, showComments, showFreeUsers, showPaidUsers));

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
    
    public HBox createSurveyPieChart(ObservableList<SurveyEntry> surveyData) {
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
    
    public HBox createGenderDistributionChart(ObservableList<UserEntry> userData) {
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
    
    public HBox createIncomeVsPledgeScatterChart(ObservableList<UserEntry> userData ) {
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

    public HBox createEducationPieChart(ObservableList<UserEntry> userData ) {
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
        allButton.setOnAction(e -> updateEducationPieChart(pieChart, "All", userData));
        tier1Button.setOnAction(e -> updateEducationPieChart(pieChart, "1", userData));
        tier2Button.setOnAction(e -> updateEducationPieChart(pieChart, "2", userData));
        tier3Button.setOnAction(e -> updateEducationPieChart(pieChart, "3", userData));

        updateEducationPieChart(pieChart, "All", userData); // Initial state

        HBox layout = new HBox(10, buttonBar, pieChart);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);
        return layout;
    }

    private void updateEducationPieChart(PieChart chart, String tierFilter, ObservableList<UserEntry> userData ) {
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

    // 1. Retention Line Chart
    public LineChart<String, Number> createRetentionLineChart(String interval, List<String> selectedTiers, boolean isMock) {
        DatabaseServices db = new DatabaseServices();
        Map<String, Map<LocalDate, Integer>> retentionData = db.getTierRetentionData(interval, isMock);

        LineChart<String, Number> retentionChart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        retentionChart.setTitle("Patron Retention - " + interval);
        retentionChart.setMinWidth(1000);
        HBox.setHgrow(retentionChart, Priority.ALWAYS);

        for (String tier : selectedTiers) {
            if (!retentionData.containsKey(tier)) continue;
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(tier);
            for (Map.Entry<LocalDate, Integer> entry : retentionData.get(tier).entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
            }
            retentionChart.getData().add(series);
        }

        return retentionChart;
    }

    // 2. Avg Churn Bar Chart
    public BarChart<String, Number> createAvgChurnChart(String interval, List<String> selectedTiers, boolean isMock) {
        DatabaseServices db = new DatabaseServices();
        Map<String, Map<LocalDate, Double>> churnData = db.getAvgChurnRates(interval, isMock);

        BarChart<String, Number> churnChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        churnChart.setTitle("Average Churn Rate - " + interval);
        churnChart.setMinWidth(1000);
        HBox.setHgrow(churnChart, Priority.ALWAYS);

        for (String tier : selectedTiers) {
            if (!churnData.containsKey(tier)) continue;
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(tier);
            for (Map.Entry<LocalDate, Double> entry : churnData.get(tier).entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
            }
            churnChart.getData().add(series);
        }

        return churnChart;
    }

    // 3. Weekly Churn Line Chart
    public LineChart<String, Number> createWeeklyChurnChart(List<String> selectedTiers, boolean isMock) {
        DatabaseServices db = new DatabaseServices();
        Map<String, Map<LocalDate, Integer>> churnData = db.getWeeklyChurnData(selectedTiers, isMock);

        LineChart<String, Number> churnWeeklyChart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        churnWeeklyChart.setTitle("Weekly Churn - Last 15 Weeks");
        churnWeeklyChart.setMinWidth(1000);
        HBox.setHgrow(churnWeeklyChart, Priority.ALWAYS);

        for (String tier : selectedTiers) {
            if (!churnData.containsKey(tier)) continue;
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(tier);
            for (Map.Entry<LocalDate, Integer> entry : churnData.get(tier).entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
            }
            churnWeeklyChart.getData().add(series);
        }

        return churnWeeklyChart;
    }

    private String formatDateLabel(LocalDate date, String interval) {
        switch (interval.toLowerCase()) {
            case "monthly":
                return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
            case "weekly":
                return date.getYear() + " W" + date.get( java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear());
            default:
                return date.toString(); // daily
        }
    }


//    public HBox createWeeklyChurnChart(List<String> selectedTiers) {
//        Map<String, Map<LocalDate, Integer>> churnData = new DatabaseServices().getWeeklyChurnData(selectedTiers, true);
//
//        CategoryAxis xAxis = new CategoryAxis();
//        NumberAxis yAxis = new NumberAxis();
//        xAxis.setLabel("Week");
//        yAxis.setLabel("Churn Count");
//
//        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
//        chart.setTitle("Weekly Patron Churn (Last 15 Weeks)");
//
//        for (Map.Entry<String, Map<LocalDate, Integer>> entry : churnData.entrySet()) {
//            XYChart.Series<String, Number> series = new XYChart.Series<>();
//            series.setName(entry.getKey());
//
//            for (Map.Entry<LocalDate, Integer> dataPoint : entry.getValue().entrySet()) {
//                String week = dataPoint.getKey().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//                series.getData().add(new XYChart.Data<>(week, dataPoint.getValue()));
//            }
//
//            chart.getData().add(series);
//        }
//
//        HBox box = new HBox(chart);
//        box.setPadding(new Insets(10));
//        return box;
//    }
    
    public HBox createGrossVsNetChart(List<EarningEntry> entries) {
        // Group by year
        Map<Integer, List<EarningEntry>> earningsByYear = entries.stream()
            .collect(Collectors.groupingBy(EarningEntry::getYearValue));

        List<String> yearOptions = earningsByYear.keySet().stream()
            .sorted(Comparator.reverseOrder()) // Recent first
            .map(String::valueOf)
            .collect(Collectors.toList());
        yearOptions.add(0, "All"); // Add "All" at the top

        // ComboBox for year selection
        ComboBox<String> yearSelector = new ComboBox<>();
        yearSelector.getItems().addAll(yearOptions);
        yearSelector.getSelectionModel().selectFirst(); // Default to "All"

        // Chart and container
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Total Revenue vs. Net Earnings");
        chart.setLegendVisible(true);
        chart.setAnimated(false);
        chart.setCreateSymbols(true);
        chart.setStyle("-fx-background-color: white;");

        // Create chart data function
        Runnable updateChart = () -> {
            String selected = yearSelector.getValue();
            List<EarningEntry> filtered = selected.equals("All")
                ? entries
                : earningsByYear.getOrDefault(Integer.parseInt(selected), Collections.emptyList());

            XYChart.Series<String, Number> grossSeries = new XYChart.Series<>();
            grossSeries.setName("Total Revenue");

            XYChart.Series<String, Number> netSeries = new XYChart.Series<>();
            netSeries.setName("Net Earnings");

            filtered.stream()
                .sorted(Comparator.comparingInt(EarningEntry::getMonthNumber)) // You’ll need this method
                .forEach(entry -> {
                    String label = entry.getMonthValue().substring(0, 3); // "Jan", "Feb", etc.
                    grossSeries.getData().add(new XYChart.Data<>(label, entry.getTotalValue()));
                    netSeries.getData().add(new XYChart.Data<>(label, entry.getEarningsValue()));
                });

            chart.getData().setAll(grossSeries, netSeries);
        };

        // Initial chart build
        updateChart.run();

        // Listener to update when year changes
        yearSelector.setOnAction(e -> updateChart.run());

        VBox controlPanel = new VBox(10, new Label("Select Year:"), yearSelector);
        controlPanel.setPadding(new Insets(10));
        controlPanel.setAlignment(Pos.TOP_LEFT);

        StackPane chartPane = new StackPane(chart);
        HBox.setHgrow(chartPane, Priority.ALWAYS);

        HBox container = new HBox(10, controlPanel, chartPane);
        container.setPadding(new Insets(10));

        return container;
    }





}
