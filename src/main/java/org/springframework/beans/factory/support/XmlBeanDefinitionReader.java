package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeanDefinition;
import org.springframework.beans.factory.PropertyValue;
import org.springframework.beans.factory.RuntimeBeanReference;
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

                // 解析构造函数参数
                parseConstructorArgElements(element, beanDefinition);

                // 解析属性元素
                parsePropertyElements(element, beanDefinition);

                // 将bean定义注册到bean工厂
                beanFactory.registerBeanDefinition(id, beanDefinition);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Failed to load class: " + className, e);
            }
        }
    }

    /**
     * 解析bean元素中的property子元素
     *
     * @param beanElement bean元素
     * @param beanDefinition 对应的BeanDefinition
     */
    private void parsePropertyElements(Element beanElement, BeanDefinition beanDefinition) {
        NodeList propertyNodes = beanElement.getElementsByTagName("property");

        for (int i = 0; i < propertyNodes.getLength(); i++) {
            Element propertyElement = (Element) propertyNodes.item(i);

            String name = propertyElement.getAttribute("name");
            Object value = parsePropertyElement(propertyElement);

            beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue(name, value));
        }
    }

    /**
     * 解析单个property元素
     *
     * @param propertyElement property元素
     * @return 解析后的属性值
     */
    private Object parsePropertyElement(Element propertyElement) {
        // 检查是否有ref属性（引用其他bean）
        String ref = propertyElement.getAttribute("ref");
        if (!ref.isEmpty()) {
            return new RuntimeBeanReference(ref);
        }

        // 检查是否有value属性（字面量值）
        String value = propertyElement.getAttribute("value");
        if (!value.isEmpty()) {
            return parseValue(value);
        }

        // 如果既没有ref也没有value，则抛出异常
        throw new RuntimeException("property element must have either 'ref' or 'value' attribute");
    }

    /**
     * 解析bean元素中的constructor-arg子元素
     *
     * @param beanElement bean元素
     * @param beanDefinition 对应的BeanDefinition
     */
    private void parseConstructorArgElements(Element beanElement, BeanDefinition beanDefinition) {
        NodeList constructorArgNodes = beanElement.getElementsByTagName("constructor-arg");

        for (int i = 0; i < constructorArgNodes.getLength(); i++) {
            Element constructorArgElement = (Element) constructorArgNodes.item(i);

            // 解析constructor-arg元素
            Object value = parseConstructorArgElement(constructorArgElement);

            // 添加到BeanDefinition的构造函数参数列表中
            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(value);
        }
    }

    /**
     * 解析单个constructor-arg元素
     *
     * @param constructorArgElement constructor-arg元素
     * @return 解析后的参数值
     */
    private Object parseConstructorArgElement(Element constructorArgElement) {
        // 检查是否有ref属性（引用其他bean）
        String ref = constructorArgElement.getAttribute("ref");
        if (!ref.isEmpty()) {
            return ref; // 返回bean名称，后续会解析为实际bean实例
        }

        // 检查是否有value属性（字面量值）
        String value = constructorArgElement.getAttribute("value");
        if (!value.isEmpty()) {
            return parseValue(value); // 解析字面量值
        }

        // 如果既没有ref也没有value，则抛出异常
        throw new RuntimeException("constructor-arg element must have either 'ref' or 'value' attribute");
    }

    /**
     * 解析字面量值
     *
     * @param value 字符串形式的值
     * @return 解析后的值（可能是Integer、Boolean等类型）
     */
    private Object parseValue(String value) {
        // 尝试解析为整数
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // 忽略，继续尝试其他类型
        }

        // 尝试解析为布尔值
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            return Boolean.parseBoolean(value);
        }

        // 默认作为字符串处理
        return value;
    }

}
