package com.patreon.frontend.utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

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

public class RewardController {

	public void newReward(List<EmailReward> rewardList) {

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
            
            if ("Send Now".equals(trigger)) {
                List<String> selectedTiers = new ArrayList<>();
                if (everyone.isSelected()) selectedTiers.add("All");
                if (tier1.isSelected()) selectedTiers.add("1");
                if (tier2.isSelected()) selectedTiers.add("2");
                if (tier3.isSelected()) selectedTiers.add("3");

                boolean success = sendEmail(subject, message, selectedTiers);

                Alert alert1 = new Alert(success 
                    ? Alert.AlertType.INFORMATION 
                    : Alert.AlertType.ERROR,
                    success ? "Emails sent successfully." : "Failed to send emails.",
                    ButtonType.OK
                );
                alert1.showAndWait();
            }

            EmailReward reward = new EmailReward(messageText, subjectText, triggerText, recipients);
            rewardList.add(reward);
            try (Connection conn = DatabaseConnection.getConnection()) {
                DatabaseServices.saveRewardToDatabase(conn, reward);
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
        try {
            URL url = new URL("http://localhost:8080/send-email"); // Adjust if needed
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
            OutputStream os = conn.getOutputStream();
            os.write(out);

            int responseCode = conn.getResponseCode();
            conn.disconnect();
            return responseCode == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

