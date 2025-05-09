package com.patreon.frontend.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.patreon.backend.RewardTriggerService;
import com.patreon.frontend.models.EarningEntry;
import com.patreon.frontend.models.EmailReward;
import com.patreon.frontend.models.PostEntry;
import com.patreon.frontend.models.SurveyEntry;
import com.patreon.frontend.models.UserEntry;
import com.patreon.utils.DatabaseConnection;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;

@Controller
public class CSVParser {
	@Autowired
    private RewardTriggerService rewardTriggerService;
	
	public void parseEarningsCSV(File file, TableView<EarningEntry> earningTable, ObservableList<EarningEntry> earningData ){
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
                DatabaseServices.saveEarningsToDatabase(conn, earningData);
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
	
	
	public void parsePostsCSV(File file, TableView<PostEntry> postTable, ObservableList<PostEntry> postData){
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
                DatabaseServices.savePostToDatabase(conn, postData);
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

	public void parseSurveysCSV(File file, TableView<SurveyEntry> surveyTable, ObservableList<SurveyEntry> surveyData) {
	    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
	        String line;
	        String header = reader.readLine();

	        if (header == null) {
	            showAlert("Error", "The file is empty.");
	            return;
	        }

	        // Expected column headers
	        String[] expectedHeaders = {
	            "Submitted At (UTC)", "Name", "Email", "Tier", "Survey Choice", "Additional Comment"
	        };

	        // Normalize and split header
	        String[] actualHeaders = header.toLowerCase().split(",", -1);

	        // Validate CSV headers
	        for (String expected : expectedHeaders) {
	            boolean found = Arrays.stream(actualHeaders)
	                    .anyMatch(h -> h.trim().contains(expected.toLowerCase()));
	            if (!found) {
	                showAlert("Invalid File", "This doesn't appear to be a valid surveys CSV.");
	                return;
	            }
	        }

	        // Initialize new entries list
	        ArrayList<SurveyEntry> newEntries = new ArrayList<>();

	        // Load existing emails from the database
	        Set<String> existingEmails = DatabaseServices.getExistingSurveyEmails();

	        // Check for active "Survey Completion" rewards
	        ArrayList<EmailReward> activeSurveyRewards = DatabaseServices.getActiveSurveyRewards();
	        if (activeSurveyRewards.isEmpty()) {
	            showAlert("No Active Rewards", "No active 'Survey Completion' rewards found.");
	            return;
	        }

	        // Parse each line in the CSV
	        while ((line = reader.readLine()) != null) {
	            String[] tokens = line.split(",", -1);
	            String email = tokens[2].trim();
	            String tier = tokens[3].trim();

	            // Process only new survey entries (not existing emails)
	            if (!existingEmails.contains(email)) {
	                // Create new SurveyEntry object
	                SurveyEntry entry = new SurveyEntry(
	                        new SimpleStringProperty(tokens[0].trim()),  // Submitted At (UTC)
	                        new SimpleStringProperty(tokens[1].trim()),  // Name
	                        new SimpleStringProperty(email),             // Email
	                        new SimpleStringProperty(tier),              // Tier
	                        new SimpleStringProperty(tokens[4].trim()),  // Survey Choice
	                        new SimpleStringProperty(tokens[5].trim())   // Additional Comment
	                );

	                // Add the new entry to both the list and the ObservableList
	                newEntries.add(entry);
	                surveyData.add(entry);

	                // Check if the user is eligible for any rewards based on their tier
	                for (EmailReward reward : activeSurveyRewards) {
	                    if (reward.getRecepients().contains(tier)) {
	                        // Trigger the reward for this survey entry
	                        rewardTriggerService.handleSurveyCompletion(email, tier);;
	                        System.out.println("Survey reward sent to: " + email + " (Tier: " + tier + ")");
	                    }
	                }
	            }
	        }

	        // Save only new entries to the database
	        if (!newEntries.isEmpty()) {
	            try (Connection conn = DatabaseConnection.getConnection()) {
	                DatabaseServices.saveSurveyToDatabase(conn, newEntries);
	                System.out.println("Survey data saved successfully.");
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }

	        // Update the TableView with the latest survey data
	        surveyTable.setItems(surveyData);

	    } catch (IOException e) {
	        e.printStackTrace();
	        showAlert("File Error", "Could not read the file.");
	    } catch (Exception e) {
	        e.printStackTrace();
	        showAlert("Parsing Error", "There was an error while parsing the CSV.");
	    }
	}


	
	public void parseUserCSV(File file, TableView<UserEntry> userTable, ObservableList<UserEntry> userData) {
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
                        new SimpleStringProperty(tokens[0].trim()),
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
                DatabaseServices.saveUserToDatabase(conn, userData);
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
	
	private double parsePercent(String percentString) {
        return Double.parseDouble(percentString.replace("%", "").trim());
    }
	
	private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
