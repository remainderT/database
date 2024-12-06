package org.buaa.shortlink.toolkit;

import cn.hutool.core.io.resource.ClassPathResource;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.buaa.shortlink.dao.entity.Alert;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class FeiShuAlert {

    private static String webhookUrl = "https://open.feishu.cn/open-apis/bot/v2/hook/392d10e0-ccad-4d6a-bc4a-2b9d704e525e";

    public static void sendCardMessage(Alert message) throws Exception {
        URL url = new URL(webhookUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // 构建卡片消息
        String cardJosn = buildCard(message);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = cardJosn.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            log.info("send fs successfully!");
        } else {
            log.error("Failed to send message. HTTP response code: {}", responseCode);
        }
    }

    public static String buildCard(Alert message) throws Exception {
        // 使用 ObjectMapper 读取 JSON 文件
        ObjectMapper objectMapper = new ObjectMapper();

        // 通过 ClassPathResource 读取资源文件
        InputStream inputStream = new ClassPathResource("card.json").getStream();

        // 读取 JSON 数据并转换成 JsonNode
        JsonNode rootNode = objectMapper.readTree(inputStream);


        // 序列化 message 对象为 JSON 树
        ObjectNode templateVariableNode = objectMapper.valueToTree(message);

        ObjectNode cardNode = (ObjectNode) rootNode.path("card");
        ObjectNode dataNode = (ObjectNode) cardNode.with("data");

        // 将 template_variable 节点设置到 card.data 中
        dataNode.set("template_variable", templateVariableNode);

        String cardJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
        System.out.println(cardJson);
        return cardJson;
    }
}
