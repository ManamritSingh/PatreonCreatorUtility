package com.patreon.frontend.utils;

import com.patreon.frontend.models.EarningEntry;
import com.patreon.frontend.models.EmailReward;
import com.patreon.frontend.models.PostEntry;
import com.patreon.frontend.models.SurveyEntry;
import com.patreon.frontend.models.UserEntry;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TableCreator {
	
	@SuppressWarnings("unchecked")
	public void setupEarningTableColumns(TableView<EarningEntry> earningTable) {
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
	
	@SuppressWarnings("unchecked")
	public void setupPostTableColumns(TableView<PostEntry> postTable) {
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
	
	@SuppressWarnings("unchecked")
	public void setupSurveyTableColumns(TableView<SurveyEntry> surveyTable) {
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
	
	@SuppressWarnings("unchecked")
	public void setupUserTableColumns(TableView<UserEntry> userTable) {
    	TableColumn<UserEntry, String> userIDCol = new TableColumn<>("User ID");
        userIDCol.setCellValueFactory(cellData -> cellData.getValue().getUserID());
        
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
        
        ObservableList<TableColumn<UserEntry, String>> columns = FXCollections.observableArrayList();
        columns.addAll(userIDCol,firstNameCol, lastNameCol, emailCol, activeCol, tierCol, pledgeCol,
        		addressNameCol, addressLine1Col, addressLine2Col, cityCol, stateCol, zipCodeCol,
        		countryCol,genderCol, ageRangeCol, educationCol, incomeRangeCol, raffleEligibleCol);

        userTable.getColumns().addAll(columns);
    }
	
	@SuppressWarnings("unchecked")
	public void setupRewardsTableColumns(TableView<EmailReward> rewardsTable) {
    	TableColumn<EmailReward, String> triggerCol = new TableColumn<>("Trigger");
    	triggerCol.setCellValueFactory(cellData -> cellData.getValue().getTriggerOpt());
    	
    	TableColumn<EmailReward, String> recipientsCol = new TableColumn<>("Recipients");
    	recipientsCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.join(", ", cellData.getValue().getRecepients())));
    	
    	TableColumn<EmailReward, String> subjectCol = new TableColumn<>("Email Subject");
    	subjectCol.setCellValueFactory(cellData -> cellData.getValue().getSubject());
    	
    	TableColumn<EmailReward, String> messageCol = new TableColumn<>("Email Message");
    	messageCol.setCellValueFactory(cellData -> cellData.getValue().getMessage());
    	
    	TableColumn<EmailReward, String> statusCol = new TableColumn<>("Status");
    	statusCol.setCellValueFactory(cellData -> cellData.getValue().getStatus());
    	
    	rewardsTable.getColumns().addAll(triggerCol, recipientsCol, subjectCol, messageCol, statusCol);
    }
}
