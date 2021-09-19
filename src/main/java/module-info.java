module fgotracker {
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
    requires java.desktop;
    requires javafx.swing;
    requires javafx.web;
    requires github.api;
    requires com.opencsv;

    opens com.github.theintelligentone.fgotracker.ui.controller to javafx.fxml;
    exports com.github.theintelligentone.fgotracker;
}
