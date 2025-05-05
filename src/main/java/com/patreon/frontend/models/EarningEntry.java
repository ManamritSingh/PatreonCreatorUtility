package com.patreon.frontend.models;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.Month;
import java.time.YearMonth;

public class EarningEntry {

    private final SimpleStringProperty month;
    private final SimpleIntegerProperty year;
    private final SimpleDoubleProperty total; //before fees
    private final SimpleDoubleProperty webMembershipCharges;
    private final SimpleDoubleProperty iOSMembershipCharges;
    private final SimpleDoubleProperty webGiftCharges;
    private final SimpleDoubleProperty iOSGiftCharges;
    private final SimpleDoubleProperty earnings;
    private final SimpleDoubleProperty processingFee;
    private final SimpleDoubleProperty patreonFee;
    private final SimpleDoubleProperty iOSFee;
    private final SimpleDoubleProperty merchShipping;
    private final SimpleDoubleProperty declines;
    private final SimpleDoubleProperty percentMembershipEarnings;
    private final SimpleDoubleProperty percentMembershipProcessingFees;
    private final SimpleDoubleProperty percentMembershipPatreonFees;
    private final SimpleDoubleProperty percentGiftEarnings;
    private final SimpleDoubleProperty percentGiftProcessingFees;
    private final SimpleDoubleProperty percentGiftPatreonFees;
    private final SimpleDoubleProperty currencyConversionFee;
    private final SimpleDoubleProperty currencyConversionFeePercent;
    private final SimpleStringProperty currency;

    public EarningEntry(String monthYear, SimpleDoubleProperty total,
                        SimpleDoubleProperty webMembershipCharges, SimpleDoubleProperty iOSMembershipCharges,
                        SimpleDoubleProperty webGiftCharges, SimpleDoubleProperty iOSGiftCharges, SimpleDoubleProperty earnings,
                        SimpleDoubleProperty processingFee, SimpleDoubleProperty patreonFee, SimpleDoubleProperty iOSFee,
                        SimpleDoubleProperty merchShipping, SimpleDoubleProperty declines,
                        SimpleDoubleProperty percentMembershipEarnings, SimpleDoubleProperty percentMembershipProcessingFees,
                        SimpleDoubleProperty percentMembershipPatreonFees, SimpleDoubleProperty percentGiftEarnings,
                        SimpleDoubleProperty percentGiftProcessingFees, SimpleDoubleProperty percentGiftPatreonFees,
                        SimpleDoubleProperty currencyConversionFee, SimpleDoubleProperty currencyConversionFeePercent,
                        SimpleStringProperty currency) {
        super();

        YearMonth ym = YearMonth.parse(monthYear);
        this.month = new SimpleStringProperty(Month.of(ym.getMonthValue()).name());
        this.year = new SimpleIntegerProperty(ym.getYear());
        this.total = total;
        this.webMembershipCharges = webMembershipCharges;
        this.iOSMembershipCharges = iOSMembershipCharges;
        this.webGiftCharges = webGiftCharges;
        this.iOSGiftCharges = iOSGiftCharges;
        this.earnings = earnings;
        this.processingFee = processingFee;
        this.patreonFee = patreonFee;
        this.iOSFee = iOSFee;
        this.merchShipping = merchShipping;
        this.declines = declines;
        this.percentMembershipEarnings = percentMembershipEarnings;
        this.percentMembershipProcessingFees = percentMembershipProcessingFees;
        this.percentMembershipPatreonFees = percentMembershipPatreonFees;
        this.percentGiftEarnings = percentGiftEarnings;
        this.percentGiftProcessingFees = percentGiftProcessingFees;
        this.percentGiftPatreonFees = percentGiftPatreonFees;
        this.currencyConversionFee = currencyConversionFee;
        this.currencyConversionFeePercent = currencyConversionFeePercent;
        this.currency = currency;
    }
    
