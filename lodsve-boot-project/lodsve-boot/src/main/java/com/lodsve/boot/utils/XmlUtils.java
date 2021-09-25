/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lodsve.boot.utils;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.util.Assert;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * xml工具类.
 *
 * @author Hulk Sun
 */
public final class XmlUtils {
    private static final SAXReader SAX_READER = new SAXReader();

    private XmlUtils() {
    }

    /**
     * 解析xml
     *
     * @param in in
     * @return 解析xml
     */
    public static Document parseXML(InputStream in) {
        Assert.notNull(in);

        try {
            return SAX_READER.read(in);
        } catch (DocumentException e) {
            throw new RuntimeException("XML解析发生错误");
        }
    }

    /**
     * xml 2 string
     *
     * @param document xml document
     * @return string
     */
    public static String parseXMLToString(Document document) {
        Assert.notNull(document);

        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        StringWriter writer = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter(writer, format);
        try {
            xmlWriter.write(document);
            xmlWriter.close();
        } catch (IOException e) {
            throw new RuntimeException("XML解析发生错误");
        }
        return writer.toString();
    }

    /**
     * 获取元素下指定节点名的子元素集合
     *
     * @param element 元素
     * @param tagName 子元素
     * @return 子元素集合
     */
    @SuppressWarnings("unchecked")
    public static List<Element> getChildren(Element element, String tagName) {
        Assert.notNull(element);
        Assert.hasText(tagName);

        return element.elements(tagName);
    }

    /**
     * 获取元素下指定节点名的子元素
     *
     * @param element 元素
     * @param tagName 子元素
     * @return 子元素
     */
    public static Element getChild(Element element, String tagName) {
        Assert.notNull(element);
        Assert.hasText(tagName);

        return element.element(tagName);
    }

    /**
     * 获取元素下的全部子元素
     *
     * @param element 元素
     * @return 全部子元素
     */
    @SuppressWarnings("unchecked")
    public static List<Element> getChildren(Element element) {
        Assert.notNull(element);

        return element.elements();
    }

    /**
     * 获取元素中包含的内容
     *
     * @param element 元素
     * @return 包含的内容
     */
    public static String getElementBody(Element element) {
        Assert.notNull(element);

        return element.getTextTrim();
    }

    /**
     * 获取元素属性
     *
     * @param element 元素
     * @param attr    属性名
     * @return 元素属性
     */
    public static String getElementAttr(Element element, String attr) {
        Assert.notNull(element);
        Assert.hasText(attr);

        return element.attributeValue(attr);
    }

    /**
     * 获取元素下子元素的属性
     *
     * @param element 元素
     * @param tagName 标签名
     * @param attr    属性名
     * @return 子元素的属性
     */
    public static String getChildAttr(Element element, String tagName, String attr) {
        Assert.notNull(element);
        Assert.hasText(tagName);
        Assert.hasText(attr);

        Element child = element.element(tagName);
        return child.attributeValue(attr);
    }

    /**
     * 获取指定路径节点
     *
     * @param root 根
     * @param path 路径（相对于给定的根元素）,eg: root/node1/node2...
     * @return 路径节点
     */
    public static Element getElement(Element root, String path) {
        Assert.notNull(root);
        Assert.hasText(path);

        String[] paths = StringUtils.split(path, "/");
        Element element = null;
        for (String p : paths) {
            element = getChild(root, p);
        }

        return element;
    }

    /**
     * 获取指定路径节点的属性
     *
     * @param root        根
     * @param pathAndAttr 路径（相对于给定的根元素）,eg: root/node1/node2/attr3:attrName
     * @return 指定路径节点的属性
     */
    public static String getAttrValue(Element root, String pathAndAttr) {
        Assert.notNull(root);
        Assert.hasText(pathAndAttr);

        String[] temp = StringUtils.split(pathAndAttr, ":");
        if (temp.length != 2) {
            return StringUtils.EMPTY;
        }

        String path = temp[0];
        String attr = temp[1];

        Element element = getElement(root, path);
        if (element == null) {
            return StringUtils.EMPTY;
        }

        return getElementAttr(element, attr);
    }

    /**
     * 判断元素是否含有子节点
     *
     * @param ele      元素
     * @param nodeName 子节点
     * @return true/false
     */
    public static boolean hasChildNode(Element ele, String nodeName) {
        List<Element> eles = getChildren(ele, nodeName);
        return eles != null && eles.size() > 0;
    }

    /**
     * 判断元素是否含有属性
     *
     * @param ele      元素
     * @param attrName 属性名
     * @return true/false
     */
    public static boolean hasAttr(Element ele, String attrName) {
        return ele.attributeValue(attrName) != null;
    }

    public static Map<String, String> parse(InputStream body) {
        try {
            InputSource source = new InputSource(new InputStreamReader(body, StandardCharsets.UTF_8));
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            org.w3c.dom.Document document = dbf.newDocumentBuilder().parse(source);
            NodeList childNodes = document.getElementsByTagName("xml").item(0).getChildNodes();
            Map<String, String> requestMap = new HashMap<>(childNodes.getLength());
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node item = childNodes.item(i);
                requestMap.put(item.getNodeName(), item.getTextContent());
            }
            return requestMap;
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    public static String toXML(Object obj) {
        try {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            StringWriter writer = new StringWriter();
            marshaller.marshal(obj, writer);
            return writer.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
            return "";
        }
    }
}
