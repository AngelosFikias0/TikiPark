# 🚗 TikiPark Android Application

![TikiPark Logo](Mockups/logo.jpg)

## 📌 Project Overview

---

## 📊 Project Details

<p align="center">
  <img src="https://img.shields.io/github/repo-size/AngelosFikias0/TikiPark?style=for-the-badge&color=blue" alt="Repo Size">
  <img src="https://img.shields.io/github/issues/AngelosFikias0/TikiPark?style=for-the-badge&color=yellow" alt="Issues">
  <img src="https://img.shields.io/github/issues-pr/AngelosFikias0/TikiPark?style=for-the-badge&color=brightgreen" alt="Pull Requests">
  <img src="https://img.shields.io/github/last-commit/AngelosFikias0/TikiPark?style=for-the-badge&color=red" alt="Last Commit">
  <img src="https://img.shields.io/github/contributors/AngelosFikias0/TikiPark?style=for-the-badge&color=purple" alt="Contributors">
</p>

## 📱 Τεχνολογίες & Εργαλεία
<p align="center">
  <img src="https://img.shields.io/badge/Android_Studio-Java-green?style=for-the-badge&logo=android" alt="Android">
  <img src="https://img.shields.io/badge/XAMPP-PHP_&_MySQL-orange?style=for-the-badge&logo=mysql" alt="XAMPP">
  <img src="https://img.shields.io/badge/GitHub_Actions-CI/CD-blue?style=for-the-badge&logo=github-actions" alt="GitHub Actions">
</p>

---

## 🛠 Installation

### 1. Κλωνοποίηση του Repository
```bash
git clone https://github.com/AngelosFikias0/TikiPark.git
cd TikiPark
```

### 2. Άνοιγμα στο Android Studio 

1. Άνοιξε το **Android Studio**
2. Επίλεξε **Open an Existing Project**
3. Εντόπισε και επέλεξε το φάκελο **TikiPark**
4. Πάτα **Run** για εκτέλεση σε Emulator ή πραγματική συσκευή

### 3. **Ρύθμιση XAMPP**

1. Εγκατέστησε το XAMPP από το Apache Friends
2. Ξεκίνησε Apache και MySQL από το XAMPP Control Panel
3. Αντέγραψε τα αρχεία PHP από το Backend/ στο C:\xampp\htdocs\TikiPark
4. Εισήγαγε τη βάση δεδομένων:
5. Άνοιξε το phpMyAdmin (http://localhost/phpmyadmin/)
6. Δημιούργησε μια νέα βάση δεδομένων με όνομα TikiPark
7. Εισήγαγε το Database/tikipark.sql
8. Ενημέρωσε το Backend/config.php με τα διαπιστευτήρια της βάσης
   
---

## 👥 Συμμετοχή & Contributing

### 1. **Κλωνοποίηση & Ρύθμιση του Repository**
Πριν ξεκινήσεις, βεβαιώσου ότι έχεις την τελευταία έκδοση του κώδικα:
```bash
git clone https://github.com/AngelosFikias0/TikiPark.git
cd TikiPark
git checkout main
git fetch origin
git pull origin main
```

### 2. **Δημιουργία Branch για τις Αλλαγές σου**
```bash
git checkout -b feature-branch
```

### 3. **Fetch & Pull Πριν τις Αλλαγές (Αποφυγή Conflicts)**
```bash
git fetch origin
git pull origin main
```

### 4. **Commit & Push των Αλλαγών σου**
```bash
git add .
git commit -m "Προσθήκη νέας λειτουργίας"
git push origin feature-branch
```

### 5. **Δημιουργία Pull Request (PR)**
1. Πήγαινε στο repository στο GitHub
2. Πάτα **New Pull Request**
3. Επίλεξε το `feature-branch` και σύγκρινέ το με το `main`
4. Πρόσθεσε περιγραφή και πάτα **Create Pull Request**
5. Περίμενε έγκριση και merge από τους maintainers

### 6. **Ενημέρωση του Τοπικού Main Branch Μετά το Merge**
```bash
git checkout main
git fetch origin
git pull origin main
```

### 7. **Διαγραφή του Feature Branch (Τοπικά & Remote, Προαιρετικό)**
```bash
git branch -d feature-branch
git push origin --delete feature-branch
```
