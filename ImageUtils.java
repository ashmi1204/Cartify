package com.cartify;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ImageUtils {
    private static final Map<String, ImageIcon> imageCache = new HashMap<>();

    // Sample product images (using placeholder URLs)
    private static final Map<String, String> productImages = Map.of(
            "MacBook Pro", "https://via.placeholder.com/150x150/4CAF50/white?text=MacBook+Pro",
            "iPhone 15", "https://via.placeholder.com/150x150/2196F3/white?text=iPhone+15",
            "Gaming Chair", "https://via.placeholder.com/150x150/FF9800/white?text=Gaming+Chair",
            "Mechanical Keyboard", "https://via.placeholder.com/150x150/9C27B0/white?text=Keyboard",
            "Office Desk", "https://via.placeholder.com/150x150/795548/white?text=Office+Desk"
    );

    public static ImageIcon getProductImage(String productName, int width, int height) {
        String key = productName + "_" + width + "x" + height;

        if (imageCache.containsKey(key)) {
            return imageCache.get(key);
        }

        // For demo, create a colored placeholder with product name
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Set background color based on product name hash
        int color = Math.abs(productName.hashCode()) % 0xFFFFFF;
        g2d.setColor(new Color(color));
        g2d.fillRect(0, 0, width, height);

        // Add product text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        String[] words = productName.split(" ");
        String line1 = words.length > 0 ? words[0] : "";
        String line2 = words.length > 1 ? words[1] : "";

        FontMetrics fm = g2d.getFontMetrics();
        int x1 = (width - fm.stringWidth(line1)) / 2;
        int x2 = (width - fm.stringWidth(line2)) / 2;
        g2d.drawString(line1, x1, height/2 - 5);
        g2d.drawString(line2, x2, height/2 + 15);

        g2d.dispose();

        ImageIcon icon = new ImageIcon(image);
        imageCache.put(key, icon);
        return icon;
    }

    public static ImageIcon getScaledImage(ImageIcon original, int width, int height) {
        Image img = original.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }
}