# pjlinkControl
A simple remote control commandline client for pjlink compatible devices written in Java

<pre>
usage: [-h] -i &lt;ip&gt; [-p &lt;port&gt;] [-a &lt;authpass&gt;] -c &lt;command&gt;

PjlinkControl 1.0 (http://github.com/Bendr0id/)

 -a,--authpass &lt;authpass&gt;     The Password of the Pjlink device [default: JBMIAProjectorLink]
 -c,--command &lt;command&gt;       The Commant to send to the Pjlink device
 -h,--help                    Prints this help
 -i,--ip &lt;ip&gt;                 The IP of the Pjlink device
 -p,--port &lt;port&gt;             The Port of the Pjlink device [default: 4352]

Available commands:

LAMP ? = Lamp status query
        Result:
                1st digits (1-5) = Lamp cumulative operating time
                2nd digit
                        Values:
                        0 = Lamp off
                        1 = Lamp on

INF1 ? = Manufacturer name query
        Result:
                Manufacturer name

INPT ? = Input selection query
        Result:
                11-19 = RGB 1-9
                21-29 = VIDEO 1-9
                31-39 = DIGITAL 1-9
                41-49 = AUX 1-9
                51-59 = Network 1-9

AVMT = AV MUTE control
        Parameters:
                30 = AV MUTE mode off
                31 = AV MUTE mode on

NAME ? = Device name query
        Result:
                Device name

INF2 ? = Model name query
        Result:
                Model name

INPT = Input selection
        Parameters:
                11-19 = RGB 1-9
                21-29 = VIDEO 1-9
                31-39 = DIGITAL 1-9
                41-49 = AUX 1-9
                51-59 = Network 1-9

POWR = Power supply control
        Parameters:
                0 = Standby
                1 = Power ON

AVMT ? = AV MUTE status query
        Result:
                30 = AV MUTE mode off
                31 = AV MUTE mode on

ERST ? = Error status query
        Result:
                1st byte = Indicates fan errors
                2nd byte = Indicates lamp errors
                3rd byte = Indicates temperature errors
                4th byte = Always 0
                5th byte = Indicates filter errors
                6th byte = Indicates other errors
        Values:
                0 = No error known
                1 = Warning
                2 = Error

INF0 ? = Other information query
        Result:
                Information such as version number is returned

POWR ? = Power supply status query
        Result:
                0 = Standby
                1 = Power ON
                2 = Cool-down in progress
                3 = Warmup
</pre>