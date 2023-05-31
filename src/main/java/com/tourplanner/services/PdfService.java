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
import com.itextpdf.layout.properties.BackgroundImage;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.tourplanner.models.Tour;
import com.itextpdf.kernel.pdf.*;
import com.tourplanner.models.TourLog;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
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

    public void createPdfSummary(List<Tour> tours, String path) {

        try (PdfWriter writer = new PdfWriter(path)) {

            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.setFont(customFont);
            document.setFontColor(WebColors.getRGBColor("#3D405B"));

            //TITLE PAGE
            addSummaryTitlePage(document, tours);


            //create table for tour names
            Table content = new Table(UnitValue.createPercentArray(1))
                    .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
            content.setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setBorder(Border.NO_BORDER);
            content.setMarginTop(20);
            int row = 0;
            int numberOfTours = tours.size();

            for (Tour tour : tours) {
                Cell cell = new Cell().add(new Paragraph(tour.getName()));
                cell.setBorder(Border.NO_BORDER)
                        .setBorderLeft(new SolidBorder(WebColors.getRGBColor("#E07A5F"), 1f))
                        .setBorderRight(new SolidBorder(WebColors.getRGBColor("#E07A5F"), 1f))
                        .setPadding(10);
                if (row == 0) {
                    cell.setBorderTop(new SolidBorder(WebColors.getRGBColor("#E07A5F"), 1f));
                }
                if (row == numberOfTours - 1) {
                    cell.setBorderBottom(new SolidBorder(WebColors.getRGBColor("#E07A5F"), 1f));
                }
                content.addCell(cell);

                row++;
            }

            document.add(content);
//---------------------------------------------------------------
            //TOUR PAGES
            //for each tour in tours

            for (Tour tour : tours) {
                //create a new page
                document.add(new AreaBreak());

                //add the tour name to the page
                Paragraph header = new Paragraph(tour.getName())
                        .setFontSize(18)
                        .setBold()
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER);

                document.add(header);

                //add the tour start and destination to the page
                Paragraph startAndDestination = new Paragraph(tour.getStartingPoint() + " - " + tour.getDestinationPoint())
                        .setFontSize(14)
                        .setItalic()
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                        .setMarginBottom(20);
                document.add(startAndDestination);

                String pathToImageString = tour.getPathToMapImage();
                //if there is no Map image available
                if (pathToImageString == null || pathToImageString.equals("error") || pathToImageString.equals("loading")) {
                    Image noMapImage = new Image(ImageDataFactory.create("./src/main/resources/com/tourplanner/images/tour_map_error.png"))
                            .setMaxHeight(50)
                            .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER)
                            .setMarginTop(50);
                    document.add(noMapImage);
                    //create paragraph
                    Paragraph noMapParagraph = new Paragraph("No Map Image available for this Tour")
                            .setBold()
                            .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                            .setMarginTop(5)
                            .setFontColor(WebColors.getRGBColor("#3D405B"))
                            .setFontSize(12)
                            .setMarginBottom(40);

                    document.add(noMapParagraph);
                }
                //Map image available
                else {
                    Path pathToImage = Paths.get(tour.getPathToMapImage());
                    File imageFile = pathToImage.toFile();
                    document.add(new Image(ImageDataFactory.create(imageFile.toURI().toString())).setMaxHeight(500).setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER).setMarginTop(50));

                }
                document.add(new AreaBreak());

                Paragraph details = new Paragraph("Tour Details")
                        .setItalic()
                        .setFontSize(12)
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                        .setMarginTop(20)
                        .setMarginBottom(10);

                document.add(details);
                //create table for tour details


                Table detailsTable = createTableForDetails(tour);
                document.add(detailsTable);

            }
            pdf.close();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void addSummaryTitlePage(Document document, List<Tour> tours) {
        //add logo image
        try {
            document.add(new Image(ImageDataFactory.create("./src/main/resources/com/tourplanner/images/app_icon.png")).setMaxHeight(50).setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER).setMarginTop(10));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        //add title
        Paragraph title = new Paragraph("Summarize Report")
                .setFontSize(20).setBold().setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setFontColor(WebColors.getRGBColor("#E07A5F"));
        document.add(title);

        //add subtitle
        Paragraph subtitle = new Paragraph("This is a summary of following tours. " +
                "Displaying start, destination, distance, average duration, transport type, " +
                "description and average rating. \n the average duration is calculated from the tour logs of each Tour.\n \n The following Tours are displayed:")
                .setFontSize(15).setItalic().setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER);
        document.add(subtitle);


    }

    Duration calculateAverageDuration(List<TourLog> logs) {
        Duration averageDuration = Duration.ZERO;
        for (TourLog log : logs) {
            averageDuration = averageDuration.plus(log.getTotalTime());
            System.out.println(log.getTotalTime());
            System.out.println(averageDuration);
        }
        System.out.println(logs.size());
        if (logs.size() > 0) {
            averageDuration = averageDuration.dividedBy(logs.size());
        } else {
            averageDuration = Duration.ZERO;
        }
        System.out.println(averageDuration);
        return averageDuration;
    }



    int calculateAverageRating(List<TourLog> logs) {

        double averageRating = 0;
        //no tour Logs
        if (logs.size() == 0) {
            return 0;
        }
        for (TourLog log : logs) {
            averageRating += log.getRating();
        }
        if (logs.size() > 0) {
            averageRating = averageRating / logs.size();
        } else {
            averageRating = 0;
        }
        return ((int) Math.round(averageRating));
    }


    public void createPdfSingleTour(Tour tour, String path) {
        //String fontPath
        String fontPath = "./src/main/resources/com/tourplanner/fonts/jetbrains_mono/JetBrainsMono-Regular.ttf";

        //create the pdf
        try (PdfWriter writer = new PdfWriter(path)) {
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            document.setFont(customFont);
            document.setFontColor(WebColors.getRGBColor("#3D405B"));

            //add logo image
            Image logoImage = new Image(ImageDataFactory.create("./src/main/resources/com/tourplanner/images/app_icon.png")).setMaxHeight(40).setOpacity(0.7f);
            //add header

            Table titleTable = new Table(UnitValue.createPercentArray(new float[]{5, 90, 5})).useAllAvailableWidth();
            titleTable.setMarginTop(-20);
            titleTable.addCell(new Cell().setBorder(Border.NO_BORDER));
            titleTable.addCell(new Cell().add(new Paragraph(tour.getName()))
                    .setFontSize(20).setBold()
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setBorder(Border.NO_BORDER));

            titleTable.addCell(new Cell().add(logoImage).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER));
            titleTable.addCell(new Cell().setBorder(Border.NO_BORDER));
            String subheader = tour.getStartingPoint() + " - " + tour.getDestinationPoint();
            titleTable.addCell(new Cell().add(new Paragraph(subheader))
                    .setFontSize(12).setBold()
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setBorder(Border.NO_BORDER));

            document.add(titleTable);
            //subheader

            //add the tour map image
            String pathToImageString = tour.getPathToMapImage();

            //if there is no Map image available
            if (pathToImageString == null || pathToImageString.equals("error") || pathToImageString.equals("loading")) {
                Image noMapImage = new Image(ImageDataFactory.create("./src/main/resources/com/tourplanner/images/tour_map_error.png"))
                        .setMaxHeight(50)
                        .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER)
                        .setMarginTop(50)
                        .setOpacity(0.5f);

                document.add(noMapImage);
                //create paragraph
                Paragraph noMapParagraph = new Paragraph("No Map Image available for this Tour")
                        .setBold()
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                        .setMarginTop(5)
                        .setFontColor(WebColors.getRGBColor("#3D405B"))
                        .setMarginBottom(50)
                        .setOpacity(0.5f);
                document.add(noMapParagraph);
            }
            //Map image available
            else {
                Path pathToImage = Paths.get(tour.getPathToMapImage());
                File imageFile = pathToImage.toFile();
                document.add(new Image(ImageDataFactory.create(imageFile.toURI().toString()))
                        .setMaxHeight(500)
                        .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER)
                        .setMarginTop(50));
                //create new page
                document.add(new AreaBreak());
            }



            Table detailsTable = createTableForDetails(tour);
            document.add(detailsTable);

            //add logs
            Paragraph logsTitle = new Paragraph("Tour Logs").setFontSize(15).setBold().setMarginTop(15);
            document.add(logsTitle);
            createLogsParagraphs(tour.getTourLogs(), document);


            pdf.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createLogsParagraphs(List<TourLog> tourLogs, Document document) {
        if (tourLogs.size() > 0) {
            for (TourLog log : tourLogs) {
                Paragraph p = new Paragraph()
                        .setBorder(new SolidBorder(1))
                        .setBackgroundColor(WebColors.getRGBColor("#F4F1DE")).setPadding(5);
                //parse date
                Date date = log.getDate();
                SimpleDateFormat formatter = new SimpleDateFormat("dd MM YYYY HH:mm:ss");
                String formattedDate = formatter.format(date);

                p.add(new Text(formattedDate).setItalic());
                p.add(new Text("\n"));

                p.add(new Text("Comment:\n")
                        .setFontSize(10)
                        .setItalic());
                if(log.getComment() == null){
                    p.add(new Text(""));
                }else {
                    p.add(new Text(log.getComment()));
                }
                p.add(new Text("\n"));

                p.add(new Text("Total Time:\n")
                        .setFontSize(10)
                        .setItalic());
                p.add(new Text(log.getTotalTime().toString()));
                p.add(new Text("\n"));

                p.add(new Text("Difficulty:\n").setFontSize(10).setItalic());
                p.add(new Text(String.valueOf(log.getDifficulty().toString())));
                document.add(p);
            }
        } else {
            Image noLogsImage = null;
            try {
                noLogsImage = new Image(ImageDataFactory.create("./src/main/resources/com/tourplanner/images/no_logs.png"))
                        .setMaxHeight(50)
                        .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER)
                        .setMarginTop(50)
                        .setOpacity(0.5f);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            Paragraph noLogs = new Paragraph("No Tour Logs available for this Tour").setFontSize(12)
                    .setBold()
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setMarginTop(5)
                    .setFontColor(WebColors.getRGBColor("#3D405B"))
                    .setOpacity(0.5f);
            document.add(noLogsImage);
            document.add(noLogs);

        }
    }

    Table createTableForDetails(Tour tour) {
        Duration averageDuration = calculateAverageDuration(tour.getTourLogs());

        Table table = new Table(UnitValue.createPercentArray(new float[]{30, 70})).useAllAvailableWidth();
        table.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
        table.setMarginTop(20);

        //Starting Point
        table.addCell(new Cell().add(new Paragraph("Start"))
                .setBackgroundColor(WebColors.getRGBColor("#F2CC8F"), 0.5f)
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph(tour.getStartingPoint()))
                .setBackgroundColor(WebColors.getRGBColor("#F2CC8F"), 0.5f)
                .setBorder(Border.NO_BORDER));
        //Destination
        table.addCell(new Cell().add(new Paragraph("Destination"))
                .setBackgroundColor(WebColors.getRGBColor("#FFE0AF"), 0.5f)
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph(tour.getDestinationPoint()))
                .setBackgroundColor(WebColors.getRGBColor("#FFE0AF"), 0.5f)
                .setBorder(Border.NO_BORDER));
        //Transport Type
        table.addCell(new Cell().add(new Paragraph("Transport Type"))
                .setBackgroundColor(WebColors.getRGBColor("#F2CC8F"), 0.5f)
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph(String.valueOf(tour.getTransportType())))
                .setBackgroundColor(WebColors.getRGBColor("#F2CC8F"), 0.5f)
                .setBorder(Border.NO_BORDER));
        //Distance
        table.addCell(new Cell().add(new Paragraph("Distance"))
                .setBackgroundColor(WebColors.getRGBColor("#FFE0AF"), 0.5f)
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph(String.valueOf(tour.getDistance())))
                .setBackgroundColor(WebColors.getRGBColor("#FFE0AF"), 0.5f)
                .setBorder(Border.NO_BORDER));
        //Description
        table.addCell(new Cell().add(new Paragraph("Description"))
                .setBackgroundColor(WebColors.getRGBColor("#F2CC8F"), 0.5f)
                .setBorder(Border.NO_BORDER));
        if(tour.getDescription() == null){
            table.addCell(new Cell().add(new Paragraph("No Description available"))
                    .setBackgroundColor(WebColors.getRGBColor("#F2CC8F"), 0.5f)
                    .setBorder(Border.NO_BORDER));
        }else {
            table.addCell(new Cell().add(new Paragraph(tour.getDescription()))
                    .setBackgroundColor(WebColors.getRGBColor("#F2CC8F"), 0.5f)
                    .setBorder(Border.NO_BORDER));
        }
        //Rating (average)
        table.addCell(new Cell().add(new Paragraph("Rating"))
                .setBackgroundColor(WebColors.getRGBColor("#FFE0AF"), 0.5f)
                .setBorder(Border.NO_BORDER));
        if(tour.getTourLogs().size() > 0) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(calculateAverageRating(tour.getTourLogs()))))
                    .setBackgroundColor(WebColors.getRGBColor("#FFE0AF"), 0.5f)
                    .setBorder(Border.NO_BORDER));
        } else {
            table.addCell(new Cell().add(new Paragraph("No Ratings available"))
                    .setBackgroundColor(WebColors.getRGBColor("#FFE0AF"), 0.5f)
                    .setBorder(Border.NO_BORDER));
        }

        //calculated Duration
        table.addCell(new Cell().add(new Paragraph("Calculated Duration"))
                .setBackgroundColor(WebColors.getRGBColor("#F2CC8F"), 0.5f)
                .setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph(durationToString(tour.getDuration())))
                .setBackgroundColor(WebColors.getRGBColor("#F2CC8F"), 0.5f)
                .setBorder(Border.NO_BORDER));

        //average Duration

        table.addCell(new Cell().add(new Paragraph("Average Duration"))
                .setBackgroundColor(WebColors.getRGBColor("#FFE0AF"), 0.5f)
                .setBorder(Border.NO_BORDER));
        if(averageDuration != Duration.ZERO) {
            String averageDurationString = String.format("%d:%02d", averageDuration.getSeconds() / 3600, (averageDuration.getSeconds() % 3600) / 60);
            table.addCell(new Cell().add(new Paragraph(averageDurationString))
                    .setBackgroundColor(WebColors.getRGBColor("#FFE0AF"), 0.5f)
                    .setBorder(Border.NO_BORDER));
        }else {
            table.addCell(new Cell().add(new Paragraph("No Logs available"))
                    .setBackgroundColor(WebColors.getRGBColor("#FFE0AF"), 0.5f)
                    .setBorder(Border.NO_BORDER));
        }

        return table;


    }

    private String durationToString(Duration duration) {
        if (duration == null || duration.getSeconds() == -1) { //-1 is loading, null is for new tours
            return "No Logs available";
        } else if (duration.getSeconds() == -2) { //-2 is error
            return "Error";
        } else {
            return String.format("%d:%02d", duration.getSeconds() / 3600, (duration.getSeconds() % 3600) / 60);
        }
    }
}
