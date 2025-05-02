package com.patreon.frontend.models;

import javafx.beans.property.*;

public class PostEntry {

    private final SimpleStringProperty title;
    private final SimpleIntegerProperty totalImpressions;
    private final SimpleIntegerProperty likes;
    private final SimpleIntegerProperty comments;
    private final SimpleIntegerProperty newFreeMembers;
    private final SimpleIntegerProperty newPaidMembers;
    private final SimpleStringProperty publishedDateTime;
    private final SimpleStringProperty link;

    public PostEntry(SimpleStringProperty title, SimpleIntegerProperty totalImpressions, SimpleIntegerProperty likes,
                     SimpleIntegerProperty comments, SimpleIntegerProperty newFreeMembers, SimpleIntegerProperty newPaidMembers,
                     SimpleStringProperty publishedDateTime, SimpleStringProperty link) {
        super();
        this.title = title;
        this.totalImpressions = totalImpressions;
        this.likes = likes;
        this.comments = comments;
        this.newFreeMembers = newFreeMembers;
        this.newPaidMembers = newPaidMembers;
        this.publishedDateTime = publishedDateTime;
        this.link = link;
    }

    public SimpleStringProperty getTitle() {
        return title;
    }

    public SimpleIntegerProperty getTotalImpressions() {
        return totalImpressions;
    }

    public SimpleIntegerProperty getLikes() {
        return likes;
    }

    public SimpleIntegerProperty getComments() {
        return comments;
    }

    public SimpleIntegerProperty getNewFreeMembers() {
        return newFreeMembers;
    }

    public SimpleIntegerProperty getNewPaidMembers() {
        return newPaidMembers;
    }

    public SimpleStringProperty getPublishedDateTime() {
        return publishedDateTime;
    }

    public SimpleStringProperty getLink() {
        return link;
    }
}