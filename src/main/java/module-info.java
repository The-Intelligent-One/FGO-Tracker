module fgotracker {
    requires java.desktop;
    requires javafx.swing;
    requires javafx.web;
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.beans;
    requires spring.boot;
    requires net.rgielen.fxweaver.core;
    requires net.rgielen.fxweaver.spring;
    requires net.rgielen.fxweaver.spring.boot.autoconfigure;
    requires lombok;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires org.kohsuke.github.api;
    requires com.opencsv;
    requires org.controlsfx.controls;

    opens com.github.theintelligentone.fgotracker.ui.controller to javafx.fxml;
    exports com.github.theintelligentone.fgotracker;
}
