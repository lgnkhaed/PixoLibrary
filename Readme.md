# 🖼️ PixoBIB - Image Management and Encryption App

**PixoBIB** is a full-stack desktop application built with **Java**, **JavaFX**, and **Apache Derby**. It allows users to manage images locally with powerful features such as tagging, transformation, searching, and secure encryption/decryption using password-based hashing.

---

## ✨ Features

### 🔐 Authentication System
- Secure login with password.
- User verification is handled via a DerbyDB-backed system.

### 📷 Image Management
- Upload and display images from a local `uploads/` folder.
- Basic image transformations and visual filters.
- Image metadata (tags, filename, date, etc.) stored and retrieved via **DerbyDB**.

### 🔑 Image Encryption & Decryption
- **Main feature**: Encrypt and decrypt images based on a password.
- Encryption process:
  - Password is hashed using **SHA-256**.
  - The resulting hash is used to seed a `SecureRandom` instance.
  - Image pixels are shuffled using `Collections.shuffle()` with the seeded random.
- Decryption is achieved by applying the same shuffle order using the same password.

### 🏷️ Tag System (Metadata)
- Add and remove tags for each image.
- Search images by tags.
- Tags and metadata are stored in a relational table in DerbyDB.

---

## 🛠️ Tech Stack

- **Java 21**
- **JavaFX** (FXML, SceneBuilder UI)
- **Apache Derby (Embedded)** as the relational database
- **Maven** for dependency management
- **SHA-256** hashing via `MessageDigest`
- **SecureRandom** for deterministic encryption
- **Collections.shuffle()** for pixel permutation
