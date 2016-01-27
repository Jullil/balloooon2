function init_game(canvasElem) {
    var canvas = canvasElem.get(0);
    var ctx = canvas.getContext("2d");
    var canvasWidth = canvasElem.width();
    var canvasHeight = canvasElem.height();

    var canvasCenterX = Math.round(canvasWidth / 2);
    var canvasCenterY = Math.round(canvasHeight / 2);

    var mainArea = new Path2D();
    mainArea.rect(0, 0, canvasWidth, canvasHeight);
    ctx.stroke(mainArea);

    Location = function (x, y) {
        this.x = x;
        this.y = y;
    };


    Avatar = function (id, location) {
        this.id = id;
        this.location = location;
    };
    $.extend(Avatar, {
        prototype: {
            updateLocation: function (location) {
                this.location = location;
                console.log("Received new location for avatar=" + this.id + ": " + this.location.x + "," + this.location.y);
            },
            repaint: function () {
                var path = new Path2D();
                var radius = 100;
                path.arc(location.x, location.y, radius, 0, 2 * Math.PI);
                return path;
            }
        }
    });

    World = function () {
        this.avatars = {};
    };
    $.extend(World, {
        prototype: {
            addAvatar: function (id, location) {
                var avatar = new Avatar(id, location);
                this.avatars[avatar.id] = avatar;
                console.log("Added new avatar: ");
                console.log(this.avatars[avatar.id]);
                return avatar;
            },
            updateAvatarLocation: function (id, location) {
                var avatar = this.getAvatar(id);
                if (avatar == undefined) {
                    avatar = this.addAvatar(id, location);
                } else {
                    avatar.updateLocation(location);
                }
                //this.avatars[avatar.id] = avatar;
                console.log("Undated location: ");
                console.log(avatar);
                console.log(this.avatars[avatar.id]);
            },
            getAvatar: function (id) {
                return this.avatars[id];
            },
            repaint: function () {
                var path = new Path2D();
                var radius = 100;
                path.arc(location.x, location.y, radius, 0, 2 * Math.PI);
                return path;
            }
        }
    });


    var avatar = new Avatar("1", new Location(canvasCenterX, canvasCenterY));
    ctx.fill(avatar.repaint());

    var world = new World();


    var socket1 = createSocket();
    var socket2 = createSocket();
    var socket3 = createSocket();

    socket1.onopen = function () {
        sendLocation(socket1, 300, 200);
    };
    socket3.onopen = function () {
        sendLocation(socket3, 200, 400);
    };

    function createSocket() {
        var socket = new WebSocket("ws://localhost:9000/game/");

        socket.onmessage = function (event) {
            var msg = $.parseJSON(event.data);
            console.log("Received a message: ");
            console.log(msg);
            if (msg.type == "location") {
                world.updateAvatarLocation(msg.uid, new Location(msg.x, msg.y));
            }
            console.log(world);
        };
        return socket;
    }

    function sendLocation(socket, x, y) {
        var msg = {
            type: "location",
            clientId: "uuid_10",
            data: {uid: 0, x: x, y: y}
        };
        socket.send(JSON.stringify(msg));
    }


    //var sock = new SockJS('/game/');
    //sock.onopen = function () {
    //    console.log('open');
    //};
    //sock.onmessage = function (e) {
    //    console.log('message', e.data);
    //};
    //sock.onclose = function () {
    //    console.log('close');
    //};
    //
    //sock.send('test');
    //sock.close();
}