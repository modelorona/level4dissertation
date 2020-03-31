## Data
Keep your data (e.g. from evaluations here)

### Contents of this folder
* The `data/ethics` folder contains forms that were used and a signed ethics form.
* The `data/processed` folder contains the processed data. The application categories were amended with the script from the `data/notebooks/preprocessing`
script.
* The `data/notebooks` folder contains the notebook analysis.ipynb that was used for the data analysis.
* The `data/notebooks/preprocessing` contains a script that processed the raw categories data to fix the categories. This script should
not need to be run again. If you want to run it on your own collected data, make sure you change the database connector configuration. Additionally, I
feel it important to note that this operated on a copy of the raw data, hence never modifying the original raw gathered data.
* The `data/raw` folder has the original survey responses.


### Data that is collected
_Not all of the data was used_
* Location
  * altitude
  * horizontal Accuracy
  * vertical Accuracy
  * bearing
  * bearing Accuracy 
  * latitude
  * longitude
  * speed
  * speed Accuracy
  * system Timestamp
  * location Timestamp
  * elapsed Nanos Since Boot
  * elapsed Nanos Location
  * location provider
  
* Call
  * start time
  * end time
  
* Apps
  * app name
  * app category
  * app package
  
* Sessions
  * session start
  * session end
  * total time visible
  * last time used
  * last time foreground service used
  * app name
  * total time in foreground
  * last time visible
  * total Time Foreground Service Used
  * 

### How data was collected
Data was collected by the Android application. All specifics can be seen in the `src` folder.
