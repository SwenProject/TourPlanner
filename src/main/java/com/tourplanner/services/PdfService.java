package com.tourplanner.services;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.WebColors;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import com.itextpdf.layout.properties.VerticalAlignment;
import com.tourplanner.models.Tour;
import com.itextpdf.kernel.pdf.*;
import com.tourplanner.models.TourLog;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PdfService {

    //set the custom font for the pdf
    String fontPath = "./src/main/resources/com/tourplanner/fonts/jetbrains_mono/JetBrainsMono-Regular.ttf";

    PdfFont customFont;
    {
        try {
            customFont = PdfFontFactory.createFont(fontPath, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createPdfSummary(ArrayList<Tour> tours, String path){

        try(PdfWriter writer = new PdfWriter(path)) {

            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.setFont(customFont);

            //TITLE PAGE
            //add logo image
            document.add(new Image(ImageDataFactory.create("./src/main/resources/com/tourplanner/images/app_icon.png")).setMaxHeight(50).setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER).setMarginTop(10));
            //add title
            Paragraph title = new Paragraph("Summarize Report").setFontSize(20).setBold().setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER).setFontColor(ColorConstants.CYAN, 0.7f);
            document.add(title);

            //add subtitle
            Paragraph subtitle = new Paragraph("This is a summary of following tours. " +
                    "Displaying start, destination, distance, average duration, transport type, " +
                    "description and average rating. \n the average duration is calculated from the tour logs of each Tour.\n \n The following Tours are displayed:")
                    .setFontSize(15).setItalic().setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER);
            document.add(subtitle);

            //create table for tour names

            Table content = new Table(UnitValue.createPercentArray(1)).setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
            content.setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER).setBorder(Border.NO_BORDER);
            content.setMarginTop(20);
            int row = 0;
            int numberOfTours = tours.size();

            for (Tour tour : tours) {
                Cell cell = new Cell().setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(ColorConstants.CYAN, 1f)).setBorderRight(new SolidBorder(ColorConstants.CYAN, 1f)).setPadding(10);

                if (row == 0) {
                    content.addCell(cell.add(new Paragraph(tour.getName()))
                            .setBorderTop(new SolidBorder(ColorConstants.CYAN, 1f)));
                } else if (row == numberOfTours - 1) {
                    content.addCell(cell.add(new Paragraph(tour.getName()))
                            .setBorderBottom(new SolidBorder(ColorConstants.CYAN, 1f)));
                } else {
                    content.addCell(cell.add(new Paragraph(tour.getName())));
                }

                row++;
            }

            document.add(content);
            document.add(new AreaBreak());

            //TOUR PAGES
            //for each tour in tours
            for (Tour tour : tours) {

                Duration averageDuration = calculateAverageDuration(tour.getTourLogs());

                //add the tour name to the page
                Paragraph header = new Paragraph(tour.getName())
                        .setFontSize(15).setBold().setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER);

                document.add(header);

                Path pathToImage = Paths.get(tour.getPathToMapImage());
                File imageFile = pathToImage.toFile();

                //add the tour map image to the page
                document.add(new Image(ImageDataFactory.create(imageFile.toURI().toString())).setMaxHeight(350).setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER));


                //create table for tour details

                Table table = new Table(UnitValue.createPercentArray(new float[]{30, 70})).useAllAvailableWidth();
                table.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
                table.setMarginTop(20);
                //add the tour details to the table
                //Starting Point
                table.addCell(new Cell().add(new Paragraph("Start")).setBackgroundColor(ColorConstants.CYAN, 0.2f));
                table.addCell(new Cell().add(new Paragraph(tour.getStartingPoint())).setBackgroundColor(ColorConstants.CYAN, 0.2f).setTextAlignment(TextAlignment.CENTER));
                //Destination
                table.addCell(new Cell().add(new Paragraph("Destination")).setBackgroundColor(ColorConstants.ORANGE, 0.2f));
                table.addCell(new Cell().add(new Paragraph(tour.getDestinationPoint())).setBackgroundColor(ColorConstants.ORANGE, 0.2f).setTextAlignment(TextAlignment.CENTER));
                //Distance
                table.addCell(new Cell().add(new Paragraph("Distance")).setBackgroundColor(ColorConstants.CYAN, 0.2f));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(tour.getDistance()))).setBackgroundColor(ColorConstants.CYAN, 0.2f).setTextAlignment(TextAlignment.CENTER));
                //Duration
                table.addCell(new Cell().add(new Paragraph("Duration")).setBackgroundColor(ColorConstants.ORANGE, 0.2f));
                String duration = String.format("%d:%02d", averageDuration.getSeconds() / 3600, (averageDuration.getSeconds() % 3600) / 60);
                table.addCell(new Cell().add(new Paragraph(duration)).setBackgroundColor(ColorConstants.ORANGE, 0.2f).setTextAlignment(TextAlignment.CENTER));
                //Transport Type
                table.addCell(new Cell().add(new Paragraph("Transport Type")).setBackgroundColor(ColorConstants.CYAN, 0.2f));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(tour.getTransportType()))).setBackgroundColor(ColorConstants.CYAN, 0.2f).setTextAlignment(TextAlignment.CENTER));
                //Description
                table.addCell(new Cell().add(new Paragraph("Description")).setBackgroundColor(ColorConstants.ORANGE, 0.2f));
                table.addCell(new Cell().add(new Paragraph(tour.getDescription())).setBackgroundColor(ColorConstants.ORANGE, 0.2f).setTextAlignment(TextAlignment.CENTER));
                //Rating (average)
                table.addCell(new Cell().add(new Paragraph("Rating")).setBackgroundColor(ColorConstants.CYAN, 0.2f));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(calculateAverageRating(tour.getTourLogs())))).setBackgroundColor(ColorConstants.CYAN, 0.2f).setTextAlignment(TextAlignment.CENTER));
                document.add(table);

                //create a new page
                document.add(new AreaBreak());

            }
            pdf.close();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    Duration calculateAverageDuration(List<TourLog> logs){
        Duration averageDuration = Duration.ZERO;
        for (TourLog log : logs) {
            averageDuration = averageDuration.plus(log.getTotalTime());
            System.out.println(log.getTotalTime());
            System.out.println(averageDuration);
        }
        System.out.println(logs.size());
        if(logs.size() > 0){
            averageDuration = averageDuration.dividedBy(logs.size());
        }
        else{
            averageDuration = Duration.ZERO;
        }
        System.out.println(averageDuration);
        return averageDuration;
    }

    //create PDF for single tour

    int calculateAverageRating(List<TourLog> logs){
        double averageRating = 0;
        for (TourLog log : logs) {
            averageRating += log.getRating();

        }
        if(logs.size() > 0){
            averageRating = averageRating / logs.size();
        }
        else{
            averageRating = 0;
        }
        return ((int) Math.round(averageRating));
    }


    public void createPdfSingleTour(Tour tour, String path){
        //String fontPath
        String fontPath = "./src/main/resources/com/tourplanner/fonts/jetbrains_mono/JetBrainsMono-Regular.ttf";

        //create the pdf
        try(PdfWriter writer = new PdfWriter(path)) {

            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.setFont(customFont);

            //add logo image
            Image logoImage = new Image(ImageDataFactory.create("./src/main/resources/com/tourplanner/images/app_icon.png")).setMaxHeight(20);
            //add header

            Table titleTable = new Table(UnitValue.createPercentArray(new float[]{95, 5})).useAllAvailableWidth();
            titleTable.setMarginTop(-20);
            titleTable.addCell(new Cell().add(new Paragraph("Tour Planner"))
                    .setFontSize(20).setBold()
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setBorder(Border.NO_BORDER));

            String subheader = tour.getStartingPoint() + " - " + tour.getDestinationPoint();
            titleTable.addCell(new Cell().add(logoImage).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            titleTable.addCell(new Cell().add(new Paragraph(subheader))
                    .setFontSize(12).setBold()
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setBorder(Border.NO_BORDER));
            document.add(titleTable);
            //subheader

            //add the tour map image
            Path pathToImage = Paths.get(tour.getPathToMapImage());
            File imageFile = pathToImage.toFile();
            document.add(new Image(ImageDataFactory.create(imageFile.toURI().toString())).setMaxHeight(500).setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER).setMarginTop(50));

            //create new page
            document.add(new AreaBreak());

            //table for tour details
            Table table = new Table(UnitValue.createPercentArray(new float[]{30, 70})).useAllAvailableWidth();
            table.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
            table.setMarginTop(20);
            table.setMarginBottom(20);
            //add the tour details to the table
            //Starting Point
            table.addCell(new Cell().add(new Paragraph("Start")).setBackgroundColor(WebColors.getRGBColor("#F2CC8F"), 0.5f).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph(tour.getStartingPoint())).setBackgroundColor(WebColors.getRGBColor("#F2CC8F"), 0.5f).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            //Destination
            table.addCell(new Cell().add(new Paragraph("Destination")).setBackgroundColor(WebColors.getRGBColor("#FFE0AF"), 0.5f).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph(tour.getDestinationPoint())).setBackgroundColor(WebColors.getRGBColor("#FFE0AF"), 0.5f).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            //Distance
            table.addCell(new Cell().add(new Paragraph("Distance")).setBackgroundColor(WebColors.getRGBColor("#F2CC8F"), 0.5f).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(tour.getDistance()))).setBackgroundColor(WebColors.getRGBColor("#F2CC8F"), 0.5f).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            //Duration
            table.addCell(new Cell().add(new Paragraph("Duration")).setBackgroundColor(WebColors.getRGBColor("#FFE0AF"), 0.5f).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(tour.getDuration()))).setBackgroundColor(WebColors.getRGBColor("#FFE0AF"), 0.5f).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            //Transport Type
            table.addCell(new Cell().add(new Paragraph("Transport Type")).setBackgroundColor(WebColors.getRGBColor("#F2CC8F"), 0.5f).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(tour.getTransportType()))).setBackgroundColor(WebColors.getRGBColor("#F2CC8F"), 0.5f).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            //Description
            table.addCell(new Cell().add(new Paragraph("Description")).setBackgroundColor(WebColors.getRGBColor("#FFE0AF"), 0.5f).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph(tour.getDescription())).setBackgroundColor(WebColors.getRGBColor("#FFE0AF"), 0.5f).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
            //Rating
            table.addCell(new Cell().add(new Paragraph("Rating")).setBackgroundColor(WebColors.getRGBColor("#F2CC8F"), 0.5f).setBorder(Border.NO_BORDER));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(tour.getRating()))).setBackgroundColor(WebColors.getRGBColor("#F2CC8F"), 0.5f).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));

            document.add(table);


            Paragraph logsTitle = new Paragraph("Tour Logs").setFontSize(15).setBold().setMarginTop(15);
            document.add(logsTitle);
            for(TourLog log : tour.getTourLogs()) {
                Paragraph p = new Paragraph().setBorder(new SolidBorder(1)).setBackgroundColor(WebColors.getRGBColor("#F4F1DE")).setPadding(5);
                //parse date
                Date date = log.getDate();
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyy HH:mm:ss"); //TODO: year gets displayed wrong
                String formattedDate = formatter.format(date);

                p.add(new Text(formattedDate).setItalic());
                p.add(new Text("\n"));
                p.add(new Text("Comment:\n").setFontSize(10).setItalic());
                p.add(new Text(log.getComment()));
                p.add(new Text("\n"));
                p.add(new Text("Total Time:\n").setFontSize(10).setItalic());
                String duration = String.format("%d:%02d", log.getTotalTime().getSeconds() / 3600, (log.getTotalTime().getSeconds() % 3600) / 60);
                p.add(new Text(duration));
                p.add(new Text("\n"));

                p.add(new Text("Difficulty:\n").setFontSize(10).setItalic());
                p.add(new Text(String.valueOf(log.getDifficulty().toString())));
                document.add(p);
            }


            pdf.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
