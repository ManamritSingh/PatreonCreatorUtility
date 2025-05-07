package com.patreon.backend.chatbot;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import io.github.cdimascio.dotenv.Dotenv;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Standalone utility to inspect the OpenAI Response object structure.
 * This can be run directly to print information about the Response object.
 */
public class ResponseInspector {

    public static void main(String[] args) {
        // Create client using environment variables
        OpenAIClient client;

        try {
            // Load from .env file
            Dotenv dotenv = Dotenv.load();
            String apiKey = dotenv.get("OPENAI_API_KEY");

            client = OpenAIOkHttpClient.builder()
                    .apiKey(apiKey)
                    .build();

            // Create a simple request
            ResponseCreateParams params = ResponseCreateParams.builder()
                    .input("Hello, what's the weather today?")
                    .model(ChatModel.GPT_4_1)
                    .build();

            // Get the response
            Response response = client.responses().create(params);

            // Print basic information
            System.out.println("=== RESPONSE OBJECT INFORMATION ===");
            System.out.println("Response class: " + response.getClass().getName());
            System.out.println("Response toString(): " + response);

            // Print all methods
            System.out.println("\n=== AVAILABLE METHODS ===");
            Method[] methods = response.getClass().getMethods();
            Arrays.sort(methods, (m1, m2) -> m1.getName().compareTo(m2.getName()));

            for (Method method : methods) {
                // Filter out Object class methods to reduce noise
                if (method.getDeclaringClass() != Object.class) {
                    System.out.println(formatMethod(method));
                }
            }

            // Print all fields
            System.out.println("\n=== FIELDS ===");
            Field[] fields = response.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(response);
                    System.out.println(field.getName() + " (" + field.getType().getSimpleName() + "): " + value);
                } catch (Exception e) {
                    System.out.println(field.getName() + " (" + field.getType().getSimpleName() + "): [Error accessing]");
                }
            }

            // Try common methods for getting response text
            System.out.println("\n=== ATTEMPTING TO EXTRACT RESPONSE TEXT ===");
            tryMethod(response, "getContent");
            tryMethod(response, "getText");
            tryMethod(response, "getMessage");
            tryMethod(response, "getBody");
            tryMethod(response, "getValue");
            tryMethod(response, "getOutput");
            tryMethod(response, "getResponse");

            // Try to navigate through nested objects (common pattern)
            System.out.println("\n=== ATTEMPTING TO NAVIGATE NESTED STRUCTURE ===");
            try {
                Method getChoices = findMethod(response, "getChoices");
                if (getChoices != null) {
                    Object choices = getChoices.invoke(response);
                    System.out.println("Choices type: " + choices.getClass().getName());

                    if (choices instanceof Iterable) {
                        Object firstChoice = ((Iterable<?>) choices).iterator().next();
                        System.out.println("First choice type: " + firstChoice.getClass().getName());

                        // Try to get message
                        Method getMessage = findMethod(firstChoice, "getMessage");
                        if (getMessage != null) {
                            Object message = getMessage.invoke(firstChoice);
                            System.out.println("Message type: " + message.getClass().getName());

                            Method getContent = findMethod(message, "getContent");
                            if (getContent != null) {
                                Object content = getContent.invoke(message);
                                System.out.println("FOUND TEXT VIA NESTED STRUCTURE: " + content);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error navigating nested structure: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void tryMethod(Object obj, String methodName) {
        try {
            Method method = obj.getClass().getMethod(methodName);
            Object result = method.invoke(obj);
            System.out.println(methodName + "() WORKS! Result: " + result);
        } catch (NoSuchMethodException e) {
            System.out.println(methodName + "(): Method not found");
        } catch (Exception e) {
            System.out.println(methodName + "(): Error calling method: " + e.getMessage());
        }
    }

    private static Method findMethod(Object obj, String methodName) {
        try {
            return obj.getClass().getMethod(methodName);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static String formatMethod(Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getReturnType().getSimpleName()).append(" ");
        sb.append(method.getName()).append("(");

        Class<?>[] paramTypes = method.getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(paramTypes[i].getSimpleName());
        }

        sb.append(")");
        return sb.toString();
    }
}