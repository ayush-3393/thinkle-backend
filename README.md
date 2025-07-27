## Frontend Repo
https://github.com/ayush-3393/thinkle-frontend

## Demo
https://github.com/user-attachments/assets/a29562da-961f-4885-9d0b-132cc01257e7

# Thinkle 🎯 – A Smarter Word Guessing Game

## 🧠 About

**Thinkle** is a word-guessing game inspired by *Wordle*, but with a twist!

- You have **10 lives** to guess the word of the day.
- You can request **up to 2 hints**, each costing **3 lives**.
- Every incorrect guess costs **1 life**.
- Each guess is met with feedback from an **AI Oracle**, offering playful and insightful commentary.

---

## 🛠 Technologies Used
- **Java** - Robust, object-oriented programming language
- **Spring Boot** - Enterprise-grade framework for rapid application development
- **PostgresSQL** - Advanced open-source relational database---
- **JPA/Hibernate** - Object-relational mapping for database operations
- **Spring Security** - Comprehensive security framework for authentication & authorization## 🚀 Features
- **Maven** - Dependency management and build automation
- **Google Gemini AI** - Advanced AI integration for word generation and hints### 🔐 Authentication

- Secure **Signup/Login** using **Spring Security** and **JWT tokens**
- JWT tokens expire after **24 hours**

---
## 🚀 Features

### 🔐 Authentication

- Secure **Signup/Login** using **Spring Security** and **JWT tokens**
- JWT tokens expire after **24 hours**

---

### 🕹 Game Sessions

- A **new session** is created on the user's first login of the day
- Unfinished sessions are **resumed** on next login
- A single **Word of the Day** is generated for all users once per day
- All hints are generated and stored at the time of word generation

---

### 🔤 Word Generation

- Only **one unique word** is generated per day
- The word is generated via **Google Gemini AI**

---

### 💡 Hints

- Players can request **up to 2 hints** to assist with guessing
- Hints are pre-generated using **Gemini AI** and stored in the database
- Each hint usage costs **3 lives**
- Hints are **fetched from the database**, not re-generated

---

### 🎯 Guesses & AI Feedback

- Players can guess **5-letter words**
- The **AI Oracle** provides witty feedback on each guess
- The board shows:
  - 🟩 Correct letters in the correct position
  - 🟨 Correct letters in the wrong position

---

Enjoy Thinkle, where guessing meets AI-powered fun! 🚀

---












