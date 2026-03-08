# 🍳 DigitalCookbook

A Java desktop application for managing your personal recipe collection, paired with browser extensions for Chrome and Firefox that let you import recipes from [chefkoch.de](https://www.chefkoch.de) in one click.

---

## Features

- **Add, edit & delete recipes** — Full CRUD management for your recipe library
- **One-click import** — Browser extensions for Chrome and Firefox scrape recipe data directly from chefkoch.de into the app
- **Category organisation** — Group recipes by type (e.g. appetisers, mains, desserts)
- **Search** — Find recipes by name or ingredient
- **Local persistence** — Recipes are saved to a local database so your collection is always there when you open the app

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| HTML parsing | jsoup 1.13.1 |
| JSON handling | json-20200518 |
| Utilities | commons-lang 2.6 |
| Browser extensions | Chrome (Manifest V3) / Firefox (.xpi) |

---

## Getting Started

### Prerequisites

- Java 8 or higher
- Chrome or Firefox (optional, for the import extension)

### Run the App

```bash
git clone https://github.com/TheEugen/DigitalCookbook.git
cd DigitalCookbook/src/CookbookApplication
```

Then open the project in your preferred Java IDE (IntelliJ, Eclipse, etc.) and run the main class. Alternatively, compile and run from the command line:

```bash
javac -cp ".:lib/*" src/CookbookApplication/*.java
java -cp ".:lib/*" src/CookbookApplication.Main
```

> Make sure the required `.jar` dependencies (jsoup, json, commons-lang) are on your classpath.

---

## Browser Extension Setup

The extensions communicate with the desktop app via the `HTMLManager` and `JSONManager` classes to parse and import recipe data.

### Chrome

1. Go to `chrome://extensions/` and enable **Developer mode**
2. Click **Load unpacked** and select the project's `chrome` extension directory

### Firefox

1. Go to `about:addons` → click the gear icon → **Install Add-on From File**
2. Select `dcb_recipe_import-0.3.0-fx.xpi` from the `firefox` directory

Once installed, navigate to any recipe page on chefkoch.de and click the extension icon to import it directly into the app.

---

## Project Structure

```
DigitalCookbook/
├── src/
│   └── CookbookApplication/   # Core Java application
├── manifest_chrome.json        # Chrome extension manifest
├── manifest_firefox.json       # Firefox extension manifest
├── LICENSE
└── README.md
```

---

## Roadmap

- [ ] Support additional recipe websites beyond chefkoch.de
- [ ] Encrypted local storage for recipe data
- [ ] Extended category and filter options
- [ ] Export recipes to PDF or print view

---

## License

This project is licensed under the [MIT License](LICENSE).