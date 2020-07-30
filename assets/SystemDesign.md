# System design

## Functional requirements

### General

- Request permissions
  - ACCESS_FINE_LOCATION
  - READ_CONTACTS
  - SEND_SMS
- Handling the required permissions
- Saving the images in the internal memory

### Alerts

- Check the location when the alert is triggered
- Show in the UI whether the alert is active or not
- If no contact has been selected yet, request that you choose a contact to enable the alert
- List all alert triggered [date, time, sending status]
- If no alert has been triggered yet, put a message centered on the screen
- Change the default message of the message that is to be sent
- From the notification created you can stop the alert
- If the alert was triggered in the notifications you can cancel the sending of messages

### Contacts

- Show contact list
- Add contact
- Import contact
- See contact details
- Edit contact
- Edit profile image
- Delete profile image
- Delete contact
- Enable / disable as a trusted contact
- Show a message if no contact has been - added yet

### Profile

- See personal data
- Edit personal data [Name, safe places, photo]
- Clean up the data of the whole application
- Show instructions for use

### Onboarding

- Create an illustrated sequence of how the application works if it was entered for the first time (gifs/video)
- Create guided user instructions if you enter for the first time
- Create loading screen at app startup

### Settings

- Check out the policies of the application
- Check out the application information

### Upcoming

- Insert advertisements
- Exporting the alert list
