package base.fx;

import base.utils.ImageFileHandler;
import base.utils.wrapers.IFrameWrapper;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConverterBindFx {
    ImageFileHandler imageFileHandler = new ImageFileHandler();

    MainController controller;

    public ConverterBindFx(MainController controller) {
        this.controller = controller;
    }

    public void convert(List<File> listFiles, IFrameWrapper imageWrapper, double oLeft, double oRight, double oTop, double oBottom) {
        List<String> invalidFiles = new ArrayList<>();
        Path destFolder = Paths.get("framed_img");
        for (File file : listFiles
        ) {
            Platform.runLater(() -> controller.getStatusBar().setText("����������� " + file.getName()));
            Image source = null;
            try {
                source = imageFileHandler.openImageFile(file);
            } catch (FileNotFoundException fileNotFoundException) {
                invalidFiles.add(file.getName());
            }
            Image result = imageWrapper.wrapImage(source, oLeft, oRight, oTop, oBottom);
            boolean successful = imageFileHandler.saveImage(result, destFolder, file.getName(), "jpg");
            System.out.println(successful ? "image have been saved" : "something get wrong");
        }
        if (!invalidFiles.isEmpty()) {
            Platform.runLater(() -> controller.getStatusBar().setText("��������� �� ������ �� ������� �������"));
        } else {
            Platform.runLater(() -> notifyFinished(destFolder));
        }
    }

    private void notifyFinished(Path folder) { //todo ��� �� ���������
        controller.getStatusBar().setText("��������");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Finished!");
        alert.setHeaderText("��� �������� ���� ���������");
        ButtonType openFolder = new ButtonType("Open folder");

        alert.getButtonTypes().clear();

        alert.getButtonTypes().addAll(openFolder, ButtonType.CLOSE);
        Optional<ButtonType> option = alert.showAndWait();
        controller.getConvertButton().setDisable(false);
        if (option.isPresent() && option.get() == openFolder) {
            try {
                Desktop.getDesktop().open(folder.toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
