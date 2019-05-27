<?php

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    system("python /home/pi/Desktop/MyCity/bus_stop/qr_reader/main.py > /dev/null 2>&1 &");
}else{
    echo "
    <html>
        <body>
            <button type=\"button\" onclick=\"Http = new XMLHttpRequest();
                                                Http.open(\"POST\",\".\");
                                                Http.send();
                                                \" >Click Me!</button>
        </body>
    </html>
    ";
}
?>