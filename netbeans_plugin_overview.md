# Netbeans plugin overview #

This page is an overview of implemented features in the hAtom plugin for Netbeans 6.1 and 6.5

## Code completion ##

Code completion works in the same way as the HTML code completion module. It's activated with a CTRL+SPACE shortcut; a popup is opened and hAtom keywords are filtered while typing more letters. The completion popup id deactivated with the pressure of ESC button or once a keyword is accpted using the ENTER key.

![![](http://netbeans-hatom-plugin.googlecode.com/svn/wiki/images/hatomPluginNBcompletion.png)](http://netbeans-hatom-plugin.googlecode.com/svn/wiki/images/hatomPluginNBcompletion.png)

The plugin also shows documentation about hAtom elements in a dedicated doc panel. The panel is opened just above the completion popup, as shown in the image below.

![![](http://netbeans-hatom-plugin.googlecode.com/svn/wiki/images/hatomPluginNBdocum.png)](http://netbeans-hatom-plugin.googlecode.com/svn/wiki/images/hatomPluginNBdocum.png)

## Code Highlight ##

The default color for hAtom keywords highlight is magenta. This setting can be changed using the **Tools > Options > Fonts & Colors**, selecting the "**HTML**" value in the language drop down menu and "**hAtom keyword**" in the Category list, as shown in the picture below.

![![](http://netbeans-hatom-plugin.googlecode.com/svn/wiki/images/hatomPluginNBoptionWindow2.png)](http://netbeans-hatom-plugin.googlecode.com/svn/wiki/images/hatomPluginNBoptionWindow2.png)

## Validation ##
Hatom validation process is started by clicking on the button with the microformats icon.

![![](http://netbeans-hatom-plugin.googlecode.com/svn/wiki/images/hatomButton.png)](http://netbeans-hatom-plugin.googlecode.com/svn/wiki/images/hatomButton.png)

For every file to be validated, a new output tab is opened, as shown in the picture below.

![![](http://netbeans-hatom-plugin.googlecode.com/svn/wiki/images/hatomPluginNB6.png)](http://netbeans-hatom-plugin.googlecode.com/svn/wiki/images/hatomPluginNB6.png)


The output result is printed on the output tab in the form of a sequence of an atomic report that shows the user:
**the error message** the complete node that contains the error
**the path of the wrong node tag (if available) with the following form: _html > body > div_** the total number of errors occurred
\\