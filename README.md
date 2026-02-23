# DigitalCookbook

## Overview
DigitalCookbook is a Java-based desktop application designed to help users create, store, and manage a digital recipe collection. It includes a browser extension for Chrome and Firefox that allows users to import recipes directly from chefkoch.de with a single click. The extension interacts with the application via the `HTMLManager` and `JSONManager` classes, streamlining the process of adding new recipes.

## Dependencies
jsoup-1.13.1, json-20200518, commons-lang-2.6

## Features
- **Recipe Management**: Easily add, edit, delete, and search for recipes.
- **Browser Extension**: Import recipes directly from chefkoch.de using the provided Chrome and Firefox extensions.
- **Categorization**: Organize recipes by category, such as appetizers, main courses, and desserts.
- **Search Functionality**: Quickly locate recipes by name or ingredients.
- **Persistence**: Save recipes to a local database for future access.

## How to Run
1. Ensure you have Java installed on your system.
2. Clone the repository and navigate to the project directory.
3. Compile and run the application using your preferred Java IDE or command line.
4. Install the browser extension in Chrome or Firefox to enable recipe imports from chefkoch.de.

## Browser Extension Installation
1. **Chrome**:
   - Navigate to `chrome://extensions/` and enable "Developer mode."
   - Click "Load unpacked" and select the `chrome` directory from the extracted addon files.
2. **Firefox**:
   - Go to `about:addons` and select "Install Add-on From File."
   - Choose the `dcb_recipe_import-0.3.0-fx.xpi` file from the `firefox` directory.

## Future Improvements
- Enhance the extension to support more recipe websites.
- Implement encryption for secure storage of recipe data.
- Expand the recipe categorization options and search filters.

## License
This project is licensed under the MIT License.
