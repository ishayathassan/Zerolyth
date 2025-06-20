module com.example.zerolyth {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.zerolyth to javafx.fxml;
    exports com.example.zerolyth;

    opens com.example.zerolyth.puzzle to javafx.fxml;
    exports com.example.zerolyth.puzzle;
}