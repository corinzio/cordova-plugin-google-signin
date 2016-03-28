# Google Sign-In Cordova/PhoneGap Plugin

## 0. Index
1. [Description](#1-description)
2. [Screenshots](#2-screenshots)
3. [Installation (CLI / Plugman)](#3-installation-phonegap-cli--cordova-cli)
4. [Usage](#4-usage)
5. [API Documentation](#5-API-documentation)
6. [Changelog](#6-changelog)
7. [License](#7-license)

## 1. Description
This plugin allows you to log on with your Google account on Android.  
The plugin can be configured to get an authentication token to use it with a specific backend server and/or enable server side API-access.

## 2. Screenshots
### Android

<img src="https://raw.githubusercontent.com/corinzio/cordova-plugin-google-signin/master/screen/base.png" width="300" style="float: left; margin-right: 10px" />

<img src="https://raw.githubusercontent.com/corinzio/cordova-plugin-google-signin/master/screen/select.png" width="300"  />  

## 3. Installation (Cordova CLI)
At the moment the plugin is not released as a npm package but you can install latest version directly from git repository with the command:  
```
cordova plugin add https://github.com/corinzio/cordova-plugin-google-signin
```
#### 3.1 Android Configuration
To configure Android, you will need to perform Step 2 (Get a configuration file) of [this guide](https://developers.google.com/identity/sign-in/android/start). If you plan to use only authentication in your cordova app this step will create necessary credentials in Developer Console and you don't have to use the generated file in your project. Anyway make sure to get the debug and/or release certificate fingerprint as explained [here](https://developers.google.com/android/guides/client-auth.html) or authentication will fail. During the configuration file step it will be requested to enter that fingerprint in this page:  
<img src="https://raw.githubusercontent.com/corinzio/cordova-plugin-google-signin/master/screen/signin.png" width="300" style="clear: both; display: block;" />
Usually I insert in thath page the debug certificate fingerprint, when you will need the release certificate you will be able to add it in the google developer console.


## 4. Usage

TODO

## 5. API Documentation
You can find the plugin API documentation [here](http://corinzio.github.io/cordova-plugin-google-signin/)  
It is possible to use jsdoc to generate the documentation with the command:
```
jsdoc -c jsdoc.conf -t node_modules/minami www/GSignIn.js
```


## 6. Changelog
- 0.0.1: Initial version

## 7. License
GNU GENERAL PUBLIC LICENSE Version 3, 29 June 2007.

see [License](https://raw.githubusercontent.com/corinzio/cordova-plugin-google-signin/master/LICENSE)
