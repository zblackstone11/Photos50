The `viewController` package is a central part of the application's architecture, combining the user interface definitions 
and their corresponding interaction logic into a cohesive unit. 
This package houses both the FXML files, which define the layout and visual structure of the application's user interface, 
and the controller classes, which manage user interactions with the UI components.

#### FXML Files:
- FXML files within this package serve as the blueprint for the application's UI. 
- They describe how UI elements like buttons, images, and text fields are arranged on the screen.
- This separation of UI layout from the application logic (model and app packages) helps in maintaining a clear distinction 
- between how the application looks and how it behaves.

#### Controller Classes:
- Accompanying each FXML file is a dedicated controller class responsible for handling events and interactions initiated 
- by the user. These controllers listen for actions within the UI, such as button clicks or text input, 
- and execute the appropriate response in the application's logic. 
- By encapsulating the interaction logic within these controllers, the application promotes a clean separation of concerns, 
- where the `viewController` package acts as a mediator between the user's inputs and the application's processing and 
- response mechanisms.