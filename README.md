![Platform: Windows](https://img.shields.io/badge/Platform-Windows-blue)
![Platform: Android](https://img.shields.io/badge/Platform-Android-green?logo=android&logoColor=white)


# Train Controller
Control your Lego Powered Up Trains from your PC or phone! On Windows, you can even do so with a gamepad (or more than one!)
## Features
- **Control speed and Light**
- **Gamepad Support** (only Windows): use your Xbox Gamepad to drive your trains
- **Control multiple trains at the same time**: When using a Gamepad, you can select multiple trains to control at once
- **Multiplayer**: Use multiple controllers at the same time to control different trains

## UI
<img width="1037" height="827" alt="Screenshot 2025-07-27 131602" src="https://github.com/user-attachments/assets/43d9b4e8-7869-4bc0-a747-71ef395ed05f" />

## How to use

- Download and unzip the [latest Release](https://github.com/ronnyporsch/TrainController/releases/latest)
- Run TrainController.exe in the root dir
- (Optional) Connect up to four Gamepads
- Turn on your Lego Trains
- Enjoy!

## Build it locally
- Clone the repository
- run it using ".\gradlew composeApp:run"

## Limitations
- Gamepad support only on Windows
- This program only works with the Lego City Powered Up Hubs (found for example in the current [Passenger](https://www.lego.com/en-us/product/express-passenger-train-60337) and [Freight Trains](https://www.lego.com/en-us/product/freight-train-60336) )
- The program expects the Motor to be on Port A of the train hub, the light (if available) on Port B
- Gamepad Support was only tested with Xbox One Gamepads. Others may or may not work properly

## Roadmap
- Include Gamepad support on Android
- Include Browser

## Credits
This software was inspired by the amazing [Brick Automation Project](https://github.com/Cosmik42/BAP)
