package com.cartify.cartify.gui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.cartify.cartify.model.Product;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ProductGUI {
    private JFrame frame;
    private JTextArea outputArea;
    private JTextField categoryField;
    private static final String BASE_URL = "http://localhost:8080/products";

    public ProductGUI() {
        frame = new JFrame("Cartify Product Manager");
        frame.setSize(600,500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel top = new JPanel();
        JButton listBtn = new JButton("List Products");
        JButton addBtn = new JButton("Add Product");
        JButton filterBtn = new JButton("Filter Category");
        categoryField = new JTextField(10);
        top.add(listBtn); top.add(addBtn); top.add(new JLabel("Category:")); top.add(categoryField); top.add(filterBtn);

        outputArea = new JTextArea(); JScrollPane scroll = new JScrollPane(outputArea);
        frame.add(top,BorderLayout.NORTH); frame.add(scroll,BorderLayout.CENTER);

        listBtn.addActionListener(e->listProducts());
        filterBtn.addActionListener(e->filterProducts());
        addBtn.addActionListener(e->addProduct());

        frame.setVisible(true);
    }

    private void listProducts(){ callGet(BASE_URL); }
    private void filterProducts(){ callGet(BASE_URL+"/category/"+categoryField.getText().trim()); }

    private void callGet(String urlStr){
        try{
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            if(conn.getResponseCode()==200){
                ObjectMapper mapper = new ObjectMapper();
                List<Product> products = mapper.readValue(conn.getInputStream(),
                        mapper.getTypeFactory().constructCollectionType(List.class, Product.class));
                outputArea.setText(""); products.forEach(p->outputArea.append(p.toString()+"\n"));
            }else outputArea.setText("Error: "+conn.getResponseCode());
            conn.disconnect();
        }catch(Exception e){ e.printStackTrace(); outputArea.setText("Error: "+e.getMessage());}
    }

    private void addProduct(){
        JTextField name=new JTextField(),desc=new JTextField(),price=new JTextField(),qty=new JTextField(),cat=new JTextField();
        Object[] message={"Name:",name,"Desc:",desc,"Price:",price,"Qty:",qty,"Category:",cat};
        if(JOptionPane.showConfirmDialog(frame,message,"Add Product",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){
            try{
                Product p=new Product();
                p.setProductName(name.getText()); p.setProductDescription(desc.getText());
                p.setProductPrice(Integer.parseInt(price.getText()));
                p.setProductQty(Integer.parseInt(qty.getText())); p.setCategory(cat.getText());

                ObjectMapper mapper=new ObjectMapper(); String json=mapper.writeValueAsString(p);
                URL url=new URL(BASE_URL); HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST"); conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type","application/json");
                OutputStream os=conn.getOutputStream(); os.write(json.getBytes()); os.flush(); os.close();

                if(conn.getResponseCode()==200 || conn.getResponseCode()==201) JOptionPane.showMessageDialog(frame,"Product added!");
                else JOptionPane.showMessageDialog(frame,"Error: "+conn.getResponseCode());
                conn.disconnect();
            }catch(Exception e){ e.printStackTrace(); JOptionPane.showMessageDialog(frame,"Error: "+e.getMessage());}
        }
    }

    public static void main(String[] args){ SwingUtilities.invokeLater(ProductGUI::new);}
}
