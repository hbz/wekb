# Initial setup

## install nodejs on Linux

    sudo apt-get install --yes nodejs

## install nodejs on Windows with NVM

-  go to https://github.com/coreybutler/nvm-windows
-  there go to "Download Now" and download Windows-Exe and install it
-  go to terminal and install node.js with nvm:


    nvm install 12.13.0

## install gulp globaly

    npm install -g gulp

## installing fomantic-ui

- is not needed
- we put the all of the fomantic ui files in the repository

   


# How to update fomantic-ui



 
    
-----------------------------------------------------


# How to customize CSS

Our custom themes and the default theme override the *.less in 'src/definitions'. The folder 'src/side' is not used yet.


## Theming

- we got the theme 'wekb'
- all changes in css we make by changing inside the theme!


## Example

I would like to change the padding between an icon and content in a list

1.) find the variable in default theme. In this case there

    src/themes/default/elements/list.variables
    
2.) create the list.variables in the wekb theme folder

    src/themes/wekb/elements/list.variables
    
3.) change the specifig variable only there

4.) Change the theme.config 

    /files/frontend/semantic/src/definitions/themes/theme.config

    
old:

    @list       : 'default';
    
new:

    @list       : 'wekb';
    
5.) Build css

    cd semantic
    gulp build

