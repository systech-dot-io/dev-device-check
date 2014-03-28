dev-device-check
================

Simple tool to check passwords on VoIP endpoints

<b><a href="https://github.com/systech-dot-io/dev-device-check">SPDC</a></b> - Systech Password Device Check 

<p>This tool if for checking log in credentials for http(s) enabled devices that are open to the public internet.</p>
<p>Having an open publicly accessible device is a serious issue, that can have huge financial impact on an organization.</p>
<p>This tool has been built as yet another means of monitoring your customer premise equipment to ensure one is not able to easily log into one of your devices.</p>

This tool is cross platform and needs to be run from the command line. <br/>
The syntax is as follows:<br/>
<code>
java -jar systech-device-check.jar 'ipaddress' 'port' 'username' 'password' 'devicetype'
</code>



<h4>Supported Device Types</h4>



<ul>
	<li>spa122</li>
	<li>spa112</li>
        <li>spa2102</li>
        <li>spa3102</li>
	<li>Adtran</li>
	<li>Aastra</li>
</ul>
<code>
example:
java -jar systech-device-check.jar '1.2.3.4' '8080' 'cisco' 'cisco' 'spa122'
</code>
<br/>
<br/>

<b>Required Libs</b> <a href="http://hc.apache.org/downloads.cgi">HERE</a>
<ul>
	<li>httpcore-4.3.2</li>
	<li>httpclient-4.3.3</li>
</ul>


