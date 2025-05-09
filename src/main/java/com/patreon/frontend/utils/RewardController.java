package com.patreon.frontend.utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.patreon.backend.RewardTriggerService;
import com.patreon.frontend.models.EmailReward;
import com.patreon.utils.DatabaseConnection;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;

@Controller
public class RewardController {

	private DatabaseServices ds = new DatabaseServices();
	@Autowired
    private RewardTriggerService rewardTriggerService;

	public void newReward(List<EmailReward> rewardList) {

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("New Email Reward");

        // Trigger dropdown
        Label triggerLabel = new Label("Trigger:");
        ComboBox<String> triggerCombo = new ComboBox<>();
        triggerCombo.getItems().addAll(
                "Send Now", "New Subscriber", "Upgraded Tier", "Raffle", "Unsubscribed"
        );
        triggerCombo.setValue("Send Now");

        // Recipient checkboxes
        Label recipientLabel = new Label("Recipient:");
        HBox recipientBox = new HBox(10);
        recipientBox.setAlignment(Pos.CENTER_LEFT);

        CheckBox everyone = new CheckBox("All");
        recipientBox.getChildren().add(everyone);

        // Dynamically load tiers
        List<String> tiers = ds.getAllTiers(false);
        for (String tier : tiers) {
            CheckBox tierCheckbox = new CheckBox(tier);
            recipientBox.getChildren().add(tierCheckbox);
        }

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

            // Collect selected recipients
            if (everyone.isSelected()) {
                recipients.add("All");
            } else {
                for (Node node : recipientBox.getChildren()) {
                    if (node instanceof CheckBox checkbox && checkbox.isSelected() && !checkbox.equals(everyone)) {
                        recipients.add(checkbox.getText());
                    }
                }
            }

            String subject = subjectField.getText();
            String message = messageArea.getText();
            SimpleStringProperty subjectText = new SimpleStringProperty(subject);
            SimpleStringProperty messageText = new SimpleStringProperty(message);
            SimpleStringProperty triggerText = new SimpleStringProperty(trigger);
            SimpleStringProperty statusText = new SimpleStringProperty("");

            if (subject.isBlank() || recipients.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a subject and select at least one recipient.", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            // Handle Send Now logic
            if ("Send Now".equals(trigger)) {
                boolean success = sendEmail(subject, message, recipients);
                String statusMessage = success ? "Sent Successfully" : "Failed to Send";
                statusText.set(statusMessage);

                Alert alert1 = new Alert(success 
                    ? Alert.AlertType.INFORMATION 
                    : Alert.AlertType.ERROR,
                    success ? "Emails sent successfully." : "Failed to send emails.",
                    ButtonType.OK
                );
                alert1.showAndWait();
            } else {
                statusText.set("Active");
            }

            // Create and save the reward
            EmailReward reward = new EmailReward(messageText, subjectText, triggerText, recipients, statusText);
            rewardList.add(reward);

            try (Connection conn = DatabaseConnection.getConnection()) {
                DatabaseServices.saveRewardToDatabase(conn, reward);
                System.out.println("Reward saved successfully.");
            } catch (SQLException e1) {
                e1.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Failed to save the reward.", ButtonType.OK);
                errorAlert.showAndWait();
            }

            popupStage.close();
        });

        // Cancel button
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
        rewardTriggerService.triggerRaffleReward();
        refreshRewards(rewardList);
    }

	
	public void deleteSelectedReward(TableView<EmailReward> rewardsTable, ObservableList<EmailReward> rewardList) {
	    EmailReward selected = rewardsTable.getSelectionModel().getSelectedItem();
	    if (selected != null) {
	        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
	                "Are you sure you want to delete this reward?",
	                ButtonType.YES, ButtonType.NO);
	        confirm.setHeaderText("Confirm Delete");
	        Optional<ButtonType> result = confirm.showAndWait();
	        
	        if (result.isPresent() && result.get() == ButtonType.YES) {
	            // Delete from database
	            String deleteSQL = "DELETE FROM rewards WHERE id = ?";
	            try (Connection conn = DatabaseConnection.getConnection();
	                 PreparedStatement stmt = conn.prepareStatement(deleteSQL)) {

	                stmt.setInt(1, selected.getId());
	                int rowsDeleted = stmt.executeUpdate();

	                if (rowsDeleted > 0) {
	                    rewardList.remove(selected);
	                    System.out.println("Reward deleted successfully.");
	                } else {
	                    System.out.println("No reward found with that ID.");
	                }

	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	    } else {
	        Alert alert = new Alert(Alert.AlertType.WARNING,
	                "Please select a reward to delete.",
	                ButtonType.OK);
	        alert.setHeaderText("No Selection");
	        alert.showAndWait();
	    }
	}
	
	public static boolean sendEmail(String subject, String message, List<String> selectedTiers) {
	    boolean success = false;
	    try {
	        URL url = new URL("http://localhost:8080/send-email");
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("POST");
	        conn.setDoOutput(true);
	        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

	        StringJoiner sj = new StringJoiner("&");
	        sj.add("subject=" + URLEncoder.encode(subject, "UTF-8"));
	        sj.add("messageBody=" + URLEncoder.encode(message, "UTF-8"));

	        for (String tier : selectedTiers) {
	            sj.add("selectedTiers=" + URLEncoder.encode(tier, "UTF-8"));
	        }

	        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
	        try (OutputStream os = conn.getOutputStream()) {
	            os.write(out);
	        }

	        int responseCode = conn.getResponseCode();
	        conn.disconnect();
	        success = responseCode == HttpURLConnection.HTTP_OK;

	        // Update reward status in the database
	        try (Connection connDB = DatabaseConnection.getConnection()) {
	            String updateStatusQuery = "UPDATE rewards SET status = ? WHERE subject = ? AND message = ?";
	            try (PreparedStatement stmt = connDB.prepareStatement(updateStatusQuery)) {
	                stmt.setString(1, success ? "Sent Successfully" : "Failed to Send");
	                stmt.setString(2, subject);
	                stmt.setString(3, message);
	                stmt.executeUpdate();
	                System.out.println("Reward status updated successfully.");
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return success;
	}

	
	public void checkRaffle() {
		rewardTriggerService.triggerRaffleReward();
	}
	
	private void refreshRewards(List<EmailReward> rewardList) {
	    try (Connection conn = DatabaseConnection.getConnection()) {
	        rewardList.clear();
	        rewardList.addAll(loadRewardsFromDatabase(conn));
	        System.out.println("Reward list refreshed.");
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public static List<EmailReward> loadRewardsFromDatabase(Connection conn) throws SQLException {
	    List<EmailReward> rewardList = new ArrayList<>();
	    String query = "SELECT id, subject, message, trigger, recipients, status FROM rewards";

	    try (PreparedStatement statement = conn.prepareStatement(query);
	         ResultSet resultSet = statement.executeQuery()) {

	        while (resultSet.next()) {
	            String subject = resultSet.getString("subject");
	            String message = resultSet.getString("message");
	            String trigger = resultSet.getString("trigger");
	            String recipientsString = resultSet.getString("recipients");
	            String status = resultSet.getString("status");

	            List<String> recipients = Arrays.asList(recipientsString.split(",\\s*"));
	            EmailReward reward = new EmailReward(
	                new SimpleStringProperty(subject),
	                new SimpleStringProperty(message),
	                new SimpleStringProperty(trigger),
	                recipients,
	                new SimpleStringProperty(status)
	            );
	            rewardList.add(reward);
	        }
	    }

	    return rewardList;
	}
}

