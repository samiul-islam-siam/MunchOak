package com.munchoak.manager;

import com.munchoak.mainpage.FoodItems;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class CategoryStorage {
    private CategoryStorage() {}

    static void ensureSeedCategories() throws IOException {
        // If file was just created and empty, seed it.
        if (StoragePaths.CATEGORIES_FILE.length() > 0) return;

        String[] initializedCategories = {
                "Drinks", "Sweets", "Spicy Foods", "Main Course", "Appetizers"
        };

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(StoragePaths.CATEGORIES_FILE, true))) {
            for (String category : initializedCategories) dos.writeUTF(category);
        }
    }

    public static List<String> loadCategories() {
        StorageInit.ensureDataDir();
        List<String> cats = new ArrayList<>();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(StoragePaths.CATEGORIES_FILE))) {
            while (dis.available() > 0) cats.add(dis.readUTF());
        } catch (EOFException ignored) {
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
        return cats;
    }

    public static void addCategory(String name) throws IOException {
        StorageInit.ensureDataDir();
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(StoragePaths.CATEGORIES_FILE, true))) {
            dos.writeUTF(name);
        }
    }

    public static void replaceCategory(String oldName, String newName) throws IOException {
        List<String> cats = loadCategories();
        for (int i = 0; i < cats.size(); i++)
            if (cats.get(i).equals(oldName)) cats.set(i, newName);

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(StoragePaths.CATEGORIES_FILE, false))) {
            for (String c : cats) dos.writeUTF(c);
        }

        // Update menu items
        List<FoodItems> menu = MenuStorage.loadMenu();
        boolean changed = false;
        for (FoodItems f : menu) {
            if (f.getCategory().equals(oldName)) {
                f.setCategory(newName);
                changed = true;
            }
        }
        if (changed) MenuStorage.rewriteMenu(menu);
    }

    public static void deleteCategory(String name) throws IOException {
        List<String> cats = loadCategories();
        cats.removeIf(s -> s.equals(name));

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(StoragePaths.CATEGORIES_FILE, false))) {
            for (String c : cats) dos.writeUTF(c);
        }

        List<FoodItems> menu = MenuStorage.loadMenu();
        menu.removeIf(f -> f.getCategory().equals(name));
        MenuStorage.rewriteMenu(menu);
    }
}