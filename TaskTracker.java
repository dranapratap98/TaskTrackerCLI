import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class TaskTracker {
    private static final String FILE_PATH = "tasks.json";

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java TaskTracker <command> [options]");
            return;
        }

        String command = args[0];
        switch (command) {
            case "add":
                if (args.length < 2) {
                    System.out.println("Usage: java TaskTracker add <description>");
                } else {
                    addTask(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                }
                break;
            case "update":
                if (args.length < 3) {
                    System.out.println("Usage: java TaskTracker update <id> <description>");
                } else {
                    updateTask(Integer.parseInt(args[1]), String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
                }
                break;
            case "delete":
                if (args.length < 2) {
                    System.out.println("Usage: java TaskTracker delete <id>");
                } else {
                    deleteTask(Integer.parseInt(args[1]));
                }
                break;
            case "mark-in-progress":
                if (args.length < 2) {
                    System.out.println("Usage: java TaskTracker mark-in-progress <id>");
                } else {
                    updateTaskStatus(Integer.parseInt(args[1]), "in-progress");
                }
                break;
            case "mark-done":
                if (args.length < 2) {
                    System.out.println("Usage: java TaskTracker mark-done <id>");
                } else {
                    updateTaskStatus(Integer.parseInt(args[1]), "done");
                }
                break;
            case "list":
                if (args.length == 1) {
                    listTasks(null);
                } else {
                    listTasks(args[1]);
                }
                break;
            default:
                System.out.println("Invalid command!");
        }
    }

    private static void addTask(String description) {
        JSONArray tasks = readTasks();
        int id = tasks.length() + 1;
        JSONObject task = new JSONObject();
        task.put("id", id);
        task.put("description", description);
        task.put("status", "todo");
        task.put("createdAt", LocalDateTime.now().toString());
        task.put("updatedAt", LocalDateTime.now().toString());

        tasks.put(task);
        writeTasks(tasks);
        System.out.println("Task added successfully (ID: " + id + ")");
    }

    private static void updateTask(int id, String description) {
        JSONArray tasks = readTasks();
        for (int i = 0; i < tasks.length(); i++) {
            JSONObject task = tasks.getJSONObject(i);
            if (task.getInt("id") == id) {
                task.put("description", description);
                task.put("updatedAt", LocalDateTime.now().toString());
                writeTasks(tasks);
                System.out.println("Task updated successfully.");
                return;
            }
        }
        System.out.println("Task not found!");
    }

    private static void deleteTask(int id) {
        JSONArray tasks = readTasks();
        for (int i = 0; i < tasks.length(); i++) {
            if (tasks.getJSONObject(i).getInt("id") == id) {
                tasks.remove(i);
                writeTasks(tasks);
                System.out.println("Task deleted successfully.");
                return;
            }
        }
        System.out.println("Task not found!");
    }

    private static void updateTaskStatus(int id, String status) {
        JSONArray tasks = readTasks();
        for (int i = 0; i < tasks.length(); i++) {
            JSONObject task = tasks.getJSONObject(i);
            if (task.getInt("id") == id) {
                task.put("status", status);
                task.put("updatedAt", LocalDateTime.now().toString());
                writeTasks(tasks);
                System.out.println("Task status updated to '" + status + "'.");
                return;
            }
        }
        System.out.println("Task not found!");
    }

    private static void listTasks(String status) {
        JSONArray tasks = readTasks();
        for (int i = 0; i < tasks.length(); i++) {
            JSONObject task = tasks.getJSONObject(i);
            if (status == null || task.getString("status").equals(status)) {
                System.out.println(task);
            }
        }
    }

    private static JSONArray readTasks() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(FILE_PATH)));
            return new JSONArray(content);
        } catch (IOException e) {
            return new JSONArray();
        }
    }

    private static void writeTasks(JSONArray tasks) {
        try (FileWriter file = new FileWriter(FILE_PATH)) {
            file.write(tasks.toString(4));
        } catch (IOException e) {
            System.out.println("Error writing to file.");
        }
    }
}

