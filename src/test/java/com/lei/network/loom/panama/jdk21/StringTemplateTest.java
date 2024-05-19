package com.lei.network.loom.panama.jdk21;

/**
 * <p>
 * jdk 21 新特性 字符串模板
 * </p>
 *
 * @author 伍磊
 */
public class StringTemplateTest {

    public static void main(String[] args) {
        String name = "JDK21";
        String message = STR."Hello \{name}!";
        System.out.println(message);

        String json = STR."""
                {
                    "name": "\{name}"
                }
                """;
        System.out.println(json);

        name = null;
        System.out.println(STR."name == \{name == null ? "null" : name}");
    }

}
