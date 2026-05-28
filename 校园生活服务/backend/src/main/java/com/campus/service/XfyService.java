package com.campus.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class XfyService {

    @Value("${xfy.app-id}")
    private String appId;

    @Value("${xfy.api-secret}")
    private String apiSecret;

    @Value("${xfy.api-key}")
    private String apiKey;

    @Value("${xfy.iat-url}")
    private String iatUrl;

    @Value("${xfy.tts-url}")
    private String ttsUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    public String speechToText(byte[] audioData) {
        try {
            String url = buildWebSocketUrl(iatUrl);
            String requestJson = buildIatRequest(audioData);
            String response = sendWebSocketText(url, requestJson);
            return parseIatResponse(response);
        } catch (Exception e) {
            log.error("讯飞语音听写失败", e);
            return null;
        }
    }

    public byte[] textToSpeech(String text) {
        try {
            log.info("开始语音合成，文本: {}", text);
            String url = buildWebSocketUrl(ttsUrl);
            String requestJson = buildTtsRequest(text);
            log.info("TTS请求JSON: {}", requestJson);
            byte[] result = sendWebSocketBinary(url, requestJson);
            log.info("语音合成完成，返回音频大小: {} bytes", result != null ? result.length : 0);
            return result;
        } catch (Exception e) {
            log.error("讯飞语音合成失败", e);
            return null;
        }
    }

    private String buildWebSocketUrl(String baseUrl) throws Exception {
        String now = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US)
                .format(new Date());

        URI uri = new URI(baseUrl);
        String host = uri.getHost();
        String path = uri.getPath();

        String signatureOrigin = "host: " + host + "\n"
                + "date: " + now + "\n"
                + "GET " + path + " HTTP/1.1";

        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);
        byte[] digest = mac.doFinal(signatureOrigin.getBytes(StandardCharsets.UTF_8));
        String signature = Base64.getEncoder().encodeToString(digest);

        String authorizationOrigin = "api_key=\"" + apiKey + "\", algorithm=\"hmac-sha256\", "
                + "headers=\"host date request-line\", signature=\"" + signature + "\"";
        String authorization = Base64.getEncoder()
                .encodeToString(authorizationOrigin.getBytes(StandardCharsets.UTF_8));

        return baseUrl + "?authorization=" + URLEncoder.encode(authorization, "UTF-8")
                + "&date=" + URLEncoder.encode(now, "UTF-8")
                + "&host=" + host;
    }

    private String buildIatRequest(byte[] audioData) throws Exception {
        String audioBase64 = Base64.getEncoder().encodeToString(audioData);

        ObjectNode frame = objectMapper.createObjectNode();
        ObjectNode common = objectMapper.createObjectNode();
        common.put("app_id", appId);
        frame.set("common", common);

        ObjectNode business = objectMapper.createObjectNode();
        business.put("language", "zh_cn");
        business.put("domain", "iat");
        business.put("accent", "mandarin");
        business.put("vad_eos", 3000);
        business.put("dwa", "wpgs");
        frame.set("business", business);

        ObjectNode data = objectMapper.createObjectNode();
        data.put("status", 2);
        data.put("format", "audio/L16;rate=16000");
        data.put("encoding", "raw");
        data.put("audio", audioBase64);
        frame.set("data", data);

        return objectMapper.writeValueAsString(frame);
    }

    private String buildTtsRequest(String text) throws Exception {
        ObjectNode frame = objectMapper.createObjectNode();
        ObjectNode common = objectMapper.createObjectNode();
        common.put("app_id", appId);
        frame.set("common", common);

        ObjectNode business = objectMapper.createObjectNode();
        business.put("aue", "lame");
        business.put("sfl", 1);
        business.put("auf", "audio/L16;rate=16000");
        business.put("vcn", "xiaoyan");
        business.put("speed", 50);
        business.put("volume", 50);
        business.put("pitch", 50);
        business.put("bgs", 0);
        business.put("tte", "UTF8");
        frame.set("business", business);

        ObjectNode data = objectMapper.createObjectNode();
        data.put("status", 2);
        data.put("text", Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8)));
        frame.set("data", data);

        return objectMapper.writeValueAsString(frame);
    }

    private String sendWebSocketText(String url, String requestJson) throws Exception {
        StringBuilder responseBuilder = new StringBuilder();
        CountDownLatch latch = new CountDownLatch(1);

        Request request = new Request.Builder()
                .url(url)
                .build();

        WebSocket webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket ws, Response response) {
                ws.send(requestJson);
            }

            @Override
            public void onMessage(WebSocket ws, String text) {
                responseBuilder.append(text);
                try {
                    JsonNode root = objectMapper.readTree(text);
                    int code = root.path("code").asInt();
                    if (code != 0) {
                        log.error("讯飞API错误: {} - {}", code, root.path("message").asText());
                        latch.countDown();
                        return;
                    }
                    JsonNode dataNode = root.path("data");
                    int status = dataNode.path("status").asInt();
                    if (status == 2 || status == 1) {
                        latch.countDown();
                    }
                } catch (Exception e) {
                    log.error("解析讯飞响应失败", e);
                    latch.countDown();
                }
            }

            @Override
            public void onFailure(WebSocket ws, Throwable t, Response response) {
                log.error("WebSocket连接失败", t);
                latch.countDown();
            }

            @Override
            public void onClosed(WebSocket ws, int code, String reason) {
                latch.countDown();
            }
        });

        latch.await(20, TimeUnit.SECONDS);
        webSocket.close(1000, "OK");

        return responseBuilder.toString();
    }

    private byte[] sendWebSocketBinary(String url, String requestJson) throws Exception {
        List<byte[]> audioChunks = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch latch = new CountDownLatch(1);

        Request request = new Request.Builder()
                .url(url)
                .build();

        WebSocket webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket ws, Response response) {
                ws.send(requestJson);
            }

            @Override
            public void onMessage(WebSocket ws, String text) {
                try {
                    JsonNode root = objectMapper.readTree(text);
                    int code = root.path("code").asInt();
                    if (code != 0) {
                        log.error("讯飞TTS错误: {} - {}", code, root.path("message").asText());
                        latch.countDown();
                        return;
                    }
                    JsonNode dataNode = root.path("data");
                    int status = dataNode.path("status").asInt();
                    String audioBase64 = dataNode.path("audio").asText();
                    log.info("收到TTS响应帧: status={}, audio长度={}", status, audioBase64.length());
                    if (!audioBase64.isEmpty()) {
                        byte[] audioBytes = Base64.getDecoder().decode(audioBase64);
                        audioChunks.add(audioBytes);
                        log.info("添加音频分片，分片大小={}", audioBytes.length);
                    }
                    if (status == 2) {
                        log.info("收到结束标识，共收到{}个音频分片", audioChunks.size());
                        latch.countDown();
                    }
                } catch (Exception e) {
                    log.error("解析TTS响应失败: {}", text, e);
                    latch.countDown();
                }
            }

            @Override
            public void onFailure(WebSocket ws, Throwable t, Response response) {
                log.error("TTS WebSocket连接失败", t);
                latch.countDown();
            }

            @Override
            public void onClosed(WebSocket ws, int code, String reason) {
                latch.countDown();
            }
        });

        latch.await(20, TimeUnit.SECONDS);
        webSocket.close(1000, "OK");

        log.info("音频分片接收完成，共{}个分片", audioChunks.size());
        ByteArrayOutputStream audioStream = new ByteArrayOutputStream();
        for (byte[] chunk : audioChunks) {
            audioStream.write(chunk);
        }

        byte[] audioData = audioStream.toByteArray();
        log.info("音频数据总大小: {} bytes", audioData.length);
        return audioData;
    }

    private String parseIatResponse(String response) {
        try {
            StringBuilder result = new StringBuilder();
            String[] lines = response.split("(?<=\\})\\s*(?=\\{)");
            for (String line : lines) {
                if (line == null || line.trim().isEmpty()) continue;
                try {
                    JsonNode root = objectMapper.readTree(line);
                    JsonNode data = root.path("data");
                    JsonNode resultNode = data.path("result");
                    if (!resultNode.isMissingNode()) {
                        JsonNode wsArray = resultNode.path("ws");
                        for (JsonNode ws : wsArray) {
                            JsonNode cwArray = ws.path("cw");
                            for (JsonNode cw : cwArray) {
                                result.append(cw.path("w").asText());
                            }
                        }
                    }
                } catch (Exception e) {
                    log.debug("跳过非标准JSON行: {}", e.getMessage());
                }
            }

            return result.length() > 0 ? result.toString() : null;
        } catch (Exception e) {
            log.error("解析语音听写结果失败", e);
            return null;
        }
    }
}
