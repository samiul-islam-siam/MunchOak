package com.munchoak.manager;

import com.munchoak.mainpage.FoodItems;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MenuStorage {
    private MenuStorage() {
    }

    public static void setMenuFile(File file) {
        StoragePaths.setMenuFile(file);
        saveAttachedMenu(file);
    }

    public static File getMenuFile() {
        return StoragePaths.getMenuFile();
    }

    public static List<FoodItems> loadMenu() {
        StorageInit.ensureDataDir();
        List<FoodItems> list = new ArrayList<>();
        File menuFile = getMenuFile();
        if (!menuFile.exists() || menuFile.length() == 0) return list;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(menuFile))) {
            while (dis.available() > 0) {
                int id = dis.readInt();
                String name = dis.readUTF();
                String details = dis.readUTF();
                double price = dis.readDouble();
                String cuisine = dis.readUTF();
                String imagePath = dis.readUTF();
                String category = dis.readUTF();
                int quantity = dis.readInt();
                String addOne = dis.readUTF();
                double addOnePrice = dis.readDouble();
                String addTwo = dis.readUTF();
                double addTwoPrice = dis.readDouble();

                list.add(new FoodItems(
                        id, name, details, price, cuisine, imagePath,
                        category, quantity, addOne, addOnePrice, addTwo, addTwoPrice
                ));
            }
        } catch (EOFException ignored) {
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }

        return list;
    }

    public static Map<Integer, FoodItems> loadFoodMap() {
        Map<Integer, FoodItems> map = new HashMap<>();
        for (FoodItems f : loadMenu()) map.put(f.getId(), f);
        return map;
    }

    public static void appendMenuItem(FoodItems item) throws IOException {
        StorageInit.ensureDataDir();
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(getMenuFile(), true))) {
            writeFoodItem(dos, item);
        }
    }

    public static void rewriteMenu(List<FoodItems> items) throws IOException {
        StorageInit.ensureDataDir();
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(getMenuFile(), false))) {
            for (FoodItems item : items) writeFoodItem(dos, item);
        }
    }

    private static void writeFoodItem(DataOutputStream dos, FoodItems item) throws IOException {
        dos.writeInt(item.getId());
        dos.writeUTF(item.getName());
        dos.writeUTF(item.getDetails());
        dos.writeDouble(item.getPrice());
        dos.writeUTF(item.getCuisine());
        dos.writeUTF(item.getImagePath());
        dos.writeUTF(item.getCategory());
        dos.writeInt(item.getQuantity());
        dos.writeUTF(item.getAddOne());
        dos.writeDouble(item.getAddOnePrice());
        dos.writeUTF(item.getAddTwo());
        dos.writeDouble(item.getAddTwoPrice());
    }

    static void loadAttachedMenu() {
        StorageInit.ensureDataDir();
        if (!StoragePaths.getMenuPointerFile().exists() || StoragePaths.getMenuPointerFile().length() == 0) return;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(StoragePaths.getMenuPointerFile()))) {
            String filename = dis.readUTF();
            File menuFile = new File(StoragePaths.DATA_DIR, filename);
            if (menuFile.exists()) {
                StoragePaths.setMenuFile(menuFile);
            }
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    private static void saveAttachedMenu(File menuFile) {
        StorageInit.ensureDataDir();
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(StoragePaths.getMenuPointerFile(), false))) {
            dos.writeUTF(menuFile.getName());
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }
}