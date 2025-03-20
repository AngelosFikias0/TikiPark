# 🚗 TikiPark

## 📌 Project Overview
Το **TikiPark** είναι μια Android εφαρμογή για εύκολη διαχείριση στάθμευσης, με ασφαλή σύνδεση και cloud αποθήκευση.

## 📱 Τεχνολογίες & Εργαλεία
- **Android Studio** (Java)
- **Firebase** (Authentication & Database)
- **GitHub Actions** (CI/CD)

## 📸 Screenshots
_(Πρόσθεσε εδώ screenshots της εφαρμογής)_

---

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

### 1. **Fork το Repository**

Πριν κάνεις αλλαγές, δημιούργησε ένα αντίγραφο του repository στον λογαριασμό σου:

1. Πήγαινε στο [TikiPark Repository](https://github.com/AngelosFikias0/TikiPark)
2. Πάτα **Fork** (επάνω δεξιά) για να δημιουργήσεις ένα αντίγραφο στο δικό σου GitHub

### 2. **Κλωνοποίηση του Forked Repository**

```bash
git clone https://github.com/your-username/TikiPark.git
cd TikiPark
```

### 3. **Ρύθμιση Firebase**
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

### 4. **Δημιουργία Branch για τις αλλαγές σου**

```bash
git checkout -b feature-branch main
```

### 5. **Commit & Push των αλλαγών σου**

Μετά τις αλλαγές σου, προσθέτεις τα αρχεία και κάνεις commit:

```bash
git add .
git commit -m "Προσθήκη νέας λειτουργίας"
```

Στη συνέχεια, κάνεις push το branch στο απομακρυσμένο repository σου:

```bash
git push origin feature-branch
```

### 6. **Δημιουργία Pull Request**

1. Πήγαινε στο repository σου στο GitHub
2. Θα δεις μια ειδοποίηση για να ανοίξεις ένα **Pull Request**
3. Συμπλήρωσε μια περιγραφή των αλλαγών σου και πάτα **Create Pull Request**
4. Περιμένε να εγκριθεί το PR και να συγχωνευθεί στο **main**

### 7. **Ενημέρωση του Τοπικού σου Main Branch**

Αφού συγχωνευτεί το PR, ενημέρωσε το τοπικό σου repository:

```bash
git checkout main
git pull origin main
```

### 8. **Διαγραφή του Feature Branch (Προαιρετικό)**

Αν το branch δεν χρειάζεται πλέον, μπορείς να το διαγράψεις:

```bash
git branch -d feature-branch
git push origin --delete feature-branch
```
