/*
 * MIT License
 *
 * Copyright (c) 2025 Francesco Scotti
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package it.unicam.cs.mpgc.jbudget126118.persistency;


import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import it.unicam.cs.mpgc.jbudget126118.model.Tag;
import it.unicam.cs.mpgc.jbudget126118.model.Transaction;
import it.unicam.cs.mpgc.jbudget126118.model.TransactionType;
import org.w3c.dom.*;

/**
 * XmlPersistency is a class that implements the TransactionPersistency interface.
 * It provides methods to load and save transactions and tags (for tags only load) from/to XML files.
 * The transactions are stored in an XML file with a specific structure, allowing for easy retrieval and manipulation.
 */
public class XmlPersistency implements TransactionPersistency {
    private final String xmlTransactionFile;
    private final String xmlTagFile;

    /**
     * Constructor for the class XmlPersistency
     * @param xmlTransactionFile String with the path of the file that contains transactions
     * @param xmlTagFile String with the path of the file that contains tags
     */
    public XmlPersistency(String xmlTransactionFile, String xmlTagFile) {
        this.xmlTransactionFile = xmlTransactionFile;
        this.xmlTagFile = xmlTagFile;
    }

    /**
     * Loads all transactions from the XML file.
     * This method reads the XML file and parses it to create a list of Transaction objects.
     * Each transaction is represented by an XML element with attributes and child elements.
     * * @return a list of Transaction objects loaded from the XML file.
     * * If the file does not exist or is malformed, an empty list is returned.
     */
    @Override
    public List<Transaction> load() {
        List<Transaction> transactions = new ArrayList<>();

        try {
            Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(xmlTransactionFile);
            doc.getDocumentElement().normalize();

            NodeList transactionNodes = doc.getElementsByTagName("transaction");

            for (int i = 0; i < transactionNodes.getLength(); i++) {
                Element txEl = (Element) transactionNodes.item(i);
                long id = Long.parseLong(txEl.getAttribute("id"));
                double amount = Double.parseDouble(txEl.getElementsByTagName("amount").item(0).getTextContent());
                TransactionType type = TransactionType.valueOf(txEl.getElementsByTagName("type").item(0).getTextContent());
                LocalDate date = LocalDate.parse(txEl.getElementsByTagName("date").item(0).getTextContent());

                List<Tag> tags = new ArrayList<>();
                NodeList tagNodes = ((Element) txEl.getElementsByTagName("tags").item(0)).getElementsByTagName("tag");

                for (int j = 0; j < tagNodes.getLength(); j++) {
                    Element tagEl = (Element) tagNodes.item(j);
                    long tagId = Long.parseLong(tagEl.getAttribute("id"));
                    String name = tagEl.getAttribute("name");
                    tags.add(new Tag(tagId, name, null)); // Ignoriamo i parent per ora
                }

                transactions.add(new Transaction(id, amount, type, date, tags));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return transactions;
    }


    /**
     * Loads tags from the XML file.
     * This method reads the XML file containing tags and parses it to create a list of Tag objects.
     * * Each tag is represented by an XML element with attributes and may have child elements representing sub-tags.
     * * @return a list of Tag objects loaded from the XML file.
     * * If the file does not exist or is malformed, an empty list is returned.
     */
    @Override
    public List<Tag> loadTags() {
        List<Tag> allTags = new ArrayList<>();

        try {
            if (!(new File(xmlTagFile).exists())) {
                return allTags;
            }

            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse(xmlTagFile);
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();
            NodeList tagNodes = root.getChildNodes();

            for (int i = 0; i < tagNodes.getLength(); i++) {
                if (tagNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    parseTagElement((Element) tagNodes.item(i), null, allTags);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return allTags;
    }

    /**
     *  Parses a tag element and its children recursively.
     *  This method creates a Tag object for the current element and adds it to the provided tagSet.
     *  It also recursively processes any child elements, treating them as sub-tags.
     * 
     * @param el The XML element representing the tag.
     * @param parent  The parent tag, or null if this is a top-level tag.
     * @param tagSet  The list to which the parsed tag will be added.
     */
    private void parseTagElement(Element el, Tag parent, List<Tag> tagSet) {
        long id = Long.parseLong(el.getAttribute("id"));
        String name = el.getAttribute("name");

        Tag current = new Tag(id, name, parent);
        tagSet.add(current);

        NodeList children = el.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                parseTagElement((Element) children.item(i), current, tagSet);
            }
        }
    }

    /**
     * Saves a transaction to the XML file.
     * This method creates or updates the XML file with the provided transaction data.
     * * If the file does not exist, it creates a new file with a root element.
     * * If the file exists, it appends the new transaction to the existing list of transactions.
     * * @param t The transaction to save.
     */
    @Override
    public void save(Transaction t) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc;

            // Se il file esiste, caricalo. Altrimenti creane uno nuovo
            if (new File(xmlTransactionFile).exists()) {
                doc = builder.parse(xmlTransactionFile);
            } else {
                doc = builder.newDocument();
                Element root = doc.createElement("transactions");
                doc.appendChild(root);
            }

            Element root = doc.getDocumentElement();
            Element tx = doc.createElement("transaction");
            tx.setAttribute("id", String.valueOf(t.id()));

            Element amount = doc.createElement("amount");
            amount.setTextContent(String.valueOf(t.amount()));
            tx.appendChild(amount);

            Element type = doc.createElement("type");
            type.setTextContent(t.transactionType().toString());
            tx.appendChild(type);

            Element date = doc.createElement("date");
            date.setTextContent(t.transactionDate().format(DateTimeFormatter.ISO_DATE));
            tx.appendChild(date);

            Element tagsEl = doc.createElement("tags");
            for (Tag tag : t.tags()) {
                Element tagEl = doc.createElement("tag");
                tagEl.setAttribute("id", String.valueOf(tag.id()));
                tagEl.setAttribute("name", tag.name());
                tagsEl.appendChild(tagEl);
            }
            tx.appendChild(tagsEl);

            root.appendChild(tx);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new FileOutputStream(xmlTransactionFile));
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
