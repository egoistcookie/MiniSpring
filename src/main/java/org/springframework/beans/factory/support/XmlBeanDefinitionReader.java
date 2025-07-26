package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeanDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

/**
 * XML Bean定义读取器
 *
 * 该类负责从XML配置文件中解析Bean定义信息，并将其注册到Bean工厂中。
 * 它是Spring框架中基于XML配置的核心组件之一，实现了配置驱动的IoC容器功能。
 *
 * 主要功能包括：
 * 1. 解析XML格式的配置文件
 * 2. 提取Bean定义信息（如id、class等属性）
 * 3. 创建BeanDefinition对象并注册到Bean工厂
 * 4. 处理解析过程中可能出现的异常情况
 */
public class XmlBeanDefinitionReader {

    /**
     * Bean工厂实例，用于注册解析到的Bean定义
     * 该字段在构造时注入，XmlBeanDefinitionReader依赖于具体的Bean工厂实现
     */
    private final DefaultListableBeanFactory beanFactory;

    /**
     * 构造函数，初始化XML Bean定义读取器
     *
     * @param beanFactory Bean工厂实例，用于后续将解析到的Bean定义注册到容器中
     */
    public XmlBeanDefinitionReader(DefaultListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * 从输入流中加载Bean定义信息
     *
     * 该方法是XML配置文件解析的入口点，负责读取输入流中的XML数据，
     * 使用DOM解析器解析XML文档，并提取其中的Bean定义信息。
     * 解析完成后会将Bean定义注册到关联的Bean工厂中。
     *
     * @param inputStream 包含Bean定义的XML配置文件输入流，通常来自classpath或文件系统
     * @throws Exception 解析过程中可能抛出的异常，包括：
     *                   - ParserConfigurationException: 解析器配置异常
     *                   - SAXException: XML解析异常
     *                   - IOException: IO读取异常
     *                   - ClassNotFoundException: Bean类未找到异常
     */
    public void loadBeanDefinitions(InputStream inputStream) throws Exception {
        // 创建DOM解析器工厂
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        // 解析XML文档
        Document doc = builder.parse(inputStream);
        // 解析Bean定义元素
        parseBeanDefinitions(doc.getDocumentElement());
    }

    /**
     * 解析XML配置文件中的bean定义并注册到bean工厂
     *
     * 该方法遍历XML文档中的所有<bean>元素，提取每个Bean的id和class属性，
     * 创建对应的BeanDefinition对象，并将其注册到Bean工厂中。
     * 这是实际执行Bean定义解析和注册的核心方法。
     *
     * @param root XML配置文件的根元素，从该元素开始遍历查找bean定义
     */
    private void parseBeanDefinitions(Element root) {
        // 获取所有bean标签节点
        NodeList nodeList = root.getElementsByTagName("bean");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            String id = element.getAttribute("id");
            String className = element.getAttribute("class");

            try {
                // 创建BeanDefinition实例并设置相关属性
                BeanDefinition beanDefinition = new BeanDefinition();
                beanDefinition.setBeanClass(Class.forName(className));
                // 将bean定义注册到bean工厂
                beanFactory.registerBeanDefinition(id, beanDefinition);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Failed to load class: " + className, e);
            }
        }
    }

}