    public EarningEntry(
    	    String month, int year, double total, double webMembershipCharges,
    	    double iOSMembershipCharges, double webGiftCharges, double iOSGiftCharges,
    	    double earnings, double processingFee, double patreonFee, double iOSFee,
    	    double merchShipping, double declines, double percentMembershipEarnings,
    	    double percentMembershipProcessingFees, double percentMembershipPatreonFees,
    	    double percentGiftEarnings, double percentGiftProcessingFees,
    	    double percentGiftPatreonFees, double currencyConversionFee,
    	    double currencyConversionFeePercent, String currency
    	) {
    	    this.month = new SimpleStringProperty(month);
    	    this.year = new SimpleIntegerProperty(year);
    	    this.total = new SimpleDoubleProperty(total);
    	    this.webMembershipCharges = new SimpleDoubleProperty(webMembershipCharges);
    	    this.iOSMembershipCharges = new SimpleDoubleProperty(iOSMembershipCharges);
    	    this.webGiftCharges = new SimpleDoubleProperty(webGiftCharges);
    	    this.iOSGiftCharges = new SimpleDoubleProperty(iOSGiftCharges);
    	    this.earnings = new SimpleDoubleProperty(earnings);
    	    this.processingFee = new SimpleDoubleProperty(processingFee);
    	    this.patreonFee = new SimpleDoubleProperty(patreonFee);
    	    this.iOSFee = new SimpleDoubleProperty(iOSFee);
    	    this.merchShipping = new SimpleDoubleProperty(merchShipping);
    	    this.declines = new SimpleDoubleProperty(declines);
    	    this.percentMembershipEarnings = new SimpleDoubleProperty(percentMembershipEarnings);
    	    this.percentMembershipProcessingFees = new SimpleDoubleProperty(percentMembershipProcessingFees);
    	    this.percentMembershipPatreonFees = new SimpleDoubleProperty(percentMembershipPatreonFees);
    	    this.percentGiftEarnings = new SimpleDoubleProperty(percentGiftEarnings);
    	    this.percentGiftProcessingFees = new SimpleDoubleProperty(percentGiftProcessingFees);
    	    this.percentGiftPatreonFees = new SimpleDoubleProperty(percentGiftPatreonFees);
    	    this.currencyConversionFee = new SimpleDoubleProperty(currencyConversionFee);
    	    this.currencyConversionFeePercent = new SimpleDoubleProperty(currencyConversionFeePercent);
    	    this.currency = new SimpleStringProperty(currency);
    	}


    public SimpleStringProperty getMonth() {
        return month;
    }

    public SimpleIntegerProperty getYear() {
        return year;
    }

    public SimpleDoubleProperty getTotal() {
        return total;
    }

    public SimpleDoubleProperty getWebMembershipCharges() {
        return webMembershipCharges;
    }

    public SimpleDoubleProperty getiOSMembershipCharges() {
        return iOSMembershipCharges;
    }

    public SimpleDoubleProperty getWebGiftCharges() {
        return webGiftCharges;
    }

    public SimpleDoubleProperty getiOSGiftCharges() {
        return iOSGiftCharges;
    }

    public SimpleDoubleProperty getEarnings() {
        return earnings;
    }

    public SimpleDoubleProperty getProcessingFee() {
        return processingFee;
    }

    public SimpleDoubleProperty getPatreonFee() {
        return patreonFee;
    }

    public SimpleDoubleProperty getiOSFee() {
        return iOSFee;
    }

    public SimpleDoubleProperty getMerchShipping() {
        return merchShipping;
    }

    public SimpleDoubleProperty getDeclines() {
        return declines;
    }

    public SimpleDoubleProperty getPercentMembershipEarnings() {
        return percentMembershipEarnings;
    }

    public SimpleDoubleProperty getPercentMembershipProcessingFees() {
        return percentMembershipProcessingFees;
    }

    public SimpleDoubleProperty getPercentMembershipPatreonFees() {
        return percentMembershipPatreonFees;
    }

    public SimpleDoubleProperty getPercentGiftEarnings() {
        return percentGiftEarnings;
    }

    public SimpleDoubleProperty getPercentGiftProcessingFees() {
        return percentGiftProcessingFees;
    }

    public SimpleDoubleProperty getPercentGiftPatreonFees() {
        return percentGiftPatreonFees;
    }

    public SimpleDoubleProperty getCurrencyConversionFee() {
        return currencyConversionFee;
    }

    public SimpleDoubleProperty getCurrencyConversionFeePercent() {
        return currencyConversionFeePercent;
    }

    public SimpleStringProperty getCurrency() {
        return currency;
    }

    public String getMonthValue() {
        return month.get();
    }

    public int getYearValue() {
        return year.get();
    }

    public double getTotalValue() {
        return total.get();
    }

    public double getEarningsValue() {
        return earnings.get();
    }

}
