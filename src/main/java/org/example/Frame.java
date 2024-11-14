package org.example;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;

public class Frame extends JFrame {
    private JButton SELECTIMAGEButton;
    private JPanel panelMain;
    private JComboBox<String> comboBoxExtensionFiles;
    private JButton CONVERTButton;
    private JLabel labelNoImgSelected;
    private JLabel status;

    public Frame() {
        this.setTitle("Image Converter");
        this.setContentPane(this.panelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(350, 250);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        SELECTIMAGEButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("jpg, jpeg, png, pdf", "jpg", "jpeg", "png", "pdf");
            fileChooser.setFileFilter(filter);
            int response = fileChooser.showOpenDialog(new JFrame());
            if (response == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String extension = getImgExtension(file.getName());

                if (!isValidExtension(extension)) {
                    labelNoImgSelected.setText("invalid file type selected");
                } else {
                    labelNoImgSelected.setText(file.getAbsolutePath());
                    CONVERTButton.setEnabled(true);
                }
            }
        });
        CONVERTButton.addActionListener(e -> {
            String desireExtension = (String) comboBoxExtensionFiles.getSelectedItem();
            String targetImage = labelNoImgSelected.getText();
            convertImage(targetImage, desireExtension, getImgExtension(targetImage));
        });
    }

    private void convertImage(String fileName, String desireExtension, String fileExtension) {
        if (desireExtension.equalsIgnoreCase(fileExtension)) {
            status.setText("you cant convert to the same extension dawgg");
            return;
        }

        try {
            File inputFile = new File(fileName);
            String baseName = (fileName.lastIndexOf('.') == -1) ? fileName : fileName.substring(0, fileName.lastIndexOf('.'));
            File outputFile = new File(baseName + "." + desireExtension.toLowerCase());
            BufferedImage image = ImageIO.read(inputFile);

            if (desireExtension.equalsIgnoreCase("jpg") ||
                    desireExtension.equalsIgnoreCase("jpeg") ||
                    desireExtension.equalsIgnoreCase("png")) {
                if (fileExtension.equalsIgnoreCase("pdf")) {
                    PdfToImg(fileName, baseName, desireExtension);
                } else {
                    ImageIO.write(image, desireExtension.toLowerCase(), outputFile);
                }
                status.setText("success! saved at : " + baseName + "." + desireExtension.toLowerCase());
            } else if (desireExtension.equalsIgnoreCase("pdf")) {
                convertToPdf(fileName, baseName, image);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void PdfToImg(String fileName, String baseName, String desireExtension) {
        try {
            PDDocument document = Loader.loadPDF(new File(fileName));
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 300); // Page index 0, 300 DPI
            ImageIO.write(bufferedImage, desireExtension.toLowerCase(), new File(baseName + "." + desireExtension.toLowerCase()));
            status.setText("success! saved at : " + baseName + "." + desireExtension.toLowerCase());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void convertToPdf(String fileName, String baseName, BufferedImage image) {
        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage(new PDRectangle(image.getWidth(), image.getHeight()));
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            PDImageXObject pdImage = PDImageXObject.createFromFile(fileName, document);
            contentStream.drawImage(pdImage, 0, 0, image.getWidth(), image.getHeight());
            contentStream.close();

            document.save(baseName + ".pdf");
            document.close();
            status.setText("success! saved at : " + baseName + ".pdf");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private String getImgExtension(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }
        int dotIndex = filePath.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == filePath.length() - 1) {
            return "";
        }
        return filePath.substring(dotIndex + 1);
    }

    private boolean isValidExtension(String extension) {
        String[] validExtensions = { "jpg", "jpeg", "png", "pdf" };
        for (String validExtension : validExtensions) {
            if (extension.equals(validExtension)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.out.println(e);
        }

        new Frame();
    }
}
