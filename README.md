# AI-Powered Visual Testing (Java)

This project demonstrates a simple approach to visual regression testing in Java.  
It uses Selenium for browser automation and a lightweight image comparison library to detect differences.

## How It Works
- Launches a browser (Chrome by default).
- Navigates to a test URL (Google).
- Captures a screenshot.
- Compares it against a baseline image (if it exists).

## Setup & Run
1. **Install Dependencies**:
   ```bash
   mvn clean install
