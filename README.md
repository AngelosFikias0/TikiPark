# 🚗 TikiPark Android Application

## 📌 Project Overview
Το **TikiPark** είναι μια Android εφαρμογή για εύκολη διαχείριση στάθμευσης, με ασφαλή σύνδεση και cloud αποθήκευση.

## 📱 Τεχνολογίες & Εργαλεία
- **Android Studio** (Java)
- **Firebase** (Authentication & Database)
- **GitHub Actions** (CI/CD)

## 🛠 Installation

### 1️⃣ Κλωνοποίηση του Repository
```bash
git clone https://github.com/AngelosFikias0/TikiPark.git
cd TikiPark
```

### 2️⃣ Άνοιγμα στο Android Studio

1. Άνοιξε το **Android Studio**
2. Επίλεξε **Open an Existing Project**
3. Εντόπισε και επέλεξε το φάκελο **TikiPark**
4. Πάτα **Run** για εκτέλεση σε Emulator ή πραγματική συσκευή

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

### 2. **Ρύθμιση Firebase**
1. Δημιούργησε ένα **Firebase Project** στο [Firebase Console](https://console.firebase.google.com/)
2. Πρόσθεσε την εφαρμογή Android και κατέβασε το **google-services.json**
3. Αντιγραφή του `google-services.json` στον φάκελο `app/`
4. Βεβαιώσου ότι τα dependencies είναι σωστά στο `build.gradle`:
```gradle
classpath 'com.google.gms:google-services:4.3.10' // Project level
implementation 'com.google.firebase:firebase-auth:21.0.1'
implementation 'com.google.firebase:firebase-database:20.0.4'
```
5. Συγχρονισμός του project στο Android Studio

### 3. **Δημιουργία Branch για τις Αλλαγές σου**
```bash
git checkout -b feature-branch
```

### 4. **Fetch & Pull Πριν τις Αλλαγές (Αποφυγή Conflicts)**
```bash
git fetch origin
git pull origin main
```

### 5. **Commit & Push των Αλλαγών σου**
```bash
git add .
git commit -m "Προσθήκη νέας λειτουργίας"
git push origin feature-branch
```

### 6. **Δημιουργία Pull Request (PR)**
1. Πήγαινε στο repository στο GitHub
2. Πάτα **New Pull Request**
3. Επίλεξε το `feature-branch` και σύγκρινέ το με το `main`
4. Πρόσθεσε περιγραφή και πάτα **Create Pull Request**
5. Περίμενε έγκριση και merge από τους maintainers

### 7. **Ενημέρωση του Τοπικού Main Branch Μετά το Merge**
```bash
git checkout main
git fetch origin
git pull origin main
```

### 8. **Διαγραφή του Feature Branch (Τοπικά & Remote, Προαιρετικό)**
```bash
git branch -d feature-branch
git push origin --delete feature-branch
```

## 🤝 Collaborators
-
