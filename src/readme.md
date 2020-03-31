# Readme

The `app` folder contains the android code. It is standard folder structure.

The `server` folder contains the code for the server.

## Build instructions

To run the android application, open it up in Android studio, build, and run the debug version there. If you want to sign, provide your own key. Additionally, you need to create your own `app_key` string and `db_url` string and save them as environmental variables. I had them set in the Travis built menu to be passed along with every build. They are needed for the export section; everything else will work fine without them. If you want to configure Sentry, you will need to go on their site, generate the api key, and then change it in the application dsn setting.

To run the server, go into the folder and `npm install` to install the dependencies. The newest stable version of nodejs is recommended. There is an `.env.example` file that you need to copy into a `.env` file and fill out the values. That is where the database connection is configured.

To run the data analysis, start the notebook and run all the cells. It will save all graphs as pdf files as well as show the metrics in the notebook itself.

### Requirements

For data analysis:
* Python 3.7
* Packages: listed in `requirements.txt` 
* Tested on Ubuntu 18.04

Android application:
* All required dependencies are in the gradle file. App was not tested on emulator but on physical Samsung S10e running android 10.

NodeJS server:
* Tested on Ubuntu Server 18.04
* Nginx server configured with a tls key from lets encrypt and a reverse proxy to the port of the nodejs server.

### Build steps

To build the android application, open it in android studio. It should index in the background. Once that is done, press the bright green button on the top center to build and run on emulator/physical device.

Nothing else needs to be built.

### Test steps

No tests were added. Project was research based.
