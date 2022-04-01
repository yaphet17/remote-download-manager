module downloadmanager {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires undertow.core;

    opens downloadmanager to javafx.fxml;
    exports downloadmanager;
}