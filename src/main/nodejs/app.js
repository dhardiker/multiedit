var net = require("net");
var app = require("express").createServer();
var io = require("socket.io").listen(app);

app.listen(8081);

app.get('/', function (req, res) {
    console.log(__dirname);
    res.sendfile(__dirname + '/index.html');
});

io.sockets.on('connection', function(socket){
	console.log('connection');
	socket.on('msg', function(data) {
		socket.broadcast.emit('msg', data);
	});
});