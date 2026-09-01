import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.awt.Rectangle;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println(new Date() + " Start of loading variables and cards.");

        Map<String, String> map = importImageProperties(args[0]);

        DrawCards drawCards = new DrawCards(
                map.get("templateFileName"),
                new Rectangle(
                        Integer.parseInt(map.get("typeRectangleX")),
                        Integer.parseInt(map.get("typeRectangleY")),
                        Integer.parseInt(map.get("typeRectangleWidth")),
                        Integer.parseInt(map.get("typeRectangleHeight"))),
                Integer.parseInt(map.get("typeFontSize")),
                new Rectangle(
                        Integer.parseInt(map.get("symbolsRectangleX")),
                        Integer.parseInt(map.get("symbolsRectangleY")),
                        Integer.parseInt(map.get("symbolsRectangleWidth")),
                        Integer.parseInt(map.get("symbolsRectangleHeight"))),
                Integer.parseInt(map.get("symbolWidth")),
                Integer.parseInt(map.get("symbolHeight")),
                Integer.parseInt(map.get("symbolHorizontalPadding")),
                new Rectangle(
                        Integer.parseInt(map.get("pointsRectangleX")),
                        Integer.parseInt(map.get("pointsRectangleY")),
                        Integer.parseInt(map.get("pointsRectangleWidth")),
                        Integer.parseInt(map.get("pointsRectangleHeight"))),
                Integer.parseInt(map.get("pointsFontSize")),
                new Rectangle(
                        Integer.parseInt(map.get("artRectangleX")),
                        Integer.parseInt(map.get("artRectangleY")),
                        Integer.parseInt(map.get("artRectangleWidth")),
                        Integer.parseInt(map.get("artRectangleHeight"))),
                new Rectangle(
                        Integer.parseInt(map.get("nameRectangleX")),
                        Integer.parseInt(map.get("nameRectangleY")),
                        Integer.parseInt(map.get("nameRectangleWidth")),
                        Integer.parseInt(map.get("nameRectangleHeight"))),
                Integer.parseInt(map.get("nameFontSize")),
                new Rectangle(
                        Integer.parseInt(map.get("textRectangleX")),
                        Integer.parseInt(map.get("textRectangleY")),
                        Integer.parseInt(map.get("textRectangleWidth")),
                        Integer.parseInt(map.get("textRectangleHeight"))),
                Integer.parseInt(map.get("cardWidth")),
                Integer.parseInt(map.get("cardHeight")),
                Integer.parseInt(map.get("sheetWidth")),
                Integer.parseInt(map.get("sheetHeight")),
                Integer.parseInt(map.get("numberOfCardsWide")),
                Integer.parseInt(map.get("numberOfCardsHigh")),
                Integer.parseInt(map.get("numberOfCardsWide")) * Integer.parseInt(map.get("numberOfCardsHigh")),
                Boolean.parseBoolean(map.get("createImageFiles")),
                Boolean.parseBoolean(map.get("createSheetFiles")),
                Integer.parseInt(map.get("pixelateToWidth")),
                Integer.parseInt(map.get("pixelateToHeight")),
                Boolean.parseBoolean(map.get("pixelateCard"))
        );

        List<Card> cards = importCards(args[1]);

        System.out.println(new Date() + " End of loading variables and cards.");

        drawCards.drawCardsAndSheets(cards);
    }

    static Map<String, String> importImageProperties(String fileName) {
        Map<String, String> map = new HashMap<>();

        try (CSVReader reader = new CSVReader(new FileReader("./input/" + fileName))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                map.put(line[0], line[1]);
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println(e.getMessage());
        }

        return map;
    }

    static List<Card> importCards(String fileName) {
        List<Card> cards = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader("./input/" + fileName))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (!line[0].isEmpty() && !line[1].isEmpty() && !line[2].isEmpty()) {
                    Card.CardType cardType = Objects.equals(line[0], "B") ? Card.CardType.BAG : Card.CardType.CHARM;
                    cards.add(new Card(cardType, line[1], line[2], line[3], line[4], line[5], line[6], line[7]));
                }
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println(e.getMessage());
        }

        return cards;
    }
}
