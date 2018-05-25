# ClooudStorageApp
Android application for Open, Save and Edit Text files from the Cloud

The application's purpose is creating text (notes) and storing them in a cloud storage infrastructure. 
This is a basic  way to create, save and read content to / from a remote storage file (Google Drive). The files will be text only.

The application does the following:
The user logs in using Google Account authentication.
The user is able to open an existing text, create text, or save one that has already been opened or created.

When app launches, a welcome message appears and below a large button labeled "Enter". 
By clicking the button the user is guided to the main app. 
In order to create text (or edit existing), there is a field in which the user will be able to place and edit multi-line text with vertical scrolling.


Create a file
When  user clicks the "NEW" button, the app starts with the actions required to create a MIME text file in Google Drive, with a user-defined name.

Save a file
When user presses the "SAVE" button, he / she is given the option of selecting the file where the text he has typed will be stored. 
If it is already editing an existing text then it is saved with the name it already has. 
If the file is new, it is saved in with the name given when it was created. 
The selection will only be given for files of the appropriate type (MIME text / plain).

Open a file
When user presses the "OPEN" button, he / she is given the option to select the file from which he / she will read the corresponding text displaying the latter under the buttons. 
The selection will only be given for files of the appropriate type (MIME text / plain).
