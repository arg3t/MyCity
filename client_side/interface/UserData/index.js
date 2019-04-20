var exampleSocket = new WebSocket("ws://localhost:3000", "protocolOne");
exampleSocket.onmessage = function (event) {
    console.log(event.data);
}